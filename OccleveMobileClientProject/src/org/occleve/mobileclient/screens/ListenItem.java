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
import javax.microedition.media.control.*;

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
            ServerBrowser browser = new ServerBrowser();
            m_ClipData =
               browser.loadAudioClipFromWiki(m_sAudioClipFilename,m_ProgressAlert);

            // Save the clip data into the recordstore for future use
            mgr.createFileInRecordStore(m_sAudioClipFilename,m_ClipData,false);
        }

        // Get a player for the clip.
        ByteArrayInputStream bais = new ByteArrayInputStream(m_ClipData);
        Player player = null;

        try
        {
            player = Manager.createPlayer(bais, "audio/mpeg");
        }
        catch (MediaException me)
        {
            String sMsg = "Sorry! It looks like your phone can't play MP3s";
            OccleveMobileMidlet.getInstance().onError(sMsg);
            return;
        }

        // Crank up the volume. To avoid an exception, it's necessary to realize
        // the player first.
        player.realize();
        VolumeControl vc =
            (VolumeControl)player.getControl("javax.microedition.media.control.VolumeControl");
        vc.setLevel(100);

        // Play the clip.
        player.start();

        OccleveMobileMidlet.getInstance().setCurrentForm(previousDisplayable);
    }
}

