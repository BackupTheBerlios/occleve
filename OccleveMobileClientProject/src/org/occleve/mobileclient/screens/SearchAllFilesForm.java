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

import java.util.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

public class SearchAllFilesForm extends TextBox
implements CommandListener,Runnable
{
    protected ListOfTests m_ListOfTests;
    protected Alert m_ProgressAlert;
    protected Command m_NewTestCommand;
    protected Command m_OKCommand;

    public SearchAllFilesForm() throws Exception
    {
        super("Search all tests for:","",100,TextField.ANY);

        m_NewTestCommand = new Command("New test",Command.BACK,0);
        addCommand(m_NewTestCommand);

        m_OKCommand = new Command("OK",Command.OK,0);
        addCommand(m_OKCommand);

        setCommandListener(this);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_NewTestCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
        else if (c==m_OKCommand)
        {
            try
            {
                startSearch();
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command in JumpToForm.commandAction");
        }
    }

    /** Run the search in a background thread. This enables
    updating of the UI while the search is in progress. */
    protected void startSearch() throws Exception
    {
        m_ProgressAlert = new Alert(null, "Searching...", null, null);
        m_ProgressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(m_ProgressAlert);
        OccleveMobileMidlet.getInstance().displayAlert(m_ProgressAlert,this);

        new Thread(this).start();
    }

    public void run()
    {
        try
        {
            doSearch(getString());
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    protected void doSearch(String sSearchString) throws Exception
    {
        // MessageForm mf = new MessageForm("Searching... please wait");
        // OccleveMobileMidlet.getInstance().setCurrentForm(mf);
        // this.setString("Searching... please wait");
        // this.setConstraints(TextField.UNEDITABLE);

        ListOfTests list = new ListOfTests();
        int noOfTests = list.getSize();

        Vector filenamesOfMatchingTests = new Vector();
        Vector indicesOfMatchingQAs = new Vector();

        for (int i=0; i<noOfTests; i++)
        {
        	ListOfTestsEntry entry = list.getEntry(i);
            m_ProgressAlert.setString("Searching " + entry.getFilename());
            System.out.println("Searching " + entry.getFilename());

            // String sTestContents = StaticHelpers.readUnicodeFile("/" + sFilename);
            String sTestSource = Test.readTestSource(entry);

            if (sTestSource.indexOf(sSearchString) != -1)
            {
                boolean bMatchingQA =
                   doSearch_MatchFoundInRawTextOfFile(entry,sSearchString);
                if (bMatchingQA) return;
            }
        }

        System.out.println("Displaying search failed form");
        Alert alert = new Alert(null, "Couldn't find " + sSearchString, null, null);
        alert.setTimeout(5000);
        OccleveMobileMidlet.getInstance().displayAlert(alert,this);
    }

    /**Subfunction for code clarity.*/
    private boolean doSearch_MatchFoundInRawTextOfFile
    (
    	ListOfTestsEntry entry,String sSearchString
    )
    throws Exception
    {
        Test theTest = new Test(entry);
        VocabViewerScreen viewerForm = new VocabViewerScreen(entry.getFilename(),theTest);

        boolean bMatchingQA = viewerForm.searchVocabFile(sSearchString);
        if (bMatchingQA)
        {
            OccleveMobileMidlet.getInstance().setCurrentForm(viewerForm);
        }

        return bMatchingQA;
    }

}

