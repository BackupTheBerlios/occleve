/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2010  Joe Gittings

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

package org.occleve.mobileclient.qa;

import java.util.*;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.exploringxml.xml.*;

import org.occleve.mobileclient.OccleveMobileMidlet;
import org.occleve.mobileclient.util.*;
import org.occleve.mobileclient.serverbrowser.*;

/**QA which can be used to generate many variants on a given type of
mathematical question.
The solutions are calculated by a Sage server.*/
public class SageQA extends QA implements Runnable
{
	/**localhost while still under dev.*/
	public static String SAGE_SERVER_URL = "http://localhost:8000";
	
	protected String m_Desc;
	protected String m_Problem;
	protected Vector m_Vars = new Vector();
	protected Vector m_Solutions = new Vector();
	
	protected Vector m_EvaluatedSolutions;

	protected class Var
	{
		private String m_Name;
		private double m_Min;
		private double m_Max;
		private double m_Value;
		
		public Var(Node varNode)
		{
        	boolean isInt = (varNode.name.equals("RandomInt"));
        	String m_Name = (String)varNode.attributes.get("Name");

        	String min = (String)varNode.attributes.get("Min");
        	double m_Min = Double.parseDouble(min);
        	
        	String max = (String)varNode.attributes.get("Max");            	
        	double m_Max = Double.parseDouble(max);

        	trace("Parsed var " + m_Name + " min=" + m_Min + " max=" + m_Max);
						
			double diff = m_Max - m_Min;
			Random gen = new Random();
			if (isInt)
			{
				m_Value = m_Min + (gen.nextDouble() * diff);
				m_Value = (double)( (int)m_Value );
			}
			else
				m_Value = m_Min + (gen.nextDouble() * diff);
		}
		
		public String toString()
		{
        	return m_Name + "=" + m_Value;			
		}
	}
	
    /**Load a MathQA from an XML file.*/
    public SageQA(Node qaNode) throws Exception
    {
        trace("Entering SageQA constructor");
        trace("With qaNode.name = " + qaNode.name);

    	Node descNode = qaNode.findFirst("Desc");
    	trace("descNode=" + descNode);
    	trace("descNode chars=" + descNode.getCharacters());
        m_Desc = descNode.getCharacters();

        Node problemNode = qaNode.findFirst("Problem");
    	trace("problemNode chars=" + problemNode.getCharacters());
        m_Problem = problemNode.getCharacters();

        Vector vChildren = qaNode.getContents();
        trace("vChildren=" + vChildren);
        for (int i=0; i<vChildren.size(); i++)
        {
            Node child = (Node)vChildren.elementAt(i);
            if (child.name!=null)
            {
	            if (child.name.equals("RandomVar") ||
            		child.name.equals("RandomInt"))
	            {
	            	trace("Parsing RandomVar node: " + child);
	            	Var var = new Var(child);
	            	m_Vars.addElement(var);
	            }
	            else if (child.name.equals("Solution"))
	            {
	            	m_Solutions.addElement(child.getCharacters());
	            }
            }
        }
    }

    public Vector getEntireContentsAsItems()
    {
    	return new Vector();
    }

    public boolean containsString(String s)
    {
    	return false;
    }

    public String getEntireContentsAsString() {return null;}
    
    public Vector getQuestion()
    {
    	String question =
    		m_Desc + "\n\n" + m_Problem + "\n where \n";
    	for (int i=0; i<m_Vars.size(); i++)
    	{
    		question += " " + m_Vars.elementAt(i) + " ";
    	}
    	
    	Vector qv = new Vector();    	
    	qv.addElement(question);
    	return qv;
    }
    
    public Vector getAnswer()
    {
    	// These are not evaluated until the user is asked the question.
    	// Otherwise the quiz would take a long time to load with
    	// all the round trips to the Sage server
    	if (m_EvaluatedSolutions==null)
    	{
    		new Thread(this).run();

    		do
    		{
    			try {Thread.sleep(500);} catch (Exception e) {}
    		} while (m_EvaluatedSolutions==null);
    	}
    	return m_EvaluatedSolutions;
    }

    /**Implementation of Runnable.*/
    public void run()
    {
    	try
    	{
    		evaluateSolutions();
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }

    protected void evaluateSolutions() throws Exception
    {
    	Vector evaluatedSolns = new Vector();
    	
    	for (int i=0; i<m_Solutions.size(); i++)
    	{
    		String toEval = (String)m_Solutions.elementAt(i);
        	String encoded = URLEncoder.encode(toEval, "UTF-8");
        	
        	// String sURL = this.SAGE_SERVER_URL + "/eval?code=" + encoded;
        	String sURL = this.SAGE_SERVER_URL + "/eval?code=x^2";
        	trace("EVALUATING " + sURL);
        	
        	WikiConnection wc = new WikiConnection();
        	byte[] bytes = wc.readAllBytes(sURL, null, true);
        	wc.close();
        	String evaluated = new String(bytes);
        	trace("EVALUATED SOLN=" + evaluated);        	
    	}
    	
    	m_EvaluatedSolutions = evaluatedSolns;
    }
    
    public Vector getMatchingLastLinesUpToNextTestableChars()
    {
        Vector vLastLines = new Vector();

        trace("m_vAnswerFragment=" + m_vAnswerFragment);
        trace("m_vAnswerFragment.size=" + m_vAnswerFragment.size());
        trace("m_vUnansweredLines=" + m_vUnansweredLines);
        trace("m_vUnansweredLines.size=" + m_vUnansweredLines.size());

        if (m_vAnswerFragment.size()==0) return m_vUnansweredLines;
        
        String sLastLine = (String)m_vAnswerFragment.lastElement();
        Enumeration e = m_vUnansweredLines.elements();
        while (e.hasMoreElements())
        {
            String sUnansweredLine = (String)e.nextElement();
            if (sUnansweredLine.startsWith(sLastLine))
            {
                vLastLines.addElement(sUnansweredLine);
            }
        }

        return vLastLines;
    }
    
    public String toXML() {return null;}

    private void trace(String s)
    {
    	System.out.println(s);
    }

}
