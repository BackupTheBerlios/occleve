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

package org.occleve.mobileclient.screens;

import java.util.*;
import com.sun.lwuit.*;
import com.sun.lwuit.layouts.*;
import org.occleve.mobileclient.*;

public class ShowGPLForm extends Form
{
    protected Command m_NewTestCommand;

    public ShowGPLForm() throws Exception
    {
        super("Copyright and license");
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        
        String sCopyright =
            "Copyright (C) 2007-11 Joe Gittings, except for: " + Constants.NEWLINE +
            "Floating point classes copyright " +
            "(C) 2003, 2004 David Clausen. " + Constants.NEWLINE +
            "XML parser copyright " +
            "(C) 2000 Michael Claßen. " + Constants.NEWLINE +
            "OpenBaseMovil database classes " +
            "copyright (C) 2004-2008 Elondra S.L." + Constants.NEWLINE +
        	"All rights reserved. " + Constants.NEWLINE;
        sCopyright += Constants.NEWLINE + Constants.NEWLINE;
  //      TextArea comp = new TextArea(sCopyright,2,2);
   //     addComponent(comp);

        String sPreamble =
            "This program is free software; you can redistribute it and/or " +
            "modify it under the terms of the GNU General Public License " +
            "as published by the Free Software Foundation; either version 2 " +
            "of the License, or (at your option) any later version. " +
            "This program is distributed in the hope that it will be useful, " +
            "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the " +
            "GNU General Public License for more details." +
            Constants.NEWLINE + Constants.NEWLINE;
     //   comp = new TextArea(sPreamble,2,2);
      //  addComponent(comp);

        String sLicence = StaticHelpers.readUnicodeFile("/COPYING");
        
        TextArea comp = new TextArea(sCopyright + Constants.NEWLINE +
        	sPreamble + Constants.NEWLINE + sLicence,500,2);
        addComponent(comp);
        
        /* Vector vLicense = StaticHelpers.stringToVector(sLicense);
        String sLine = "";
        for (int i=0; i<vLicense.size(); i++)
        {
            sLine += " " + (String)vLicense.elementAt(i);
            if (   ((i%10)==0)   ||  (i==vLicense.size()-1)   )
            {
                comp = new TextArea(sLine);
                // StaticHelpers.safeSetFont(si, OccleveMobileFonts.DETAILS_FONT);
                addComponent(comp);
                sLine = "";
            }
        } */

        m_NewTestCommand = new Command("New test",0);
        addCommand(m_NewTestCommand);
    }

    protected void actionCommand(Command c)
    {
        if (c==m_NewTestCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
    }
}
