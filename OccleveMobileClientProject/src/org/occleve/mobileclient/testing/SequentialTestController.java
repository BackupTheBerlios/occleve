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
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.test.*;

public class SequentialTestController extends TestController
{
    /**This isn't really applicable to a random order test.*/
    protected Command m_JumpToCommand;

    protected Command m_SkipForwardCommand;
    
    public SequentialTestController(Test theTest,QADirection direction,
    		int iFirstQuestionIndex,int iLastQuestionIndex,int iMinScore,
    		boolean showMnemonics,ProgressAlert progressAlert)
    throws Exception
    {
        super(theTest,direction,iFirstQuestionIndex,
        		iLastQuestionIndex,iMinScore,showMnemonics,progressAlert);

        int iMaxIndex = m_Test.getQACount()-1;
        if (iFirstQuestionIndex <= iMaxIndex)
            m_iCurrentQAIndex = iFirstQuestionIndex;
        else
            m_iCurrentQAIndex = 0;

        getCurrentQA().initialize(direction);

        // Labelling this as Command.BACK (it doesn't make this
        // midlet go BACK) is a cheat to make
        // it appear on the sub-bar and not on the menu,
        // ie. to make it get its own button on the phone.
        m_SkipForwardCommand = new Command(">> 5 >> forward", Command.ITEM, 1);
        m_View.getDisplayable().addCommand(m_SkipForwardCommand);

        m_JumpToCommand = new Command("Jump to", Command.ITEM, 1);
        m_View.getDisplayable().addCommand(m_JumpToCommand);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_SkipForwardCommand)
        {
            int iMaxIndex = m_Test.getQACount()-1;
            int iNewIndex = m_iCurrentQAIndex + 5;
            if (iNewIndex > iMaxIndex) iNewIndex = iMaxIndex;
            jumpToQuestion(iNewIndex);
        }
        else if (c==m_JumpToCommand)
        {
        	JumpToForm jtf = new JumpToForm( this);
            OccleveMobileMidlet.getInstance().setCurrentForm(jtf);
        }
        else
        {
            super.commandAction(c,s);
        }

    }

    /*Implementation of TestForm.nextQuestion().*/
    public void moveToNextQuestion() throws Exception
    {    	
        //////if (m_iCurrentQAIndex < (m_Test.getQACount()-1))

        if (m_iCurrentQAIndex < m_iLastQuestionIndex)
        {
            m_iCurrentQAIndex++;
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
    
    /**Implementation of TestController.getNumberOfQuestionsAsked()*/
    public int getNumberOfQuestionsAsked()
    {
    	return m_iCurrentQAIndex - m_iFirstQuestionIndex + 1;
    }    
}

