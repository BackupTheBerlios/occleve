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
@version 0.9.5
*/

package org.occleve.mobileclient;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;

public class StaticHelpers
{
    public static String readUnicodeFile(String filename) throws Exception
    {
        StringBuffer buffer = null;
        InputStream is = null;
        InputStreamReader isr = null;

        if (filename.startsWith("file:"))
        {
        	// Read the file from the local filesystem.
            FileConnection fc = (FileConnection)Connector.open(filename);
            if(!fc.exists())
            {
            	throw new IOException("File does not exist");
            }
            is = fc.openInputStream();        	
        }
        else
        {
	        // Reading the file from the OccleveMobileClient jar,
	        // therefore call getResourceAsStream() on the midlet class
	        // in order to ensure that the correct JAR is read from.
	        Class c = OccleveMobileMidlet.getInstance().getClass();
	        is = c.getResourceAsStream(filename);
	        if (is == null)
	        {
	            System.out.println("Length of filename = " + filename.length());
	            char lastChar = filename.charAt( filename.length()-1 );
	            System.out.println("Last char in filename = " + ((long)lastChar));
	            throw new Exception("File " + filename + " does not exist");
	        }
        }
        
        // Specifying UTF8 encoding here makes the Sony-E K300 throw
        // an UnsupportedEncodingException... and it's not necessary anyway.
        isr = new InputStreamReader(is,"UTF-8");

        buffer = new StringBuffer();
        int ch;
        while ((ch = isr.read()) > -1)
        {
            buffer.append((char) ch);
        }

        if (isr != null) isr.close();

        return buffer.toString();
    }

    public static Vector stringToVector(String sParseThis)
    {
        return stringToVector(sParseThis,false);
    }

    public static Vector stringToVector(String sParseThis,boolean bRemoveBlankLines)
    {
        // Cope with both CRLF and LF files (but the file can't contain a mixture).

        String FLEXI_NEWLINE;
        if (sParseThis.indexOf("\r\n")!=-1)
            FLEXI_NEWLINE = "\r\n";
        else
            FLEXI_NEWLINE = "\n";

        final int newlineLength = FLEXI_NEWLINE.length();

        int startIndex = 0;

        int endIndex = 0;
        // int endIndexLF = 0;

        Vector v = new Vector();

        while (endIndex != -1)
        {
            endIndex = sParseThis.indexOf(FLEXI_NEWLINE,startIndex);

            // endIndexLF = sParseThis.indexOf(Constants.LINEFEED,startIndex);
            // if ((endIndex!=-1) && (endIndexLF!=-1))
            // {
            //    if (endIndexLF
            // }

            String sLine;
            if (endIndex==-1)
                sLine = sParseThis.substring(startIndex);
            else
                sLine = sParseThis.substring(startIndex,endIndex);

            boolean bKeepBlankLines = (!bRemoveBlankLines);
            boolean bAddElement = ((sLine.length()!=0) || bKeepBlankLines);

            if (bAddElement)
            {
                if (sLine.endsWith(FLEXI_NEWLINE))
                    sLine = sLine.substring(0,sLine.length() - newlineLength);

                v.addElement(sLine);
            }

            // System.out.println("Line " + v.size() + " = " + sLine);

            startIndex = endIndex + newlineLength;
        }

        return v;
    }

    public static Vector tokenizeString(String s,String sDelim)
    {
        Vector v = new Vector();
        int iIndex;
        do
        {
            iIndex = s.indexOf(sDelim);
            if (iIndex!=-1)
            {
                String sToken = s.substring(0,iIndex);
                v.addElement(sToken);

                s = s.substring(iIndex + sDelim.length());
            }
        } while (iIndex!=-1);

        // Final token is the remaining portion of the string.
        v.addElement(s);

        return v;
    }

    public static String getDisplayableTime()
    {
        Calendar now = Calendar.getInstance();

        String sMinutes = Integer.toString( now.get(Calendar.MINUTE) );
        if (sMinutes.length()==1) sMinutes = "0" + sMinutes;

        String sTime = new String();
        sTime += now.get(Calendar.HOUR_OF_DAY) + ":" + sMinutes;
        return sTime;
    }

