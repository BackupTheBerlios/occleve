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

package org.occleve.mobileclient.excludable.devstuff;

import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

import org.occleve.mobileclient.*;

/**Lists all the recordstores belonging to this midlet, and gives options to delete, etc.*/
public class RecordStoreExplorer extends List
implements CommandListener
{	
    protected Command m_BackCommand;
    protected Command m_DeleteCommand;
    protected Command m_DetailsCommand;

    protected String[] m_RecordStoreNames;
    protected Hashtable m_RecordIndicesKeyedByFilenames;

    public RecordStoreExplorer()
    throws Exception
    {
        super(Constants.PRODUCT_NAME,List.IMPLICIT);

        m_BackCommand = new Command("Back", Command.ITEM, 0);
        m_DeleteCommand = new Command("Delete", Command.ITEM, 1);
        m_DetailsCommand = new Command("Details", Command.ITEM, 1);

        addCommand(m_BackCommand);
        addCommand(m_DeleteCommand);
        addCommand(m_DetailsCommand);
        setSelectCommand(m_DetailsCommand);

        populate();
        setCommandListener(this);
    }

    public void populate() throws Exception
    {
        // Clear out the existing items in this form, if any.
        deleteAll();

        m_RecordStoreNames = RecordStore.listRecordStores();
        
        if (m_RecordStoreNames==null)
        {
        	append("No recordstores",null);
        }
        else
        {
	        for (int i=0; i<m_RecordStoreNames.length; i++)
	        {
	        	RecordStore rs = RecordStore.openRecordStore(m_RecordStoreNames[i],false);
	        	int iSize = rs.getSize();
	        	rs.closeRecordStore();

	        	append(m_RecordStoreNames[i] + " " + (iSize/1024) + "K",null);
	        }
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

        if (c==m_BackCommand)
        {
        	OccleveMobileMidlet.getInstance().displayFileChooser(true);
        }
        else if (c==m_DeleteCommand)
        {
        	String sRSName = m_RecordStoreNames[iSelIndex];
        	RecordStore.deleteRecordStore(sRSName);
        	populate();
        }
        else if (c==m_DetailsCommand)
        {
        	String sRSName = m_RecordStoreNames[iSelIndex];
        	showRecordStoreDetails(sRSName);
        }
        else
        {
        	OccleveMobileMidlet.getInstance().onError("Unknown Command in FileManager");
        }
    }

    /**Show useful information about the selected recordstore.*/
    private void showRecordStoreDetails(String sRecordStoreName) throws Exception
    {
    	RecordStore rs = RecordStore.openRecordStore(sRecordStoreName,false);
    	int iSize = rs.getSize();
    	int iSpaceToGrow = rs.getSizeAvailable();
    	int iNumRecords = rs.getNumRecords();
    	rs.closeRecordStore();
    	
        String sInfo =
        	"Name = " + sRecordStoreName +  Constants.NEWLINE +
        	"Size = " + iSize + Constants.NEWLINE +
        	"Space available to grow = " + iSpaceToGrow + Constants.NEWLINE +
        	"No of records = " + iNumRecords;
                
        Alert infoAlert = new Alert(sRecordStoreName,sInfo,null,AlertType.INFO);
        OccleveMobileMidlet.getInstance().displayAlert(infoAlert,this);
    }
}

