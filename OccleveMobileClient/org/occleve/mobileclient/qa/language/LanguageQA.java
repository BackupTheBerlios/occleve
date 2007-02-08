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

package org.occleve.mobileclient.qa.language;

import com.exploringxml.xml.*;
import java.util.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.languageentity.*;
import org.occleve.mobileclient.languageentity.chinese.*;
import org.occleve.mobileclient.qa.*;

/**A QA class designed specifically for learning languages, with
separate fields for the romanized form, native script form, etc.*/
public abstract class LanguageQA extends QA
{
    /**Eg. EN*/
    protected String m_sFirsteseCode;

    /**Eg. ZH*/
    protected String m_sSecondeseCode;

    protected Vector m_vFirsteseEntities = new Vector();
    protected Vector m_vSecondeseEntities = new Vector();

    /**Load a LanguageQA from an XML file.*/
    public LanguageQA(Node qaNode,
                      String sFirsteseCode,String sSecondeseCode)
    throws Exception
    {
        m_sFirsteseCode = sFirsteseCode;
        m_sSecondeseCode = sSecondeseCode;

        trace("Entering CQA constructor");
        trace("With qaNode.name = " + qaNode.name);

        Node englishNode = qaNode.findFirst(sFirsteseCode);
        trace("English node = " + englishNode);

        Node chineseNode = qaNode.findFirst(sSecondeseCode);
        trace("Chinese node = " + chineseNode);

        // Process English entities
        Vector vChildren = englishNode.getContents();
        for (int i=0; i<vChildren.size(); i++)
        {
            Node child = (Node)vChildren.elementAt(i);

            // XParser-J currently returning newlines as child nodes
            // with null names... hence this check.
            if (child.name!=null)
            {
                if (XML.isClosingTag(child.name,XML.EN)==false)
                {
                    trace("English child.name = " + child.name);
                    LanguageEntity entity =
                            LanguageEntity.make(child,englishNode);
                    trace("Made english entity " + entity);
                    m_vFirsteseEntities.addElement(entity);
                }
            }
        }

        // Process Chinese entities
        vChildren = chineseNode.getContents();
        for (int i=0; i<vChildren.size(); i++)
        {
            Node child = (Node)vChildren.elementAt(i);

            // XParser-J currently returning newlines as child nodes
            // with null names... hence this check.
            if (child.name!=null)
            {
                trace("Chinese: child.name = " + child.name);
                if (XML.isClosingTag(child.name,XML.ZH)==false)
                {
                    LanguageEntity entity =
                            LanguageEntity.make(child,chineseNode);
                    m_vSecondeseEntities.addElement(entity);
                    trace("Made chinese entity " + entity);
                }
            }
        }
    }

    /**Deprecated - loads from the old format file.
    Code now moved to a static helper class in order to keep this
    class tidier.*/
    public LanguageQA(VectorReader in) throws Exception
    {
        m_sFirsteseCode = "EN";
        m_sSecondeseCode = "ZH";
        LanguageQA_OldFormatLoader.load(this,in);
    }

    public Vector getQuestion()
    {
        LanguageQADirection cqaDir = (LanguageQADirection)m_QADirection;
        boolean bIncludeMW = cqaDir.includeMeasureWords();

        if (cqaDir.isQuestionPinyin())
            return getAllRomanForms(bIncludeMW);
        else if (cqaDir.isQuestionEnglish())
            return getAllEnglish(bIncludeMW);
        else if (cqaDir.isQuestionChars())
            return getAllCharacters(bIncludeMW);
        else if (cqaDir.isQuestionCharsAndPinyin())
            return getAllPinyinAndChars(bIncludeMW);
        else
        {
            String sError = "NOT IMPLEMENTED YET!";
            Vector v = new Vector();
            v.addElement(sError);
            return v;
        }
    }

    public Vector getAnswer()
    {
        LanguageQADirection cqaDir = (LanguageQADirection)m_QADirection;
        boolean bIncludeMW = cqaDir.includeMeasureWords();

        if (cqaDir.isAnswerPinyin())
            return getAllRomanForms(bIncludeMW);
        else if (cqaDir.isAnswerEnglish())
            return getAllEnglish(bIncludeMW);
        else if (cqaDir.isAnswerChars())
            return getAllCharacters(bIncludeMW);
        else if (cqaDir.isAnswerCharsAndPinyin())
            return getAllPinyinAndChars(bIncludeMW);
        else
        {
            String sError = "NOT IMPLEMENTED YET!";
            Vector v = new Vector();
            v.addElement(sError);
            return v;
        }
    }

