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

import javax.microedition.lcdui.*;

/**Used for selecting whether a multiple choice response
is correct or not.*/
public class RACorrectIncorrectScreen extends javax.microedition.lcdui.List
{
    protected static String CORRECT = "Correct";
    protected static String INCORRECT = "Incorrect";

    public RACorrectIncorrectScreen(String sTitle,Command okCommand,
    								Command cancelCommand,
    								CommandListener clr)
    throws Exception
    {
        super(sTitle,List.IMPLICIT);

        addCommand(okCommand);
        addCommand(cancelCommand);
        setCommandListener(clr);

        append(CORRECT,null);
        append(INCORRECT,null);
    }

    public boolean isCorrect()
    {
        int iIndex = getSelectedIndex();
        String sChoice = getString(iIndex);
        return (sChoice.equals(CORRECT));
    }
}

