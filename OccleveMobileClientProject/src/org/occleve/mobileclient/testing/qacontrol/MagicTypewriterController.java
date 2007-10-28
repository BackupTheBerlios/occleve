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
@version 0.9.4
*/

package org.occleve.mobileclient.testing.qacontrol;

import javax.microedition.lcdui.*;
import java.util.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.qaview.*;
import org.occleve.mobileclient.testing.test.*;

public class MagicTypewriterController
{
    protected TestController m_TestController;
    protected Test m_Test;
    protected QADirection m_QADirection;
    protected TestResults m_TestResults;

    /////protected int m_iCurrentQAIndex;
    /////protected QuestionView m_TestController.getQuestionView();

    public void appendToAnswerFragment(char c) throws Exception
    {
        m_TestController.getCurrentQA().appendToAnswerFragment(c);
    }

    public MagicTypewriterController(TestController tc,Test theTest,
                                     QADirection direction,
                                     TestResults testResults)
    throws Exception
    {
        m_TestController = tc;
        m_Test = theTest;
        m_QADirection = direction;
        m_TestResults = testResults;
    }

    public TestController getTestController()
    {
        return m_TestController;
    }

    public Vector getCurrentAnswerFragment()
    {
        QA qa = m_TestController.getCurrentQA();
        return qa.getAnswerFragment();
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
            m_TestController.getCurrentQA().nextPossibleCharsAreUnicode();
        int iCount =
            m_TestController.getCurrentQA().getNextPossibleCharsCount();

        if (bUnicode && (iCount==1) && (bIsCheatKey == false))
        {
        	invokeUnicodeInputScreen();
        }
        else
        {
            processKeypress(keyCode);

            // Always need to do a repaint since the interim test results
            // will have been updated.
            m_TestController.getQuestionView().doRepainting();
        }
    }

    public void invokeUnicodeInputScreen() throws Exception
    {
    	// Deal gracefully with multiple calls in quick succession.
        Displayable current =
        	OccleveMobileMidlet.getInstance().getCurrentDisplayable();
        if (current instanceof UnicodeInputScreen) return;
            	
        Vector v = m_TestController.getCurrentQA().getNextPossibleChars();
        Character c = (Character)v.elementAt(0);
        char desiredChar = c.charValue();

        ////////////// TO DO - allow UnicodeInputScreen to take
        /////////////          multiple desired chars
        UnicodeInputScreen uis =
                new UnicodeInputScreen(desiredChar,this,m_TestResults);
        OccleveMobileMidlet.getInstance().setCurrentForm(uis);    	
    }
    
    protected void processKeypress(int iKeycode) throws Exception
    {
        // Star is the cheat key for single characters,
        // hash/pound for the rest of the line.

        Character matchingChar = possibleCharThatKeypressEquals(iKeycode);

        if (matchingChar!=null)
        {
            trace("Appending " + matchingChar.charValue());

            m_TestController.getCurrentQA().appendToAnswerFragment(matchingChar.charValue());
            m_TestResults.addResponse(true);
        }
        else if (iKeycode==Canvas.KEY_STAR) // Star key
        {
            cheatOneCharacter();
            m_TestResults.addResponse(false);
        }
        else if (iKeycode==Canvas.KEY_POUND) // Hash key
        {
            cheatQuestion();
        }
        else
        {
            // Incorrect keypress - perhaps do something to indicate this.
            onIncorrectKeypress();
            m_TestResults.addResponse(false);
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
        Vector vPossibleChars =
            m_TestController.getCurrentQA().getNextPossibleChars();
        Character cFirstPoss = (Character) vPossibleChars.elementAt(0);
        char firstPossChar = cFirstPoss.charValue();

        m_TestController.getCurrentQA().appendToAnswerFragment(firstPossChar);
    }

    private void cheatQuestion() throws Exception
    {
        Vector vWholeAnswer = m_TestController.getCurrentQA().getAnswer();
        int wholeAnswerLength = totalLengthOfStrings(vWholeAnswer);

        Vector vAnswerFrag = m_TestController.getCurrentQA().getAnswerFragment();
        int fragmentLength = totalLengthOfStrings(vAnswerFrag);

        m_TestResults.addResponses(false, wholeAnswerLength - fragmentLength);

        // Do it this way instead of using moveToNextQuestion(), because
        // then we'll get a short pause before moving to the next question.
        m_TestController.getCurrentQA().setAnswerFragment(vWholeAnswer);
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
            m_TestController.getCurrentQA().getNextPossibleChars();

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
        if (m_TestController.getCurrentQA().isAnswered())
        {
            m_TestController.getQuestionView().doRepainting();
            // serviceRepaints();
            Thread.sleep(1000);
            m_TestController.moveToNextQuestion();
            return;
        }

        Vector vNPCSanityCheck = m_TestController.getCurrentQA().getNextPossibleChars();
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
            if (m_TestController.getCurrentQA().isAnswered())
            {
                m_TestController.getQuestionView().doRepainting();
                // serviceRepaints();
                Thread.sleep(1000);
                m_TestController.moveToNextQuestion();
            }

            bSkippedPunctuation = false;

            Vector vNextPossibleChars = m_TestController.getCurrentQA().getNextPossibleChars();
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

                m_TestController.getCurrentQA().appendToAnswerFragment(nextChar);
                m_TestController.getQuestionView().doRepainting();
                bSkippedPunctuation = true;
            }
        } while (bSkippedPunctuation);
    }

    public void setVisible()
    {
        Displayable disp = m_TestController.getQuestionView().getDisplayable();
        OccleveMobileMidlet.getInstance().setCurrentForm(disp);
        m_TestController.getQuestionView().doRepainting();
    }

    /**So that trace output from this class can be easily switched on/off.*/
    protected void trace(Object o)
    {
        //System.out.println(o);
    }
}

