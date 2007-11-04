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

    /**The total amount of data in the page (or MP3 file, etc) in bytes.
    Useful for checking we've read the whole thing.*/
    private int m_iPageLengthInBytes;
    public int getPageLength() {return m_iPageLengthInBytes;}

    /**Connection problems with new combinations of phones and ISPs
    is a certainty not a possibility... This provides
    more detail on the context of IOExceptions.*/
    private String m_sConnectionAction;
    public void setConnectionAction(String s) {m_sConnectionAction = s;}
    public String getConnectionAction() {return m_sConnectionAction;}
    
    public InputStreamReader openISR(String sURL,Alert progressAlert)
    throws Exception
    {

    	//outputRawConnectionDataViaException(sURL);
    	
    	setConnectionAction("Calling WikiConnection.openInputStream");
        openInputStream(sURL,progressAlert);

    	setConnectionAction("Creating new InputStreamReader");
        m_InputStreamReader =
            new InputStreamReader(m_InputStream,Config.ENCODING);

    	setConnectionAction("Checking InputStreamReader.ready()");
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
    	setConnectionAction("Calling Connector.open()");
    	System.out.println(sURL);
    	m_HttpConnection =
    		(HttpConnection)Connector.open(sURL,Connector.READ);

        if (progressAlert!=null)
        {
            progressAlert.setString("Connecting to " + sURL);
        }

    	//setConnectionAction("Pausing for a few secs after Connector.open()");
        //Thread.sleep(7000);
        
        // Getting the response code will open the connection,
        // send the request, and read the HTTP response headers.
        // The headers are stored until requested.
    	setConnectionAction("Calling HttpConnection.getResponseCode");
        int rc = m_HttpConnection.getResponseCode();
        if (rc != HttpConnection.HTTP_OK)
        {
        	setConnectionAction("Response code not HTTP_OK... throwing IOException");
            throw new IOException("HTTP response code indicates failure: " + rc);
        }

    	setConnectionAction("Calling HttpConnection.openInputStream()");
        m_InputStream = m_HttpConnection.openInputStream();

        if (progressAlert!=null)
        {
            progressAlert.setString("Successfully connected to wiki");
        }

        // Get the ContentType
    	setConnectionAction("Calling HttpConnection.getType()");
        String type = m_HttpConnection.getType();

    	setConnectionAction("Calling HttpConnection.getLength()");
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
    
    private void outputRawConnectionDataViaException(String url)
    throws Exception
    {
        StreamConnection c = null;
        InputStream s = null;

    	setConnectionAction("Calling Connector.open()");
        c = (StreamConnection)Connector.open(url);

    	setConnectionAction("Calling Connector.openInputStream()");
        s = c.openInputStream();
        
        int ch;
        StringBuffer rawChars = new StringBuffer();

    	setConnectionAction("Calling InputStream.read()");
        while ((ch = s.read()) != -1)
        {
        	char castedChar = (char)ch;
        	rawChars.append(castedChar);
        	setConnectionAction("Appended one char: string so far" + rawChars.toString());
        }
        
        throw new Exception("Raw data from connection: " + rawChars.toString());
    }

    /**New in 0.9.4: reads from the specified URL, and blocks until the
    number of bytes specified by the HTTP content-length
    header field have been read.*/
    public byte[] readAllBytes(String sURL,Alert progressAlert)
    throws Exception
    {
        DataInputStream dis = openDIS(sURL,progressAlert);
        System.out.println("Opened DataInputStream ok");

        int iPageLength = getPageLength();
        System.out.println("iPageLength = " + iPageLength);
        
        // TODO - more elegant approach and remove 100k limit for pages of unknown length.
        int iBufferSize;
        if (iPageLength==-1)
        	iBufferSize = 100000;
        else
        	iBufferSize = iPageLength;
        
        byte[] theData = new byte[iBufferSize];

        int iBytesRead;
        int iOffset = 0;

        boolean bContinue;
        do
        {
            iBytesRead = dis.read(theData,iOffset,iBufferSize-iOffset);
            iOffset += iBytesRead;

            String sMsg = "Loaded " + iOffset + " of ";
            if (iPageLength==-1)
            	sMsg += "an unknown number of bytes";
            else
            	sMsg += iBufferSize + " bytes";
            
            progressAlert.setString(sMsg);
            
            if (iBufferSize==-1)
            	bContinue = (iBytesRead!=-1);
            else
            	bContinue = (iOffset < iPageLength);
        } while (bContinue);

        System.out.println("Closing DataInputStream");
        dis.close();

        System.out.println("Number of bytes read = " + iOffset);
        
        if (iPageLength==-1)
        {
        	// If the page length was unknown, copy the data into an array
        	// of the correct size.
        	byte[] finalData = new byte[iOffset];
        	System.arraycopy(theData, 0, finalData, 0, iOffset);
        	return finalData;
        }
        else
        {
        	return theData;
        }
    }
}

