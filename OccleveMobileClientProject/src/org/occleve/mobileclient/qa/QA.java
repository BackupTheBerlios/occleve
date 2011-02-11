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

package org.occleve.mobileclient.qa;

import java.util.*;
import org.occleve.mobileclient.*;

/**
i)  For the Vectors of Strings returned by the methods in this class, each String
    represents a line on the display (which may have to be wrapped).
ii) Methods which take a QADirection argument should accept null - this signifies
    "don't reverse direction".*/
public abstract class QA
{
    protected QADirection m_QADirection;

    /**The lines in the answer which have not yet been fully answered.*/
    protected Vector m_vUnansweredLines;

    /**The lines that the user has typed, in the order he/she has typed them.*/
    protected Vector m_vAnswerFragment;

    /**Should return the XML representation of this QA.*/
    public abstract String toXML();

    /**Returns a Vector of String objects.*/
    public abstract Vector getQuestion();

    /**Returns a Vector of String objects.*/
    public abstract Vector getAnswer();

    /**Should return a Vector of Items.*/
    public abstract Vector getEntireContentsAsItems();

    public abstract String getEntireContentsAsString();

    /**Returns a Vector of String objects.*/
    public Vector getAnswerFragment() {return m_vAnswerFragment;}

    public void setAnswerFragment(Vector vAnswerFragment)
    {
        m_vAnswerFragment = vAnswerFragment;
    }

    /**Returns a Vector of Character objects.*/
    public abstract Vector getMatchingLastLinesUpToNextTestableChars();
    
    public abstract boolean containsString(String s);

    /**Gets this ready for use in testing: clears the answer fragment, and so on.*/
    public void initialize(QADirection dir)
    {    	
        m_QADirection = dir;

        m_vAnswerFragment = new Vector();
        m_vAnswerFragment.addElement( new String() );

        m_vUnansweredLines = getAnswer();
    }

    /**Internal helper function.*/
    protected String vectorToString(Vector v)
    {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<v.size(); i++)
        {
            if (i!=0) sb.append( Constants.NEWLINE );

            sb.append( (String)v.elementAt(i) );
        }
        return sb.toString();
    }

    public String getAnswerAsString()
    {
        Vector v = getAnswer();
        return vectorToString(v);
    }

    /**Returns true if *all* the next possible chars are non-ASCII.*/
    public boolean nextPossibleCharsAreUnicode()
    {
        Vector v = getMatchingLastLinesUpToNextTestableChars();
        Enumeration e = v.elements();
        while (e.hasMoreElements())
        {
        	String sFragment = (String)e.nextElement();
        	char c = sFragment.charAt(sFragment.length()-1);
        	
            boolean isNotUnicode = (c <= 255);
            if (isNotUnicode) return false;
        }
        return true;
    }

    public int getNextPossibleCharsCount()
    {
    	return getMatchingLastLinesUpToNextTestableChars().size();
    }

    /**Adds the specified character to the answer fragment.
    If this means the current line has been fully answered, remove it from
    the list of untyped answer lines.*/
    public void setAnswerFragmentLastLine(String sSetToThis)
    throws Exception
    {
        // Sanity check: first make sure that the character being appended
        // is one of the allowed ones.
    	// 0.9.6 - DISABLE THIS SANITY CHECKING FOR NOW........

        Vector vAnswerFragment = getAnswerFragment();
        int iLastIndex = vAnswerFragment.size() - 1;
        vAnswerFragment.setElementAt(sSetToThis,iLastIndex);

        // If the current line has been fully answered:
        // i)  remove it from the list of untyped answer lines,
        // ii) append an empty string to the answer fragment vector
        //     (ie. start a new answer line).

        for (int i=0; i<m_vUnansweredLines.size(); i++)
        {
            String sLine = (String)m_vUnansweredLines.elementAt(i);
            if (sSetToThis.equals(sLine))
            {
                m_vUnansweredLines.removeElementAt(i);

                // Only start a new answer line if there are unanswered lines.
                if (m_vUnansweredLines.size() > 0)
                {
                    vAnswerFragment.addElement(new String());
                }

                break;
            }
        }

        // Store the new answer fragment.
        setAnswerFragment(vAnswerFragment);
    }

    public boolean isAnswered()
    {
        Vector answer = getAnswer();
        Vector fragment = getAnswerFragment();
        
        if (   fragment.size()!=answer.size()   ) return false;

        for (int i=0; i<answer.size(); i++)
        {
            String s = (String)answer.elementAt(i);
            String s2 = (String)fragment.elementAt(i);
            boolean same = s.equals(s2);
            if (same==false) return false;
        }

        return true;
    }

    public boolean containsQADirectionFields(QADirection qadir)
    {
    	return true;
    }

    /**Return all the unanswered lines which begin with the current
    last line of the answer fragment.*/
    public Vector getMatchingUnansweredLines()
    {
		if (m_vAnswerFragment.size()==0)
		{
			return m_vUnansweredLines;
		}
		else
		{
	        Vector vMatchingUAL = new Vector();
	        String sLastLine = (String)m_vAnswerFragment.lastElement();
	        Enumeration e = m_vUnansweredLines.elements();
	        while (e.hasMoreElements())
	        {
	            String sUnansweredLine = (String)e.nextElement();
	            if (sUnansweredLine.startsWith(sLastLine))
	            {
	                vMatchingUAL.addElement(sUnansweredLine);
	            }
	        }
	        return vMatchingUAL;
		}
    }
}

