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

//import java.io.*;
//import java.util.*;
//import javax.microedition.io.*;
//import javax.microedition.io.file.*;

import java.io.DataInputStream;
import java.util.Vector;

import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.VocabRecordStoreManager;
import org.occleve.mobileclient.serverbrowser.WikiConnection;

/**0.9.7: Put FileConnection API specific stuff in here.*/
public class MediaHelpers
{		
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
    	// Display a progress bar during the whole process
        // Alert progressAlert = new Alert(null, "Loading image " + sImageFilename,null, null);
        // progressAlert.setTimeout(Alert.FOREVER);
        // StaticHelpers.safeAddGaugeToAlert(progressAlert);
        // Displayable previousDisplayable =
        //     OccleveMobileMidlet.getInstance().getCurrentDisplayable();
        // OccleveMobileMidlet.getInstance().setCurrentForm(progressAlert);

        // Integer rsid = rsMgr.findRecordByFilename(sImageFilename);
        // System.out.println("rsid = " + rsid);
        // byte[] imageData;
        // if (rsid!=null)
        //{
            // Load from recordstore.
        //	System.out.println("Animation already in recordstore... loading");
        //    imageData = rsMgr.getRecordContentsMinusFilename(rsid.intValue());
        // }
        // else
        // {
    	
        // Load from website.
    	System.out.println("Animation not in recordstore... loading from web");
    	WikiConnection wc = new WikiConnection();

    	// 0.9.6 - use a retry system similar to that used for loading quizzes.
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
        		}
        		
        		progressAlert.setString("Obtained image URL from locator: URL=" + sImageURL);
                Thread.sleep(5000);
        		
        		imageData = wc.readAllBytes(sImageURL,progressAlert,false);

        		progressAlert.setString("Read image data OK: length=" + imageData.length);
                Thread.sleep(5000);

    			Image image = Image.createImage(imageData, 0, imageData.length);
    			
        		progressAlert.setString("Created Image object from data ok");
                Thread.sleep(5000);    			
    		}
    		catch (Exception e)
    		{
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
    		progressAlert.setString("Couldn't load a valid image, even after retrying");
            Thread.sleep(5000);    			
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
}

