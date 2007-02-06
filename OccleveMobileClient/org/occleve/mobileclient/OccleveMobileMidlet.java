/* OccleveMobileMidlet.java */

package org.occleve.mobileclient;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.midlet.*;

import org.occleve.mobileclient.screens.*;
import org.occleve.mobileclient.testing.*;

public class OccleveMobileMidlet extends MIDlet implements CommandListener
{
    /**Singleton implementation.*/
    private static OccleveMobileMidlet m_SingleInstance;

    /**Singleton implementation.*/
    public static OccleveMobileMidlet getInstance()
    {
        return m_SingleInstance;
    }

    protected Displayable m_CurrentForm;
    protected FileChooserForm m_FileChooserForm;

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
            m_FileChooserForm = new FileChooserForm(true);
            m_CurrentForm = m_FileChooserForm;
	}

        // Not sure if this really works...
        /*
        private void getDirListing() throws IOException
        {
            FileConnection fc = (FileConnection) Connector.open("file:///");
            Enumeration dirListing = fc.list("*",true);
            while (dirListing.hasMoreElements())
            {
                String sFile = (String)dirListing.nextElement();
                System.out.println(sFile);
            }
        }
        */

	public void startApp()
	{
            Display.getDisplay(this).setCurrent(m_CurrentForm);
	}

        public void setCurrentForm(Displayable form)
        {
            m_CurrentForm = form;
            Display.getDisplay(this).setCurrent(form);
        }

        public void displayAlert(Alert alert,Displayable nextScreen)
        {
            Display.getDisplay(this).setCurrent(alert,nextScreen);
        }

        public void displayAlertThenFileChooser(Alert alert)
        {
            Display.getDisplay(this).setCurrent(alert,m_FileChooserForm);
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

            setCurrentForm(m_FileChooserForm);
        }

        public void repopulateFileChooser() throws Exception
        {
            m_FileChooserForm.populateWithFilenames();
        }

        public void onError(Exception e)
        {
            onError(e.toString());
            e.printStackTrace();
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

        public void displayTest(String sFilename,Integer iRecordStoreID) throws Exception
        {
            Test theTest = new Test(sFilename,iRecordStoreID);
            System.out.println("Read unicode file");

            // Strip .txt from screen heading.
            String sHeading = sFilename;
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
}

