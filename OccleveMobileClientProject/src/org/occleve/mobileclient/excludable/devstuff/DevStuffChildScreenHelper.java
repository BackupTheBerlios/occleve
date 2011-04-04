/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2011  Joe Gittings

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
@version 0.9.10
*/

package org.occleve.mobileclient.excludable.devstuff;

import com.sun.lwuit.*;
import org.occleve.mobileclient.*;

public class DevStuffChildScreenHelper
{
    protected Command m_BackCommand;
    protected Object m_Parent;

    public DevStuffChildScreenHelper(Form form,Object parent)
    {
        m_Parent = parent;
        m_BackCommand = new Command("Back");
        form.addCommand(m_BackCommand);
    }
    
    public void actionCommand(Command c)
    {
        if (c==m_BackCommand)
        {
            OccleveMobileMidlet.getInstance().setCurrentForm(m_Parent);
        }
    }
}
