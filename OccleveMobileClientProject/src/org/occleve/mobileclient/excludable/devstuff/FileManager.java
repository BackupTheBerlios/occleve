/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2008-9  Joe Gittings

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

package org.occleve.mobileclient.excludable.devstuff;

import java.util.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.testing.*;

import com.exploringxml.xml.Node;
import com.exploringxml.xml.Xparse;

/**New in 0.9.6 - a utility class which lists ALL files stored in the recordstore
(quizzes, GIFs, MP3s,....)*/
public class FileManager extends List
implements CommandListener
{
	protected DevStuffScreen m_DevStuffScreen;
	protected DevStuffChildScreenHelper m_Helper;
	
	/**0.9.7*/
	protected VocabRecordStoreManager m_RecordStoreManager;
	
    protected Command m_DeleteCommand;
    protected Command m_DetailsCommand;

    protected Hashtable m_RecordIndicesKeyedByFilenames;

    public FileManager(VocabRecordStoreManager rsMgr,DevStuffScreen dvs)
    throws Exception
    {
        super(Constants.PRODUCT_NAME,List.IMPLICIT);

        m_DevStuffScreen = dvs;
        // m_Helper = new DevStuffChildScreenHelper(this,dvs);

        m_RecordStoreManager = rsMgr;
        m_DeleteCommand = new Command("Delete", Command.ITEM, 1);
        m_DetailsCommand = new Command("Details", Command.ITEM, 1);

        addCommand(m_DeleteCommand);
        addCommand(m_DetailsCommand);

        populate();
        setCommandListener(this);
    }

    public void populate() throws Exception
    {
        // Clear out the existing items in this form, if any.
        deleteAll();

    	// 0.9.6
        //VocabRecordStoreManager mgr = new VocabRecordStoreManager();
    	////VocabRecordStoreManager mgr = OccleveMobileMidlet.getInstance().getVocabRecordStoreManager();
        
        m_RecordIndicesKeyedByFilenames =
        	m_RecordStoreManager.getRecordIndicesKeyedByFilenames();
        Enumeration filenames = m_RecordIndicesKeyedByFilenames.keys();
                
        for (int i=0; i<m_RecordIndicesKeyedByFilenames.size(); i++)
        {
        	String sFilename = (String)filenames.nextElement();
        	append(sFilename,null);
        }
    }

    /*Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable d)
    {
        try
        {
            commandAction_Inner(c,d);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    /*Subfunction for code clarity.*/
    private void commandAction_Inner(Command c,Displayable d) throws Exception
    {
        int iSelIndex = getSelectedIndex();

        if (c==m_DeleteCommand)
        {
        	// Delete the selected record from the record store.
        	String sFilename = getString(iSelIndex);
        	Integer iRecordID =
        		(Integer)m_RecordIndicesKeyedByFilenames.get(sFilename);
            m_RecordStoreManager.deleteTest(iRecordID.intValue(),sFilename);
            
            // Need to update the contents of this screen, now.
            // Simplest way to do this would be to call populate() again...
            // but this changes the order of the remaining items in the list,
            // which looks a bit confusing (although is perfectly correct).
            delete(iSelIndex);
            m_RecordIndicesKeyedByFilenames.remove(sFilename);

            // Once we exit the dev stuff screen, the main list of quizzes
            // will need refreshing.
            m_DevStuffScreen.setQuizListNeedsRefreshing();
        }
        else if (c==m_DetailsCommand)
        {
        	String sFilename = getString(iSelIndex);
        	showFileDetails(sFilename);
        }
        else
        {
        	//m_Helper.actionCommand(c);
        }
    }

    /**Show useful information about the selected file.*/
    private void showFileDetails(String sFilename) throws Exception
    {
    	System.out.println("Filename = " + sFilename);
    	
    	Integer iRecordID =
    		(Integer)m_RecordIndicesKeyedByFilenames.get(sFilename);
        byte[] recordData = m_RecordStoreManager.getRecordBytes(iRecordID.intValue());

        String sFileInfo =
        	"Size = " + recordData.length + Constants.NEWLINE +
        	"Recordstore ID = " + iRecordID.intValue();

        if ((sFilename.endsWith(".gif")==false) && (sFilename.endsWith(".mp3")==false))
        {
	        ListOfTestsEntry entry = new ListOfTestsEntry(sFilename,iRecordID,null);
	        String sTestSource = m_RecordStoreManager.getTestContents(entry);
	
	        long lFreeMemBefore = Runtime.getRuntime().freeMemory();
			System.out.println("Free memory before parsing = " + lFreeMemBefore);
	        
	        Xparse parser = new Xparse();
	        Node root = parser.parse(sTestSource);
	
	        long lFreeMemAfter = Runtime.getRuntime().freeMemory();
	        System.out.println("Free memory after parsing = " + lFreeMemAfter);
	        
	        long lMemUsedByParsing = lFreeMemBefore - lFreeMemAfter;
	        sFileInfo +=
	        	Constants.NEWLINE + "Memory used by parsing XML = " + lMemUsedByParsing;
        }
                
        Alert infoAlert = new Alert(sFilename,sFileInfo,null,AlertType.INFO);
        OccleveMobileMidlet.getInstance().displayAlert(infoAlert,this);    	
    }
}

