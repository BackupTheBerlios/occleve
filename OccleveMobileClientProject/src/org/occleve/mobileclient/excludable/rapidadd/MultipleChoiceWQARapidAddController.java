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
@version 0.9.4
*/

package org.occleve.mobileclient.excludable.rapidadd;

import java.util.Vector;
import javax.microedition.lcdui.*;

public class MultipleChoiceWQARapidAddController extends RapidAddController
{
	private RapidAddTextBox m_QuestionTextBox;
	private RACorrectIncorrectScreen m_CorrectIncorrectScreen;
	private RapidAddTextBox m_Answer;
	private RapidAddTextBox m_Feedback;
	private Alert m_YesNoAlert;

    protected Command m_YesCommand;
    protected Command m_NoCommand;
	
	public MultipleChoiceWQARapidAddController() throws Exception
	{
		m_ScreenSequence = new Vector();

		m_QuestionTextBox = new RapidAddTextBox("1/5: Question",m_OKCommand,m_CancelCommand,this);
		m_CorrectIncorrectScreen = new RACorrectIncorrectScreen(m_OKCommand,m_CancelCommand,this);
		m_Answer = new RapidAddTextBox("3/5: Possible answer",m_OKCommand,m_CancelCommand,this);
		m_Feedback = new RapidAddTextBox("4/5: Answer feedback",m_OKCommand,m_CancelCommand,this);

		m_YesNoAlert = new Alert(null,"5/5: Add another response?", 
        							null, AlertType.CONFIRMATION);
		m_YesNoAlert.setTimeout(Alert.FOREVER);
        m_YesCommand = new Command("Yes",Command.OK,0);
        m_NoCommand = new Command("No",Command.CANCEL,0);
		m_YesNoAlert.addCommand(m_YesCommand);
		m_YesNoAlert.addCommand(m_NoCommand);
		m_YesNoAlert.setCommandListener(this);
        
        m_ScreenSequence.addElement(m_QuestionTextBox);
        m_ScreenSequence.addElement(m_CorrectIncorrectScreen);
        m_ScreenSequence.addElement(m_Answer);
        m_ScreenSequence.addElement(m_Feedback);
        m_ScreenSequence.addElement(m_YesNoAlert);
	}
	
	protected void addNewTestQuestion() throws Exception
	{
		// TODO Auto-generated method stub

	}

   protected void onOK() throws Exception
   {
	   int iLastIndex = m_ScreenSequence.size()-1;
       if (m_iCurrentScreenIndex < iLastIndex)
       {
    	   moveToNextScreen();
       }
       else
       {
    	   
       }
   }
}

