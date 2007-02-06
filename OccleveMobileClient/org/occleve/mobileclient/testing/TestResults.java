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

import net.dclausen.microfloat.*;

public class TestResults
{
    protected int m_iTestableCharCount = 0;
    public int getTestableCharCount() {return m_iTestableCharCount;}

    protected int m_iTestableNumericCharCount = 0;
    public int getTestableNumericCharCount() {return m_iTestableNumericCharCount;}

    protected int m_iWrongKeypressCount = 0;
    public int getWrongKeypressCount() {return m_iWrongKeypressCount;}

    protected int m_iTotalKeypressCount = 0;
    public int getTotalKeypressCount() {return m_iTotalKeypressCount;}

    public int getCorrectKeypressCount()
    {
        return m_iTotalKeypressCount - m_iWrongKeypressCount;
    }

    public TestResults() throws Exception
    {
    }

    public void addKeypress(boolean bWasCorrect)
    {
        m_iTotalKeypressCount++;

        if (!bWasCorrect) m_iWrongKeypressCount++;
    }

    public void addKeypresses(boolean bWereCorrect,int iCount)
    {
        m_iTotalKeypressCount += iCount;

        if (!bWereCorrect) m_iWrongKeypressCount += iCount;
    }

    /**Returns a true integer, not an integer containing a packed
    floating point number.*/
    public int getAccuracyPercentage()
    {
        // fpii stands for "float packed into integer".

        int fpiiCorrect
            = MicroFloat.intToFloat( getCorrectKeypressCount() );
        int fpiiTotal = MicroFloat.intToFloat(  getTotalKeypressCount() );
        int fpiiPackedFraction = MicroFloat.div( fpiiCorrect, fpiiTotal );

        int fpiiHundred = MicroFloat.intToFloat((int)100);
        int fpiiPercentage = MicroFloat.mul( fpiiPackedFraction,fpiiHundred );
        int fpiiRounded = MicroFloat.rint(fpiiPercentage);

        return MicroFloat.intValue(fpiiRounded);
    }
}

