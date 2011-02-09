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

package org.occleve.mobileclient;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.testing.*;

public class ExcludableHooks
{
    public static Class classForName(String sClassName)
    throws Exception
    {
        try
        {
            return Class.forName(sClassName);
        }
        catch (Exception e)
        {
            String sErr =
                    "This feature is not supported in this release of " +
                    Constants.PRODUCT_NAME;
            throw new Exception(sErr);
        }
    }

    public static void displayDevStuffScreen(ListOfTestsEntry entry)
    throws Exception
    {
        Class dvsClass = classForName("org.occleve.mobileclient.excludable.devstuff.DevStuffScreen");
        Excludable dvs = (Excludable)dvsClass.newInstance();

        dvs.setListOfTestsEntry(entry);
        //dvs.setTestFilename(sSelectedFilename);
        //dvs.setTestRecordStoreID(iSelectedRecordStoreID);

        OccleveMobileMidlet.getInstance().setCurrentForm((Displayable)dvs,true);
    }

    public static void displayRapidAdd(ListOfTestsEntry entry)
                                       throws Exception
    {
        Class racClass = classForName("org.occleve.mobileclient.excludable.rapidadd.MultipleChoiceWQARapidAddController");
        Excludable rac = (Excludable)racClass.newInstance();

        //rac.setTestFilename(sFilename);
        //rac.setTestRecordStoreID(iRecordStoreID);
        rac.setListOfTestsEntry(entry);
        rac.initialize();
    }

    public static void editQA(ListOfTestsEntry entry,
                              Integer iQAIndex,
                              Displayable screenToReturnTo)
    {
        if (entry.getRecordStoreID()==null)
        {
            OccleveMobileMidlet.getInstance().onError("Error - record store ID is null");
        }

        try
        {
            Class reClass = classForName("org.occleve.mobileclient.excludable.raweditor.RawEditor");
            Excludable rawEditor = (Excludable)reClass.newInstance();

            rawEditor.setQAIndex(iQAIndex);
            rawEditor.setScreenToReturnTo(screenToReturnTo);
            //rawEditor.setTestFilename(sFilename);
            //rawEditor.setTestRecordStoreID(iRecordID);
            rawEditor.setListOfTestsEntry(entry);
            rawEditor.initialize();

            OccleveMobileMidlet.getInstance().setCurrentForm((Displayable)rawEditor);
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }
}

