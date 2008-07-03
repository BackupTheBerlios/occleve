/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-8  Joe Gittings

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

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;

public class DeleteTestConfirmationScreen extends TextBox
implements CommandListener
{
    protected Displayable m_ScreenToReturnTo;
    protected int m_iRecordIDToDelete;
    protected String m_sFilenameToDelete;

    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    public DeleteTestConfirmationScreen(int iRecordID,
                                        String sFilename,
                                        Displayable screenToReturnTo)
    throws Exception
    {
        super("Delete " + sFilename + "?","",3,TextField.ANY);
        m_ScreenToReturnTo = screenToReturnTo;
        m_iRecordIDToDelete = iRecordID;
        m_sFilenameToDelete = sFilename;

        m_OKCommand = new Command("OK",Command.OK,0);
        addCommand(m_OKCommand);

        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);
        addCommand(m_CancelCommand);

        setCommandListener(this);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_OKCommand)
        {
            String text = getString().toLowerCase();
            if (text.equals("yes"))
            {
                deleteTest();
                return;
            }
        }

        OccleveMobileMidlet.getInstance().setCurrentForm(m_ScreenToReturnTo);
    }

    protected void deleteTest()
    {
        try
        {
        	VocabRecordStoreManager mgr =
        		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();        	
            mgr.deleteTest(m_iRecordIDToDelete,m_sFilenameToDelete);
            OccleveMobileMidlet.getInstance().repopulateFileChooser();

            // Display confirmation
            String sMsg = "Test " + m_sFilenameToDelete +
                          " deleted from recordstore";
            Alert alert = new Alert(null,sMsg,null,null);
            OccleveMobileMidlet.getInstance().displayAlertThenFileChooser(alert);
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }
}

