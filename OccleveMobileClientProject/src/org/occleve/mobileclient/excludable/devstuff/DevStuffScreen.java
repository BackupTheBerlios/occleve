/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-11  Joe Gittings

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
@version 0.9.10
*/

package org.occleve.mobileclient.excludable.devstuff;

import java.io.*;
import java.util.*;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.*;
import com.sun.lwuit.list.*;

import javax.microedition.io.Connector;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.rms.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.components.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;
import org.occleve.mobileclient.util.*;
import org.occleve.mobileclient.excludable.translation.*;

/**Stuff for devs and power users.*/
public class DevStuffScreen extends Form
implements ActionListener,Excludable,Runnable
{
	protected OccleveList m_List = new OccleveList();
	protected Command m_SelectCommand = new Command("Select");

	/**Will the list of quizzes need rebuilding when the user quits this screen?*/
	protected boolean m_bQuizListNeedsRefreshing = false;

	/**Accessor.*/
	public void setQuizListNeedsRefreshing() {m_bQuizListNeedsRefreshing=true;}
	
	protected ListOfTestsEntry m_SelectedListOfTestsEntry;

    ////////////// Implementation of Excludable ////////////////////////////////
    public void setListOfTestsEntry(ListOfTestsEntry e) {m_SelectedListOfTestsEntry = e;}

    // Not relevant in this class:
    public void initialize() {}
    public void setQAIndex(Integer i) {}
    public void setScreenToReturnTo(Object screen) {}
    ////////////////////////////////////////////////////////////////////////////

    /**Should be one of the prompt values.*/
    protected String m_sThreadAction;

    protected final String SET_SAGE_SERVER = "Set Sage server address";
    protected final String COUNT_NEWLINES = "Count LFs and CRLFs";
    protected final String CREATE_BACKUP = "Create backup";
    protected final String DELETE_TEST = "Delete test";
    protected final String VIEW_SOURCE = "View source";
    protected final String COPY_TO_RECORDSTORE = "Copy to recordstore";
    protected final String PRINT_TO_FILE = "Print to file";

    protected final String QUIZ_FILE_MANAGER = "Quiz file manager";
    protected final String MEDIA_FILE_MANAGER = "Media file manager";
    protected final String SAVE_ALL_TESTS_TO_FILESYSTEM = "Save all tests in RecordStore to filesystem";
    ///protected final String JAVA_UTF_CONVERT_ALL_TO_XML = "JavaUTF: Convert all to XML";
    ///protected final String STD_UTF_CONVERT_ALL_TO_XML = "StdUTF: Convert all to XML";
    protected final String DELETE_ALL_XML = "Delete all XML-prefixed files";
    protected final String TEST_BABELFISH = "Test Babelfish";
    protected final String TESTBED_CANVAS = "Testbed canvas";
    protected final String TESTBED_FORM = "Testbed form";
    protected final String TESTBED_TEXTBOX = "Testbed textbox";
    protected final String TEST_LWUIT = "Test the LWUIT";
    protected final String COUNT_QUESTIONS = "Count questions";
    protected final String DISPLAY_PHONE_MODEL = "Display phone model";
    protected final String DISPLAY_MEMORY_STATS = "Memory stats";
    protected final String DISPLAY_THREAD_STATS = "Thread stats";
    protected final String RUN_GARBAGE_COLLECTOR = "Run gc";
    protected final String SHOW_FILESYSTEM_ROOTS = "Show filesystem roots";
    protected final String SHOW_FILES_AND_DIRS_UNDER_FILESYSTEM_ROOTS = "Show all files and dirs in filesystem roots";
    protected final String SHOW_WIKIPEDIA_STROKE_FILES = "Show first few anims in wikipedia_stroke on e drive";
    protected final String SHOW_ROOT_FILE_URLS = "Show root file URLs";
    protected final String IS_FILECONNECTION_API_AVAILABLE = "FileConnection API available?";
    protected final String TEST_RECORDSTORE_CAPACITY = "Test RecordStore capacity";
    protected final String CREATE_NEW_TEST = "Create new test";
    protected final String BUILD_EUCCN_UNICODE_MAP = "Build EUC-CN to Unicode map";
    protected final String COUNT_RS_RECORDS = "Count records in recordstores";
    protected final String LIST_RECORDSTORES = "List recordstores";
    protected final String SHOW_DEFAULT_ENCODING = "Show default encoding";
    protected final String ENCODING_TESTER = "Encoding tester";
    protected final String TRACE_ON = "Trace on";
    
    //protected final String MOVE_MEDIA_FILES_TO_MEDIA_RS = "Move media files to media RS";
    //protected final String FIX_FOULED_MEDIA_FILES = "Fix fouled media files";

    protected Command m_BackCommand;

    /**sSelectedFilename is the file selected when this screen was invoked.*/
    public DevStuffScreen() throws Exception
    {
        super("Dev stuff");
        setScrollable(false); // Otherwise the List won't scroll.

        setLayout(new BorderLayout());
        addComponent(BorderLayout.CENTER,m_List);
        m_List.addActionListener(this);
        addCommand(m_SelectCommand);

        append("For selected file:",null);
        append(VIEW_SOURCE,null);
        append(COUNT_NEWLINES,null);
        append(CREATE_BACKUP,null);
        append(DELETE_TEST,null);
        append(COPY_TO_RECORDSTORE,null);
        append(PRINT_TO_FILE,null);

        append("----------------------",null);
        append(SET_SAGE_SERVER,null);
        append(QUIZ_FILE_MANAGER,null);
        append(MEDIA_FILE_MANAGER,null);
        append(SAVE_ALL_TESTS_TO_FILESYSTEM,null);
        ////append(JAVA_UTF_CONVERT_ALL_TO_XML,null);
        ////append(STD_UTF_CONVERT_ALL_TO_XML,null);
        append(DELETE_ALL_XML,null);
        append(TEST_BABELFISH,null);
        append(TESTBED_CANVAS,null);
        append(TESTBED_FORM,null);
        append(TESTBED_TEXTBOX,null);
        append(TEST_LWUIT,null);
        append(COUNT_QUESTIONS,null);
        append(DISPLAY_PHONE_MODEL,null);
        append(DISPLAY_MEMORY_STATS,null);
        append(DISPLAY_THREAD_STATS,null);
        append(RUN_GARBAGE_COLLECTOR,null);
        append(SHOW_FILESYSTEM_ROOTS,null);
        append(SHOW_FILES_AND_DIRS_UNDER_FILESYSTEM_ROOTS,null);
        append(SHOW_WIKIPEDIA_STROKE_FILES,null);        
        append(SHOW_ROOT_FILE_URLS,null);
        append(IS_FILECONNECTION_API_AVAILABLE,null);
        append(TEST_RECORDSTORE_CAPACITY,null);
        append(CREATE_NEW_TEST,null);
        append(BUILD_EUCCN_UNICODE_MAP,null);
        append(COUNT_RS_RECORDS,null);
        append(LIST_RECORDSTORES,null);
        append(SHOW_DEFAULT_ENCODING,null);
        append(ENCODING_TESTER,null);
        append(TRACE_ON,null);

        //append(MOVE_MEDIA_FILES_TO_MEDIA_RS,null);
        //append(FIX_FOULED_MEDIA_FILES,null);
        
        m_BackCommand = new Command("Back");
        addCommand(m_BackCommand);
        setCommandListener(this);
    }

    protected void append(String item,Object ignored)
    {
    	m_List.addItem(item);
    }
    
    /**Implementation of ActionListener.*/
    public void actionPerformed(ActionEvent evt)
    {
    	
    }
    
    public void actionCommand(Command c)
    {
        try
        {
            if (c==m_BackCommand)
            {
                OccleveMobileMidlet.getInstance().displayFileChooser(m_bQuizListNeedsRefreshing);
            }
            else if (c==m_SelectCommand)
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
        String prompt = (String)m_List.getSelectedItem();
        onSelectCommand_PerFileOptions(prompt);
        onSelectCommand_GlobalOptions(prompt);
    }

    protected void onSelectCommand_PerFileOptions(String sSelectedPrompt)
    throws Exception
    {
        if (sSelectedPrompt.equals(VIEW_SOURCE))
        {
            viewSourceOfSelectedQuiz();
        }
        else if (sSelectedPrompt.equals(COUNT_NEWLINES))
        {
            countNewlinesInSelectedQuiz();
        }
        else if (sSelectedPrompt.equals(CREATE_BACKUP))
        {
            createBackupOfSelectedQuiz();
        }
        else if (sSelectedPrompt.equals(DELETE_TEST))
		{
		    deleteSelectedTest();
		    m_bQuizListNeedsRefreshing = true;
		}
        else if (sSelectedPrompt.equals(COPY_TO_RECORDSTORE))
        {
        	VocabRecordStoreManager mgr =
        		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();
            mgr.copyFileFromJarToRecordStore(m_SelectedListOfTestsEntry.getFilename());
		    m_bQuizListNeedsRefreshing = true;
        }
        else if (sSelectedPrompt.equals(PRINT_TO_FILE))
        {
            m_sThreadAction = PRINT_TO_FILE;
            new Thread(this).start();
        }
    }

    public class SageServerTextBox extends Form
    {
    	protected Container m_Parent;
    	protected TextArea m_TextArea = new TextField(SageQA.SAGE_SERVER);

        public SageServerTextBox(Container parent) throws Exception
        {
            super("Sage server");
            m_Parent = parent;
            addCommand(new Command("OK"));
            setLayout(new BorderLayout());
            addComponent(BorderLayout.CENTER,m_TextArea);
            m_TextArea.setEditable(true);
            m_TextArea.setEnabled(true);
            m_TextArea.requestFocus();
            m_TextArea.setMaxSize(100);
            this.setEnabled(true);
        }

        public void actionCommand(Command c)
        {
        	SageQA.SAGE_SERVER = m_TextArea.getText();
            OccleveMobileMidlet.getInstance().setCurrentForm(m_Parent);
        }
    }
    
    protected void onSelectCommand_GlobalOptions(String sOption)
    throws Exception
    {        
        if (sOption.equals(SET_SAGE_SERVER))
        {
        	SageServerTextBox tb = new SageServerTextBox(this);
            OccleveMobileMidlet.getInstance().setCurrentForm(tb);        	
        }
        else if (sOption.equals(QUIZ_FILE_MANAGER))
        {
        	VocabRecordStoreManager quizRsMgr =
        		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();
        	FileManager fmgr = new FileManager(quizRsMgr,this);
            OccleveMobileMidlet.getInstance().setCurrentForm(fmgr);        	
        }
        else if (sOption.equals(MEDIA_FILE_MANAGER))
        {
        	VocabRecordStoreManager mediaRsMgr =
        		OccleveMobileMidlet.getInstance().getMediaRecordStoreManager();
        	FileManager fmgr = new FileManager(mediaRsMgr,this);
            OccleveMobileMidlet.getInstance().setCurrentForm(fmgr);        	
        }
        else if (sOption.equals(DELETE_ALL_XML))
        {
        	VocabRecordStoreManager mgr =
        		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();
            mgr.deleteAllXmlPrefixedFiles();

		    m_bQuizListNeedsRefreshing = true;
        }
        else if (sOption.equals(TESTBED_CANVAS))
        {
            TestbedCanvas tbc = new TestbedCanvas(this);
            OccleveMobileMidlet.getInstance().setCurrentForm(tbc);
        }
        else if (sOption.equals(TESTBED_FORM))
        {
            TestbedForm tbf = new TestbedForm(this);
            OccleveMobileMidlet.getInstance().setCurrentForm(tbf);
        }
        else if (sOption.equals(TESTBED_TEXTBOX))
        {
            TestbedTextBox ttb = new TestbedTextBox(this);
            OccleveMobileMidlet.getInstance().setCurrentForm(ttb);
        }
        else if (sOption.equals(TEST_LWUIT))
        {
            TestLWUIT tl = new TestLWUIT();
            tl.display();
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
        else if (sOption.equals(DISPLAY_THREAD_STATS))
        {
            displayThreadStats();
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
        else if (sOption.equals(SHOW_WIKIPEDIA_STROKE_FILES))
        {
            m_sThreadAction = SHOW_WIKIPEDIA_STROKE_FILES;
            new Thread(this).start();
        }
        else if (sOption.equals(SHOW_FILES_AND_DIRS_UNDER_FILESYSTEM_ROOTS))
        {
            m_sThreadAction = SHOW_FILES_AND_DIRS_UNDER_FILESYSTEM_ROOTS;
            new Thread(this).start();
        }
        else if (sOption.equals(SHOW_ROOT_FILE_URLS))
        {
            m_sThreadAction = SHOW_ROOT_FILE_URLS;
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
            VocabRecordFilenameTextBox tb = new VocabRecordFilenameTextBox(this);
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
        else if (sOption.equals(BUILD_EUCCN_UNICODE_MAP))
        {
            m_sThreadAction = BUILD_EUCCN_UNICODE_MAP;
            new Thread(this).start();
        }
        else if (sOption.equals(COUNT_RS_RECORDS))
        {
        	countRecordsInRecordStores();
        }
        else if (sOption.equals(LIST_RECORDSTORES))
        {
        	RecordStoreExplorer rse = new RecordStoreExplorer(this);
            OccleveMobileMidlet.getInstance().setCurrentForm(rse);        	
        }
        else if (sOption.equals(SHOW_DEFAULT_ENCODING))
        {
        	displayPropertyValue("microedition.encoding");
        }
        else if (sOption.equals(ENCODING_TESTER))
        {
            EncodingTester et = new EncodingTester(this);
            OccleveMobileMidlet.getInstance().setCurrentForm(et);
        }
        else if (sOption.equals(TRACE_ON))
        {
        	OccleveTrace.setTraceOn();
        	dssAlert("Tracing enabled");
        }
        
        /*
        else if (sOption.equals(MOVE_MEDIA_FILES_TO_MEDIA_RS))
        {
        	moveMediaFilesFromQuizRsToMediaRs();
        }
        else if (sOption.equals(FIX_FOULED_MEDIA_FILES))
        {
        	fixFouledMediaFiles();
        }
        */
    }

    /**0.9.7 - migration function written primarily for my own use.
    Moves any media files that are in the quiz recordstore to the new media recordstore.*/
	protected void moveMediaFilesFromQuizRsToMediaRs() throws Exception
	{
    	VocabRecordStoreManager quizRsMgr =
    		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();

    	VocabRecordStoreManager mediaRsMgr =
    		OccleveMobileMidlet.getInstance().getMediaRecordStoreManager();

        Hashtable recordIndicesKeyedByFilenames =
        	quizRsMgr.getRecordIndicesKeyedByFilenames();
        Enumeration filenames = recordIndicesKeyedByFilenames.keys();

        //Alert progress = new Alert(null,"Moving...",null,AlertType.INFO);
        //OccleveMobileMidlet.getInstance().displayAlert(progress,this);

        int iFilesMoved = 0;

        while (filenames.hasMoreElements())
        {
        	String sFilename = (String)filenames.nextElement();
        	String sFilenameLower = sFilename.toLowerCase();
        	
            if ((sFilenameLower.endsWith(".gif")) || (sFilenameLower.endsWith(".mp3")))
            {
            	Integer iOriginalRSID =
            		(Integer)recordIndicesKeyedByFilenames.get(sFilename);
            	byte[] recordData = quizRsMgr.getRecordBytes(iOriginalRSID.intValue());
            	
            	// Sanity check: is it the correct filename?
            	String sFilenameInData =
            		VocabRecordStoreManager.getFilenameFromRecordData(recordData);
            	
            	if (sFilenameInData.equals(sFilename)==false)
            	{
            		String sErr =
            			"Filenames don't match: " + sFilename + " vs " + sFilenameInData;
            		new Dialog(sErr).show();
                    return;
            	}
            	
            	iFilesMoved++;
            	String sMsg =
            		"Moving " + sFilename + "... " + iFilesMoved + " files moved. ";
            	// progress.setString(sMsg);
            	
            	// Copy the media file to the media recordstore.
            	byte[] recordDataMinusFilename =
            		quizRsMgr.getRecordContentsMinusFilename(iOriginalRSID.intValue());
            	mediaRsMgr.createFileInRecordStore(sFilename,recordDataMinusFilename,false);
            	
            	// Now delete the media file from the quiz recordstore.
            	quizRsMgr.deleteTest(iOriginalRSID.intValue(),sFilename);            	
            }
        }	
        
        // progress.setString("Finished moving media files - moved " + iFilesMoved + " files");
	}

	/**0.9.7: Fix a foul-up caused by a previous bug in moveMediaFilesFromQuizRsToMediaRs()*/
	protected void fixFouledMediaFiles() throws Exception
	{
    	VocabRecordStoreManager mediaRsMgr =
    		OccleveMobileMidlet.getInstance().getMediaRecordStoreManager();

        Hashtable recordIndicesKeyedByFilenames =
        	mediaRsMgr.getRecordIndicesKeyedByFilenames();
        Enumeration indicesEnum = recordIndicesKeyedByFilenames.elements();

        //Alert progress = new Alert(null,"Fixing...",null,AlertType.INFO);
        //OccleveMobileMidlet.getInstance().displayAlert(progress,this);

        int iNumberFixed = 0;
        while (indicesEnum.hasMoreElements())
        {
        	Integer rsIndex = (Integer)indicesEnum.nextElement();
        	byte[] dataMinusFilename =
        		mediaRsMgr.getRecordContentsMinusFilename(rsIndex.intValue());
        	mediaRsMgr.setRawRecordBytes(rsIndex.intValue(),dataMinusFilename);
        	
        	// progress.setString("Fixed file with index " + rsIndex.intValue());
        	iNumberFixed++;
        }		
        
        // progress.setString("Finished fixing... fixed " + iNumberFixed + " files");
	}

    protected void countRecordsInRecordStores() throws Exception
    {
    	VocabRecordStoreManager quizMgr =
    		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();
    	int iQuizRecordCount = quizMgr.getRecordCount();

    	VocabRecordStoreManager mediaMgr =
    		OccleveMobileMidlet.getInstance().getMediaRecordStoreManager();
    	int iMediaRecordCount = mediaMgr.getRecordCount();

    	String sMsg =
    		"Quiz recordstore contains " + iQuizRecordCount + " records. " +
    		"Media recordstore contains " + iMediaRecordCount + " records.";
		dssAlert(sMsg);
    }
    
    protected void countNewlinesInSelectedQuiz() throws Exception
    {
        String sTestContents;
        if (m_SelectedListOfTestsEntry.getRecordStoreID()!=null)
        {
            int id = m_SelectedListOfTestsEntry.getRecordStoreID().intValue();
        	VocabRecordStoreManager mgr =
        		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();
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
    	new Dialog(sMsg).show();
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

    protected void createBackupOfSelectedQuiz() throws Exception
    {
    	VocabRecordStoreManager mgr =
    		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();

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

        String msg = "Total number of questions = " + iTotalQuestions;
    	new Dialog(msg).show();
    }

    protected void displayPhoneModel() throws Exception
    {
        String sModel = System.getProperty("microedition.platform");
        String msg = "microedition.platform = " + sModel;
		dssAlert(msg);
    }

    protected void displayMemoryStats() throws Exception
    {
        Runtime rt = Runtime.getRuntime();
        String msg =
            "Free memory = " + rt.freeMemory() + Constants.NEWLINE +
            "Total memory used = " + rt.totalMemory();
		new Dialog(msg).show();
    }

    protected void displayThreadStats() throws Exception
    {
        String sMsg =
            "Active thread count = " + Thread.activeCount();
		new Dialog(sMsg).show();
    }

    // From http://developers.sun.com/techtopics/mobility/apis/articles/fileconnection/
    private void showFilesystemRoots() throws Exception
    {
        Enumeration drives = FileSystemRegistry.listRoots();

        String sMsg = "The valid roots found are:" + Constants.NEWLINE;
        while (drives.hasMoreElements())
        {
            String root = (String) drives.nextElement();
            sMsg += root + Constants.NEWLINE;
        }
		new Dialog(sMsg).show();
    }

    /**From http://developers.sun.com/techtopics/mobility/apis/articles/fileconnection/*/
    private void showRootFilesAndDirs() throws Exception
    {
        String sMsg = "All files and directories under roots:" + Constants.NEWLINE;

    	Enumeration drives = FileSystemRegistry.listRoots();
		while (drives.hasMoreElements())
		{
			String root = (String) drives.nextElement();
			
			FileConnection fc = (FileConnection)
			Connector.open("file:///" + root);
			
			// Include hidden files.
			System.out.println("List of files and directories under " + root);
			Enumeration filelist = fc.list("*", true);
			while(filelist.hasMoreElements())
			{
			    String fileName = (String) filelist.nextElement();
			    System.out.println(fileName);
			    sMsg += fileName + Constants.NEWLINE;
			}   
			fc.close();
		}
		new Dialog(sMsg).show();
    }

    private void showWikipediaStrokeFiles() throws Exception
    {
		final String fileURL = "file:///e:/wikipedia_stroke";
        String sMsg = "Contents of " + fileURL + Constants.NEWLINE;
		FileConnection fc = (FileConnection)Connector.open(fileURL);
		
		// Include hidden files.
		System.out.println("List of files and directories under " + fileURL);
		Enumeration filelist = fc.list("*", true);
		int iCount = 0;
		while(filelist.hasMoreElements() && ((iCount++)<3)   )
		{
		    String fileName = (String) filelist.nextElement();
		    System.out.println(fileName);
		    sMsg += fileName + Constants.NEWLINE;
		    
		    try {
		    	String fullURL = fileURL + "/" + fileName;
	        	sMsg += fullURL + Constants.NEWLINE;
	        	InputStream is =
	        		FileConnectionHelpers.openFileInputStream(fullURL);        	
	            DataInputStream dis = new DataInputStream(is);
	        	int b = dis.read();
	        	dis.close();
	        	is.close();
	        	sMsg += "Opened ok" + Constants.NEWLINE;
		    }
		    catch (Exception e) {
		    	sMsg += "Opening: " + e.toString() + Constants.NEWLINE;
		    }
		}   
		fc.close();

		new Dialog(sMsg).show();
    }

    private void showRootFileURLs() throws Exception
    {
    	Hashtable filenamesToURLs =
    		FileConnectionHelpers.getAllFilenamesInRootDirs("*");
    	Enumeration enm = filenamesToURLs.elements();
    	String sMsg = "";
    	while (enm.hasMoreElements())
    	{
    		String url = (String)enm.nextElement();
		    sMsg += url + Constants.NEWLINE;
    	}
		new Dialog(sMsg).show();
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
            else if (m_sThreadAction.equals(SHOW_FILES_AND_DIRS_UNDER_FILESYSTEM_ROOTS))
            {
                showRootFilesAndDirs();
            }
            else if (m_sThreadAction.equals(SHOW_WIKIPEDIA_STROKE_FILES))
            {
                showWikipediaStrokeFiles();
            }
            else if (m_sThreadAction.equals(SHOW_ROOT_FILE_URLS))
            {
                showRootFileURLs();
            }
            else if (m_sThreadAction.equals(SAVE_ALL_TESTS_TO_FILESYSTEM))
            {
            	VocabRecordStoreManager mgr =
            		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();                
                mgr.saveAllTestsToFilesystem();
            }
            else if (m_sThreadAction.equals(BUILD_EUCCN_UNICODE_MAP))
            {
            	BuildGB2312ToUnicodeArray.buildArray();
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
        new Dialog(sMsg).show();
    }

    public void displayRecordStoreCapacity() throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore("testCapacity",true);
        int capacity = rs.getSizeAvailable();
        rs.closeRecordStore();
        new Dialog("RecordStore capacity = " + capacity).show();
    }

    protected void deleteSelectedTest() throws Exception
    {
        if (m_SelectedListOfTestsEntry.getRecordStoreID() != null)
        {
        	boolean delete = Dialog.show("Delete quiz?",
        			"Delete " + m_SelectedListOfTestsEntry.getFilename() + "?",
                    Dialog.TYPE_CONFIRMATION,
                    null,"Yes","No");
        	
        	if (delete)
        	{
        		deleteTest(m_SelectedListOfTestsEntry.getRecordStoreID(),
    				m_SelectedListOfTestsEntry.getFilename());
        	}
        	
            //DeleteTestConfirmationScreen conf =
            //        new DeleteTestConfirmationScreen(
            //               m_SelectedListOfTestsEntry.getRecordStoreID().intValue(),
            //               m_SelectedListOfTestsEntry.getFilename(),
            //               this);
            // OccleveMobileMidlet.getInstance().setCurrentForm(conf);
        }
    }

    protected void deleteTest(Integer rsid,String filename)
    {
        try
        {
        	VocabRecordStoreManager mgr =
        		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();        	
            mgr.deleteTest(rsid.intValue(),filename);
            OccleveMobileMidlet.getInstance().repopulateFileChooser();

            // Display confirmation
            String msg = "Quiz " + filename +
                          " deleted from recordstore";
            new Dialog(msg).show();
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }
    protected void viewSourceOfSelectedQuiz() throws Exception
    {
        String sTestContents;
        if (m_SelectedListOfTestsEntry.getRecordStoreID()!=null)
        {
        	VocabRecordStoreManager mgr =
        		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();

        	int id = m_SelectedListOfTestsEntry.getRecordStoreID().intValue();
            sTestContents = mgr.getTestContents(m_SelectedListOfTestsEntry);
        }
        else
        {
            sTestContents = StaticHelpers.readUnicodeFile("/" + m_SelectedListOfTestsEntry.getFilename());
        }

        Vector vContents = StaticHelpers.stringToVector(sTestContents);
        TestSourceViewer viewer =
        	new TestSourceViewer(m_SelectedListOfTestsEntry.getFilename(),
                                   vContents,this);
        OccleveMobileMidlet.getInstance().setCurrentForm(viewer);
    }

    /*
    ==========0.9.7 - disabled - defunct function=============
    protected void convertSelectedQuizToXML() throws Exception
    {
        Test test = new Test(m_SelectedListOfTestsEntry);
        String sXML = test.toXML();

        // Add a newline since the convention is that all toXML() functions
        // don't add a trailing newline.
        sXML += Constants.NEWLINE;
        System.out.println(sXML);

		VocabRecordStoreManager mgr =
			OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();
		
		String sNameOfXmlFile = "XML " + m_SelectedListOfTestsEntry.getFilename();
		mgr.createFileInRecordStore(sNameOfXmlFile,sXML,true);
    }
    */
    
    protected void dssAlert(String msg)
    {
    	Dialog.show(Constants.PRODUCT_NAME,msg,"OK","");
    }
}

