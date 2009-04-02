/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2009  Joe Gittings

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

import java.io.*;

import javax.microedition.lcdui.*;
import org.occleve.mobileclient.*;

public class EncodingTester extends Form implements CommandListener
{
    protected DevStuffScreen m_DevStuffScreen;
    protected Command m_BackCommand;
    protected Command m_EncodeCommand;

    protected TextField m_InputTextField;
    protected TextField m_ResultsTextField;
    
    public EncodingTester(DevStuffScreen dvs) throws Exception
    {
        super(null);

        m_DevStuffScreen = dvs;
        
        m_BackCommand = new Command("Back",Command.BACK,0);
        addCommand(m_BackCommand);

        m_EncodeCommand = new Command("Encode",Command.ITEM,0);
        addCommand(m_EncodeCommand);

        setCommandListener(this);

        m_InputTextField = new TextField("String to encode: ","",30,TextField.ANY);
        append(m_InputTextField);

        m_ResultsTextField = new TextField("Results: ","",500,TextField.ANY);
        append(m_ResultsTextField);
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable s)
    {
        if (c==m_BackCommand)
        {
            OccleveMobileMidlet.getInstance().setCurrentForm(m_DevStuffScreen);
        }
        else if (c==m_EncodeCommand)
        {
        	try
        	{
        		doEncoding(m_InputTextField.getString());
        	}
        	catch (Throwable t)
        	{
        		OccleveMobileMidlet.getInstance().onError(t);
        	}
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command type");
        }
    }
    
    private void doEncoding(String sEncodeMe) throws Throwable
    {
    	StringBuffer sbResults = new StringBuffer();

        encode(sEncodeMe,sbResults,"UTF-8","standard UTF-8 ('UTF-8')");

    	sbResults.append(".............. \n");
        encodeIntoJavaUTF8(sEncodeMe,sbResults);

    	sbResults.append(".............. \n");
        String sDefaultEncoding = System.getProperty("microedition.encoding");
        String sEncodingDesc = "platform default encoding (" + sDefaultEncoding + ")"; 
        encode(sEncodeMe,sbResults,sDefaultEncoding,sEncodingDesc);
        
    	m_ResultsTextField.setString(sbResults.toString());
    }

    private void encode(String sEncodeMe,StringBuffer sbResults,
    		String sEncoding,String sEncodingDescription)
    throws Exception
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos,sEncoding);
        writer.write(sEncodeMe);

        byte[] bytes = baos.toByteArray();
        String encodedString = new String(bytes);
    	
    	sbResults.append("Encoded in " + sEncodingDescription + ": \n");
    	sbResults.append("String: " + encodedString + "\n");
    	sbResults.append("Bytes: " + getPrintableListOfBytes(bytes) + "\n");    	
    }

    private void encodeIntoJavaUTF8(String sEncodeMe,StringBuffer sbResults)
    throws Exception
    {
    	ByteArrayOutputStream baosJavaUTF = new ByteArrayOutputStream();
        DataOutputStream dosJavaUTF = new DataOutputStream(baosJavaUTF);
        dosJavaUTF.writeUTF(sEncodeMe);
    	
        byte[] javaUTFBytes = baosJavaUTF.toByteArray();
        String javaUTFString = new String(javaUTFBytes);

    	sbResults.append("Encoded in Java-format UTF-8: \n");
    	sbResults.append("String: " + javaUTFString + "\n");
    	sbResults.append("Bytes: " + getPrintableListOfBytes(javaUTFBytes) + "\n");
    }

    private String getPrintableListOfBytes(byte[] bytes)
    {
    	StringBuffer sbList = new StringBuffer();
    	for (int i=0; i<bytes.length; i++)
    	{
    		if (i>0) sbList.append(",");
    		Byte b = new Byte(bytes[i]);    		
    		sbList.append(b.toString());
    	}
    	return sbList.toString();
    }
}

