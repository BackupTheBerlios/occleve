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

import java.util.*;
import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.testing.ListOfTestsEntry;

public abstract class RapidAddController
implements CommandListener,Excludable
{
	protected ListOfTestsEntry m_Entry;
    //protected int m_iRecordStoreID;
    //protected String m_sFilename;

    ////////////// Implementation of Excludable /////////////////////
    //public void setTestFilename(String s) {m_sFilename=s;}
    //public void setTestRecordStoreID(Integer i) {m_iRecordStoreID=i.intValue();}
    public void setListOfTestsEntry(ListOfTestsEntry e) {m_Entry = e;}

    // Not relevant to this class:
    public void setQAIndex(Integer i) {}
    public void setScreenToReturnTo(Displayable d) {}
    /////////////////////////////////////////////////////////////////

    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    // The input sequence...

    /**All the screens in the sequence, as Displayable objects.
    This can include the same instance of a Displayable more than once.*/
    protected Vector m_ScreenSequence;
    
    protected int m_iCurrentScreenIndex;

    public RapidAddController() throws Exception
    {
        m_OKCommand = new Command("OK",Command.OK,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);
        CommandListener clr = this;        
    }

    /**Implementation of Excludable*/
    public void initialize()
    {
        Displayable curr = (Displayable)m_ScreenSequence.firstElement();
        setCurrentScreen(curr);
        m_iCurrentScreenIndex = 0;
    }

    protected void setCurrentScreen(Displayable d)
    {
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

   public void moveToNextScreen()
   {
	   m_iCurrentScreenIndex++;
	   Displayable curr =
		   (Displayable)m_ScreenSequence.elementAt(m_iCurrentScreenIndex);
	   setCurrentScreen(curr);	   
   }
   
   protected void setTitle(TextBox tb,String sPrompt)
   {
       // Get the time.
       String sTime = StaticHelpers.getDisplayableTime();
       tb.setTitle(sTime);
   }

   protected abstract void onOK() throws Exception;
   protected abstract void addNewTestQuestion() throws Exception;
}

