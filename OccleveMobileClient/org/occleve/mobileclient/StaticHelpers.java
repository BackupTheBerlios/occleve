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

package org.occleve.mobileclient;

import java.io.*;
import java.util.*;

public class StaticHelpers
{
    public static String readUnicodeFile(String filename) throws Exception
    {
        StringBuffer buffer = null;
        InputStream is = null;
        InputStreamReader isr = null;

        String dummy = "";
        Class c = dummy.getClass();
        is = c.getResourceAsStream(filename);
        if (is == null)
        {
            System.out.println("Length of filename = " + filename.length());
            char lastChar = filename.charAt( filename.length()-1 );
            System.out.println("Last char in filename = " + ((long)lastChar));
            throw new Exception("File " + filename + " does not exist");
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

    public static String stripEnding(String sFilename,String sEnding)
    {
        if (sFilename.endsWith(sEnding))
        {
            int iChars = sFilename.length() - sEnding.length();
            return sFilename.substring(0, iChars);
        }
        else
        {
            return sFilename;
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
}

