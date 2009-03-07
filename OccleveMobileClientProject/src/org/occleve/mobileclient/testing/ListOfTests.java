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

package org.occleve.mobileclient.testing;

import java.util.*;

import javax.microedition.io.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.media.Manager;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.util.*;

/**Contains a list of all the tests known to the system, whether they
be in the JAR's resource file, or in the application's RecordStore.*/
public class ListOfTests
{
    private static final String JAR_INDEX_FILENAME = "/list_of_tests.txt";

    private String m_sOriginalProgressAlertPrompt;

    protected Vector m_vEntries = new Vector();
    public int getSize() {return m_vEntries.size();}

    protected Hashtable m_htEntriesKeyedByFilename = new Hashtable();

    public ListOfTestsEntry getEntry(int i)
    {
    	ListOfTestsEntry entry = (ListOfTestsEntry)m_vEntries.elementAt(i);
        return entry;
    }

    public String getFilename(int i)
    {
    	ListOfTestsEntry entry = (ListOfTestsEntry)m_vEntries.elementAt(i);
        return entry.getFilename();
    }

    public Integer getRecordStoreIDByIndex(int iIndex)
    {
        ListOfTestsEntry entry = (ListOfTestsEntry)m_vEntries.elementAt(iIndex);
        return entry.getRecordStoreID();
    }

    public String getLocalFilesystemURLByIndex(int iIndex)
    {
        ListOfTestsEntry entry = (ListOfTestsEntry)m_vEntries.elementAt(iIndex);
        return entry.getLocalFilesystemURL();
    }

    public Integer getRecordStoreID(String sFilename)
    {
    	ListOfTestsEntry entry = (ListOfTestsEntry)m_htEntriesKeyedByFilename.get(sFilename);
        return entry.getRecordStoreID();
    }

    /*
    private class Entry
    {
        private String m_sFilename;
        private Integer m_iRecordStoreID;
        private String m_sLocalFilesystemURL;
    }
    */

    public ListOfTests() throws Exception
    {
    	this(null);
    }

    public ListOfTests(Alert progressAlert) throws Exception
    {
    	if (progressAlert!=null)
    	{
    		m_sOriginalProgressAlertPrompt = progressAlert.getString();
    	}
    	
        ListOfTests_LoadFromJar();
        ListOfTests_LoadFromRS(progressAlert);
        
        if (OccleveMobileMidlet.getInstance().isLocalFilesystemAvailable())
        {
        	try
        	{
        		ListOfTests_LoadFromFilesystem(progressAlert);
        	}
        	catch (Exception e)
        	{
        		// Even if the phone supports JSR75, perhaps the permissions set up
        		// will not allow access. Silently tolerate this.
        		System.err.println(e);
        	}
        }
        
        ListOfTests_AlphaSort();
        System.out.println("Number of tests = " + m_vEntries.size());
    }

    /**Subfunction for code clarity.
    Read the list of tests that are embedded in the jar.*/
    private void ListOfTests_LoadFromJar() throws Exception
    {
        String sContents = StaticHelpers.readUnicodeFile(JAR_INDEX_FILENAME);

        // Release 0.9.3 doesn't have any tests in the jar.
        // Deal with this possibility.
        sContents = sContents.trim();
        if (sContents.length()==0)
        {
            System.out.println("No tests in the JAR...");
            return;
        }

        // FUDGE: Discard the first character of the contents list since
        // for reasons I don't understand, it's junk.
        // TO DO: Sort this problem out.
        ///////////sContents = sContents.substring(1);

        Vector vTestFilenames = StaticHelpers.stringToVector(sContents, true);

        for (int i = 0; i < vTestFilenames.size(); i++)
        {
            String sFilename = (String) vTestFilenames.elementAt(i);

            // Make sure the filename doesn't end in a newline.
            if (sFilename.endsWith(Constants.NEWLINE))
            {
                sFilename =
                        sFilename.substring(0,
                                            sFilename.length() - Constants.NEWLINE_LENGTH);
            }

            ListOfTestsEntry entry = new ListOfTestsEntry(sFilename,null,null);

            m_vEntries.addElement(entry);
            m_htEntriesKeyedByFilename.put(sFilename, entry);
        }

        System.out.println("Finished reading files from JAR");

        long lFirstChar = (long)( getFilename(0).charAt(0) );
        System.out.println("First char code = " + lFirstChar);

        long lSecondChar = (long)( getFilename(0).charAt(1) );
        System.out.println("Second char code = " + lSecondChar);
    }

