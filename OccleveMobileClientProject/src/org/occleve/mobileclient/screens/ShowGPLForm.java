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

import java.util.*;
import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;

public class ShowGPLForm extends Form implements CommandListener
{
    protected Command m_NewTestCommand;

    public ShowGPLForm() throws Exception
    {
        super("Copyright and license");

        String sCopyright =
            "Copyright (C) 2007 Joe Gittings, except for: " + Constants.NEWLINE +
            "Floating point classes copyright " +
            "(C) 2003, 2004 David Clausen " + Constants.NEWLINE +
            "XML parser copyright " +
            "(C) 2000 Michael Claﬂen " + Constants.NEWLINE +
            "All rights reserved. ";
        sCopyright += Constants.NEWLINE + Constants.NEWLINE;
        StringItem si = new StringItem(null,sCopyright);
        append(si);

        String sPreamble =
            "This program is free software; you can redistribute it and/or " +
            "modify it under the terms of the GNU General Public License " +
            "as published by the Free Software Foundation; either version 2 " +
            "of the License, or (at your option) any later version. " +
            "This program is distributed in the hope that it will be useful, " +
            "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the " +
            "GNU General Public License for more details." +
            Constants.NEWLINE + Constants.NEWLINE;
        si = new StringItem(null,sPreamble);
        append(si);

        String sLicense = StaticHelpers.readUnicodeFile("/COPYING");
        Vector vLicense = StaticHelpers.stringToVector(sLicense);
        String sLine = "";
        for (int i=0; i<vLicense.size(); i++)
        {
            sLine += " " + (String)vLicense.elementAt(i);

            if (   ((i%10)==0)   ||  (i==vLicense.size()-1)   )
            {
                si = new StringItem(null, sLine);
                StaticHelpers.safeSetFont(si, OccleveMobileFonts.DETAILS_FONT);
                append(si);
                sLine = "";
            }
        }

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
