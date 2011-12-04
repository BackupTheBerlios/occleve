/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-11  Joe Gittings

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

package org.occleve.mobileclient.testing;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.test.*;

public class TestResultsForm extends Form
{
	private Test m_Test;
	private Command m_NewTestCommand;
	private Command m_RestartCommand;
	
	public TestResultsForm
    (
        Test theTest,TestResults testResults
    )
    {
        super("Results");
        m_Test = theTest;

        Label si;

        si = new Label("Total responses = " + testResults.getTotalResponseCount());
        addComponent(si);

        si = new Label("Wrong responses = " + testResults.getWrongResponseCount());
        addComponent(si);

        si = new Label("Accuracy = " + testResults.getAccuracyPercentage() + "%");
        addComponent(si);

        m_RestartCommand = new Command("Restart",0);
        addCommand(m_RestartCommand);

        m_NewTestCommand = new Command("New test",0);
        addCommand(m_NewTestCommand);
    }

    public void actionCommand(Command c)
    {
    	try
    	{
	    	if (c==m_RestartCommand)
	    		OccleveMobileMidlet.getInstance().displayTestOptions(m_Test);
	        else if (c==m_NewTestCommand)
	            OccleveMobileMidlet.getInstance().displayFileChooser();
	        else
	        	OccleveMobileMidlet.getInstance().onError("Unknown command type!");
    	}
    	catch (Exception e)
    	{
    		OccleveMobileMidlet.getInstance().onError(
    			"TestResultsForm.actionCommand",e);
    	}
    }
}

