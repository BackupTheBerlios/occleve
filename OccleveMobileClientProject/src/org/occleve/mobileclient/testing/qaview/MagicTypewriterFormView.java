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
@version 0.9.10
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

        m_ResultsItem = new StringItem("","");
        m_QuestionItem = new StringItem("","");

        m_AnswerTextFields = new Vector();
        TextField firstAnswerField =
        	new TextField("","",1000,TextField.ANY);
        m_AnswerTextFields.addElement(firstAnswerField);

        m_Controller = mtc;

        // Sony Ericsson K300 seems to object to this if it's
        // zero-sized.
        // MagicTypewriterFormViewCustomItem solelyToCatchKeypresses =
        //    new MagicTypewriterFormViewCustomItem(this);
        // append(solelyToCatchKeypresses);

        //TextField foo = new TextField("foo","foo12345",10,TextField.ANY);
        //append(foo);
        
        append(m_ResultsItem);
        append(m_QuestionItem);
        append(firstAnswerField);

        new Thread(this).start();
        
        setCommandListener(
        	(CommandListener)m_Controller.getTestController());
        
        // Set focus
        Display.getDisplay(OccleveMobileMidlet.getInstance()).
        	setCurrentItem(firstAnswerField);
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
    	try {
    		doRepainting_Inner();
    	}
    	catch (Exception e) {
    		OccleveMobileMidlet.getInstance().onError(
        			"MagicTypewriterFormView.doRepainting",e);
    	}
    }

    //public static String debug = "";    
    
    private void doRepainting_Inner() throws Exception
    {
    	//debug += "#";    	
        MagicTypewriterController mtc = m_Controller;

        String sQuestion =
            vectorToString( mtc.getTestController().getCurrentQuestion() );

        Vector answerLines =
            mtc.getTestController().getCurrentAnswerFragment();
        
        int diff = answerLines.size() - m_AnswerTextFields.size();

        //if (diff!=0) {
        //	debug += "alsize=" + answerLines.size() +
        //	" atfsize=" + m_AnswerTextFields.size() +
        //	" diff=" + diff;
        //}
        
		if (diff>0) {
	        for (int i=0; i<diff; i++) {
	            TextField tf = new TextField("","",1000,TextField.ANY);
	            m_AnswerTextFields.addElement(tf);
	
	            append(tf);
	
	            //debug += " i=" + i;            
	        }
		}
		else if (diff<0) {
            for (int i=0; i>diff; i--) {
            	m_AnswerTextFields.removeElementAt(m_AnswerTextFields.size()-1);
            	delete(size()-1);
            }
        }
        
        for (int i=0; i<answerLines.size(); i++) {
        	String line = (String)answerLines.elementAt(i);

        	TextField tf = (TextField)m_AnswerTextFields.elementAt(i);
            if (i == answerLines.size()-1) {
            	tf.setConstraints(TextField.ANY);
            }
            else {
            	tf.setConstraints(TextField.UNEDITABLE);
            }

        	tf.setString("");
        	tf.insert(line,0);
        }

        TextField lastTF = (TextField)m_AnswerTextFields.lastElement();

        m_QuestionItem.setText("Q: " + sQuestion );
        m_ResultsItem.setText( // debug + " " +
        		mtc.getTestController().getCurrentScore() );
        
    	Display.getDisplay(OccleveMobileMidlet.getInstance()).
    		setCurrentItem(lastTF);        
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

Display.getDisplay(OccleveMobileMidlet.getInstance()).
   setCurrentItem(tf);
    		
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
    		
    	} while (!m_Controller.getTestController().isTestCompleted());
    }
}
