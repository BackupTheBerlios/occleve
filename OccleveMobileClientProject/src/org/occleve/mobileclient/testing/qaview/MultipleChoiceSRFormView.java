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
implements ItemCommandListener,ItemStateListener,QuestionView,Runnable
{
    /**Whether this might be running
    in the J2ME MicroEmulator (microemu.org)
    (in which case this class needs to use ChoiceGroup items
    rather than StringItems).*/
    protected boolean m_bMaybeMicroEmulator;

    protected MultipleChoiceController m_Controller;

    protected Item m_QuestionItem;
    protected Vector m_vAnswerItems;
    protected StringItem m_ResultsItem;

    protected Command m_ChooseCommand;

    public MultipleChoiceSRFormView(MultipleChoiceController controller)
    throws Exception
    {
        super("");

        m_ResultsItem = new StringItem("","");

        m_ChooseCommand = new Command("Choose",Command.OK,0);
        m_Controller = controller;

        // When a user selects an answer, that choice will be
        // immediately submitted for checking.
        setItemStateListener(this);

        try
        {
            String sModel = System.getProperty("microedition.platform");
            System.out.println("microedition.platform = " + sModel);
            m_bMaybeMicroEmulator = false;
        }
        catch (Exception e)
        {
            m_bMaybeMicroEmulator = true;
        }

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
        deleteAll();

        TestController tc = m_Controller.getTestController();

        MultipleChoiceWikiversityQA wqa =
            (MultipleChoiceWikiversityQA)tc.getCurrentQA();

        // A workaround for the J2ME MicroEmulator (microemu.org).
        // That sets the scroll offset to the first selectable item,
        // meaning that if the question is a StringItem, it doesn't appear
        // by default. Which is very confusing for the user.
        // So for the MicroEmulator, use a single-item popup ChoiceGroup.
        if (m_bMaybeMicroEmulator)
        {
            ChoiceGroup cg = new ChoiceGroup(null,Choice.POPUP);
            cg.append(wqa.getQuestionString(),null);
            m_QuestionItem = cg;
        }
        else
        {
            m_QuestionItem =
               new StringItem(null,wqa.getQuestionString() + Constants.NEWLINE);
        }
        append(m_QuestionItem);

        Vector vAllAnswers = wqa.getAllAnswers();
        m_vAnswerItems = new Vector();
        for (int i=0; i<vAllAnswers.size(); i++)
        {
            String sAnswer = (String)vAllAnswers.elementAt(i);

            if (m_bMaybeMicroEmulator)
            {
                ChoiceGroup cg = new ChoiceGroup(null,Choice.MULTIPLE);
                cg.append(sAnswer,null);
                append(cg);
                m_vAnswerItems.addElement(cg);
            }
            else
            {
                StringItem si =
                    new StringItem(null, sAnswer + Constants.NEWLINE,
                                   Item.HYPERLINK);
                append(si);
                si.setDefaultCommand(m_ChooseCommand);
                si.setItemCommandListener(this);
                m_vAnswerItems.addElement(si);
            }
        }

        m_ResultsItem.setText( tc.getCurrentScore() );
        append(m_ResultsItem);
    }

    /**Implementation of ItemCommandListener.*/
    public void commandAction(Command c,Item item)
    {
        System.out.println("Entering commandAction....");

        try
        {
            onQuestionAnswered(item);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    /**Implementation of ItemStateListener.*/
    public void itemStateChanged(Item item)
    {
        System.out.println("Entering itemStateChanged....");

        try
        {
            onQuestionAnswered(item);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void onQuestionAnswered(Item answerItem) throws Exception
    {
        for (int i=0; i<m_vAnswerItems.size();i++)
        {
            Item item = (Item)m_vAnswerItems.elementAt(i);
            if (item==answerItem)
            {
                System.out.println("Selected index = " + i);
                onQuestionAnswered(i);
                return;
            }
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
            String sCorrectAnswer = wqa.getFirstCorrectAnswer();

            // TODO - do this properly (polymorphically).
            // Probably by creating a FauxStringItem class for the
            // microemulator.
            Item item = (Item)m_vAnswerItems.elementAt(iCorrectIndex);
            if (item instanceof StringItem)
            {
                StringItem si = (StringItem)item;
                si.setText("***" + sCorrectAnswer + "***" + Constants.NEWLINE);
            }
            else
            {
                ChoiceGroup cg = (ChoiceGroup)item;
                cg.set(0,"***" + sCorrectAnswer + "***",null);
            }

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

            TestController tc = m_Controller.getTestController();
            tc.moveToNextQuestion();
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    /**Needs to clone itself for the J2ME MicroEmulator.*/
    public QuestionView perhapsClone() throws Exception
    {
        return new MultipleChoiceSRFormView(m_Controller);
    }

}

