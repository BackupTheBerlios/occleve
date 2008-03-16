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
@version 0.9.6
*/

package org.occleve.mobileclient.screens;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.serverbrowser.*;

public class ChooseQuizServerScreen extends List
implements CommandListener
{
    protected Command m_ConnectCommand;
    protected Command m_BackCommand;
    protected CommonCommands m_CommonCommands;

    private static final String PREAMBLE =
    	"WHERE DO YOU WANT TO DOWNLOAD QUIZZES FROM?" + Constants.NEWLINE;

    private static final String OCCLEVE_WIKI =
    	"The PocketChinese server (aka Occleve). " +
    	"Mostly contains Chinese-English quizzes." + Constants.NEWLINE;

    private static final String ENGLISH_WIKIVERSITY =
    	"The English wikiversity. Contains quizzes on a range of topics."
    	+ Constants.NEWLINE;

    private static final String FRENCH_WIKIVERSITY =
    	"The French wikiversity. Contains quizzes on a range of topics.";

    public ChooseQuizServerScreen()
    throws Exception
    {
        super(Constants.PRODUCT_NAME,List.IMPLICIT);

        // Try to make the phone wrap long test names
        setFitPolicy(Choice.TEXT_WRAP_ON);
        
        m_ConnectCommand = new Command("Connect", Command.ITEM, 0);
        addCommand(m_ConnectCommand);
        m_BackCommand = new Command("Back", Command.ITEM, 1);
        addCommand(m_BackCommand);

        m_CommonCommands = new CommonCommands();
        m_CommonCommands.addToDisplayable(this);

        append(PREAMBLE,null);
        append(OCCLEVE_WIKI,null);
        append(ENGLISH_WIKIVERSITY,null);
        append(FRENCH_WIKIVERSITY,null);
        
        // By default, OCCLEVE_WIKI is selected.
        setSelectedIndex(1,true);

        // "Connect" is the default select command.
        setSelectCommand(m_ConnectCommand);
        
        setCommandListener(this);
    }

    /*Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable d)
    {
        try
        {
            commandAction_Inner(c,d);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    /*Subfunction for code clarity.*/
    public void commandAction_Inner(Command c,Displayable d) throws Exception
    {
    	if (c==m_ConnectCommand)
    	{
    		onConnectCommand();
    	}
    	else if (c==m_BackCommand)
    	{
    		// No need to refresh file list if user did nothing here.
            OccleveMobileMidlet.getInstance().displayFileChooser(false);
    	}
        else
        {
        	m_CommonCommands.commandAction(c,this);
        }
    }
    
    protected void onConnectCommand()
    {
    	int iSelIndex = getSelectedIndex();
    	String sSelection = getString(iSelIndex);
    	
        if (sSelection.equals(ENGLISH_WIKIVERSITY))
        {
            ServerBrowser browser =
               new ServerBrowser(Config.WIKIVERSITY_LIST_OF_QUIZZES_URL,
                                 Config.WIKIVERSITY_QUIZ_URL_STUB,
                                 Config.WIKIVERSITY_QUIZ_URL_SUFFIX);
            browser.populateAndDisplay();
        }
        else if (sSelection.equals(FRENCH_WIKIVERSITY))
        {
            ServerBrowser browser =
               new ServerBrowser(Config.FRENCH_WIKIVERSITY_LIST_OF_QUIZZES_URL,
                                 Config.FRENCH_WIKIVERSITY_QUIZ_URL_STUB,
                                 Config.WIKIVERSITY_QUIZ_URL_SUFFIX);
            browser.populateAndDisplay();
        }
        else if (sSelection.equals(OCCLEVE_WIKI))
        {
            ServerBrowser browser =
                    new ServerBrowser(Config.OCCLEVE_LIST_OF_TESTS_URL,
                                      Config.OCCLEVE_QUIZ_URL_STUB,
                                      Config.OCCLEVE_QUIZ_URL_SUFFIX);
            browser.populateAndDisplay();
        }
    	
    }
}

