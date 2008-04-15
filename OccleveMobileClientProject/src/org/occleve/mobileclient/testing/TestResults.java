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

package org.occleve.mobileclient.testing;

import net.dclausen.microfloat.*;

/**The meaning of "response" varies depending on the type of questions in the
test. For magic typewriter questions, it's the keypresses. For multiple choice
questions, it's the answers.*/
public class TestResults
{
    protected int m_iTestableResponseCount = 0;
    public int getTestableResponseCount() {return m_iTestableResponseCount;}

    protected int m_iWrongResponseCount = 0;
    public int getWrongResponseCount() {return m_iWrongResponseCount;}

    protected int m_iTotalResponseCount = 0;
    public int getTotalResponseCount() {return m_iTotalResponseCount;}

    public int getCorrectResponseCount()
    {
        return m_iTotalResponseCount - m_iWrongResponseCount;
    }

    public void addResponse(boolean bWasCorrect)
    {
        m_iTotalResponseCount++;

        if (!bWasCorrect) m_iWrongResponseCount++;
    }

    public void addResponses(boolean bWereCorrect,int iCount)
    {
        m_iTotalResponseCount += iCount;

        if (!bWereCorrect) m_iWrongResponseCount += iCount;
    }

    /**Returns a true integer, not an integer containing a packed
    floating point number.*/
    public int getAccuracyPercentage()
    {
    	// 0.9.6 - if no responses at all so far, the accuracy is deemed
    	// to be 100%, not 0% (which the calculation below would give).
    	if (m_iTotalResponseCount==0) return 100;
    	
        // fpii stands for "float packed into integer".

        int fpiiCorrect
            = MicroFloat.intToFloat( getCorrectResponseCount() );
        int fpiiTotal = MicroFloat.intToFloat(  getTotalResponseCount() );
        int fpiiPackedFraction = MicroFloat.div( fpiiCorrect, fpiiTotal );

        int fpiiHundred = MicroFloat.intToFloat((int)100);
        int fpiiPercentage = MicroFloat.mul( fpiiPackedFraction,fpiiHundred );
        int fpiiRounded = MicroFloat.rint(fpiiPercentage);

        return MicroFloat.intValue(fpiiRounded);
    }
    
    // 0.9.6
    public void reset()
    {
        m_iTestableResponseCount = 0;
        m_iWrongResponseCount = 0;
        m_iTotalResponseCount = 0;
    }
}

