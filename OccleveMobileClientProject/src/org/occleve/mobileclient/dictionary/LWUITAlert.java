/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2008  Joe Gittings

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

package org.occleve.mobileclient.dictionary;

import com.sun.lwuit.*;

public class LWUITAlert extends Form
{
	private TextArea m_TextArea;
	
	public LWUITAlert(String sTitle,String sMessage)
	{
		m_TextArea = new TextArea(sMessage);
		m_TextArea.setColumns(20);
		m_TextArea.setRows(5);
		m_TextArea.setEditable(false);
		
		addComponent(m_TextArea);
	}

	public void setString(String s)
	{
		m_TextArea.setText(s);
	}
}
