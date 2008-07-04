package bm.core.tools;

/* -----------------------------------------------------------------------------
    bmScript Scripting language for Mobile Devices
    Copyright (C) 2004-2008 Elondra S.L.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.
    If not, see <a href="http://www.gnu.org/licenses">http://www.gnu.org/licenses</a>.
----------------------------------------------------------------------------- */

import bm.core.J2MEException;

/*
 * File Information
 *
 * Created on       : 19-oct-2007 14:44:44
 * Created by       : narciso
 * Last modified by : $Author: joe_gittings $
 * Last modified on : $Date: 2008/07/04 18:42:54 $
 * Revision         : $Revision: 1.1 $
 */

/**
 * Error compiling source.
 *
 * @author <a href="mailto:narciso@elondra.com">Narciso Cerezo</a>
 * @version $Revision: 1.1 $
 */
public class ParserException
        extends J2MEException
{
    private int line;
    private int character;

    public ParserException(
            final String  message,
            final int     line,
            final int     character
    )
    {
        super( 0, message );
        this.line = line;
        this.character = character;
    }

    public int getLine()
    {
        return line;
    }

    public int getCharacter()
    {
        return character;
    }

    public String toString()
    {
        return getMessage() + " @ " + line + " : " + character;
    }
}