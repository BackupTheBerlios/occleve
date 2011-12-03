/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-11  Joe Gittings

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

package org.occleve.mobileclient.testing.qaview;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;

public class MagicTypewriterFormViewCustomItem
	extends CustomItem implements Runnable
{
    private final int SIZE = 10;

    protected MagicTypewriterFormView m_MagicTypewriterFormView;

    public MagicTypewriterFormViewCustomItem(MagicTypewriterFormView mtFormView)
    {
        super("FOOOO");
        m_MagicTypewriterFormView = mtFormView;
        
super.setLabel("keypresses? " + areKeypressesSupported());        

		//new Thread(this).start();
    }

    /**Implementation of abstract method in CustomItem.*/
    protected int getMinContentHeight() {return SIZE;}

    /**Implementation of abstract method in CustomItem.*/
    protected int getMinContentWidth() {return SIZE;}

    /**Implementation of abstract method in CustomItem.*/
    protected int getPrefContentHeight(int _int) {return SIZE;}

    /**Implementation of abstract method in CustomItem.*/
    protected int getPrefContentWidth(int _int) {return SIZE;}

    /**Override of CustomItem method:
    don't allow this item to lose focus.*/
    protected void traverseOut()
    {
        Display.getDisplay(OccleveMobileMidlet.getInstance()).setCurrentItem(this);
    }

    public boolean areKeypressesSupported()
    {
        int intModes = getInteractionModes();
        int iKeyPressMode = (intModes & CustomItem.KEY_PRESS);
        return (iKeyPressMode==CustomItem.KEY_PRESS);
    }

    /**Implementation of abstract method in CustomItem.*/
    protected void paint(Graphics graphics, int width, int height) {}

    protected void keyPressed(int keyCode)
    {
String foo = "" + keyCode;    	
super.setLabel(foo);    	
        m_MagicTypewriterFormView.onKeyPressEvent(keyCode);
    }
    
    public void run() {
        Display.getDisplay(OccleveMobileMidlet.getInstance()).setCurrentItem(this);
        try {
        	Thread.sleep(100);
        }
        catch (Exception e) {}
    }
}
