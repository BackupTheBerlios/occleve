/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-9  Joe Gittings

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
@version 0.9.7
*/

package org.occleve.mobileclient.excludable.devstuff;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;

/**Rather vaguely named class used for creating a new quiz.*/
public class VocabRecordFilenameTextBox extends TextBox
implements CommandListener
{
	protected DevStuffScreen m_DevStuffScreen;
	protected DevStuffChildScreenHelper m_Helper;
    protected Command m_OKCommand;

    public VocabRecordFilenameTextBox(DevStuffScreen dvs)
    throws Exception
    {
        super("Name of new test:","",100,TextField.ANY);

        m_DevStuffScreen = dvs;
        m_Helper = new DevStuffChildScreenHelper(this,dvs);

        m_OKCommand = new Command("OK",Command.OK,0);
        addCommand(m_OKCommand);

        setCommandListener(this);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        if (c==m_OKCommand)
        {
            try
            {            	
            	VocabRecordStoreManager mgr =
            		OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();
                mgr.createEmptyTest(getString());
                
                m_DevStuffScreen.setQuizListNeedsRefreshing();
                OccleveMobileMidlet.getInstance().setCurrentForm(m_DevStuffScreen);
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else
        {
        	m_Helper.commandAction(c, s);
        }
    }
}

