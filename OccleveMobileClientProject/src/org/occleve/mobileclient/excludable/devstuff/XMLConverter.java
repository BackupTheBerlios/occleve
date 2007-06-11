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

package org.occleve.mobileclient.excludable.devstuff;

import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

public class XMLConverter implements Runnable
{
    private boolean m_bUseJavaUTF;
    private Alert m_ProgressAlert;

    public XMLConverter(boolean bUseJavaUTF)
    {
        m_bUseJavaUTF = bUseJavaUTF;
    }

    public void run()
    {
        try
        {
            Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE,Gauge.CONTINUOUS_RUNNING);
            m_ProgressAlert = new Alert(null, "Converting...", null, null);
            m_ProgressAlert.setTimeout(Alert.FOREVER);
            m_ProgressAlert.setIndicator(gauge);
            OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

            convertAllFilesToXML();

            m_ProgressAlert.setString("Updating file chooser...");
            boolean bRefreshList = true;
            OccleveMobileMidlet.getInstance().displayFileChooser(bRefreshList);
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    public void convertAllFilesToXML() throws Exception
    {
        ListOfTests list = new ListOfTests();

        VocabRecordStoreManager mgr = new VocabRecordStoreManager();
        if (m_bUseJavaUTF)
            mgr.useJavaUTF();
        else
            mgr.useStandardUTF();

        for (int i=0; i<list.getSize(); i++)
        {
            String sFilename = list.getFilename(i);
            Integer iRSID = list.getRecordStoreIDByIndex(i);

            if (sFilename.startsWith("XML")==false)
            {
                m_ProgressAlert.setString("Converting " + sFilename);

                Test test = new Test(sFilename, iRSID);
                String sXML = test.toXML();

                // Add a newline since the convention is that all toXML() functions
                // don't add a trailing newline.
                sXML += Constants.NEWLINE;

                String sFilenameStub = StaticHelpers.stripEnding(sFilename,".txt");

                String sNameOfXmlFile = "XML " + sFilenameStub + ".xml";
                boolean bDoUIStuff = false;
                mgr.createFileInRecordStore(sNameOfXmlFile,
                        sXML,bDoUIStuff);
            }
        }
    }
}

