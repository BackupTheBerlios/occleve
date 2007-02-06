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

public class FileChooserCustomItem extends CustomItem
{
    protected FileChooserForm m_FileChooser;

    public FileChooserCustomItem(FileChooserForm fcf)
    {
        super("");
        m_FileChooser = fcf;
    }

    /**Implementation of abstract method in CustomItem.*/
    protected int getMinContentHeight() {return 0;}

    /**Implementation of abstract method in CustomItem.*/
    protected int getMinContentWidth() {return 0;}

    /**Implementation of abstract method in CustomItem.*/
    protected int getPrefContentHeight(int _int)
    {
        return 0;
    }

    /**Implementation of abstract method in CustomItem.*/
    protected int getPrefContentWidth(int _int)
    {
        return 0;
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
        char cJumpToThis = keypressToFirstPossibleChar(keyCode);
        m_FileChooser.jumpToFilename(cJumpToThis);
    }

    protected char keypressToFirstPossibleChar(int iKeycode)
    {
        if (iKeycode == Canvas.KEY_NUM0)
            return 'a';
        else if (iKeycode == Canvas.KEY_NUM1)
            return 'a';
        else if (iKeycode == Canvas.KEY_NUM2)
            return 'a';
        else if (iKeycode == Canvas.KEY_NUM3)
            return 'd';
        else if (iKeycode == Canvas.KEY_NUM4)
            return 'g';
        else if (iKeycode == Canvas.KEY_NUM5)
            return 'j';
        else if (iKeycode == Canvas.KEY_NUM6)
            return 'm';
        else if (iKeycode == Canvas.KEY_NUM7)
            return 'p';
        else if (iKeycode == Canvas.KEY_NUM8)
            return 't';
        else if (iKeycode == Canvas.KEY_NUM9)
            return 'w';
        else
            return 'a';
    }
}
