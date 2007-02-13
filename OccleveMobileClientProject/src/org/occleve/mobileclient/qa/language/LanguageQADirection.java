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

import org.occleve.mobileclient.qa.*;

public class LanguageQADirection extends QADirection
{
    public static int FIRSTESE_NATIVE = 1;
    public static int FIRSTESE_ROMAN = 2;

    public static int SECONDESE_NATIVE = 3;
    public static int SECONDESE_ROMAN = 4;

    // The self-combinations
    public static int FN_FR = 5;
    public static int SN_SR = 6;

    // The cross-combinations
    public static int FN_SN = 7;
    public static int FN_SR = 8;
    public static int FR_SN = 9;
    public static int FR_SR = 10;

    /**One of the above enumerated values.*/
    protected int m_iQuestionLanguage;

    /**One of the above enumerated values.*/
    protected int m_iAnswerLanguage;

    protected boolean m_bIncludeMeasureWords;

    /**"value" should be one of the static enumerated values declared by this class.*/
    public LanguageQADirection(int questionLang,int answerLang,
                              boolean bIncludeMeasureWords)
    {
        m_iQuestionLanguage = questionLang;
        m_iAnswerLanguage = answerLang;
        m_bIncludeMeasureWords = bIncludeMeasureWords;
    }

    public boolean includeMeasureWords() {return m_bIncludeMeasureWords;}

    public boolean isQuestionChars() {return (m_iQuestionLanguage==SECONDESE_NATIVE);}
    public boolean isQuestionCharsAndPinyin() {return (m_iQuestionLanguage==SN_SR);}
    public boolean isQuestionEnglish() {return (m_iQuestionLanguage==FIRSTESE_ROMAN);}
    public boolean isQuestionPinyin() {return (m_iQuestionLanguage==SECONDESE_ROMAN);}

    public boolean isAnswerChars() {return (m_iAnswerLanguage==SECONDESE_NATIVE);}
    public boolean isAnswerCharsAndPinyin() {return (m_iAnswerLanguage==SN_SR);}
    public boolean isAnswerEnglish() {return (m_iAnswerLanguage==FIRSTESE_ROMAN);}
    public boolean isAnswerPinyin() {return (m_iAnswerLanguage==SECONDESE_ROMAN);}
}

