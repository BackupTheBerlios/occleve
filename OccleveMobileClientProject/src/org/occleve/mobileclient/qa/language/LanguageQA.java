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
        trace("Firstese language code = " + sFirsteseCode);
        trace("Secondese language code = " + sSecondeseCode);

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

    public Vector getQuestion()
    {
        LanguageQADirection cqaDir = (LanguageQADirection)m_QADirection;
        boolean bIncludeMW = cqaDir.includeMeasureWords();

        boolean bIncludeMnemonics = cqaDir.getIncludeMnemonics();

        if (cqaDir.isQuestionPinyin())
            return getAllRomanForms(bIncludeMW);
        else if (cqaDir.isQuestionEnglish())
            return getAllFirsteseRoman(bIncludeMW);
        else if (cqaDir.isQuestionChars())
            return getAllCharacters(bIncludeMW);
        else if (cqaDir.isQuestionCharsAndPinyin())
            return getAllPinyinAndChars(bIncludeMW);
        else if (cqaDir.isQuestionEnglishAndPinyin())
            return getAllEnglishAndPinyin(bIncludeMW,bIncludeMnemonics);
        else if (cqaDir.isQuestionEnglishAndChars())
            return getAllEnglishAndChars(bIncludeMW);
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
        
        boolean bIncludeMnemonics = cqaDir.getIncludeMnemonics();

        if (cqaDir.isAnswerPinyin())
            return getAllRomanForms(bIncludeMW);
        else if (cqaDir.isAnswerEnglish())
            return getAllFirsteseRoman(bIncludeMW);
        else if (cqaDir.isAnswerChars())
            return getAllCharacters(bIncludeMW);
        else if (cqaDir.isAnswerCharsAndPinyin())
            return getAllPinyinAndChars(bIncludeMW);
        else if (cqaDir.isAnswerEnglishAndPinyin())
            return getAllEnglishAndPinyin(bIncludeMW,bIncludeMnemonics);
        else if (cqaDir.isAnswerEnglishAndChars())
            return getAllEnglishAndChars(bIncludeMW);
        else
        {
            String sError = "NOT IMPLEMENTED YET!";
            Vector v = new Vector();
            v.addElement(sError);
            return v;
        }
    }

    /**Compares the answer and the answer fragment vectors to see what
    are the next possible values for the last line, including skipping any punctuation,
    and the next testable char at the end of each line.
    (Before 0.9.6 was called getNextPossibleChars() and returned a vector of Characters).*/
    public Vector getMatchingLastLinesUpToNextTestableChars()
    {
        Vector vLastLines = new Vector();

        String sLastLine = (String)m_vAnswerFragment.lastElement();
        Enumeration e = m_vUnansweredLines.elements();
        while (e.hasMoreElements())
        {
            String sUnansweredLine = (String)e.nextElement();
            if (sUnansweredLine.startsWith(sLastLine))
            {
                ////char possChar = sUnansweredLine.charAt( sLastLine.length() );
                ////Character cPossChar = new Character(possChar);

            	String sAddMe =
            		getUnansweredLineUpToNextTestableChar(sLastLine,sUnansweredLine);
            	
                if (sAddMe!=null) vLastLines.addElement(sAddMe);
            }
        }

        //System.out.println("Returning possible chars vector of size " + vChars.size());
        return vLastLines;
    }

    /**Skips any punctuation (non-testable) in a matching
    unanswered line in order to find the next testable character
    in that line, and then returns the line up to and including that char.
    If the rest of the line is punctuation, returns null.*/
    private String getUnansweredLineUpToNextTestableChar
            (String sAnswerFragmentLastLine,String sMatchingUnansweredLine)
    {
    	// 0.9.6 - there isn't a testable char if they're of matching length.
        if (sAnswerFragmentLastLine.length()==sMatchingUnansweredLine.length())
        {
        	return null;
        }

        int iIndex = sAnswerFragmentLastLine.length();
        char possChar;
        do
        {
            possChar = sMatchingUnansweredLine.charAt(iIndex);
            iIndex++;
        } while (StaticHelpers.isPunctuation(possChar)
                 && (iIndex<sMatchingUnansweredLine.length())   );

        if (StaticHelpers.isPunctuation(possChar)==false)
            return (sMatchingUnansweredLine.substring(0,iIndex));
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

    protected Vector getAllEnglishAndChars(boolean bIncludeMW)
    {
        Vector v = new Vector();
        for (int i=0; i<m_vFirsteseEntities.size(); i++)
        {
            LanguageEntity cle =
                (LanguageEntity)m_vFirsteseEntities.elementAt(i);
            String sLine = cle.getRomanForm(bIncludeMW);
            if (sLine!=null)v.addElement(sLine);
        }
        
        for (int i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity cle =
                (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            String sLine = cle.getNativeForm(bIncludeMW);
            if (sLine!=null)v.addElement(sLine);
        }
        return v;
    }
    
    protected Vector getAllEnglishAndPinyin(boolean bIncludeMW,boolean bIncludeMnemonics)
    {
        Vector v = new Vector();
        for (int i=0; i<m_vFirsteseEntities.size(); i++)
        {
            LanguageEntity cle =
                (LanguageEntity)m_vFirsteseEntities.elementAt(i);
            String sLine = cle.getRomanForm(bIncludeMW);
            if (sLine!=null)v.addElement(sLine);
            
            if (bIncludeMnemonics) {
            	String mnem = cle.getMnemonic();
                if (mnem!=null)v.addElement(mnem);
            }
        }
        
        for (int i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity cle =
                (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            String sLine = cle.getRomanForm(bIncludeMW);
            if (sLine!=null)v.addElement(sLine);
            
            if (bIncludeMnemonics) {
            	String mnem = cle.getMnemonic();
                if (mnem!=null)v.addElement(mnem);
            }
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
    	StringBuffer sb = new StringBuffer();
        for (int i=0; i<m_vFirsteseEntities.size(); i++)
        {
            LanguageEntity leFirstese =
               (LanguageEntity)m_vFirsteseEntities.elementAt(i);
            sb.append ( leFirstese.toString() + " " );
        }

        for (int i=0; i<m_vSecondeseEntities.size(); i++)
        {
            LanguageEntity leSecondese =
                (LanguageEntity)m_vSecondeseEntities.elementAt(i);
            sb.append( leSecondese.toString() + " " );
        }
        return sb.toString();
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
    	// System.out.println(s);
    }

    /**Tests whether the question and answer fields specified by the QADirection
    are non-null and non-blank in this LanguageQA.*/
    public boolean containsQADirectionFields(QADirection qadir)
    {
    	trace("Entering LanguageQA.containsQADirectionFields()");
    	
    	Vector vQuestion = getQuestion();
    	if (vQuestion==null) return false;
    	trace("vQuestion.size()==" + vQuestion.size());
    	if (vQuestion.size()==0) return false;
    	Enumeration eQuestion = vQuestion.elements();
    	while (eQuestion.hasMoreElements())
    	{
    		String sElement = (String)eQuestion.nextElement();
    		if (sElement==null) return false;
    		if (sElement.length()==0) return false;
    	}

    	Vector vAnswer = getAnswer();
    	if (vAnswer==null) return false;
    	trace("vAnswer.size()==" + vAnswer.size());
    	if (vAnswer.size()==0) return false;
    	Enumeration eAnswer = vAnswer.elements();
    	while (eAnswer.hasMoreElements())
    	{
    		String sElement = (String)eAnswer.nextElement();
    		if (sElement==null) return false;
    		if (sElement.length()==0) return false;
    	}
    	
    	trace("");
    	trace("Including " + this.toXML());
    	
    	return true;    	
    }

}
