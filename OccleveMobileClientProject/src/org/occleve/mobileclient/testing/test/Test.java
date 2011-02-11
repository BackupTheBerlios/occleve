/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007-11  Joe Gittings

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
@version 0.9.10
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

    public Test(ListOfTestsEntry entry) throws Exception
    {
    	// Defaults to loading the test.
    	this(entry,true);
    }

    public Test(ListOfTestsEntry entry, boolean bLoad) throws Exception
    {
    	m_ListOfTestsEntry = entry;

    	if (bLoad)
    	{
    		load(entry,null);
    	}
    	else
    	{
    		// Just construct an empty test.
            m_QuestionAskedFlags = new Vector();
            m_QAs = new Vector();	
    	}
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
                
        m_sFirsteseISOCode =
        	sTestFilename.toUpperCase().substring(0,iFirstHyphenIndex);
        m_sSecondeseISOCode =
        	sTestFilename.toUpperCase().substring(iFirstHyphenIndex+1,
				                                   iSecondHyphenIndex);

		/* if (progressAlert!=null) progressAlert.setString(
				progressAlert.getString() + "\r\n\r\n" +
				"Free memory before parsing = \r\n\r\n" + Runtime.getRuntime().freeMemory() );
        Xparse parser = new Xparse();
        Node root = parser.parse(sTestSource);

        if (progressAlert!=null) progressAlert.setString(
				progressAlert.getString() + "\r\n\r\n" +
				"Free memory after parsing = \r\n\r\n" + Runtime.getRuntime().freeMemory());
		*/

        Xparse parser = new Xparse();
        String sLowerTestSource = sTestSource.toLowerCase();
        int iTestTagIndex = sLowerTestSource.indexOf(XML.lowerOpenTag(XML.TEST));
        int iPreTagIndex = sLowerTestSource.indexOf(XML.lowerOpenTag(XML.PRE));

        if (iPreTagIndex==-1 && iTestTagIndex==-1)
    	{        	
        	throw new Exception("Couldn't find Test node or pre node");
    	}

    	if (iTestTagIndex==-1)
    	{        	
        	throw new Exception("Couldn't find Test node");
    	}

		int lqaIndex = sLowerTestSource.indexOf("<lqa>");
		int mqaIndex = sLowerTestSource.indexOf("<mqa>");
        if (lqaIndex==-1 && mqaIndex==-1)
        {
        	throw new Exception("Couldn't find any QA nodes in the test");        	
        }

		String qaType = (lqaIndex!=-1) ? "lqa":"mqa";
    	String openQATag = XML.lowerOpenTag(qaType);
    	String closeQATag = XML.lowerCloseTag(qaType);

		int startIndex = iTestTagIndex;
		int openQAIndex;
    	do {
    		openQAIndex = sLowerTestSource.indexOf(openQATag,startIndex);
    		if (openQAIndex!=-1)
    		{
        		int closeQAIndex = sLowerTestSource.indexOf(closeQATag,openQAIndex);
        		if (closeQAIndex!=-1) 
        		{
        			String qaText =
        				sTestSource.substring(openQAIndex, closeQAIndex + closeQATag.length());
        	        Node root = parser.parse(qaText);
        	        Node qaNode = root.findFirst(qaType.toUpperCase());

        	        xmlLoadQuestions_OneQA(qaType,qaText,qaNode,sOriginalProgressPrompt,progressAlert);        	        
        	        
        	        root = null;
        	        qaNode = null;
        		}
        		startIndex = closeQAIndex; 
    		}
    	} while (openQAIndex!=-1);
    	
    	Runtime.getRuntime().gc();

        if (m_QAs.size()==0)
        {
            throw new Exception("Test appears to contain zero questions");
        }
    }

    private void xmlLoadQuestions_OneQA(String qaType,String qaText,Node qaNode,
		String sOriginalProgressPrompt,Alert progressAlert) throws Exception
    {
    	// System.out.println("qaText=" + qaText);
    	
    	QA qa;
    	if (qaType.equals("lqa"))
    		qa = new LanguageQA(qaNode,m_sFirsteseISOCode,m_sSecondeseISOCode);
    	else
    		qa = new SageQA(qaNode);
    	
        m_QAs.addElement(qa);

        if ((m_QAs.size()%10) == 0)
		{
			System.out.println("Free memory / bytes = " +
				Runtime.getRuntime().freeMemory());
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
    		VocabRecordStoreManager mgr =
    			OccleveMobileMidlet.getInstance().getQuizRecordStoreManager();
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
    	
    	Test restricted = new Test(m_ListOfTestsEntry,false);
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

