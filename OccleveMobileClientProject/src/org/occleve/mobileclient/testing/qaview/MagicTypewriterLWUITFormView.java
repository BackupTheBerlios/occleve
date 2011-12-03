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

import com.sun.lwuit.*;
import com.sun.lwuit.layouts.*;
import java.util.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.qacontrol.*;

public class MagicTypewriterLWUITFormView extends Form
implements QuestionView
{
	protected Font m_Font;
    protected TextArea m_QuestionArea;
    protected TextArea m_AnswerArea;
    protected TextArea m_ResultsArea;
    
    protected TextField m_DummyTextField;

    protected javax.microedition.lcdui.TextField m_MIDPTextField;

    protected MagicTypewriterController m_Controller;

    private String lastAction = "";    
    public static String extraDebugInfo = "";

    public MagicTypewriterLWUITFormView(MagicTypewriterController mtc)
    throws Exception
    {
        super();
        m_Controller = mtc;

        m_Font = Font.createSystemFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN, Font.SIZE_SMALL);
        getStyle().setFont(m_Font);
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        m_QuestionArea = new TextArea("",2,2);
        m_QuestionArea.setGrowByContent(true);
        m_QuestionArea.setEditable(false);
        addComponent(m_QuestionArea);

        m_AnswerArea = new TextArea("",2,2);
        m_AnswerArea.setGrowByContent(true);
        m_AnswerArea.setEditable(false);
        addComponent(m_AnswerArea);

        m_ResultsArea = new TextArea("",2,2);
        m_ResultsArea.setGrowByContent(true);        
        m_ResultsArea.setEditable(false);
        addComponent(m_ResultsArea);
        
        m_QuestionArea.getStyle().setFont(m_Font);
        m_AnswerArea.getStyle().setFont(m_Font);
        m_ResultsArea.getStyle().setFont(m_Font);

        // Create virtual keyboard and bind to text field
        com.sun.lwuit.impl.midp.VirtualKeyboard vkb
        	= new com.sun.lwuit.impl.midp.VirtualKeyboard();
        vkb.setInputModeOrder(new String[] {VirtualKeyboard.QWERTY_MODE} );
        
        m_DummyTextField = new TextField("foo");
        m_DummyTextField.setUseNativeTextInput(true);
        m_DummyTextField.setEditable(true);
        
        com.sun.lwuit.impl.midp.VirtualKeyboard.
    	bindVirtualKeyboard(m_DummyTextField, vkb);

        addComponent(m_DummyTextField);
        m_DummyTextField.setFocus(true);

        javax.microedition.lcdui.TextField m_MIDPTextField =
        	new javax.microedition.lcdui.TextField
        	("foo","foo",5,javax.microedition.lcdui.TextField.ANY);

        //android.widget.Button btn = new android.widget.Button(
        //		com.sun.lwuit.impl.android.LWUITActivity.currentActivity);
        //btn.setText("Test");        
        //PeerComponent pc = PeerComponent.create(btn);
        //addComponent(pc);
                
        System.out.println("FINISHED");
    }

    public void keyPressed(int keyCode)
    {
    	lastAction = "Key pressed=" + keyCode;    		
        m_Controller.onKeyPressed(keyCode);
        // doUpdate();
        
        m_DummyTextField.keyPressed(keyCode);
        
    	com.sun.lwuit.Display disp =
    		com.sun.lwuit.Display.getInstance();
    	disp.setShowVirtualKeyboard(true);

    }

    /**0.9.4: If the pointer is pressed, jump straight into the Unicode input screen.
    This behaviour is so that, if you're using a pen phone to input Hanzi
    or similar pictographic characters,
    starting to write the character will immediately invoke the
    appropriate input screen.*/
    /* public void pointerPressed(int x,int y)
    {
    	try
    	{
    		lastAction = "Pointer pressed at " + x + "," + y;    		
    		m_Controller.invokeUnicodeInputScreen();
    	}
    	catch (Exception e)
    	{
    		OccleveMobileMidlet.getInstance().onError(e);
    	}
    } */

    /**See pointerPressed().*/
    /* public void pointerDragged(int x,int y)
    {
    	try
    	{
    		lastAction = "Pointer dragged at " + x + "," + y;    		
    		m_Controller.invokeUnicodeInputScreen();
    	}
    	catch (Exception e)
    	{
    		OccleveMobileMidlet.getInstance().onError(e);
    	}
    } */
    
    public void doUpdate()
    {
        printVectorOfStrings(
        	"Q: ",
        	m_Controller.getTestController().getCurrentQuestion(),
        	m_QuestionArea);

        printVectorOfStrings(
        	"A: ",
        	m_Controller.getTestController().getCurrentAnswerFragment(),
        	m_AnswerArea);

        // If it won't encroach on the QA, display current score along the bottom.
    	// 0.9.10 - also show memory/thread stats
    	String sScoreEtc = m_Controller.getTestController().getCurrentScore();
        Runtime rt = Runtime.getRuntime();
    	sScoreEtc =
    		//// extraDebugInfo + " " + lastAction + "  " + 
    		"" + (rt.freeMemory()/1000) + "/" +
    		(rt.totalMemory()/1000) + "k " +
    		Thread.activeCount() + " " +
    		sScoreEtc;
    	m_ResultsArea.setText(sScoreEtc);
    }

    /**Prints a Vector of strings, putting a carriage return after each one.*/
    protected void printVectorOfStrings
    (
       String sPrefix,Vector vPrintMe,TextArea ta
    )
    {
        ta.setText(sPrefix);

        for (int i=0; i<vPrintMe.size(); i++)
        {
            String sLine = (String)vPrintMe.elementAt(i);
            ta.setText(ta.getText() + sLine + "\n");
        }
    }

    /**Implementation of MagicTypewriterView method.*/
    public Object getDisplayable()
    {
        return this;
    }

    /**Implementation of MagicTypewriterView method.*/
    public void doRepainting()
    {
        doUpdate();
        
        Display.getInstance().invokeAndBlock(new Runnable() {
            public void run() {repaint();}
        });        
    }

    private void trace(String s)
    {
        //// System.out.println(s);
    }

    /**No need to clone itself.*/
    public QuestionView perhapsClone() throws Exception
    {
        return this;
    }
    
    public void actionCommand(Command c)
    {
    	// m_Controller.getTestController().actionCommand(c);    	
    }
}

