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

package org.occleve.mobileclient.qa.wikiversity;

import java.util.*;
import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;

/**The QA class for multiple choice questions in the wikiversity format.*/
public class MultipleChoiceWikiversityQA extends WikiversityQA
{
    static final Boolean RIGHT = new Boolean(true);
    static final Boolean WRONG = new Boolean(false);

    /**All answers, as String objects, in the order in which they are
     listed in the test.*/
    protected Vector m_vAllAnswers;

    /**Contains boolean objects.*/
    protected Vector m_vIsAnswerCorrect;

    /**Contains String objects.*/
    protected Vector m_vCorrectAnswers;

    /**Contains String objects.*/
    protected Vector m_vIncorrectAnswers;

    /**Load the QA from wikitext. Maximal format is:
    {Question
    |type="[]"}
    + Correct answer.
    - Incorrect answer.
    + Correct answer.
    - Incorrect answer.
    */
    public MultipleChoiceWikiversityQA(String sQuestion,
                                       VectorReader answerLines)
    throws Exception
    {
        super(sQuestion);
        m_vAllAnswers = new Vector();
        m_vIsAnswerCorrect = new Vector();
        m_vCorrectAnswers = new Vector();
        m_vIncorrectAnswers = new Vector();

        String sLine;
        do
        {
            sLine = answerLines.readLine().trim();
            System.out.println("Parsing:" + sLine);

            if (sLine.length() > 0)
            {
                String sRemainder = sLine.substring(1);
                sRemainder = stripWikiMarkup(sRemainder);

                char firstChar = sLine.charAt(0);
                switch (firstChar)
                {
                case '{':
                    throw new Exception("Error! Unexpected start of question");
                case '+':
                    m_vAllAnswers.addElement(sRemainder);
                    m_vIsAnswerCorrect.addElement(RIGHT);
                    m_vCorrectAnswers.addElement(sRemainder);
                    break;
                case '-':
                    m_vAllAnswers.addElement(sRemainder);
                    m_vIsAnswerCorrect.addElement(WRONG);
                    m_vIncorrectAnswers.addElement(sRemainder);
                    break;
                }
            }
        } while (sLine.length() > 0);
    }

    public Vector getAnswer()
    {
        return null;
    }

    /**Compares the answer and the answer fragment vectors to see what
    are the next possible chars.*/
    public Vector getNextPossibleChars()
    {
        return null;
    }

    public boolean containsString(String s)
    {
        return false;
    }

    public String getEntireContentsAsString()
    {
        return "TODO";
    }


    /**Implementation of QA.toXML()*/
    public String toXML()
    {
        return null;
    }

    /**For ease of switching trace output on and off.*/
    private void trace(String s)
    {
        //System.out.println(s);
    }

    public Vector getAllAnswers() {return m_vAllAnswers;}

    public boolean isAnswerCorrect(int iIndex)
    {
        Boolean bCorrect =
            (Boolean)m_vIsAnswerCorrect.elementAt(iIndex);
        return bCorrect.booleanValue();
    }

    public int getFirstCorrectIndex() throws Exception
    {
        for (int i=0; i<m_vAllAnswers.size(); i++)
        {
            if (isAnswerCorrect(i)) return i;
        }

        String sErr = "Error! No correct answer in MultipleChoiceWikiversityQA";
        throw new Exception(sErr);
    }

    public String getFirstCorrectAnswer() throws Exception
    {
        for (int i=0; i<m_vAllAnswers.size(); i++)
        {
            if (isAnswerCorrect(i))
            {
                String sAnswer = (String)m_vAllAnswers.elementAt(i);
                return sAnswer;
            }
        }

        String sErr = "Error! No correct answer in MultipleChoiceWikiversityQA";
        throw new Exception(sErr);
    }

    /**Implementation of abstract function in QA class.*/
    public Vector getEntireContentsAsItems()
    {
        Vector items = new Vector();

        StringItem qItem = new StringItem(null,m_sQuestion);
        items.addElement(qItem);

        for (int i=0; i<m_vCorrectAnswers.size(); i++)
        {
            String sAnswer = (String)m_vCorrectAnswers.elementAt(i);
            StringItem aItem = new StringItem(null,sAnswer);
            items.addElement(aItem);
        }

        return items;
    }

}

