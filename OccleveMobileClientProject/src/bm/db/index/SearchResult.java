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

import java.util.Hashtable;
import java.util.Enumeration;

/*
 * File Information
 *
 * Created on       : 06-jul-2005 13:39:47
 * Created by       : narciso
 * Last modified by : $Author: joe_gittings $
 * Last modified on : $Date: 2008/07/04 18:42:54 $
 * Revision         : $Revision: 1.1 $
 */

/**
 * Utility class to be able to return a composite value from node searchs.
 *
 * @author <a href="mailto:narciso@elondra.org">Narciso Cerezo</a>
 * @version $Revision: 1.1 $
 */
class SearchResult
{
    // Search results for normal search
    private Node    node;
    private int     keyIndex;
    // Search results for fuzzy search
    private SortedIntArray  recordIds;
    private Hashtable       complexResult = new Hashtable( 5 );

    SearchResult()
    {
    }

    SearchResult( final Node btnode, final int keyIndex )
    {
        this.node       = btnode;
        this.keyIndex   = keyIndex;
    }

    int getKeyIndex()
    {
        return keyIndex;
    }

    Node getNode()
    {
        return node;
    }

    public void setNode( final Node node )
    {
        this.node = node;
    }

    public void setKeyIndex( final int keyIndex )
    {
        this.keyIndex = keyIndex;
    }

    /**
     *
     * @param results add results to this one
     * @noinspection FieldRepeatedlyAccessedInMethod
     */
    public void addResults( final int[] results )
    {
        if( results != null )
        {
            final int length = results.length;
            if( recordIds == null )
            {
                recordIds = new SortedIntArray( length, 16 );
            }
            for( int i = 0; i < length; i++ )
            {
                if( recordIds.findIndex( results[i] ) == -1 )
                {
                    recordIds.insert( results[i] );
                }
            }
        }
    }

    public void addResults( final String key, final int[] results )
    {
        if( results == null )
        {
            return;
        }
        final int length = results.length;
        SortedIntArray array = (SortedIntArray) complexResult.get( key );
        if( array == null )
        {
            array = new SortedIntArray( length, 16 );
        }
        for( int i = 0; i < length; i++ )
        {
            array.insert( results[i] );
        }
        complexResult.put( key, array );
    }

    public int[] merge()
    {
        final int size = complexResult.size();
        if( size <= 0 )
        {
            return null;
        }
        final SortedIntArray[] arrays = new SortedIntArray[ size ];
        int index = 0;
        int maxSize = 0;
        //noinspection MethodCallInLoopCondition
        for( Enumeration i = complexResult.elements(); i.hasMoreElements(); )
        {
            arrays[index] = (SortedIntArray) i.nextElement();
            if( maxSize < arrays[index].size() )
            {
                maxSize = arrays[index].size();
            }
            index++;
        }
        final SortedIntArray finalResult = new SortedIntArray( maxSize, 16 );
        final int length = arrays[0].size();
        for( int i = 0; i < length; i++ )
        {
            final int value = arrays[0].getElementAt( i );
            boolean insert = true;
            for( int j = 1; j < size && insert; j++ )
            {
                insert = arrays[j].findIndex( value ) != -1;
            }
            if( insert )
            {
                finalResult.insert( value );
            }
        }
        return finalResult.toIntArray();
    }

    public int[] getRecordIds()
    {
        return recordIds != null ? recordIds.toIntArray() : null;
    }
}
