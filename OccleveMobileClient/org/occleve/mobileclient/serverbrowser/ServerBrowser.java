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

package org.occleve.mobileclient.serverbrowser;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;

/**Connects to the wiki, downloads the list of available tests, and
displays them.*/
public class ServerBrowser implements Runnable
{
    ////protected String m_sServerURL;
    protected Alert m_ProgressAlert;

    public ServerBrowser() ////// ,String sServerURL)
    {
        /////m_sServerURL = sServerURL;

        try
        {
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
            fetchListOfTests();
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void fetchListOfTests() throws Exception
    {
        // Open and close a dummy connection, to force the display of the
        // permissions dialog for internet access.
        // Otherwise, on some phones it will foul up the progress alert.
        /*
        HttpConnection hc = (HttpConnection) Connector.open(m_sServerURL);
        hc.close();
        System.out.println("Successfully opened and closed dummy HttpConnection");
        */

        m_ProgressAlert = new Alert(null, "Reading list of tests from server...",
                                    null, null);
        m_ProgressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(m_ProgressAlert);
        OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

        HttpConnection hc = (HttpConnection)Connector.open(Config.LIST_OF_TESTS_URL);
        trace("Called Connection.open with " + Config.LIST_OF_TESTS_URL);

        // Getting the response code will open the connection,
        // send the request, and read the HTTP response headers.
        // The headers are stored until requested.
        int rc = hc.getResponseCode();
        if (rc != HttpConnection.HTTP_OK)
        {
            throw new IOException("HTTP response code indicates failure: " + rc);
        }

        ////hc.setRequestMethod(HttpConnection.POST);
        ////trace("Set request method to POST");

        InputStream istream = hc.openInputStream();
        trace("Opened InputStream ok");
        InputStreamReader reader = new InputStreamReader(istream,"UTF-8");

        // Get the ContentType
        String type = hc.getType();

        int length = (int)hc.getLength();
        System.out.println("Length = " + length);

        System.out.println("reader.ready() = " + reader.ready());

///        int firstChar = istream.read();
///        System.out.println("istream.read() = " + firstChar);

        do
        {
            String sLine = StaticHelpers.readFromISR(reader,true);
            System.out.println(sLine);
        } while (reader.ready());

        /*
        String sMsg = "Sent " + list.getSize() + " over the Internet";
        Alert alert = new Alert(null, sMsg, null, null);
       OccleveMobileMidlet.getInstance().displayAlertThenFileChooser(alert);
        */

       reader.close();
       istream.close();
       hc.close();
       trace("Closed everything ok");

       OccleveMobileMidlet.getInstance().displayFileChooser();
    }

    private void trace(String s)
    {
        m_ProgressAlert.setString(s);
        pause();
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