    /**Subfunction for code clarity.
    Now get the list of tests that are in the recordstore.*/
    private void ListOfTests_LoadFromRS(Alert progressAlert) throws Exception
    {
    	// 0.9.6 - this is the call which will actually instantiate the
    	// VocabRecordStoreManager for quizzes.
		VocabRecordStoreManager quizRsMgr =
			OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();

        Hashtable rsIndex = quizRsMgr.getRecordIndicesKeyedByFilenames();
        System.out.println("Size of rsIndex = " + rsIndex.size());

        Enumeration enumKeys = rsIndex.keys();
        while (enumKeys.hasMoreElements())
        {
        	if ((m_vEntries.size()%5 == 0) && (progressAlert!=null))
        	{
        		String sNewPrompt = m_sOriginalProgressAlertPrompt + " - " +
        							m_vEntries.size() + " quizzes";
        		progressAlert.setString(sNewPrompt);
        	}
        	
            String rsFilename = (String) enumKeys.nextElement();
            System.out.println("rsFilename = " + rsFilename);

            // Ignore MP3 and (from 0.9.5) GIF files
            boolean bIgnore = 
            	(rsFilename.toLowerCase().endsWith(".mp3")) ||
            	(rsFilename.toLowerCase().endsWith(".gif"));
            
            if (bIgnore==false)
            {
                Integer rsID = (Integer) rsIndex.get(rsFilename);

                ListOfTestsEntry entry = (ListOfTestsEntry) m_htEntriesKeyedByFilename.get(rsFilename);
                if (entry != null)
                {
                    // The entry already exists in the JAR. Record the record store ID.
                    entry.setRecordStoreID(rsID);
                }
                else
                {
                    // This is a new test that isn't in the JAR.
                    // For convenience, put it at the start of the list.
                	ListOfTestsEntry entry2 = new ListOfTestsEntry(rsFilename,rsID,null);

                    m_vEntries.insertElementAt(entry2, 0);
                    m_htEntriesKeyedByFilename.put(rsFilename, entry2);
                }
            }
        }
    }

    /**Get the list of tests that are in the phone's filesystem.*/
    private void ListOfTests_LoadFromFilesystem(Alert progressAlert) throws Exception
    {
    	Hashtable filenamesToURLs = FileConnectionHelpers.getAllFilenamesInRootDirs("*.xml");
    	
    	// Make sure the progress alert is still visible (the phone's own
    	// security popup for JSR75 access might have displaced it).
        OccleveMobileMidlet.getInstance().setCurrentForm(progressAlert);
    	
		Enumeration filelist = filenamesToURLs.keys();
		while(filelist.hasMoreElements())
		{
		    String filename = (String) filelist.nextElement();		    
		    if (filename.endsWith(".xml"))
		    {
		    	String sFileURL = (String)filenamesToURLs.get(filename);
			    ListOfTestsEntry entry = new ListOfTestsEntry(filename,null,sFileURL);
	            m_vEntries.addElement(entry);
	            m_htEntriesKeyedByFilename.put(entry.getFilename(), entry);
		    }
		}   
    }

    /**Subfunction for code clarity.
    Sort the list into alpha order.
    TO DO: Use a better sort algorithm than bubble sort.*/
    private void ListOfTests_AlphaSort() throws Exception
    {
        boolean bSorted = false;
        final int iLastIndex = m_vEntries.size()-1;

        while (bSorted==false)
        {
            bSorted = true;
            for (int i = 0; i < iLastIndex; i++)
            {
                ListOfTestsEntry entryA = (ListOfTestsEntry)m_vEntries.elementAt(i);
                ListOfTestsEntry entryB = (ListOfTestsEntry)m_vEntries.elementAt(i+1);

                int comp = entryA.getFilename().toLowerCase().compareTo
                (
                    entryB.getFilename().toLowerCase()
                );

                if (comp > 0)
                {
                    m_vEntries.setElementAt(entryA,i+1);
                    m_vEntries.setElementAt(entryB,i);
                    bSorted = false;
                }
            }
        }
    }
}

