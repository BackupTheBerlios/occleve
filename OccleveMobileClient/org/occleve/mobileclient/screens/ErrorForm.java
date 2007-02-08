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

package org.occleve.mobileclient.screens;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;

public class ErrorForm extends Form implements CommandListener
{
    protected Command m_NewTestCommand;

    public ErrorForm(Exception theException)
    {
        this(theException.toString());
    }

    public ErrorForm(String sError)
    {
        super("Occleve");

        StringItem si = new StringItem(null,"ERROR!" + Constants.NEWLINE);
        StaticHelpers.safeSetFont(si,OccleveMobileFonts.TITLE_FONT);
        append(si);

        StringItem details = new StringItem(null,sError + Constants.NEWLINE);
        StaticHelpers.safeSetFont(details,OccleveMobileFonts.DETAILS_FONT);
        append(details);

        m_NewTestCommand = new Command("New test", Command.BACK, 0);
        addCommand(m_NewTestCommand);
        setCommandListener(this);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_NewTestCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
    }
}
