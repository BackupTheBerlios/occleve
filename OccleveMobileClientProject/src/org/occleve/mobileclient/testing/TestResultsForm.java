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

package org.occleve.mobileclient.testing;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.test.*;

public class TestResultsForm extends Form implements CommandListener
{
	private Test m_Test;
	private Command m_RestartCommand;
	
	public TestResultsForm
    (
        Test theTest,TestResults testResults
    )
    {
        super("Results");
        m_Test = theTest;

        String text;
        StringItem si;

        text = "Total responses = " + testResults.getTotalResponseCount() +
               Constants.NEWLINE;
        si = new StringItem(null,text);
        StaticHelpers.safeSetFont(si,OccleveMobileFonts.DETAILS_FONT);
        append(si);

        text = "Wrong responses = " + testResults.getWrongResponseCount() +
               Constants.NEWLINE;
        si = new StringItem(null,text);
        StaticHelpers.safeSetFont(si,OccleveMobileFonts.DETAILS_FONT);
        append(si);

        text =
            "Accuracy = " +
            testResults.getAccuracyPercentage() + "%" +
            Constants.NEWLINE;
        si = new StringItem(null,text);
        StaticHelpers.safeSetFont(si,OccleveMobileFonts.DETAILS_FONT);
        append(si);

        m_RestartCommand = new Command("Restart", Command.ITEM, 0);
        addCommand(m_RestartCommand);

        addCommand(new Command("New test", Command.BACK, 0));
        setCommandListener(this);
    }

    /**Implementation of CommandListener.
    Handler for BACK and EXIT commands.*/
    public void commandAction(Command c, Displayable s)
    {
    	/*
        if (c.getCommandType() == Command.EXIT)
        {
            OccleveMobileMidlet.getInstance().notifyDestroyed();
        }
        */
    	
    	try
    	{
	    	if (c==m_RestartCommand)
	    	{
	    		OccleveMobileMidlet.getInstance().displayTestOptions(m_Test);
	    	}
	        else if (c.getCommandType() == Command.BACK)
	        {
	            OccleveMobileMidlet.getInstance().displayFileChooser();
	        }
	        else
	        {
	            System.err.println("Unknown command type!");
	            System.exit( -1);
	        }
    	}
    	catch (Exception e)
    	{
    		OccleveMobileMidlet.getInstance().onError(e);
    	}
    }
}

