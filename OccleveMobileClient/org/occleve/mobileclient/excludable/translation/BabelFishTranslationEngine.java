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

package org.occleve.mobileclient.excludable.translation;

import org.occleve.mobileclient.*;

public class BabelFishTranslationEngine extends TranslationEngine
{
    public BabelFishTranslationEngine()
    {
        super();
    }

    protected String getTranslationWebsitePostURL()
    {
        return "http://babelfish.altavista.com/tr";
    }

    protected String getEnglishToChineseRawPostData(String sTranslateMe)
    {
        String params =
            "trtext=" + sTranslateMe + "&" +
            "lp=en_zh";
        return params;
    }

    protected String extractTranslation(StringBuffer sbReturnedPage)
    {
       String sSearchMe = sbReturnedPage.toString();
       System.out.println();
       System.out.println();
       System.out.println(sSearchMe);

       String sFindMe = "<input type=hidden name=\"q\" value=\"";
       System.out.println("sFindMe=" + sFindMe);

       int iIndex = sSearchMe.indexOf(sFindMe);
       System.out.println("iIndex = " + iIndex);

       String sRemainder = sSearchMe.substring(iIndex);
       System.out.println("sRemainder = " + sRemainder);

       int iClosingQuoteIndex = sRemainder.indexOf('"');
       String sTranslation = sRemainder.substring(0,iClosingQuoteIndex);
       return sTranslation;
    }

}

