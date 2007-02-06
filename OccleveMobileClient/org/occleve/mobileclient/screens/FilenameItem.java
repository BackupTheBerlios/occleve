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

public class FilenameItem extends StringItem
{
    private String m_sFilename;
    public String getFilename() {return m_sFilename;}

    private Integer m_RecordStoreID;
    public Integer getRecordStoreID() {return m_RecordStoreID;}

    public FilenameItem(String sDisplayText,String sFilename,Integer recordStoreID)
    {
        super(null,sDisplayText);
        m_sFilename = sFilename;
        m_RecordStoreID = recordStoreID;
    }

    public void addCommandIfExists(Command cmd)
    {
        if (cmd!=null) addCommand(cmd);
    }
}

