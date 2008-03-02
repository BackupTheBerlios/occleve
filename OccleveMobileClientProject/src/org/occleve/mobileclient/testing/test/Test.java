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
@version 0.9.6
*/

package org.occleve.mobileclient.testing.test;

import com.exploringxml.xml.*;
import java.util.*;
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.qa.language.*;
import org.occleve.mobileclient.qa.wikiversity.*;
import org.occleve.mobileclient.recordstore.*;
import org.occleve.mobileclient.testing.ListOfTestsEntry;

public class Test
{
	protected ListOfTestsEntry m_ListOfTestsEntry;
	public ListOfTestsEntry getEntry() {return m_ListOfTestsEntry;}
	
    public Integer getRecordStoreID() {return m_ListOfTestsEntry.getRecordStoreID();}
    public String getFilename() {return m_ListOfTestsEntry.getFilename();}

    /**ISO code of first language in language pair.*/
    protected String m_sFirsteseISOCode;
    public String getFirsteseISOCode() {return m_sFirsteseISOCode;}

    /**ISO code of second language in language pair.*/
    protected String m_sSecondeseISOCode;
    public String getSecondeseISOCode() {return m_sSecondeseISOCode;}

    protected Vector m_QuestionAskedFlags;

    protected Vector m_QAs;
    public QA getQA(int iIndex) {return (QA)m_QAs.elementAt(iIndex);}
    public int getQACount() {return m_QAs.size();}
    public void addQA(QA qa) {m_QAs.addElement(qa);}

    /**Constructs an empty test.*/
    public Test() throws Exception
    {
        m_QuestionAskedFlags = new Vector();
        m_QAs = new Vector();
    }

    public Test(ListOfTestsEntry entry) throws Exception
    {
        load(entry,null);
    }

    public Test(ListOfTestsEntry entry,Alert progressAlert) throws Exception
    {
        load(entry,progressAlert);
    }

    public void load(ListOfTestsEntry entry,Alert progressAlert) throws Exception
    {
        load_Inner(entry,progressAlert);
        System.out.println("Loaded " + m_QAs.size() + " QAs");
    }

    private void load_Inner(ListOfTestsEntry entry,Alert progressAlert)
    throws Exception
    {
    	m_ListOfTestsEntry = entry;

        String sTestSource = readTestSource(entry);

        m_QuestionAskedFlags = new Vector();
        m_QAs = new Vector();

        //String sLowerFilename = sTestFilename.toLowerCase();
        //if (sLowerFilename.endsWith(".xml"))

        if (sTestSource.indexOf(XML.TEST) != -1)
        {
            xmlLoadQuestions(entry.getFilename(),sTestSource,progressAlert);
        }
        else if (sTestSource.indexOf(Config.WIKIVERSITY_QUIZ_TAG_STUB) != -1)
        {
            loadWikiversityQuiz(entry.getFilename(),sTestSource);
        }
        else
        {
        	throw new Exception("Unknown test format");
        }
    }

    /**Load an XML test.*/
    public void xmlLoadQuestions(String sTestFilename,String sTestSource,Alert progressAlert)
    throws Exception
    {
    	String sOriginalProgressPrompt = null;
    	if (progressAlert!=null)
    	{
    		sOriginalProgressPrompt = progressAlert.getString();
    	}
    	
        int iFirstHyphenIndex = sTestFilename.indexOf('-');
        int iSecondHyphenIndex = sTestFilename.indexOf('-',iFirstHyphenIndex+1);

        m_sFirsteseISOCode = sTestFilename.substring(0,iFirstHyphenIndex);
        m_sSecondeseISOCode = sTestFilename.substring(iFirstHyphenIndex+1,
                                                           iSecondHyphenIndex);

        Xparse parser = new Xparse();
        Node root = parser.parse(sTestSource);
        int[] first = {1};
        Node test = root.find(XML.TEST,first);
        if (test==null)
    	{
        	Node pre = root.find(XML.PRE,first);
            if (pre==null)
        	{        	
            	throw new Exception("Couldn't find Test node or pre node");
        	}

        	test = pre.find(XML.TEST,first);
        	if (test==null)
        	{        	
            	throw new Exception("Couldn't find Test node");
        	}
    	}

        Node[] allQANodes = test.findAllChildElements(test,XML.QA);
        if (allQANodes.length==0)
        {
        	throw new Exception("Couldn't find any LQA nodes in the test");        	
        }
                
        for (int i=0; i<allQANodes.length; i++)
        {
        	LanguageQA lqa = new LanguageQA(allQANodes[i],m_sFirsteseISOCode,
                                                m_sSecondeseISOCode);
            m_QAs.addElement(lqa);

            if ((m_QAs.size()%10) == 0)
    		{
            	System.out.println("Forcing garbage collection...");
            	Runtime.getRuntime().gc();
    		}
            
            if (((m_QAs.size()%10) == 0) && (progressAlert!=null))
            {
            	String sMsg = sOriginalProgressPrompt +
            					" - loaded " + m_QAs.size() + " questions. " +
            					"Free memory in bytes = " +
            					Runtime.getRuntime().freeMemory();
            	progressAlert.setString(sMsg);
            }
    	}
    
        if (m_QAs.size()==0)
        {
            throw new Exception("Test appears to contain zero questions");
        }
    }

