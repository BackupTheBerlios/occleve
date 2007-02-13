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

package org.occleve.mobileclient.excludable.translation;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;

public abstract class TranslationEngine implements Runnable
{
    protected String m_sRawPostDataForTranslationThread;

    public TranslationEngine()
    {
    }

    protected abstract String getTranslationWebsitePostURL();

    /**Derived class should implement this to provide the appropriate
    HTTP POST param string (not encoded) for the translation website
    it encapsulates.*/
    protected abstract String getEnglishToChineseRawPostData(String sTranslateMe);

    protected abstract String extractTranslation(StringBuffer sbReturnedPage);

    public void translateEnglishToChinese(String sTranslateMe)
    {
        try
        {
            m_sRawPostDataForTranslationThread =
                getEnglishToChineseRawPostData(sTranslateMe);

            Thread toPreventBlocking = new Thread(this);
            toPreventBlocking.start();

            Alert alert = new Alert(null, "Sending data to translation website", null, null);
            OccleveMobileMidlet.getInstance().displayAlertThenFileChooser(alert);
        }
        catch (Exception e)
        {
            try
            {
            }
            catch (Exception e2) {OccleveMobileMidlet.getInstance().onError(e2); return;}

            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    public void run()
    {
        try
        {
            translate();
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void translate()
    throws Exception
    {
        String sURL = getTranslationWebsitePostURL();
        System.out.println("sURL = " + sURL);

        HttpConnection hc = (HttpConnection)Connector.open(sURL);
        System.out.println("Successfully opened HttpConnection");

        // writeDirectly(hc);
        writeUsingSetRequestProperty(hc);

        int rc = hc.getResponseCode();
        System.out.println("Response code = " + rc);

        // Retrieve the response back from the server

        StringBuffer messagebuffer = new StringBuffer();
        DataInputStream dis = new DataInputStream(hc.openInputStream());
        int ch;

        // Check the Content-Length first
        long len = hc.getLength();

        if (len != -1)
        {
            for (int i = 0; i < len; i++)
            {
                if ((ch = dis.read()) != -1) messagebuffer.append((char) ch);
            }
        }
        else
        {
            // if the content-length is not available
            while ((ch = dis.read()) != -1) messagebuffer.append((char) ch);
        }

        dis.close();
        hc.close();

        String sTranslation = extractTranslation(messagebuffer);
        Alert alert = new Alert(null, "Translation = " + sTranslation, null, null);
        OccleveMobileMidlet.getInstance().displayAlertThenFileChooser(alert);
    }

    /**Uses the results from web-sniffer.net, eg.
    http://web-sniffer.net/?url=http%3A%2F%2Fbabelfish.altavista.com%2Ftr%3Ftrtext%3Dhello%26lp%3Den_zh&submit=Submit&http=1.0h&type=POST&ua=
    */
    protected void writeDirectly(HttpConnection hc) throws Exception
    {
        String sEncodedPostData = URLEncoder(m_sRawPostDataForTranslationThread);
        System.out.println("Unencoded data is:" + m_sRawPostDataForTranslationThread);
        System.out.println("Encoded data is:" + sEncodedPostData);

        byte[] bytes = sEncodedPostData.getBytes();
        String sByteArrayLength = Integer.toString(bytes.length);
        System.out.println("byte array length = " + sByteArrayLength);

        OutputStream ostream = hc.openOutputStream();
        System.out.println("Opened OutputStream ok");

        OutputStreamWriter writer = new OutputStreamWriter(ostream); // ,"UTF-8");

        writer.write("POST /tr HTTP/1.1" + Constants.CRLF);
        writer.write("Host: babelfish.altavista.com" + Constants.CRLF);
        writer.write("Connection: close" + Constants.CRLF);
        writer.write("Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5" + Constants.CRLF);
        writer.write("Accept-Language: en-gb,en;q=0.5" + Constants.CRLF);
        writer.write("Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7" + Constants.CRLF);
        writer.write("User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.0.7) Gecko/20060909 Firefox/1.5.0.7" + Constants.CRLF);
        writer.write("Referer: http://babelfish.altavista.com/" + Constants.CRLF);
        writer.write("Content-type: application/x-www-form-urlencoded" + Constants.CRLF);
        // writer.write("Content-length: " + sByteArrayLength + Constants.CRLF);
        writer.write("Content-length: 20" + Constants.CRLF);
        writer.write(Constants.CRLF);

        writer.write("trtext=hello&lp=en_zh");
        //writer.write(sEncodedPostData);

        writer.close();
        ostream.close();
    }

    protected void writeUsingSetRequestProperty(HttpConnection hc) throws Exception
    {
        hc.setRequestMethod(HttpConnection.POST);
        System.out.println("Set request method to POST");

        String type = "application/x-www-form-urlencoded";
        hc.setRequestProperty( "Content-Type",type);
        System.out.println("Set content type OK");

        //String agent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.0.7) Gecko/20060909 Firefox/1.5.0.7";
        //hc.setRequestProperty( "User-Agent", agent );

        // Set a bunch of properties identified using web-sniffer.net
        //hc.setRequestProperty( "Accept","text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5" );
        //hc.setRequestProperty( "Accept-Language","en-gb,en;q=0.5");
        //hc.setRequestProperty( "Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7");

        String sEncodedPostData = URLEncoder(m_sRawPostDataForTranslationThread);
        System.out.println("Unencoded data is:" + m_sRawPostDataForTranslationThread);
        System.out.println("Encoded data is:" + sEncodedPostData);

        byte[] bytes = sEncodedPostData.getBytes();
        String sByteArrayLength = Integer.toString(bytes.length);
        System.out.println("byte array length = " + sByteArrayLength);

        String sLength = Integer.toString(sEncodedPostData.length());
        System.out.println("String length = " + sLength);

        hc.setRequestProperty("Content-length",sByteArrayLength);
        hc.setRequestProperty("Content-Length",sByteArrayLength);
        System.out.println("Set content length to " + sByteArrayLength);

        OutputStream ostream = hc.openOutputStream();
        System.out.println("Opened OutputStream ok");
        OutputStreamWriter writer = new OutputStreamWriter(ostream,"UTF-8");

        writer.write( sEncodedPostData );

        // Calling flush() forces the data to be sent via chunked encoding -
        // which Babelfish won't accept.
        ///////ostream.flush();

        writer.close();
        ostream.close();
    }

    // From http://forum.java.sun.com/thread.jspa?threadID=330405&tstart=270
    public static String URLEncoder(String str)
    {
        if (str == null)return null;

        StringBuffer resultStr = new StringBuffer(str.length());
        char tmpChar;

        for (int ix = 0; ix < str.length(); ix++)
        {
            tmpChar = str.charAt(ix);
            switch (tmpChar)
            {
            case ' ':
                resultStr.append("%20");
                break;
            case '-':
                resultStr.append("%2D");
                break;
            case '/':
                resultStr.append("%2F");
                break;
            case ':':
                resultStr.append("%3A");
                break;
            /*
            //////// Babelfish appears to object to this being URL encoded //////
            case '=':
                resultStr.append("%3D");
                // System.out.println( "tmpChar = '=' " + "add %3D " );
                break;
            */
            case '?':
                resultStr.append("%3F");
                // System.out.println( "tmpChar = '?' " + "add %3F " );
                break;
            case '#':
                resultStr.append("%23");
                break;
            case '\r':
                resultStr.append("%0D");
                break;
            case '\n':
                resultStr.append("%0A");
                break;
            default:
                resultStr.append(tmpChar);
                break;
            }
        }

        return resultStr.toString();
    }

}

