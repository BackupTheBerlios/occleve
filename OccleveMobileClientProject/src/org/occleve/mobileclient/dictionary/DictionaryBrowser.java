/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2008  Joe Gittings

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

package org.occleve.mobileclient.dictionary;

import com.sun.lwuit.*;
import com.sun.lwuit.plaf.*;
import com.sun.lwuit.util.*;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
//////import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;

import bm.db.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.util.*;

/**0.9.7 - a browser for the CC-CEDICT dictionary.
Now using LWUIT rather than MIDP for the UI.*/
public class DictionaryBrowser extends Form
implements J2MEFileSelectorListener,Runnable
////CommandListener,ItemCommandListener,  MIDP STUFF
{
	private static final String DICTIONARY_DB_NAME = "dictionaryDb";
	private static final String DICTIONARY_TABLE_NAME = "CEDICT";
	private static final String INDEX_NAME = "allFieldsIndex";
	
	private static final String TRADITIONAL_FIELD_NAME = "traditional";
	private static final String SIMPLIFIED_FIELD_NAME = "simplified";
	private static final String PINYIN_FIELD_NAME = "pinyin";
	private static final String ENGLISH1_FIELD_NAME = "english1";
	private static final String ENGLISH2_FIELD_NAME = "english2";

	////////protected Database m_Database;

	protected String m_sExceptionContext;
	
	/**The full URL of the CEDICT file, including the "file:///" prefix.*/
	protected String m_sCedictFileURL;
	
	protected Command m_SearchCommand;
    protected Command m_QuizModeCommand;
    protected Command m_DeleteDatabaseCommand;
    protected Command m_CreateDatabaseCommand;
    protected CommonCommands m_CommonCommands;

    protected TextField m_SearchForTextField;

    protected static final String SEARCH_BUTTON_TEXT = "Search";
    protected Button m_SearchButton;

    protected TextArea m_SearchResultsTextArea;
    
    public DictionaryBrowser() throws Exception
    {
        super("Dictionary");

        m_SearchCommand = new Command("Search",0);
        addCommand(m_SearchCommand);

        m_QuizModeCommand = new Command("Quiz mode",1);
        addCommand(m_QuizModeCommand);

        m_DeleteDatabaseCommand = new Command("Delete DB",2);
        addCommand(m_DeleteDatabaseCommand);

        m_CreateDatabaseCommand = new Command("Create DB",3);
        addCommand(m_CreateDatabaseCommand);

        /////m_CommonCommands = new CommonCommands();
        /////m_CommonCommands.addToDisplayable(this);
        
        ///////setCommandListener(this);

        // Append items to this form.

        Label prompt = new Label("Search for:");
        addComponent(prompt);
        
        m_SearchForTextField = new TextField();
        addComponent(m_SearchForTextField);

        m_SearchButton = new Button(m_SearchCommand);
        addComponent(m_SearchButton);
        
        ////m_SearchButton.setItemCommandListener(this);
        ///Command temp = new Command("temp",Command.OK,1);
        ////m_SearchButton.setDefaultCommand(temp);

        m_SearchResultsTextArea = new TextArea();
        m_SearchResultsTextArea.setConstraint(TextArea.UNEDITABLE);
        m_SearchResultsTextArea.setMaxSize(500);
        addComponent(m_SearchResultsTextArea);

        ///// OccleveMobileMidlet.getInstance().setCurrentForm(this);        

		Display.init(OccleveMobileMidlet.getInstance());
		show();

        try
        {
        	  Resources r = Resources.open("/javaTheme.res");
        	  UIManager.getInstance().setThemeProps(r.getTheme("javaTheme"));
              Display.getInstance().getCurrent().refreshTheme();
    	}
        catch (Exception e)
        {
        	System.out.println("Couldn't load theme.");
        	OccleveMobileMidlet.getInstance().onError(e);
    	}
                        
        /*
		Database db = null;
		db = Database.connect(DICTIONARY_DB_NAME);
        Table tbl = db.getTable(DICTIONARY_TABLE_NAME);            
        if (tbl!=null)
        {
			System.out.println("Successfully connected to database " + DICTIONARY_DB_NAME);
		}
        else
		{        
			System.out.println("Couldn't connect to database " + DICTIONARY_DB_NAME);
			System.out.println("Starting background thread to create and populate it");
			new Thread(this).start();
		}
		*/
    }

    /**Note that this code needs to be in a separate thread because it accesses the
    phone's filesystem, *not* because it uses the OpenBaseMovil database.*/
    public void run()
    {
    	try
    	{
    		createAndPopulateDictionaryDatabase();
            ///OccleveMobileMidlet.getInstance().setCurrentForm(this);
    		show();
    	}
    	catch (Exception e)
    	{
    		System.err.println(e);
    		
    		String sMsg = "While: " + m_sExceptionContext + "..." + e.toString();    			
    		OccleveMobileMidlet.getInstance().onError(sMsg);
    	}
    }
    
    /**The database doesn't exist yet. Create it, and then import the CEDICT data
    into it from the CEDICT flat file which should be somewhere on the phone's
    filesystem.*/
    private void createAndPopulateDictionaryDatabase() throws Exception
    {
        LWUITAlert progress = new LWUITAlert(null,"Creating database...");
        progress.show();

        Table tbl = createDatabaseAndTable(progress);
    	
    	progress.setString("Importing dictionary....");
    	progress.show();

    	importDictionary(tbl,progress);
    }

    private Table createDatabaseAndTable(LWUITAlert progress) throws Exception
    {
    	Database db = Database.create(DICTIONARY_DB_NAME);
    	Table tbl = new Table(DICTIONARY_TABLE_NAME);
    	
    	// According to Row.serializeField(), which in turns calls
    	// SerializerOutputStream.writeString(), the string
    	// field lengths are only used to determine the max length of data to
    	// be written. So we can choose generous field sizes here.
    	tbl.addColumn(TRADITIONAL_FIELD_NAME,bm.db.Constants.FT_STRING,50);
    	tbl.addColumn(SIMPLIFIED_FIELD_NAME, bm.db.Constants.FT_STRING,50);
    	tbl.addColumn(PINYIN_FIELD_NAME, bm.db.Constants.FT_STRING,255);
    	tbl.addColumn(ENGLISH1_FIELD_NAME, bm.db.Constants.FT_STRING,255);
    	tbl.addColumn(ENGLISH2_FIELD_NAME, bm.db.Constants.FT_STRING,255);

    	// Create an index across all the columns.
    	String[] cols = {TRADITIONAL_FIELD_NAME,SIMPLIFIED_FIELD_NAME,PINYIN_FIELD_NAME,
    						ENGLISH1_FIELD_NAME,ENGLISH2_FIELD_NAME};
    	tbl.createFullTextIndex(INDEX_NAME,cols,false);
    	
        progress.setString("Creating table...");
    	db.createTable(tbl);    	
    	
    	return tbl;
    }

    /**Imports the dictionary from the CEDICT flat file on the local filesystem,
    to the OpenBaseMovil database.*/
    private void importDictionary(Table tbl,LWUITAlert progress) throws Exception
    {
    	System.out.println("Opening " + m_sCedictFileURL);
    	
        ////////String sFilename = "file:///root1/cedict_ts.u8";

    	// For now, this should be the UNCOMPRESSED file.        
        FileConnection fc = (FileConnection)Connector.open(m_sCedictFileURL);
        if(!fc.exists())
        {
        	throw new IOException("The dictionary file does not exist");
        }
        System.out.println("Opened file ok...");
        
        InputStream is = fc.openInputStream();        	
        InputStreamReader isr = new InputStreamReader(is,"UTF-8");
        
        String oneLine = "";
        int iMaxLength = 0;

        ////OccleveMobileMidlet.getInstance().displayAlert(progress,this);
        progress.show();

        // while (oneLine!=StaticHelpers.END_OF_STREAM_REACHED)
        //while (isr.ready())
        for (int i=0; i<65000; i++)
        {
        	m_sExceptionContext = "Reading line from dictionary file";
        	oneLine = StaticHelpers.readFromISR(isr,true);
        	
        	if (oneLine.length() > iMaxLength) iMaxLength = oneLine.length();
        	
////        	boolean bTrace = (i%50 == 0);
        	boolean bTrace = true;
        	
        	if (i!=45)
        	{
        		processCedictLine(oneLine,tbl,bTrace,i);
        	}
        	
        	if (bTrace)
        	{
        		String sMsg =
        			"Processed " + i + " lines" +
        			org.occleve.mobileclient.Constants.NEWLINE +
					"Free memory in bytes = " +
					Runtime.getRuntime().freeMemory();
        		progress.setString(sMsg);
        	}
        }
        
        System.out.println("Max line length = " + iMaxLength);    	
    }
    
    /**Some sample CEDICT lines:
    With one English meaning:
    一共 一共 [yi1 gong4] /altogether/
	With two English meanings:
    三級跳 三级跳 [san1 ji2 tiao4] /triple jump (athletics)/hop, skip and jump/
    */
    private void processCedictLine(String sLine,Table tbl,boolean bTrace,int iLineNo)
    throws Exception
    {
    	// If it's a comment line, drop out.
    	if (sLine.startsWith("#")) return;

    	m_sExceptionContext = "Parsing dictionary line";
    	
    	int iDivider1 = sLine.indexOf(' ');
    	int iDivider2 = sLine.indexOf(' ',iDivider1 + 1);
    	int iDivider3 = sLine.indexOf('[',iDivider2 + 1);
    	int iDivider4 = sLine.indexOf(']',iDivider3 + 1);
    	int iDivider5 = sLine.indexOf('/',iDivider4 + 1);
       	int iDivider6 = sLine.indexOf('/',iDivider5 + 1);
       	int iDivider7 = sLine.indexOf('/',iDivider6 + 1);
           	
    	String sTraditional = sLine.substring(0,iDivider1);
    	String sSimplified = sLine.substring(iDivider1 + 1,iDivider2);
    	String sPinyin = sLine.substring(iDivider3 + 1, iDivider4);
    	String sEnglish1 = sLine.substring(iDivider5 + 1, iDivider6);

    	String sEnglish2;
    	if (iDivider7!=-1)
    		sEnglish2 = sLine.substring(iDivider6 + 1, iDivider7);
    	else
    		sEnglish2 = null;

    	m_sExceptionContext = "Calling Table.createRow()";
    	Row rw = tbl.createRow();

    	m_sExceptionContext = "Calling Row.setField()";
    	rw.setField(TRADITIONAL_FIELD_NAME,sTraditional);
    	rw.setField(SIMPLIFIED_FIELD_NAME,sSimplified);
    	rw.setField(PINYIN_FIELD_NAME,sPinyin);
    	rw.setField(ENGLISH1_FIELD_NAME,sEnglish1);
    	rw.setField(ENGLISH2_FIELD_NAME,sEnglish2);

    	m_sExceptionContext = "Calling Row.save()";
    	rw.save();

    	if (bTrace)
    	{
        	m_sExceptionContext = "Tracing out dictionary line";
        	System.out.println(iLineNo + ": " + sLine);
    	}

    	m_sExceptionContext = "";
    }
    
    /**Implementation of CommandListener.*/
    //// MIDP STUFF ///// public void commandAction(Command c, Displayable s)
    
    protected void actionCommand(Command c)
    {
    	if (c==m_SearchCommand)
        {
            try
            {
            	searchDatabase();
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
    	else if (c==m_QuizModeCommand)
        {
            try
            {
            	OccleveMobileMidlet.getInstance().displayFileChooser(false);
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else if (c==m_DeleteDatabaseCommand)
        {
            try
            {
            	deleteDatabase();
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else if (c==m_CreateDatabaseCommand)
        {
            try
            {
            	J2MEFileSelector fs = new J2MEFileSelector("Choose CEDICT file",null);        
            	fs.setJ2MEFileSelectorListener(this);
            	OccleveMobileMidlet.getInstance().setCurrentForm(fs);        
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command in DictionaryBrowser.commandAction");
        }
    }

    private void deleteDatabase() throws Exception
    {
    	//// MIDP STUFF
        /////Alert progress = new Alert(null,"Deleting database...",null,null);
        ////progress.setTimeout(Alert.FOREVER);
        ////OccleveMobileMidlet.getInstance().displayAlert(progress,this);

    	LWUITAlert progress = new LWUITAlert("","Deleting database...");
    	progress.show();
    	
        //// This approach didn't work....
    	////Database db = Database.connect(DICTIONARY_DB_NAME);
		///db.start();
		////db.drop();            	
		    	
    	String[] rsNames = RecordStore.listRecordStores();
        
        if (rsNames==null)
        {
            OccleveMobileMidlet.getInstance().onError("No recordstores");
        }
        else
        {
	        for (int i=0; i<rsNames.length; i++)
	        {
	        	boolean bIsQuizData =
	        		(rsNames[i].equals(VocabRecordStoreManager.MEDIA_RECORDSTORE_NAME)) ||
	        		(rsNames[i].equals(VocabRecordStoreManager.QUIZ_RECORDSTORE_NAME));
	        	if (bIsQuizData==false) RecordStore.deleteRecordStore(rsNames[i]);
	        }
        }

		progress.setString("Database deleted");
    }
    
    /*Implementation of ItemCommandListener.*/
    /*
    public void commandAction(Command c, Item item)
    {
        try
        {
        	System.out.println("Entering commandAction...");
         	
            if (item==m_SearchButton)
            {
            	searchDatabase();
            }
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }
    */

    /**Called when the user clicks Search.*/
    private void searchDatabase() throws Exception
    {
    	System.out.println("Search button clicked...");
        m_SearchButton.setText("Searching...");

		Database db = Database.connect(DICTIONARY_DB_NAME);
    	System.out.println("db = " + db);
    	if (db==null)
    	{
    		m_SearchResultsTextArea.setText("No database connection");
            m_SearchButton.setText(SEARCH_BUTTON_TEXT);
    		return;
    	}
    	else
    	{
    		// Need to call start() before using an existing database.
    		db.start();
    	}
        
        Table tbl = db.getTable(DICTIONARY_TABLE_NAME);
        System.out.println("tbl = " + tbl);
    	if (tbl==null)
    	{
    		m_SearchResultsTextArea.setText("Table not found in database");
            m_SearchButton.setText(SEARCH_BUTTON_TEXT);
    		return;
    	}

    	// See the javadoc for Table.findFuzzy() for the spec for this expression.
    	String sFieldExpression =
    		TRADITIONAL_FIELD_NAME + "+" +
    		SIMPLIFIED_FIELD_NAME + "+" +
    		PINYIN_FIELD_NAME + "+" +
    		ENGLISH1_FIELD_NAME + "+" +
    		ENGLISH2_FIELD_NAME;
    		    	
        RowSet rs = tbl.findFuzzy(sFieldExpression,m_SearchForTextField.getText());
        System.out.println("rs = " + rs);
        
        System.out.println("Rowset contains " + rs.size() + " rows");
        
        StringBuffer sbResults = new StringBuffer();

        if (rs.size()==0)
        {
        	sbResults.append("No matches");
        }
        else if (rs.size()>5)
        {
        	sbResults.append(rs.size() + " results... showing first 5" +
        						org.occleve.mobileclient.Constants.NEWLINE);
        }

        int iCount = 0;
        while (rs.next() && (iCount<5))
        {
        	String sTrad = rs.getCurrent().getString(TRADITIONAL_FIELD_NAME);
        	String sSimp = rs.getCurrent().getString(SIMPLIFIED_FIELD_NAME);
        	String sPinyin = rs.getCurrent().getString(PINYIN_FIELD_NAME);
        	String sEnglish1 = rs.getCurrent().getString(ENGLISH1_FIELD_NAME);
        	String sEnglish2 = rs.getCurrent().getString(ENGLISH2_FIELD_NAME);
        	
        	sbResults.append(sTrad);
        	sbResults.append("; " + sSimp);
        	sbResults.append("; " + sPinyin);
        	sbResults.append("; " + sEnglish1);

        	// Not all entries have a second english meaning.
        	if (sEnglish2!=null)
        	{
        		if (sEnglish2.length()!=0)
        		{
        			sbResults.append("; " + sEnglish2);
        		}
        	}
        	
        	sbResults.append(org.occleve.mobileclient.Constants.NEWLINE);
        	
        	iCount++;
        }

        String sDisplayMe = sbResults.toString();
        int iMaxLength = m_SearchResultsTextArea.getMaxSize();
        if (sDisplayMe.length() > iMaxLength)
        {
        	sDisplayMe = sDisplayMe.substring(0,iMaxLength);
        }
        
        m_SearchResultsTextArea.setText(sDisplayMe);
        m_SearchButton.setText(SEARCH_BUTTON_TEXT);    	
    }

    /**Implementation of J2MEFileSelectorListener.*/
	public void fileSelected(String sFullPathname)
	{
		m_sCedictFileURL = "file:///" + sFullPathname;
		new Thread(this).start();		
	}
}

