/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-2009  Joe Gittings

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

package org.occleve.mobileclient.screens.options;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.*;
import com.sun.lwuit.plaf.*;
import com.sun.lwuit.util.*;
//////import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;

public class SimpleTestOptionsScreen extends TestOptionsScreen
{
    protected String DONT_REVERSE = "Don't reverse questions";
    protected String REVERSE = "Reverse questions";
    protected ComboBox m_QADirectionChoiceGroup;

    public SimpleTestOptionsScreen()
    throws Exception
    {
        super();

        String[] choices = {DONT_REVERSE,REVERSE};
        m_QADirectionChoiceGroup = new ComboBox(choices);
        addComponent(m_QADirectionChoiceGroup);
    }

    protected QADirection getQADirection() throws Exception
    {
        //////int i = m_QADirectionChoiceGroup.getSelectedIndex();
        String sChoice = (String)m_QADirectionChoiceGroup.getSelectedItem();
        boolean bReverse = (sChoice.equals(REVERSE));

        return new SimpleQADirection(bReverse);
    }
}

