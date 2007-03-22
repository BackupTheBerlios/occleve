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

package org.occleve.mobileclient.screens.options;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.qa.language.*;

public class ChineseTestOptionsScreen extends TestOptionsScreen
implements ItemCommandListener
{
    protected static String ZI = "\u5B57";
    protected static String TO = "to ";

    protected static String PINYIN = "Pinyin";
    protected static String ENGLISH = "English";
    protected static String CHARS = ZI;

    protected static String CHARS_ENGLISH = ZI + " & English";
    protected static String CHARS_PINYIN = ZI + " & Pinyin";
    protected static String ENGLISH_PINYIN = "English & Pinyin";

    protected ChoiceGroup m_FromChoiceGroup;
    protected ChoiceGroup m_ToChoiceGroup;
    protected ChoiceGroup m_MeasureWordsRadioButton;

    /*
    protected final String MEASURE_WORDS_ON = "Measure words";
    protected final String MEASURE_WORDS_OFF = "No measure words";
    protected StringItem m_MeasureWordsItem;
    protected Command m_ToggleMWCommand = new Command("Toggle",Command.ITEM,0);
    */

    public ChineseTestOptionsScreen()
    throws Exception
    {
        super();

        String[] fromChoices =
            {PINYIN,ENGLISH,CHARS,CHARS_ENGLISH,CHARS_PINYIN,ENGLISH_PINYIN};
        m_FromChoiceGroup =
            new ChoiceGroup(null,ChoiceGroup.POPUP,fromChoices,null);

        String[] toChoices =
            {TO+PINYIN,TO+ENGLISH,TO+CHARS,
            TO+CHARS_ENGLISH,TO+CHARS_PINYIN,TO+ENGLISH_PINYIN};
        m_ToChoiceGroup =
            new ChoiceGroup(null,ChoiceGroup.POPUP,toChoices,null);

        /*
        m_MeasureWordsItem = new StringItem(null,MEASURE_WORDS_ON);
        m_MeasureWordsItem.setItemCommandListener(this);
        m_MeasureWordsItem.setDefaultCommand(m_ToggleMWCommand);
        */

        String[] oneChoice = {"Measure words"};
        m_MeasureWordsRadioButton =
            new ChoiceGroup(null,ChoiceGroup.MULTIPLE,oneChoice,null);
        m_MeasureWordsRadioButton.setSelectedIndex(0,true);

        append(m_FromChoiceGroup);
        append(m_ToChoiceGroup);
        append(m_MeasureWordsRadioButton);

        // Initial settings are english to pinyin with measure words enabled.
        m_FromChoiceGroup.setSelectedIndex(1,true);
        m_ToChoiceGroup.setSelectedIndex(0,true);
    }

    protected QADirection getQADirection() throws Exception
    {
        int iFrom = getLanguageCode(m_FromChoiceGroup);
        int iTo = getLanguageCode(m_ToChoiceGroup);

        //boolean bIncludeMW = m_MeasureWordsItem.getText().equals(MEASURE_WORDS_ON);
        boolean bIncludeMW = m_MeasureWordsRadioButton.isSelected(0);

        return new LanguageQADirection(iFrom,iTo,bIncludeMW);
    }

    protected int getLanguageCode(ChoiceGroup choiceGroup) throws Exception
    {
        int i = choiceGroup.getSelectedIndex();
        String sChoice = choiceGroup.getString(i);

        // If the choice starts with "to ", strip it.
        if (sChoice.startsWith(TO))
        {
            sChoice = sChoice.substring( TO.length() );
        }

        if (sChoice.equals(CHARS))
            return LanguageQADirection.SECONDESE_NATIVE;
        else if (sChoice.equals(ENGLISH))
            return LanguageQADirection.FIRSTESE_ROMAN;
        else if (sChoice.equals(PINYIN))
            return LanguageQADirection.SECONDESE_ROMAN;
        else if (sChoice.equals(CHARS_ENGLISH))
            return LanguageQADirection.FR_SN;
        else if (sChoice.equals(CHARS_PINYIN))
            return LanguageQADirection.SN_SR;
        else if (sChoice.equals(ENGLISH_PINYIN))
            return LanguageQADirection.FR_SR;
        else
            throw new Exception("Invalid choice in language ChoiceGroup");
    }

    /*Implementation of ItemCommandListener.*/
    /*
    public void commandAction(Command c, Item item)
    {
        if (item==m_MeasureWordsItem)
        {
            String newValue;
            if (m_MeasureWordsItem.getText().equals(MEASURE_WORDS_ON))
                newValue = MEASURE_WORDS_OFF;
            else
                newValue = MEASURE_WORDS_ON;

            m_MeasureWordsItem.setText(newValue);
        }
        else
        {
            super.commandAction(c,item);
        }
    }
    */
}

