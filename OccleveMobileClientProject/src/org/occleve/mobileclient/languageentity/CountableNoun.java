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
@version 0.9.5
*/

package org.occleve.mobileclient.languageentity;

import com.exploringxml.xml.*;
import org.occleve.mobileclient.*;
////import org.occleve.mobileclient.languageentity.*;

public class CountableNoun extends Noun
{
    // Fields for the old format
    // Deprecated - due to go in 0.9.4 release.
    protected static final String MWP_FIELD_NAME = "MWP=";
    protected static final String MWC_FIELD_NAME = "MWC=";

    // Canned measure word fields for the old format
    // Deprecated - due to go in 0.9.4 release.
    protected static final String MW_GE_FIELD_NAME = "MW_GE";
    protected static final String MW_SUO_FIELD_NAME = "MW_SUO";
    protected static final String MW_TIAO_FIELD_NAME = "MW_TIAO";

    /**The measure word in romanized form.
    Language-specific customizations such as "yi2" or "yi4" for Chinese,
    and "a" or "an" for English, are automatically added
    by this class at runtime.*/
    protected String m_sMeasureWordRoman;

    /**Ditto in characters.*/
    protected String m_sMeasureWordScript;

    /**XML constructor.*/
    public CountableNoun(Node entityNode,String sLanguageCode)
    throws Exception
    {
        super(entityNode,sLanguageCode);

        Node mwRomanNode = entityNode.findFirst(XML.MW_ROMAN);
        if (mwRomanNode!=null)
        {
            m_sMeasureWordRoman = mwRomanNode.getCharacters();
        }

        Node mwScriptNode = entityNode.findFirst(XML.MW_SCRIPT);
        if (mwScriptNode!=null)
        {
            m_sMeasureWordScript = mwScriptNode.getCharacters();
        }
    }

    /**Deprecated constructor for the old file format.*/
    public CountableNoun(String sSourceFileLine)
    throws Exception
    {
        // Allow the base class to parse the fields.
        super(sSourceFileLine,XML.CNOUN);

        // For consistency, if no characters are supplied for the body,
        // make sure no characters are supplied for the measure word either
        // (since that field could be automatically filled in by
        // a canned measure word field such as MW_GE).
        if (m_sNativeForm==null) m_sMeasureWordScript=null;

        // Now complain if the romanized measure word field is null
        // (the measure word character can be null as not all test
        // entries have to have characters).
        if (m_sMeasureWordRoman==null)
        {
            String sErr = "Error! Countable noun has no romanized " +
                          "measure word. Source line = " + Constants.NEWLINE +
                          sSourceFileLine;
            throw new Exception(sErr);
        }
    }

    /**Is called via a bodge from LanguageEntity_OldFormatLoader.*/
    public void oldFormatProcessField(String s) throws Exception
    {
        if (s.startsWith(MWP_FIELD_NAME))
            m_sMeasureWordRoman =
                LanguageEntity_OldFormatLoader.getRHS(s,MWP_FIELD_NAME);
        else if (s.startsWith(MWC_FIELD_NAME))
            m_sMeasureWordScript =
                LanguageEntity_OldFormatLoader.getRHS(s,MWC_FIELD_NAME);
        else if (s.startsWith(MW_GE_FIELD_NAME))
        {
            m_sMeasureWordRoman = "ge4";
            m_sMeasureWordScript = "\u4E2A";
        }
        else if (s.startsWith(MW_SUO_FIELD_NAME))
        {
            m_sMeasureWordRoman = "suo3";
            m_sMeasureWordScript = "\u6240";
        }
        else if (s.startsWith(MW_TIAO_FIELD_NAME))
        {
            m_sMeasureWordRoman = "tiao2";
            m_sMeasureWordScript = "\u6761";
        }
    }

    /**Override of LanguageEntity function.*/
    public boolean entityContainsString(String s)
    {
        if (fieldContains(m_sMeasureWordRoman,s)) return true;
        if (fieldContains(m_sMeasureWordScript,s)) return true;

        return super.entityContainsString(s);
    }

    /**Override of LanguageEntity function.*/
    public String getRomanForm(boolean bIncludeMeasureWord)
    {
    	trace("Entering CountableNoun.getRomanForm with bIncludeMeasureWord=" + bIncludeMeasureWord);
    	trace("and m_sLanguageCode = " + m_sLanguageCode);

    	StringBuffer sb = new StringBuffer();

        if (bIncludeMeasureWord)
        {
            sb.append(getRomanForm_GenerateArticleOrMeasureWord());
            sb.append(' ');
        }

        sb.append(m_sRomanForm);
        return sb.toString();
    }

    /**Does special processing for Chinese or English. For all other languages,
    just returns the measure word if it exists, otherwise an empty string.*/
    private String getRomanForm_GenerateArticleOrMeasureWord()
    {
        String sLowerCaseLang = m_sLanguageCode.toLowerCase();
        if ((sLowerCaseLang.equals("zh")) && (m_sMeasureWordRoman!=null))
        {
            // Chinese: apply the pronunciation rule: yi2 xxx4, yi4 xxxB
            if (m_sMeasureWordRoman.endsWith("4"))
                return "yi2 " + m_sMeasureWordRoman;
            else
                return "yi4 " + m_sMeasureWordRoman;
        }
        else if (sLowerCaseLang.equals("en"))
        {
            // English: add indefinite article ('a' or 'an').
            char first = m_sRomanForm.charAt(0);
            if (first == 'a' || first == 'e' || first == 'i' ||
                first == 'o' || first == 'u')
                return "an";
            else
                return "a";
        }

        if (m_sMeasureWordRoman==null)
            return "";
        else
            return m_sMeasureWordRoman;
    }

    /**Override of LanguageEntity function.*/
    public String getChineseChars(boolean bIncludeMeasureWord)
    {
        StringBuffer sb = new StringBuffer();

        if (m_sLanguageCode.toLowerCase().equals("zh"))
        {
            if (bIncludeMeasureWord && (m_sMeasureWordScript != null))
            {
                // Append the character for "yi" (one).
                sb.append("\u4E00");

                sb.append(m_sMeasureWordScript);
            }
        }

        sb.append(m_sNativeForm);
        return sb.toString();
    }

    /**Override of LanguageEntity method.*/
    protected void toXML_ExtraFields(StringBuffer sbXML)
    {
        addXMLFieldIfNotBlank(sbXML,XML.MW_ROMAN,m_sMeasureWordRoman);
        addXMLFieldIfNotBlank(sbXML,XML.MW_SCRIPT,m_sMeasureWordScript);
    }


}
