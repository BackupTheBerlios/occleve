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

import java.util.Enumeration;
import java.util.Vector;

import org.occleve.mobileclient.StaticHelpers;

import com.exploringxml.xml.*;

public class MathQA extends PlainQA
{
    /**Load a MathQA from an XML file.*/
    public MathQA(Node qaNode) throws Exception
    {
    	super(qaNode);
    }

    public Vector getMatchingLastLinesUpToNextTestableChars()
    {
        Vector vLastLines = new Vector();

        String sLastLine = (String)m_vAnswerFragment.lastElement();
        Enumeration e = m_vUnansweredLines.elements();
        while (e.hasMoreElements())
        {
            String sUnansweredLine = (String)e.nextElement();
            if (sUnansweredLine.startsWith(sLastLine))
            {
            	String sAddMe =
            		getUnansweredLineUpToNextTestableChar(sLastLine,sUnansweredLine);
            	
                if (sAddMe!=null) vLastLines.addElement(sAddMe);
            }
        }

        return vLastLines;
    }

    private String getUnansweredLineUpToNextTestableChar
            (String sAnswerFragmentLastLine,String sMatchingUnansweredLine)
    {
    	// There isn't a testable char if they're of matching length.
        if (sAnswerFragmentLastLine.length()==sMatchingUnansweredLine.length())
        {
        	return null;
        }

        int iIndex = sAnswerFragmentLastLine.length();
        char possChar;
        do
        {
            possChar = sMatchingUnansweredLine.charAt(iIndex);
            iIndex++;
        } while ( (possChar==' ')
                 && (iIndex<sMatchingUnansweredLine.length())   );

        if (possChar==' ')
            return (sMatchingUnansweredLine.substring(0,iIndex));
        else
            return null;
    }    
}
