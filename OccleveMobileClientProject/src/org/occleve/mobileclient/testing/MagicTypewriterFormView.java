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

package org.occleve.mobileclient.testing;

import javax.microedition.lcdui.*;
import java.util.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.*;

public class MagicTypewriterFormView extends Form
implements MagicTypewriterView
{
    protected MagicTypewriterController m_Controller;

    protected StringItem m_QuestionItem;
    protected StringItem m_AnswerItem;
    protected StringItem m_ResultsItem;

    public MagicTypewriterFormView(MagicTypewriterController mtc)
    throws Exception
    {
        super("");

        m_QuestionItem = new StringItem("","");
        m_AnswerItem = new StringItem("","");
        m_ResultsItem = new StringItem("","");

        m_Controller = mtc;

        // Sony Ericsson K300 seems to object to this if it's
        // zero-sized.
        MagicTypewriterFormViewCustomItem solelyToCatchKeypresses =
            new MagicTypewriterFormViewCustomItem(this);
        append(solelyToCatchKeypresses);

        append(m_QuestionItem);
        append(m_AnswerItem);
        append(m_ResultsItem);

        // Ensure the CustomItem has focus so it will catch keypresses
        // (which is the point of it).
        Display.getDisplay(OccleveMobileMidlet.getInstance()).setCurrentItem(solelyToCatchKeypresses);
    }

    public void onKeyPressEvent(int keyCode)
    {
        m_Controller.onKeyPressed(keyCode);
    }

    /**Implementation of MagicTypewriterView method.*/
    public Displayable getDisplayable()
    {
        return this;
    }

    /**Implementation of MagicTypewriterView method.*/
    public void doRepainting()
    {
        MagicTypewriterController mtc = m_Controller;

        String sQuestion = vectorToString( mtc.getCurrentQuestion() );
        String sAnswer = vectorToString( mtc.getCurrentAnswerFragment() );

        m_QuestionItem.setText( "Q: " + sQuestion );
        m_AnswerItem.setText( "A: " + sAnswer );
        m_ResultsItem.setText( mtc.getCurrentScore() );
    }

    protected String vectorToString(Vector v)
    {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<v.size(); i++)
        {
            sb.append( (String)v.elementAt(i) );
            sb.append( Constants.NEWLINE );
        }

        return sb.toString();
    }
}

