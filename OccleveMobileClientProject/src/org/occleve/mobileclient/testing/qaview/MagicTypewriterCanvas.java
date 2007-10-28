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
@version 0.9.4
*/

package org.occleve.mobileclient.testing.qaview;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.util.*;

import org.occleve.mobileclient.*;
////import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.qacontrol.*;

public class MagicTypewriterCanvas extends GameCanvas
implements QuestionView
{
    private static final Font FONT_TO_USE = OccleveMobileFonts.DETAILS_FONT;
    private static final int MARGIN = 5;
    private static final int LINE_OFFSET = 15;

    private static int RIGHT_HAND_BOUNDARY;

    protected MagicTypewriterController m_Controller;

    protected int m_iRepaintScrollUpBy = 0;

    public MagicTypewriterCanvas(MagicTypewriterController mtc)
    throws Exception
    {
        super(false);
        m_Controller = mtc;

        RIGHT_HAND_BOUNDARY = getWidth() - MARGIN;
    }

    protected void keyPressed(int keyCode)
    {
        m_Controller.onKeyPressed(keyCode);
    }

    /**0.9.4: If the pointer is pressed, jump straight into the Unicode input screen.
    This behaviour is so that, if you're using a pen phone to input Hanzi
    or similar pictographic characters,
    starting to write the character will immediately invoke the
    appropriate input screen.*/
    protected void pointerPressed(int x,int y)
    {
    	try
    	{
    		m_Controller.invokeUnicodeInputScreen();
    	}
    	catch (Exception e)
    	{
    		OccleveMobileMidlet.getInstance().onError(e);
    	}
    }

    /**See pointerPressed().*/
    protected void pointerDragged(int x,int y)
    {
    	try
    	{
    		m_Controller.invokeUnicodeInputScreen();
    	}
    	catch (Exception e)
    	{
    		OccleveMobileMidlet.getInstance().onError(e);
    	}
    }
    
    public void paint(Graphics g)
    {
        if (m_iRepaintScrollUpBy!=0)
        {
            g.translate(0,-m_iRepaintScrollUpBy);
        }

        MagicTypewriterController mtc = m_Controller;

        g.setFont(FONT_TO_USE);
        g.setColor(255,255,255);
        final int iTotalHeight = getHeight()+m_iRepaintScrollUpBy;
        g.fillRect(0,0,getWidth(),iTotalHeight);
        g.setColor(0,0,0);

        int y = MARGIN;
        y = printVectorOfStrings("Q: ",mtc.getTestController().getCurrentQuestion(),g,y);
        y = printVectorOfStrings("A: ",mtc.getTestController().getCurrentAnswerFragment(),g,y);
        int iFooterY = getHeight() - MARGIN - LINE_OFFSET;

        // If it won't encroach on the QA, display current score along the bottom.
        if (y < iFooterY)
        {
            String sScoreEtc = mtc.getTestController().getCurrentScore();
            printString(sScoreEtc, g, iFooterY);
        }

        // If the text has gone off the bottom of the screen, scroll it
        // up so the end of the text is visible.

        final int BOTTOM = getHeight() - LINE_OFFSET;
        if (m_iRepaintScrollUpBy!=0)
        {
            // This has been a second, recursive call to paint() to repaint
            // the canvas with it scrolled up. Clear the variable that
            // flags this.
            m_iRepaintScrollUpBy = 0;
        }
        else if (y > BOTTOM)
        {
            // The text went off the bottom of the screen. Recursively call
            // paint() again and repaint the display with it scrolled up.
            m_iRepaintScrollUpBy = (y-BOTTOM);
            paint(g);
        }
    }

    /**Prints a Vector of strings, putting a carriage return after each one.*/
    protected int printVectorOfStrings
    (
       String sPrefix,Vector vPrintMe,Graphics g,int iStartY
    )
    {
        int y = iStartY;

        for (int i=0; i<vPrintMe.size(); i++)
        {
            String sLine = (String)vPrintMe.elementAt(i);
            if (i==0) sLine = sPrefix + sLine;
            y = printString(sLine,g,y);
        }

        return y;
    }

    protected int printString(String sPrintMe,Graphics g,int iStartY)
    {
        int iOffset = 0;
        int iPrintMeLastIndex = sPrintMe.length() - 1;
        int iLineLength;

        do
        {
            int iDisplayedWidth;
            iLineLength = 0;
            boolean bNextIterationWithinStringLength;
            do
            {
                iLineLength++;

                trace("==========================================");
                trace(sPrintMe);
                trace("Calculating width with");
                trace("iOffset = " + iOffset);
                trace("iLineLength = " + iLineLength);

                iDisplayedWidth =
                        FONT_TO_USE.substringWidth(sPrintMe, iOffset, iLineLength);

                // Redundant var for code clarity - will be optimized out
                bNextIterationWithinStringLength =
                        (iOffset + (iLineLength+1) <= sPrintMe.length());

            } while (   (MARGIN + iDisplayedWidth < RIGHT_HAND_BOUNDARY)
                    && bNextIterationWithinStringLength);

            trace("==========================================");
            trace(sPrintMe);
            trace("Drawing substring with");
            trace("iOffset = " + iOffset);
            trace("iLineLength = " + iLineLength);

            g.drawSubstring(sPrintMe,iOffset,iLineLength,MARGIN,iStartY,Graphics.TOP|Graphics.LEFT);
            iOffset += iLineLength;
            iStartY += LINE_OFFSET;
        } while ((iOffset <= iPrintMeLastIndex) && (iLineLength>0));

        return iStartY;
    }

    /**Implementation of MagicTypewriterView method.*/
    public Displayable getDisplayable()
    {
        return this;
    }

    /**Implementation of MagicTypewriterView method.*/
    public void doRepainting()
    {
        repaint();
        serviceRepaints();
    }

    private void trace(String s)
    {
        ///////System.out.println(s);
    }

    /**No need to clone itself.*/
    public QuestionView perhapsClone() throws Exception
    {
        return this;
    }

}

