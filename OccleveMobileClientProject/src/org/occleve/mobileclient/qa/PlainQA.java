/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-2011  Joe Gittings

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

package org.occleve.mobileclient.qa;

import java.util.*;
import org.occleve.mobileclient.*;
import com.exploringxml.xml.Node;
import javax.microedition.lcdui.*;

public class PlainQA extends QA
{
    protected String m_sQuestion;
    protected String m_sAnswer;

    public PlainQA(Node qaNode) throws Exception
    {
        Node questionNode = qaNode.findFirst("Q");
        Node answerNode = qaNode.findFirst("A");

        m_sQuestion = questionNode.getCharacters();
        m_sAnswer = answerNode.getCharacters();
    }

    public PlainQA(String sQuestion,String sAnswer)
    {
        m_sQuestion = sQuestion;
        m_sAnswer = sAnswer;
    }

    protected Vector getQuestionMember()
    {
        Vector v = new Vector();
        v.addElement(m_sQuestion);
        return v;
    }

    protected Vector getAnswerMember()
    {
        Vector v = new Vector();
        v.addElement(m_sAnswer);
        return v;
    }

    public Vector getQuestion()
    {
        if (m_QADirection==null) return getQuestionMember();

        SimpleQADirection sqa = (SimpleQADirection)m_QADirection;
        if (sqa.isReversed())
            return getAnswerMember();
        else
            return getQuestionMember();
    }

    public Vector getAnswer()
    {
        if (m_QADirection==null) return getAnswerMember();

        SimpleQADirection sqa = (SimpleQADirection)m_QADirection;
        if (sqa.isReversed())
            return getQuestionMember();
        else
            return getAnswerMember();
    }

    /**Implementation of abstract function in class QA.*/
    public String getEntireContentsAsString()
    {
        Vector vQuestion = getQuestionMember();
        Vector vAnswer = getAnswerMember();

        String s = (String)vQuestion.elementAt(0) + Constants.NEWLINE +
                   (String)vAnswer.elementAt(0);
        return s;
    }

    /**Implementation of abstract function in class QA.*/
    public Vector getEntireContentsAsItems()
    {
        StringItem item = new StringItem(null,getEntireContentsAsString());
        Vector v = new Vector(1);
        v.addElement(item);
        return v;
    }

    public Vector getMatchingLastLinesUpToNextTestableChars()
    {
        Vector vPRA = getAnswer();
        String sPossiblyReversedAnswer = (String)vPRA.firstElement();

        String sAnswerFragment = (String)m_vAnswerFragment.firstElement();

        if (sAnswerFragment.length() == sPossiblyReversedAnswer.length())
        {
            // Whole question has been answered, so no lines.
            return new Vector();
        }
        else
        {
            int index = sAnswerFragment.length();
            ////char c = sPossiblyReversedAnswer.charAt(index);
            String sFragment = sPossiblyReversedAnswer.substring(0,index+1);

            Vector v = new Vector();
            v.addElement(sFragment);
            return v;
        }
    }

    public boolean containsString(String s)
    {
        if (m_sQuestion.indexOf(s)!=-1) return true;
        if (m_sAnswer.indexOf(s)!=-1) return true;
        return false;
    }

    /**Implementation of QA.toXML()*/
    public String toXML()
    {
        StringBuffer sb = new StringBuffer();
        XML.appendStartTag(sb,XML.QA);
        sb.append(Constants.NEWLINE);

        XML.appendStartTag(sb,"Q");
        sb.append(Constants.NEWLINE);
        sb.append(m_sQuestion + Constants.NEWLINE);
        XML.appendEndTag(sb,"Q");
        sb.append(Constants.NEWLINE);

        XML.appendStartTag(sb,"A");
        sb.append(Constants.NEWLINE);
        sb.append(m_sAnswer + Constants.NEWLINE);
        XML.appendEndTag(sb,"A");
        sb.append(Constants.NEWLINE);

        XML.appendEndTag(sb,XML.QA);
        return sb.toString();
    }
}
