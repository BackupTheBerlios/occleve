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
@version 0.9.0
*/

package org.occleve.mobileclient.excludable.devstuff;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;

public class VocabRecordFilenameTextBox extends TextBox
implements CommandListener
{
    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    public VocabRecordFilenameTextBox() throws Exception
    {
        super("Name of new test:","",100,TextField.ANY);

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
            try
            {
                VocabRecordStoreManager mgr = new VocabRecordStoreManager();
                mgr.createEmptyTest(getString());
                OccleveMobileMidlet.getInstance().displayFileChooser(true);
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else
        {
            OccleveMobileMidlet.getInstance().onUnknownCommand( this.getClass() );
        }
    }
}

