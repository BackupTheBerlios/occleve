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
@version 0.9.6
*/

package org.occleve.mobileclient.screens.options;

import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

public class TestOptionsScreen extends Form
implements CommandListener,ItemCommandListener,ItemStateListener
{
    protected Test m_Test;

    protected StringItem m_StartTestItem =
            new StringItem(null,"Start test",Item.BUTTON);

    protected String SEQUENTIAL = "In sequence";
    protected String RANDOM = "Random";
    protected ChoiceGroup m_SequentialOrRandomChoiceGroup;

    // 0.9.6 - add a Start From Question No field for sequential mode
    protected TextField m_FirstQuestionTextField;
    protected TextField m_LastQuestionTextField;
    
    //// Took this out in 0.9.3 as it was just confusing users.
    //protected String CANVAS = "Canvas view";
    //protected String FORM = "Form view";
    //protected ChoiceGroup m_ViewChoiceGroup;

    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    /**Does nothing except in the derived class.*/
    protected void addSubclassControls() throws Exception {}

    public TestOptionsScreen() throws Exception
    {
        super(Constants.PRODUCT_NAME);

        m_OKCommand = new Command("OK",Command.OK,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);

        addCommand(m_OKCommand);
        addCommand(m_CancelCommand);
        setCommandListener(this);

        // Append items to this form.

        append(m_StartTestItem);
        m_StartTestItem.setItemCommandListener(this);
        m_StartTestItem.setDefaultCommand(m_OKCommand);

        String[] orderChoices = {SEQUENTIAL,RANDOM};
        m_SequentialOrRandomChoiceGroup =
            new ChoiceGroup(null,ChoiceGroup.POPUP,orderChoices,null);
        append(m_SequentialOrRandomChoiceGroup);

        // Give the derived class a chance to add other controls.
        addSubclassControls();
        
        m_FirstQuestionTextField =
        	new TextField("Question to start from:","1",10,TextField.NUMERIC);
        append(m_FirstQuestionTextField);

        m_LastQuestionTextField =
        	new TextField("Question to end at:","1",10,TextField.NUMERIC);
        append(m_LastQuestionTextField);

        setItemStateListener(this);
        
/*
ChoiceGroup dummyItem =
     new ChoiceGroup(null,Choice.MULTIPLE);
dummyItem.append("Hello",null);
dummyItem.setDefaultCommand(m_OKCommand);
dummyItem.setItemCommandListener(this);
append(dummyItem);
setItemStateListener(this);
*/

        //String[] viewChoices = {CANVAS,FORM};
        //m_ViewChoiceGroup =
        //    new ChoiceGroup(null,ChoiceGroup.POPUP,viewChoices,null);
        //append(m_ViewChoiceGroup);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_OKCommand)
        {
            try
            {
                runTest();
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else if (c==m_CancelCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command in TestOptionsScreen.commandAction");
        }
    }

    protected void runTest() throws Exception
    {
        int i = m_SequentialOrRandomChoiceGroup.getSelectedIndex();
        String sChoice = m_SequentialOrRandomChoiceGroup.getString(i);
        boolean bRandom = (sChoice.equals(RANDOM));

        QADirection direction = getQADirection();

    	String sFirstQuestion = m_FirstQuestionTextField.getString();
    	int iFirstQuestion;
    	try
    	{
    		iFirstQuestion = Integer.parseInt(sFirstQuestion);
    	}
    	catch (Exception e)
    	{
    		System.err.println("Invalid value in textfield, setting to 1");
    		iFirstQuestion = 1;
    	}

    	String sLastQuestion = m_LastQuestionTextField.getString();
    	int iLastQuestion;
    	try
    	{
    		iLastQuestion = Integer.parseInt(sLastQuestion);
    	}
    	catch (Exception e)
    	{
    		System.err.println("Invalid value in textfield, setting to max value");
    		iLastQuestion = m_Test.getQACount();
    	}

        TestController tc;
        if (bRandom)
        {
            tc = new RandomTestController(m_Test,direction,
            		iFirstQuestion-1,iLastQuestion-1);
        }
        else
        {        	        	
        	tc = new SequentialTestController(m_Test,direction,
        			iFirstQuestion-1,iLastQuestion-1);
        }

        tc.setVisible();
    }

    public void makeVisible(Test test)
    {
    	// If the user is running a NEW test, reset the first and last
    	// question text fields to 1 and the max value respectively.
    	boolean bSameFilename =
    		test.getFilename().equals(m_Test.getFilename());
    	if (!bSameFilename)
    	{
    		System.out.println("Resetting first and last question fields");
    		System.out.println("Because test = " + test);
    		System.out.println("and m_Test = " + m_Test);
    		m_Test = test;
    		m_FirstQuestionTextField.setString("1");    		
    		String sValue = new Integer(m_Test.getQACount()).toString();
    		m_LastQuestionTextField.setString(sValue);    		
    	}
    	
        OccleveMobileMidlet.getInstance().setCurrentForm(this);
    }

    /*Implementation of ItemCommandListener.*/
    public void commandAction(Command c, Item item)
    {
        try
        {
            if (item==m_StartTestItem)
            {
                OccleveMobileMidlet.getInstance().beep();
                runTest();
            }
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    public void itemStateChanged(Item item)
    {
        ////System.out.println("Entering itemStateChanged....");
    	if (item==m_SequentialOrRandomChoiceGroup)
    	{
        	int i = m_SequentialOrRandomChoiceGroup.getSelectedIndex();
            String sChoice = m_SequentialOrRandomChoiceGroup.getString(i);
            boolean bSequential = (sChoice.equals(SEQUENTIAL));
            setFirstQuestionTextFieldVisibility(bSequential);
    	}
    }

   protected void setFirstQuestionTextFieldVisibility(boolean bVisible)
   {
	   for (int i=0; i<size(); i++)
	   {
			Item matchingItem = get(i);
			if (matchingItem==m_FirstQuestionTextField)
			{
				if (bVisible==false) delete(i);
				return;
			}
	   }

	   // Couldn't find it, so the textfield isn't already in the Form.
	   if (bVisible)
	   {
	       append(m_FirstQuestionTextField);
	   }
   }
    
   protected QADirection getQADirection() throws Exception
   {
       boolean bReverse = false;
       return new SimpleQADirection(false);
   }

}

