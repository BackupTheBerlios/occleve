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
	private static final String DICTIONARY_DB_NAME = "dictionaryDB_2";

	private static final String TRADITIONAL_FIELD_NAME = "traditional";
	private static final String SIMPLIFIED_FIELD_NAME = "simplified";
	private static final String PINYIN_FIELD_NAME = "pinyin";
	private static final String ENGLISH1_FIELD_NAME = "english1";
	private static final String ENGLISH2_FIELD_NAME = "english2";
	
    protected StringItem m_StartTestItem =
            new StringItem(null,"Start test",Item.BUTTON);

    protected String SEQUENTIAL = "In sequence";
    protected String RANDOM = "Random";
    protected ChoiceGroup m_SequentialOrRandomChoiceGroup;

    // 0.9.6 - add a Start From Question No field for sequential mode
    protected TextField m_FirstQuestionTextField;
    protected TextField m_LastQuestionTextField;
    protected TextField m_RestartOnPercentageBelowTextField;
    
    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    public DictionaryBrowser() throws Exception
    {
        super(org.occleve.mobileclient.Constants.PRODUCT_NAME);

        m_OKCommand = new Command("OK",Command.OK,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);

        addCommand(m_OKCommand);
        addCommand(m_CancelCommand);
        setCommandListener(this);

        // Append items to this form.

        append(m_StartTestItem);
        m_StartTestItem.setItemCommandListener(this);
        m_StartTestItem.setDefaultCommand(m_OKCommand);

        String[] orderChoices = {SEQUENTIAL,RANDOM};
        m_SequentialOrRandomChoiceGroup =
            new ChoiceGroup(null,ChoiceGroup.POPUP,orderChoices,null);
        append(m_SequentialOrRandomChoiceGroup);

        /*
        m_FirstQuestionTextField =
        	new TextField("Question to start from:","1",10,TextField.NUMERIC);
        append(m_FirstQuestionTextField);

        m_LastQuestionTextField =
        	new TextField("Question to end at:","1",10,TextField.NUMERIC);
        append(m_LastQuestionTextField);

        m_RestartOnPercentageBelowTextField =
        	new TextField("Restart if percentage drops under:","0",10,TextField.NUMERIC);
        append(m_RestartOnPercentageBelowTextField);
        */

        OccleveMobileMidlet.getInstance().setCurrentForm(this);        
        new Thread(this).start();
    }

    public void run()
    {
    	try
    	{
    		connectToDictionaryDatabase();
    	}
    	catch (Exception e)
    	{
    		System.err.println(e);
    	}
    }
    
    private void connectToDictionaryDatabase() throws Exception
    {
    	Database db = null;
    	
    	try
    	{
    		db = Database.connect(DICTIONARY_DB_NAME);
    		System.out.println("Successfully connected to database " + DICTIONARY_DB_NAME);

    		System.out.println("Now dropping...");
    		db.drop();
    		db = null;
    	}
    	catch (Exception e) {System.err.println("Error in db connect section: " + e);}
    	
    	if (db==null)
    	{
    		createAndPopulateDictionaryDatabase();
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
    	
    	Database db = Database.create(DICTIONARY_DB_NAME);
    	Table tbl = new Table("CEDICT");
    	
    	// According to Row.serializeField(), which in turns calls
    	// SerializerOutputStream.writeString(), the string
    	// field lengths are only used to determine the max length of data to
    	// be written. So we can choose generous field sizes here.
    	tbl.addColumn(TRADITIONAL_FIELD_NAME,bm.db.Constants.FT_STRING,50);
    	tbl.addColumn(SIMPLIFIED_FIELD_NAME, bm.db.Constants.FT_STRING,50);
    	tbl.addColumn(PINYIN_FIELD_NAME, bm.db.Constants.FT_STRING,255);
    	tbl.addColumn(ENGLISH1_FIELD_NAME, bm.db.Constants.FT_STRING,255);
    	tbl.addColumn(ENGLISH2_FIELD_NAME, bm.db.Constants.FT_STRING,255);
    	
    	tbl.createFullTextIndex("pinyinIndex",PINYIN_FIELD_NAME,false);
    	
    	db.createTable(tbl);
    	
    	progress.setString("Importing dictionary....");

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
        for (int i=0; i<5000; i++)
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
        
        db.dropTable(tbl);

        System.out.println("Max line length = " + iMaxLength);
    }

    private void processCedictLine(String sLine,Table tbl,boolean bTrace,int iLineNo)
    throws Exception
    {
    	// If it's a comment line, drop out.
    	if (sLine.startsWith("#")) return;

    	int iDivider1 = sLine.indexOf(' ');
    	int iDivider2 = sLine.indexOf(' ',iDivider1 + 1);
    	int iDivider3 = sLine.indexOf('[',iDivider2 + 1);
    	int iDivider4 = sLine.indexOf(']',iDivider3 + 1);
    	
    	String sTraditional = sLine.substring(0,iDivider1);
    	String sSimplified = sLine.substring(iDivider1 + 1,iDivider2);
    	String sPinyin = sLine.substring(iDivider3 + 1, iDivider4);
    	
    	Row rw = tbl.createRow();
    	
    	rw.setField(TRADITIONAL_FIELD_NAME,sTraditional);
    	rw.setField(SIMPLIFIED_FIELD_NAME,sSimplified);
    	rw.setField(PINYIN_FIELD_NAME,sPinyin);
    	rw.save();

    	if (bTrace)
    	{
        	System.out.println(iLineNo + ": " + sLine);
        	System.out.println(sTraditional + ", " + sSimplified + ", " + sPinyin);
        	System.out.flush();
    	}
    }
    
    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_OKCommand)
        {
            try
            {
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else if (c==m_CancelCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
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
            if (item==m_StartTestItem)
            {
                OccleveMobileMidlet.getInstance().beep();
            }
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }


}

