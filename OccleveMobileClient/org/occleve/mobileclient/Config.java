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
    //////String sURL = "http://www.wikia.com/wiki/Main_Page";

    public static String SERVER_URL = "http://localhost:8080/wiki/index.php/";
    public static String LIST_OF_TESTS_URL = "http://localhost:8080/wiki/index.php?title=EN-ZH-ListOfTests&action=raw";
}
