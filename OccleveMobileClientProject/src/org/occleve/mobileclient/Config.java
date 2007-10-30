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
@version 0.9.4
*/

package org.occleve.mobileclient;

/**Static variables with various config settings. To be moved to an ini
file downloaded from the wiki.*/
public class Config
{
    // 0.9.4 in a form that can be used for comparisons: 000904
    public static final long VERSION = 904;

    public static final String MIN_RELEASE_DIRECTIVE = "!MIN_RELEASE=";

    public static final String ANNOUNCEMENT_DIRECTIVE = "!ANNOUNCEMENT=";

    ////public static final String SERVER_URL =
    /////   "http://occleve.berlios.de/wiki/index.php/";

    public static final String OCCLEVE_QUIZ_URL_STUB =
       "http://occleve.berlios.de/wiki/index.php?title=";

    public static final String OCCLEVE_QUIZ_URL_SUFFIX = "&action=raw";

    public static final String OCCLEVE_LIST_OF_TESTS_URL =
       "http://occleve.berlios.de/wiki/index.php?title=ListOfTests&action=raw";

    public static final String AUDIO_CLIP_URL_STUB =
       "http://occleve.berlios.de/wiki/index.php?title=Image:";

    public static final String AUDIO_CLIP_URL_SUFFIX =
       "&action=edit&externaledit=true&mode=file";

    /**The default encoding for talking to the wiki, storing the tests
    in the recordstore, etc etc.*/
    public static final String ENCODING = "UTF-8";

    /**The maximum number of times to try connecting to the wiki
    before giving up.*/
    public static final int CONNECTION_TRIES_LIMIT = 3;

    //////////////////////////////////////////////////////////////////////
    // Wikiversity Quiz support
    // For now the list of Wikiversity quizzes and the quizzes themselves
    // are retrieved via a custom proxy running on the berlios webserver.
    // This is to enable the demo applet to access them (since the sandbox security
    // model restricts it to connecting back to the berlios webserver).
    //////////////////////////////////////////////////////////////////////

    public static final String FRENCH_WIKIVERSITY_QUIZ_URL_STUB =
        "http://occleve.berlios.de/fetch_wv_quiz_raw.php?lang=fr&quiz=";

    public static final String FRENCH_WIKIVERSITY_LIST_OF_QUIZZES_URL =
        "http://occleve.berlios.de/fetch_wv_quiz_raw.php?lang=fr&quiz=ListOfQuizzesForMobileClient";

    public static final String WIKIVERSITY_QUIZ_URL_STUB =
       "http://occleve.berlios.de/fetch_wv_quiz_raw.php?lang=en&quiz=";
       /////"http://en.wikiversity.org/w/index.php?title=";

    public static final String WIKIVERSITY_QUIZ_URL_SUFFIX = ""; //"&action=raw";

    public static final String WIKIVERSITY_LIST_OF_QUIZZES_URL =
       "http://occleve.berlios.de/fetch_wv_quiz_raw.php?lang=en&quiz=ListOfQuizzesForMobileClient";
       ////"http://en.wikiversity.org/w/index.php?title=ListOfQuizzesForMobileClient&action=raw";

    public static final String WIKIVERSITY_QUIZ_TAG_STUB = "<quiz";

    ////////////////////////////////////////////////////////////////////
    // File extensions
    ////////////////////////////////////////////////////////////////////

    public static final String ADDITIONS_FILENAME_EXT = ".additions";

    public static final String OCCLEVE_XML_FILETYPE = ".oxml";
    public static final String WIKIVERSITY_FILETYPE = ".wvq";
    public static final String WIKIVERSITY_ADDITIONS_FILETYPE = ".wvq.additions";
    public static final String AUDIO_FILETYPE = ".mp3";
}

