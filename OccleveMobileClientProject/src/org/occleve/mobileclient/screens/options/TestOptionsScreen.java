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

package org.occleve.mobileclient.screens.options;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

public abstract class TestOptionsScreen extends Form
implements CommandListener,ItemCommandListener
{
    protected Test m_Test;

    protected StringItem m_StartTestItem = new StringItem(null,"Start test");

    protected String SEQUENTIAL = "In sequence";
    protected String RANDOM = "Random";
    protected ChoiceGroup m_SequentialOrRandomChoiceGroup;

    //// Took this out in 0.9.3 as it was just confusing users.
    //protected String CANVAS = "Canvas view";
    //protected String FORM = "Form view";
    //protected ChoiceGroup m_ViewChoiceGroup;

    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    protected abstract QADirection getQADirection() throws Exception;

    public TestOptionsScreen() throws Exception
    {
        super(Constants.PRODUCT_NAME);

        m_OKCommand = new Command("OK",Command.OK,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);

        addCommand(m_OKCommand);
        addCommand(m_CancelCommand);
        setCommandListener(this);

        // Append items to this form.

        append(m_StartTestItem);
        m_StartTestItem.setItemCommandListener(this);
        m_StartTestItem.setDefaultCommand(m_OKCommand);

        String[] orderChoices = {SEQUENTIAL,RANDOM};
        m_SequentialOrRandomChoiceGroup =
            new ChoiceGroup(null,ChoiceGroup.POPUP,orderChoices,null);
        append(m_SequentialOrRandomChoiceGroup);

        //String[] viewChoices = {CANVAS,FORM};
        //m_ViewChoiceGroup =
        //    new ChoiceGroup(null,ChoiceGroup.POPUP,viewChoices,null);
        //append(m_ViewChoiceGroup);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_OKCommand)
        {
            try
            {
                runTest();
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else if (c==m_CancelCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command in TestOptionsScreen.commandAction");
        }
    }

    protected void runTest() throws Exception
    {
        int i = m_SequentialOrRandomChoiceGroup.getSelectedIndex();
        String sChoice = m_SequentialOrRandomChoiceGroup.getString(i);
        boolean bRandom = (sChoice.equals(RANDOM));

        /*
        MagicTypewriterView view;
        i = m_ViewChoiceGroup.getSelectedIndex();
        sChoice = m_ViewChoiceGroup.getString(i);
        boolean bFormView = (sChoice.equals(FORM));
        */

        QADirection direction = getQADirection();

        TestController tc;
        if (bRandom)
            tc = new RandomTestController(m_Test,direction);
        else
            tc = new SequentialTestController(m_Test,direction);

        tc.setVisible();
    }

    public void makeVisible(Test test)
    {
        m_Test = test;
        OccleveMobileMidlet.getInstance().setCurrentForm(this);
    }

    /*Implementation of ItemCommandListener.*/
    public void commandAction(Command c, Item item)
    {
        try
        {
            if (item==m_StartTestItem)
            {
                OccleveMobileMidlet.getInstance().beep();
                runTest();
            }
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }
}

