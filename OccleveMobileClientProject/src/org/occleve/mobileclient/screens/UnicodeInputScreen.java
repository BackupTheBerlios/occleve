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

package org.occleve.mobileclient.screens;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.microedition.lcdui.*;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.VocabRecordStoreManager;
import org.occleve.mobileclient.serverbrowser.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.qacontrol.*;
import org.occleve.mobileclient.testing.qaview.*;

public class UnicodeInputScreen extends TextBox
implements CommandListener,Runnable
{
    protected char m_UnicodeCharToInput;
    protected MagicTypewriterController m_TestControllerThatInvokedThis;
    protected TestResults m_TestResults;

    protected static final int MONITOR_TEXTBOX = 0;
    protected static final int VIEW_ANIMATION = 1;
    protected static final int VIEW_DRAWING = 2;
    protected int m_iThreadAction;

    protected Command m_ViewAnimationCommand;
    protected Command m_ViewDrawingCommand;
    protected Command m_PeekCommand;
    protected Command m_CancelCommand;
    protected Command m_PauseCommand;

    protected boolean m_bExitThread;

    public UnicodeInputScreen
    (
        char unicodeCharToInput,
        MagicTypewriterController testControllerThatInvokedThis,
        TestResults results
    )
    throws Exception
    {
        super("Input character:","",1,TextField.ANY);
                      
        m_UnicodeCharToInput = unicodeCharToInput;
        m_TestControllerThatInvokedThis = testControllerThatInvokedThis;
        m_TestResults = results;

        m_ViewAnimationCommand = new Command("View animation",Command.ITEM,0);
        m_ViewDrawingCommand = new Command("View drawing",Command.ITEM,0);
        m_PeekCommand = new Command("Peek",Command.ITEM,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);
        m_PauseCommand = new Command("Pause",Command.ITEM,0);

        addCommand(m_ViewAnimationCommand);
        addCommand(m_ViewDrawingCommand);
        addCommand(m_PeekCommand);
        addCommand(m_CancelCommand);
        addCommand(m_PauseCommand);
        setCommandListener(this);

        m_iThreadAction = MONITOR_TEXTBOX;
        new Thread(this).start();
    }

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
    	try
    	{
	        if (c==m_ViewAnimationCommand)
	        {
		        m_iThreadAction = VIEW_ANIMATION;
		        new Thread(this).start();
	        }
	        else if (c==m_ViewDrawingCommand)
	        {
	            m_iThreadAction = VIEW_DRAWING;
	            new Thread(this).start();
	        }
	        else if (c==m_PeekCommand)
	        {
	        	onPeek(true);
	        }
	        else if (c==m_CancelCommand)
	        {
	            m_bExitThread = true;
	            m_TestControllerThatInvokedThis.setVisible();
	        }
	        else if (c==m_PauseCommand)
	        {
	            OccleveMobileMidlet.getInstance().tryToPlaceinBackground();
	        }
	        else
	        {
	            OccleveMobileMidlet.getInstance().onError("Unknown command in UnicodeInputScreen.commandAction");
	        }
    	}
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    protected void onPeek(boolean bAutoTimeout)
    {
        String sChar = new String();
        sChar += m_UnicodeCharToInput;
        Alert alert = new Alert(null,sChar,null,null);

        if (bAutoTimeout)
        {
        	alert.setTimeout(2000);
        	OccleveMobileMidlet.getInstance().displayAlert(alert,this);
        }
        else
        {
        	OccleveMobileMidlet.getInstance().displayAlert(alert,this);
            try
            {
                Thread.sleep(2000);
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        	OccleveMobileMidlet.getInstance().setCurrentForm(this);
        }

        // Peeking at the character counts as a "wrong" keypress.
        m_TestResults.addResponse(false);    	
    }

    /**0.9.5: Attempts to display a large drawing of a character. For now that means
    displaying the animated GIF from the Ocrat mirror as a still image.*/
    public void onViewDrawing() throws Exception
    {
    	String sHexString =
			StaticHelpers.unicodeCharToEucCnHexString(m_UnicodeCharToInput);

    	String sFilename = sHexString + ".gif";
    	String sURL = Config.OCRAT_ANIMATIONS_MIRROR_URL_STUB + sFilename;
    	byte[] imageData = loadImage(sFilename,sURL);
        Image image = Image.createImage(imageData, 0, imageData.length);

    	String sTitle = "" + m_UnicodeCharToInput;
    	String sCredit = "Courtesy of lost-theory.org";
    	Alert alert = new Alert(sTitle,sCredit,image,AlertType.INFO);
    	OccleveMobileMidlet.getInstance().displayAlert(alert,this);

        // Viewing the drawing counts as a "wrong" keypress.
        m_TestResults.addResponse(false);
    }

    /**0.9.5: Attempts to display an animation of how to draw the unicode character.
    For now, that means attempting to retrieve an animated character from the mirror
    of the Ocrat animations of Chinese characters.*/
    public void onViewAnimation() throws Exception
    {
    	String sHexString =
			StaticHelpers.unicodeCharToEucCnHexString(m_UnicodeCharToInput);

    	String sFilename = sHexString + ".gif";
    	String sURL = Config.OCRAT_ANIMATIONS_MIRROR_URL_STUB + sFilename;
    	byte[] animationData = loadImage(sFilename,sURL);


        // Get a player for the clip.
        ByteArrayInputStream bais = new ByteArrayInputStream(animationData);
        Player player = null;

        try
        {
            player = Manager.createPlayer(bais, "image/gif");
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
    	String sTitle = "" + m_UnicodeCharToInput;
        Form videoForm = new Form(sTitle);
        videoForm.append(videoItem);
    	OccleveMobileMidlet.getInstance().setCurrentForm(videoForm);

        player.start();

        // Wait until the animation has finished, plus a couple of seconds afterwards.
        try
        {
	        do
	        {
	            Thread.sleep(1000);
	        } while (player.getState()==Player.STARTED);

            Thread.sleep(4000);
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        
        OccleveMobileMidlet.getInstance().setCurrentForm(this);

    	// Release player resources. Failing to do this results in noticeable
    	// resource drain on a Sony Ericsson Z558c.
    	player.stop();
    	player.deallocate();
    	player.close();
    	
        // Viewing the animation counts as a "wrong" keypress.
        m_TestResults.addResponse(false);
    }

    private byte[] loadImage(String sImageFilename,String sImageURL)
    throws Exception
    {
        // Display a progress bar during the whole process
        Alert progressAlert = new Alert(null, "Loading image...",
                                    null, null);
        progressAlert.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(progressAlert);
        Displayable previousDisplayable =
            OccleveMobileMidlet.getInstance().getCurrentDisplayable();
        OccleveMobileMidlet.getInstance().setCurrentForm(progressAlert);

        VocabRecordStoreManager mgr = new VocabRecordStoreManager();
        Integer rsid = mgr.findRecordByFilename(sImageFilename);
        System.out.println("rsid = " + rsid);

        byte[] imageData;
        if (rsid!=null)
        {
            // Load from recordstore.
        	System.out.println("Animation already in recordstore... loading");
            imageData = mgr.getRecordContentsMinusFilename(rsid.intValue());
        }
        else
        {
            // Load from website.
        	System.out.println("Animation not in recordstore... loading from web");
        	WikiConnection wc = new WikiConnection();
        	imageData = wc.readAllBytes(sImageURL,progressAlert,false);
        	
            // Save the image into the recordstore for future use
            mgr.createFileInRecordStore(sImageFilename,imageData,false);
        }
                
        return imageData;
    }

    
    public void run()
    {
    	try
    	{
	    	if (m_iThreadAction==MONITOR_TEXTBOX)
	    	{
		        monitorTextBoxContents();
	    	}
	    	else if (m_iThreadAction==VIEW_ANIMATION)
	    	{
				onViewAnimation();
	    	}
	    	else if (m_iThreadAction==VIEW_DRAWING)
	    	{
				onViewDrawing();
	    	}
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    protected void monitorTextBoxContents() throws Exception
    {
        m_bExitThread = false;
        while (m_bExitThread==false)
        {
            if (size()==1)
            {
                String contents = getString();
                char inputtedChar = contents.charAt(0);
                
                // 0.9.4: Allow "?" to invoke the Peek function (useful when using a pen
                // phone as it allows user to peek by writing "?" with the pen).
                if ((inputtedChar=='?') && (m_UnicodeCharToInput!='?'))
                {
                	onPeek(false);
                    setString("");
                }
                else
                {
	                boolean bCorrect = (inputtedChar==m_UnicodeCharToInput);
	                m_TestResults.addResponse(bCorrect);
	
	                if (bCorrect)
	                {
	                    m_bExitThread = true;
	                    m_TestControllerThatInvokedThis.appendToAnswerFragment(inputtedChar);
	                    m_TestControllerThatInvokedThis.setVisible();
	                    m_TestControllerThatInvokedThis.skipPunctuation();
	                }
	                else
	                {
	                    setString("");
	                }
                }
            }

            // Brief pause to prevent this thread hogging CPU time.
            try
            {
                Thread.sleep(50);
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
    }

}

