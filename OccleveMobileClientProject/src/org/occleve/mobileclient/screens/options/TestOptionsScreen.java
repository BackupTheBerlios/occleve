/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-2011  Joe Gittings

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

package org.occleve.mobileclient.screens.options;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

public class TestOptionsScreen extends Form implements ActionListener,Runnable
{
    protected Test m_Test;

    protected Button m_StartTestItem = new Button("Start test");

    protected String SEQUENTIAL = "In sequence";
    protected String RANDOM = "Random";
    protected ComboBox m_SequentialOrRandomChoiceGroup;

    protected CheckBox m_MnemonicCheckbox = new CheckBox("Show mnemonics");

    // Start From Question No field for sequential mode
    protected TextField m_FirstQuestionTextField = new TextField();
    protected TextField m_LastQuestionTextField = new TextField();
    protected TextField m_RestartOnPercentageBelowTextField = new TextField();
    
    protected CommonCommands m_CommonCommands;
    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    protected ProgressAlert m_ProgressAlert;
    
    /**Does nothing except in the derived class.*/
    protected void addSubclassControls() throws Exception {}

    public TestOptionsScreen() throws Exception
    {
        super(Constants.PRODUCT_NAME);

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        Font font = Font.createSystemFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN, Font.SIZE_SMALL);
        getStyle().setFont(font);

        m_CommonCommands = new CommonCommands();
        m_CommonCommands.addToForm(this);

        m_OKCommand = new Command("OK",0);
        m_CancelCommand = new Command("Cancel",0);

        addCommand(m_OKCommand);
        addCommand(m_CancelCommand);
        //setCommandListener(this);

        // Append items to this form.

        addComponent(m_StartTestItem);
        //m_StartTestItem.setItemCommandListener(this);
        //m_StartTestItem.setDefaultCommand(m_OKCommand);
        m_StartTestItem.addActionListener(this);

        String[] orderChoices = {SEQUENTIAL,RANDOM};
        m_SequentialOrRandomChoiceGroup = new ComboBox(orderChoices);
        addComponent(m_SequentialOrRandomChoiceGroup);
        m_SequentialOrRandomChoiceGroup.getStyle().setFont(font);

        addComponent(m_MnemonicCheckbox);
        
        // Give the derived class a chance to add other controls.
        addSubclassControls();

        addPromptAndNumField("Question to start from:",
        		m_FirstQuestionTextField,"1");

        addPromptAndNumField("Question to end at:",
        		m_LastQuestionTextField,"1");

        addPromptAndNumField("Restart if score below:",
        		m_RestartOnPercentageBelowTextField,"0");
    }

    private void addPromptAndNumField(String sPrompt,TextField field,String sInitialValue)
    {
        field.setConstraint(TextField.NUMERIC);
        field.setInputModeOrder(new String[] {"123"});

        Label prompt = new Label(sPrompt);
        Container promptAndField = new Container(new BoxLayout(BoxLayout.X_AXIS));
        promptAndField.addComponent(prompt);
        promptAndField.addComponent(field);
        addComponent(promptAndField);
        field.setText(sInitialValue);
    }
    
    protected void actionCommand(Command c)
    {
        if (c==m_OKCommand)
        {
        	createRunTestThread();
        }
        else if (c==m_CancelCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
        else
        {
        	m_CommonCommands.actionCommand(c);
        }
    }

    protected void createRunTestThread()
    {    	
        try
        {
        	QA firstQA = m_Test.getQA(0);
        	if (firstQA instanceof SageQA)
        	{
                m_ProgressAlert =
                	new ProgressAlert("","Evaluating solutions for maths QAs");
                OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);
        	}
        	else {
        		m_ProgressAlert = null;
        	}
        	
        	new Thread(this).start();
        }
        catch (Exception e) {
        	OccleveMobileMidlet.getInstance().onError(
        		"TestOptionsScreen.createRunTestThread",e);
        }
    }

    /**Implementation of Runnable.*/
    public void run()
    {
    	try {
    		runTest();
    	}
    	catch (Exception e) {
    		OccleveMobileMidlet.getInstance().onError(
    			"TestOptionsScreen.run: gotTo=" + gotTo,e);
    	}
    }
    
