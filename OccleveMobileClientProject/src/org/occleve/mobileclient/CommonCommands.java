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

package org.occleve.mobileclient;

import javax.microedition.lcdui.Displayable;

import com.sun.lwuit.Command;

/**Encapsulates commands which appear on screens throughout the program, such
as Pause and Exit.*/
public class CommonCommands
{
    protected Command m_PauseCommand;
    protected Command m_ExitCommand;

    protected javax.microedition.lcdui.Command m_PauseCommandMIDP;
    protected javax.microedition.lcdui.Command m_ExitCommandMIDP;

    public CommonCommands()
    {
        m_PauseCommand = new Command("Pause");
        m_ExitCommand = new Command("Exit " + Constants.PRODUCT_NAME);

        m_PauseCommandMIDP =
        	new javax.microedition.lcdui.Command("Pause",
        			javax.microedition.lcdui.Command.ITEM,0);
        m_ExitCommandMIDP =
        	new javax.microedition.lcdui.Command("Exit " + Constants.PRODUCT_NAME,
        			javax.microedition.lcdui.Command.ITEM,0);
    }

    public void addToDisplayable(Displayable d)
    {
        d.addCommand(m_PauseCommandMIDP);
        d.addCommand(m_ExitCommandMIDP);
    }

    /**LWUIT style.*/
    public void actionCommand(Command c)
    {
        if (c==m_ExitCommand)
        {
            OccleveMobileMidlet.getInstance().notifyDestroyed();
        }
        else if (c==m_PauseCommand)
        {
            OccleveMobileMidlet.getInstance().tryToPlaceinBackground();
        }    	
    }

    /**MIDP style.*/
    public void commandAction(javax.microedition.lcdui.Command c, Displayable s)
    {
        if (c==m_ExitCommandMIDP)
        {
            OccleveMobileMidlet.getInstance().notifyDestroyed();
        }
        else if (c==m_PauseCommandMIDP)
        {
            OccleveMobileMidlet.getInstance().tryToPlaceinBackground();
        }    	
    }
}

