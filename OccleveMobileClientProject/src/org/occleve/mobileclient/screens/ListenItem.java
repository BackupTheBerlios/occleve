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

package org.occleve.mobileclient.screens;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.serverbrowser.*;

public class ListenItem extends StringItem implements Runnable
{
    protected Alert m_ProgressAlert;

    /**Should end in ".mp3"*/
    private String m_sAudioClipFilename;

    // public String getFilename() {return m_sFilename;}

    private byte[] m_ClipData;

    public ListenItem(String sAudioClipFilename)
    {
        super(null,"Listen",Item.BUTTON);
        m_sAudioClipFilename = sAudioClipFilename;
        Command cmd = new Command("Play",Command.ITEM,0);
        addCommand(cmd);
    }

    public void play() throws Exception
    {
        System.out.println("Entering play() on ListenItem");
        System.out.println("With audioclipfilename = " + m_sAudioClipFilename);

        VocabRecordStoreManager mgr = new VocabRecordStoreManager();
        Integer rsid = mgr.findRecordByFilename(m_sAudioClipFilename);
        System.out.println("rsid = " + rsid);

        if (rsid!=null)
        {
            // Play from recordstore.
        }
        else
        {
            // Load from wiki.
            loadAudioClipFromWiki();
        }
    }

    private void loadAudioClipFromWiki() throws Exception
    {
        new Thread(this).start();
    }

    public void run()
    {
        System.out.println("Entering ListenItem.run");

        // Instantiate the WikiConnection here so the exception
        // handler can call close() on it if need be.
        WikiConnection wc = new WikiConnection();
        System.out.println("Obtained wikiconnection ok");

        try
        {
            loadAudioClip(wc);

            ByteArrayInputStream bais = new ByteArrayInputStream(m_ClipData);
            Player player = Manager.createPlayer(bais,"audio/mpeg");
            player.start();
        }
        catch (Exception e)
        {
            try
            {
                wc.close();
            }
            catch (Exception e2) {System.err.println(e2);}

            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    protected void loadAudioClip(WikiConnection wc) throws Exception
    {
        m_ProgressAlert = new Alert(null, "Loading audio clip from wiki...",
                                    null, null);
        m_ProgressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(m_ProgressAlert);
        OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

        String sWithUnderscores = m_sAudioClipFilename.replace(' ','_');
        String sDescriptorURL = Config.AUDIO_CLIP_URL_STUB + sWithUnderscores +
                      Config.AUDIO_CLIP_URL_SUFFIX;
        System.out.println("Audio clip descriptor URL = " + sDescriptorURL);

        InputStreamReader reader = wc.openISR(sDescriptorURL,m_ProgressAlert);
        String sTrueURL = null;
        do
        {
            String sLine = StaticHelpers.readFromISR(reader,true);
            System.out.println("Parsing " + sLine);

            int iIndex = sLine.indexOf("URL=");
            if (iIndex!=-1)
            {
                sTrueURL = sLine.substring(iIndex + "URL=".length());
                System.out.println("sTrueURL = " + sTrueURL);
            }

        } while ((reader.ready()) && (sTrueURL==null));

        if (sTrueURL==null)
        {
            throw new Exception("Couldn't find true URL of audio clip");
        }

        reader.close();

        ////////////////////////////////////////////////////////////////////
        // Now load the actual MP3 file

        System.out.println("Loading MP3 file...");
        DataInputStream dis = wc.openDIS(sTrueURL,m_ProgressAlert);
        System.out.println("Opened DataInputStream ok");

        int iBufferSize = wc.getPageLength();
        System.out.println("iBufferSize = " + iBufferSize);
        m_ClipData = new byte[iBufferSize + 1000];

        int iBytesRead;
        int iOffset = 0;

        // Terminate this loop on iBytesRead==0 as well as ==-1, since
        // in practice that seems to happen when reading an MP3 from the wiki.
        do
        {
            iBytesRead = dis.read(m_ClipData,iOffset,iBufferSize-iOffset);
            m_ProgressAlert.setString("Loaded " + iBytesRead + " of " +
                                      iBufferSize + " bytes");
            iOffset += iBytesRead;
        } while ((iBytesRead!=-1) && (iBytesRead!=0));

        dis.close();

        System.out.println("Number of MP3 bytes read = " + iOffset);

        wc.close();
        m_ProgressAlert.setString("Loaded MP3 ok");
    }

}

