/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-8  Joe Gittings

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

package org.occleve.mobileclient.recordstore;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.testing.ListOfTestsEntry;

import javax.microedition.io.file.FileConnection;
import javax.microedition.io.Connector;

/**Format of a record is the filename (as a UTF-encoded string), followed
by the UTF-encoded file data.
File extensions are used to differentiate file types:
Config.OCCLEVE_XML_FILETYPE for Occleve XML quizzes.
Config.WIKIVERSITY_FILETYPE for Wikiversity wikitext quizzes.
Config.WIKIVERSITY_ADDITIONS_FILETYPE for user additions to a Wikiversity quiz.
Config.AUDIO_FILETYPE for audio clips.*/
public class VocabRecordStoreManager
{
	/**The name of the recordstore which stores quizzes.*/
    public final static String QUIZ_RECORDSTORE_NAME = "OccleveMobileClientTests";

	/**The name of the recordstore which stores audio clips, animations, etc.*/
    public final static String MEDIA_RECORDSTORE_NAME = "OccleveMobileClientMedia";

    /**The name of the recordstore which this instance is encapsulating.*/
    private String m_sRecordStoreName;
    
    private boolean m_bUseJavaUTF = true;

    // 0.9.6 - introduced this member so that it only needs to be built once for a given
    // recordstore. Should speed up test, image, and MP3 loading a lot.
    private Hashtable m_RecordIndicesKeyedByFilenames;
    
    // 0.9.5: Commented out these - they could only cause trouble!
    //////public void useJavaUTF() {m_bUseJavaUTF = true;}
    //////public void useStandardUTF() {m_bUseJavaUTF = false;}

    public VocabRecordStoreManager(String sRecordStoreName) throws Exception
    {
    	m_sRecordStoreName = sRecordStoreName;
    	
    	m_RecordIndicesKeyedByFilenames = buildRecordIndicesKeyedByFilenames();
    }

    /**Returns a Hashtable with the record indices keyed by the filenames.*/
    public Hashtable getRecordIndicesKeyedByFilenames() throws Exception
    {
    	return m_RecordIndicesKeyedByFilenames;
    }

    /**Builds a Hashtable with the record indices keyed by the filenames.*/
    private Hashtable buildRecordIndicesKeyedByFilenames() throws Exception
    {
        RecordStore rs = null;
        try
        {
            rs = RecordStore.openRecordStore(m_sRecordStoreName, true);
            Hashtable ht = buildRecordIndicesKeyedByFilenames_Inner(rs);
            rs.closeRecordStore();
            return ht;
        }
        catch (Exception e)
        {
            rs.closeRecordStore();
            throw e;
        }
    }

    private Hashtable buildRecordIndicesKeyedByFilenames_Inner(RecordStore rs)
    throws Exception
    {
        boolean keepUpdated = false;
        RecordEnumeration recEnum = rs.enumerateRecords(null,null,keepUpdated);

        Hashtable htable = new Hashtable( rs.getNumRecords() );
        while (recEnum.hasNextElement())
        {
            int recID = recEnum.nextRecordId();

            // 0.9.6 - switched to using RecordEnumeration.nextRecord() to get the
            // record data. This presumably should be faster.
            // It's necessary to step back first using previousRecordId().
            ////////recEnum.previousRecordId();
            ////////byte[] recData = recEnum.nextRecord();
            
            byte[] recData = rs.getRecord(recID);
            
            String sFilename = getFilenameFromRecordData(recData);
            if (htable.get(sFilename) != null)
            {
                // Have already found a record with this filename.
                // So change the duplicate record's filename in the list
                // to something guaranteed to be unique.
                sFilename += " duplicate " + System.currentTimeMillis();
            }

            htable.put( sFilename,new Integer(recID) );
        }

        return htable;
    }

