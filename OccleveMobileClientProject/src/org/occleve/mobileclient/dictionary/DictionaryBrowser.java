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

import java.io.*;

import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;

import bm.db.*;
import org.occleve.mobileclient.*;

/**0.9.7 - a browser for the CC-CEDICT dictionary.*/
public class DictionaryBrowser extends Form
implements CommandListener,ItemCommandListener,Runnable
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
	
    protected Command m_QuizModeCommand;
    protected Command m_DeleteDatabaseCommand;
    protected Command m_CreateDatabaseCommand;
    protected CommonCommands m_CommonCommands;

    protected TextField m_SearchForTextField;

    protected static final String SEARCH_BUTTON_TEXT = "Search";
    protected StringItem m_SearchButton =
        new StringItem(null,SEARCH_BUTTON_TEXT,Item.BUTTON);

    protected TextField m_SearchResultsTextField;
    
    public DictionaryBrowser() throws Exception
    {
        super("Dictionary");

        m_QuizModeCommand = new Command("Quiz mode", Command.ITEM, 1);
        addCommand(m_QuizModeCommand);

        m_DeleteDatabaseCommand = new Command("Delete DB", Command.ITEM, 1);
        addCommand(m_DeleteDatabaseCommand);

        m_CreateDatabaseCommand = new Command("Create DB", Command.ITEM, 1);
        addCommand(m_CreateDatabaseCommand);

        m_CommonCommands = new CommonCommands();
        m_CommonCommands.addToDisplayable(this);
        
        setCommandListener(this);

        // Append items to this form.

        m_SearchForTextField =
        	new TextField("Search for:","",10,TextField.ANY);
        append(m_SearchForTextField);

        append(m_SearchButton);
        m_SearchButton.setItemCommandListener(this);
        Command temp = new Command("temp",Command.OK,1);
        m_SearchButton.setDefaultCommand(temp);

        m_SearchResultsTextField =
        	new TextField(null,"",500,TextField.UNEDITABLE);
        append(m_SearchResultsTextField);

        OccleveMobileMidlet.getInstance().setCurrentForm(this);        

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
            OccleveMobileMidlet.getInstance().setCurrentForm(this);        
    	}
    	catch (Exception e)
    	{
    		System.err.println(e);
    	}
    }
    
    /**The database doesn't exist yet. Create it, and then import the CEDICT data
    into it from the CEDICT flat file which should be somewhere on the phone's
    filesystem.*/
    private void createAndPopulateDictionaryDatabase() throws Exception
    {
        Alert progress = new Alert(null,"Creating database...",null,null);
        OccleveMobileMidlet.getInstance().displayAlert(progress,this);
        progress.setTimeout(Alert.FOREVER);

        Table tbl = createDatabaseAndTable(progress);
    	
    	progress.setString("Importing dictionary....");

    	importDictionary(tbl,progress);
    }

    private Table createDatabaseAndTable(Alert progress) throws Exception
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
    private void importDictionary(Table tbl,Alert progress) throws Exception
    {
    	// For now, this should be the UNCOMPRESSED file.
        String sFilename = "file:///root1/cedict_ts.u8";
        
        FileConnection fc = (FileConnection)Connector.open(sFilename);
        if(!fc.exists())
        {
        	throw new IOException("The dictionary file does not exist");
        }
        System.out.println("Opened file ok...");
        
        InputStream is = fc.openInputStream();        	
        InputStreamReader isr = new InputStreamReader(is,"UTF-8");
        
        String oneLine = "";
        int iMaxLength = 0;

        // while (oneLine!=StaticHelpers.END_OF_STREAM_REACHED)
        //while (isr.ready())
        for (int i=0; i<751; i++)
        {
        	oneLine = StaticHelpers.readFromISR(isr,true);
        	if (oneLine.length() > iMaxLength) iMaxLength = oneLine.length();
        	
        	boolean bTrace = (i%250 == 0);
        	processCedictLine(oneLine,tbl,bTrace,i);
        	
        	if (bTrace)
        	{
        		progress.setString("Processed " + i + " lines");
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
    	
    	Row rw = tbl.createRow();
    	
    	rw.setField(TRADITIONAL_FIELD_NAME,sTraditional);
    	rw.setField(SIMPLIFIED_FIELD_NAME,sSimplified);
    	rw.setField(PINYIN_FIELD_NAME,sPinyin);
    	rw.setField(ENGLISH1_FIELD_NAME,sEnglish1);
    	rw.setField(ENGLISH2_FIELD_NAME,sEnglish2);
    	rw.save();

    	if (bTrace)
    	{
        	System.out.println(iLineNo + ": " + sLine);
        	System.out.flush();
    	}
    }
    
    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_QuizModeCommand)
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
                Alert progress = new Alert(null,"Deleting database...",null,null);
                OccleveMobileMidlet.getInstance().displayAlert(progress,this);
                progress.setTimeout(Alert.FOREVER);

            	Database db = Database.connect(DICTIONARY_DB_NAME);
        		db.start();
        		db.drop();            	
        		
        		progress.setString("Database deleted");
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else if (c==m_CreateDatabaseCommand)
        {
            try
            {
    			new Thread(this).start();
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command in DictionaryBrowser.commandAction");
        }
    }

    /*Implementation of ItemCommandListener.*/
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

    /**Called when the user clicks Search.*/
    private void searchDatabase() throws Exception
    {
    	System.out.println("Search button clicked...");
    	OccleveMobileMidlet.getInstance().beep();
        m_SearchButton.setText("Searching...");

		Database db = Database.connect(DICTIONARY_DB_NAME);
    	System.out.println("db = " + db);
    	if (db==null)
    	{
    		m_SearchResultsTextField.setString("No database connection");
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
    		m_SearchResultsTextField.setString("Table not found in database");
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
    		    	
        RowSet rs = tbl.findFuzzy(sFieldExpression,m_SearchForTextField.getString());
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
        	sbResults.append(", " + sSimp);
        	sbResults.append(", " + sPinyin);
        	sbResults.append(", " + sEnglish1);

        	// Not all entries have a second english meaning.
        	if (sEnglish2!=null)
        	{
        		if (sEnglish2.length()!=0)
        		{
        			sbResults.append(", " + sEnglish2);
        		}
        	}
        	
        	sbResults.append(org.occleve.mobileclient.Constants.NEWLINE);
        	
        	iCount++;
        }

        String sDisplayMe = sbResults.toString();
        int iMaxLength = m_SearchResultsTextField.getMaxSize();
        if (sDisplayMe.length() > iMaxLength)
        {
        	sDisplayMe = sDisplayMe.substring(0,iMaxLength);
        }
        
        m_SearchResultsTextField.setString(sDisplayMe);
        m_SearchButton.setText(SEARCH_BUTTON_TEXT);    	
    }

}

