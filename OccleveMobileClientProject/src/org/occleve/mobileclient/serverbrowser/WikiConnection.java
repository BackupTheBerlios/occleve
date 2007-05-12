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

package org.occleve.mobileclient.serverbrowser;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;

/**Connects to the wiki, downloads the list of available tests, and
displays them.*/
public class WikiConnection
{
    private HttpConnection m_HttpConnection;
    private InputStream m_InputStream;

    private InputStreamReader m_InputStreamReader;
    private DataInputStream m_DataInputStream;

    private int m_iPageLengthInBytes;
    public int getPageLength() {return m_iPageLengthInBytes;}

    public InputStreamReader openISR(String sURL,Alert progressAlert)
    throws Exception
    {
        openInputStream(sURL,progressAlert);

        m_InputStreamReader =
            new InputStreamReader(m_InputStream,Config.ENCODING);
        System.out.println("reader.ready() = " + m_InputStreamReader.ready());
        return m_InputStreamReader;
    }

    public DataInputStream openDIS(String sURL,Alert progressAlert)
    throws Exception
    {
        openInputStream(sURL,progressAlert);

        m_DataInputStream = new DataInputStream(m_InputStream);
        return m_DataInputStream;
    }

    private void openInputStream(String sURL,Alert progressAlert)
    throws Exception
    {
        m_HttpConnection = (HttpConnection)Connector.open(sURL);

        if (progressAlert!=null)
        {
            progressAlert.setString("Connecting to " + sURL);
        }

        // Getting the response code will open the connection,
        // send the request, and read the HTTP response headers.
        // The headers are stored until requested.
        int rc = m_HttpConnection.getResponseCode();
        if (rc != HttpConnection.HTTP_OK)
        {
            throw new IOException("HTTP response code indicates failure: " + rc);
        }

        m_InputStream = m_HttpConnection.openInputStream();
        progressAlert.setString("Successfully connected to wiki");

        // Get the ContentType
        String type = m_HttpConnection.getType();

        m_iPageLengthInBytes = (int)m_HttpConnection.getLength();
        System.out.println("Length = " + m_iPageLengthInBytes);
    }

    public void close() throws Exception
    {
       if (m_InputStreamReader!=null) m_InputStreamReader.close();
       if (m_DataInputStream!=null) m_DataInputStream.close();

       m_InputStream.close();
       m_HttpConnection.close();
    }
}
