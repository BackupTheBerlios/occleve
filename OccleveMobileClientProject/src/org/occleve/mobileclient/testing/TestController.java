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

package org.occleve.mobileclient.testing;

import javax.microedition.lcdui.*;
import java.util.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.qa.wikiversity.*;
import org.occleve.mobileclient.testing.qacontrol.*;
import org.occleve.mobileclient.testing.qaview.*;
import org.occleve.mobileclient.testing.test.*;

public abstract class TestController implements CommandListener
{
    protected Test m_Test;    
    protected QADirection m_QADirection;

    protected Command m_NewTestCommand;
    protected Command m_ExitCommand;
    protected Command m_RestartCommand;
    protected Command m_PauseCommand;

    // 0.9.6
    protected Command m_TestOptionsCommand;

    // 0.9.6 - remove the Edit command - isn't working anyway, and is confusing users.
    //protected Command m_EditThisQACommand;

    protected TestResults m_TestResults;

    protected int m_iCurrentQAIndex;
    protected QuestionView m_View;

    /////////////////////////////////////////////
    // 0.9.6
    protected int m_iFirstQuestionIndex;
    protected int m_iLastQuestionIndex;
    protected int m_iMinScore;
    public int getMinScore() {return m_iMinScore;}
    /////////////////////////////////////////////
    
    public TestController(Test theTest,QADirection direction,
    		int iFirstQuestionIndex,int iLastQuestionIndex,int iMinScore)
    throws Exception
    {
    	// 0.9.6: Only include QAs in the test which contain the desired
    	// question and answer fields. For example, if testing on english-to-hanzi,
    	// make sure all the QAs actually contain hanzi.
        theTest = theTest.restrictToQADirectionTypes(direction);
    	System.out.println("Restricted test to size " + theTest.getQACount());
    	m_Test = theTest;

        m_QADirection = direction;
        m_TestResults = new TestResults();

        //////////////////// 0.9.6 ///////////////////////////////
        m_iFirstQuestionIndex = iFirstQuestionIndex;
        m_iLastQuestionIndex = iLastQuestionIndex;
        m_iMinScore = iMinScore;
        //////////////////////////////////////////////////////////
                
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
            m_View = new MagicTypewriterCanvas(mtc);
        }

        Displayable disp = m_View.getDisplayable();
        addCommandsToDisplayable(disp);
    }

    /**More than one Displayable may be used through the course of a test,
    corresponding to different question types.*/
    protected void addCommandsToDisplayable(Displayable disp)
    {
        m_NewTestCommand = new Command("New test",Command.ITEM,1);
        disp.addCommand(m_NewTestCommand);

        m_ExitCommand = new Command("Exit Occleve",Command.ITEM,1);
        disp.addCommand(m_ExitCommand);

        m_RestartCommand = new Command("Restart",Command.ITEM,1);
        disp.addCommand(m_RestartCommand);

        m_PauseCommand = new Command("Pause",Command.ITEM,1);
        disp.addCommand(m_PauseCommand);

        // Disabled in 0.9.6 - see earlier comment
        //m_EditThisQACommand = new Command("Edit this QA",Command.ITEM,1);
        //disp.addCommand(m_EditThisQACommand);

        m_TestOptionsCommand = new Command("Test options",Command.ITEM,1);
        disp.addCommand(m_TestOptionsCommand);

        disp.setCommandListener(this);
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

        String sAccuracyDisplay =
                percentage + "% " +
                "(" + m_TestResults.getCorrectResponseCount() +
                "/" + m_TestResults.getTotalResponseCount() + ") " + sTime;
        return sAccuracyDisplay;
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {      
    	try
    	{
    		commandAction_Inner(c,s);
    	}
    	catch (Exception e)
    	{
    		OccleveMobileMidlet.getInstance().onError(e);
    	}
    }

	private void commandAction_Inner(Command c, Displayable s)
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
    }

    /*Clear out the test results, and jump to the first question.*/
    public void restart()
    {
        try
        {
            m_TestResults = new TestResults();
            
            // 0.9.6
            jumpToQuestion(m_iFirstQuestionIndex);
            //jumpToQuestion(0);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
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

    /**So that trace output from this class can be easily switched on/off.*/
    protected void trace(Object o)
    {
        //System.out.println(o);
    }

    public TestResults getTestResults() {return m_TestResults;}

    public QuestionView getQuestionView() {return m_View;}
}

