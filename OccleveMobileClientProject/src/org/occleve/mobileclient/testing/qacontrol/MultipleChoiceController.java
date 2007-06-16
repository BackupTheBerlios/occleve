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

package org.occleve.mobileclient.testing.qacontrol;

import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

public class MultipleChoiceController
{
    protected TestController m_TestController;
    protected Test m_Test;
    protected QADirection m_QADirection;
    protected TestResults m_TestResults;

    public MultipleChoiceController(TestController tc,Test theTest,
                                     QADirection direction,
                                     TestResults testResults)
    throws Exception
    {
        m_TestController = tc;
        m_Test = theTest;
        m_QADirection = direction;
        m_TestResults = testResults;
    }

    public TestController getTestController() {return m_TestController;}

    public void setVisible()
    {
        Displayable disp = m_TestController.getQuestionView().getDisplayable();
        OccleveMobileMidlet.getInstance().setCurrentForm(disp);
        m_TestController.getQuestionView().doRepainting();
    }
}
