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

package org.occleve.mobileclient.testing;

import javax.microedition.lcdui.*;
import java.util.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.qa.wikiversity.*;
import org.occleve.mobileclient.screens.ProgressAlert;
import org.occleve.mobileclient.testing.qacontrol.*;
import org.occleve.mobileclient.testing.qaview.*;
import org.occleve.mobileclient.testing.test.*;

public abstract class TestController
implements CommandListener
{
    protected Test m_Test;    
    protected QADirection m_QADirection;

    protected Command m_NewTestCommand;
    protected Command m_ExitCommand;
    protected Command m_RestartCommand;
    protected Command m_PauseCommand;
    protected Command m_TestOptionsCommand;
    //protected Command m_EditThisQACommand;

    protected TestResults m_TestResults;

    protected int m_iCurrentQAIndex;
    protected QuestionView m_View;
    
    //// protected int m_iNoOfQuestionsAnswered; // 0.9.6

    protected int m_iFirstQuestionIndex;
    protected int m_iLastQuestionIndex;
    protected int m_iMinScore;
    public int getMinScore() {return m_iMinScore;}
    
    protected boolean m_bShowMnemonics;
    public boolean getShowMnemonics() {return m_bShowMnemonics;}
    
    protected boolean m_bTestCompleted;
    public boolean isTestCompleted() {return m_bTestCompleted;}
    
    public TestController(Test theTest,QADirection direction,
    		int iFirstQuestionIndex,int iLastQuestionIndex,int iMinScore,
    		boolean showMnemonics,ProgressAlert pa)
    throws Exception
    {
    	m_bTestCompleted = false;
    	
    	for (int i=0; i<theTest.getQACount(); i++)
    	{
    		if (theTest.getQA(i) instanceof SageQA)
    		{
    			if (pa!=null) pa.setMessage("Evaluating answer " + (i+1));
    			theTest.getQA(i).getAnswer();
    		}
    	}

    	// Only include QAs in the test which contain the desired
    	// question and answer fields. For example, if testing on english-to-hanzi,
    	// make sure all the QAs actually contain hanzi.
        theTest = theTest.restrictToQADirectionTypes(direction);
    	System.out.println("Restricted test to size " + theTest.getQACount());
    	m_Test = theTest;

        m_QADirection = direction;
        m_TestResults = new TestResults();

        m_iFirstQuestionIndex = iFirstQuestionIndex;
        m_iLastQuestionIndex = iLastQuestionIndex;
        m_iMinScore = iMinScore;
        m_bShowMnemonics = showMnemonics;
                
        ///////////////////// TO FINISH //////////////////////////////
        QA firstQA = m_Test.getQA(0);
        if (firstQA instanceof MultipleChoiceWikiversityQA)
        {
            MultipleChoiceController mcc =
                    new MultipleChoiceController(this,theTest,direction,
                                                 m_TestResults);
            m_View = new MultipleChoiceSRFormView(mcc);
        }
        else
        {        	
            MagicTypewriterController mtc =
                    new MagicTypewriterController(this,theTest,direction,m_TestResults);
            m_View = new MagicTypewriterFormView(mtc);
        }

        Form frm = (Form)m_View.getDisplayable();
        addCommandsToDisplayable(frm);
    }

    /**More than one Displayable may be used through the course of a test,
    corresponding to different question types.*/
    protected void addCommandsToDisplayable(Form disp)
    {
        m_NewTestCommand = addCommand("New test",disp);
        m_ExitCommand = addCommand("Exit Occleve",disp);
        m_RestartCommand = addCommand("Restart",disp);
        m_PauseCommand = addCommand("Pause",disp);
        m_TestOptionsCommand = addCommand("Test options",disp);

        //m_EditThisQACommand = new Command("Edit this QA",Command.ITEM,1);
        //disp.addCommand(m_EditThisQACommand);
    }
    
    private Command addCommand(String label,Form frm)
    {
        Command c = new Command(label,Command.ITEM,0);
        frm.addCommand(c);
    	return c;
    }

    public QA getCurrentQA()
    {
        return m_Test.getQA(m_iCurrentQAIndex);
    }

    public Vector getCurrentQuestion()
    {
        QA qa = m_Test.getQA(m_iCurrentQAIndex);
        return qa.getQuestion();
    }

    public Vector getCurrentAnswer()
    {
        QA qa = m_Test.getQA(m_iCurrentQAIndex);
        return qa.getAnswer();
    }

    public Vector getCurrentAnswerFragment()
    {
        QA qa = m_Test.getQA(m_iCurrentQAIndex);
        return qa.getAnswerFragment();
    }

    public String getCurrentScore()
    {
        int percentage = m_TestResults.getAccuracyPercentage();
        String sTime = StaticHelpers.getDisplayableTime();
        
        // 0.9.7 - disabled since made results line too cluttered.
        // String sCorrectResponsesOutOfTotal =
        //    "(" + m_TestResults.getCorrectResponseCount() +
        //    "/" + m_TestResults.getTotalResponseCount() + ")";

        // 0.9.6 - also display the number of questions answered vs the total number to answer.
        int iTotalQuestionsToAnswer = m_iLastQuestionIndex - m_iFirstQuestionIndex + 1;
        String sCurrentQuestionVersusLast =
        	"Q" + getNumberOfQuestionsAsked() + "/" + iTotalQuestionsToAnswer;

        String sAccuracyDisplay =
                percentage + "%  " +
                /// sCorrectResponsesOutOfTotal + " " +
                sCurrentQuestionVersusLast + "  " +
        		sTime;
        return sAccuracyDisplay;
    }

    public void commandAction(Command c,Displayable d)
    {      
    	try
    	{
    		commandAction_Inner(c,d);
    	}
    	catch (Exception e)
    	{
    		OccleveMobileMidlet.getInstance().onError(
    			"TestController.commandAction",e);
    	}
    }

	private void commandAction_Inner(Command c,Displayable d)
	throws Exception
	{        
    	if (c==m_ExitCommand)
        {
            OccleveMobileMidlet.getInstance().notifyDestroyed();
        }
        else if (c==m_NewTestCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
        else if (c==m_RestartCommand)
        {
            restart();
        }
        else if (c==m_PauseCommand)
        {
            OccleveMobileMidlet.getInstance().tryToPlaceinBackground();
        }
        else if (c==m_TestOptionsCommand)
        {
    		OccleveMobileMidlet.getInstance().displayTestOptions(m_Test);
        }
        // Disabled in 0.9.6 - see earlier comment
        /*
        else if (c==m_EditThisQACommand)
        {
            Displayable returnTo =
                    OccleveMobileMidlet.getInstance().getCurrentDisplayable();
            ExcludableHooks.editQA(m_Test.getEntry(),new Integer(m_iCurrentQAIndex),returnTo);
        }
        */
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command in TestForm.commandAction");
        }
    }

    public void setVisible()
    {
        OccleveMobileMidlet.getInstance().setCurrentForm( m_View.getDisplayable() );
        m_View.doRepainting();
        
        //String os = System.getProperty("microedition.platform");
        //if (os.indexOf("microemulator-android")!=-1) {       
        //	com.sun.lwuit.Display disp = com.sun.lwuit.Display.getInstance();
        //	disp.setShowVirtualKeyboard(true);
        //}
    }

    /*Clear out the test results, and jump to the first question.*/
    public void restart()
    {
        try
        {
        	// 0.9.6 Creating a new TestResults here creates the situation
        	// where the TestController and MagicTypewriterController
        	// have different TestResults objects.
        	m_TestResults.reset();
            ////m_TestResults = new TestResults();
            
            // 0.9.6
            jumpToQuestion(m_iFirstQuestionIndex);
            //jumpToQuestion(0);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().
            	onError("TestController.restart",e);
        }
    }

    public void jumpToQuestion(int iQuestionZeroBasedIndex)
    {
        m_iCurrentQAIndex = iQuestionZeroBasedIndex;
        getCurrentQA().initialize(m_QADirection);
        setVisible();
        m_View.doRepainting();
    }

    /**Derived classes should provide appropriate behaviour in order to
    move to the next question.*/
    public abstract void moveToNextQuestion() throws Exception;

    /**Derived classes should provide appropriate behaviour.*/
    public abstract int getNumberOfQuestionsAsked();

    /**So that trace output from this class can be easily switched on/off.*/
    protected void trace(Object o)
    {
        //System.out.println(o);
    }

    public TestResults getTestResults() {return m_TestResults;}

    public QuestionView getQuestionView() {return m_View;}
}