    /**Compares the answer and the answer fragment vectors to see what
    are the next possible chars.*/
    public Vector getNextPossibleChars()
    {
        /*
        boolean bAtStartOfNewLine = false;
        String sLastLine = (String)m_vAnswerFragment.lastElement();
        Enumeration eAnswer = getAnswer(dirn).elements();
        while (eAnswer.hasMoreElements())
        {
            String sAnswerLine = (String)eAnswer.nextElement();
            if (sLastLine.equals( sAnswerLine ))
            {
                bAtStartOfNewLine = true;
                break;
            }
        }
        */

        Vector vChars = new Vector();

        String sLastLine = (String)m_vAnswerFragment.lastElement();
        Enumeration e = m_vUnansweredLines.elements();
        while (e.hasMoreElements())
        {
            String sUnansweredLine = (String)e.nextElement();

//System.out.println("In getNextPossibleChars: sUnansweredLine = " +
//                               sUnansweredLine);

            if (sUnansweredLine.startsWith(sLastLine))
            {
                char possChar = sUnansweredLine.charAt( sLastLine.length() );
                Character cPossChar = new Character(possChar);

                // TEMPORARILY DISABLED UNTIL BIGGER CHANGES MADE
                // Character cPossChar =
                //    getNextPossibleNonPunctuationChar(sLastLine,sUnansweredLine);

                if (cPossChar!=null) vChars.addElement(cPossChar);
            }
        }

//System.out.println("Returning possible chars vector of size " + vChars.size());
        return vChars;
    }

    /**Skips any punctuation (non-testable) in a matching
    unanswered line in order to find the next testable character
    in that line.*/
    private Character getNextPossibleNonPunctuationChar
            (String sAnswerFragmentLastLine,String sMatchingUnansweredLine)
    {
        int iIndex = sAnswerFragmentLastLine.length();
        char possChar;
        do
        {
            possChar = sMatchingUnansweredLine.charAt(iIndex);
            iIndex++;
        } while (StaticHelpers.isPunctuation(possChar)
                 && (iIndex<sMatchingUnansweredLine.length())   );

        if (StaticHelpers.isPunctuation(possChar)==false)
            return new Character(possChar);
        else
            return null;
    }