    public static boolean isPunctuation(char c)
    {
        boolean bIsLowercaseLetter = (c>='a') && (c<='z');
        boolean bIsUppercaseLetter = (c>='A') && (c<='Z');

        return (   (bIsLowercaseLetter==false)
                   && (bIsUppercaseLetter==false)
                   && (Character.isDigit(c)==false)   );
    }

    public static String stripBeginning(String s,String sBeginning)
    {
        if (s.startsWith(sBeginning))
        {
            int iChars = sBeginning.length();
            return s.substring(iChars);
        }
        else
        {
            return s;
        }
    }

    public static String stripEnding(String s,String sEnding)
    {
        if (s.endsWith(sEnding))
        {
            int iChars = s.length() - sEnding.length();
            return s.substring(0, iChars);
        }
        else
        {
            return s;
        }
    }

    /**Reads a string from the InputStreamReader.
    Either stops at the first newline, or at the end of the data, depending
    on the flag supplied.*/
    public static String readFromISR(InputStreamReader isr,boolean bStopAtNewline)
    throws Exception
    {
        int FIRST_NEWLINE_CHAR;
        if (bStopAtNewline)
           FIRST_NEWLINE_CHAR = Constants.NEWLINE.charAt(0);
        else
           FIRST_NEWLINE_CHAR = -1;

        StringBuffer buffer = new StringBuffer();
        int ch;
        do
        {
            ch = isr.read();
            if ((ch!=-1) && (ch!=FIRST_NEWLINE_CHAR)) buffer.append((char) ch);
        } while ((ch!=-1) && (ch!=FIRST_NEWLINE_CHAR));

        return buffer.toString();
    }

    /**The microemulator applet throws a
    "java.lang.RuntimeException: Not implemented"
    when StringItem.setFont is called. So this function is
    designed to silently fail if that happens.*/
    public static void safeSetFont(StringItem si,Font font)
    {
        try
        {
            si.setFont(font);
        }
        catch (Exception e)
        {
            System.err.println("Call to StringItem.setFont failed");
        }
    }

    /**Another function designed to benignly fail in the microemulator applet.*/
    public static void safeAddGaugeToAlert(Alert alert)
    {
        try
        {
            Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE,Gauge.CONTINUOUS_RUNNING);
            alert.setIndicator(gauge);
        }
        catch (Exception e)
        {
            System.err.println("Failed to add Gauge to Alert");
        }
    }

    /**Another function designed to benignly fail in the microemulator applet.*/
    public static void safeSetCurrentItem(Item item)
    {
        try
        {
            OccleveMobileMidlet mid = OccleveMobileMidlet.getInstance();
            Display.getDisplay(mid).setCurrentItem(item);
        }
        catch (Exception e)
        {
            System.err.println("Call to setCurrentItem() failed");
        }
    }
        
    // 0.9.5: Output char as EUC-CN
    public static String unicodeCharToEucCnHexString(char unicodeChar)
    throws IOException
    {    
	    System.out.println("Unicode char = " + unicodeChar);

	    // Works fine on the emulator - but EUC_CN is not a supported encoding on
	    // most actual phones.
	    //ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    //java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(baos,"EUC_CN");
	    //String sUnicode = new Character(unicodeChar).toString();
	    //osw.write(sUnicode);
	    //osw.flush();
	    //byte[] eucCnByteArray = baos.toByteArray();
	    
	    byte[] eucCnByteArray = new EucCnToUnicodeMap().unicodeCharToEucCnBytePair(unicodeChar);
	    
	    String sHexString = "";	    
	    for (int i=0; i<eucCnByteArray.length; i++)
	    {
	  	  byte eucCnByte = eucCnByteArray[i];
	  	  
	  	  // Integer.toHexString will produce an eight char string
	  	  // with the eucCn value in the last two chars.
	  	  String sEuCnByteHex = Integer.toHexString(eucCnByte).substring(6);	  	  
	  	  sHexString += sEuCnByteHex;
	    }
	    
	    System.out.println("Unicode char in EUC-CN encoding as hex = "+ sHexString);
	    return sHexString;
    }

}

