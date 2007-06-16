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
@version 0.9.3
*/

package org.occleve.mobileclient.testing.qaview;

import javax.microedition.lcdui.*;
import java.util.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.wikiversity.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.qacontrol.*;

/**For multiple choice, single response wikiversity quiz questions.*/
public class MultipleChoiceSRFormView extends Form
implements ItemStateListener,QuestionView,Runnable
{
    protected MultipleChoiceController m_Controller;

    protected StringItem m_QuestionItem;
    protected ChoiceGroup m_AnswerChoiceGroup;
    protected StringItem m_ResultsItem;

    public MultipleChoiceSRFormView(MultipleChoiceController controller)
    throws Exception
    {
        super("");

        m_QuestionItem = new StringItem("","");
        m_AnswerChoiceGroup = new ChoiceGroup("",ChoiceGroup.MULTIPLE);
        m_ResultsItem = new StringItem("","");

        m_Controller = controller;

        append(m_QuestionItem);
        append(m_AnswerChoiceGroup);
        append(m_ResultsItem);

        // When a user selects an answer, that choice will be
        // immediately submitted for checking.
        setItemStateListener(this);

        ////Command choose = new Command("Correct",Command.OK,1);
        ////m_AnswerChoiceGroup.setItemCommandListener(this);
        ////////m_AnswerChoiceGroup.addCommand(choose);
        /////m_AnswerChoiceGroup.setDefaultCommand(choose);

        populate();
    }

    /**Implementation of QuestionView method.*/
    public Displayable getDisplayable()
    {
        return this;
    }

    /**Implementation of QuestionView method.*/
    public void doRepainting()
    {
        populate();
    }

    private void populate()
    {
        TestController tc = m_Controller.getTestController();

        MultipleChoiceWikiversityQA wqa =
            (MultipleChoiceWikiversityQA)tc.getCurrentQA();

        m_QuestionItem.setText( wqa.getQuestionString() );

        Vector vAllAnswers = wqa.getAllAnswers();
        m_AnswerChoiceGroup.deleteAll();
        for (int i=0; i<vAllAnswers.size(); i++)
        {
            String sAnswer = (String)vAllAnswers.elementAt(i);
            m_AnswerChoiceGroup.append(sAnswer,null);
        }

        m_ResultsItem.setText( tc.getCurrentScore() );
    }

    /**Implementation of ItemCommandListener.*/
    ///public void commandAction(Command c,Item item)
    ///{
    ///    System.out.println("Entering commandAction....");
    ///}

    /**Implementation of ItemStateListener.*/
    public void itemStateChanged(Item item)
    {
        System.out.println("Entering itemStateChanged....");

        try
        {
            if (item == m_AnswerChoiceGroup)
            {
                int iSize = m_AnswerChoiceGroup.size();
                boolean[] iSelIndices = new boolean[iSize];
                m_AnswerChoiceGroup.getSelectedFlags(iSelIndices);

                for (int i=0; i<iSize; i++)
                {
                    if (iSelIndices[i])
                    {
                        System.out.println("Selected index = " + i);
                        onQuestionAnswered(i);
                        return;
                    }
                }

            }
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void onQuestionAnswered(int iSelectedAnswerIndex) throws Exception
    {
        TestController tc = m_Controller.getTestController();
        MultipleChoiceWikiversityQA wqa =
            (MultipleChoiceWikiversityQA)tc.getCurrentQA();

        TestResults results = tc.getTestResults();

        if (wqa.isAnswerCorrect(iSelectedAnswerIndex))
        {
            m_ResultsItem.setText("CORRECT");
            results.addResponse(true);
        }
        else
        {
            m_ResultsItem.setText("INCORRECT");
            results.addResponse(false);

            int iCorrectIndex = wqa.getFirstCorrectIndex();
            String sAnswer = m_AnswerChoiceGroup.getString(iCorrectIndex);
            m_AnswerChoiceGroup.set(iCorrectIndex,"***" + sAnswer + "***",null);

            Display d = Display.getDisplay(OccleveMobileMidlet.getInstance());
            d.flashBacklight(250);
        }

        // Start a new thread which pauses before moving onto the
        // next question. This is done in a separate thread so that
        // the phone's UI can update with the changes made above.
        new Thread(this).start();
    }

    public void run()
    {
        try
        {
            Thread.sleep(2000);
        }
        catch (Exception e) {}

        TestController tc = m_Controller.getTestController();
        tc.moveToNextQuestion();
    }


}