    public boolean containsString(String s)
    {
        int i;

        for (i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity cle =
                (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            if (cle.entityContainsString(s)) return true;
        }

        for (i=0; i<m_vFirsteseEntities.size(); i++)
        {
            LanguageEntity eng = (LanguageEntity)m_vFirsteseEntities.elementAt(i);
            String sEnglish = eng.getRomanForm(false);
            if (sEnglish.indexOf(s)!=-1) return true;
        }

        return false;
    }

    protected Vector getAllRomanForms(boolean bIncludeMeasureWords)
    {
        Vector v = new Vector();
        for (int i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity cle =
                (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            String sLine = cle.getRomanForm(bIncludeMeasureWords);
            v.addElement(sLine);
        }
        return v;
    }

    protected Vector getAllCharacters(boolean bIncludeMeasureWords)
    {
        Vector v = new Vector();
        for (int i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity cle =
                (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            String sLine = cle.getNativeForm(bIncludeMeasureWords);
            v.addElement(sLine);
        }
        return v;
    }

    protected Vector getAllPinyinAndChars(boolean bIncludeMW)
    {
        Vector v = new Vector();
        for (int i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity cle =
                (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            String sLine = cle.getRomanForm(bIncludeMW);
            String sChar = cle.getNativeForm(bIncludeMW);

            if (sChar!=null)
            {
                if (sChar.length()>0) sLine += " (" + sChar + ")";
            }

            v.addElement(sLine);
        }
        return v;
    }

    protected Vector getAllPinyinCharsAndLiteralTranslations(boolean bIncludeMW)
    {
        Vector v = new Vector();
        for (int i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity cle =
                (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            String sLine = cle.getRomanForm(bIncludeMW);
            String sChar = cle.getNativeForm(bIncludeMW);
            String sLiteral = cle.getLiteralTranslation();

            if (sChar!=null)
            {
                if (sChar.length()>0) sLine += " (" + sChar + ")";
            }

            if (sLiteral!=null)
            {
                if (sLiteral.length()>0) sLine += ", lit. " + sLiteral;
            }

            v.addElement(sLine);
        }
        return v;
    }

    /**TO DO: Need to move the code that adds indefinite articles to nouns,
    and "to" to verb, out of here and to a more elegant home.*/
    protected Vector getAllEnglish(boolean bIncludeMeasureWords)
    {
        // FUDGE: FOR NOW, LOOK AT THE FIRST LanguageEntity TO DETERMINE
        // IF THIS IS A NOUN OR VERB.
        LanguageEntity firstCLE =
                (LanguageEntity) m_vSecondeseEntities.elementAt(0);
        boolean bAddIndefiniteArticle =
            (firstCLE instanceof ChineseCountableNoun) && (bIncludeMeasureWords);
        boolean bAddVerbalTo = (firstCLE instanceof Verb);

        Vector v = new Vector();
        for (int i=0; i<m_vFirsteseEntities.size(); i++)
        {
            LanguageEntity eng = (LanguageEntity)m_vFirsteseEntities.elementAt(i);
            String sLine = eng.getRomanForm(false);

            // If this is a countable noun, and measure words are
            // required, add an
            // english indefinite article ('a' or 'an').
            if (bAddIndefiniteArticle)
            {
                char first = sLine.charAt(0);
                if (first=='a' || first=='e' || first=='i' || first=='o' || first=='u')
                    sLine = "an " + sLine;
                else
                    sLine = "a " + sLine;
            }
            else if (bAddVerbalTo) // Similarly for verbal "to".
            {
                sLine = "to " + sLine;
            }

            v.addElement(sLine);
        }
        return v;
    }

    public String getEntireContentsAsString()
    {
        Vector vEnglish = getAllEnglish(true);
        Vector vChinese = getAllPinyinCharsAndLiteralTranslations(true);

        StringBuffer sb = new StringBuffer();
        int i;

        for (i=0; i<vEnglish.size(); i++)
        {
            if (i!=0) sb.append(", ");
            String sLine = (String)vEnglish.elementAt(i);

            trace("Appending English entity: " + sLine);

            sb.append(sLine);
        }

        sb.append(Constants.NEWLINE);

        for (i=0; i<vChinese.size(); i++)
        {
            if (i!=0) sb.append(", ");
            String sLine = (String)vChinese.elementAt(i);
            sb.append(sLine);
        }

        return sb.toString();
    }

    /**Implementation of QA.toXML()*/
    public String toXML()
    {
        StringBuffer sb = new StringBuffer();
        XML.appendStartTag(sb,XML.QA);
        sb.append(Constants.NEWLINE);

        /////////////////////////////////////////////////////////
        // Do the Firstese entities.
        /////////////////////////////////////////////////////////

        XML.appendStartTag(sb,m_sFirsteseCode);
        sb.append(Constants.NEWLINE);

        for (int i=0; i<m_vFirsteseEntities.size(); i++)
        {
            LanguageEntity ent = (LanguageEntity)m_vFirsteseEntities.elementAt(i);
            sb.append( ent.toXML() );
            sb.append(Constants.NEWLINE);
        }

        XML.appendEndTag(sb,m_sFirsteseCode);
        sb.append(Constants.NEWLINE);

        /////////////////////////////////////////////////////////
        // Do the Secondese entities.
        /////////////////////////////////////////////////////////

        XML.appendStartTag(sb,m_sSecondeseCode);
        sb.append(Constants.NEWLINE);

        for (int i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity ent = (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            sb.append( ent.toXML() );
            sb.append(Constants.NEWLINE);
        }

        XML.appendEndTag(sb,m_sSecondeseCode);
        sb.append(Constants.NEWLINE);

        XML.appendEndTag(sb,XML.QA);
        return sb.toString();
    }

    /**For ease of switching trace output on and off.*/
    private void trace(String s)
    {
        //System.out.println(s);
    }

}
