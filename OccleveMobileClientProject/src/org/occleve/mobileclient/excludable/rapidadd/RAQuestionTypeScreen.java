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

package org.occleve.mobileclient.excludable.rapidadd;

import javax.microedition.lcdui.*;

public class RAQuestionTypeScreen extends javax.microedition.lcdui.List
// was extends Form
{
    protected static String CNOUN = "Noun (countable)";
    protected static String UNOUN = "Noun (uncountable)";
    protected static String PNOUN = "Noun (proper)";

    protected static String ADJ = "Adjective";
    protected static String PHRASE = "Phrase";
    protected static String VERB = "Verb";

    //protected ChoiceGroup m_TypeChoiceGroup;

    public RAQuestionTypeScreen(Command okCommand,Command cancelCommand,
                                CommandListener clr)
    throws Exception
    {
        super("Question type:",List.IMPLICIT);

        addCommand(okCommand);
        addCommand(cancelCommand);
        setCommandListener(clr);

        append(CNOUN,null);
        append(UNOUN,null);
        append(PNOUN,null);
        append(ADJ,null);
        append(PHRASE,null);
        append(VERB,null);

        /*
        String[] choices = {ADJ,CNOUN,UNOUN,PNOUN,PHRASE,VERB};
        m_TypeChoiceGroup =
            new ChoiceGroup(null,ChoiceGroup.POPUP,choices,null);
        append(m_TypeChoiceGroup);
        */

        // Default setting is Countable Noun.
        // m_TypeChoiceGroup.setSelectedIndex(1,true);
    }

    public boolean isCountableNoun()
    {
        //int iIndex = m_TypeChoiceGroup.getSelectedIndex();
        //String sChoice = m_TypeChoiceGroup.getString(iIndex);

        int iIndex = getSelectedIndex();
        String sChoice = getString(iIndex);
        return (sChoice.equals(CNOUN));
    }

    public String getLanguageEntityCode() throws Exception
    {
        //int iIndex = m_TypeChoiceGroup.getSelectedIndex();
        //String sChoice = m_TypeChoiceGroup.getString(iIndex);

        int iIndex = getSelectedIndex();
        String sChoice = getString(iIndex);

        if (sChoice.equals(CNOUN))
            return "CNOUN";
        else if (sChoice.equals(UNOUN))
            return "UNOUN";
        else if (sChoice.equals(PNOUN))
            return "PNOUN";
        else if (sChoice.equals(ADJ))
            return "ADJ";
        else if (sChoice.equals(PHRASE))
            return "PHRASE";
        else if (sChoice.equals(VERB))
            return "VERB";
        else
        {
            throw new Exception("Invalid choice in RAQuestionTypeScreen");
        }
    }
}

