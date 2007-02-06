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

package org.occleve.mobileclient.excludable.raweditor;

import org.occleve.mobileclient.*;

/**This class is to stop the menu in the RawEditor becoming
too bloated. Some of the less common options have been moved to
classes derived from this class.*/
public class RawEditorInserters extends RawEditorExtraOptions
{
    protected final String INSERT_ADJ = "Insert adjective zhqa";
    protected final String INSERT_CNOUN = "Insert Cnoun zhqa";
    protected final String INSERT_GE_CNOUN = "Insert ge4 Cnoun zhqa";
    protected final String INSERT_SUO_CNOUN = "Insert suo3 Cnoun zhqa";
    protected final String INSERT_TIAO_CNOUN = "Insert tiao2 Cnoun zhqa";
    protected final String INSERT_UNOUN = "Insert Unoun zhqa";
    protected final String INSERT_PHRASE = "Insert phrase zhqa";
    protected final String INSERT_VERB = "Insert verb zhqa";
    protected final String INSERT_GE = "Insert ge char";
    protected final String INSERT_PLAIN_QA = "Insert PlainQA";

    public RawEditorInserters(RawEditor notepad) throws Exception
    {
        super(notepad,"Insert...");

        append(INSERT_ADJ,null);
        append(INSERT_CNOUN,null);
        append(INSERT_GE_CNOUN,null);
        append(INSERT_SUO_CNOUN,null);
        append(INSERT_TIAO_CNOUN,null);
        append(INSERT_UNOUN,null);
        append(INSERT_PHRASE,null);
        append(INSERT_VERB,null);
        append(INSERT_GE,null);
        append(INSERT_PLAIN_QA,null);
    }

    protected void onSelectCommand() throws Exception
    {
        int iIndex = getSelectedIndex();
        String sPrompt = getString(iIndex);

        if (sPrompt.equals(INSERT_ADJ))
        {
            insertAdjectiveQA("x","x","x",false);
        }
        else if (sPrompt.equals(INSERT_CNOUN))
        {
            insertCNounQA("x","x","x","x","x",false);
        }
        else if (sPrompt.equals(INSERT_GE_CNOUN))
        {
            insertGeCNounQA("x","x","x",false);
        }
        else if (sPrompt.equals(INSERT_SUO_CNOUN))
        {
            insertSuoCNounQA("x","x","x",false);
        }
        else if (sPrompt.equals(INSERT_TIAO_CNOUN))
        {
            insertTiaoCNounQA("x","x","x",false);
        }
        else if (sPrompt.equals(INSERT_UNOUN))
        {
            insertUNounQA("x","x","x",false);
        }
        else if (sPrompt.equals(INSERT_PHRASE))
        {
            insertPhraseQA("x","x","x",false);
        }
        else if (sPrompt.equals(INSERT_VERB))
        {
            insertVerbQA("x","x","x",false);
        }
        else if (sPrompt.equals(INSERT_GE))
        {
            insertGeCharacter();
        }
        else if (sPrompt.equals(INSERT_PLAIN_QA))
        {
            insertPlainQA();
        }
        else
        {
            String sErr =
                "Unknown choice in RawEditorInserters.onSelectCommand";
            OccleveMobileMidlet.getInstance().onError(sErr);
        }
    }

    protected void insertGeCharacter()
    {
        String sGeMeasureWord = "\u4E2A";
        m_Notepad.appendThenDisplay(sGeMeasureWord,false);
    }

    protected void insertPlainQA()
    {
        String sText =
            Constants.NEWLINE +
            "Qx" + Constants.NEWLINE +
            "Ax" + Constants.NEWLINE;
        m_Notepad.appendThenDisplay(sText,false);
    }
}