    /**Load a wikiversity quiz.*/
    public void loadWikiversityQuiz(String sTestFilename,String sTestSource)
    throws Exception
    {
        Vector vFileContents = StaticHelpers.stringToVector(sTestSource);
        VectorReader in = new VectorReader(vFileContents);
        String sPossibleQuestion;
        do
        {
            sPossibleQuestion = in.readLine();
            if (sPossibleQuestion != null)
            {
                if (sPossibleQuestion.startsWith("{"))
                {
                    loadWikiversityQuiz_DoOneQA(in, sPossibleQuestion);
                }
            }
        } while (sPossibleQuestion != null);
    }

    private void loadWikiversityQuiz_DoOneQA(VectorReader in, String sQuestionLine)
    throws Exception
    {
        String sQuestion = sQuestionLine.substring(1);

        String sTypeLine = in.readLine();
        System.out.println("sTypeLine = " + sTypeLine);

        //int iFirstQuoteIndex = sTypeLine.indexOf("\"");
        //int iSecondQuoteIndex = sTypeLine.indexOf("\"",iFirstQuoteIndex+1);
        //String sType = sTypeLine.substring(iFirstQuoteIndex+1,iSecondQuoteIndex);

        final String MULTIPLE_CHOICE_TYPE = "type=\"()\"";

        if (sTypeLine.indexOf(MULTIPLE_CHOICE_TYPE)!=-1)
        {
            QA qa = new MultipleChoiceWikiversityQA(sQuestion,in);
            m_QAs.addElement(qa);
        }
        else
        {
            // Unknown or currently unsupported question type.
            // Skip past it.
            System.out.println("Unknown question type: skipping the rest...");
            do {} while (in.readLine().trim().length() > 0);
        }
    }

    /**If the file exists in the RecordStore, read it from there, otherwise
     read it from the JAR.
     0.9.4: If the filename begins with the "file://" prefix, read it from the phone's
     filesystem.*/
    public static String readTestSource(ListOfTestsEntry entry)
    throws Exception
    {
        String sTestSource;
        if (entry.getRecordStoreID() == null)
        {
        	if (entry.getLocalFilesystemURL() != null)
        		sTestSource = StaticHelpers.readUnicodeFile(entry.getLocalFilesystemURL());
        	else
        		sTestSource = StaticHelpers.readUnicodeFile("/" + entry.getFilename());
        }
        else
        {
            VocabRecordStoreManager mgr = new VocabRecordStoreManager();
            sTestSource = mgr.getTestContents(entry);
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

    /**Returns a copy of this Test that only contain QAs in which the question
    and answer fields specified by the QADirection are non-null and non-blank.*/
    public Test restrictToQADirectionTypes(QADirection qadir)
    throws Exception
    {
    	// Set the QADirection on all the QAs or this will fail.
    	for (int i=0; i<getQACount(); i++)
    	{
    		getQA(i).initialize(qadir);
    	}
    	
    	Test restricted = new Test();
    	for (int i = 0; i < m_QAs.size(); i++)
        {
            QA qa = (QA) m_QAs.elementAt(i);
            
            if (qa.containsQADirectionFields(qadir))
            {
            	restricted.addQA(qa);
            }
        }
    	
    	return restricted;
    }
}

