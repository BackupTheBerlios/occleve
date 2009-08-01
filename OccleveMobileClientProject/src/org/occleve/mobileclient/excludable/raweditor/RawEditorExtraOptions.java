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

/**This class is to stop the menu in the RawEditor becoming
too bloated. Some of the less common options have been moved to
classes derived from this class.*/
public abstract class RawEditorExtraOptions
extends javax.microedition.lcdui.List
implements CommandListener
{
    protected RawEditor m_Notepad;

    protected Command m_BackCommand;

    // protected final String COUNT_NEWLINES = "Count LFs and CRLFs";

    /**Derived classes should implement this to handle the
    options they provide.*/
    protected abstract void onSelectCommand() throws Exception;

    public RawEditorExtraOptions(RawEditor notepad,
                                 String sTitle) throws Exception
    {
        super("Extra options",List.IMPLICIT);
        m_Notepad = notepad;

        // append(COUNT_NEWLINES,null);

        m_BackCommand = new Command("Back",Command.ITEM,0);
        addCommand(m_BackCommand);
        setCommandListener(this);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable s)
    {
        // System.out.println("Entering RawEditorExtraOptions.commandAction");

        try
        {
            if (c==m_BackCommand)
            {
                // LWUIT-TO-DO OccleveMobileMidlet.getInstance().setCurrentForm(m_Notepad);
            }
            else if (c==List.SELECT_COMMAND)
            {
                onSelectCommand();
            }
            else
            {
                OccleveMobileMidlet.getInstance().onUnknownCommand(this.getClass());
            }
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void insertAdjectiveQA(String eng,String bp,String bc,boolean bClearFirst)
    {
        // System.out.println("Inserting adjective QA...");

        String sText =
            "<zhqa>" + Constants.NEWLINE +
            "ENADJ##" + eng + Constants.NEWLINE +
            "ZHADJ##BP=" + bp + "##BC=" + bc + Constants.NEWLINE +
            "</zhqa>" + Constants.NEWLINE;

        m_Notepad.appendThenDisplay(sText,bClearFirst);
    }

    protected void insertCNounQA(String eng,String bp,String bc,String mwp,String mwc,
                                 boolean bClearFirst)
    {
        String sText =
            "<zhqa>" + Constants.NEWLINE +
            "ENCNOUN##" + eng + Constants.NEWLINE +
            "ZHCNOUN##MWP=" + mwp + "##BP=" + bp +
            "##MWC=" + mwc + "##BC=" + bc + Constants.NEWLINE +
            "</zhqa>" + Constants.NEWLINE;

        m_Notepad.appendThenDisplay(sText,bClearFirst);
    }

    protected void insertGeCNounQA(String eng,String bp,String bc,boolean bClearFirst)
    {
        String sText =
            "<zhqa>" + Constants.NEWLINE +
            "ENCNOUN##" + eng + Constants.NEWLINE +
            "ZHCNOUN##MW_GE##BP=" + bp + "##BC=" + bc + Constants.NEWLINE +
            "</zhqa>" + Constants.NEWLINE;

        m_Notepad.appendThenDisplay(sText,bClearFirst);
    }

    protected void insertSuoCNounQA(String eng,String bp,String bc,boolean bClearFirst)
    {
        String sText =
            "<zhqa>" + Constants.NEWLINE +
            "ENCNOUN##" + eng + Constants.NEWLINE +
            "ZHCNOUN##MW_SUO##BP=" + bp + "##BC=" + bc + Constants.NEWLINE +
            "</zhqa>" + Constants.NEWLINE;

        m_Notepad.appendThenDisplay(sText,bClearFirst);
    }

    protected void insertTiaoCNounQA(String eng,String bp,String bc,boolean bClearFirst)
    {
        String sText =
            "<zhqa>" + Constants.NEWLINE +
            "ENCNOUN##" + eng + Constants.NEWLINE +
            "ZHCNOUN##MW_TIAO##BP=" + bp + "##BC=" + bc + Constants.NEWLINE +
            "</zhqa>" + Constants.NEWLINE;

        m_Notepad.appendThenDisplay(sText,bClearFirst);
    }

    /**Insert a blank entry for an uncountable Chinese noun.*/
    protected void insertUNounQA(String eng,String bp,String bc,boolean bClearFirst)
    {
        String sText =
            "<zhqa>" + Constants.NEWLINE +
            "ENUNOUN##" + eng +  Constants.NEWLINE +
            "ZHUNOUN##BP=" + bp + "##BC=" + bc + Constants.NEWLINE +
            "</zhqa>" + Constants.NEWLINE;

        m_Notepad.appendThenDisplay(sText,bClearFirst);
    }

    protected void insertPhraseQA(String eng,String bp,String bc,boolean bClearFirst)
    {
        String sText =
            "<zhqa>" + Constants.NEWLINE +
            "ENPHRASE##" + eng + Constants.NEWLINE +
            "ZHPHRASE##BP=" + bp + "##BC=" + bc + Constants.NEWLINE +
            "</zhqa>" + Constants.NEWLINE;

        m_Notepad.appendThenDisplay(sText,bClearFirst);
    }

    protected void insertVerbQA(String eng,String bp,String bc,boolean bClearFirst)
    {
        String sText =
            "<zhqa>" + Constants.NEWLINE +
            "ENVERB##" + eng + Constants.NEWLINE +
            "ZHVERB##BP=" + bp + "##BC=" + bc + Constants.NEWLINE +
            "</zhqa>" + Constants.NEWLINE;

        m_Notepad.appendThenDisplay(sText,bClearFirst);
    }

}
