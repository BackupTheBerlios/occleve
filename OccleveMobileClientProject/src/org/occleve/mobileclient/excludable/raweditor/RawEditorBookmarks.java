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
@version 0.9.4
*/

package org.occleve.mobileclient.excludable.raweditor;

import java.io.*;
import javax.microedition.rms.*;
import org.occleve.mobileclient.testing.*;

/**Keeps track of bookmarks for the RawEditor.*/
public class RawEditorBookmarks
{
    public static String BOOKMARK_RECORDSTORE_NAME = "OccleveMobileClientEditorBookmarks";

    /**Returns a bookmark for the specified filename.*/
    public static Integer getBookmark(String sFilenameToFind) throws Exception
    {
        RecordStore rs = null;
        try
        {
            rs = RecordStore.openRecordStore(BOOKMARK_RECORDSTORE_NAME, true);
            Integer iChunkIndex = getBookmark_Inner(sFilenameToFind,rs);
            rs.closeRecordStore();

            return iChunkIndex;
        }
        catch (Exception e)
        {
            rs.closeRecordStore();
            throw e;
        }
    }

    /**Subfunction for code clarity.*/
    private static Integer getBookmark_Inner(String sFilenameToFind,RecordStore rs)
    throws Exception
    {
        RecordEnumeration recEnum = rs.enumerateRecords(null, null, false);
        while (recEnum.hasNextElement())
        {
            int recID = recEnum.nextRecordId();
            byte[] recData = rs.getRecord(recID);

            ByteArrayInputStream bais = new ByteArrayInputStream(recData);
            DataInputStream dis = new DataInputStream(bais);
            String sFilename = dis.readUTF();

            if (sFilename.equals(sFilenameToFind))
            {
                int iChunkBookmark = dis.readInt();
                return new Integer(iChunkBookmark);
            }
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////

    /**Sets a bookmark for the specified filename.*/
    public static void setBookmark(ListOfTestsEntry entry,int iChunkIndex)
    throws Exception
    {
        RecordStore rs = null;
        try
        {
            rs = RecordStore.openRecordStore(BOOKMARK_RECORDSTORE_NAME, true);
            setBookmark_Inner(entry,iChunkIndex,rs);
            rs.closeRecordStore();
        }
        catch (Exception e)
        {
            rs.closeRecordStore();
            throw e;
        }
    }

    /**Subfunction for code clarity.*/
    private static void setBookmark_Inner(ListOfTestsEntry entry,int iChunkIndex,
                                             RecordStore rs)
    throws Exception
    {
        // Get the data ready for writing.

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(baos);
        outputStream.writeUTF( entry.getFilename() );
        outputStream.writeInt(iChunkIndex);
        byte[] bytesToWrite = baos.toByteArray();
        outputStream.close();
        baos.close();

        // Now either write the data to the existing record, or if it
        // doesn't exist, create a new record.

        RecordEnumeration recEnum = rs.enumerateRecords(null, null, false);
        while (recEnum.hasNextElement())
        {
            int recID = recEnum.nextRecordId();
            byte[] recData = rs.getRecord(recID);

            ByteArrayInputStream bais = new ByteArrayInputStream(recData);
            DataInputStream dis = new DataInputStream(bais);
            String sFilename2 = dis.readUTF();

            if (sFilename2.equals( entry.getFilename() ))
            {
                rs.setRecord(recID,bytesToWrite,0,bytesToWrite.length);
                return;
            }
        }

        rs.addRecord(bytesToWrite,0,bytesToWrite.length);
    }

}
