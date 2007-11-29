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
@version 0.9.5
*/

package org.occleve.mobileclient.serverbrowser;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.VocabRecordStoreManager;

/**Connects to a compatible wiki, downloads the list of available tests, and
displays them.*/
public class ServerBrowser extends javax.microedition.lcdui.List
implements CommandListener,Runnable
{
    protected Alert m_ProgressAlert;
    protected Vector m_vTestPageNames = new Vector();

    protected boolean m_bListOfTestsIsValid;

    /**Announcement from ListOfTests page on the server.
    Cached here and displayed when the list has finished loading.*/
    protected String m_sAnnouncement;

    protected Command m_DownloadTestCommand;
    protected Command m_MyTestsCommand;

    protected int m_iThreadAction;
    protected final int GET_LIST = 0;
    protected final int DOWNLOAD_TEST = 1;

    protected String m_sPageNameToDownload;
    protected Displayable m_DisplayableAfterDownload;

    protected String m_sListOfTestsURL;
    protected String m_sQuizURLStub;
    protected String m_sQuizURLSuffix;

    public ServerBrowser(String sListOfTestsURL,
                         String sQuizURLStub,
                         String sQuizURLSuffix)
    {
        super("Tests you can download",
              javax.microedition.lcdui.List.IMPLICIT);

        m_sListOfTestsURL = sListOfTestsURL;
        m_sQuizURLStub = sQuizURLStub;
        m_sQuizURLSuffix = sQuizURLSuffix;

        // The download test command should have the higher priority.
        m_DownloadTestCommand = new Command("Download", Command.ITEM, 1);
        m_MyTestsCommand = new Command("Your tests", Command.ITEM, 2);

        addCommand(m_DownloadTestCommand);
        addCommand(m_MyTestsCommand);
        setCommandListener(this);
    }

    public void populateAndDisplay()
    {
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

    public void startDownloadingTest(String sPageName,
                                     Displayable displayableAfterDownload)
    {
        try
        {
            m_sPageNameToDownload = sPageName;
            m_DisplayableAfterDownload = displayableAfterDownload;

            m_iThreadAction = DOWNLOAD_TEST;
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

            int iListOfTestsTries = -1;
            if (m_iThreadAction==GET_LIST)
            {
                iListOfTestsTries = fetchListOfTests(wc);

                if (m_sAnnouncement!=null)
                {
                    String sMsg = m_sAnnouncement;
                    if (iListOfTestsTries>1)
                    {
                        sMsg = "(Took " + iListOfTestsTries + " tries to load)..." +
                               sMsg;
                    }

                    Alert alert = new Alert("Latest news",sMsg,
                                            null,AlertType.INFO);
                    alert.setTimeout(Alert.FOREVER);
                    OccleveMobileMidlet.getInstance().displayAlert(alert, this);
                }
                else
                {
                    OccleveMobileMidlet.getInstance().setCurrentForm(this);
                }
            }
            else if (m_iThreadAction==DOWNLOAD_TEST)
            {
                downloadTest(wc);
                OccleveMobileMidlet.getInstance().setCurrentForm(m_DisplayableAfterDownload);
            }
        }
        catch (Exception e)
        {
        	System.err.println(e);
        	
            try
            {
                wc.close();
            }
            catch (Exception e2) {System.err.println(e2);}

            String sMsg = e.toString() + "... " + wc.getConnectionAction();
            OccleveMobileMidlet.getInstance().onError(sMsg);
        }
    }

    /**Returns the number of tries it took to load the list of tests.*/
    protected int fetchListOfTests(WikiConnection wc) throws Exception
    {
        m_ProgressAlert = new Alert(null, "Reading list of tests from wiki...",
                                    null, null);
        m_ProgressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(m_ProgressAlert);
        OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

        InputStreamReader reader;

        int iTries = 0;
        boolean bRetry;
        m_bListOfTestsIsValid = false;

        do
        {
        	wc.setConnectionAction("Calling WikiConnection.openISR");
            reader = wc.openISR(m_sListOfTestsURL,m_ProgressAlert,true);

            int iLineCount = 0;
            do
            {
            	wc.setConnectionAction("Calling StaticHelpers.readFromISR");
                String sLine = StaticHelpers.readFromISR(reader, true);
                
                System.out.println(sLine);
                processLineInListOfTests(sLine);

                iLineCount++;
                m_ProgressAlert.setString("Read " + iLineCount + " lines");
            } while (reader.ready());

            // 0.9.3: Retry up to a limit specified in Config class.
            // This is primarily to deal with China Mobile's
            // recently-introduced "welcome" page (but should help
            // for other mobile operators with a similar policy).
            iTries++;
            bRetry = ((m_bListOfTestsIsValid==false) &&
                      (iTries<Config.CONNECTION_TRIES_LIMIT));
        } while (bRetry);

    	wc.setConnectionAction("Calling WikiConnection.close()");
        wc.close();

        m_ProgressAlert.setString("Closed connection to wiki (tries=" + iTries + ")");

        if (m_bListOfTestsIsValid==false)
        {
            throw new Exception("Failed to load list of tests from wiki");
        }

        return iTries;
    }

    /**Format of line is CategoryName,EN-ZH-TestName,
    unless it's a directive.*/
    private void processLineInListOfTests(String sLine) throws Exception
    {
        sLine = sLine.trim();
        if (sLine.equals("")) return;
        if (sLine.indexOf("<pre>")!=-1) return;
        if (sLine.indexOf("</pre>")!=-1) return;

        // Directives start with !
        if (sLine.startsWith("!"))
        {
            processDirective(sLine);
            return;
        }

        // Try to make sure the line is correctly formatted.
        int iCommaIndex = sLine.indexOf(',');

        // 0.9.3: Don't look for the hyphens... in order to tolerate
        // the naming conventions for wikiversity quizzes.
        //int iFirstHyphenIndex = sLine.indexOf('-');
        //int iSecondHyphenIndex = sLine.indexOf('-',iFirstHyphenIndex+1);

        if (iCommaIndex!=-1) /////&& (iSecondHyphenIndex!=-1))
        {
            // Parse the category but for now ignore it.
            // Next release will have a category screen which
            // allows you to descend into each category.
            String sCategory = sLine.substring(0,iCommaIndex);

            String sQuizName = sLine.substring(iCommaIndex+1);
            m_vTestPageNames.addElement(sQuizName);
            append(sQuizName,null);
        }
    }

    /**Ignores unknown directives, for future compatibility.*/
    private void processDirective(String sLine)
    throws Exception
    {
        if (sLine.startsWith(Config.ANNOUNCEMENT_DIRECTIVE))
        {
            m_sAnnouncement =
               StaticHelpers.stripBeginning(sLine,Config.ANNOUNCEMENT_DIRECTIVE);
        }
        else if (sLine.startsWith(Config.MIN_RELEASE_DIRECTIVE))
        {
            String sMinRelease =
               StaticHelpers.stripBeginning(sLine,Config.MIN_RELEASE_DIRECTIVE);
            long lMinRelease = Long.parseLong(sMinRelease);
            m_bListOfTestsIsValid = true;

            if (Config.VERSION < lMinRelease)
            {
                throw new Exception("This version of the software is too " +
                                    "old to access the wiki. Please " +
                                    "upgrade to the latest version.");
            }
        }
    }

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
                int iSelIndex = getSelectedIndex();
                String sPageName = getString(iSelIndex);
                startDownloadingTest(sPageName,this);

                //m_sPageNameToDownload = sPageName;
                //m_iThreadAction = DOWNLOAD_TEST;
                //Thread toPreventBlocking = new Thread(this);
                //toPreventBlocking.start();
            }
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    /**New in 0.9.4: rewritten to be guaranteed to read all bytes from the URL
    where the test is stored.*/
    private void downloadTest(WikiConnection wc) throws Exception
    {
        m_ProgressAlert = new Alert(null, "Getting test from server...",
                                    null, null);
        m_ProgressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(m_ProgressAlert);
        OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

        String sPageNameUnderscores = m_sPageNameToDownload.replace(' ','_');
        String sURL = m_sQuizURLStub + sPageNameUnderscores + m_sQuizURLSuffix;
        System.out.println("Quiz URL = " + sURL);

        boolean bIsISPWelcomePage = true;
        int iTries = 0;
    	String sQuizData;

    	do
        {
    		System.out.println("About to call readAllBytes");
        	byte[] quizData = wc.readAllBytes(sURL,m_ProgressAlert,true);        	
        	System.out.println("About to instantiate String from bytes");

        	sQuizData = new String(quizData,Config.ENCODING);
        	System.out.println("Instantiated String from bytes ok");
        	////System.out.println("It's");
        	////System.out.println(sQuizData);

        	pause(1000);
            
        	int iPreIndex = sQuizData.indexOf("<pre>");
            int iClosingPreIndex = sQuizData.indexOf("</pre>");
            int iQuizIndex = sQuizData.indexOf(Config.WIKIVERSITY_QUIZ_TAG_STUB);

            if ((iPreIndex!=-1) || (iClosingPreIndex!=-1) || (iQuizIndex!=-1))
            {
                // If there's any of these tags we can guess it's
                // not the ISP welcome page.
                bIsISPWelcomePage = false;
            }

            iTries++;
        } while (bIsISPWelcomePage && (iTries<Config.CONNECTION_TRIES_LIMIT));

        wc.close();
        System.out.println("Closed WikiConnection");

        if (bIsISPWelcomePage)
        {
            throw new Exception("Failed to load test from wiki");
        }
        else
        {
        	System.out.println("Creating file in RecordStore");
        	VocabRecordStoreManager mgr = new VocabRecordStoreManager();
            mgr.createFileInRecordStore(m_sPageNameToDownload, sQuizData,
                                        false);
        	System.out.println("Created file in RecordStore");
        }

        String sMsg = "Successfully loaded " + m_sPageNameToDownload;
        if (iTries>1) sMsg += " (after " + iTries + " attempts)";
        m_ProgressAlert.setString(sMsg);

        // Pause briefly so the user can actually read the message.
        pause(2000);
    }

    /**The pre-0.9.4 version of downloadTest(). Defunct.*/
    private void OLD_downloadTest(WikiConnection wc) throws Exception
    {
        m_ProgressAlert = new Alert(null, "Getting test from server...",
                                    null, null);
        m_ProgressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(m_ProgressAlert);
        OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

        String sPageNameUnderscores = m_sPageNameToDownload.replace(' ','_');
        String sURL = m_sQuizURLStub + sPageNameUnderscores + m_sQuizURLSuffix;
        System.out.println("Quiz URL = " + sURL);

        boolean bIsISPWelcomePage = true;
        int iTries = 0;
        InputStreamReader reader;
        StringBuffer sbSource;
        int iTotalBytesRead;
        do
        {
            reader = wc.openISR(sURL, m_ProgressAlert,true);

            sbSource = new StringBuffer();
            iTotalBytesRead = 0;
            do
            {
                String sLine = StaticHelpers.readFromISR(reader, true);

                int iPreIndex = sLine.indexOf("<pre>");
                int iClosingPreIndex = sLine.indexOf("</pre>");
                int iQuizIndex = sLine.indexOf(Config.WIKIVERSITY_QUIZ_TAG_STUB);

                if ((iPreIndex == -1) && (iClosingPreIndex == -1))
                {
                    sbSource.append(sLine + Constants.NEWLINE);
                }

                if ((iPreIndex!=-1) || (iClosingPreIndex!=-1) || (iQuizIndex!=-1))
                {
                    // If there's any of these tags we can guess it's
                    // not the ISP welcome page.
                    bIsISPWelcomePage = false;
                }

                // Update progress display.
                int iBytesInLine = sLine.getBytes(Config.ENCODING).length + 1;
                iTotalBytesRead += iBytesInLine;
                
                m_ProgressAlert.setString("Loaded " + iTotalBytesRead + " of " +
                		wc.getPageLength() + " bytes");
            } while (reader.ready());

            iTries++;
        } while (bIsISPWelcomePage && (iTries<Config.CONNECTION_TRIES_LIMIT));

        wc.close();

        if (bIsISPWelcomePage)
        {
            throw new Exception("Failed to load test from wiki");
        }
        else
        {
            VocabRecordStoreManager mgr = new VocabRecordStoreManager();
            mgr.createFileInRecordStore(m_sPageNameToDownload, sbSource.toString(),
                                        false);
        }

        String sMsg = "Successfully loaded " + m_sPageNameToDownload +
        				" (loaded " + iTotalBytesRead + " of " +
        				wc.getPageLength() + " bytes)";
        if (iTries>1) sMsg += " (after " + iTries + " attempts)";
        m_ProgressAlert.setString(sMsg);

        // Pause briefly so the user can actually read the message.
        pause(3000);
    }

    public byte[] loadAudioClipFromWiki(String sAudioClipFilename,Alert progressAlert)
    {
        // Instantiate the WikiConnection here so the exception
        // handler can call close() on it if need be.
        WikiConnection wc = new WikiConnection();
        System.out.println("Obtained wikiconnection ok");

        try
        {
            return loadAudioClipFromWiki_Inner(sAudioClipFilename,progressAlert,wc);
        }
        catch (Exception e)
        {
            try
            {
                wc.close();
            }
            catch (Exception e2) {System.err.println(e2);}

            OccleveMobileMidlet.getInstance().onError(e);
            return null;
        }
    }

    private byte[] loadAudioClipFromWiki_Inner(String sAudioClipFilename,
                                               Alert progressAlert,
                                               WikiConnection wc) throws Exception
    {
        String sWithUnderscores = sAudioClipFilename.replace(' ', '_');
        String sDescriptorURL = Config.AUDIO_CLIP_URL_STUB + sWithUnderscores +
                                Config.AUDIO_CLIP_URL_SUFFIX;
        System.out.println("Audio clip descriptor URL = " + sDescriptorURL);

        progressAlert.setString("Loading clip locator");

        int iTries = 0;
        InputStreamReader reader;
        String sTrueURL;
        do
        {
            System.out.println("Trying to obtain InputStreamReader");
            reader = wc.openISR(sDescriptorURL, null,false);
            System.out.println("Obtained InputStreamReader ok");

            sTrueURL = null;
            do
            {
                String sLine = StaticHelpers.readFromISR(reader, true);
                System.out.println("Parsing " + sLine);

                int iIndex = sLine.indexOf("URL=");
                if (iIndex != -1)
                {
                    sTrueURL = sLine.substring(iIndex + "URL=".length());
                    System.out.println("sTrueURL = " + sTrueURL);
                }

            } while ((reader.ready()) && (sTrueURL == null));

            iTries++;
        } while ((sTrueURL == null) && (iTries<Config.CONNECTION_TRIES_LIMIT));

        reader.close();

        if (sTrueURL==null)
        {
            throw new Exception("Couldn't find true URL of audio clip");
        }

        ////////////////////////////////////////////////////////////////////
        // Now load the actual MP3 file

        progressAlert.setString("Loading the clip itself");
        DataInputStream dis = wc.openDIS(sTrueURL,m_ProgressAlert,false);
        System.out.println("Opened DataInputStream ok");

        int iBufferSize = wc.getPageLength();
        System.out.println("iBufferSize = " + iBufferSize);
        byte[] clipData = new byte[iBufferSize]; //////// + 1000];

        int iBytesRead;
        int iOffset = 0;

        // Terminate this loop on iBytesRead==0 as well as ==-1, since
        // in practice that seems to happen when reading an MP3 from the wiki.
        do
        {
            iBytesRead = dis.read(clipData,iOffset,iBufferSize-iOffset);
            iOffset += iBytesRead;
            progressAlert.setString("Loaded " + iOffset + " of " +
                                      iBufferSize + " bytes");
        } while ((iBytesRead!=-1) && (iBytesRead!=0));

        dis.close();

        System.out.println("Number of MP3 bytes read = " + iOffset);

        wc.close();
        progressAlert.setString("Loaded MP3 ok");

        return clipData;
    }
    
    private void pause(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }
}

