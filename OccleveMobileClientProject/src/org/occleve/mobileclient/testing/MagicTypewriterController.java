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

import javax.microedition.lcdui.*;
import java.util.*;
import net.dclausen.microfloat.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.*;

public abstract class MagicTypewriterController implements CommandListener
{
    protected Test m_Test;
    protected QADirection m_QADirection;

    protected Command m_NewTestCommand;
    protected Command m_ExitCommand;
    protected Command m_JumpToCommand;
    protected Command m_SkipForwardCommand;
    protected Command m_RestartCommand;
    protected Command m_PauseCommand;
    protected Command m_EditThisQACommand;

    protected TestResults m_TestResults;

    protected int m_iCurrentQAIndex;
    protected MagicTypewriterView m_View;

    public void appendToAnswerFragment(char c) throws Exception
    {
        getCurrentQA().appendToAnswerFragment(c);
    }

    public MagicTypewriterController(Test theTest,
                    QADirection direction,boolean bFormView)
    throws Exception
    {
        m_Test = theTest;
        m_QADirection = direction;

        m_TestResults = new TestResults();

        if (bFormView)
            m_View = new MagicTypewriterFormView(this);
        else
            m_View = new MagicTypewriterCanvas(this);

        Displayable disp = m_View.getDisplayable();

        m_NewTestCommand = new Command("New test",Command.ITEM,1);
        disp.addCommand(m_NewTestCommand);

        m_ExitCommand = new Command("Exit",Command.ITEM,1);
        disp.addCommand(m_ExitCommand);

        m_RestartCommand = new Command("Restart",Command.ITEM,1);
        disp.addCommand(m_RestartCommand);

        m_PauseCommand = new Command("Pause",Command.ITEM,1);
        disp.addCommand(m_PauseCommand);

        m_EditThisQACommand = new Command("Edit this QA",Command.ITEM,1);
        disp.addCommand(m_EditThisQACommand);

        disp.setCommandListener(this);
    }

    protected QA getCurrentQA()
    {
        return m_Test.getQA(m_iCurrentQAIndex);
    }

    protected Vector getCurrentQuestion()
    {
        QA qa = m_Test.getQA(m_iCurrentQAIndex);
        return qa.getQuestion();
    }

    protected Vector getCurrentAnswer()
    {
        QA qa = m_Test.getQA(m_iCurrentQAIndex);
        return qa.getAnswer();
    }

