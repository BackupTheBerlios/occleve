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

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.testing.test.*;

public class VocabViewerScreen extends Form
implements CommandListener,ItemCommandListener
{
    protected Test m_Test;

    protected Command m_NewTestCommand;
    protected Command m_SearchThisFileCommand;
    protected Command m_ExitCommand;
    protected Command m_ScrollToStartCommand;
    protected Command m_ScrollToMiddleCommand;
    protected Command m_ScrollToEndCommand;
    protected Command m_EditCommand;

    protected TextBox m_QuestionNoTextBox;

    public VocabViewerScreen(String sHeading,Test theTest)
    {
        super(sHeading);
        m_Test = theTest;

        // DOESN'T WORK ON K300 (BUT DOES ON EMULATOR)
        // Passing in null removes the redundant title and saves
        // screen space.
        // super(null);

        try
        {
            populate(theTest);
        }
        catch (IllegalArgumentException e)
        {
            // On Sony Ericsson phones, this is thrown if you try to append
            // more than 256 Items to a Form.
        }

        m_NewTestCommand = new Command("New test", Command.BACK, 0);
        addCommand(m_NewTestCommand);

        m_SearchThisFileCommand = new Command("Search", Command.ITEM, 0);
        addCommand(m_SearchThisFileCommand);

        m_ExitCommand = new Command("Exit", Command.EXIT, 0);
        addCommand(m_ExitCommand);

        m_ScrollToStartCommand = new Command("Scroll to start", Command.ITEM, 0);
        addCommand(m_ScrollToStartCommand);

        m_ScrollToMiddleCommand = new Command("Scroll to middle", Command.ITEM, 0);
        addCommand(m_ScrollToMiddleCommand);

        m_ScrollToEndCommand = new Command("Scroll to end", Command.ITEM, 0);
        addCommand(m_ScrollToEndCommand);

        m_EditCommand = new Command("Edit", Command.ITEM, 0);
        addCommand(m_EditCommand);

        setCommandListener(this);
    }

    public void populate(Test theTest) throws IllegalArgumentException
    {
        // Clear any existing items.
        deleteAll();

        // This is a definite possibility until the mobile client
        // supports all wikiversity quiz types.
        if (theTest.getQACount()==0)
        {
            StringItem empty = new StringItem(null,Constants.EMPTY_QUIZ_MSG);
            append(empty);
            return;
        }

        for (int i=0; i<theTest.getQACount(); i++)
        {
            String sQuestionNo = Integer.toString(i+1);
            QA currentQA = theTest.getQA(i);
            Vector items = currentQA.getEntireContentsAsItems();
            for (int i2=0; i2<items.size(); i2++)
            {
                StringItem si = (StringItem)items.elementAt(i2);

                if (i2==0)
                {
                    si.setText(sQuestionNo + ". " + si.getText());
                }

                StaticHelpers.safeSetFont(si,OccleveMobileFonts.SMALL_FONT);
                append(si);

                // If it's the last item for this LQA, add a newline.
                if (i2==(items.size()-1))
                {
                    ////si.setText(si.getText() + Constants.NEWLINE);
                    append(new StringItem(null,Constants.NEWLINE));
                }

                // You can click Play on a ListenItem.
                if (si instanceof ListenItem)
                {
                    si.setItemCommandListener(this);
                }
            }
        }
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if ((s!=null) && (s==m_QuestionNoTextBox))
        {
            String sQuestionNo = m_QuestionNoTextBox.getString();
            int iQuestionIndex = Integer.parseInt(sQuestionNo) - 1;
            ExcludableHooks.editQA(m_Test.getFilename(),
                                   m_Test.getRecordStoreID(),
                                   new Integer(iQuestionIndex),this);
            return;
        }

        if (c==m_NewTestCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
        else if (c==m_SearchThisFileCommand)
        {
//            startSearch();
        }
        else if (c==m_ExitCommand)
        {
            OccleveMobileMidlet.getInstance().notifyDestroyed();
        }
        else if (c==m_ScrollToStartCommand)
        {
            Item qaItem = get(0);
            StaticHelpers.safeSetCurrentItem(qaItem);
        }
        else if (c==m_ScrollToMiddleCommand)
        {
            int iIndex = (size()-1) / 2;
            Item qaItem = get(iIndex);
            StaticHelpers.safeSetCurrentItem(qaItem);
        }
        else if (c==m_ScrollToEndCommand)
        {
            int iLastItemIndex = size()-1;
            Item qaItem = get(iLastItemIndex);
            StaticHelpers.safeSetCurrentItem(qaItem);
        }
        else if (c==m_EditCommand)
        {
            m_QuestionNoTextBox =
                    new TextBox("Question number","",10,TextField.NUMERIC);

            Command okCmd = new Command("Edit it",Command.OK,0);
            m_QuestionNoTextBox.addCommand(okCmd);

            OccleveMobileMidlet.getInstance().setCurrentForm(m_QuestionNoTextBox);
            m_QuestionNoTextBox.setCommandListener(this);
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command in VocabViewerScreen.commandAction");
        }
    }

    public boolean searchVocabFile(String sFindMe)
    {
        for (int i=0; i<m_Test.getQACount(); i++)
        {
            QA currentQA = m_Test.getQA(i);

            if (currentQA.containsString(sFindMe))
            {
                // System.out.println("In VocabViewerScreen.searchVocabFile:");
                // System.out.println("sFindMe = " + sFindMe);
                // System.out.println("Matching QA = " + currentQA.getEntireContentsAsString());
                // System.out.println("Matching QA index = " + i);

                Item qaItem = get(i);
                StaticHelpers.safeSetCurrentItem(qaItem);
                return true;
            }
        }

        return false;
    }

    /**Dumps the view of this test as seen in this viewer to a file
    in the local filesystem.
    See http://developers.sun.com/techtopics/mobility/apis/articles/fileconnection/
    */
    public void printToFile() throws Exception
    {
        FileConnection filecon = (FileConnection)
                                 Connector.open("file:///root1/dumpfile.txt");

        // Always check whether the file or directory exists.
        // Create the file if it doesn't exist.
        if (!filecon.exists()) filecon.create();

        OutputStream os = filecon.openOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(os,"UTF-8");

        for (int i=0; i<size(); i++)
        {
            StringItem item = (StringItem)get(i);
            String sLine = item.getText();
            writer.write(sLine + Constants.CRLF);
        }

        writer.flush();
        writer.close();
        os.close();
        filecon.close();

        // Display confirmation
        String sMsg = "Test " + m_Test.getFilename() +
                      " successfully printed to file ";
        Alert alert = new Alert(null,sMsg,null,null);
        OccleveMobileMidlet.getInstance().displayAlertThenFileChooser(alert);
    }

    /**Implementation of ItemCommandListener.*/
    public void commandAction(Command c,Item item)
    {
        System.out.println("Item clicked");

        try
        {
            ListenItem listen = (ListenItem) item;
            listen.play();
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }
}