    public byte[] getRecordBytes(int iRecordID)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);
        byte[] recData = rs.getRecord(iRecordID);
        rs.closeRecordStore();
        return recData;
    }

    private String getFilenameFromRecordData(byte[] recData)
    throws Exception
    {
        String sFilename;
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(recData);
            DataInputStream dis = new DataInputStream(bais);
            sFilename = dis.readUTF();
            bais.close();
        }
        catch (EOFException e)
        {
            // Trying to read standard UTF as if it were Java-modified UTF
            // will throw an EOFException.
            // So the data must be in standard UTF format.
            ByteArrayInputStream bais2 = new ByteArrayInputStream(recData);
            InputStreamReader isr = new InputStreamReader(bais2,"UTF-8");
            sFilename = readStandardUTF(isr,true);
            bais2.close();
        }

        return sFilename;
    }

    public String getTestContents(ListOfTestsEntry entry)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);
        byte[] recData = rs.getRecord(entry.getRecordStoreID().intValue());
        rs.closeRecordStore();

        String sFilenameFromRecord;
        String sTestContents;
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(recData);
            DataInputStream dis = new DataInputStream(bais);
            sFilenameFromRecord = dis.readUTF();
            sTestContents = dis.readUTF();
            bais.close();
        }
        catch (EOFException e)
        {
            ByteArrayInputStream bais2 = new ByteArrayInputStream(recData);
            InputStreamReader isr = new InputStreamReader(bais2,"UTF-8");
            sFilenameFromRecord = readStandardUTF(isr,true);
            sTestContents = readStandardUTF(isr,false);
            isr.close();
            bais2.close();
        }

        if (sFilenameFromRecord.equals( entry.getFilename() )==false)
        {
            String sErr =
                "Error! Filename retrieved from record is incorrect" +
                Constants.NEWLINE +
                "Filename supplied = " + entry.getFilename() + Constants.NEWLINE +
                "Filename in record = " + sFilenameFromRecord;
            throw new Exception(sErr);
        }

        return sTestContents;
    }

    public byte[] getRecordContentsMinusFilename(int iRecID)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);
        byte[] recData = rs.getRecord(iRecID);
        rs.closeRecordStore();

        ByteArrayInputStream bais = new ByteArrayInputStream(recData);
        DataInputStream dis = new DataInputStream(bais);
        String sFilenameFromRecord = dis.readUTF();
        
        byte[] followingData = new byte[recData.length];
        int b;
        int iBytesRead = 0;
        do
        {
        	b = dis.read();
        	if (b!=-1)
        	{
        		followingData[iBytesRead] = (byte)b;
        		iBytesRead++;
        	}
        } while (b!=-1);
        bais.close();
        	
    	// Copy the data into an array of the correct size.
    	byte[] finalData = new byte[iBytesRead];
    	System.arraycopy(followingData, 0, finalData, 0, iBytesRead);
        return finalData;
    }

    public void setTestContents(ListOfTestsEntry entry,String sTestContents)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);

        // Sanity check: look at the existing record to make sure the filename
        // is the same.

        byte[] existingBytes = rs.getRecord(entry.getRecordStoreID().intValue());
        String sFilenameFromRecord = getFilenameFromRecordData(existingBytes);
        if (sFilenameFromRecord.equals( entry.getFilename() )==false)
        {
            rs.closeRecordStore();
            throw new Exception("Error! Filename retrieved from record is incorrect");
        }

        // Now write the new test contents to the record.

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        flexiWriteUTF(baos,entry.getFilename());
        flexiWriteUTF(baos,sTestContents);

        byte[] newBytes = baos.toByteArray();
        rs.setRecord(entry.getRecordStoreID().intValue(),newBytes,0,newBytes.length);
        baos.close();

        rs.closeRecordStore();
    }

    public void copyFileFromJarToRecordStore(String sFilename)
    throws Exception
    {
        // Read the file from the JAR's resources.
        String sTestContents = StaticHelpers.readUnicodeFile("/" + sFilename);

        createFileInRecordStore(sFilename,sTestContents,true);
    }

    public int createFileInRecordStore(String sFilename,String sContents,
            boolean bDoUserInterfaceStuff) throws Exception
    {
        // Write the contents into a byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        flexiWriteUTF(baos, sFilename);
        flexiWriteUTF(baos, sContents);

        return createFileInRecordStore_Inner(sFilename,baos,
        										bDoUserInterfaceStuff);
    }

    public int createFileInRecordStore(String sFilename,byte[] contents,
            boolean bDoUserInterfaceStuff) throws Exception
    {
        // Write the contents into a byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        flexiWriteUTF(baos, sFilename);
        DataOutputStream outputStream = new DataOutputStream(baos);
        outputStream.write(contents);

        return createFileInRecordStore_Inner(sFilename,baos,
        										bDoUserInterfaceStuff);
    }

    private int createFileInRecordStore_Inner(String sFilename,
                                               ByteArrayOutputStream baosData,
                                               boolean bDoUserInterfaceStuff)
    throws Exception
    {
        byte[] byteArray = baosData.toByteArray();

        // Check whether the filename already exists.
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);
        Integer iExistingRSID = findRecordByFilename(rs,sFilename);
        if (iExistingRSID!=null)
        {
            rs.setRecord(iExistingRSID.intValue(),byteArray,0,byteArray.length);
            rs.closeRecordStore();
            return iExistingRSID.intValue();
        }
        else
        {
            // Add it to the record store
            int recordID = rs.addRecord(byteArray,0,byteArray.length);
            rs.closeRecordStore();

            // 0.9.6 - need to update the hashtable mapping filenames to rsids.
            m_RecordIndicesKeyedByFilenames.put(sFilename,new Integer(recordID));
            
            if (bDoUserInterfaceStuff)
            {
                // Display confirmation
                String sMsg = "Vocab file " + sFilename + " successfully " +
                              "created in recordstore with " +
                              "record id = " + recordID;
                Alert alert = new Alert(null, sMsg, null, null);

                OccleveMobileMidlet.getInstance().repopulateFileChooser();
                OccleveMobileMidlet.getInstance().displayAlertThenFileChooser(alert);
            }
            
            return recordID;
        }
    }

    /**Returns the record ID of the specified filename, or null
    if it isn't found.*/
    public Integer findRecordByFilename(String sFilenameToFind)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);
        System.out.println("Opened recordstore ok");
        System.out.println("m_bUseJavaUTF = " + m_bUseJavaUTF);
        
        Integer rsid = findRecordByFilename(rs,sFilenameToFind);
        rs.closeRecordStore();
        return rsid;
    }

    /**Returns the record ID of the specified filename, or null
    if it isn't found.*/
    public Integer findRecordByFilename(RecordStore rs,String sFindMe)
    throws Exception
    {
    	// 0.9.6 - now that a Hashtable mapping filenames to record indices
    	// is maintained for the lifecycle of the midlet, can just do this.
    	return (Integer)m_RecordIndicesKeyedByFilenames.get(sFindMe);
    	
    	/*
    	=======================DISABLED 0.9.6================
        boolean keepUpdated = false;
        RecordEnumeration recEnum = rs.enumerateRecords(null,null,keepUpdated);
        System.out.println("Got recEnum ok");
        while (recEnum.hasNextElement())
        {
            int recID = recEnum.nextRecordId();
            byte[] recData = rs.getRecord(recID);
            String sFilename = getFilenameFromRecordData(recData);
            // System.out.println("Comparing " + sFilename + " to " + sFindMe);
            
            if (sFilename.equals(sFindMe))
            {
                return new Integer(recID);
            }
        }
        return null;
        */
    }

    /*
    public void displayRecordStoreCapacity() throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore("testCapacity",true);
        int capacity = rs.getSizeAvailable();
        rs.closeRecordStore();

        Alert alert = new Alert(null,"RecordStore capacity = " + capacity,null,null);
        OccleveMobileMidlet.getInstance().displayAlert(alert,null);
    }
    */

    public void createEmptyTest(String sFilename)
    throws Exception
    {
        // Now write it into a byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        flexiWriteUTF(baos,sFilename);
        byte[] byteArray = baos.toByteArray();

        // Add it to the record store
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);
        int recordID = rs.addRecord(byteArray,0,byteArray.length);
        rs.closeRecordStore();

        // 0.9.6 - need to update the hashtable mapping filenames to rsids.
        m_RecordIndicesKeyedByFilenames.put(sFilename,new Integer(recordID));

        // Display confirmation
        String sMsg = "New test " + sFilename + " created in recordstore with " +
                      " record id = " + recordID;
        Alert alert = new Alert(null,sMsg,null,null);
        OccleveMobileMidlet.getInstance().displayAlertThenFileChooser(alert);
    }

    public void deleteTest(int iRecordID,String sFilename)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);

        // Sanity check: check that the filename in the record matches
        // the one supplied to this function.

        byte[] existingBytes = rs.getRecord(iRecordID);
        String sFilenameFromRecord = getFilenameFromRecordData(existingBytes);
        if (sFilenameFromRecord.equals( sFilename )==false)
        {
            rs.closeRecordStore();
            throw new Exception("Error! Filename retrieved from record is incorrect");
        }

        // Now delete that test

        rs.deleteRecord(iRecordID);
        rs.closeRecordStore();
        
        // 0.9.6 - need to update the hashtable mapping filenames to rsids.
        m_RecordIndicesKeyedByFilenames.remove(sFilename);
    }

    /**Appends the specified string to a test.*/
    public void appendToTest(ListOfTestsEntry entry,String sAppendThis)
    throws Exception
    {
        String contents = getTestContents(entry);
        contents += Constants.NEWLINE + sAppendThis;
        setTestContents(entry,contents);
    }

    /**Delete all files whose filenames begin with "XML".
    A convenience function for the switchover to XML.*/
    public void deleteAllXmlPrefixedFiles()
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);

        boolean keepUpdated = false;
        RecordEnumeration recEnum = rs.enumerateRecords(null,null,keepUpdated);

        while (recEnum.hasNextElement())
        {
            int recID = recEnum.nextRecordId();
            byte[] recData = rs.getRecord(recID);
            String sFilename = getFilenameFromRecordData(recData);
            if (sFilename.startsWith("XML"))
            {
                System.out.println("Deleting " + sFilename);
                rs.deleteRecord(recID);
                
                // 0.9.6 - need to update the hashtable mapping filenames to rsids.
                m_RecordIndicesKeyedByFilenames.remove(sFilename);
            }

        }

        rs.closeRecordStore();
    }

    /**Writes a string to the specified ByteArrayOutputStream in either
    Java-modified UTF or standard UTF, depending on m_bUseJavaUTF.*/
    private void flexiWriteUTF(ByteArrayOutputStream baos,String sWriteMe)
    throws Exception
    {
        if (m_bUseJavaUTF)
        {
            DataOutputStream outputStream = new DataOutputStream(baos);
            outputStream.writeUTF(sWriteMe);
        }
        else
        {
            OutputStreamWriter writer = new OutputStreamWriter(baos,"UTF-8");
            writer.write(sWriteMe + Constants.NEWLINE);
        }
    }

    /**Reads a string from the ByteArrayInputStream in *standard* UTF format.
    Either stops at the first newline, or at the end of the data, depending
    on the flag supplied.*/
    private String readStandardUTF(InputStreamReader isr,boolean bStopAtNewline)
    throws Exception
    {
        int FIRST_NEWLINE_CHAR;
        if (bStopAtNewline)
           FIRST_NEWLINE_CHAR = Constants.NEWLINE.charAt(0);
        else
           FIRST_NEWLINE_CHAR = -1;

        StringBuffer buffer = new StringBuffer();
        int ch;
        do
        {
            ch = isr.read();
            if ((ch!=-1) && (ch!=FIRST_NEWLINE_CHAR)) buffer.append((char) ch);
        } while ((ch!=-1) && (ch!=FIRST_NEWLINE_CHAR));

        return buffer.toString();
    }

    /**Will need to be called from a separate thread from the UI
    to prevent blocking.
    Will only work on phones that support the FileConnection API.*/
    public void saveAllTestsToFilesystem() throws Exception
    {
        System.out.println("Entering saveAllTestsToFilesystem");
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);

        boolean keepUpdated = false;
        RecordEnumeration recEnum = rs.enumerateRecords(null,null,keepUpdated);

        while (recEnum.hasNextElement())
        {
            int recID = recEnum.nextRecordId();
            byte[] recData = rs.getRecord(recID);
            String sFilenameInRecord = getFilenameFromRecordData(recData);
            ListOfTestsEntry entry =
            	new ListOfTestsEntry(sFilenameInRecord,new Integer(recID),null);
            String sContents = getTestContents(entry);

            String sFilenameOnFS = "file:///root1/" + sFilenameInRecord;
            FileConnection filecon =
                (FileConnection)Connector.open(sFilenameOnFS);

            // Always check whether the file or directory exists.
            // Create the file if it doesn't exist.
            if (!filecon.exists()) filecon.create();

            OutputStream os = filecon.openOutputStream();
            OutputStreamWriter writer =
                new OutputStreamWriter(os,Config.ENCODING);
            writer.write(sContents);
            writer.flush();
            writer.close();
            os.close();
            filecon.close();

            System.out.println("Saved " + sFilenameInRecord + " OK");
        }

        rs.closeRecordStore();
    }
    
    /**0.9.6*/
    public int getRecordCount() throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(m_sRecordStoreName, true);
        int iCount = rs.getNumRecords();
        rs.closeRecordStore();
        return iCount;    	
    }
}

