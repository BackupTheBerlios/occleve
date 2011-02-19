/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2011  Joe Gittings

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
	public static String SAGE_SERVER = "localhost:8000";

	private static Random gen = new Random(System.currentTimeMillis());
	
	protected String m_Desc;
	protected String m_Problem;
	protected Vector m_Vars = new Vector();
	protected Vector m_Solutions = new Vector();
	
	protected Vector m_EvaluatedSolutions;

	protected boolean m_bThreadActive = false;
	protected int m_iThreadAction;
	protected String m_sSageCodeToExec;
	private static final int EVALUATE_SOLNS = 0;
	private static final int EVALUATE_VAR = 1;
	private static final int EXEC = 2;
	
	protected class Var
	{
		protected String m_Name;
		protected String m_Value;
		protected String m_Exec;
		protected String m_DisplayValue;
		
		public Var(Node varNode)
		{
        	m_Name = (String)varNode.attributes.get("Name");
        	m_DisplayValue = (String)varNode.attributes.get("DisplayValue");

        	m_Exec = (String)varNode.attributes.get("Exec");
        	if (m_Exec!=null) executeSageCode(m_Exec);
		}
		
		public String toString()
		{
        	return m_Name + "=" + m_DisplayValue;			
		}
	}

	protected void executeSageCode(String sSageCode)
	{
		m_iThreadAction = EXEC;
		m_sSageCodeToExec = sSageCode;
		new Thread(this).run();

		do
		{
			try {Thread.sleep(500);} catch (Exception e) {}
		} while (m_bThreadActive);			
	}
	
	protected class RandomVar extends Var
	{
		private double m_Min;
		private double m_Max;

		public RandomVar(Node varNode)
		{
			super(varNode);
			
        	boolean isInt = (varNode.name.equals("RandomInt"));

        	String min = (String)varNode.attributes.get("Min");
        	m_Min = Double.parseDouble(min);
        	
        	String max = (String)varNode.attributes.get("Max");            	
        	m_Max = Double.parseDouble(max);
						
			double diff = m_Max - m_Min;
			if (isInt)
			{
				double value = m_Min + (gen.nextDouble() * diff);
				m_Value = "" + (int)value;
			}
			else
			{
				double value = m_Min + (gen.nextDouble() * diff);
				m_Value = "" + value;
			}

			m_DisplayValue = m_Value;
			
			trace("Parsed var " + m_Name + " min=" + m_Min +
					" max=" + m_Max + " value=" + m_Value);			
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
            	try
            	{
            		System.out.println("child.name=" + child.name);
            		System.out.println("child.getCharacters()=" + child.getCharacters());
            	} catch (Exception e) {}

                if (child.name.equals("RandomVar") ||
            		child.name.equals("RandomInt"))
	            {
	            	trace("Parsing RandomVar node: " + child);
	            	RandomVar var = new RandomVar(child);
	            	m_Vars.addElement(var);
	            }
	            else if (child.name.equals("Var"))
	            {
	            	Var var = new Var(child);
	            	m_Vars.addElement(var);
	            }
	            else if (child.name.equals("Exec"))
	            {
	            	String code = child.getCharacters();
	            	executeSageCode(code);
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
    	if (m_EvaluatedSolutions!=null)
    	{
    		System.out.println("Existing evaluated solutions size=" + m_EvaluatedSolutions.size());
    	}
    	else
    	{
    		m_iThreadAction = EVALUATE_SOLNS;
    		new Thread(this).run();

    		do
    		{
    			try {Thread.sleep(500);} catch (Exception e) {}
    		} while (m_bThreadActive);
    		
    		System.out.println("No of evaluated solutions=" + m_EvaluatedSolutions.size());
    	}

    	return m_EvaluatedSolutions;
    }

    /**Implementation of Runnable.*/
    public void run()
    {
    	synchronized(this)
    	{
    		m_bThreadActive = true;
	    	try
	    	{
	    		if (m_iThreadAction==EVALUATE_SOLNS)
	    			evaluateSolutions();
	    		else
	    			exec();
	        }
	        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    		m_bThreadActive = false;
    	}
    }

    protected void evaluateSolutions() throws Exception
    {
    	Vector evaluatedSolns = new Vector();

    	for (int i=0; i<m_Vars.size(); i++)
    	{
    		Var var = (Var)m_Vars.elementAt(i);
    		if (var instanceof RandomVar)
    		{
	    		String assign = var.m_Name + "=" + var.m_Value;
	        	String encoded = URLEncoder.encode(assign, "UTF-8");        	
	        	String sURL = "http://" + SAGE_SERVER + "/eval?code=" + encoded;
	        	WikiConnection wc = new WikiConnection();
	        	byte[] bytes = wc.readAllBytes(sURL, null, true);
	        	wc.close();
	        	System.out.println("ASSIGNED VAR: " + var);    		
    		}
    	}

    	for (int i=0; i<m_Solutions.size(); i++)
    	{
    		String toEval = (String)m_Solutions.elementAt(i);
        	String encoded = URLEncoder.encode(toEval, "UTF-8");
        	
        	String sURL = "http://" + SAGE_SERVER + "/eval?code=" + encoded;
        	trace("EVALUATING " + sURL);
        	
        	WikiConnection wc = new WikiConnection();
        	byte[] bytes = wc.readAllBytes(sURL, null, true);
        	wc.close();
        	String evaluated = new String(bytes);
        	System.out.println("EVALUATED SOLN: " + evaluated);
        	
        	evaluatedSolns.addElement(evaluated);
    	}
    	
    	m_EvaluatedSolutions = evaluatedSolns;
    }

    protected void exec() throws Exception
    {
		String toEval = m_sSageCodeToExec;
    	String encoded = URLEncoder.encode(toEval, "UTF-8");
    	
    	String sURL = "http://" + SAGE_SERVER + "/eval?code=" + encoded;
    	trace("EXECUTING: " + sURL);
    	
    	WikiConnection wc = new WikiConnection();
    	byte[] bytes = wc.readAllBytes(sURL, null, true);
    	wc.close();
    	String evaluated = new String(bytes);
    	System.out.println("EXECUTED: " + evaluated);
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
    	//// System.out.println(s);
    }
}
