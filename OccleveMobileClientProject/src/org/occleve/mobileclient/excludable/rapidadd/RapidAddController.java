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

package org.occleve.mobileclient.excludable.rapidadd;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;

/**Sequence is:
question type
then input ENG
(then input MWP)
then input BP
(then input MWC)
then input BC */
public class RapidAddController implements CommandListener,Excludable
{
    protected int m_iRecordStoreID;
    protected String m_sFilename;

    ////////////// Implementation of Excludable /////////////////////
    public void setTestFilename(String s) {m_sFilename=s;}
    public void setTestRecordStoreID(Integer i) {m_iRecordStoreID=i.intValue();}

    // Not relevant to this class:
    public void setQAIndex(Integer i) {}
    public void setScreenToReturnTo(Displayable d) {}
    /////////////////////////////////////////////////////////////////

    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    // The input sequence...
    protected final int TYPE = 0;
    protected final int ENGLISH = 1;
    protected final int MW_PINYIN = 2;
    protected final int BODY_PINYIN = 3;
    protected final int MW_HANZI = 4;
    protected final int BODY_HANZI = 5;

    protected int m_iCurrentStep;
    protected Displayable m_CurrentScreen;

    protected String m_sEnglish;
    protected String m_sPinyinMW;
    protected String m_sPinyinBody;
    protected String m_sHanziMW;
    protected String m_sHanziBody;

    protected RAQuestionTypeScreen m_TypeScreen;
    protected RapidAddTextBox m_LatinTextBox;
    protected RapidAddTextBox m_HanziTextBox;

    public RapidAddController() throws Exception
    {
        m_OKCommand = new Command("OK",Command.OK,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);
        CommandListener clr = this;

        m_TypeScreen = new RAQuestionTypeScreen(m_OKCommand,m_CancelCommand,clr);
        m_LatinTextBox = new RapidAddTextBox(m_OKCommand,m_CancelCommand,clr);
        m_HanziTextBox = new RapidAddTextBox(m_OKCommand,m_CancelCommand,clr);
    }

    /**Implementation of Excludable*/
    public void initialize()
    {
        m_iCurrentStep = TYPE;
        setCurrentScreen(m_TypeScreen);
    }

    protected void setCurrentScreen(Displayable d)
    {
        //m_CurrentScreen.setCommandListener(null);
        //d.setCommandListener(this);

        if (d==m_LatinTextBox) m_LatinTextBox.setString("");
        if (d==m_HanziTextBox) m_HanziTextBox.setString("");

        m_CurrentScreen = d;
        OccleveMobileMidlet.getInstance().setCurrentForm(d);
    }

   /**Implementation of CommandListener.*/
   public void commandAction(Command c, Displayable s)
   {
       if ((c==m_OKCommand) || (c==List.SELECT_COMMAND))
       {
           try
           {
               onOK();
           }
           catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
       }
       else if (c==m_CancelCommand)
       {
           OccleveMobileMidlet.getInstance().displayFileChooser();
       }
       else
       {
           OccleveMobileMidlet.getInstance().onError("Unknown command in RapidAddController.commandAction");
       }
   }

   /**Move to the next screen.*/
   protected void onOK() throws Exception
   {
       if (m_iCurrentStep==TYPE)
       {
           moveToEnglish();
       }
       else if (m_iCurrentStep==ENGLISH)
       {
           m_sEnglish = m_LatinTextBox.getString();

           if (m_TypeScreen.isCountableNoun())
               moveToPinyinMeasureWord();
           else
               moveToPinyinBody();
       }
       else if (m_iCurrentStep==MW_PINYIN)
       {
           m_sPinyinMW = m_LatinTextBox.getString();
           moveToPinyinBody();
       }
       else if (m_iCurrentStep==BODY_PINYIN)
       {
           m_sPinyinBody = m_LatinTextBox.getString();

           if (m_TypeScreen.isCountableNoun())
               moveToHanziMeasureWord();
           else
               moveToHanziBody();
       }
       else if (m_iCurrentStep==MW_HANZI)
       {
           m_sHanziMW = m_HanziTextBox.getString();
           moveToHanziBody();
       }
       else if (m_iCurrentStep==BODY_HANZI)
       {
           m_sHanziBody = m_HanziTextBox.getString();
           addNewTestQuestion();
       }
       else
       {
           OccleveMobileMidlet.getInstance().onError("Invalid value of m_iCurrentStep in RapidAddController");
       }
   }

   protected void clearFields()
   {
       m_sEnglish = null;
       m_sPinyinMW = null;
       m_sPinyinBody = null;
       m_sHanziMW = null;
       m_sHanziBody = null;
   }

   protected void moveToQuestionTypeScreen()
   {
       clearFields();

       m_iCurrentStep = TYPE;
       setCurrentScreen(m_TypeScreen);
   }

   protected void moveToEnglish()
   {
       m_iCurrentStep = ENGLISH;
       setTitle(m_LatinTextBox,"English");
       setCurrentScreen(m_LatinTextBox);
   }

   protected void moveToPinyinMeasureWord()
   {
       m_iCurrentStep = MW_PINYIN;
       setTitle(m_LatinTextBox,"Measure word (pinyin)");
       setCurrentScreen(m_LatinTextBox);
   }

   protected void moveToPinyinBody()
   {
       m_iCurrentStep = BODY_PINYIN;
       setTitle(m_LatinTextBox,"Chinese (pinyin)");
       setCurrentScreen(m_LatinTextBox);
   }

   protected void moveToHanziMeasureWord()
   {
       m_iCurrentStep = MW_HANZI;
       setTitle(m_HanziTextBox,"Measure word (hanzi)");
       setCurrentScreen(m_HanziTextBox);
   }

   protected void moveToHanziBody()
   {
       m_iCurrentStep = BODY_HANZI;
       setTitle(m_HanziTextBox,"Chinese (hanzi)");
       setCurrentScreen(m_HanziTextBox);
   }

   protected void setTitle(TextBox tb,String sPrompt)
   {
       // Get the time.
       String sTime = StaticHelpers.getDisplayableTime();
       tb.setTitle(m_iCurrentStep + " " + sPrompt + " " + sTime);
   }

   protected void addNewTestQuestion() throws Exception
   {
       // Will be e.g. "CNOUN" for a countable noun.
       String leCode = m_TypeScreen.getLanguageEntityCode();

       String sAddThis =
           "<zhqa>" + Constants.NEWLINE +
           "EN" + leCode + "##" + m_sEnglish + Constants.NEWLINE +
           "ZH" + leCode;

       if (m_TypeScreen.isCountableNoun())
       {
           sAddThis += "##MWP=" + m_sPinyinMW;
       }

       sAddThis += "##BP=" + m_sPinyinBody;

       if (m_TypeScreen.isCountableNoun())
       {
           sAddThis += "##MWC=" + m_sHanziMW;
       }

       sAddThis +=
           "##BC=" + m_sHanziBody + Constants.NEWLINE +
           "</zhqa>" + Constants.NEWLINE;

       VocabRecordStoreManager mgr = new VocabRecordStoreManager();
       mgr.appendToTest(m_iRecordStoreID,m_sFilename,sAddThis);

       moveToQuestionTypeScreen();
   }
}

