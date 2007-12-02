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

import java.io.ByteArrayOutputStream;
import javax.microedition.lcdui.*;

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
    protected int m_iThreadAction;

    protected Command m_ViewAnimationCommand;
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

		// 0.9.5: Output char as EUC-CN
		System.out.println("Unicode char = " + unicodeCharToInput);
		StaticHelpers.unicodeCharToEucCnHexString(unicodeCharToInput);
                      
        m_UnicodeCharToInput = unicodeCharToInput;
        m_TestControllerThatInvokedThis = testControllerThatInvokedThis;
        m_TestResults = results;

        m_ViewAnimationCommand = new Command("View animation",Command.ITEM,0);
        m_PeekCommand = new Command("Peek",Command.ITEM,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);
        m_PauseCommand = new Command("Pause",Command.ITEM,0);

        addCommand(m_ViewAnimationCommand);
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
        if (c==m_ViewAnimationCommand)
        {
        	try
        	{
                m_iThreadAction = VIEW_ANIMATION;
                new Thread(this).start();
        	}
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
        }
        else if (c==m_PeekCommand)
        {
        	onPeek(true);
        }
        else if (c==m_CancelCommand)
        {
            try
            {
                m_bExitThread = true;
                m_TestControllerThatInvokedThis.setVisible();
            }
            catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
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

    /**0.9.5: Attempts to display an animation of how to draw the unicode character.
    For now, that means attempting to retrieve an animated character from the mirror
    of the Ocrat animations of Chinese characters.*/
    public void onViewAnimation() throws Exception
    {
    	String sHexString =
			StaticHelpers.unicodeCharToEucCnHexString(m_UnicodeCharToInput);

    	String sFilename = sHexString + ".gif";
    	String sURL = Config.OCRAT_ANIMATIONS_MIRROR_URL_STUB + sFilename;

    	Image animation = loadImage(sFilename,sURL);
    	String sTitle = "" + m_UnicodeCharToInput;
    	String sCredit = "Courtesy of lost-theory.org";
    	Alert alert = new Alert(sTitle,sCredit,animation,AlertType.INFO);

    	OccleveMobileMidlet.getInstance().displayAlert(alert,this);
        try
        {
            Thread.sleep(2000);
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    	OccleveMobileMidlet.getInstance().setCurrentForm(this);

        // Viewing the animation counts as a "wrong" keypress.
        m_TestResults.addResponse(false);
    }

    private Image loadImage(String sImageFilename,String sImageURL)
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
        
        Image image = Image.createImage(imageData, 0, imageData.length);
        return image;
    }

    
    public void run()
    {
    	if (m_iThreadAction==MONITOR_TEXTBOX)
    	{
	        try
	        {
	            monitorTextBoxContents();
	        }
	        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    	}
    	else if (m_iThreadAction==VIEW_ANIMATION)
    	{
    		try
    		{
    			onViewAnimation();
    		}
	        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    	}
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

