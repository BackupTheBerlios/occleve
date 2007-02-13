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

package org.occleve.mobileclient.languageentity.chinese;

import com.exploringxml.xml.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.languageentity.*;

public class ChineseCountableNoun extends Noun
{
    // Fields for the old format
    protected static final String MWP_FIELD_NAME = "MWP=";
    protected static final String MWC_FIELD_NAME = "MWC=";

    // Canned measure word fields for the old format
    protected static final String MW_GE_FIELD_NAME = "MW_GE";
    protected static final String MW_SUO_FIELD_NAME = "MW_SUO";
    protected static final String MW_TIAO_FIELD_NAME = "MW_TIAO";

    /**The measure word in pinyin, excluding the "yi2" or "yi4". This
    is automatically added by this class at runtime.*/
    protected String m_sMeasureWordPinyin;

    /**Ditto in characters.*/
    protected String m_sMeasureWordChar;

    /**XML constructor.*/
    public ChineseCountableNoun(Node entityNode)
    throws Exception
    {
        super(entityNode);
    }

    /**Deprecated constructor for the old file format.*/
    public ChineseCountableNoun(String sSourceFileLine)
    throws Exception
    {
        // Allow the base class to parse the fields.
        super(sSourceFileLine,XML.CNOUN);

        // For consistency, if no characters are supplied for the body,
        // make sure no characters are supplied for the measure word either
        // (since that field could be automatically filled in by
        // a canned measure word field such as MW_GE).
        if (m_sNativeForm==null) m_sMeasureWordChar=null;

        // Now complain if the PINYIN measure word field is null
        // (the measure word character can be null as not all test
        // entries have to have characters).
        if (m_sMeasureWordPinyin==null)
        {
            String sErr = "Error! Countable chinese noun has no pinyin " +
                          "measure word. Source line = " + Constants.NEWLINE +
                          sSourceFileLine;
            throw new Exception(sErr);
        }
    }

    /**Is called via a bodge from LanguageEntity_OldFormatLoader.*/
    public void oldFormatProcessField(String s) throws Exception
    {
        if (s.startsWith(MWP_FIELD_NAME))
            m_sMeasureWordPinyin =
                LanguageEntity_OldFormatLoader.getRHS(s,MWP_FIELD_NAME);
        else if (s.startsWith(MWC_FIELD_NAME))
            m_sMeasureWordChar =
                LanguageEntity_OldFormatLoader.getRHS(s,MWC_FIELD_NAME);
        else if (s.startsWith(MW_GE_FIELD_NAME))
        {
            m_sMeasureWordPinyin = "ge4";
            m_sMeasureWordChar = "\u4E2A";
        }
        else if (s.startsWith(MW_SUO_FIELD_NAME))
        {
            m_sMeasureWordPinyin = "suo3";
            m_sMeasureWordChar = "\u6240";
        }
        else if (s.startsWith(MW_TIAO_FIELD_NAME))
        {
            m_sMeasureWordPinyin = "tiao2";
            m_sMeasureWordChar = "\u6761";
        }
    }

    /**Override of LanguageEntity function.*/
    public boolean entityContainsString(String s)
    {
        if (fieldContains(m_sMeasureWordPinyin,s)) return true;
        if (fieldContains(m_sMeasureWordChar,s)) return true;

        return super.entityContainsString(s);
    }

    /**Override of LanguageEntity function.*/
    public String getRomanForm(boolean bIncludeMeasureWord)
    {
        StringBuffer sb = new StringBuffer();

        if (bIncludeMeasureWord && (m_sMeasureWordPinyin!=null))
        {
            // Apply the pronunciation rule: yi2 xxx4, yi4 xxxB
            if (m_sMeasureWordPinyin.endsWith("4"))
                sb.append("yi2 ");
            else
                sb.append("yi4 ");

            sb.append(m_sMeasureWordPinyin + " ");
        }

        sb.append(m_sRomanForm);
        return sb.toString();
    }

    /**Override of LanguageEntity function.*/
    public String getChineseChars(boolean bIncludeMeasureWord)
    {
        StringBuffer sb = new StringBuffer();

        if (bIncludeMeasureWord && (m_sMeasureWordChar!=null))
        {
            // Append the character for "yi" (one).
            sb.append("\u4E00");

            sb.append(m_sMeasureWordChar);
        }

        sb.append(m_sNativeForm);
        return sb.toString();
    }


}
