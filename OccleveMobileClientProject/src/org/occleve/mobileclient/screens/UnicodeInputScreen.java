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
@version 0.9.3
*/

package org.occleve.mobileclient.screens;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.qacontrol.*;
import org.occleve.mobileclient.testing.qaview.*;

public class UnicodeInputScreen extends TextBox
implements CommandListener,Runnable
{
    protected char m_UnicodeCharToInput;
    protected MagicTypewriterController m_TestControllerThatInvokedThis;
    protected TestResults m_TestResults;

    protected Command m_PeekCommand;
    protected Command m_CancelCommand;

    protected boolean m_bExitThread;

    public UnicodeInputScreen
    (
        char unicodeCharToInput,
        MagicTypewriterController testControllerThatInvokedThis,
        TestResults results
    )
    throws Exception
    {
        super("Input character:","",1,TextField.ANY);
        m_UnicodeCharToInput = unicodeCharToInput;
        m_TestControllerThatInvokedThis = testControllerThatInvokedThis;
        m_TestResults = results;

        m_PeekCommand = new Command("Peek",Command.ITEM,0);
        addCommand(m_PeekCommand);

        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);
        addCommand(m_CancelCommand);

        setCommandListener(this);

        new Thread(this).start();
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_PeekCommand)
        {
            String sChar = new String();
            sChar += m_UnicodeCharToInput;
            Alert alert = new Alert(null,sChar,null,null);
            alert.setTimeout(500);
            OccleveMobileMidlet.getInstance().displayAlert(alert,this);

            // Peeking at the character counts as a "wrong" keypress.
            m_TestResults.addResponse(false);
        }
        else if (c==m_CancelCommand)
        {
            try
            {
                m_bExitThread = true;
                m_TestControllerThatInvokedThis.setVisible();
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command in UnicodeInputScreen.commandAction");
        }
    }

    public void run()
    {
        try
        {
            monitorTextBoxContents();
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    protected void monitorTextBoxContents() throws Exception
    {
        m_bExitThread = false;
        while (m_bExitThread==false)
        {
            if (size()==1)
            {
                String contents = getString();
                char inputtedChar = contents.charAt(0);

                boolean bCorrect = (inputtedChar==m_UnicodeCharToInput);
                m_TestResults.addResponse(bCorrect);

                if (bCorrect)
                {
                    m_bExitThread = true;
                    m_TestControllerThatInvokedThis.appendToAnswerFragment(inputtedChar);
                    m_TestControllerThatInvokedThis.setVisible();
                    m_TestControllerThatInvokedThis.skipPunctuation();
                }
                else
                {
                    setString("");
                }
            }

            // Brief pause to prevent this thread hogging CPU time.
            try
            {
                Thread.sleep(50);
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
    }

}

