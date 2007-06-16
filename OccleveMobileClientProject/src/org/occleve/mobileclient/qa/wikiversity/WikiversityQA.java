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
@version 0.9.3
*/

package org.occleve.mobileclient.qa.wikiversity;

import java.util.*;
//import javax.microedition.lcdui.*;

//import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
//import org.occleve.mobileclient.screens.*;

/**The base QA class for questions in the Wikiversity quiz format.*/
public abstract class WikiversityQA extends QA
{
    /**Just a simple string for the question.*/
    protected String m_sQuestion;

    /**Load the QA from wikitext.*/
    public WikiversityQA(String sQuestion)
    throws Exception
    {
        m_sQuestion = sQuestion;
    }

    public abstract Vector getAnswer();

    /**Compares the answer and the answer fragment vectors to see what
    are the next possible chars.*/
    public Vector getNextPossibleChars()
    {
        return null;
    }

    public boolean containsString(String s)
    {
        return false;
    }


    /**Implementation of abstract function in QA class.*/
    public Vector getEntireContentsAsItems()
    {
        return null;
    }


    /**Implementation of QA.toXML()*/
    public String toXML()
    {
        return null;
    }

    /**For ease of switching trace output on and off.*/
    private void trace(String s)
    {
        //System.out.println(s);
    }

    public String getQuestionString() {return m_sQuestion;}

    /**All wikiversity quiz question types have just one single question string.*/
    public Vector getQuestion()
    {
        Vector v = new Vector();
        v.addElement(m_sQuestion);
        return v;
    }
}
