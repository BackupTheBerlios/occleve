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

package org.occleve.mobileclient.util;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.VocabRecordStoreManager;
import org.occleve.mobileclient.serverbrowser.WikiConnection;

/**0.9.7: Put FileConnection API specific stuff in here.*/
public class MediaHelpers
{		
	private static final int TRACE_PAUSE = 2000;
	
    public static byte[] loadImage(String sImageFilename,String sImageURL,
    	VocabRecordStoreManager rsMgr,Alert progressAlert) throws Exception
    {
    	byte[] imageData = loadImageFromRecordStore(sImageFilename,rsMgr);
    	if (imageData==null)
    	{
    		imageData = loadImageFromWeb(sImageFilename,sImageURL,null,rsMgr,
    				Config.CONNECTION_TRIES_LIMIT,progressAlert);
    	}
    	return imageData;
    }

	public static byte[] loadImageFromJar(String sImageFilename,
			Alert progressAlert) throws Exception
    {
        progressAlert.setString("Trying to load..." + sImageFilename + "...from jar");
        Thread.sleep(TRACE_PAUSE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos,"UTF-8");
        writer.write(sImageFilename);
        byte[] byteArray = baos.toByteArray();
        String sUTFEncodedFilename = new String(byteArray);
		
		// Reading the file from the OccleveMobileClient jar,
        // therefore call getResourceAsStream() on the midlet class
        // in order to ensure that the correct JAR is read from.
        Class c = OccleveMobileMidlet.getInstance().getClass();

        progressAlert.setString("UTF-encoded filename = " + sUTFEncodedFilename);
        Thread.sleep(TRACE_PAUSE);

        InputStream is = c.getResourceAsStream(sUTFEncodedFilename);
        progressAlert.setString("Got InputStream: is=" + is);
        Thread.sleep(TRACE_PAUSE);

        if (is==null)
        {
            progressAlert.setString("Length of filename = " + sImageFilename.length());
            Thread.sleep(TRACE_PAUSE);

            char lastChar = sImageFilename.charAt( sImageFilename.length()-1 );
            progressAlert.setString("Last char in filename = " + ((long)lastChar));
            Thread.sleep(TRACE_PAUSE);

            return null;
        }
        {
            DataInputStream dis = new DataInputStream(is);
            progressAlert.setString("Got DataInputStream: dis=" + is);
            Thread.sleep(TRACE_PAUSE);

            int iBufferSize = 100000;
            byte[] theData = new byte[iBufferSize];

            int b;
            int iBytesRead = 0;
            do
            {
            	b = dis.read();
            	if (b!=-1)
            	{
            		theData[iBytesRead] = (byte)b;
            		iBytesRead++;
            	}
            } while (b!=-1);
            	
            progressAlert.setString("iBytesRead = " + iBytesRead);
            Thread.sleep(TRACE_PAUSE);
            
        	// Copy the data into an array of the correct size.
        	byte[] finalData = new byte[iBytesRead];
        	System.arraycopy(theData, 0, finalData, 0, iBytesRead);
                   
        	progressAlert.setString("Closing streams");
            Thread.sleep(TRACE_PAUSE);

            dis.close();
            is.close();
            
            return finalData;
        }
    }

	public static byte[] loadImageFromRecordStore(String sImageFilename,
    		VocabRecordStoreManager rsMgr) throws Exception
    {
        Integer rsid = rsMgr.findRecordByFilename(sImageFilename);
        System.out.println("rsid = " + rsid);

        byte[] imageData = null;
        if (rsid!=null)
        {
            imageData = rsMgr.getRecordContentsMinusFilename(rsid.intValue());
        }

        return imageData;
    }
	
