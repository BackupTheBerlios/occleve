/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2008  Joe Gittings

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

import java.util.*;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;

/**0.9.7: A utility class to allow the user to browse the phone's filesystem
and select files from it.*/
public class J2MEFileSelector extends List
implements CommandListener,Runnable
{	
	/**Doesn't include the "file:///" prefix. If it's null, the user is viewing the
	filesystem roots.*/
	private String m_sCurrentPath;
	
    private Command m_SelectCommand;
    private Command m_UpCommand;

    private J2MEFileSelectorListener m_Listener;
    
    /**The symbol displayed in the list of files to enable the user to move up a level.*/
    private static final String UP_SYMBOL = "..";

        
    public J2MEFileSelector(String sTitle,String sInitialPath)
    throws Exception
    {
        super(sTitle,List.IMPLICIT);

        m_sCurrentPath = sInitialPath;
        
        m_UpCommand = new Command("Up", Command.ITEM, 0);
        m_SelectCommand = new Command("Select", Command.ITEM, 0);

        addCommand(m_UpCommand);
        addCommand(m_SelectCommand);        
        setSelectCommand(m_SelectCommand);
        setCommandListener(this);
        
        new Thread(this).start();
    }

    public void run()
    {
    	System.out.println("Entering populate() thread with m_sCurrentPath = " + m_sCurrentPath);

    	try
    	{
    		populate();
    	}
    	catch (Exception e)
    	{
    		OccleveMobileMidlet.getInstance().onError(e);
    	}
    	
    	System.out.println("Exited populate() thread");
    }
    
    private void populate() throws Exception
    {
        // Clear out the existing items in this form, if any.
        deleteAll();

        if (m_sCurrentPath==null)
        {
        	populateWithRoots();
        }
        else if (m_sCurrentPath.equals(""))
        {
        	populateWithRoots();        	
        }
        else
        {
        	populateWithNonRoots();
        }                
    }

    private void populateWithRoots() throws Exception
    {
        Enumeration drives = FileSystemRegistry.listRoots();    	
        System.out.println("Called listRoots...");

        while (drives.hasMoreElements())
        {
            String sRoot = (String)drives.nextElement();
            append(sRoot,null);
        }
    }

    private void populateWithNonRoots() throws Exception
    {
    	append(UP_SYMBOL,null);
    	
		FileConnection fc = (FileConnection)
		Connector.open("file:///" + m_sCurrentPath);
		
		// Include hidden files.
		System.out.println("List of files and directories under " + m_sCurrentPath);
		Enumeration filesEnum = fc.list("*", true);
		while(filesEnum.hasMoreElements())
		{
		    String sFilename = (String) filesEnum.nextElement();
		    System.out.println(sFilename);
		    append(sFilename,null);
		}   

		fc.close();
    }

    /*Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable d)
    {
        try
        {
            commandAction_Inner(c,d);
        }
        catch (Exception e)
        {
            OccleveMobileMidlet.getInstance().onError(e);
        }
    }

    /*Subfunction for code clarity.*/
    private void commandAction_Inner(Command c,Displayable d) throws Exception
    {
        int iSelIndex = getSelectedIndex();
    	String sFilename = getString(iSelIndex);

        if (c==m_UpCommand)
        {
        	// Don't go up a level if we're at the root level.
        	if (m_sCurrentPath!=null) onUp();
        }
        else if (c==m_SelectCommand)
        {
        	if (sFilename.equals(UP_SYMBOL))
        		onUp();
        	else
        		onSelect(sFilename);
        }
    }

    private void onUp()
    {
    	String sExcludingLastChar = m_sCurrentPath.substring(0,m_sCurrentPath.length()-1);
    	int iLastButOneSlash = sExcludingLastChar.lastIndexOf('/');
    	
    	if (iLastButOneSlash==-1)
    	{
    		// Display the roots.
    		m_sCurrentPath = null;
    	}
    	else
    	{
    		// Move up a level.
    		m_sCurrentPath = m_sCurrentPath.substring(0,iLastButOneSlash+1);
    	}

    	new Thread(this).start();    	    		
    }
    
    private void onSelect(String sFilename)
    {
    	if (sFilename.endsWith("/"))
    		moveDownFilesystem(sFilename);
    	else
    	{
    		String sFullPathname = m_sCurrentPath + sFilename;
    		if (m_Listener!=null) m_Listener.fileSelected(sFullPathname);    		
    	}
    }
    
    private void moveDownFilesystem(String sFilename)
    {
    	if (m_sCurrentPath==null)
    		m_sCurrentPath = sFilename;
    	else
    	{
    		// No need to add a slash in between since if sFilename is a directory
    		// name, it'll have a trailing slash.
    		m_sCurrentPath += sFilename;
    	}

    	new Thread(this).start();    	
    }
    
    public void setJ2MEFileSelectorListener(J2MEFileSelectorListener listener)
    {
    	m_Listener = listener;
    }
}

