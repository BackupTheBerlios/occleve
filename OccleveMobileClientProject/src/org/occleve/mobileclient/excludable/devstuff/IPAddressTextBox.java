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

public class IPAddressTextBox extends TextBox
implements CommandListener
{
    static String INITIAL_CONTENTS = "218.";

    protected Command m_OKCommand;
    protected Command m_CancelCommand;
    protected Command m_61Dot170Command;

    public IPAddressTextBox() throws Exception
    {
        super("IP address to send to:",INITIAL_CONTENTS,100,TextField.ANY);

        m_OKCommand = new Command("OK",Command.OK,0);
        addCommand(m_OKCommand);

        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);
        addCommand(m_CancelCommand);

        m_61Dot170Command = new Command("61.170.",Command.CANCEL,0);
        addCommand(m_61Dot170Command);

        setCommandListener(this);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_OKCommand)
        {
            try
            {
                String sIPAddress = getString();
                VocabRecordTransmitter transmitter = new VocabRecordTransmitter();
                transmitter.transmitAllTestFiles(sIPAddress);
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else if (c==m_CancelCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser(true);
        }
        else if (c==m_61Dot170Command)
        {
            setString("61.170.");
        }
        else
        {
            OccleveMobileMidlet.getInstance().onUnknownCommand( this.getClass() );
        }
    }
}

