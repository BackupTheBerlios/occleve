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

package org.occleve.mobileclient.components;

import com.sun.lwuit.*;
import com.sun.lwuit.list.*;

public class OccleveList extends List
{
	protected boolean m_bUseTextArea = false;
	public void setUseTextArea(boolean b) {m_bUseTextArea = b;}
	
	protected Font m_Font;
	public void setFont(Font f) {m_Font = f;}
		
	public OccleveList()
	{
        DefaultListModel model = new DefaultListModel();
        setModel(model);
        
		// Don't display a number next to each item in the list.
        OccleveListCellRenderer renderer = new OccleveListCellRenderer();
        renderer.setShowNumbers(false);
        setListCellRenderer(renderer);

        // June 2010 - this is just annoying on slow phones
        setSmoothScrolling(false);
        
        setItemGap(0);
	}

    public boolean isScrollableX() {return false;}
    public boolean isScrollableY() {return true;}

	public void clear()
	{
        DefaultListModel model = new DefaultListModel();
        setModel(model);		
	}

	protected class OccleveListCellRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(List list,
				Object value,int index,boolean isSelected)
		{
			Component c;
			if (m_bUseTextArea)
			{
				TextArea ta = new TextArea((String)value + "  fooooooooooooooooo fooooo foooo",2,2);
				ta.setGrowByContent(true);
				c = (Component)ta;
			}
			else
				c = super.getListCellRendererComponent(list,value,index,isSelected);
			
			if (m_Font!=null) c.getStyle().setFont(m_Font);
			return c;
		}
	}
}
