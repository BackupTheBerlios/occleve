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

package org.occleve.mobileclient.testing.qacontrol;

import java.util.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

/**This class contains the logic which controls the 'magic typewriter'
question testing mode.
0.9.6: switched from matching against the "next possible chars" to matching
against the "next possible lines", i.e. checking against
getMatchingLastLinesUpToNextTestableChars(). This enables the correct
handling of multiline answers, where each line can be entered in any order
(for example, the user having to type both the pinyin and english for a hanzi).*/
public class MagicTypewriterController extends QAController
{
    protected TestController m_TestController;
    protected Test m_Test;
    protected QADirection m_QADirection;
    protected TestResults m_TestResults;

    /**0.9.7: start keeping a reference so we can shut down
    UnicodeInputScreen's thread. */
    protected UnicodeInputScreen m_UnicodeInputScreen;
    
    public void setAnswerFragmentLastLine(String sSetToThis) throws Exception
    {
        m_TestController.getCurrentQA().setAnswerFragmentLastLine(sSetToThis);
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
        trace("Key pressed = " + keyCode);
        try
        {
            onKeyPressed_Inner(keyCode);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().
            	onError("MagicTypewriterController.onKeyPressed()",e);
        }
        
        // 0.9.6 - if score drops below a user-specified threshold,
        // automatically restart the test
    	if (m_TestResults.getAccuracyPercentage() <
    			m_TestController.getMinScore())
    	{
            trace("=====================================");
            trace("Restarting because....");
            trace("Score = " + m_TestResults.getAccuracyPercentage());
            trace("Min score = " + m_TestController.getMinScore());
    		m_TestController.restart();
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
        trace("nextPossibleCharsAreUnicode() = " + bUnicode);
        
        int iCount =
            m_TestController.getCurrentQA().getNextPossibleCharsCount();
        trace("getNextPossibleCharsCount = " + iCount);

        String os = System.getProperty("microedition.platform");
                
        if (bUnicode && (iCount==1) && (bIsCheatKey == false)
        	&& (os.indexOf("microemulator-android")==-1) )
        {
        	// Only do this on J2ME, not Android
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
        if (current==m_UnicodeInputScreen) return;
            	
        Vector v = m_TestController.getCurrentQA().getMatchingLastLinesUpToNextTestableChars();
        String sFragment = (String)v.elementAt(0);
        char desiredChar = sFragment.charAt(sFragment.length()-1);

        ////////////// TO DO - allow UnicodeInputScreen to take
        /////////////          multiple desired chars
        m_UnicodeInputScreen =
                new UnicodeInputScreen(desiredChar,sFragment,this,m_TestResults);
        OccleveMobileMidlet.getInstance().setCurrentForm(m_UnicodeInputScreen);    	
    }
    
    protected void processKeypress(int iKeycode) throws Exception
    {
        // Star is the cheat key for single characters,
        // hash/pound for the rest of the line.

    	String sMatchingFragment = lastLineFragmentEndingInKeypress(iKeycode);

    	boolean skipLineCompletion = false;
        if (sMatchingFragment!=null)
        {
            trace("Setting last line of answer to: " + sMatchingFragment);

            m_TestController.getCurrentQA().setAnswerFragmentLastLine(sMatchingFragment);
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
            skipLineCompletion = true;
        }
        else
        {
            // Incorrect keypress - perhaps do something to indicate this.
            onIncorrectKeypress();
            m_TestResults.addResponse(false);
        }

        checkForLineCompletionAndQuestionCompletion(skipLineCompletion);
    }

    public void checkForLineCompletionAndQuestionCompletion() throws Exception {
    	checkForLineCompletionAndQuestionCompletion(false);
    }

    public void checkForLineCompletionAndQuestionCompletion(boolean skipLineCompletion)
    throws Exception
    {
    	if (!skipLineCompletion) {
	        // If there are no matching unanswered lines with testable characters,
	        // the current line has been completed. Move past any punctuation at the end
	        // of that line by retrieving the whole line.
	        Vector vTestableLastLines =
	            m_TestController.getCurrentQA().getMatchingLastLinesUpToNextTestableChars();
	        if (vTestableLastLines.size()==0)
	        {
	        	Vector vLastLines =
	                m_TestController.getCurrentQA().getMatchingUnansweredLines();
	        	if (vLastLines.size()!=0)
	        	{
	        		String s = (String)vLastLines.firstElement();
	                m_TestController.getCurrentQA().setAnswerFragmentLastLine(s);        		
	                m_TestController.getQuestionView().doRepainting();
	        	}
	        }
    	}
        
        // If the question's now been answered, move on.
        // Prior to 0.9.6 this was in the now-defunct skipPunctuation().
        if (m_TestController.getCurrentQA().isAnswered())
        {
        	// 0.9.7: Shut down the UnicodeInputScreen's thread and mark for
        	// garbage collection if appropriate. The failure to do this
        	// before 0.9.7 meant that on a Sony Ericsson Z558c, once
        	// the UnicodeInputScreen had been invoked, the power-hungry
        	// pen input device driver was constantly running.
        	if (m_UnicodeInputScreen!=null)
        	{
        		m_UnicodeInputScreen.setExitThread();
        		m_UnicodeInputScreen = null;
        	}
        	
            m_TestController.getQuestionView().doRepainting();
            Thread.yield();
            Thread.sleep(1000);
            m_TestController.moveToNextQuestion();
        }    	
    }
    
    protected void onIncorrectKeypress()
    {
        Display.getDisplay(OccleveMobileMidlet.getInstance()).flashBacklight(50);
    }

    public void cheatOneCharacter() throws Exception
    {
        Vector vPossibleLastLines =
            m_TestController.getCurrentQA().getMatchingLastLinesUpToNextTestableChars();
    	String sFirstPoss = (String)vPossibleLastLines.elementAt(0);

        // Do it this way instead of using moveToNextQuestion(), because
        // then we'll get a short pause before moving to the next question.
    	m_TestController.getCurrentQA().setAnswerFragmentLastLine(sFirstPoss);    	
    }

    public void cheatQuestion() throws Exception
    {
        Vector vWholeAnswer = m_TestController.getCurrentQA().getAnswer();

        //org.occleve.mobileclient.testing.qaview.
        //   MagicTypewriterFormView.debug += " vWholeAnswer.size=" + vWholeAnswer.size();
        
        int wholeAnswerLength = totalLengthOfStrings(vWholeAnswer);

        Vector vAnswerFrag = m_TestController.getCurrentQA().getAnswerFragment();
        int fragmentLength = totalLengthOfStrings(vAnswerFrag);

        m_TestResults.addResponses(false, wholeAnswerLength - fragmentLength);

        // Do it this way instead of using moveToNextQuestion(), because
        // then we'll get a short pause before moving to the next question.
        m_TestController.getCurrentQA().setAnswerFragment(vWholeAnswer);
    }

    /**Internal helper function.*/
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
    protected String lastLineFragmentEndingInKeypress(int keyCode)
    throws Exception
    {
        Vector vPossibleLastLines =
            m_TestController.getCurrentQA().getMatchingLastLinesUpToNextTestableChars();

        for (int i=0; i<vPossibleLastLines.size(); i++)
        {
        	String sPossLastLineFragment = (String)vPossibleLastLines.elementAt(i);
        	int iLastIndex = sPossLastLineFragment.length()-1;
            char possibleChar = sPossLastLineFragment.charAt(iLastIndex);
            if (keypressEqualsChar(keyCode,possibleChar))
            {
                return sPossLastLineFragment;
            }
        }

        return null;
    }

    /**MIDP: Tests whether the specified mobile phone keycode (0,1,2, etc)
    matches the supplied character.
    For example, '2' matches a,b,c,A,B,C,2.
    On Android, the keycode is just the ASCII code.*/
    protected boolean keypressEqualsChar(int iKeycode,char theChar)
    {
        trace("Entering keypressEqualsChar()");

        // On Microemulator on Android, the keycode is just the ASCII code.
        String os = System.getProperty("microedition.platform");
    	org.occleve.mobileclient.testing.qaview.
    		MagicTypewriterCanvas.extraDebugInfo = os;        
        if (os.indexOf("microemulator-android")!=-1) {

            char lowerKey = Character.toLowerCase((char)iKeycode);
            char lowerChar = Character.toLowerCase(theChar);

        	// For physical keyboard phones with shifted numerics
        	if (lowerKey=='q' && theChar=='1' ||
        		lowerKey=='w' && theChar=='2' ||
        		lowerKey=='e' && theChar=='3' ||
        		lowerKey=='r' && theChar=='4' ||
        		lowerKey=='t' && theChar=='5' ||
        		lowerKey=='y' && theChar=='6' ||
        		lowerKey=='u' && theChar=='7' ||
        		lowerKey=='i' && theChar=='8' ||
        		lowerKey=='o' && theChar=='9' ||
        		lowerKey=='p' && theChar=='0') return true;
        	
        	org.occleve.mobileclient.testing.qaview.
        		MagicTypewriterCanvas.extraDebugInfo += lowerKey + " " + lowerChar;
        	
        	return (lowerKey==lowerChar);
        }
        
        char lowercaseChar = Character.toLowerCase(theChar);
        lowercaseChar = StaticHelpers.removeAccent(lowercaseChar);
        
        trace("Testing keypress against char: " + theChar);
        trace("In lowercase and minus accent: " + lowercaseChar);

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

    public void setVisible()
    {
        Object disp = m_TestController.getQuestionView().getDisplayable();
        OccleveMobileMidlet.getInstance().setCurrentForm(disp);
        m_TestController.getQuestionView().doRepainting();
    }

    /**So that trace output from this class can be easily switched on/off.*/
    protected void trace(Object o)
    {
        System.out.println(o);
    }
}

