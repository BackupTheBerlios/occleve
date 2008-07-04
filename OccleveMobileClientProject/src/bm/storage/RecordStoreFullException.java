package bm.storage;

/* -----------------------------------------------------------------------------
    OpenBaseMovil Storage Library
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
 * Created on       : 02-may-2007 17:22:37
 * Created by       : narciso
 * Last modified by : $Author: joe_gittings $
 * Last modified on : $Date: 2008/07/04 18:42:54 $
 * Revision         : $Revision: 1.1 $
 */

/**
 * Elimates the dependency on the javax.microedition.rms package.
 *
 * @author <a href="mailto:narciso@elondra.com">Narciso Cerezo</a>
 * @version $Revision: 1.1 $
 */
public class RecordStoreFullException
        extends J2MEException
{
    public RecordStoreFullException( final int errorNumber )
    {
        super( errorNumber );
    }

    public RecordStoreFullException( final int errorNumber, final String string )
    {
        super( errorNumber, string );
    }

    public RecordStoreFullException( final int errorNumber, final Throwable source )
    {
        super( errorNumber, source );
    }

    public RecordStoreFullException(
            final int errorNumber, final Throwable source, final String message
    )
    {
        super( errorNumber, source, message );
    }
}
