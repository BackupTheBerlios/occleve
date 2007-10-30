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

package org.occleve.mobileclient.excludable.raweditor;

import java.util.*;
import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.*;

/**Format of a record is the filename (as a UTF-encoded string), followed
by the UTF-encoded file data.*/
public class RawEditor extends TextBox
implements CommandListener,Excludable,ItemCommandListener
{
    private static String INITIAL_TEXT = "abc";

    protected ListOfTestsEntry m_Entry;
    //protected int m_iRecordID;
    //protected String m_sFilename;
    protected Displayable m_ScreenToReturnTo;
    protected Integer m_iQAIndex;

    ////////////// Implementation of Excludable /////////////////////
    public void setQAIndex(Integer i) {m_iQAIndex=i;}
    public void setScreenToReturnTo(Displayable d) {m_ScreenToReturnTo=d;}
    //public void setTestFilename(String s) {m_sFilename=s;}
    //public void setTestRecordStoreID(Integer i) {m_iRecordID=i.intValue();}
    public void setListOfTestsEntry(ListOfTestsEntry e) {m_Entry = e;}
    /////////////////////////////////////////////////////////////////

    /**Because of size limitations on the amount of text in a TextBox,
    the test file is broken into chunks. A blank line begins a new chunk.*/
    protected Vector m_StringChunks;
    int m_iCurrentChunkIndex = 0;

    /**A separate screen that provides options for inserting various
    preformed commands at the current caret position.*/
    protected RawEditorInserters m_InsertersScreen;

    /**A separate screen that provides options for various
    conversion operations.*/
    protected RawEditorConverters m_ConvertersScreen;

    /**A file chooser form that's used to select the destination
    test to move a chunk to.*/
    protected FileChooserForm m_FileChooserForMovingChunks;

    protected Command m_SaveAndExitCommand;
    protected Command m_SaveCommand;
    protected Command m_CancelCommand;

    protected Command m_HomeCommand;
    protected Command m_EndCommand;
    protected Command m_NextChunkCommand;
    protected Command m_PreviousChunkCommand;

    protected Command m_InsertersScreenCommand;
    protected Command m_ConvertersScreenCommand;
    protected Command m_InsertLFCommand;
    protected Command m_MoveChunkToOtherTestCommand;

    public RawEditor() throws Exception
    {
        super(null,INITIAL_TEXT,INITIAL_TEXT.length(),TextField.ANY);
    }

    public void initialize() throws Exception
    {
        addAllCommands();
        setCommandListener(this);

        m_InsertersScreen = new RawEditorInserters(this);
        m_ConvertersScreen = new RawEditorConverters(this);

        VocabRecordStoreManager mgr = new VocabRecordStoreManager();
        String sContents = mgr.getTestContents(m_Entry);
        m_StringChunks = breakContentsIntoChunks(sContents);

        int iMaxSize = 5000;
        setMaxSize(iMaxSize);

        int iInitialChunkIndex =
            initialize_DetermineInitialChunkIndex(m_iQAIndex);
        moveToChunk(iInitialChunkIndex,false);
    }

    /**Subfunction for code clarity.*/
    private int initialize_DetermineInitialChunkIndex
    (Integer iInitialChunkIndexParam)
    throws Exception
    {
        // If an index was supplied as a param to the constructor, use it.
        if (iInitialChunkIndexParam!=null)
        {
            return iInitialChunkIndexParam.intValue();
        }

        // If a bookmark exists, start by editing the bookmarked chunk,
        // else start by editing the last chunk.
        Integer bookmark = RawEditorBookmarks.getBookmark(m_Entry.getFilename());
        int iInitialChunkIndex;
        if (bookmark==null)
        {
            iInitialChunkIndex = (m_StringChunks.size()-1);
        }
        else
        {
            iInitialChunkIndex = bookmark.intValue();
            if (iInitialChunkIndex > (m_StringChunks.size()-1))
            {
                iInitialChunkIndex = (m_StringChunks.size()-1);
            }
        }

        return iInitialChunkIndex;
    }

    protected Command addItemCommand(String sTitle)
    {
        Command cmd = new Command(sTitle,Command.ITEM,0);
        addCommand(cmd);
        return cmd;
    }

    protected void addAllCommands()
    {
        m_SaveAndExitCommand = new Command("Save and exit",Command.BACK,0);
        addCommand(m_SaveAndExitCommand);

        m_SaveCommand = new Command("Save",Command.BACK,0);
        addCommand(m_SaveCommand);

        m_CancelCommand = new Command("Cancel",Command.BACK,0);
        addCommand(m_CancelCommand);

        m_HomeCommand = addItemCommand("Home");
        m_EndCommand = addItemCommand("End");

        m_InsertersScreenCommand = addItemCommand("Inserter commands");
        m_ConvertersScreenCommand = addItemCommand("Converter commands");
        m_InsertLFCommand = addItemCommand("Insert LF");

        m_NextChunkCommand = addItemCommand("Next chunk");
        m_PreviousChunkCommand = addItemCommand("Previous chunk");

        m_MoveChunkToOtherTestCommand = addItemCommand("Move chunk to other test");
    }

    protected Vector breakContentsIntoChunks(String sContents)
    {
        int iFirstCrlfIndex = sContents.indexOf(Constants.CRLF);
        int iFirstLfIndex = sContents.indexOf(Constants.LF);

        String sDelim;
        if ((iFirstCrlfIndex!=-1) && (iFirstCrlfIndex < iFirstLfIndex))
            sDelim = Constants.CRLF + Constants.CRLF;
        else
            sDelim = Constants.LF + Constants.LF;

        Vector vChunks = StaticHelpers.tokenizeString(sContents,sDelim);
        return vChunks;
    }

    protected void moveToChunk(int iChunkIndexToMoveTo,boolean bSaveCurrentChunkFirst)
    {
        if (bSaveCurrentChunkFirst)
        {
            // Save the current chunk back into the Vector.
            String sSaveThis = getString();
            m_StringChunks.setElementAt(sSaveThis, m_iCurrentChunkIndex);
        }

        // Now move to the new index.
        m_iCurrentChunkIndex = iChunkIndexToMoveTo;
        String sNewContents = (String)m_StringChunks.elementAt(m_iCurrentChunkIndex);
        setString(sNewContents);

        // Get the time.
        String sTime = StaticHelpers.getDisplayableTime();

        // Set the titlebar to show the chunk index.
        String sTitle = "Chunk " + (iChunkIndexToMoveTo + 1) +
                        "/" + m_StringChunks.size() + " " + sTime;
        setTitle(sTitle);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable screen)
    {
        //if (screen==m_FileChooserForMovingChunks)
        //{
        //    commandAction_FileChooser(c);
        //    return;
        //}

        if (c==m_SaveAndExitCommand)
        {
            onSaveCommand();
            bookmarkCurrentChunk();
            OccleveMobileMidlet.getInstance().setCurrentForm(m_ScreenToReturnTo);
        }
        else if (c==m_SaveCommand)
        {
            onSaveCommand();
        }
        else if (c==m_CancelCommand)
        {
            bookmarkCurrentChunk();
            OccleveMobileMidlet.getInstance().setCurrentForm(m_ScreenToReturnTo);
        }
        else if (c==m_HomeCommand)
        {
            moveToChunk(0,true);
        }
        else if (c==m_EndCommand)
        {
            moveToChunk( m_StringChunks.size()-1,true );
        }
        else if (c==m_NextChunkCommand)
        {
            if (m_iCurrentChunkIndex < (m_StringChunks.size()-1))
            {
                moveToChunk(m_iCurrentChunkIndex + 1,true);
            }
        }
        else if (c==m_PreviousChunkCommand)
        {
            if (m_iCurrentChunkIndex > 0)
            {
                moveToChunk(m_iCurrentChunkIndex - 1,true);
            }
        }
        else if (c==m_InsertersScreenCommand)
        {
            OccleveMobileMidlet.getInstance().setCurrentForm(m_InsertersScreen);
        }
        else if (c==m_ConvertersScreenCommand)
        {
            OccleveMobileMidlet.getInstance().setCurrentForm(m_ConvertersScreen);
        }
        else if (c==m_InsertLFCommand)
        {
            insertLF();
        }
        else if (c==m_MoveChunkToOtherTestCommand)
        {
            onMoveChunkToOtherTestCommand();
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command type in TestbedTextBox.commandAction");
        }
    }

    /**Implementation of ItemCommandListener. For handling events on the
    special FileChooser form owned by this notepad.*/
    public void commandAction(Command c,Item itm)
    {
         try
         {
             onFileSelectedToWhichToMoveCurrentChunk(itm);
         }
         catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
     }

     protected void onFileSelectedToWhichToMoveCurrentChunk(Item itm)
     throws Exception
     {
         FilenameItem fi = (FilenameItem) itm;
         String sFilename = fi.getFilename();
         int iRecordID = fi.getRecordStoreID().intValue();

         // Append the current chunk to the selected test.
         String sCurrentChunk = getString();
         VocabRecordStoreManager mgr = new VocabRecordStoreManager();
         ListOfTestsEntry entry = new ListOfTestsEntry(sFilename,new Integer(iRecordID),null);
         mgr.appendToTest(entry,sCurrentChunk);

         // Now wipe out the current chunk, both in the array
         // and on the screen.
         m_StringChunks.setElementAt("", m_iCurrentChunkIndex);
         setString("");

         // Display confirmation
         String sMsg = "Current chunk moved to test " + sFilename;
         Alert alert = new Alert(null,sMsg,null,null);
         OccleveMobileMidlet.getInstance().displayAlert(alert,this);
     }

    protected void onMoveChunkToOtherTestCommand()
    {
        try
        {
            if (m_FileChooserForMovingChunks==null)
            {
                m_FileChooserForMovingChunks =
                        new FileChooserForm( false);
                m_FileChooserForMovingChunks.setExternalCommandListener(this);

                Command moveTo = new Command("Move to",Command.OK,0);
                m_FileChooserForMovingChunks.addCommand(moveTo);
            }

            OccleveMobileMidlet.getInstance().setCurrentForm(m_FileChooserForMovingChunks);
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    protected void onSaveCommand()
    {
        try
        {
            onSaveCommand_Inner();
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    private void onSaveCommand_Inner() throws Exception
    {
        // Save the current chunk back into the Vector.

        String sSaveThis = getString();
        m_StringChunks.setElementAt(sSaveThis,m_iCurrentChunkIndex);

        // Now write the chunks to the record store.

        final String BLANK_LINE = Constants.NEWLINE + Constants.NEWLINE;
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<m_StringChunks.size(); i++)
        {
            String sChunk = (String)m_StringChunks.elementAt(i);

            // Don't save empty chunks.
            if (sChunk.length() > 0)
            {
                sb.append(sChunk);
                sb.append(BLANK_LINE);
            }
        }

        VocabRecordStoreManager mgr = new VocabRecordStoreManager();
        mgr.setTestContents(m_Entry,sb.toString());
    }

    /**Sets the bookmark for this test to the current chunk.*/
    protected void bookmarkCurrentChunk()
    {
        try
        {
            RawEditorBookmarks.setBookmark(m_Entry,m_iCurrentChunkIndex);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void insertLF()
    {
        final String LF = "\n";
        int iCaretPos = getCaretPosition();
        insert(LF,iCaretPos);
    }

    /**Used by eg the RawEditorInserters class to insert text.*/
    public void appendThenDisplay(String sText,boolean bClearFirst)
    {
        if (bClearFirst)
        {
            setString("");
        }
        else
        {
            // Bit of a fudge: if appending instead of replacing, prefix
            // a blank line.
            sText = Constants.NEWLINE + Constants.NEWLINE + sText;
        }

        // insert(s,i) doesn't seem to work very well: on the K300 it always
        // prepends the text, on KToolbar it always appends.

        String sNewText = getString() + sText;
        // insert(sText,getCaretPosition());
        setString(sNewText);

        OccleveMobileMidlet.getInstance().setCurrentForm(this);
    }

    public void getPlainQAFromCurrentChunk(StringBuffer question,
                                           StringBuffer answer)
    {
        String s = getString();
        Vector v = StaticHelpers.stringToVector(s);
        Enumeration e = v.elements();
        while (e.hasMoreElements())
        {
            String sLine = (String)e.nextElement();

            if (sLine.startsWith("Q"))
            {
                question.append( sLine.substring(1) );
            }

            if (sLine.startsWith("A"))
            {
                answer.append( sLine.substring(1) );
            }
        }
    }

}

