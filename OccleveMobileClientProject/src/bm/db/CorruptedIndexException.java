package bm.db;
/* -----------------------------------------------------------------------------
    OpenBaseMovil Database Library
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

/*
 * File Information
 *
 * Created on       : 12-abr-2006 12:02:58
 * Created by       : narciso
 * Last modified by : $Author: joe_gittings $
 * Last modified on : $Date: 2008/07/04 18:42:55 $
 * Revision         : $Revision: 1.1 $
 */

/**
 * A corrupted index is detected during update of a table.
 *
 * @author <a href="mailto:narciso@elondra.org">Narciso Cerezo</a>
 * @version $Revision: 1.1 $
 */
class CorruptedIndexException
        extends DBException
{
    public CorruptedIndexException( final int errorNumber )
    {
        super( errorNumber );
    }

    public CorruptedIndexException(
            final String message,
            final int errorNumber
    )
    {
        super( errorNumber, message );
    }

    public CorruptedIndexException(
            final Throwable cause,
            final int errorNumber
    )
    {
        super( errorNumber, cause );
    }

    public CorruptedIndexException( final String message,
                                    final Throwable cause,
                                    final int errorNumber
    )
    {
        super( errorNumber, message, cause );
    }
}
