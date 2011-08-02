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

import java.util.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.test.*;
import net.dclausen.microfloat.*;

public class RandomTestController extends TestController
{
    protected Random m_Random = new Random(System.currentTimeMillis());
    protected Hashtable m_htQuestionsAsked = new Hashtable();

    public RandomTestController(Test theTest,QADirection direction,
                          int iFirstQuestionIndex,int iLastQuestionIndex,
                          int iMinScore,
                          boolean showMnemonics,ProgressAlert progressAlert)
    throws Exception
    {
        super(theTest,direction,iFirstQuestionIndex,iLastQuestionIndex,
        		iMinScore,showMnemonics,progressAlert);

        Integer ciIndex = findRandomUnansweredQuestionIndex();
        if (ciIndex==null)
        {
            OccleveMobileMidlet.getInstance().onError("Can't find an unanswered question at start of test!");
        }

        m_iCurrentQAIndex = ciIndex.intValue();
        getCurrentQA().initialize(m_QADirection);

        m_htQuestionsAsked.put(ciIndex,ciIndex);
    }

    /**Implementation of CommandListener.*/
    /*
    public void commandAction(Command c, Displayable s)
    {
        super.commandAction(c,s);
    }
*/

    /*Implementation of TestForm.nextQuestion().*/
    public void moveToNextQuestion() throws Exception
    {
        Integer nextQuestionIndex =
            findRandomUnansweredQuestionIndex();

        if (nextQuestionIndex!=null)
        {
            m_htQuestionsAsked.put(nextQuestionIndex,nextQuestionIndex);

            m_iCurrentQAIndex = nextQuestionIndex.intValue();
            getCurrentQA().initialize(m_QADirection);

            // A bodge for the J2ME MicroEmulator.
            m_View = m_View.perhapsClone();

            m_View.doRepainting();

            // This is needed to force updating of the UI in
            // the J2ME MicroEmulator (microemu.org).
            OccleveMobileMidlet.getInstance().setCurrentForm(m_View.getDisplayable());
        }
        else
        {
            TestResultsForm resultsForm =
                new TestResultsForm(m_Test,m_TestResults);
            OccleveMobileMidlet.getInstance().setCurrentForm(resultsForm);
        }
    }

    /**Returns null if all questions have been answered.*/
    protected Integer findRandomUnansweredQuestionIndex()
    {
    	// First, repeatedly try to find a random unanswered question.
    	int iRandomQIndex = 0;
        for (int iTry=0; iTry<10; iTry++)
        {
            iRandomQIndex = randomQuestionIndex();
            Integer ciRandomQIndex = new Integer(iRandomQIndex);

            if (m_htQuestionsAsked.containsKey(ciRandomQIndex) == false)
            {
                return ciRandomQIndex;
            }
        }

        // If that fails, find the next question after the random question index
        // that is unanswered.
        /*
        ///// 0.9.6 - disabled this
        int iNoOfQuestions = m_Test.getQACount();
        int i;
        for (i = iRandomQIndex + 1; i < iNoOfQuestions; i++)
        {
            Integer ci = new Integer(i);
            if (m_htQuestionsAsked.containsKey(ci) == false)return ci;
        }
        */

        // If that fails, find the first unanswered question in the test.
        for (int i = m_iFirstQuestionIndex; i <= m_iLastQuestionIndex; i++)
        {
            Integer ci = new Integer(i);
            if (m_htQuestionsAsked.containsKey(ci) == false)return ci;
        }

        return null;
    }

    protected int randomQuestionIndex()
    {
    	// 0.9.6 - changed to support question range entered on the test
    	// options screen.
    	
    	//// 0.9.6.....int iNoOfQuestions = m_Test.getQACount();
    	int iNoOfQuestions = m_iLastQuestionIndex - m_iFirstQuestionIndex + 1;

        int fpiiNoOfQuestions =
            MicroFloat.intToFloat(iNoOfQuestions);

        int fpiiMaxLong =
            MicroFloat.longToFloat(Long.MAX_VALUE);

        int fpiiFraction = MicroFloat.div(fpiiNoOfQuestions,fpiiMaxLong);

        long absRandomLong = Math.abs( m_Random.nextLong() );
        int fpiiAbsRandomLong = MicroFloat.longToFloat(absRandomLong);

        int fpiiRandomOffset = MicroFloat.mul(fpiiFraction,fpiiAbsRandomLong);

        long lRandomOffset = MicroFloat.longValue(fpiiRandomOffset);
        int iRandomOffset = (int)lRandomOffset;

        return m_iFirstQuestionIndex + iRandomOffset;
    }
    
    /**Implementation of TestController.getNumberOfQuestionsAsked()*/
    public int getNumberOfQuestionsAsked()
    {
    	return m_htQuestionsAsked.size();
    }       
}

