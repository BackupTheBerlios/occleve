/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007  Joe Gittings

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

@author Joe Gittings
@version 0.9.0
*/

package org.occleve.mobileclient.serverbrowser;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.VocabRecordStoreManager;

/**Connects to the wiki, downloads the list of available tests, and
displays them.*/
public class ServerBrowser extends javax.microedition.lcdui.List
implements CommandListener,Runnable
{
    protected Alert m_ProgressAlert;
    protected Vector m_vTestPageNames = new Vector();

    /**Announcement from ListOfTests page on the server.
    Cached here and displayed when the list has finished loading.*/
    protected String m_sAnnouncement;

    protected Command m_DownloadTestCommand;
    protected Command m_MyTestsCommand;

    protected int m_iThreadAction;
    protected final int GET_LIST = 0;
    protected final int DOWNLOAD_TEST = 1;

    public ServerBrowser()
    {
        super(Constants.PRODUCT_NAME,
              javax.microedition.lcdui.List.IMPLICIT);

        m_DownloadTestCommand = new Command("Download", Command.ITEM, 1);
        m_MyTestsCommand = new Command("Your tests", Command.ITEM, 1);

        addCommand(m_DownloadTestCommand);
        addCommand(m_MyTestsCommand);
        setCommandListener(this);

        try
        {
            m_iThreadAction = GET_LIST;
            Thread toPreventBlocking = new Thread( this );
            toPreventBlocking.start();
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }

    }

    public void run()
    {
        // Instantiate the WikiConnection here so the exception
        // handler can call close() on it if need be.
        WikiConnection wc = new WikiConnection();

        try
        {
            m_sAnnouncement = null;

            if (m_iThreadAction==GET_LIST)
            {
                fetchListOfTests(wc);
            }
            else if (m_iThreadAction==DOWNLOAD_TEST)
            {
                downloadTest(wc);
            }

            if (m_sAnnouncement!=null)
            {
                Alert alert = new Alert("Error",m_sAnnouncement,
                                        null,AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                OccleveMobileMidlet.getInstance().displayAlert(alert, this);
            }
            else
            {
                OccleveMobileMidlet.getInstance().setCurrentForm(this);
            }
        }
        catch (Exception e)
        {
            try
            {
                wc.close();
            }
            catch (Exception e2) {System.err.println(e2);}

            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void fetchListOfTests(WikiConnection wc) throws Exception
    {
        m_ProgressAlert = new Alert(null, "Reading list of tests from wiki...",
                                    null, null);
        m_ProgressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(m_ProgressAlert);
        OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

        InputStreamReader reader = wc.open(Config.LIST_OF_TESTS_URL,
                                           m_ProgressAlert);
        do
        {
            String sLine = StaticHelpers.readFromISR(reader,true);
            System.out.println(sLine);
            processLineInListOfTests(sLine);
        } while (reader.ready());

        wc.close();
        m_ProgressAlert.setString("Closed connection to wiki");
    }

    /**Format of line is CategoryName,EN-ZH-TestName.*/
    private void processLineInListOfTests(String sLine) throws Exception
    {
        sLine = sLine.trim();
        if (sLine.equals("")) return;
        if (sLine.indexOf("<pre>")!=-1) return;
        if (sLine.indexOf("</pre>")!=-1) return;

        // Directives start with !
        if (sLine.startsWith("!"))
        {
            if (sLine.startsWith(Config.ANNOUNCEMENT_DIRECTIVE))
            {
                m_sAnnouncement =
                   StaticHelpers.stripBeginning(sLine,Config.ANNOUNCEMENT_DIRECTIVE);
                return;
            }
            else if (sLine.startsWith(Config.MIN_RELEASE_DIRECTIVE))
            {
                String sMinRelease =
                   StaticHelpers.stripBeginning(sLine,Config.MIN_RELEASE_DIRECTIVE);
                long lMinRelease = Long.parseLong(sMinRelease);

                if (Config.VERSION < lMinRelease)
                {
                    throw new Exception("This version of the software is too " +
                                        "old to access the wiki. Please " +
                                        "upgrade to the latest version.");
                }
            }
        }

        // Try to make sure the line is correctly formatted.
        int iCommaIndex = sLine.indexOf(',');
        int iFirstHyphenIndex = sLine.indexOf('-');
        int iSecondHyphenIndex = sLine.indexOf('-',iFirstHyphenIndex+1);

        if ((iCommaIndex!=-1) && (iSecondHyphenIndex!=-1))
        {
            // Parse the category but for now ignore it.
            // Next release will have a category screen which
            // allows you to descend into each category.
            String sCategory = sLine.substring(0,iCommaIndex);

            String sLangPairAndTestName = sLine.substring(iCommaIndex+1);

            m_vTestPageNames.addElement(sLangPairAndTestName);
            append(sLangPairAndTestName,null);
        }
    }

    /*
    private void trace(String s)
    {
        m_ProgressAlert.setString(s);
        pause();
    }
    */

    /*
    private void pause()
    {
        try
        {
            Thread.sleep(2000);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }
    */

    /*Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable d)
    {
        try
        {
            if (c==m_MyTestsCommand)
            {
                OccleveMobileMidlet.getInstance().displayFileChooser(true);
            }
            else if (c==m_DownloadTestCommand)
            {
                m_iThreadAction = DOWNLOAD_TEST;
                Thread toPreventBlocking = new Thread(this);
                toPreventBlocking.start();
            }
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    private void downloadTest(WikiConnection wc) throws Exception
    {
        int iSelIndex = getSelectedIndex();
        String sPageName = getString(iSelIndex);
            ////m_vTestPageNames.elementAt(iSelIndex);

        m_ProgressAlert = new Alert(null, "Getting test from server...",
                                    null, null);
        m_ProgressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(m_ProgressAlert);
        OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

        String sPageNameUnderscores = sPageName.replace(' ','_');
        String sURL = Config.PAGE_URL_STUB + sPageNameUnderscores +
                      Config.PAGE_URL_SUFFIX;
        System.out.println("Page URL = " + sURL);
        InputStreamReader reader = wc.open(sURL,m_ProgressAlert);

        StringBuffer sbSource = new StringBuffer();
        do
        {
            String sLine = StaticHelpers.readFromISR(reader,true);

            int iPreIndex = sLine.indexOf("<pre>");
            int iClosingPreIndex = sLine.indexOf("</pre>");

            if ((iPreIndex==-1) && (iClosingPreIndex==-1))
            {
                //System.out.println(sLine);
                sbSource.append(sLine + Constants.NEWLINE);
            }
        } while (reader.ready());

        VocabRecordStoreManager mgr = new VocabRecordStoreManager();
        mgr.createFileInRecordStore(sPageName,sbSource.toString(),false);

        wc.close();
        m_ProgressAlert.setString("Closed connection to wiki");
    }

}

