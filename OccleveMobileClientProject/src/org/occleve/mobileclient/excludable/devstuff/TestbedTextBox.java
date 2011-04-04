/////////// DEFUNCT /////////////////

/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-9  Joe Gittings

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

public class TestbedTextBox extends TextBox implements CommandListener
{
    private static final int MARGIN = 5;
	protected DevStuffChildScreenHelper m_Helper;
    protected Command m_InsertTextCommand;

    public TestbedTextBox(Object parentDisplayable) throws Exception
    {
        super("Title","initial text",100,TextField.ANY);

        //m_Helper = new DevStuffChildScreenHelper(this,parentDisplayable);

        m_InsertTextCommand = new Command("Insert text",Command.BACK,0);
        addCommand(m_InsertTextCommand);

        setCommandListener(this);

        // This initial input mode does NOT work on the K300c.
        // String sMode = "IS_SIMPLIFIED_HANZI";

        // This page states that SIMPLIFIED_HANZI includes
        // "...a subset of the CJK unified ideographs as well as
        // Simplified Chinese Han characters defined in higher planes."
        // http://www.w3.org/TR/2001/WD-xforms-20011207/sliceB.html
        String sMode = "CJK_UNIFIED_IDEOGRAPHS";

        setInitialInputMode(sMode);
        setString("Initial input mode set to:" + Constants.NEWLINE + sMode);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable s)
    {
        if (c==m_InsertTextCommand)
        {
            this.insert("12",0);
        }
        else
        {
            //m_Helper.commandAction(c, s);
        }
    }
}

