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

package org.occleve.mobileclient.testing;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.testing.test.*;

public class SequentialMagicTypewriter extends MagicTypewriterController
{
    protected Command m_JumpToCommand;
    protected Command m_SkipForwardCommand;

    public SequentialMagicTypewriter(Test theTest,
                              QADirection direction,boolean bFormView)
    throws Exception
    {
        super(theTest,direction,bFormView);

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
            JumpToForm jtForm = new JumpToForm( this);
            OccleveMobileMidlet.getInstance().setCurrentForm(jtForm);
        }
        else
        {
            super.commandAction(c,s);
        }

    }

    /*Implementation of TestForm.nextQuestion().*/
    public void moveToNextQuestion()
    {
        if (m_iCurrentQAIndex < (m_Test.getQACount()-1))
        {
            m_iCurrentQAIndex++;
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
}

