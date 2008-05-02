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
@version 0.9.6
*/

package org.occleve.mobileclient.excludable.rapidadd;

import java.util.Vector;
import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.wikiversity.*;
import org.occleve.mobileclient.recordstore.VocabRecordStoreManager;
import org.occleve.mobileclient.testing.*;

public class MultipleChoiceWQARapidAddController extends RapidAddController
{
	/**Answers are stored here as they are defined.*/
	private Vector m_vAnswers;
	
	private RapidAddTextBox m_QuestionTextBox;
	private RapidAddTextBox m_AnswerBox;
	private RACorrectIncorrectScreen m_CorrectIncorrectScreen;
	private RapidAddTextBox m_FeedbackBox;
	private Alert m_YesNoAlert;

    protected Command m_YesCommand;
    protected Command m_NoCommand;
	
	public MultipleChoiceWQARapidAddController() throws Exception
	{
		m_ScreenSequence = new Vector();

		m_QuestionTextBox = new RapidAddTextBox("1/5: Question",m_OKCommand,m_CancelCommand,this);
		m_AnswerBox = new RapidAddTextBox("2/5: Possible answer",m_OKCommand,m_CancelCommand,this);
		m_CorrectIncorrectScreen = new RACorrectIncorrectScreen("3/5: Correct?",m_OKCommand,m_CancelCommand,this);
		m_FeedbackBox = new RapidAddTextBox("4/5: Answer feedback",m_OKCommand,m_CancelCommand,this);

		m_YesNoAlert = new Alert(null,"5/5: Add another answer?", 
        							null, AlertType.CONFIRMATION);
		m_YesNoAlert.setTimeout(Alert.FOREVER);
        m_YesCommand = new Command("Yes",Command.OK,0);
        m_NoCommand = new Command("No",Command.CANCEL,0);
		m_YesNoAlert.addCommand(m_YesCommand);
		m_YesNoAlert.addCommand(m_NoCommand);
		m_YesNoAlert.setCommandListener(this);
        
        m_ScreenSequence.addElement(m_QuestionTextBox);
        m_ScreenSequence.addElement(m_AnswerBox);
        m_ScreenSequence.addElement(m_CorrectIncorrectScreen);
        m_ScreenSequence.addElement(m_FeedbackBox);
        m_ScreenSequence.addElement(m_YesNoAlert);
        
        m_vAnswers = new Vector();
	}

	protected void clear()
	{
		m_QuestionTextBox.setString("");
		m_AnswerBox.setString("");
		m_FeedbackBox.setString("");
	}
	
	/**Retrieve the additions file from the recordstore.
	If one doesn't exist yet, create it.
	Then add the question that's just been defined to the end of it.*/
	protected void addNewTestQuestion() throws Exception
	{
        // 0.9.6----VocabRecordStoreManager mgr = new VocabRecordStoreManager();
    	VocabRecordStoreManager mgr = OccleveMobileMidlet.getInstance().getVocabRecordStoreManager();
		
		String sAdditionsFilename = m_Entry.getFilename() + Config.ADDITIONS_FILENAME_EXT;
		Integer rsid = mgr.findRecordByFilename(sAdditionsFilename);
		
		if (rsid==null)
		{
			int newRecordID =
				mgr.createFileInRecordStore(sAdditionsFilename,"",false);
			rsid = new Integer(newRecordID);
		}

		MultipleChoiceWikiversityQA qa =
			new MultipleChoiceWikiversityQA(m_QuestionTextBox.getString());

		for (int i=0; i<m_vAnswers.size(); i++)
		{
			WikiversityAnswer ans = (WikiversityAnswer)m_vAnswers.elementAt(i);
			qa.addAnswer(ans);
		}
		
		String sQuestionWikitext = qa.toWikitext();
		
		ListOfTestsEntry entry = new ListOfTestsEntry(sAdditionsFilename,rsid,null);
		mgr.appendToTest(entry,sQuestionWikitext);		
	}

   /**Override of RapidAddController.commandAction().*/
   public void commandAction(Command c, Displayable s)
   {
       if (c==m_YesCommand)
       {
    	   addAnotherAnswer();
       }
       else if (c==m_NoCommand)
       {
    	   try
    	   {
    		   addNewTestQuestion();
    	   }
    	   catch (Exception e)
    	   {
    		   OccleveMobileMidlet.getInstance().onError(e);
    	   }
       }
       else
       {
    	   super.commandAction(c,s);
       }
   }

   protected void addAnotherAnswer()
   {
	   // Save the response just defined.
	   WikiversityAnswer ans = new WikiversityAnswer(m_AnswerBox.getString());
	   ans.setCorrect(m_CorrectIncorrectScreen.isCorrect());
	   ans.setFeedback(m_FeedbackBox.getString());
	   m_vAnswers.addElement(ans);

	   // Clear all screens.
	   clear();
	   
	   // Now move back in the screen sequence so the user can
	   // enter an additional response.
	   m_iCurrentScreenIndex = 1;
	   Displayable curr =
		   (Displayable)m_ScreenSequence.elementAt(m_iCurrentScreenIndex);
	   setCurrentScreen(curr);
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
    	   // The last screen in the sequence (the YesNoAlert) doesn't
    	   // have an OK button, so this shouldn't get called.
    	   throw new Exception("Unknown screen in sequence");
       }
   }
}

