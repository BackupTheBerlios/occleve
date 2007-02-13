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

package org.occleve.mobileclient;

/**Static variables with various config settings. To be moved to an ini
file downloaded from the wiki.*/
public class Config
{
    // 0.9.2 in a form that can be used for comparisons: 000902
    public static final long VERSION = 902;

    public static final String MIN_RELEASE_DIRECTIVE = "!MIN_RELEASE=";

    public static final String ANNOUNCEMENT_DIRECTIVE = "!ANNOUNCEMENT=";

    public static final String SERVER_URL =
       "http://occleve.berlios.de/wiki/index.php/";

    public static final String PAGE_URL_STUB =
       "http://occleve.berlios.de/wiki/index.php?title=";

    public static final String PAGE_URL_SUFFIX = "&action=raw";

    public static final String LIST_OF_TESTS_URL =
       "http://occleve.berlios.de/wiki/index.php?title=ListOfTests&action=raw";

    /**The default encoding for talking to the wiki, storing the tests
    in the recordstore, etc etc.*/
    public static final String ENCODING = "UTF-8";
}

