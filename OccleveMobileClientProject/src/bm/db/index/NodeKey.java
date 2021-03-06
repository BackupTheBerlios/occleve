/*
 * Copyright (c) 2005 Elondra, S.L. All Rights Reserved.
 */
package bm.db.index;
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

import bm.core.io.Serializable;
import bm.core.io.SerializationException;
import bm.core.io.SerializerInputStream;
import bm.core.io.SerializerOutputStream;

/*
 * File Information
 *
 * Created on       : 06-jul-2005 13:04:00
 * Created by       : narciso
 * Last modified by : $Author: joe_gittings $
 * Last modified on : $Date: 2008/07/04 18:42:54 $
 * Revision         : $Revision: 1.1 $
 */

/**
 * A key in a tree node.
 *
 * @author <a href="mailto:narciso@elondra.org">Narciso Cerezo</a>
 * @version $Revision: 1.1 $
 */
class NodeKey
    implements Serializable
{
    private Index           index;
    private IndexKey        key;
    private Object          data;

    public NodeKey( final Index index )
    {
        this.index = index;
        key = new IndexKey( null );
    }

    NodeKey(
            final Index     index,
            final IndexKey  key,
            final Object    data
    )
    {
        this.index  = index;
        this.key    = key;
        this.data   = data;
    }

    /**
     * Get the name of the class to be used for serialization/deserialization
     * of complex/nested objects.
     *
     * @return class name
     */
    public String getSerializableClassName()
    {
        return "bm.db.index.NodeKey";
    }

    public IndexKey getKey()
    {
        return key;
    }

    public Object getData()
    {
        return data;
    }

    public void setKey( final IndexKey key )
    {
        this.key = key;
    }

    public void setData( final Object data )
    {
        this.data = data;
    }

    /**
     * Write the contents of this object to an output stream.
     *
     * @param out output stream
     * @throws SerializationException on errors
     */
    public void serialize( final SerializerOutputStream out )
            throws SerializationException
    {
        out.writeByte( (byte) 1 ); // Version
        key.serialize( out );
        if( index.tableIndex )
        {
            ((SortedIntArray)data).serialize( out );
        }
        else
        {
            out.writeNullableObject( data );
        }
    }

    /**
     * Read the contents of this object from an input stream.
     *
     * @param in input stream
     * @throws SerializationException on errors
     */
    public void deserialize( final SerializerInputStream in )
            throws SerializationException
    {
        in.readByte(); // Skip version
        key.deserialize( in );
        if( index.tableIndex )
        {
            final SortedIntArray array = new SortedIntArray( 16, 16 );
            array.deserialize( in );
            data = array;
        }
        else
        {
            data = in.readNullableObject();
        }
    }

    /**
     * Compare to another key.
     *
     * @param nodeKey key to compare
     * @return a value less than 0 if this key is smaller, 0 if both keys are
     * equal, and a value greater than 0 if this key is bigger
     */
    public int compareTo( final NodeKey nodeKey )
    {
        return compareTo( nodeKey.key );
    }

    /**
     * Compare to another key value.
     *
     * @param object key value to compare, assumed to be a String or Long
     * @return a value less than 0 if this key is smaller, 0 if both keys are
     * equal, and a value greater than 0 if this key is bigger
     */
    public int compareTo( final IndexKey object )
    {
        return key.compare( object, index.isCaseSensitive() );
    }
}
