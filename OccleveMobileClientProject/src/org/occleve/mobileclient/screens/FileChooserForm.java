/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-9  Joe Gittings

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
@version 0.9.7
*/

package org.occleve.mobileclient.screens;

import com.sun.lwuit.*;
import com.sun.lwuit.layouts.*;
import com.sun.lwuit.list.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.dictionary.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.qa.language.*;
import org.occleve.mobileclient.qa.wikiversity.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.serverbrowser.*;
import org.occleve.mobileclient.screens.options.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

/**The main screen of the application: lists the quizzes currently stored in the
phone, and allows the user to select a quiz for testing or viewing.*/
public class FileChooserForm extends Form
implements Runnable //,CommandListener
{
	protected List m_List = new InnerList();
	
	protected ListOfTests m_ListOfTests;

    protected ProgressAlert m_ProgressAlertCache;
    protected ListOfTestsEntry m_EntryCache;

    protected static final String NO_TESTS_IN_PHONE_MSG =
            "Please download some tests either " +
            "from Wikiversity or Occleve";

    // LWUIT-TODO - reenable
    //protected CommandListener m_ExternalCommandListener;
    // public void setExternalCommandListener(CommandListener cl) {m_ExternalCommandListener = cl;}

    /**Stored as a member so options are remembered between invoking tests.*/
    protected TestOptionsScreen m_TestOptionsScreen;

    /**Stored as a member so options are remembered between invoking tests.*/
    protected SimpleTestOptionsScreen m_SimpleTestOptionsScreen;

    /**Stored as a member so that the options are remembered between
    invoking tests.*/
    protected ChineseTestOptionsScreen m_ChineseTestOptionsScreen;

    /**An intermediate screen on which the user chooses
    which source to download quizzes from.*/
    protected Command m_DownloadQuizzesCommand;
    
    protected Command m_ConnectionTroubleshooterCommand;  // 0.9.7
    protected Command m_DictionaryCommand;  // 0.9.7
    protected Command m_TestCommand;
    protected Command m_ViewCommand;
    protected Command m_RedownloadCommand;
    protected Command m_SearchAllTestsCommand;
    protected Command m_DevStuffScreenCommand;
    protected Command m_ShowLicenseCommand;
    protected CommonCommands m_CommonCommands;

    // 0.9.6 - remove the Edit and Rapid Add commands - those functions aren't
    // working anyway, and they're confusing users.
    //protected Command m_EditCommand;
    //protected Command m_RapidAddCommand;

    /////public boolean isScrollableX() {return true;}
    
    private class InnerList extends List
    {
        public boolean isScrollableX() {return true;}
        public boolean isScrollableY() {return true;}

        /*        
        public int getPreferredH()
        {
        	System.out.println("Setting List preferredH to " + getParent().getHeight());
        	return getParent().getHeight();
        }
        
        public int getPreferredW()
        {
        	System.out.println("Setting List preferredW to " + getParent().getWidth());
        	return getParent().getWidth();
        }*/
    }
    
    public FileChooserForm(boolean bAddCommands) throws Exception
    {
        super();
        setScrollable(false); // Otherwise the List won't be scrollable.
        
    	//Image logoImage = StaticHelpers.loadOccleveLogo();
    	//setBgImage(logoImage);

        setLayout(new BorderLayout());
        addComponent(BorderLayout.CENTER,m_List);
        
        // Don't display a number next to each item in the list.
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setShowNumbers(false);
        m_List.setListCellRenderer(renderer);

        m_List.setItemGap(0);

        if (bAddCommands)
        {
        	m_DownloadQuizzesCommand = new Command("Download quizzes");

        	m_ConnectionTroubleshooterCommand =
        		new Command("Test connection"); // 0.9.7
            m_DictionaryCommand = new Command("Dictionary"); // 0.9.7
            m_TestCommand = new Command("Test");
            m_ViewCommand = new Command("View");
            m_RedownloadCommand = new Command("Redownload");
            m_SearchAllTestsCommand = new Command("Search all tests");
            m_DevStuffScreenCommand = new Command("Dev stuff");
            m_ShowLicenseCommand = new Command("Show license");

            // Disabled in 0.9.6 - see earlier comment
            //m_EditCommand = new Command("Edit");
            //m_RapidAddCommand = new Command("Rapid add");

            m_CommonCommands = new CommonCommands();

            addCommand(m_TestCommand);

            addCommand(m_DevStuffScreenCommand);
            addCommand(m_ShowLicenseCommand);
            ///////addCommand(m_DictionaryCommand); // 0.9.7
            addCommand(m_ConnectionTroubleshooterCommand); // 0.9.7

            m_CommonCommands.addToForm(this);
            addCommand(m_SearchAllTestsCommand);
            addCommand(m_RedownloadCommand);
            addCommand(m_DownloadQuizzesCommand); // 0.9.6
            addCommand(m_ViewCommand);            

            // Disabled in 0.9.6 - see earlier comment
            //addCommand(m_EditCommand);
            //addCommand(m_RapidAddCommand);
        }

        populateWithFilenames();
        System.out.println("Finished populating with filenames");

        m_TestOptionsScreen = new TestOptionsScreen();
        m_SimpleTestOptionsScreen = new SimpleTestOptionsScreen();
        m_ChineseTestOptionsScreen = new ChineseTestOptionsScreen();
        
        OccleveMobileMidlet.getInstance().setCurrentForm(this);
    }

    public void populateWithFilenames() throws Exception
    {
    	System.out.println("Entering populateWithFilenames");
    	
    	// LWUIT-TO-DO Image logoImage = StaticHelpers.loadOccleveLogo();    	
    	//String sMsg = "occleve.berlios.de/pocketchinese\n" +
    	//				"©2007-9 Joe Gittings & contributors";
    	//ProgressAlert alt = new ProgressAlert("",sMsg);
        //////alt.setTimeout(Alert.FOREVER);
        ///////StaticHelpers.safeAddGaugeToAlert(alt);
    	//        OccleveMobileMidlet.getInstance().setCurrentForm(alt);
    	//       alt.show();
    	//    	System.out.println("Showed alt dialog");
    	
    	// Clear out the existing items in this form, if any.
        DefaultListModel model = new DefaultListModel();
        m_List.setModel(model);

        m_ListOfTests = new ListOfTests(null); // LWUIT-TO-DO alt);

        OccleveMobileMidlet.getInstance().showSplashScreen();
        
        if (m_ListOfTests.getSize()==0)
        {
            m_List.addItem(NO_TESTS_IN_PHONE_MSG);
            return;
        }

        for (int i=0; i<m_ListOfTests.getSize(); i++)
        {
            String sFilename = m_ListOfTests.getFilename(i);
            Integer recordStoreID = m_ListOfTests.getRecordStoreID(sFilename);

            String sDisplayText = StaticHelpers.stripEnding(sFilename,".txt");
            sDisplayText = StaticHelpers.stripEnding(sDisplayText,".xml");

            if (sDisplayText.startsWith("EN-ZH"))
            {
            	sDisplayText = sDisplayText.substring(5);
            }
            
            // The asterisk indicates a quiz that's NOT stored in the recordstore
            if (recordStoreID==null) sDisplayText = "* " + sDisplayText;

            m_List.addItem(sDisplayText);
        }
    }

    /*Implementation of CommandListener.*/
    public void actionCommand(Command c)
    {
        try
        {
        	// LWUIT-TO-DO - reenable later
        	/*
            if (m_ExternalCommandListener!=null)
            {
                m_ExternalCommandListener.commandAction(c,d);
            }
            */

            commandAction_Inner(c);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    /*Subfunction for code clarity.*/
    public void commandAction_Inner(Command c) throws Exception
    {
        ListOfTestsEntry entry;
        if (m_ListOfTests.getSize()==0)
        {
        	entry = null;
        }
        else
        {
	        int iSelIndex = m_List.getSelectedIndex();
	        entry = m_ListOfTests.getEntry(iSelIndex);
        }

        if (c==m_DownloadQuizzesCommand)
        {
        	ChooseQuizServerScreen chooser = new ChooseQuizServerScreen();
            OccleveMobileMidlet.getInstance().setCurrentForm(chooser);
        }
        else if (c==m_ShowLicenseCommand)
        {
            OccleveMobileMidlet.getInstance().setCurrentForm(new ShowGPLForm());
        }
        else if (c==m_DictionaryCommand)
        {
        	DictionaryBrowser dictBrowser = new DictionaryBrowser();
            /////OccleveMobileMidlet.getInstance().setCurrentForm(dictBrowser);
        }
        else if (c==m_ConnectionTroubleshooterCommand)
        {
        	ConnectionTroubleshooter ct = new ConnectionTroubleshooter();
            OccleveMobileMidlet.getInstance().setCurrentForm(ct);
        }
        else if (c==m_DevStuffScreenCommand)
        {           	
            ExcludableHooks.displayDevStuffScreen(entry);
        }

        try
        {
        	Thread.sleep(10000);
        }
        catch (Exception e) {}
        
        // The rest of the commands aren't appropriate if there
        // aren't any tests in the phone.
        if (m_ListOfTests.getSize()==0)
        {
            return;
        }

        if (c==m_TestCommand)
        {
        	// Display progress while loading the test.
            ProgressAlert alt =
            	new ProgressAlert("","Loading \n" + entry.getFilename());
            System.out.println("Showing ProgressAlert");
            OccleveMobileMidlet.getInstance().setCurrentForm(alt);
            System.out.println("Showed ProgressAlert");

            System.out.println("Starting thread");
            m_EntryCache = entry;
            m_ProgressAlertCache = alt;
            new Thread(this).start();
            System.out.println("Started thread");

        	///////displayTestOptions(entry);
        }
        else if (c==m_ViewCommand)
        {
            OccleveMobileMidlet.getInstance().displayTest(entry);
        }
        else if (c==m_RedownloadCommand)
        {
            redownloadQuiz(entry);
        }
        else if (c==m_SearchAllTestsCommand)
        {
            SearchAllFilesForm saff = new SearchAllFilesForm();
            OccleveMobileMidlet.getInstance().setCurrentForm(saff);
        }
        /*
        // Disabled in 0.9.6 - see earlier comment
        else if (c==m_EditCommand)
        {
            Screen returnTo = this;
            ExcludableHooks.editQA(entry,null,returnTo);
        }
        else if (c==m_RapidAddCommand)
        {
            ExcludableHooks.displayRapidAdd(entry);
        }
        */
        else
        {
        	m_CommonCommands.actionCommand(c);

        	// Could be an external command so don't object if the command
            // is unknown.
        }
    }

    /**0.9.6 - background thread so that a progress alert can be displayed while
    the test is being loaded.*/
    public void run()
    {
        try
        {
            displayTestOptions(m_EntryCache,m_ProgressAlertCache);
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    protected void displayTestOptions(ListOfTestsEntry entry,ProgressAlert progressAlert)
    throws Exception
    {
    	System.out.println("Trying to load Test");
       Test theTest = new Test(entry,null); // LWUIT-TO-DO progressAlert);
   	System.out.println("Loaded Test ok");
   	System.out.println("Trying to displayTestOptions");
       displayTestOptions(theTest);
      	System.out.println("Called displayTestOptions ok");
     	System.out.println("Disposed progressAlert");
    }

   /**Public so it can be invoked when the user restarts a test from the test
   results screen.*/
   public void displayTestOptions(Test theTest)
   throws Exception
   {
       // Until this software supports all wikiversity question types,
       // this is a definite possibility.
       if (theTest.getQACount()==0)
       {
           Dialog alert = new Dialog(Constants.EMPTY_QUIZ_MSG);
           //alert.setTimeout(Alert.FOREVER);
           //OccleveMobileMidlet.getInstance().displayAlert(alert,this);
           alert.show();
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

    protected void redownloadQuiz(ListOfTestsEntry entry)
    throws Exception
    {
		VocabRecordStoreManager rsMgr =
			OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();

        String sTestSource = rsMgr.getTestContents(entry);

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

        browser.startDownloadingTest(entry.getFilename(),this);
    }
}

