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
@version 0.9.3
*/

package org.occleve.mobileclient;

/**Static constants for the entire midlet.*/
public class Constants
{
    /**In most cases, you should use NEWLINE instead.*/
    public static String CRLF = "\r\n";

    /**In most cases, you should use NEWLINE instead.*/
    public static String LF = "\n";

    /**This is intended to make it easy to switch the entire
    midlet code between using LF and CRLF.*/
    public static String NEWLINE = "\n";

    public static int NEWLINE_LENGTH = NEWLINE.length();

    public static String PRODUCT_NAME = "Occleve";

    public static String EMPTY_QUIZ_MSG =
            "This quiz doesn't contain any " +
            "testable questions. (The mobile " +
            "client doesn't support all Wikiversity quiz " +
            "question types yet, so that might be the reason)";
}

