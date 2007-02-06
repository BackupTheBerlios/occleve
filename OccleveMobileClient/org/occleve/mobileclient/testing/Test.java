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

import com.exploringxml.xml.*;
import java.util.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.qa.language.*;
import org.occleve.mobileclient.recordstore.*;

public class Test
{
    /**If non-null, the ID of the RecordStore entry that stores this test.*/
    protected Integer m_iRecordStoreID;
    public Integer getRecordStoreID() {return m_iRecordStoreID;}

    /**This test's filename.*/
    protected String m_sFilename;
    public String getFilename() {return m_sFilename;}

    protected Vector m_QuestionAskedFlags;

    protected Vector m_QAs;
    public QA getQA(int iIndex) {return (QA)m_QAs.elementAt(iIndex);}
    public int getQACount() {return m_QAs.size();}

    public Test(String sTestFilename,Integer iRecordStoreID) throws Exception
    {
        load(sTestFilename,iRecordStoreID);
    }

    public void load(String sTestFilename,Integer iRecordStoreID) throws Exception
    {
        load_Inner(sTestFilename,iRecordStoreID);
        System.out.println("Loaded " + m_QAs.size() + " QAs");
    }

    private void load_Inner(String sTestFilename,Integer iRecordStoreID)
    throws Exception
    {
        m_sFilename = sTestFilename;
        m_iRecordStoreID = iRecordStoreID;

        String sTestSource = readTestSource(sTestFilename,iRecordStoreID);

        m_QuestionAskedFlags = new Vector();
        m_QAs = new Vector();

        String sLowerFilename = sTestFilename.toLowerCase();
        if (sLowerFilename.endsWith(".xml"))
        {
            xmlLoadQuestions(sTestSource);
        }
        else
        {
            Vector vFileContents = StaticHelpers.stringToVector(sTestSource);
            Test_OldFormatLoader.oldFormatLoadQuestions(this,vFileContents);
        }
    }

    /**Load an XML test.*/
    public void xmlLoadQuestions(String sTestSource)
    throws Exception
    {
        Xparse parser = new Xparse();
        Node root = parser.parse(sTestSource);
        int[] first = {1};
        Node test = root.find(XML.TEST,first);
        if (test==null) throw new Exception("Couldn't find Test node");

        Node qaNode = null;
        do
        {
            // Because Node.find uses indices starting from one.
            int[] next = {m_QAs.size()+1};

            qaNode = test.find(XML.QA,next);

            if (qaNode!=null)
            {
                LanguageQA cqa = new ChineseQA(qaNode);
                m_QAs.addElement(cqa);
            }
        } while (qaNode!=null);

        if (m_QAs.size()==0)
        {
            throw new Exception("Test appears to contain zero questions");
        }
    }

    /**If the file exists in the RecordStore, read it from there, otherwise
     read it from the JAR.*/
    public static String readTestSource(String sFilename,
                                        Integer iRecordStoreID) throws
            Exception
    {
        String sTestSource;
        if (iRecordStoreID == null)
        {
            sTestSource = StaticHelpers.readUnicodeFile("/" + sFilename);
        }
        else
        {
            int id = iRecordStoreID.intValue();
            VocabRecordStoreManager mgr = new VocabRecordStoreManager();
            sTestSource = mgr.getTestContents(id, sFilename);
        }

        return sTestSource;
    }

    public String toXML()
    {
        StringBuffer sb = new StringBuffer();

        XML.appendStartTag(sb, XML.TEST);
        sb.append(Constants.NEWLINE);

        for (int i = 0; i < m_QAs.size(); i++)
        {
            QA qa = (QA) m_QAs.elementAt(i);
            sb.append(qa.toXML());
            sb.append(Constants.NEWLINE);

            // Put a blank line between QAs.
            sb.append(Constants.NEWLINE);
        }

        XML.appendEndTag(sb, XML.TEST);
        return sb.toString();
    }
}

