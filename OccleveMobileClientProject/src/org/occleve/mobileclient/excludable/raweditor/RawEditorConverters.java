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

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;

public class RawEditorConverters extends RawEditorExtraOptions
{
    protected final String CONVERT_TO_ADJ = "Convert PQA to adj zhqa";
    protected final String CONVERT_TO_CNOUN = "Convert PQA to cnoun zhqa";
    protected final String CONVERT_TO_GE_CNOUN = "Convert PQA to ge-cnoun zhqa";
    protected final String CONVERT_TO_SUO_CNOUN = "Convert PQA to suo-cnoun zhqa";
    protected final String CONVERT_TO_TIAO_CNOUN = "Convert PQA to tiao-cnoun zhqa";
    protected final String CONVERT_TO_UNOUN = "Convert PQA to unoun zhqa";
    protected final String CONVERT_TO_VERB = "Convert PQA to verb zhqa";

    public RawEditorConverters(RawEditor notepad) throws Exception
    {
        super(notepad,"Insert...");

        append(CONVERT_TO_ADJ,null);
        append(CONVERT_TO_CNOUN,null);
        append(CONVERT_TO_GE_CNOUN,null);
        append(CONVERT_TO_SUO_CNOUN,null);
        append(CONVERT_TO_TIAO_CNOUN,null);
        append(CONVERT_TO_UNOUN,null);
        append(CONVERT_TO_VERB,null);
    }

    protected void onSelectCommand() throws Exception
    {
        // System.out.println("Entering inserters class . onSelectCommand");

        int iIndex = getSelectedIndex();
        String sPrompt = getString(iIndex);

        // System.out.println("sPrompt = " + sPrompt);

        StringBuffer q = new StringBuffer();
        StringBuffer a = new StringBuffer();
        m_Notepad.getPlainQAFromCurrentChunk(q,a);

        if ((q.length()==0) && (a.length()==0))
        {
            String sMsg = "Not a PlainQA";
            Alert alert = new Alert(null,sMsg,null,null);
            OccleveMobileMidlet.getInstance().displayAlert(alert,m_Notepad);
            return;
        }

        String eng = q.toString();
        String bp = a.toString();
        String bc = "";

        if (sPrompt.equals(CONVERT_TO_ADJ))
            insertAdjectiveQA(eng,bp,bc,true);
        else if (sPrompt.equals(CONVERT_TO_CNOUN))
            insertCNounQA(eng,bp,bc,"x","x",true);
        else if (sPrompt.equals(CONVERT_TO_GE_CNOUN))
            insertGeCNounQA(eng,bp,bc,true);
        else if (sPrompt.equals(CONVERT_TO_SUO_CNOUN))
            insertSuoCNounQA(eng,bp,bc,true);
        else if (sPrompt.equals(CONVERT_TO_TIAO_CNOUN))
            insertTiaoCNounQA(eng,bp,bc,true);
        else if (sPrompt.equals(CONVERT_TO_UNOUN))
            insertUNounQA(eng,bp,bc,true);
        else if (sPrompt.equals(CONVERT_TO_VERB))
            insertVerbQA(eng,bp,bc,true);
        else
        {
            String sErr =
                "Unknown choice in RawEditorConverters.onSelectCommand";
            OccleveMobileMidlet.getInstance().onError(sErr);
        }
    }

    /*
    protected void convertToUNoun(StringBuffer q,StringBuffer a)
    {
        String eng = q.toString();
        String bp = a.toString();
        String bc = "";
        insertUNounQA(eng,bp,bc,true);
    }
    protected void convertToVerb(StringBuffer q,StringBuffer a)
    {
        String eng = q.toString();
        String bp = a.toString();
        String bc = "";
        insertVerbQA(eng,bp,bc,true);
    }
    */

}

