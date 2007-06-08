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
@version 0.9.0
*/

package org.occleve.mobileclient.testing;

import java.util.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.recordstore.*;

/**Contains a list of all the tests known to the system, whether they
be in the JAR's resource file, or in the application's RecordStore.*/
public class ListOfTests
{
    private static final String FILENAME = "/list_of_tests.txt";

    protected Vector m_vEntries = new Vector();
    public int getSize() {return m_vEntries.size();}

    protected Hashtable m_htEntriesKeyedByFilename = new Hashtable();

    public String getFilename(int i)
    {
        Entry entry = (Entry)m_vEntries.elementAt(i);
        return entry.m_sFilename;
    }

    public Integer getRecordStoreIDByIndex(int iIndex)
    {
        Entry entry = (Entry)m_vEntries.elementAt(iIndex);
        return entry.m_iRecordStoreID;
    }

    public Integer getRecordStoreID(String sFilename)
    {
        Entry entry = (Entry)m_htEntriesKeyedByFilename.get(sFilename);
        return entry.m_iRecordStoreID;
    }

    private class Entry
    {
        private String m_sFilename;
        private Integer m_iRecordStoreID;
    }

    public ListOfTests() throws Exception
    {
        ListOfTests_LoadFromJar();
        ListOfTests_LoadFromRS();
////        ListOfTests_AlphaSort();
        System.out.println("Number of tests = " + m_vEntries.size());
    }

    /**Subfunction for code clarity.
    Read the list of tests that are embedded in the jar.*/
    private void ListOfTests_LoadFromJar() throws Exception
    {
        String sContents = StaticHelpers.readUnicodeFile(FILENAME);

        // FUDGE: Discard the first character of the contents list since
        // for reasons I don't understand, it's junk.
        // TO DO: Sort this problem out.
        sContents = sContents.substring(1);

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

            Entry entry = new Entry();
            entry.m_sFilename = sFilename;
            entry.m_iRecordStoreID = null;

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
    private void ListOfTests_LoadFromRS() throws Exception
    {
        VocabRecordStoreManager mgr = new VocabRecordStoreManager();
        Hashtable rsIndex = mgr.getRecordIndicesKeyedByFilenames();
        System.out.println("Size of rsIndex = " + rsIndex.size());

        Enumeration enumKeys = rsIndex.keys();
        while (enumKeys.hasMoreElements())
        {
            String rsFilename = (String) enumKeys.nextElement();
            System.out.println("rsFilename = " + rsFilename);

            // Ignore MP3 files
            if (rsFilename.toLowerCase().endsWith(".mp3") == false)
            {
                Integer rsID = (Integer) rsIndex.get(rsFilename);

                Entry entry = (Entry) m_htEntriesKeyedByFilename.get(rsFilename);
                if (entry != null)
                {
                    // The entry already exists in the JAR. Record the record store ID.
                    entry.m_iRecordStoreID = rsID;
                }
                else
                {
                    // This is a new test that isn't in the JAR.
                    // For convenience, put it at the start of the list.
                    entry = new Entry();
                    entry.m_sFilename = rsFilename;
                    entry.m_iRecordStoreID = rsID;

                    m_vEntries.insertElementAt(entry, 0);
                    m_htEntriesKeyedByFilename.put(rsFilename, entry);
                }
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
                Entry entryA = (Entry)m_vEntries.elementAt(i);
                Entry entryB = (Entry)m_vEntries.elementAt(i+1);

                int comp = entryA.m_sFilename.toLowerCase().compareTo
                (
                    entryB.m_sFilename.toLowerCase()
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

