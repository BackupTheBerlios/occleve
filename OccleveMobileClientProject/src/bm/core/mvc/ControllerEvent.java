/*
 * Copyright (c) 2006 Elondra S.L. All Rights Reserved.
 */
package bm.core.mvc;
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
import java.util.Hashtable;
import java.util.Enumeration;

/*
 * File Information
 *
 * Created by       : narciso
 * Last modified by : $Author: joe_gittings $
 * Last modified on : $Date: 2008/07/04 18:42:54 $
 * Revision         : $Revision: 1.1 $
 */

/**
 * A ControllerEvent is the way that views use to communicate their controllers
 * a user interaction.
 *
 * @author <a href="mailto:narciso@elondra.com">Narciso Cerezo</a>
 * @version $Revision: 1.1 $
 */
public class ControllerEvent
{
    /**
     * Standard event for application exit.
     */
    public static final int EXIT    = 10000;
    /**
     * Standard event for help.
     */
    public static final int HELP    = 10001;
    /**
     * Standard event for back.
     */
    public static final int BACK    = 10002;
    /**
     * Standard event for accept.
     */
    public static final int ACCEPT  = 10003;
    /**
     * Standard event for cancel.
     */
    public static final int CANCEL  = 10004;
    /**
     * Standard event for stop.
     */
    public static final int STOP    = 10005;
    /**
     * Standard event for home.
     */
    public static final int HOME    = 10006;
    /**
     * Standard event for list item selection on list views.
     */
    public static final int LIST_ITEM   = 10007;

    private int         code;
    private View        source;
    private int         index;
    private Hashtable   properties = new Hashtable( 1 );

    /**
     * Construct an empty event.
     */
    public ControllerEvent()
    {
    }

    /**
     * Construct an event with the given code.
     *
     * @param code event code
     */
    public ControllerEvent( final int code )
    {
        this.code = code;
    }

    /**
     * Construct an event for the given view.
     *
     * @param source source view
     */
    public ControllerEvent( final View source )
    {
        this.source = source;
    }

    /**
     * Construct an event for the given view with the given code.
     *
     * @param code
     * @param source
     */
    public ControllerEvent( final int code, final View source )
    {
        this.code = code;
        this.source = source;
    }

    public ControllerEvent( final int code, final View source, final int index )
    {
        this.code = code;
        this.source = source;
        this.index = index;
    }

    /**
     * Get event code.
     *
     * @return event code
     */
    public int getCode()
    {
        return code;
    }

    /**
     * Set event code.
     *
     * @param code event code
     */
    public void setCode( final int code)
    {
        this.code = code;
    }

    /**
     * Get event source view.
     *
     * @return source view
     */
    public View getSource()
    {
        return source;
    }

    /**
     * Set event source view.
     *
     * @param source source view
     */
    public void setSource( final View source )
    {
        this.source = source;
    }

    /**
     * Add event poperty.
     *
     * @param key property  key
     * @param value property value
     */
    public void add( final Object key, final Object value )
    {
        properties.put( key, value );
    }

    /**
     * Get event property.
     *
     * @param key property key
     * @return property value or null if not found
     */
    public Object get( final Object key )
    {
        return properties.get( key );
    }

    /**
     * Remove event property.
     *
     * @param key event property
     */
    public void remove( final Object key )
    {
        properties.remove( key );
    }

    /**
     * Property keys enumeration.
     *
     * @return enumeration
     */
    public Enumeration propertyKeys()
    {
        return properties.keys();
    }

    /**
     * Property values enumeration.
     *
     * @return enumeration
     */
    public Enumeration properties()
    {
        return properties.elements();
    }

    /**
     * Get selected index for LIST_ITEM events.
     *
     * @return selected index
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Set selected index for LIST_ITEM events.
     *
     * @param index selected index
     */
    public void setIndex( final int index )
    {
        this.index = index;
    }
}
