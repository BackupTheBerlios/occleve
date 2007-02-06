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

import java.util.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.testing.*;
import net.dclausen.microfloat.*;

public class RandomMagicTypewriter extends MagicTypewriterController
{
    protected Random m_Random = new Random(System.currentTimeMillis());
    protected Hashtable m_htQuestionsAsked = new Hashtable();

    public RandomMagicTypewriter(Test theTest,
                          QADirection direction,boolean bFormView)
    throws Exception
    {
        super(theTest,direction,bFormView);

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
    public void moveToNextQuestion()
    {
        Integer nextQuestionIndex =
            findRandomUnansweredQuestionIndex();

        if (nextQuestionIndex!=null)
        {
            m_htQuestionsAsked.put(nextQuestionIndex,nextQuestionIndex);

            m_iCurrentQAIndex = nextQuestionIndex.intValue();
            getCurrentQA().initialize(m_QADirection);
            m_View.doRepainting();
        }
        else
        {
            TestResultsForm resultsForm =
                new TestResultsForm(m_TestResults);
            OccleveMobileMidlet.getInstance().setCurrentForm(resultsForm);
        }
    }

    /**Returns null if all questions have been answered.*/
    protected Integer findRandomUnansweredQuestionIndex()
    {
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

        int iNoOfQuestions = m_Test.getQACount();
        int i;
        for (i = iRandomQIndex + 1; i < iNoOfQuestions; i++)
        {
            Integer ci = new Integer(i);
            if (m_htQuestionsAsked.containsKey(ci) == false)return ci;
        }

        for (i = 0; i < iRandomQIndex; i++)
        {
            Integer ci = new Integer(i);
            if (m_htQuestionsAsked.containsKey(ci) == false)return ci;
        }

        return null;
    }

    protected int randomQuestionIndex()
    {
        int iNoOfQuestions = m_Test.getQACount();

        int fpiiNoOfQuestions =
            MicroFloat.intToFloat(iNoOfQuestions);

        int fpiiMaxLong =
            MicroFloat.longToFloat(Long.MAX_VALUE);

        int fpiiFraction = MicroFloat.div(fpiiNoOfQuestions,fpiiMaxLong);

        long absRandomLong = Math.abs( m_Random.nextLong() );
        int fpiiAbsRandomLong = MicroFloat.longToFloat(absRandomLong);

        int fpiiRandomIndex = MicroFloat.mul(fpiiFraction,fpiiAbsRandomLong);

        long lRandomIndex = MicroFloat.longValue(fpiiRandomIndex);
        int iRandomIndex = (int)lRandomIndex;
        return iRandomIndex;
    }
}

