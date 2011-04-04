/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2008-11  Joe Gittings

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
@version 0.9.10
*/

package org.occleve.mobileclient.screens;

import com.sun.lwuit.*;
import com.sun.lwuit.layouts.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.components.OccleveList;
import org.occleve.mobileclient.serverbrowser.*;

public class ChooseQuizServerScreen extends Form
{
	protected OccleveList m_List = new OccleveList();
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

    public ChooseQuizServerScreen() throws Exception
    {
        super(Constants.PRODUCT_NAME);
        
        setScrollable(false); // Otherwise the List won't scroll.
        setLayout(new BorderLayout());
        addComponent(BorderLayout.CENTER,m_List);
        
        m_ConnectCommand = new Command("Connect");
        addCommand(m_ConnectCommand);
        m_BackCommand = new Command("Back");
        addCommand(m_BackCommand);

        m_CommonCommands = new CommonCommands();
        m_CommonCommands.addToForm(this);

        m_List.addItem(PREAMBLE);
        m_List.addItem(OCCLEVE_WIKI);
        m_List.addItem(ENGLISH_WIKIVERSITY);
        m_List.addItem(FRENCH_WIKIVERSITY);
        
        // By default, OCCLEVE_WIKI is selected.
        m_List.setSelectedIndex(1,true);

        // "Connect" is the default select command.
        // setSelectCommand(m_ConnectCommand);
    }

    protected void append(String s)
    {
    	TextArea ta = new TextArea(s,2,2);
    	addComponent(ta);
    }

    public void actionCommand(Command c)
    {
        try
        {
            actionCommand_Inner(c);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    public void actionCommand_Inner(Command c) throws Exception
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
        	m_CommonCommands.actionCommand(c);
        }
    }
    
    protected void onConnectCommand()
    {
    	String sSelection = (String)m_List.getSelectedItem();
    	System.out.println("Connect: " + sSelection);
    	
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

