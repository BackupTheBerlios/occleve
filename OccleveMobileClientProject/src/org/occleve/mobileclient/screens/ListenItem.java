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
        System.out.println("With audio clip filename = " + m_sAudioClipFilename);
        new Thread(this).start();
    }

    public void run()
    {
        System.out.println("Entering ListenItem.run");

        try
        {
            loadAndPlayAudioClip();
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    private void loadAndPlayAudioClip() throws Exception
    {
        // Display a progress bar during the whole process
        m_ProgressAlert = new Alert(null, "Loading audio clip...",
                                    null, null);
        m_ProgressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(m_ProgressAlert);
        Displayable previousDisplayable =
            OccleveMobileMidlet.getInstance().getCurrentDisplayable();
        OccleveMobileMidlet.getInstance().setCurrentForm(m_ProgressAlert);

        VocabRecordStoreManager mgr = new VocabRecordStoreManager();
        Integer rsid = mgr.findRecordByFilename(m_sAudioClipFilename);
        System.out.println("rsid = " + rsid);

        if (rsid!=null)
        {
            // Load from recordstore.
            m_ClipData = mgr.getRecordBytes(rsid.intValue());
        }
        else
        {
            // Load from wiki.
            loadAudioClipFromWiki();

            // Save the clip data into the recordstore for future use
            mgr.createFileInRecordStore(m_sAudioClipFilename,m_ClipData,false);
        }

        // Play the clip
        ByteArrayInputStream bais = new ByteArrayInputStream(m_ClipData);
        Player player = Manager.createPlayer(bais,"audio/mpeg");
        player.start();

        OccleveMobileMidlet.getInstance().setCurrentForm(previousDisplayable);
    }

    private void loadAudioClipFromWiki()
    {
        // Instantiate the WikiConnection here so the exception
        // handler can call close() on it if need be.
        WikiConnection wc = new WikiConnection();
        System.out.println("Obtained wikiconnection ok");

        try
        {
            loadAudioClipFromWiki_Inner(wc);
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

    private void loadAudioClipFromWiki_Inner(WikiConnection wc) throws Exception
    {
        String sWithUnderscores = m_sAudioClipFilename.replace(' ', '_');
        String sDescriptorURL = Config.AUDIO_CLIP_URL_STUB + sWithUnderscores +
                                Config.AUDIO_CLIP_URL_SUFFIX;
        System.out.println("Audio clip descriptor URL = " + sDescriptorURL);

        m_ProgressAlert.setString("Loading clip locator");

        int iTries = 0;
        InputStreamReader reader;
        String sTrueURL;
        do
        {
            reader = wc.openISR(sDescriptorURL, null);
            sTrueURL = null;
            do
            {
                String sLine = StaticHelpers.readFromISR(reader, true);
                System.out.println("Parsing " + sLine);

                int iIndex = sLine.indexOf("URL=");
                if (iIndex != -1)
                {
                    sTrueURL = sLine.substring(iIndex + "URL=".length());
                    System.out.println("sTrueURL = " + sTrueURL);
                }

            } while ((reader.ready()) && (sTrueURL == null));

            iTries++;
        } while ((sTrueURL == null) && (iTries<Config.CONNECTION_TRIES_LIMIT));

        reader.close();

        if (sTrueURL==null)
        {
            throw new Exception("Couldn't find true URL of audio clip");
        }

        ////////////////////////////////////////////////////////////////////
        // Now load the actual MP3 file

        m_ProgressAlert.setString("Loading the clip itself");
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
            iOffset += iBytesRead;
            m_ProgressAlert.setString("Loaded " + iOffset + " of " +
                                      iBufferSize + " bytes");
        } while ((iBytesRead!=-1) && (iBytesRead!=0));

        dis.close();

        System.out.println("Number of MP3 bytes read = " + iOffset);

        wc.close();
        m_ProgressAlert.setString("Loaded MP3 ok");
    }
}

