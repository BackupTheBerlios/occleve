/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-9  Joe Gittings

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

package org.occleve.mobileclient.screens;

import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.VocabRecordStoreManager;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.qacontrol.*;
import org.occleve.mobileclient.util.*;

/**If the next testable character is an 'exotic' Unicode one (such as a Chinese character),
the application displays this input screen.*/
public class UnicodeInputScreen extends TextBox
implements CommandListener,Runnable
{
    protected char m_UnicodeCharToInput;
    protected String m_sAnswerFragmentEndingInUnicodeChar;
    
    protected MagicTypewriterController m_TestControllerThatInvokedThis;
    protected TestResults m_TestResults;

    // 0.9.6
    protected static final int PEEK_LENGTH = 3000;

    // Thread actions
    protected static final int MONITOR_TEXTBOX = 0;
    protected static final int VIEW_ANIMATION = 1;
    protected static final int VIEW_DRAWING = 2;
    protected static final int VIEW_WIKIPEDIA_ANIMATION = 3;
    protected static final int VIEW_OCRAT_ANIMATION = 4;
    protected int m_iThreadAction;
    
    protected CommonCommands m_CommonCommands;
    protected Command m_ViewAnimationCommand;
    protected Command m_ViewWikipediaAnimationCommand;
    protected Command m_ViewOcratAnimationCommand;
    protected Command m_ViewDrawingCommand;
    protected Command m_PeekCommand;
    protected Command m_CancelCommand;

    /**When true, the main thread which monitors the textbox
    contents should exit.*/
    protected boolean m_bExitThread;

    /**Accessor function.*/
    public synchronized void setExitThread() {m_bExitThread=true;}
    
    public UnicodeInputScreen
    (
        char unicodeCharToInput,
        String sAnswerFragmentEndingInUnicodeChar,
        MagicTypewriterController testControllerThatInvokedThis,
        TestResults results
    )
    throws Exception
    {
        super("Input character:","",1,TextField.ANY);

        m_UnicodeCharToInput = unicodeCharToInput;
        m_sAnswerFragmentEndingInUnicodeChar = sAnswerFragmentEndingInUnicodeChar;
        
        m_TestControllerThatInvokedThis = testControllerThatInvokedThis;
        m_TestResults = results;

        m_CommonCommands = new CommonCommands();
        m_CommonCommands.addToDisplayable(this);

        m_ViewAnimationCommand = new Command("View animation",Command.ITEM,0);
        m_ViewWikipediaAnimationCommand = new Command("View wikipedia animation",Command.ITEM,0);
        m_ViewOcratAnimationCommand = new Command("View ocrat animation",Command.ITEM,0);
        m_ViewDrawingCommand = new Command("View drawing",Command.ITEM,0);
        m_PeekCommand = new Command("Peek",Command.ITEM,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);

        addCommand(m_ViewAnimationCommand);
        addCommand(m_ViewWikipediaAnimationCommand);
        addCommand(m_ViewOcratAnimationCommand);
        addCommand(m_ViewDrawingCommand);
        addCommand(m_PeekCommand);
        addCommand(m_CancelCommand);
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
	        else if (c==m_ViewWikipediaAnimationCommand)
	        {
		        m_iThreadAction = VIEW_WIKIPEDIA_ANIMATION;
		        new Thread(this).start();
	        }
	        else if (c==m_ViewWikipediaAnimationCommand)
	        {
		        m_iThreadAction = VIEW_OCRAT_ANIMATION;
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
	        else
	        {
	        	m_CommonCommands.commandAction(c,this);
	        }
    	}
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    protected void onPeek(boolean bAutoTimeout)
    {
        String sChar = new String();
        sChar += m_UnicodeCharToInput;
        
        // 0.9.6 - on some phones the font for the alert title is much nicer
        // than the body text font - so display it in both.
        Alert alert = new Alert(sChar,sChar,null,null);

        if (bAutoTimeout)
        {
        	alert.setTimeout(PEEK_LENGTH);
        	OccleveMobileMidlet.getInstance().displayAlert(alert,this);
        }
        else
        {
        	OccleveMobileMidlet.getInstance().displayAlert(alert,this);
            try
            {
                Thread.sleep(PEEK_LENGTH);
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
    	// Need to get this first as it may display its own progress bar
    	// if it needs to index the recordstore.
		VocabRecordStoreManager mediaRsMgr =
			OccleveMobileMidlet.getInstance().getMediaRecordStoreManager();

        Alert progressAlert = new Alert(null, "Loading image...",null, null);
		progressAlert.setTimeout(Alert.FOREVER);
		StaticHelpers.safeAddGaugeToAlert(progressAlert);
		OccleveMobileMidlet.getInstance().setCurrentForm(progressAlert);

		String sHexString =
			StaticHelpers.unicodeCharToEucCnHexString(m_UnicodeCharToInput);

    	String sFilename = sHexString + ".gif";
    	String sURL = Config.OCRAT_ANIMATIONS_MIRROR_URL_STUB + sFilename;
    	byte[] imageData = MediaHelpers.loadImage(sFilename,sURL,mediaRsMgr,progressAlert);
        Image image = Image.createImage(imageData, 0, imageData.length);

    	String sTitle = "" + m_UnicodeCharToInput;
    	String sCredit = "Courtesy of lost-theory.org";
    	Alert alert = new Alert(sTitle,sCredit,image,AlertType.INFO);
    	alert.setTimeout(Alert.FOREVER);
    	OccleveMobileMidlet.getInstance().displayAlert(alert,this);

        // Viewing the drawing counts as a "wrong" keypress.
        m_TestResults.addResponse(false);
    }

    /**Attempts to display an animation of how to draw the unicode character.
    For now, that means attempting to retrieve an animated character from the mirror
    of the Ocrat animations of Chinese characters.
    0.9.5 - feature introduced.
    0.9.7 - add support for Wikipedia's stroke order animations.*/
    public void onViewAnimation(boolean bOcratAnimation,boolean bWikipediaAnimation)
    throws Exception
    {    	
        Alert progressAlert = new Alert(null, "Loading image...",null, null);
		progressAlert.setTimeout(Alert.FOREVER);
		StaticHelpers.safeAddGaugeToAlert(progressAlert);
		OccleveMobileMidlet.getInstance().setCurrentForm(progressAlert);

    	// 0.9.7 - store media files in a separate recordstore.
		VocabRecordStoreManager mediaRsMgr =
			OccleveMobileMidlet.getInstance().getMediaRecordStoreManager(progressAlert);

		progressAlert.setString("Loading image...");
		
    	byte[] animationData = null;
    	
    	String sWikipediaFilenameForRS = "WP_" + m_UnicodeCharToInput + "-order.gif";

    	byte[] bUnicodeCharBytes =
    		new Character(m_UnicodeCharToInput).toString().getBytes(Config.ENCODING);
    	StringBuffer sbURLEncodedUnicodeChar = new StringBuffer();
    	for (int i=0; i<bUnicodeCharBytes.length; i++)
    	{
    		sbURLEncodedUnicodeChar.append('%');
  
			// Integer.toHexString will produce an eight char string
			// with the byte we want in the last two chars.
			String sByteInHex =
				Integer.toHexString(bUnicodeCharBytes[i]).substring(6);	  	  

    		sbURLEncodedUnicodeChar.append(sByteInHex.toUpperCase());
    	}

    	String sWikipediaFilenameForURL =
    		sbURLEncodedUnicodeChar.toString() + "-order.gif";

    	String sWikipediaFilenameForJar = "/wikipedia_stroke/" +
    		sbURLEncodedUnicodeChar.toString() + "-order.gif";

    	String sHexString = StaticHelpers.unicodeCharToEucCnHexString(m_UnicodeCharToInput);
    	String sOcratFilename = sHexString + ".gif";
    	
    	if (bWikipediaAnimation)
    	{
    		animationData =
    			MediaHelpers.loadImageFromJar(sWikipediaFilenameForJar,progressAlert);
    	}
    	
    	if (bWikipediaAnimation && (animationData==null))
    	{
    		animationData =
    			MediaHelpers.loadImageFromRecordStore(sWikipediaFilenameForRS,mediaRsMgr);
    	}

    	if (bOcratAnimation && (animationData==null))
    	{
        	animationData =
        		MediaHelpers.loadImageFromRecordStore(sOcratFilename,mediaRsMgr);
    	}

    	// 0.9.7: The index page for these is at
    	// http://commons.wikimedia.org/wiki/Category:Order.gif_stroke_order_images
    	if (bWikipediaAnimation && (animationData==null))
    	{
	    	String sWikipediaLocatorURL = 
	    		"http://commons.wikimedia.org/w/index.php?title=File:" +
	    		sWikipediaFilenameForURL +
	    		"&action=edit&externaledit=true&mode=file";
	    	animationData =
	    		MediaHelpers.loadImageFromWeb(sWikipediaFilenameForRS,null,sWikipediaLocatorURL,
	    		mediaRsMgr,Config.CONNECTION_TRIES_LIMIT,progressAlert);
    	}

    	if (bOcratAnimation && (animationData==null))
    	{
	    	String sOcratURL = Config.OCRAT_ANIMATIONS_MIRROR_URL_STUB + sOcratFilename;
	    	animationData =
	    		MediaHelpers.loadImageFromWeb(sOcratFilename,sOcratURL,null,
	    		mediaRsMgr,Config.CONNECTION_TRIES_LIMIT,progressAlert);
    	}

    	if (animationData!=null)
    	{
	    	// Display the animation
	    	String sTitle = "" + m_UnicodeCharToInput;
	    	MediaHelpers.displayAnimation(animationData,sTitle);
		
	        // Viewing the animation counts as a "wrong" keypress.
	        m_TestResults.addResponse(false);
    	}
    	else
    	{
    		progressAlert.setString("Unable to find any animations for this character");
    		Thread.sleep(2000);
    	}
    	
    	// Revert to displaying this screen
        OccleveMobileMidlet.getInstance().setCurrentForm(this);
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
				onViewAnimation(true,true);
	    	}
	    	else if (m_iThreadAction==VIEW_WIKIPEDIA_ANIMATION)
	    	{
				onViewAnimation(false,true);
	    	}
	    	else if (m_iThreadAction==VIEW_OCRAT_ANIMATION)
	    	{
				onViewAnimation(true,false);
	    	}
	    	else if (m_iThreadAction==VIEW_DRAWING)
	    	{
				onViewDrawing();
	    	}
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    /**The main thread which monitors the textbox contents, and decides
    if it matches the character the user is supposed to write.*/
    protected void monitorTextBoxContents() throws Exception
    {
        m_bExitThread = false;
        while (m_bExitThread==false)
        {
            if (size()==1)
            {
            	monitorTextBoxContents_Inner();
            }

            // Brief pause to prevent this thread hogging CPU time.
            Thread.sleep(50);
        }
    }

    /**Subfunction for code clarity.*/
    private void monitorTextBoxContents_Inner() throws Exception
    {
        String contents = getString();
        char inputtedChar = contents.charAt(0);
        
        // 0.9.4: Allow "?" to invoke the Peek function (useful when using a pen
        // phone as it allows user to peek by writing "?" with the pen).
        // 0.9.6: Also allow the subtly different Unicode "？" to invoke it,
        // as you often get this if you try and write a question mark on a Chinese
        // pen phone.               
        boolean bPeek = ((inputtedChar=='?') && (m_UnicodeCharToInput!='?')) ||
        				((inputtedChar=='？') && (m_UnicodeCharToInput!='？'));

        boolean bViewAnim =
			((inputtedChar=='!') && (m_UnicodeCharToInput!='!')) ||
			((inputtedChar=='！') && (m_UnicodeCharToInput!='！'));

        // 0.9.7 - allow cheating directly from this screen.
        boolean bCheatOneChar =
			((inputtedChar=='*') && (m_UnicodeCharToInput!='*'));

        // 0.9.7 - allow cheating directly from this screen.
        boolean bCheatQuestion =
			((inputtedChar=='#') && (m_UnicodeCharToInput!='#'));

        if (bPeek)
        {
        	onPeek(false);
            setString("");
        }
        else if (bViewAnim)
        {
        	// 0.9.6 Allow "!" to invoke the View Animation function.
        	// Also allow the unicode equivalent "！" to invoke it.
        	onViewAnimation(true,true);
            setString("");
        }
        else if (bCheatOneChar)
        {
        	m_bExitThread = true;
            m_TestControllerThatInvokedThis.cheatOneCharacter();
            m_TestControllerThatInvokedThis.setVisible();
        }
        else if (bCheatQuestion)
        {
        	m_bExitThread = true;
            m_TestControllerThatInvokedThis.cheatQuestion();
            m_TestControllerThatInvokedThis.setVisible();
        }
        else
        {
            boolean bCorrect = (inputtedChar==m_UnicodeCharToInput);
            m_TestResults.addResponse(bCorrect);

            if (bCorrect)
            {
            	m_bExitThread = true;
                m_TestControllerThatInvokedThis.setAnswerFragmentLastLine(m_sAnswerFragmentEndingInUnicodeChar);

                // Call setVisible() on the test controller first, since
                // if the test has been completed,
                // checkForLineCompletionAndQuestionCompletion() will display
                // the results form.
                m_TestControllerThatInvokedThis.setVisible();
                m_TestControllerThatInvokedThis.checkForLineCompletionAndQuestionCompletion();	                    
            }
            else
            {
                setString("");
            }
        }
    }
}