public static String gotTo = "start";    
    
    protected void runTest() throws Exception
    {    	
        String sChoice = (String)m_SequentialOrRandomChoiceGroup.getSelectedItem();
        boolean bRandom = (sChoice.equals(RANDOM));
    	
        QADirection direction = getQADirection();

gotTo = "created QADirection";

        boolean mnemonics = m_MnemonicCheckbox.isSelected();

gotTo = "checked mnemnoic checkbox";        
        
    	String sFirstQuestion = m_FirstQuestionTextField.getText();
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

gotTo = "got first question";    	
    	
    	String sLastQuestion = m_LastQuestionTextField.getText();
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

gotTo = "got last question";    	

    	String sMinScore = m_RestartOnPercentageBelowTextField.getText();
    	int iMinScore;
    	try
    	{
    		iMinScore = Integer.parseInt(sMinScore);
    	}
    	catch (Exception e)
    	{
    		System.err.println("Invalid value in textfield, setting to zero");
    		iMinScore = 0;
    	}

gotTo = "got min score";    	

        TestController tc;
        if (bRandom)
        {
            tc = new RandomTestController(m_Test,direction,
            		iFirstQuestion-1,iLastQuestion-1,iMinScore,
            		mnemonics,m_ProgressAlert);
        }
        else
        {        	        	
        	tc = new SequentialTestController(m_Test,direction,
        			iFirstQuestion-1,iLastQuestion-1,iMinScore,
        			mnemonics,m_ProgressAlert);
        }

gotTo = "create test controller";    	

        tc.setVisible();
    }

    public void makeVisible(Test test)
    {
m_Test = null;
    	
    	setTitle(test.getFilename());
    	
    	// If the user is running a NEW test, reset the first and last
    	// question text fields to 1 and the max value respectively.
    	boolean bResetFields = true;
    	if (test!=null && m_Test!=null)
    	{
        	bResetFields = ! (test.getFilename().equals(m_Test.getFilename()));
    	}
    	
    	if (bResetFields)
    	{
    		System.out.println("Resetting first and last question fields");
    		m_Test = test;
    		m_FirstQuestionTextField.setText("1");    		
    		String sValue = new Integer(m_Test.getQACount()).toString();
    		m_LastQuestionTextField.setText(sValue);    		
    	}
    	
        OccleveMobileMidlet.getInstance().setCurrentForm(this);
		show();

		/*
        try
        {
        	  Resources r = Resources.open("/javaTheme.res");
        	  UIManager.getInstance().setThemeProps(r.getTheme("javaTheme"));
              Display.getInstance().getCurrent().refreshTheme();
    	}
        catch (Exception e)
        {
        	System.out.println("Couldn't load theme.");
        	OccleveMobileMidlet.getInstance().onError(e);
    	}
    	*/
    }

    /**Implementation of ActionListener.*/
    public void actionPerformed(ActionEvent ae)
    {
        try
        {
            if (ae.getSource()==(Object)m_StartTestItem)
            {
                createRunTestThread();
            }
        }
        catch (Exception e) {
        	OccleveMobileMidlet.getInstance().onError(
        		"TestOptionsScreen.actionPerformed()",e);
        }
    }

    /*
    //// DISABLED MAY 1ST.... “FIRST QUESTION TEXT FIELD SHOULD BE AVAILABLE
    //// IN RANDOM TEST MODE TOO.
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
    */

    /*
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
   */
    
   protected QADirection getQADirection() throws Exception
   {
       boolean bReverse = false;
       return new SimpleQADirection(false);
   }

}

