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

package org.occleve.mobileclient.recordstore;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

import org.occleve.mobileclient.*;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.Connector;

/**Format of a record is the filename (as a UTF-encoded string), followed
by the UTF-encoded file data.*/
public class VocabRecordStoreManager
{
    private final static String RECORDSTORE_NAME = "OccleveMobileClientTests";

    private boolean m_bUseJavaUTF = true;

    public void useJavaUTF() {m_bUseJavaUTF = true;}
    public void useStandardUTF() {m_bUseJavaUTF = false;}

    public VocabRecordStoreManager()
    {
    }

    /**Returns a Hashtable with the record indices keyed by the filenames.*/
    public Hashtable getRecordIndicesKeyedByFilenames() throws Exception
    {
        RecordStore rs = null;
        try
        {
            rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);
            Hashtable ht = getRecordIndicesKeyedByFilenames_Inner(rs);
            rs.closeRecordStore();
            return ht;
        }
        catch (Exception e)
        {
            rs.closeRecordStore();
            throw e;
        }
    }

    private Hashtable getRecordIndicesKeyedByFilenames_Inner(RecordStore rs)
    throws Exception
    {
        boolean keepUpdated = false;
        RecordEnumeration recEnum = rs.enumerateRecords(null,null,keepUpdated);

        Hashtable htable = new Hashtable( rs.getNumRecords() );
        while (recEnum.hasNextElement())
        {
            int recID = recEnum.nextRecordId();
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
        RecordStore rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);
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

    public String getTestContents(int iRecordID,String sFilename)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);
        byte[] recData = rs.getRecord(iRecordID);
        rs.closeRecordStore();

        String sFilenameFromRecord;
        String sTestContents;
//boolean bStdUTF = false;
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
//bStdUTF = true;
        }

        if (sFilenameFromRecord.equals( sFilename )==false)
        {
            String sErr =
                "Error! Filename retrieved from record is incorrect" +
                Constants.NEWLINE +
                "Filename supplied = " + sFilename + Constants.NEWLINE +
                "Filename in record = " + sFilenameFromRecord;
            throw new Exception(sErr);
        }

        /*
         String sDebug = "Filename retrieved from record = " + sFilenameFromRecord +
         "##### Beginning of file data = " + sTestContents.substring(0,10) +
                        "##### Used standard UTF = " + bStdUTF;
         throw new Exception(sDebug);
         */

        return sTestContents;
    }

    public void setTestContents(int iRecordID,String sFilename,String sTestContents)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);

        // Sanity check: look at the existing record to make sure the filename
        // is the same.

        byte[] existingBytes = rs.getRecord(iRecordID);
        String sFilenameFromRecord = getFilenameFromRecordData(existingBytes);
        if (sFilenameFromRecord.equals( sFilename )==false)
        {
            rs.closeRecordStore();
            throw new Exception("Error! Filename retrieved from record is incorrect");
        }

        // Now write the new test contents to the record.

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        flexiWriteUTF(baos,sFilename);
        flexiWriteUTF(baos,sTestContents);

        byte[] newBytes = baos.toByteArray();
        rs.setRecord(iRecordID,newBytes,0,newBytes.length);
        baos.close();

        rs.closeRecordStore();
    }

    public void copyFileToRecordStore(String sFilename)
    throws Exception
    {
        // Read the file from the JAR's resources.
        String sTestContents = StaticHelpers.readUnicodeFile("/" + sFilename);

        createFileInRecordStore(sFilename,sTestContents,true);
    }

    public void createFileInRecordStore(String sFilename,String sContents,
            boolean bDoUserInterfaceStuff) throws Exception
    {
        // Write the contents into a byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        flexiWriteUTF(baos, sFilename);
        flexiWriteUTF(baos, sContents);

        createFileInRecordStore_Inner(sFilename,baos,bDoUserInterfaceStuff);
    }

    public void createFileInRecordStore(String sFilename,byte[] contents,
            boolean bDoUserInterfaceStuff) throws Exception
    {
        // Write the contents into a byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        flexiWriteUTF(baos, sFilename);
        DataOutputStream outputStream = new DataOutputStream(baos);
        outputStream.write(contents);

        createFileInRecordStore_Inner(sFilename,baos,bDoUserInterfaceStuff);
    }

    private void createFileInRecordStore_Inner(String sFilename,
                                               ByteArrayOutputStream baosData,
                                               boolean bDoUserInterfaceStuff)
    throws Exception
    {
        byte[] byteArray = baosData.toByteArray();

        // Check whether the filename already exists.
        RecordStore rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);
        Integer iExistingRSID = findRecordByFilename(rs,sFilename);
        if (iExistingRSID!=null)
        {
            rs.setRecord(iExistingRSID.intValue(),byteArray,0,byteArray.length);
            rs.closeRecordStore();
        }
        else
        {
            // Add it to the record store
            int recordID = rs.addRecord(byteArray,0,byteArray.length);
            rs.closeRecordStore();

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
        }
    }

    /**Returns the record ID of the specified filename, or null
    if it isn't found.*/
    public Integer findRecordByFilename(String sFilenameToFind)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);
System.out.println("Opened recordstore ok");
        Integer rsid = findRecordByFilename(rs,sFilenameToFind);
        rs.closeRecordStore();
        return rsid;
    }

    /**Returns the record ID of the specified filename, or null
    if it isn't found.*/
    public Integer findRecordByFilename(RecordStore rs,String sFindMe)
    throws Exception
    {
        boolean keepUpdated = false;
        RecordEnumeration recEnum = rs.enumerateRecords(null,null,keepUpdated);
System.out.println("Got recEnum ok");

        while (recEnum.hasNextElement())
        {
            int recID = recEnum.nextRecordId();
            byte[] recData = rs.getRecord(recID);
            String sFilename = getFilenameFromRecordData(recData);
System.out.println("Comparing " + sFilename + " to " + sFindMe);
            if (sFilename.equals(sFindMe))
            {
                return new Integer(recID);
            }
        }

        return null;
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
        RecordStore rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);
        int recordID = rs.addRecord(byteArray,0,byteArray.length);
        rs.closeRecordStore();

        // Display confirmation
        String sMsg = "New test " + sFilename + " created in recordstore with " +
                      " record id = " + recordID;
        Alert alert = new Alert(null,sMsg,null,null);
        OccleveMobileMidlet.getInstance().displayAlertThenFileChooser(alert);
    }

    public void deleteTest(int iRecordID,String sFilename)
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);

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
    }

    /**Appends the specified string to a test.*/
    public void appendToTest(int iRecordID,String sFilename,String sAppendThis)
    throws Exception
    {
        String contents = getTestContents(iRecordID,sFilename);
        contents += Constants.NEWLINE + sAppendThis;
        setTestContents(iRecordID,sFilename,contents);
    }

    /**Delete all files whose filenames begin with "XML".
    A convenience function for the switchover to XML.*/
    public void deleteAllXmlPrefixedFiles()
    throws Exception
    {
        RecordStore rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);

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
        RecordStore rs = RecordStore.openRecordStore(RECORDSTORE_NAME, true);

        boolean keepUpdated = false;
        RecordEnumeration recEnum = rs.enumerateRecords(null,null,keepUpdated);

        while (recEnum.hasNextElement())
        {
            int recID = recEnum.nextRecordId();
            byte[] recData = rs.getRecord(recID);
            String sFilenameInRecord = getFilenameFromRecordData(recData);
            String sContents = getTestContents(recID,sFilenameInRecord);

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
}

