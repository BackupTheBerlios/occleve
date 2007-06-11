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

package org.occleve.mobileclient.qa.language;

import com.exploringxml.xml.*;
import java.util.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.languageentity.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.screens.*;

/**A QA class designed specifically for learning languages, with
separate fields for the romanized form, native script form, etc.*/
public class LanguageQA extends QA
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

        trace("Entering LanguageQA constructor");
        trace("With qaNode.name = " + qaNode.name);

        Node firsteseNode = qaNode.findFirst(sFirsteseCode);
        trace("Firstese node = " + firsteseNode);

        Node secondeseNode = qaNode.findFirst(sSecondeseCode);
        trace("Secondese node = " + secondeseNode);

        // Process English entities
        Vector vChildren = firsteseNode.getContents();
        for (int i=0; i<vChildren.size(); i++)
        {
            Node child = (Node)vChildren.elementAt(i);

            // XParser-J currently returning newlines as child nodes
            // with null names... hence this check.
            if (child.name!=null)
            {
                if (XML.isClosingTag(child.name,sFirsteseCode)==false)
                {
                    trace("Firstese child.name = " + child.name);
                    LanguageEntity entity =
                            LanguageEntity.make(child,firsteseNode);
                    trace("Made firstese entity " + entity);
                    m_vFirsteseEntities.addElement(entity);
                }
            }
        }

        // Process Secondese entities
        vChildren = secondeseNode.getContents();
        for (int i=0; i<vChildren.size(); i++)
        {
            Node child = (Node)vChildren.elementAt(i);

            // XParser-J currently returning newlines as child nodes
            // with null names... hence this check.
            if (child.name!=null)
            {
                trace("Secondese: child.name = " + child.name);
                if (XML.isClosingTag(child.name,sSecondeseCode)==false)
                {
                    LanguageEntity entity =
                            LanguageEntity.make(child,secondeseNode);
                    m_vSecondeseEntities.addElement(entity);
                    trace("Made Secondese entity " + entity);
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
            return getAllFirsteseRoman(bIncludeMW);
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
            return getAllFirsteseRoman(bIncludeMW);
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
        Vector vChars = new Vector();

        String sLastLine = (String)m_vAnswerFragment.lastElement();
        Enumeration e = m_vUnansweredLines.elements();
        while (e.hasMoreElements())
        {
            String sUnansweredLine = (String)e.nextElement();
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
    /*
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
    */

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

    /**0.9.3: Moved the code in this function that added indefinite articles
     and verbal "to" into the CountableNoun and Verb classes respectively
     (the proper place for it).*/
    protected Vector getAllFirsteseRoman(boolean bIncludeMeasureWords)
    {
        Vector v = new Vector();
        for (int i = 0; i < m_vFirsteseEntities.size(); i++)
        {
            LanguageEntity eng =
                    (LanguageEntity) m_vFirsteseEntities.elementAt(i);
            String sLine = eng.getRomanForm(bIncludeMeasureWords);
            v.addElement(sLine);
        }

        return v;
    }

    /**Implementation of abstract function in QA class.*/
    public String getEntireContentsAsString()
    {
        return "DEFUNCT FUNCTION???";
    }

    /**Implementation of abstract function in QA class.*/
    public Vector getEntireContentsAsItems()
    {
        Vector vItems = new Vector();
        boolean bAudioClips = false;

        for (int i=0; i<m_vFirsteseEntities.size(); i++)
        {
            LanguageEntity leFirstese =
               (LanguageEntity)m_vFirsteseEntities.elementAt(i);
            languageEntityToStringItems(leFirstese,vItems);

            if (leFirstese.hasAudioClip()) bAudioClips = true;
        }

        for (int i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity leSecondese =
               (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            languageEntityToStringItems(leSecondese,vItems);

            if (leSecondese.hasAudioClip()) bAudioClips = true;
        }

        // If there are no audio clips, return the QA as a single StringItem,
        // as some phones have an upper limit on the number
        // of Items on a Form.
        if (bAudioClips)
        {
            return vItems;
        }
        else
        {
            StringBuffer sbMerge = new StringBuffer();
            for (int i=0; i<vItems.size(); i++)
            {
                StringItem si = (StringItem)vItems.elementAt(i);
                sbMerge.append(si.getText());
            }

            StringItem siSingle = new StringItem(null,sbMerge.toString());
            Vector vSingle = new Vector();
            vSingle.addElement(siSingle);
            return vSingle;
        }
    }

    protected void languageEntityToStringItems(LanguageEntity le,Vector vAppendTo)
    {
        StringBuffer sb = new StringBuffer();

        String sRoman = le.getRomanForm(true);
        if (sRoman!=null) sb.append(sRoman);

        String sScript = le.getNativeForm(true);
        if (sScript!=null)
        {
            if (sRoman!=null) sb.append(" (");
            sb.append(sScript);
            if (sRoman!=null) sb.append(")");
        }

        String sLiteral = le.getLiteralTranslation();
        if (sLiteral!=null)
        {
            if (sLiteral.length()>0) sb.append(", lit. " + sLiteral);
        }

        StringItem item1 = new StringItem(null,sb.toString());
        vAppendTo.addElement(item1);

        String sAudioFilename = le.getAudioFilename();
        if (sAudioFilename!=null)
        {
            ListenItem item2 = new ListenItem(sAudioFilename);
            vAppendTo.addElement(item2);
        }

        // Add a newline to the last Item for this language entity.
        StringItem siLast = (StringItem)vAppendTo.lastElement();
        siLast.setText( siLast.getText() + Constants.NEWLINE );
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
