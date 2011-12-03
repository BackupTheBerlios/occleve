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
@version 0.9.3
*/

package org.occleve.mobileclient.testing.qaview;

import javax.microedition.lcdui.*;
import java.util.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.qacontrol.*;

public class MagicTypewriterFormView extends Form
implements QuestionView, Runnable
{
    protected MagicTypewriterController m_Controller;

    protected StringItem m_QuestionItem;
    protected Vector m_AnswerTextFields;
    protected StringItem m_ResultsItem;

    public MagicTypewriterFormView(MagicTypewriterController mtc)
    throws Exception
    {
        super("");

        m_QuestionItem = new StringItem("","");

        m_AnswerTextFields = new Vector();
        TextField firstAnswerField =
        	new TextField("","",1000,TextField.ANY);
        m_AnswerTextFields.addElement(firstAnswerField);
        
        m_ResultsItem = new StringItem("","");

        m_Controller = mtc;

        // Sony Ericsson K300 seems to object to this if it's
        // zero-sized.
        // MagicTypewriterFormViewCustomItem solelyToCatchKeypresses =
        //    new MagicTypewriterFormViewCustomItem(this);
        // append(solelyToCatchKeypresses);

        //TextField foo = new TextField("foo","foo12345",10,TextField.ANY);
        //append(foo);
        
        append(m_QuestionItem);
        append(firstAnswerField);
        append(m_ResultsItem);

        new Thread(this).start();
        
        setCommandListener(
        	(CommandListener)m_Controller.getTestController());
        
        // Ensure the CustomItem has focus so it will catch keypresses
        // (which is the point of it).
        // Display.getDisplay(OccleveMobileMidlet.getInstance()).setCurrentItem(solelyToCatchKeypresses);

    }

    public void onKeyPressEvent(int keyCode)
    {
        m_Controller.onKeyPressed(keyCode);
    }

    /**Implementation of MagicTypewriterView method.*/
    public Object getDisplayable()
    {
        return this;
    }

    /**Implementation of MagicTypewriterView method.*/
    public void doRepainting()
    {
        MagicTypewriterController mtc = m_Controller;

        String sQuestion =
            vectorToString( mtc.getTestController().getCurrentQuestion() );


        Vector answerLines =
            mtc.getTestController().getCurrentAnswerFragment();
        int diff = answerLines.size() - m_AnswerTextFields.size();
        for (int i=0; i<answerLines.size(); i++) {
        	String line = (String)answerLines.elementAt(i);

        	TextField tf;
        	if (i >= m_AnswerTextFields.size()) {
                tf = new TextField("","",1000,TextField.ANY);
                m_AnswerTextFields.addElement(tf);
                
                if (i != answerLines.size()-1) {
                	tf.setConstraints(TextField.UNEDITABLE);
                }
        	}
        	else {
        		tf = (TextField)m_AnswerTextFields.elementAt(i);
        	}

        	tf.setString("");
        	tf.insert(line,0);
        }

        m_QuestionItem.setText("Q: " + sQuestion );
        m_ResultsItem.setText( mtc.getTestController().getCurrentScore() );
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

    /**No need to clone itself.*/
    public QuestionView perhapsClone() throws Exception
    {
        return this;
    }

    public void run() {
    	try {
    		run_Inner();
    	}
    	catch (Throwable t) {
    		OccleveMobileMidlet.getInstance().onError(
    			"MagicTypewriterFormView.run",t);
    	}
    }

    private void run_Inner() throws Throwable {
		String lastContents = null;
    	
    	do {
    		TextField tf = (TextField)m_AnswerTextFields.lastElement();
    		String contents = tf.getString();
    		if (lastContents==null) lastContents = contents;

    		if (contents.equals(lastContents)==false) {
    			if (contents.length()>lastContents.length()) {
    				//char nextChar = contents.charAt(lastContents.length());
    				char nextChar = contents.charAt(0);
    		        m_Controller.onKeyPressed((int)nextChar);
    			}
    		}
    		lastContents = contents;
    		
    		try {Thread.sleep(50);} catch (Exception e) {}
    		
    	} while (isShown());
    }

}

