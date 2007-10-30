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
@version 0.9.4
*/

package org.occleve.mobileclient.excludable.devstuff;

import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.file.*;
import javax.microedition.media.*;
import javax.microedition.rms.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;
import org.occleve.mobileclient.excludable.translation.*;

public class DevStuffScreen extends javax.microedition.lcdui.List
implements CommandListener,Excludable,Runnable
{
	protected ListOfTestsEntry m_SelectedListOfTestsEntry;

    ////////////// Implementation of Excludable ////////////////////////////////
    public void setListOfTestsEntry(ListOfTestsEntry e) {m_SelectedListOfTestsEntry = e;}

    // Not relevant in this class:
    public void initialize() {}
    public void setQAIndex(Integer i) {}
    public void setScreenToReturnTo(Displayable d) {}
    ////////////////////////////////////////////////////////////////////////////

    /**Should be one of the prompt values.*/
    protected String m_sThreadAction;

    protected final String COUNT_NEWLINES = "Count LFs and CRLFs";
    protected final String CREATE_BACKUP = "Create backup";
    protected final String DELETE_TEST = "Delete test";
    protected final String VIEW_SOURCE = "View source";
    protected final String COPY_TO_RECORDSTORE = "Copy to recordstore";
    protected final String CONVERT_TO_XML = "Convert to XML";
    protected final String PRINT_TO_FILE = "Print to file";

    protected final String SAVE_ALL_TESTS_TO_FILESYSTEM = "Save all tests in RecordStore to filesystem";
    protected final String JAVA_UTF_CONVERT_ALL_TO_XML = "JavaUTF: Convert all to XML";
    protected final String STD_UTF_CONVERT_ALL_TO_XML = "StdUTF: Convert all to XML";
    protected final String DELETE_ALL_XML = "Delete all XML-prefixed files";
    protected final String TEST_BABELFISH = "Test Babelfish";
    protected final String TESTBED_CANVAS = "Testbed canvas";
    protected final String TESTBED_FORM = "Testbed form";
    protected final String TESTBED_TEXTBOX = "Testbed textbox";
    protected final String COUNT_QUESTIONS = "Count questions";
    protected final String DISPLAY_PHONE_MODEL = "Display phone model";
    protected final String DISPLAY_MEMORY_STATS = "Memory stats";
    protected final String RUN_GARBAGE_COLLECTOR = "Run gc";
    protected final String SHOW_FILESYSTEM_ROOTS = "Show filesystem roots";
    protected final String IS_FILECONNECTION_API_AVAILABLE = "FileConnection API available?";
    protected final String TEST_RECORDSTORE_CAPACITY = "Test RecordStore capacity";
    protected final String CREATE_NEW_TEST = "Create new test";
    protected final String TRANSMIT_ALL_RS_TESTS = "Transmit all tests in RecordStore";

    protected Command m_BackCommand;

    /**sSelectedFilename is the file selected when this screen was invoked.*/
    public DevStuffScreen() throws Exception
    {
        super("Dev stuff",javax.microedition.lcdui.List.IMPLICIT);

        append("For selected file:",null);
        append(VIEW_SOURCE,null);
        append(COUNT_NEWLINES,null);
        append(CREATE_BACKUP,null);
        append(DELETE_TEST,null);
        append(COPY_TO_RECORDSTORE,null);
        append(CONVERT_TO_XML,null);
        append(PRINT_TO_FILE,null);

        append("----------------------",null);
        append(SAVE_ALL_TESTS_TO_FILESYSTEM,null);
        append(JAVA_UTF_CONVERT_ALL_TO_XML,null);
        append(STD_UTF_CONVERT_ALL_TO_XML,null);
        append(DELETE_ALL_XML,null);
        append(TEST_BABELFISH,null);
        append(TESTBED_CANVAS,null);
        append(TESTBED_FORM,null);
        append(TESTBED_TEXTBOX,null);
        append(COUNT_QUESTIONS,null);
        append(DISPLAY_PHONE_MODEL,null);
        append(DISPLAY_MEMORY_STATS,null);
        append(RUN_GARBAGE_COLLECTOR,null);
        append(SHOW_FILESYSTEM_ROOTS,null);
        append(IS_FILECONNECTION_API_AVAILABLE,null);
        append(TEST_RECORDSTORE_CAPACITY,null);
        append(CREATE_NEW_TEST,null);
        append(TRANSMIT_ALL_RS_TESTS,null);

        m_BackCommand = new Command("Back",Command.ITEM,0);
        addCommand(m_BackCommand);
        setCommandListener(this);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable s)
    {
        System.out.println("Entering commandAction(Command,Displayable)");

        try
        {
            if (c==m_BackCommand)
            {
                OccleveMobileMidlet.getInstance().displayFileChooser();
            }
            else if (c==javax.microedition.lcdui.List.SELECT_COMMAND)
            {
                onSelectCommand();
            }
            else
            {
                OccleveMobileMidlet.getInstance().onError("Unknown command in DevStuffScreen.commandAction");
            }
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void onSelectCommand() throws Exception
    {
        int iIndex = getSelectedIndex();
        String sSelectedPrompt = getString(iIndex);

        onSelectCommand_PerFileOptions(sSelectedPrompt);
        onSelectCommand_GlobalOptions(sSelectedPrompt);
    }

    protected void onSelectCommand_PerFileOptions(String sSelectedPrompt)
    throws Exception
    {
        if (sSelectedPrompt.equals(VIEW_SOURCE))
        {
            viewSourceOfSelectedFile();
        }
        else if (sSelectedPrompt.equals(COUNT_NEWLINES))
        {
            countNewlinesInSelectedFile();
        }
        else if (sSelectedPrompt.equals(CREATE_BACKUP))
        {
            createBackupOfSelectedFile();
        }
        else if (sSelectedPrompt.equals(DELETE_TEST))
        {
            deleteSelectedTest();
        }
        else if (sSelectedPrompt.equals(COPY_TO_RECORDSTORE))
        {
            VocabRecordStoreManager mgr = new VocabRecordStoreManager();
            mgr.copyFileToRecordStore(m_SelectedListOfTestsEntry.getFilename());
        }
        else if (sSelectedPrompt.equals(CONVERT_TO_XML))
        {
            convertSelectedFileToXML();
        }
        else if (sSelectedPrompt.equals(PRINT_TO_FILE))
        {
            m_sThreadAction = PRINT_TO_FILE;
            new Thread(this).start();
        }
    }

    protected void onSelectCommand_GlobalOptions(String sOption)
    throws Exception
    {
        if (sOption.equals(JAVA_UTF_CONVERT_ALL_TO_XML) ||
            sOption.equals(STD_UTF_CONVERT_ALL_TO_XML))
        {
            boolean bJavaUTF = (sOption.equals(JAVA_UTF_CONVERT_ALL_TO_XML));

            // Runs in a thread so it can display a progress indicator.
            XMLConverter converter = new XMLConverter(bJavaUTF);
            Thread convThread = new Thread(converter);
            convThread.start();
        }
        else if (sOption.equals(DELETE_ALL_XML))
        {
            VocabRecordStoreManager mgr = new VocabRecordStoreManager();
            mgr.deleteAllXmlPrefixedFiles();

            boolean bRefreshList = true;
            OccleveMobileMidlet.getInstance().displayFileChooser(bRefreshList);
        }
        else if (sOption.equals(TESTBED_CANVAS))
        {
            TestbedCanvas tbc = new TestbedCanvas(this);
            OccleveMobileMidlet.getInstance().setCurrentForm(tbc);
        }
        else if (sOption.equals(TESTBED_FORM))
        {
            TestbedForm tbf = new TestbedForm();
            OccleveMobileMidlet.getInstance().setCurrentForm(tbf);
        }
        else if (sOption.equals(TESTBED_TEXTBOX))
        {
            TestbedTextBox ttb = new TestbedTextBox();
            OccleveMobileMidlet.getInstance().setCurrentForm(ttb);
        }
        else if (sOption.equals(COUNT_QUESTIONS))
        {
            countQuestions();
        }
        else if (sOption.equals(DISPLAY_PHONE_MODEL))
        {
            displayPhoneModel();
        }
        else if (sOption.equals(DISPLAY_MEMORY_STATS))
        {
            displayMemoryStats();
        }
        else if (sOption.equals(RUN_GARBAGE_COLLECTOR))
        {
            System.gc();
        }
        else if (sOption.equals(SHOW_FILESYSTEM_ROOTS))
        {
            m_sThreadAction = SHOW_FILESYSTEM_ROOTS;
            new Thread(this).start();
        }
        else if (sOption.equals(IS_FILECONNECTION_API_AVAILABLE))
        {
            displayFileConnectionAPIAvailability();
        }
        else if (sOption.equals(TEST_RECORDSTORE_CAPACITY))
        {
            displayRecordStoreCapacity();
        }
        else if (sOption.equals(CREATE_NEW_TEST))
        {
            VocabRecordFilenameTextBox tb = new VocabRecordFilenameTextBox();
            OccleveMobileMidlet.getInstance().setCurrentForm(tb);
        }
        else if (sOption.equals(TRANSMIT_ALL_RS_TESTS))
        {
            IPAddressTextBox tb = new IPAddressTextBox();
            OccleveMobileMidlet.getInstance().setCurrentForm(tb);
        }
        else if (sOption.equals(TEST_BABELFISH))
        {
            BabelFishTranslationEngine eng = new BabelFishTranslationEngine();
            eng.translateEnglishToChinese("hello");
        }
        else if (sOption.equals(SAVE_ALL_TESTS_TO_FILESYSTEM))
        {
            m_sThreadAction = SAVE_ALL_TESTS_TO_FILESYSTEM;
            new Thread(this).start();
        }
    }

    protected void countNewlinesInSelectedFile() throws Exception
    {
        String sTestContents;
        if (m_SelectedListOfTestsEntry.getRecordStoreID()!=null)
        {
            int id = m_SelectedListOfTestsEntry.getRecordStoreID().intValue();
            VocabRecordStoreManager mgr = new VocabRecordStoreManager();
            sTestContents = mgr.getTestContents(m_SelectedListOfTestsEntry);
        }
        else
        {
            sTestContents = StaticHelpers.readUnicodeFile("/" + m_SelectedListOfTestsEntry.getFilename());
        }

        int lfCount = getSubstringCount(sTestContents,"\n");
        int crlfCount = getSubstringCount(sTestContents,"\r\n");

        String sMsg = "No of LFs = " + lfCount + Constants.NEWLINE +
                      "No of CRLFs = " + crlfCount;
        Alert alert = new Alert(null,sMsg, null, null);
        OccleveMobileMidlet.getInstance().displayAlert(alert,this);
    }

    protected int getSubstringCount(String sCountIn,String sSubstring)
    {
        int startIndex = 0;
        int iCount = 0;
        while (startIndex != -1)
        {
            startIndex = sCountIn.indexOf(sSubstring,startIndex);

            if (startIndex!=-1)
            {
                iCount++;
                startIndex += sSubstring.length();
                if (startIndex >= sCountIn.length()) startIndex=-1;
            }
        }

        return iCount;
    }

    protected void createBackupOfSelectedFile() throws Exception
    {
        VocabRecordStoreManager mgr = new VocabRecordStoreManager();
        String sTestContents;
        if (m_SelectedListOfTestsEntry.getRecordStoreID() != null)
        {
            int id = m_SelectedListOfTestsEntry.getRecordStoreID().intValue();
            sTestContents = mgr.getTestContents(m_SelectedListOfTestsEntry);
        }
        else
        {
            sTestContents = StaticHelpers.readUnicodeFile("/" + 
            		m_SelectedListOfTestsEntry.getFilename());
        }

        String sBackupFilename = "Backup " + System.currentTimeMillis() +
                                 " " + m_SelectedListOfTestsEntry.getFilename();
        mgr.createFileInRecordStore(sBackupFilename,
                sTestContents,true);
    }

    protected void countQuestions() throws Exception
    {
        ListOfTests list = new ListOfTests();
        int iTotalQuestions = 0;

        for (int i=0; i<list.getSize(); i++)
        {
        	ListOfTestsEntry entry = list.getEntry(i);
        	String sFilename = entry.getFilename();

            if (sFilename.endsWith(Constants.NEWLINE))
            {
                sFilename =
                    sFilename.substring(0, sFilename.length() - Constants.NEWLINE_LENGTH);
            }

            System.out.println("COUNTING: " + sFilename);
            Test theTest = new Test(entry);
            iTotalQuestions += theTest.getQACount();
        }

        Alert alert =
            new Alert(null, "Total number of questions = " + iTotalQuestions, null, null);
        OccleveMobileMidlet.getInstance().displayAlert(alert,this);
    }

    protected void displayPhoneModel() throws Exception
    {
        String sModel = System.getProperty("microedition.platform");
        Alert alert =
            new Alert(null, "microedition.platform = " + sModel, null, null);
        OccleveMobileMidlet.getInstance().displayAlert(alert,this);
    }

    protected void displayMemoryStats() throws Exception
    {
        Runtime rt = Runtime.getRuntime();
        String sMsg =
            "Free memory = " + rt.freeMemory() + Constants.NEWLINE +
            "Total memory used = " + rt.totalMemory();
        Alert alert = new Alert(null, sMsg, null, null);
        OccleveMobileMidlet.getInstance().displayAlert(alert,this);
    }

    // From http://developers.sun.com/techtopics/mobility/apis/articles/fileconnection/
    private void showFilesystemRoots() throws Exception
    {
        Manager.playTone(69, 1000, 100);
        System.out.println("About to call listRoots...");

        Enumeration drives = FileSystemRegistry.listRoots();

        System.out.println("Called listRoots...");
        Manager.playTone(90, 1000, 100);


        String sMsg = "The valid roots found are:" + Constants.NEWLINE;
        while (drives.hasMoreElements())
        {
            String root = (String) drives.nextElement();
            sMsg += root + Constants.NEWLINE;
        }

        Alert alert = new Alert(null, sMsg, null, null);
        OccleveMobileMidlet.getInstance().displayAlert(alert,this);
    }

    /**Since:
    "Warning: To avoid potential deadlock, operations that may block, such as
    networking, should be performed in a different thread than the
    commandAction() handler." */
    public void run()
    {
        try
        {
            if (m_sThreadAction.equals(PRINT_TO_FILE))
            {
                Test test = new Test(m_SelectedListOfTestsEntry);
                VocabViewerScreen viewer = new VocabViewerScreen("",test);
                viewer.printToFile();
            }
            else if (m_sThreadAction.equals(SHOW_FILESYSTEM_ROOTS))
            {
                showFilesystemRoots();
            }
            else if (m_sThreadAction.equals(SAVE_ALL_TESTS_TO_FILESYSTEM))
            {
                VocabRecordStoreManager mgr =
                        new VocabRecordStoreManager();
                mgr.saveAllTestsToFilesystem();
            }
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    public void displayFileConnectionAPIAvailability()
    {
        String sPropName = "microedition.io.file.FileConnection.version";
        displayPropertyValue(sPropName);
    }

    public void displayPropertyValue(String sPropName)
    {
        String sResult = System.getProperty(sPropName);
        String sMsg = sPropName + " = " + Constants.NEWLINE + sResult;

        Alert alert = new Alert(null,sMsg,null,null);
        OccleveMobileMidlet.getInstance().displayAlert(alert,this);
    }

    public void displayRecordStoreCapacity() throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore("testCapacity",true);
        int capacity = rs.getSizeAvailable();
        rs.closeRecordStore();

        Alert alert = new Alert(null,"RecordStore capacity = " + capacity,null,null);
        OccleveMobileMidlet.getInstance().displayAlert(alert,this);
    }

    protected void deleteSelectedTest() throws Exception
    {
        if (m_SelectedListOfTestsEntry.getRecordStoreID() != null)
        {
            DeleteTestConfirmationScreen conf =
                    new DeleteTestConfirmationScreen(
                           m_SelectedListOfTestsEntry.getRecordStoreID().intValue(),
                           m_SelectedListOfTestsEntry.getFilename(),
                           this);
            OccleveMobileMidlet.getInstance().setCurrentForm(conf);
        }
    }

    protected void viewSourceOfSelectedFile() throws Exception
    {
        String sTestContents;
        if (m_SelectedListOfTestsEntry.getRecordStoreID()!=null)
        {
            int id = m_SelectedListOfTestsEntry.getRecordStoreID().intValue();
            VocabRecordStoreManager mgr = new VocabRecordStoreManager();
            sTestContents = mgr.getTestContents(m_SelectedListOfTestsEntry);
        }
        else
        {
            sTestContents = StaticHelpers.readUnicodeFile("/" + m_SelectedListOfTestsEntry.getFilename());
        }

        Vector vContents = StaticHelpers.stringToVector(sTestContents);
        TestSourceViewer viewer = new TestSourceViewer(m_SelectedListOfTestsEntry.getFilename(),
                                                       vContents);
        OccleveMobileMidlet.getInstance().setCurrentForm(viewer);
    }

    protected void convertSelectedFileToXML() throws Exception
    {
        Test test = new Test(m_SelectedListOfTestsEntry);
        String sXML = test.toXML();

        // Add a newline since the convention is that all toXML() functions
        // don't add a trailing newline.
        sXML += Constants.NEWLINE;

        System.out.println(sXML);

        /*
        Vector vXML = StaticHelpers.stringToVector(test.toXML());
        String sTitle = m_SelectedListOfTestsEntry.getFilename() + " to XML";
        TestSourceViewer vwr = new TestSourceViewer(sTitle,vXML);
        OccleveMobileMidlet.getInstance().setCurrentForm(vwr);
        */

       String sNameOfXmlFile = "XML " + m_SelectedListOfTestsEntry.getFilename();
       VocabRecordStoreManager mgr = new VocabRecordStoreManager();
       mgr.createFileInRecordStore(sNameOfXmlFile,sXML,true);
    }
}

