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

package org.occleve.mobileclient.testing;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;

public class JumpToForm extends Form implements CommandListener
{
    protected MagicTypewriterController m_TestController;
    protected TextField m_TextField;

    public JumpToForm(MagicTypewriterController mtc)
    {
        super("Jump to?");
        m_TestController = mtc;

        StringItem si = new StringItem(null,"Jump to:");
        StaticHelpers.safeSetFont(si,OccleveMobileFonts.TITLE_FONT);
        append(si);

        m_TextField = new TextField("Question number:","1",10,TextField.ANY);

        ///////// ,TextField.NUMERIC);
        // m_TextField.setInitialInputMode("IS_SIMPLIFIED_HANZI");

        append(m_TextField);

        addCommand(new Command("Cancel",Command.BACK,0));
        addCommand(new Command("OK",Command.OK,0));
        setCommandListener(this);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c.getCommandType() == Command.OK)
        {
            String sQuestionNo = m_TextField.getString();
            int iQuestionNo = Integer.parseInt(sQuestionNo);
            int iQuestionIndex = iQuestionNo - 1;
            m_TestController.jumpToQuestion(iQuestionIndex);
        }
        else if (c.getCommandType() == Command.BACK)
        {
            m_TestController.setVisible();
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command type in JumpToForm.commandAction");
        }
    }

}

