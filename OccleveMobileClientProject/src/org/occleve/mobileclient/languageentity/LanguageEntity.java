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

package org.occleve.mobileclient.languageentity;

import com.exploringxml.xml.*;
import java.util.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.languageentity.chinese.*;

/**Abstract base class for eg. Noun, ChineseVerb, ChineseAdjective, etc.*/
public class LanguageEntity
{
    /**The name of this entity's XML tag, eg. pnoun for a proper noun.*/
    protected String m_sXmlElementName;

    /**This language entity written in romanized form.*/
    protected String m_sRomanForm;

    /**This language entity written in its native script (if any).*/
    protected String m_sNativeForm;

    /**The literal translation in the other language of this entity.*/
    protected String m_sLiteralTranslation;

    /**Accessor.*/
    public String getLiteralTranslation() {return m_sLiteralTranslation;}

    public static LanguageEntity make(Node entityNode,Node languageNode)
    throws Exception
    {
        String sName = entityNode.name;
        if (XML.isOpeningTag(sName,XML.ZH_CNOUN))
        {
            return new ChineseCountableNoun(entityNode);
        }
        else
        {
            return new LanguageEntity(entityNode);
        }
    }

    /**Constructor for loading from XML.*/
    public LanguageEntity(Node entityNode)
    throws Exception
    {
        m_sXmlElementName = entityNode.name;
        trace("Making LanguageEntity of type " + entityNode.name);

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

        // Node register = entityNode.findFirst(XML.REGISTER);
        // m_sRegister = register.getCharacters();
    }

    /**Deprecated constructor for loading from old style ## format.*/
    public LanguageEntity(String sSourceFileLine,String sXmlElementName)
    throws Exception
    {
        m_sXmlElementName = sXmlElementName;
        LanguageEntity_OldFormatLoader.oldFormatLoad(this,sSourceFileLine);
    }

    public boolean entityContainsString(String s)
    {
        if (fieldContains(m_sRomanForm,s)) return true;
        if (fieldContains(m_sNativeForm,s)) return true;
        if (fieldContains(m_sLiteralTranslation,s)) return true;
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

        if (m_sRomanForm!=null)
        {
            if (m_sRomanForm.length()!=0)
            {
                XML.appendStartTag(sb, XML.ROMAN);
                sb.append(m_sRomanForm);
                XML.appendEndTag(sb, XML.ROMAN);
                sb.append(Constants.NEWLINE);
            }
        }

        if (m_sNativeForm!=null)
        {
            if (m_sNativeForm.length()!=0)
            {
                XML.appendStartTag(sb, XML.SCRIPT);
                sb.append(m_sNativeForm);
                XML.appendEndTag(sb, XML.SCRIPT);
                sb.append(Constants.NEWLINE);
            }
        }

        if (m_sLiteralTranslation!=null)
        {
            if (m_sLiteralTranslation.length()!=0)
            {
                XML.appendStartTag(sb, XML.LITERAL);
                sb.append(m_sLiteralTranslation);
                XML.appendEndTag(sb, XML.LITERAL);
                sb.append(Constants.NEWLINE);
            }
        }

        XML.appendEndTag(sb,m_sXmlElementName);
        return sb.toString();
    }

    private void trace(String s)
    {
        //System.out.println(s);
    }
}