    protected Vector getCurrentAnswerFragment()
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
                "(" + m_TestResults.getCorrectKeypressCount() +
                "/" + m_TestResults.getTotalKeypressCount() + ") " + sTime;
        return sAccuracyDisplay;
    }

    public void onKeyPressed(int keyCode)
    {
        // trace("Key pressed = " + keyCode);
        try
        {
            onKeyPressed_Inner(keyCode);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    /**Subfunction for code clarity.*/
    private void onKeyPressed_Inner(int keyCode) throws Exception
    {
        trace("Entering keyPressed_Inner");

        boolean bIsCheatKey = (keyCode == Canvas.KEY_STAR) ||
                              (keyCode == Canvas.KEY_POUND);
        boolean bUnicode =
            getCurrentQA().nextPossibleCharsAreUnicode();
        int iCount =
            getCurrentQA().getNextPossibleCharsCount();

        if (bUnicode && (iCount==1) && (bIsCheatKey == false))
        {
            Vector v = getCurrentQA().getNextPossibleChars();
            Character c = (Character)v.elementAt(0);
            char desiredChar = c.charValue();

            ////////////// TO DO - allow UnicodeInputScreen to take
            /////////////          multiple desired chars
            UnicodeInputScreen uis =
                    new UnicodeInputScreen( desiredChar, this,
                                           m_TestResults);
            OccleveMobileMidlet.getInstance().setCurrentForm(uis);
        }
        else
        {
            processKeypress(keyCode);

            // Always need to do a repaint since the interim test results
            // will have been updated.
            m_View.doRepainting();
        }
    }

    protected void processKeypress(int iKeycode) throws Exception
    {
        // Star is the cheat key for single characters,
        // hash/pound for the rest of the line.

        Character matchingChar = possibleCharThatKeypressEquals(iKeycode);

        if (matchingChar!=null)
        {
            trace("Appending " + matchingChar.charValue());

            getCurrentQA().appendToAnswerFragment(matchingChar.charValue());
            m_TestResults.addKeypress(true);
        }
        else if (iKeycode==Canvas.KEY_STAR) // Star key
        {
            cheatOneCharacter();
            m_TestResults.addKeypress(false);
        }
        else if (iKeycode==Canvas.KEY_POUND) // Hash key
        {
            cheatQuestion();
        }
        else
        {
            // Incorrect keypress - perhaps do something to indicate this.
            onIncorrectKeypress();
            m_TestResults.addKeypress(false);
        }

        skipPunctuation();
    }

    protected void onIncorrectKeypress()
    {
        Display.getDisplay(OccleveMobileMidlet.getInstance()).flashBacklight(50);

        /*
        try
        {
            Manager.playTone(69, 200, 100);
        } catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        */
    }

    private void cheatOneCharacter() throws Exception
    {
        Vector vPossibleChars = m_Test.getQA(m_iCurrentQAIndex).getNextPossibleChars();
        Character cFirstPoss = (Character) vPossibleChars.elementAt(0);
        char firstPossChar = cFirstPoss.charValue();

        m_Test.getQA(m_iCurrentQAIndex).appendToAnswerFragment(firstPossChar);
    }

    private void cheatQuestion() throws Exception
    {
        Vector vWholeAnswer = m_Test.getQA(m_iCurrentQAIndex).getAnswer();
        int wholeAnswerLength = totalLengthOfStrings(vWholeAnswer);

        Vector vAnswerFrag = m_Test.getQA(m_iCurrentQAIndex).getAnswerFragment();
        int fragmentLength = totalLengthOfStrings(vAnswerFrag);

        m_TestResults.addKeypresses(false, wholeAnswerLength - fragmentLength);

        // Do it this way instead of using moveToNextQuestion(), because
        // then we'll get a short pause before moving to the next question.
        m_Test.getQA(m_iCurrentQAIndex).setAnswerFragment(vWholeAnswer);
    }

    private int totalLengthOfStrings(Vector vStrings)
    {
        int result = 0;
        Enumeration e = vStrings.elements();
        while (e.hasMoreElements())
        {
            String s = (String)e.nextElement();
            result += s.length();
        }
        return result;
    }

    /**From the list of characters that it's possible to type
    next in this stage of answering the question, returns the first
    one that matches this keypress. Returns null if there are no
    possible characters matching this keypress.*/
    protected Character possibleCharThatKeypressEquals(int keyCode)
    throws Exception
    {
        Vector vPossibleChars =
            m_Test.getQA(m_iCurrentQAIndex).getNextPossibleChars();

        for (int i=0; i<vPossibleChars.size(); i++)
        {
            Character possibleChar = (Character)vPossibleChars.elementAt(i);
            if (keypressEqualsChar(keyCode,possibleChar.charValue()))
            {
                return possibleChar;
            }
        }

        return null;
    }

    protected boolean keypressEqualsChar(int iKeycode,char theChar)
    {
        trace("Entering keypressEqualsChar()");

        char lowercaseChar = Character.toLowerCase(theChar);

        if (iKeycode==Canvas.KEY_NUM0)
            return (lowercaseChar=='0');
        else if (iKeycode==Canvas.KEY_NUM1)
            return (lowercaseChar=='1');
        else if (iKeycode==Canvas.KEY_NUM2)
            return ((lowercaseChar>='a' && lowercaseChar<='c') || lowercaseChar=='2');
        else if (iKeycode==Canvas.KEY_NUM3)
            return ((lowercaseChar>='d' && lowercaseChar<='f') || lowercaseChar=='3');
        else if (iKeycode==Canvas.KEY_NUM4)
            return ((lowercaseChar>='g' && lowercaseChar<='i') || lowercaseChar=='4');
        else if (iKeycode==Canvas.KEY_NUM5)
            return ((lowercaseChar>='j' && lowercaseChar<='l') || lowercaseChar=='5');
        else if (iKeycode==Canvas.KEY_NUM6)
            return ((lowercaseChar>='m' && lowercaseChar<='o') || lowercaseChar=='6');
        else if (iKeycode==Canvas.KEY_NUM7)
            return ((lowercaseChar>='p' && lowercaseChar<='s') || lowercaseChar=='7');
        else if (iKeycode==Canvas.KEY_NUM8)
            return ((lowercaseChar>='t' && lowercaseChar<='v') || lowercaseChar=='8');
        else if (iKeycode==Canvas.KEY_NUM9)
            return ((lowercaseChar>='w' && lowercaseChar<='z') || lowercaseChar=='9');
        else
            return false;
    }

    public void skipPunctuation() throws Exception
    {
        if (getCurrentQA().isAnswered())
        {
            m_View.doRepainting();
            // serviceRepaints();
            Thread.sleep(1000);
            moveToNextQuestion();
            return;
        }

        Vector vNPCSanityCheck = getCurrentQA().getNextPossibleChars();
        if (vNPCSanityCheck.size()!=1)
        {
            trace("Exiting skipPunctuation because no of poss char = " +
                               vNPCSanityCheck.size() );
            trace("They are:");
            for (int i=0; i<vNPCSanityCheck.size(); i++)
            {
                trace( vNPCSanityCheck.elementAt(i) );
            }

            return;
        }

        boolean bSkippedPunctuation;
        do
        {
            if (getCurrentQA().isAnswered())
            {
                m_View.doRepainting();
                // serviceRepaints();
                Thread.sleep(1000);
                moveToNextQuestion();
            }

            bSkippedPunctuation = false;

            Vector vNextPossibleChars = getCurrentQA().getNextPossibleChars();
            Character cNextChar = (Character)vNextPossibleChars.firstElement();
            char nextChar = cNextChar.charValue();

            boolean bIsLowercaseLetter = (nextChar>='a') && (nextChar<='z');
            boolean bIsUppercaseLetter = (nextChar>='A') && (nextChar<='Z');
            boolean bIsUnicode = (((long)nextChar) > 255);

            if ((bIsLowercaseLetter==false) && (bIsUppercaseLetter==false)
                && (Character.isDigit(nextChar)==false)
                && (bIsUnicode==false)   )
            {
                trace("-----------------------------------------");
                trace("Skipping this char: " + (long)nextChar);

                /*
                This substitution should really be done by the painting logic
                if (nextChar=='\t') m_sAnswerFragment += "   ";
                */

                getCurrentQA().appendToAnswerFragment(nextChar);
                m_View.doRepainting();
                bSkippedPunctuation = true;
            }
        } while (bSkippedPunctuation);
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
        else if (c==m_ExitCommand)
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
        else if (c==m_EditThisQACommand)
        {
            Displayable returnTo = m_View.getDisplayable();
            ExcludableHooks.editQA(
                                   m_Test.getFilename(),
                                   m_Test.getRecordStoreID(),
                                   new Integer(m_iCurrentQAIndex),
                                   returnTo);
        }
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
            jumpToQuestion(0);
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
    public abstract void moveToNextQuestion();

    /**So that trace output from this class can be easily switched on/off.*/
    protected void trace(Object o)
    {
        //System.out.println(o);
    }
}

