/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-2009  Joe Gittings

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
@version 0.9.7
*/

package org.occleve.mobileclient.screens.options;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.*;
import com.sun.lwuit.plaf.*;
import com.sun.lwuit.util.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.qa.language.*;
import org.occleve.mobileclient.testing.test.Test;

public class ChineseTestOptionsScreen extends TestOptionsScreen
{
    // protected static String ZI = "\u5B57";
    protected static final String ZI = "Hanzi";
    
    protected static final String TO = "to ";

    protected static final String PINYIN = "Pinyin";
    protected static final String ENGLISH = "English";
    protected static final String CHARS = ZI;

    protected static final String CHARS_ENGLISH = ZI + " & English";
    protected static final String CHARS_PINYIN = ZI + " & Pinyin";
    protected static final String ENGLISH_PINYIN = "English & Pinyin";

    protected ComboBox m_FromChoiceGroup;
    protected ComboBox m_ToChoiceGroup;
    protected RadioButton m_MeasureWordsRadioButton;

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
    }

    protected void addSubclassControls() throws Exception
    {
        String[] fromChoices =
        {PINYIN,ENGLISH,CHARS,CHARS_ENGLISH,CHARS_PINYIN,ENGLISH_PINYIN};
	    m_FromChoiceGroup = new ComboBox(fromChoices);
	
	    String[] toChoices =
	        {TO+PINYIN,TO+ENGLISH,TO+CHARS,
	        TO+CHARS_ENGLISH,TO+CHARS_PINYIN,TO+ENGLISH_PINYIN};
	    m_ToChoiceGroup = new ComboBox(toChoices);
	
	    /*
	    m_MeasureWordsItem = new StringItem(null,MEASURE_WORDS_ON);
	    m_MeasureWordsItem.setItemCommandListener(this);
	    m_MeasureWordsItem.setDefaultCommand(m_ToggleMWCommand);
	    */
	
	    m_MeasureWordsRadioButton = new RadioButton("Measure words");
	    m_MeasureWordsRadioButton.setSelected(true);
	
	    addComponent(m_FromChoiceGroup);
	    addComponent(m_ToChoiceGroup);
	    addComponent(m_MeasureWordsRadioButton);	
    }
    
    protected QADirection getQADirection() throws Exception
    {
        int iFrom = getLanguageCode(m_FromChoiceGroup);
        int iTo = getLanguageCode(m_ToChoiceGroup);

        boolean bIncludeMW = m_MeasureWordsRadioButton.isSelected();
        boolean bIncludeMnemonics = m_MnemonicCheckbox.isSelected();

        return new LanguageQADirection(iFrom,iTo,bIncludeMW,bIncludeMnemonics);
    }

    protected int getLanguageCode(ComboBox choiceGroup) throws Exception
    {
        ////int i = choiceGroup.getSelectedIndex();
        String sChoice = (String)choiceGroup.getSelectedItem();

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

    public void makeVisible(Test test)
    {
    	super.makeVisible(test);

	    if (test.getFilename().toLowerCase().indexOf("char")!=-1) {
	    	// For chars quizzes default to English+Pinyin -> Hanzi
	    	m_FromChoiceGroup.setSelectedItem(ENGLISH_PINYIN);
	    	m_ToChoiceGroup.setSelectedItem(TO+CHARS);	    	
	    }
	    else {
	    	// Default is english+pinyin with measure words enabled.
	    	m_FromChoiceGroup.setSelectedIndex(1);
	    	m_ToChoiceGroup.setSelectedIndex(0);
	    }
    }
    
    /*Implementation of ItemCommandListener.*/
    /* public void commandAction(Command c, Item item)
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
    } */
}

