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

package org.occleve.mobileclient.excludable.devstuff;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

import org.occleve.mobileclient.*;

public class TestbedCanvas extends GameCanvas implements CommandListener
{
    private static final int MARGIN = 5;

    protected DevStuffScreen m_DevStuffScreen;
    protected Command m_BackCommand;

    public TestbedCanvas(DevStuffScreen dvs)
    throws Exception
    {
        super(false);
        m_DevStuffScreen = dvs;

        m_BackCommand = new Command("Back",Command.BACK,0);
        addCommand(m_BackCommand);
        setCommandListener(this);

        paintOffscreenBuffer();

        setFullScreenMode(true);
    }

    protected void paintOffscreenBuffer()
    {
        Graphics g = this.getGraphics();

        g.setColor(0xffffff);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(0x000000);

        // Test using character for wo3 (pronoun 'I').
        drawZoomedCharacter(g,'\u6211');
    }

    protected void drawZoomedCharacter(Graphics g,char c)
    {
        String cs = new Character(c).toString();
        final int xOrigin=20;
        final int yOrigin=20; //-100;
        final int anchor = (Graphics.TOP|Graphics.LEFT);
        g.drawString(cs,0,0,anchor);

        // Test whether can copy from a non-visible area
        // g.copyArea(-20,-20,1,1,5,5,anchor);

        int scale = 2;
        for (int x=xOrigin; x>=0; x--)
        {
            for (int y=yOrigin; y>=0; y--)
            {
                g.copyArea(x,y,1,1,x*scale,y*scale,anchor);
                g.copyArea(x,y,1,1,x*scale-1,y*scale,anchor);
                g.copyArea(x,y,1,1,x*scale,y*scale-1,anchor);
                g.copyArea(x,y,1,1,x*scale-1,y*scale-1,anchor);
            }
        }
    }

    public void paint(Graphics g)
    {
        // FontCanvasMIDlet_paint(g);
        flushGraphics();

    }

    /**From http://www.java2s.com/Code/Java/J2ME/FontCanvasMIDlet.htm
    Learning Wireless Java, Help for New J2ME Developers, By Qusay Mahmoud.*/
    protected void FontCanvasMIDlet_paint(Graphics g)
    {
       g.setColor(0xffffff);
       g.fillRect(0, 0, getWidth(), getHeight());
       g.setColor(0x000000);

       int y = MARGIN;
       int STEP = 15;

       g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
       g.drawString("Monospace Small Plain", MARGIN, y+=STEP, g.LEFT | g.TOP);

       g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
       g.drawString("System Small Plain", MARGIN, y+=STEP, g.LEFT | g.TOP);

       g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
       g.drawString("System Large Plain", MARGIN, y+=STEP, g.LEFT | g.TOP);

       g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
       g.drawString("System Med Plain", MARGIN, y+=STEP, g.LEFT | g.TOP);

       g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
       g.drawString("System Med Bold", MARGIN, y+=STEP, g.LEFT | g.TOP);

       g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_MEDIUM));
       g.drawString("System Med Italic", MARGIN, y+=STEP, g.LEFT | g.TOP);

       g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM));
       g.drawString("System Med Underlined", MARGIN, y+=STEP, g.LEFT | g.TOP);
   }
    /**Implementation of CommandListener.*/
    public void commandAction(Command c,Displayable s)
    {
        if (c==m_BackCommand)
        {
            OccleveMobileMidlet.getInstance().setCurrentForm(m_DevStuffScreen);
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command type in TestbedCanvas.commandAction");
        }
    }
}

