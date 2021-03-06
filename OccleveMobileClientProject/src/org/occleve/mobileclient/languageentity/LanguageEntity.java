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

package org.occleve.mobileclient.languageentity;

import com.exploringxml.xml.*;
import org.occleve.mobileclient.*;
//import org.occleve.mobileclient.screens.ListenItem;

/**Abstract base class for eg. Noun, ChineseVerb, ChineseAdjective, etc.*/
public class LanguageEntity
{
    /**The ISO code of the language that this is in (eg. EN for English).*/
    protected String m_sLanguageCode;

    /**The name of this entity's XML tag, eg. pnoun for a proper noun.*/
    protected String m_sXmlElementName;

    /**This language entity written in romanized form.*/
    protected String m_sRomanForm;

    /**This language entity written in its native script (if any).*/
    protected String m_sNativeForm;

    /**Optional: The literal translation in the other language of this entity.*/
    protected String m_sLiteralTranslation;

    /**Optional: a mnemonic.*/
    protected String m_sMnemonic;
    public String getMnemonic() {return m_sMnemonic;}

    /**The filename of the audio clip (if any) associated with this entity.*/
    protected String m_sAudioClipFilename;

    /**Accessor.*/
    public String getLiteralTranslation() {return m_sLiteralTranslation;}

    /**Accessor.*/
    public boolean hasAudioClip() {return (m_sAudioClipFilename!=null);}

    /**Accessor.*/
    public String getAudioFilename() {return m_sAudioClipFilename;}

    public static LanguageEntity make(Node entityNode,Node languageNode)
    throws Exception
    {
    	// 0.9.6 - expanded this to instantiate all current subclasses of LanguageEntity correctly.
    	// Before it would only instantiate CountableNoun - all other types were instantiated
    	// as LanguageEntity.
        String sName = entityNode.name;
        if (XML.isOpeningTag(sName,XML.ADJECTIVE))
        {
            return new Adjective(entityNode,languageNode.name);
        }
        else if (XML.isOpeningTag(sName,XML.CNOUN))
        {
        	// 0.9.5: fixed longstanding bug where language code was not being
        	// correctly got, as languageNode.getCharacters() was being mistakenly called.
            return new CountableNoun(entityNode,languageNode.name); ////.getCharacters());
        }
        else if (XML.isOpeningTag(sName,XML.PHRASE))
        {
            return new Phrase(entityNode,languageNode.name);
        }
        else if (XML.isOpeningTag(sName,XML.PNOUN))
        {
            return new ProperNoun(entityNode,languageNode.name);
        }
        else if (XML.isOpeningTag(sName,XML.UNOUN))
        {
            return new UncountableNoun(entityNode,languageNode.name);
        }
        else if (XML.isOpeningTag(sName,XML.VERB))
        {
            return new Verb(entityNode,languageNode.name);
        }
        else
        {
            return new LanguageEntity(entityNode,languageNode.name); ////.getCharacters());
        }
    }

    /**Constructor for loading from XML.*/
    public LanguageEntity(Node entityNode,String sLanguageCode)
    throws Exception
    {
        m_sLanguageCode = sLanguageCode;
        m_sXmlElementName = entityNode.name;

        trace
        (
            "Making LanguageEntity of type " + entityNode.name +
            " in language " + m_sLanguageCode
        );

        Node romanization = entityNode.findFirst(XML.ROMAN);
        if (romanization!=null)
        {
            m_sRomanForm = romanization.getCharacters();
        }

        Node script = entityNode.findFirst(XML.SCRIPT);
        if (script!=null)
        {
            m_sNativeForm = script.getCharacters();
        }

        Node audioFilename = entityNode.findFirst(XML.AUDIO);
        if (audioFilename!=null)
        {
            m_sAudioClipFilename = audioFilename.getCharacters();
        }

        Node mnemonic = entityNode.findFirst(XML.MNEMONIC);
        if (mnemonic!=null) m_sMnemonic = mnemonic.getCharacters();

        // Node register = entityNode.findFirst(XML.REGISTER);
        // if (register!=null) m_sRegister = register.getCharacters();
    }

    // 0.9.5 - finally removed
    // Deprecated constructor for loading from old style ## format.
    /*
    public LanguageEntity(String sSourceFileLine,String sXmlElementName)
    throws Exception
    {
        m_sXmlElementName = sXmlElementName;
        LanguageEntity_OldFormatLoader.oldFormatLoad(this,sSourceFileLine);
    }
    */

    public boolean entityContainsString(String s)
    {
        if (fieldContains(m_sRomanForm,s)) return true;
        if (fieldContains(m_sNativeForm,s)) return true;
        if (fieldContains(m_sLiteralTranslation,s)) return true;
        if (fieldContains(m_sMnemonic,s)) return true;
        return false;
    }

    /**Copes with the string field being null.*/
    protected boolean fieldContains(String sField,String sFindMe)
    {
        if (sField==null)
            return false;
        else
        {
            return (sField.indexOf(sFindMe) != -1);
        }
    }

    public String getRomanForm(boolean bIncludeMeasureWord)
    {
        return m_sRomanForm;
    }

    public String getNativeForm(boolean bIncludeMeasureWord)
    {
        return m_sNativeForm;
    }

    public String toXML()
    {
        StringBuffer sb = new StringBuffer();
        XML.appendStartTag(sb,m_sXmlElementName);
        sb.append(Constants.NEWLINE);

        addXMLFieldIfNotBlank(sb,XML.ROMAN,m_sRomanForm);
        addXMLFieldIfNotBlank(sb,XML.SCRIPT,m_sNativeForm);
        addXMLFieldIfNotBlank(sb,XML.LITERAL,m_sLiteralTranslation);
        addXMLFieldIfNotBlank(sb,XML.MNEMONIC,m_sMnemonic);

        // Give derived classes a chance to add extra fields.
        toXML_ExtraFields(sb);

        XML.appendEndTag(sb,m_sXmlElementName);
        return sb.toString();
    }

    /**Override in derived classes to add extra fields
    to the XML.*/
    protected void toXML_ExtraFields(StringBuffer sbXML) {}

    protected void addXMLFieldIfNotBlank(StringBuffer sbXML,
                               String sFieldName,String sFieldValue)
    {
        if (sFieldValue!=null)
        {
            if (sFieldValue.length()!=0)
            {
                XML.appendStartTag(sbXML,sFieldName);
                sbXML.append(sFieldValue);
                XML.appendEndTag(sbXML,sFieldName);
                sbXML.append(Constants.NEWLINE);
            }
        }
    }

    protected void trace(String s)
    {
        //System.out.println(s);
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        String sRoman = getRomanForm(true);
        if (sRoman!=null) sb.append(sRoman);

        String sScript = getNativeForm(true);
        if (sScript!=null)
        {
            if (sRoman!=null) sb.append(" (");
            sb.append(sScript);
            if (sRoman!=null) sb.append(")");
        }

        String sLiteral = getLiteralTranslation();
        if (sLiteral!=null)
        {
            if (sLiteral.length()>0) sb.append(", lit. " + sLiteral);
        }

        if (m_sMnemonic!=null)
        {
            if (m_sMnemonic.length()>0) sb.append(", mnemonic: " + m_sMnemonic);
        }

        /* String sAudioFilename = le.getAudioFilename();
        if (sAudioFilename!=null)
        {
            ListenItem item2 = new ListenItem(sAudioFilename);
            vAppendTo.addElement(item2);
        } */

        return sb.toString();
    }
}

