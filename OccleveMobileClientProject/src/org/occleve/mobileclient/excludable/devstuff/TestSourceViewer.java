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

import java.util.*;
import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;

public class TestSourceViewer extends Form implements CommandListener
{
	protected DevStuffChildScreenHelper m_Helper;

    public TestSourceViewer(String sHeading,Vector vTestSource,
    		Displayable parentDisplayable)
    {
        super(sHeading);

        m_Helper = new DevStuffChildScreenHelper(this,parentDisplayable);

        // DOESN'T WORK ON K300 (BUT DOES ON EMULATOR)
        // Passing in null removes title and saves screen space.
        // super(null);

        try
        {
            populate(vTestSource);
        }
        catch (IllegalArgumentException e)
        {
            // On Sony Ericsson phones, this is thrown if you try to append
            // more than 256 Items to a Form.
        }

        setCommandListener(this);
    }

    public void populate(Vector vTestSource) throws IllegalArgumentException
    {
        // Clear any existing items.
        deleteAll();

        for (int i=0; i<vTestSource.size(); i++)
        {
            String sLine = (String)vTestSource.elementAt(i) + Constants.NEWLINE;
            StringItem si = new StringItem(null,sLine);
            StaticHelpers.safeSetFont(si,OccleveMobileFonts.SMALL_FONT);
            append(si);
        }
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
    	m_Helper.commandAction(c, s);
    }
}
