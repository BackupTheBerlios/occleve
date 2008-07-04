/*
 * Copyright (c) 2005 Elondra, S.L. All Rights Reserved.
 */
package bm.core.io;
/* -----------------------------------------------------------------------------
    OpenBaseMovil Core Library, foundation of the OpenBaseMovil database and tools
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
 * Created on       : 13-jul-2005 14:00:20
 * Created by       : narciso
 * Last modified by : $Author: joe_gittings $
 * Last modified on : $Date: 2008/07/04 18:42:55 $
 * Revision         : $Revision: 1.1 $
 */

/**
 * An object that can be written and read to/from a stream.
 *
 * @author <a href="mailto:narciso@elondra.com">Narciso Cerezo</a>
 * @version $Revision: 1.1 $
 */
public interface Serializable
{
    /**
     * Get the name of the class to be used for serialization/deserialization
     * of complex/nested objects.
     *
     * @return class name
     */
    String getSerializableClassName();

    /**
     * Write object status to stream.
     *
     * @param out output stream
     * @throws SerializationException on errors
     */
    void serialize( SerializerOutputStream out )
        throws SerializationException;

    /**
     * Read object status from stream.
     *
     * @param in input stream
     * @throws SerializationException on errors
     */
    void deserialize( SerializerInputStream in )
            throws SerializationException;
}
