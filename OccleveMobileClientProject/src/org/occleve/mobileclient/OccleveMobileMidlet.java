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
OccleveMobileMidlet.java
*/

package org.occleve.mobileclient;

import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.midlet.*;

import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;

public class OccleveMobileMidlet extends MIDlet
implements CommandListener,Runnable
{
    /**Singleton implementation.*/
    private static OccleveMobileMidlet m_SingleInstance;

    /**Singleton implementation.*/
    public static OccleveMobileMidlet getInstance()
    {
        return m_SingleInstance;
    }

    // Currently can be either an LWUIT Form or MIDP Displayable until switch from
    // MIDP to LWUIT is complete.
    // LWUIT-TO-DO eventually make this an LWUIT Form only.
    protected Object m_CurrentForm;
    
    protected FileChooserForm m_FileChooserForm;

    protected Alert m_ProgressAlertCache;
    protected ListOfTestsEntry m_EntryCache;

    /**0.9.6: The recordstore which stores quizzes.*/
    protected VocabRecordStoreManager m_QuizRecordStoreManager;

    /**0.9.7: The recordstore which stores audio clips, images, etc.*/
    protected VocabRecordStoreManager m_MediaRecordStoreManager;

	public OccleveMobileMidlet()
	{
        m_SingleInstance = this;

		try
		{
			OccleveMobileMidlet_Inner();
		}
		catch (Exception e)
		{
			System.err.println(e);
            e.printStackTrace();
            Display disp = Display.getDisplay(this);
            ErrorForm ef = new ErrorForm(e);

            // NB. Sony-E K300 minimizes app if you try to
            // call Display.setCurrent(); here.
            m_CurrentForm = ef;
	    }
	}

	private void OccleveMobileMidlet_Inner() throws Exception
	{
		System.out.println("Entering OccleveMobileMidlet_Inner()");

		// 0.9.7 - initialize display for LWUIT
    	com.sun.lwuit.Display.init(OccleveMobileMidlet.getInstance());

		try
		{
			Resources r = Resources.open("/javaTheme.res");
			UIManager.getInstance().setThemeProps(r.getTheme("javaTheme"));
			///////com.sun.lwuit.Display.getInstance().getCurrent().refreshTheme();
		}
		catch (Exception e)
		{
			System.out.println("Couldn't load theme.");
			onError(e);
		}

		m_FileChooserForm = new FileChooserForm(true);
		System.out.println("Constructed FileChooserForm");
		
		setCurrentForm(m_FileChooserForm);
	}

	public void showSplashScreen() throws Exception
	{
    	// LWUIT-TO-DO Image logoImage = StaticHelpers.loadOccleveLogo();
    	String sMsg = "occleve.berlios.de/pocketchinese\n" +
    					"©2007-9 Joe Gittings & contributors";
    	ProgressAlert splashScreen = new ProgressAlert("",sMsg);
    	//////setCurrentForm(splashScreen);
    	System.out.println("Showed splash screen");
	}
	
	public void startApp()
	{
        setCurrentForm(m_CurrentForm);
	}

    public Displayable getCurrentDisplayable()
    {
        return Display.getDisplay(this).getCurrent();
    }

    /**'form' can either be an LWUIT Form, or an MIDP2 Displayable.*/
    public void setCurrentForm(Object form)
    {
        m_CurrentForm = form;
        if (form instanceof Displayable)
        {
        	////////com.sun.lwuit.Display.init(null);
        	Display.getDisplay(this).setCurrent((Displayable)form);
        }
        else
        {
        	/////////////com.sun.lwuit.Display.init(OccleveMobileMidlet.getInstance());
    		com.sun.lwuit.Form lwuitForm = (com.sun.lwuit.Form)form;
    		lwuitForm.show();
        }
    }

	public void displayAlert(Alert alert,Displayable nextScreen)
	{
	    Display.getDisplay(this).setCurrent(alert,nextScreen);
	}

    public void displayAlertThenFileChooser(Alert alert)
    {
    	// LWUIT TODO
    	displayFileChooser(false);
        Display.getDisplay(this).setCurrent(alert);
    }

	public void pauseApp() {}

	public void destroyApp(boolean unconditional) {}

    /**Implementation of CommandListener.*/
    public void commandAction(Command c, Displayable s)
    {
        System.out.println("Entering commandAction(Command,Displayable)");

        if (c.getCommandType() == Command.EXIT)
            notifyDestroyed();
        else if (c.getCommandType() == Command.BACK)
        {
            displayFileChooser();
        }
        else
        {
            onError("Unknown command type in OccleveMobileMidlet.commandAction");
        }
    }

    public void displayFileChooser()
    {
        displayFileChooser(false);
    }

    public void displayFileChooser(boolean bRefreshListOfFiles)
    {
        if (bRefreshListOfFiles)
        {
            try
            {
                m_FileChooserForm.populateWithFilenames();
            }
            catch (Exception e) {onError(e);}
        }

        com.sun.lwuit.Display.init(OccleveMobileMidlet.getInstance());
		m_FileChooserForm.show();
        ///// MIDP code: setCurrentForm(m_FileChooserForm);
    }

    public void repopulateFileChooser() throws Exception
    {
        m_FileChooserForm.populateWithFilenames();
    }

    public void displayTestOptions(Test theTest) throws Exception
    {
    	m_FileChooserForm.displayTestOptions(theTest);
    }
    
    public void onError(Throwable t)
    {
        onError(t.toString());
        t.printStackTrace();
    }

    /*Helper method for other parts of the software to display an error.*/
    public void onError(String sError)
    {
        ErrorForm ef = new ErrorForm(sError);
        setCurrentForm(ef);
    }

    public void onUnknownCommand(Class c)
    {
        String sClassname = c.getName();
        String sMsg = "Unknown Command in " + sClassname + ".commandAction";
        ErrorForm ef = new ErrorForm(sMsg);
        setCurrentForm(ef);
    }

    public void displayTest(ListOfTestsEntry entry)
    throws Exception
    {
        Alert alt = new Alert(null, "Loading " + entry.getFilename(), null, null);
        alt.setTimeout(Alert.FOREVER);
        StaticHelpers.safeAddGaugeToAlert(alt);
        Display.getDisplay(this).setCurrent(alt);

        m_EntryCache = entry;
        m_ProgressAlertCache = alt;
        new Thread(this).start();
    }

    public void run()
    {
        try
        {
            displayTest_Thread(m_EntryCache,m_ProgressAlertCache);
        }
        catch (Exception e) {onError(e);}
    }

    private void displayTest_Thread(ListOfTestsEntry entry,Alert progressAlert)
    throws Exception
    {
        Test theTest = new Test(entry,progressAlert);

        // Strip .txt from screen heading.
        String sHeading = entry.getFilename();
        if (sHeading.endsWith(".txt"))
        {
            sHeading = sHeading.substring(0, sHeading.length() - 4);
        }

        Display disp = Display.getDisplay(this);
        VocabViewerScreen viewerForm = new VocabViewerScreen(sHeading,theTest);
        setCurrentForm(viewerForm);
    }

    public void tryToPlaceinBackground()
    {
        // DOESNT WORK ---- notifyPaused();

        try
        {
            Manager.playTone(69, 200, 100);
        } catch (Exception e) {onError(e);}

        // Setting current Displayable to null will only have the
        // desired effect on SOME platforms.
        Display.getDisplay(this).setCurrent(null);
    }

    public void beep()
    {
        try
        {
            Manager.playTone(69, 200, 100);
        }
        catch (Exception e) {onError(e);}
    }
    
    public boolean isLocalFilesystemAvailable()
    {
        String sPropName = "microedition.io.file.FileConnection.version";
        String sResult = System.getProperty(sPropName);
        return (sResult!=null);
    }

    /**0.9.6 - for speed, one global copy of this is kept here.*/
    public VocabRecordStoreManager getQuizRecordStoreManager()
    {
    	if (m_QuizRecordStoreManager==null)
    	{
    		try
    		{
    			m_QuizRecordStoreManager =
    				new VocabRecordStoreManager(VocabRecordStoreManager.QUIZ_RECORDSTORE_NAME);
    		}
    		catch (Exception e) {onError(e);}
    	}
    	
    	return m_QuizRecordStoreManager;
    }

    public VocabRecordStoreManager getMediaRecordStoreManager()
    throws Exception
    {
    	return getMediaRecordStoreManager(null);
    }

    /**Introduced 0.9.7 - now using separate recordstores for quizzes and media files.*/
    public VocabRecordStoreManager getMediaRecordStoreManager(Alert existingAlert)
    throws Exception
    {
    	if (m_MediaRecordStoreManager==null)
    	{
			// This could be time consuming if there are lots of them, so
			// display a progress alert.
			Displayable currentDisp = getCurrentDisplayable();

			Alert progress;
			if (existingAlert!=null)
				progress = existingAlert;
			else
			{
    			progress = new Alert(null,"",null,AlertType.INFO);
    	        displayAlert(progress,currentDisp);
			}
			progress.setString("Indexing media files...");
			
			m_MediaRecordStoreManager =
				new VocabRecordStoreManager(VocabRecordStoreManager.MEDIA_RECORDSTORE_NAME);
			
			if (existingAlert==null) setCurrentForm(currentDisp);
		}
    	
    	return m_MediaRecordStoreManager;
    }
}

