/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2009-2011  Joe Gittings

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

package org.occleve.mobileclient.screens;

import com.sun.lwuit.*;
import com.sun.lwuit.layouts.*;
import org.occleve.mobileclient.*;

/**The main screen of the application: lists the quizzes currently stored in the
phone, and allows the user to select a quiz for testing or viewing.*/
public class ProgressAlert extends Form
{
	protected TextArea m_Label;

    public ProgressAlert(String message) throws Exception
    {
    	this(Constants.PRODUCT_NAME,message);
    }

    public ProgressAlert(String title,String message) throws Exception
    {
        super(title);

    	//Image logoImage = StaticHelpers.loadOccleveLogo();
    	//setBgImage(logoImage);

        m_Label = new TextArea(message);
        m_Label.setEditable(false);
        //m_Label.setBorderPainted(false);
        m_Label.setRows(5);
        m_Label.setSingleLineTextArea(false);
        m_Label.setGrowByContent(true);
        
        setLayout(new BorderLayout());
        addComponent(BorderLayout.CENTER,m_Label);
    }

    public void actionCommand(Command c) {}

    public String getMessage() {return m_Label.getText();}

    public void setMessage(String msg) {m_Label.setText(msg);}
}