    public static byte[] loadImageFromWeb(String sImageFilename,String sImageURL,
    		String sWikiLocatorURL,
    		VocabRecordStoreManager rsMgr,int iRetries,
    		Alert progressAlert)
    throws Exception
    {    	
    	WikiConnection wc = new WikiConnection();

    	// 0.9.6 - use a retry system similar to that used for loading quizzes,
    	// in order to get past the China Telecom "welcome" page.
    	boolean bValidImage;
    	byte[] imageData = null;
    	int iTries = 0;
    	do
    	{    		
    		try
    		{
        		bValidImage = true;

        		if (sWikiLocatorURL!=null)
        		{
                	sImageURL = loadMediaFileLocatorFromWiki(sImageFilename,
                				sWikiLocatorURL,progressAlert,wc,1);
                	System.out.println(sImageURL);
        		}
        		
        		progressAlert.setString("Obtained image URL from locator: URL=" + sImageURL);
                Thread.sleep(TRACE_PAUSE);
        		
        		imageData = wc.readAllBytes(sImageURL,progressAlert,false);

        		progressAlert.setString("Read image data OK: length=" + imageData.length);
                Thread.sleep(TRACE_PAUSE);

        		progressAlert.setString("Trying to create Image object from data");
                Thread.sleep(TRACE_PAUSE);
    			Image image = Image.createImage(imageData, 0, imageData.length);
    			
        		progressAlert.setString("Created Image object from data ok");
                Thread.sleep(TRACE_PAUSE);    			
    		}
    		catch (Exception e)
    		{
        		progressAlert.setString("Failed to create Image object from data because: " + e);
                Thread.sleep(TRACE_PAUSE);

                System.err.println(e);
    			bValidImage = false;
        		iTries++;
    		}
    	} while ((bValidImage==false) && (iTries<iRetries));
    	
    	if (bValidImage)
    	{
    		// Save the image into the recordstore for future use
    		rsMgr.createFileInRecordStore(sImageFilename,imageData,false);
    	}
    	else
    	{
    		String sErr =
    			"Couldn't load a valid image, even after retrying. " +
    			"The image data loaded in the last attempt was: " +
    			new String(imageData);
    		progressAlert.setString(sErr);
            Thread.sleep(TRACE_PAUSE);
            imageData = null;
    	}
                
        return imageData;
    }

    public static String loadMediaFileLocatorFromWiki(String sMediaFilename,
    		String sDescriptorURL,Alert progressAlert,WikiConnection wc,
    		final int iMaxTries) throws Exception
    {
        int iTries = 0;
        String sTrueURL;
        do
        {
        	String sMsg = "Loading media locator";
        	if (iTries > 0) sMsg += " (try " + iTries + ")";
            progressAlert.setString(sMsg);

        	byte[] locatorData = wc.readAllBytes(sDescriptorURL,progressAlert,true);        	
        	System.out.println("About to instantiate String from bytes");

        	String sLocatorPage = new String(locatorData,Config.ENCODING);
        	System.out.println("Instantiated String from bytes ok");

            sTrueURL = null;
            Vector locatorLines = StaticHelpers.stringToVector(sLocatorPage);
            VectorReader vr = new VectorReader(locatorLines);
            do
            {
            	String sLine = vr.readLine();
                System.out.println("Parsing " + sLine);

                int iIndex = sLine.indexOf("URL=");
                if (iIndex != -1)
                {
                    sTrueURL = sLine.substring(iIndex + "URL=".length());
                    System.out.println("sTrueURL = " + sTrueURL);
                }

            } while ((vr.hasMoreLines()) && (sTrueURL == null));

            iTries++;
        } while ((sTrueURL == null) && (iTries<iMaxTries));

        if (sTrueURL==null)
        {
            throw new Exception("Couldn't find true URL of media file on wiki");
        }

        return sTrueURL;
    }
    
    public static void displayAnimation(byte[] animationData,String sTitle)
    throws Exception
    {
        // Get a player for the clip.
        ByteArrayInputStream bais = new ByteArrayInputStream(animationData);
        Player player = null;

        try
        {
            player = Manager.createPlayer(bais,"image/gif");
        }
        catch (MediaException me)
        {
            String sMsg = "Sorry! It looks like your phone can't display animated GIFs";
            System.out.println(sMsg);
            OccleveMobileMidlet.getInstance().onError(sMsg);
            return;
        }

        player.realize();

        VideoControl video = (VideoControl) player.getControl("VideoControl");
        Item videoItem = (Item)video.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, null);
        Form videoForm = new Form(sTitle);
        videoForm.append(videoItem);

        ////Displayable oldDisplayable =
        ////	OccleveMobileMidlet.getInstance().getCurrentDisplayable();
        
        OccleveMobileMidlet.getInstance().setCurrentForm(videoForm);

        player.start();

        try
        {
            // Wait until the animation has finished.
        	// DISABLED - ON SOME PHONES THE ANIMATION LOOPS AND THIS WILL NEVER FINISH
	        //do
	        //{
	        //    Thread.sleep(1000);
	        //} while (player.getState()==Player.STARTED);

            Thread.sleep(10000);
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        
        ///////////OccleveMobileMidlet.getInstance().setCurrentForm(oldDisplayable);

    	// Release player resources. Failing to do this results in noticeable
    	// resource drain on a Sony Ericsson Z558c.
    	player.stop();
    	player.deallocate();
    	player.close();    	
    }
}

