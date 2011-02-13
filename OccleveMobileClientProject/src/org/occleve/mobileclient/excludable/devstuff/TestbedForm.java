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

public class TestbedForm extends Form implements CommandListener
{
	protected DevStuffChildScreenHelper m_Helper;

    public TestbedForm(Object parentDisplayable) throws Exception
    {
        super(null);

        //m_Helper = new DevStuffChildScreenHelper(this,parentDisplayable);
                
        setCommandListener(this);

        TestbedCustomItem tci = new TestbedCustomItem();
        append(tci);

        TextField tf = new TextField(null,"hello",6,TextField.ANY);
        append(tf);

        TextField tfMax = new TextField(null,"abc",6000,TextField.ANY);
                
        String sMsg = "Textfield max size = " + tfMax.getMaxSize();
        StringItem si = new StringItem(null,sMsg);
        append(si);

        TestbedForm_TestFonts();
    }

    /**From http://www.java2s.com/Code/Java/J2ME/FontCanvasMIDlet.htm
     Learning Wireless Java, Help for New J2ME Developers, By Qusay Mahmoud.*/
    protected void TestbedForm_TestFonts()
    {
        String text;
        StringItem si;

	  si = new StringItem(null, "Monospace Small Plain" + Constants.NEWLINE);
	  StaticHelpers.safeSetFont(si,Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN,Font.SIZE_SMALL));
	  append(si);
	
	   si = new StringItem(null,"System Small Plain" + Constants.NEWLINE);
	   StaticHelpers.safeSetFont(si,Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
	   append(si);
	
	   si = new StringItem(null,"System Large Plain" + Constants.NEWLINE);
	   StaticHelpers.safeSetFont(si,Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
	   append(si);
	
	   si = new StringItem(null,"System Med Plain" + Constants.NEWLINE);
	   StaticHelpers.safeSetFont(si,Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
	   append(si);
	
	   si = new StringItem(null,"System Med Bold" + Constants.NEWLINE);
	   StaticHelpers.safeSetFont(si,Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	   append(si);
	
	   si = new StringItem(null,"System Med Italic" + Constants.NEWLINE);
	   StaticHelpers.safeSetFont(si,Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_MEDIUM));
	   append(si);
	
	   si = new StringItem(null,"System Med Underlined" + Constants.NEWLINE);
	   StaticHelpers.safeSetFont(si,Font.getFont(Font.FACE_SYSTEM, Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM));
	   append(si);
   }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable s)
    {
    	//m_Helper.commandAction(c,s);
    }
}

