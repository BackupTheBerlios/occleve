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
@version 0.9.3
*/

package org.occleve.mobileclient.screens;

import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.qa.language.*;
import org.occleve.mobileclient.qa.wikiversity.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.serverbrowser.*;
import org.occleve.mobileclient.screens.options.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

public class FileChooserForm extends List
implements CommandListener
{
    protected ListOfTests m_ListOfTests;

    protected static final String NO_TESTS_IN_PHONE_MSG =
            "Please download some tests either " +
            "from Wikiversity or Occleve";

    protected CommandListener m_ExternalCommandListener;
    public void setExternalCommandListener(CommandListener cl)
    {
        m_ExternalCommandListener = cl;
    }

    /**Stored as a member so that the options are remembered between
    invoking tests.*/
    protected TestOptionsScreen m_TestOptionsScreen;

    /**Stored as a member so that the options are remembered between
    invoking tests.*/
    protected SimpleTestOptionsScreen m_SimpleTestOptionsScreen;

    /**Stored as a member so that the options are remembered between
    invoking tests.*/
    protected ChineseTestOptionsScreen m_ChineseTestOptionsScreen;

    protected Command m_BrowseWikiversityCommand;
    protected Command m_BrowseOccleveCommand;
    protected Command m_TestCommand;
    protected Command m_ViewCommand;
    protected Command m_RedownloadCommand;
    protected Command m_SearchAllTestsCommand;
    protected Command m_PauseCommand;
    protected Command m_DevStuffScreenCommand;
    protected Command m_ShowLicenseCommand;

    protected Command m_EditCommand;
    protected Command m_RapidAddCommand;

    public FileChooserForm(boolean bAddCommands)
    throws Exception
    {
        super(Constants.PRODUCT_NAME,List.IMPLICIT);

        if (bAddCommands)
        {
            m_BrowseWikiversityCommand = new Command("Download Wikiversity quizzes", Command.ITEM, 2);
            m_BrowseOccleveCommand = new Command("Download Occleve tests", Command.ITEM, 2);

            m_TestCommand = new Command("Test", Command.ITEM, 1);
            m_ViewCommand = new Command("View", Command.ITEM, 2);
            m_RedownloadCommand = new Command("Redownload", Command.ITEM, 2);
            m_SearchAllTestsCommand = new Command("Search all tests", Command.ITEM, 2);
            m_PauseCommand = new Command("Pause", Command.ITEM, 2);
            m_DevStuffScreenCommand = new Command("Dev stuff", Command.ITEM, 2);
            m_ShowLicenseCommand = new Command("Show license", Command.ITEM, 2);
            m_EditCommand = new Command("Edit", Command.ITEM, 2);
            m_RapidAddCommand = new Command("Rapid add", Command.ITEM, 2);

            addCommand(m_BrowseWikiversityCommand);
            addCommand(m_BrowseOccleveCommand);
            addCommand(m_TestCommand);
            addCommand(m_ViewCommand);
            addCommand(m_RedownloadCommand);
            addCommand(m_SearchAllTestsCommand);
            addCommand(m_PauseCommand);
            addCommand(m_DevStuffScreenCommand);
            addCommand(m_ShowLicenseCommand);
            addCommand(m_EditCommand);
            addCommand(m_RapidAddCommand);
        }

        populateWithFilenames();

        m_TestOptionsScreen = new TestOptionsScreen();
        m_SimpleTestOptionsScreen = new SimpleTestOptionsScreen();
        m_ChineseTestOptionsScreen = new ChineseTestOptionsScreen();

        setCommandListener(this);
    }

    public void populateWithFilenames() throws Exception
    {
        // Clear out the existing items in this form, if any.
        deleteAll();

        // See whether keypresses are supported
        ///FileChooserCustomItem fcciTest = new FileChooserCustomItem(this);
        ///boolean bKeypressesSupported = fcciTest.areKeypressesSupported();

        m_ListOfTests = new ListOfTests();

        if (m_ListOfTests.getSize()==0)
        {
            append(NO_TESTS_IN_PHONE_MSG,null);
            return;
        }

        for (int i=0; i<m_ListOfTests.getSize(); i++)
        {
            String sFilename = m_ListOfTests.getFilename(i);
            Integer recordStoreID = m_ListOfTests.getRecordStoreID(sFilename);

            String sDisplayText = StaticHelpers.stripEnding(sFilename,".txt");
            sDisplayText = StaticHelpers.stripEnding(sDisplayText,".xml");

            if (recordStoreID!=null) sDisplayText = "* " + sDisplayText;

            // THIS WON'T WORK BECAUSE KEYPRESSES ONLY CAUGHT WHEN THE CustomITEM
            // HAS FOCUS...
            // If CustomItem supports keypresses on this phone, add an invisible
            // custom item before the filename which will catch keypresses.
            //if (bKeypressesSupported)
            //{
            //    FileChooserCustomItem fcci = new FileChooserCustomItem(this);
            //    append(fcci);
            //}

            append(sDisplayText,null);
        }
    }

    /*Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable d)
    {
        try
        {
            if (m_ExternalCommandListener!=null)
            {
                m_ExternalCommandListener.commandAction(c,d);
            }

            commandAction_Inner(c,d);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    /*Subfunction for code clarity.*/
    public void commandAction_Inner(Command c,Displayable d) throws Exception
    {
        if (c==m_BrowseWikiversityCommand)
        {
            ServerBrowser browser =
               new ServerBrowser(Config.WIKIVERSITY_LIST_OF_QUIZZES_URL,
                                 Config.WIKIVERSITY_QUIZ_URL_STUB,
                                 Config.WIKIVERSITY_QUIZ_URL_SUFFIX);
            browser.populateAndDisplay();
        }
        else if (c==m_BrowseOccleveCommand)
        {
            ServerBrowser browser =
                    new ServerBrowser(Config.OCCLEVE_LIST_OF_TESTS_URL,
                                      Config.OCCLEVE_QUIZ_URL_STUB,
                                      Config.OCCLEVE_QUIZ_URL_SUFFIX);
            browser.populateAndDisplay();
        }

        // The rest of the commands aren't appropriate if there
        // aren't any tests in the phone.
        if (m_ListOfTests.getSize()==0)
        {
            return;
        }

        int iSelIndex = getSelectedIndex();
        String sFilename = m_ListOfTests.getFilename(iSelIndex);
        Integer iRSID = m_ListOfTests.getRecordStoreIDByIndex(iSelIndex);

        if (c==m_TestCommand)
        {
            displayTestOptions(sFilename,iRSID);
        }
        else if (c==m_ViewCommand)
        {
            OccleveMobileMidlet.getInstance().displayTest(sFilename,iRSID);
        }
        else if (c==m_RedownloadCommand)
        {
            redownloadQuiz(sFilename,iRSID);
        }
        else if (c==m_DevStuffScreenCommand)
        {
            ExcludableHooks.displayDevStuffScreen(sFilename,iRSID);
        }
        else if (c==m_SearchAllTestsCommand)
        {
            SearchAllFilesForm saff = new SearchAllFilesForm();
            OccleveMobileMidlet.getInstance().setCurrentForm(saff);
        }
        else if (c==m_PauseCommand)
        {
            OccleveMobileMidlet.getInstance().tryToPlaceinBackground();
        }
        else if (c==m_EditCommand)
        {
            Screen returnTo = this;
            ExcludableHooks.editQA(sFilename,iRSID,null,returnTo);
        }
        else if (c==m_RapidAddCommand)
        {
            ExcludableHooks.displayRapidAdd(sFilename,iRSID);
        }
        else if (c==m_ShowLicenseCommand)
        {
            Displayable gplForm = new ShowGPLForm();
            OccleveMobileMidlet.getInstance().setCurrentForm(gplForm);
        }
        else
        {
            // Could be an external command so don't object if the command
            // is unknown.
        }
    }

    protected void displayTestOptions(String sFilename,Integer iRecordStoreID)
    throws Exception
    {
       Test theTest = new Test(sFilename,iRecordStoreID);

       // Until this software supports all wikiversity question types,
       // this is a definite possibility.
       if (theTest.getQACount()==0)
       {
           Alert alert = new Alert(null,Constants.EMPTY_QUIZ_MSG, null, null);
           alert.setTimeout(Alert.FOREVER);
           OccleveMobileMidlet.getInstance().displayAlert(alert,this);
           return;
       }

       QA firstQA = theTest.getQA(0);
       if (firstQA instanceof LanguageQA)
       {
           m_ChineseTestOptionsScreen.makeVisible(theTest);
       }
       else if (firstQA instanceof WikiversityQA)
       {
           m_TestOptionsScreen.makeVisible(theTest);
       }
       else
       {
           m_SimpleTestOptionsScreen.makeVisible(theTest);
       }
    }

    public void jumpToFilename(char cJumpToThis)
    {
        /*
        FilenameItem fi = (FilenameItem)get(0);

        final int iItemCount = size();
        for (int i=0; i<iItemCount; i++)
        {
            fi = (FilenameItem)get(i);
            if (fi.getFilename().charAt(0) == cJumpToThis) break;
        }

        Display.getDisplay(OccleveMobileMidlet.getInstance()).setCurrentItem(fi);
        */
    }

    protected void redownloadQuiz(String sFilename,Integer iRSID)
    throws Exception
    {
        VocabRecordStoreManager rsMgr = new VocabRecordStoreManager();
        String sTestSource = rsMgr.getTestContents(iRSID.intValue(),sFilename);

        boolean bIsWikiversityQuiz = (sTestSource.indexOf("<quiz") != -1);

        ServerBrowser browser;
        if (bIsWikiversityQuiz)
        {
            browser =
                    new ServerBrowser(Config.WIKIVERSITY_LIST_OF_QUIZZES_URL,
                                      Config.WIKIVERSITY_QUIZ_URL_STUB,
                                      Config.WIKIVERSITY_QUIZ_URL_SUFFIX);
        }
        else
        {
            browser =
                    new ServerBrowser(Config.OCCLEVE_LIST_OF_TESTS_URL,
                                      Config.OCCLEVE_QUIZ_URL_STUB,
                                      Config.OCCLEVE_QUIZ_URL_SUFFIX);
        }

        browser.startDownloadingTest(sFilename,this);
    }
}

