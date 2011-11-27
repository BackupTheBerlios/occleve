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

import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.ListOfTestsEntry;

/**0.9.7: Put FileConnection API specific stuff in here.*/
public class FileConnectionHelpers
{		
	/**Return an InputStream to a file on the local filesystem.*/
	public static InputStream openFileInputStream(String sFilename)
	throws Exception
	{
        FileConnection fc =
        	(FileConnection)Connector.open(sFilename,Connector.READ);
        if(!fc.exists())
        {
        	throw new IOException("File does not exist");
        }
        InputStream is = fc.openInputStream();
        return is;
	}
	
	public static Hashtable getAllFilenamesInRootDirs(String sFilter,String subdir)
	throws Exception
	{
		Hashtable htResults = new Hashtable();
				
		Enumeration drives = FileSystemRegistry.listRoots();
		while (drives.hasMoreElements())
		{
			String root = (String)drives.nextElement();
			String rootURL = "file:///" + root;
			if (subdir!=null) {
				if (rootURL.endsWith("/")==false) rootURL += "/";
				rootURL += subdir + "/";
			}
			System.out.println("Root URL=" + rootURL);
			
			// Setting the mode to READ reduces the number of
			// security challenge prompts when the midlet is not signed
			FileConnection fc = (FileConnection)Connector.open(rootURL,Connector.READ);

			System.out.println("Getting filelist");
			Enumeration filelist = fc.list(sFilter,true);
			System.out.println("Got filelist");
			while(filelist.hasMoreElements())
			{
			    String sFilename = (String) filelist.nextElement();
			    // String sFileURL = "file:///" + root + sFilename;
			    String sFileURL = rootURL + sFilename;
			    htResults.put(sFilename,sFileURL);
			}   
			fc.close();
		}

		System.out.println("Found " + htResults.size() + " files in root dirs");
		return htResults;
	}
}

