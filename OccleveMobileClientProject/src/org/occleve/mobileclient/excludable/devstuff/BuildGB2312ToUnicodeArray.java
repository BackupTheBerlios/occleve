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
@version 0.9.6
*/

package org.occleve.mobileclient.excludable.devstuff;

import java.io.*;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import org.occleve.mobileclient.*;

/**Used to build the array which is used by GB2312ToUnicodeMap.java.*/
public class BuildGB2312ToUnicodeArray
{
	public static void buildArray() throws Exception
	{
		System.out.println("Entering BuildEucCnToUnicodeArray.buildArray()...");

		// According to Wikipedia, "The rows 10-15 and 88-94 are unassigned"
		final int MAX_ROW =87;
		final int MAX_COL = 94;

		ByteArrayOutputStream baosLongArray = new ByteArrayOutputStream();
		DataOutputStream dosLongArray = new DataOutputStream(baosLongArray);
		
		StringBuffer sb = new StringBuffer();
		sb.append("byte[][] eucCnToUnicodeMap = {" + Constants.NEWLINE);
		
		for (int iGB2312Row = 1; iGB2312Row<=MAX_ROW; iGB2312Row++)
		{
			StringBuffer sbRow = new StringBuffer("\t{");
			
			for (int iGB2312Col = 1; iGB2312Col<=MAX_COL; iGB2312Col++)
			{
				long lCharUnicodeValue;
				
				if (iGB2312Row>=10 && iGB2312Row<=15)
				{
				    lCharUnicodeValue = 0;
				}
				else
				{
					// This uses the prescription giving for encoding GB2312 using
					// EUC-CN given at http://en.wikipedia.org/wiki/GB2312#EUC-CN
					
					int iHighByte = iGB2312Row + 160;
					int iLowByte = iGB2312Col + 160;	
					byte[] convertMe = {(byte)iHighByte,(byte)iLowByte};
					
					ByteArrayInputStream bais = new ByteArrayInputStream(convertMe);
				    InputStreamReader isr = new InputStreamReader(bais,"EUC_CN");
	
				    char cCharUnicodeValue = (char)isr.read();
				    lCharUnicodeValue = (long)cCharUnicodeValue;
				}

				dosLongArray.writeLong(lCharUnicodeValue);
				
				sbRow.append(lCharUnicodeValue);
				if (iGB2312Col<MAX_COL) sbRow.append(",");
			}
			
			sbRow.append("}");
			if (iGB2312Row<MAX_ROW) sbRow.append(",");
			sbRow.append(Constants.NEWLINE);

			sb.append(sbRow);
		}

		sb.append( "}" );
		System.out.println(sb.toString());

        String sFilenameOnFS = "file:///root1/" + Config.GB2312_TO_UNICODE_ARRAY_FILE;
        FileConnection filecon =
            (FileConnection)Connector.open(sFilenameOnFS);
        filecon.create();
        OutputStream os = filecon.openOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        dos.write( baosLongArray.toByteArray() );
		dos.flush();
        dos.close();
        os.close();
        filecon.close();
        
        dosLongArray.close();
        baosLongArray.close();
	}
}

