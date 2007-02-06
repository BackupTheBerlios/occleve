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
@version 0.9.0
*/

package org.occleve.mobileclient.excludable.devstuff;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.util.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.*;

/**Sends the vocab records in the RecordStore to a PC that
is running the TestReceiver application.*/
public class VocabRecordTransmitter implements Runnable
{
    private final String FIRST_LINE = "MickeyMouse";
    private final int PORT = 5511;

    protected String m_sIPAddressForThread;
    protected Alert m_ProgressAlert;

    public VocabRecordTransmitter()
    {
    }

    public void transmitAllTestFiles(String sIPAddress)
    {
        try
        {
            m_sIPAddressForThread = sIPAddress;
            Thread toPreventBlocking = new Thread( this );
            toPreventBlocking.start();
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    public void run()
    {
        try
        {
            Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE,Gauge.CONTINUOUS_RUNNING);
            m_ProgressAlert = new Alert(null, "Reading list of tests...", null, null);
            m_ProgressAlert.setTimeout(Alert.FOREVER);
            m_ProgressAlert.setIndicator(gauge);
            OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

            ListOfTests list = new ListOfTests();
            String sIPAddress = m_sIPAddressForThread;

            for (int i=0; i<list.getSize(); i++)
            {
               String sFilename = list.getFilename(i);
               Integer iRSID = list.getRecordStoreIDByIndex(i);
               String sSource = Test.readTestSource(sFilename,iRSID);
               Vector vSource = StaticHelpers.stringToVector(sSource);

               String sCompletion = " (" + (i+1) + "/" + list.getSize() + ")";
               m_ProgressAlert.setString("Sending " + sFilename + sCompletion);
               //////pause();

               transmitOneTestFile(sIPAddress, sFilename, vSource);
            }

            String sMsg = "Sent " + list.getSize() + " tests over the Internet";
            Alert alert = new Alert(null, sMsg, null, null);
            OccleveMobileMidlet.getInstance().displayAlertThenFileChooser(alert);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void transmitOneTestFile(String sIPAddress,String sFilename,
                                       Vector vTestSource)
    throws Exception
    {
        String sURL = "http://" + sIPAddress + ":" + PORT;
        System.out.println("sURL = " + sURL);

        HttpConnection hc = (HttpConnection)Connector.open(sURL);
        trace(sFilename,"Successfully opened HttpConnection to " + sURL);

        hc.setRequestMethod(HttpConnection.POST);
        trace(sFilename,"Set request method to POST");

        OutputStream ostream = hc.openOutputStream();
        trace(sFilename,"Opened OutputStream ok");
        OutputStreamWriter writer = new OutputStreamWriter(ostream,"UTF-8");

        writeLineShiftTen(FIRST_LINE,writer);
        writeLineShiftTen(sFilename,writer);
        trace(sFilename,"Wrote first 2 lines ok");

        /*
        trace("","About to call flush...");
        writer.flush();
        trace("","Called flush ok");
        */

        for (int i=0; i<vTestSource.size(); i++)
        {
            trace(sFilename,"Writing line " + (i+1) );

            String sLine = (String)vTestSource.elementAt(i);
            writeLineShiftTen(sLine,writer);
            //writer.flush();

            String sTrace = "Wrote line " + (i+1) + " of " + vTestSource.size();
            trace("",sTrace);
            System.out.println(sTrace);
        }
        trace(sFilename,"Wrote test contents ok");

        writer.flush();

        // An ugly hack - catch the exception thrown by the lack of a response
        // message from the server (I think I'd need to convert the server
        // code to a proper servlet to fix this).
        try
        {
            String sDummyResponse = hc.getResponseMessage();
            System.out.println(sDummyResponse);
        }
        catch (IOException e) {System.err.println(e);}

        writer.close();
        ostream.close();
        hc.close();
        trace(sFilename,"Closed everything ok");
    }

    /**Writes the supplied string to the OutputStreamWriter,
    but with each character's value increased by ten.*/
    protected void writeLineShiftTen(String s,OutputStreamWriter writer)
    throws Exception
    {
        StringBuffer sbShifted = new StringBuffer(s);

        for (int i=0; i<sbShifted.length(); i++)
        {
            char c = s.charAt(i);
            c += 10;
            sbShifted.setCharAt(i,c);
        }

        writer.write(sbShifted.toString(),0,sbShifted.length());
        writer.write(Constants.NEWLINE,0,Constants.NEWLINE.length());
    }

    private void trace(String sFilename,String sCurrentAction)
    {
        ////m_ProgressAlert.setString("Sending " + sFilename + ": " + sCurrentAction);
        ////pause();
    }

    private void pause()
    {
        try
        {
            Thread.sleep(2000);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }
}

