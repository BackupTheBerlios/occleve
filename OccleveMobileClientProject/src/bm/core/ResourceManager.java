/*
 * Copyright (c) 2005 Elondra, S.L. All Rights Reserved.
 */
package bm.core;
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

import bm.core.event.Event;
import bm.core.math.FixedPoint;
import bm.core.mvc.HelpView;
import bm.core.res.ResourceProvider;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

/*
 * File Information
 *
 * Created on       : 13-sep-2005 12:49:35
 * Created by       : narciso
 * Last modified by : $Author: joe_gittings $
 * Last modified on : $Date: 2008/07/04 18:42:55 $
 * Revision         : $Revision: 1.1 $
 */

/**
 * Centralized resource manager for internationalization.<br/>
 * The resource manager is an internationalization tool that allows applications
 * to specify resources in a localized way.<br/>
 * The locale can be set programatically using the activeLocale property, if
 * it's not, it's determined by loking at the following system properties in the
 * specified order:<br/>
 * <ol>
 * <li>resource.locale</li>
 * <li>microedition.locale</li>
 * <li>user.language and user.country</li>
 * </ol>
 *
 * @author <a href="mailto:narciso@elondra.com">Narciso Cerezo</a>
 * @version $Revision: 1.1 $
 */
public class ResourceManager
{
    private static String       activeLocale;
    private static Vector       providers = new Vector( 1 );

    private ResourceManager()
    {
    }

    /**
     * Get the active locale
     * @return active locale, null if not previously set
     * @see #getLocale()
     */
    public static String getActiveLocale()
    {
        return activeLocale;
    }

    /**
     * Add a new provider to the list of providers (if it's not already in it).
     * @param provider new provider
     */
    public static void addProvider( final ResourceProvider provider )
    {
        if( !providers.contains( provider ) )
        {
            providers.addElement( provider );
        }
    }

    /**
     * Remove a registered provider.
     * @param provider provider
     */
    public static void removeProvider( final ResourceProvider provider )
    {
        if( providers.contains( provider ) )
        {
            providers.removeElement( provider );
        }
    }

    /**
     * Enumerate current registered providers.
     * @return provider enumeration
     */
    public static Enumeration providers()
    {
        return providers.elements();
    }

    /**
     * Remove all registered providers.
     */
    public static void clear()
    {
        providers.removeAllElements();
    }


    /**
     * Set the active locale.<br/>
     * This method changes to a new locale and reloads resource bundles already
     * registered but with the new locale.
     * @param activeLocale non null locale string according to ISO format
     */
    public static synchronized void setActiveLocale( final String activeLocale )
    {
        if(
                activeLocale != null && (
                    ResourceManager.activeLocale == null ||
                    !ResourceManager.activeLocale.equals( activeLocale )
                )
        )
        {
            ResourceManager.activeLocale = activeLocale;
            final int count = providers.size();
            for( int i = 0; i < count; i++ )
            {
                ((ResourceProvider) providers.elementAt( i )).setLocale(
                        activeLocale
                );
            }
            // Notify listeners
            Event.dispatch( new Event( Event.ET_LANGUAGE_CHANGE ) );
            // Notify known CORE classes
            FixedPoint.reloadLanguageSettings();
            HelpView.reload();
        }
    }

    /**
     * Find a localized stream.
     * @param caller caller class
     * @param name base name
     * @param extension extension
     * @param rooted if true, a / is prepended to the name
     * @return the stream or null if not found
     */
    public static InputStream findLocalizedStream(
            final Class     caller,
            final String    name,
            final String    extension,
            final boolean   rooted
    )
    {
        final String fullLocale = getLocale();
        InputStream is = tryName( caller, name, extension, fullLocale, rooted );
        if( is == null && fullLocale.indexOf( "_" ) > 0 )
        {
            final String shortLocale = fullLocale.substring(
                    0,
                    fullLocale.indexOf( "_" )
            );
            is = tryName( caller, name, extension, shortLocale, rooted );
        }
        if( is == null )
        {
            is = tryName( caller, name, extension, CoreConstants.DEFAULT_LOCALE, rooted );
        }
        return is;
    }

    /**
     * Gets the locale beign used for bundle loading.<br/>
     * The locale is selected from the first source defined, following this order:
     * <br/>
     * <ol>
     * <li>The activeLocale property</li>
     * <li>Application.getManager().getProperty( &quot;resource.locale&quot; )</li>
     * <li>System.getProperty( &quot;microedition.locale&quot; )</li>
     * <li>System.getProperty( &quot;user.language&quot; ) + System.getProperty( &quot;user.country&quot; )</li>
     * <li></li>
     * </ol>
     * @return locale used
     * @see bm.core.ApplicationManager#getProperty( String )
     */
    public static String getLocale()
    {
        if( activeLocale != null )
        {
            return activeLocale;
        }
        String fullLocale = null;
        if( Application.getManager() != null )
        {
            fullLocale = Application.getManager().getProperty(
                    CoreConstants.PROP_RESOURCE_LOCALE
            );
        }
        if( fullLocale == null )
        {
            fullLocale = System.getProperty( CoreConstants.MICROEDITION_LOCALE );
        }
        // This if serves to make it run under normal java, not microedition
        if( fullLocale == null )
        {
            if( System.getProperty( CoreConstants.USER_LANGUAGE ) != null )
            {
                if( System.getProperty( CoreConstants.USER_COUNTRY ) != null )
                {
                    fullLocale = System.getProperty( CoreConstants.USER_LANGUAGE ) +
                                 "_" + System.getProperty( CoreConstants.USER_COUNTRY );
                }
                else
                {
                    fullLocale = System.getProperty( CoreConstants.USER_LANGUAGE );
                }
            }
            else
            {
                fullLocale = CoreConstants.DEFAULT_LOCALE;
            }
        }
        // replace - with _ if exists to normalize locale representation
        if( fullLocale.indexOf( '-' ) > 0 )
        {
            fullLocale =
                    fullLocale.substring( 0, fullLocale.indexOf( '-' ) ) +
                    "_" +
                    fullLocale.substring( fullLocale.indexOf( '-' ) + 1 );
        }
        return fullLocale;
    }


    /**
     * Get a resource from any resource bundle.
     * @param key resource key
     * @return resource if found, the key if not found, and an empty string if
     * key is null
     */
    public static String getResource( final String key )
    {
        if( key != null )
        {
            final int count = providers.size();
            String resource = null;
            for( int i = 0; i < count && resource == null; i++ )
            {
                final ResourceProvider provider = (ResourceProvider)
                        providers.elementAt( i );
                resource = provider.getResource( key );
            }
            return resource != null ? resource : key;
        }
        else
        {
            return "";
        }
    }

    /**
     * If numerous calls to getResource are about to come, it might improve
     * speed greatly to open needed resource providers first.
     */
    public static void open()
    {
        final int count = providers.size();
        for( int i = 0; i < count; i++ )
        {
            final ResourceProvider provider = (ResourceProvider)
                    providers.elementAt( i );
            provider.open();
        }
    }

    /**
     * Close providers previously opened.
     */
    public static void close()
    {
        final int count = providers.size();
        for( int i = 0; i < count; i++ )
        {
            final ResourceProvider provider = (ResourceProvider)
                    providers.elementAt( i );
            provider.close();
        }
    }

    private static InputStream tryName(
            final Class     caller,
            final String    name,
            final String    extension,
            final String    locale,
            final boolean   rooted
    )
    {
        final String resourceName = (rooted ? "/" : "" ) +
                                    name + "." + locale + extension;
        return caller.getResourceAsStream( resourceName );
    }
}
