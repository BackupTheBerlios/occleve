/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2008  Joe Gittings

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

package org.occleve.mobileclient.dictionary;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;

/**0.9.7 - a browser for the CC-CEDICT dictionary.*/
public class DictionaryBrowser extends Form
implements CommandListener,ItemCommandListener ////,ItemStateListener
{

    protected StringItem m_StartTestItem =
            new StringItem(null,"Start test",Item.BUTTON);

    protected String SEQUENTIAL = "In sequence";
    protected String RANDOM = "Random";
    protected ChoiceGroup m_SequentialOrRandomChoiceGroup;

    // 0.9.6 - add a Start From Question No field for sequential mode
    protected TextField m_FirstQuestionTextField;
    protected TextField m_LastQuestionTextField;
    protected TextField m_RestartOnPercentageBelowTextField;
    
    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    public DictionaryBrowser() throws Exception
    {
        super(Constants.PRODUCT_NAME);

        m_OKCommand = new Command("OK",Command.OK,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);

        addCommand(m_OKCommand);
        addCommand(m_CancelCommand);
        setCommandListener(this);

        // Append items to this form.

        append(m_StartTestItem);
        m_StartTestItem.setItemCommandListener(this);
        m_StartTestItem.setDefaultCommand(m_OKCommand);

        String[] orderChoices = {SEQUENTIAL,RANDOM};
        m_SequentialOrRandomChoiceGroup =
            new ChoiceGroup(null,ChoiceGroup.POPUP,orderChoices,null);
        append(m_SequentialOrRandomChoiceGroup);
        
        m_FirstQuestionTextField =
        	new TextField("Question to start from:","1",10,TextField.NUMERIC);
        append(m_FirstQuestionTextField);

        m_LastQuestionTextField =
        	new TextField("Question to end at:","1",10,TextField.NUMERIC);
        append(m_LastQuestionTextField);

        m_RestartOnPercentageBelowTextField =
        	new TextField("Restart if percentage drops under:","0",10,TextField.NUMERIC);
        append(m_RestartOnPercentageBelowTextField);
        
        //////setItemStateListener(this);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_OKCommand)
        {
            try
            {
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else if (c==m_CancelCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command in DictionaryBrowser.commandAction");
        }
    }

    /*Implementation of ItemCommandListener.*/
    public void commandAction(Command c, Item item)
    {
        try
        {
            if (item==m_StartTestItem)
            {
                OccleveMobileMidlet.getInstance().beep();
            }
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }


}

