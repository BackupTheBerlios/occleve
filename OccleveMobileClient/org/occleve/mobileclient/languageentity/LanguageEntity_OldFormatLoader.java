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

package org.occleve.mobileclient.languageentity;

import java.util.*;
import org.occleve.mobileclient.*;
import org.occleve.mobileclient.languageentity.chinese.*;

/**Static helper class for loading from old style ## format...
will be phased out.*/
public class LanguageEntity_OldFormatLoader
{
    // Fields for the old format
    protected static final String BP_FIELD_NAME = "BP=";
    protected static final String BC_FIELD_NAME = "BC=";
    protected static final String LIT_FIELD_NAME = "LIT=";

    public static void oldFormatLoad(LanguageEntity ent,String sSourceFileLine)
    throws Exception
    {
        Vector vTokens = StaticHelpers.tokenizeString(sSourceFileLine,"##");

        // The first token should be the type of LanguageEntity
        String sFirstToken = (String)vTokens.elementAt(0);

        if (sFirstToken.startsWith("EN"))
        {
            ent.m_sRomanForm = (String)vTokens.elementAt(1);
        }
        else if (sFirstToken.startsWith("ZH"))
        {
            // Discard the first token.
            vTokens.removeElementAt(0);

            // Process the fields in the CLE - measure word, body, etc.
            Enumeration e = vTokens.elements();
            while (e.hasMoreElements())
            {
                String sToken = (String) e.nextElement();
                oldFormatProcessField(ent,sToken);
            }
        }
        else
        {
            String sErr =
                    "Unknown entity type: " + sFirstToken +
                    " in source file line: " + sSourceFileLine;
            throw new Exception(sErr);
        }
    }

    private static void oldFormatProcessField(LanguageEntity ent,String s)
    throws Exception
    {
        if (s.startsWith(BP_FIELD_NAME))
            ent.m_sRomanForm = getRHS(s,BP_FIELD_NAME);
        else if (s.startsWith(BC_FIELD_NAME))
            ent.m_sNativeForm = getRHS(s,BC_FIELD_NAME);
        else if (s.startsWith(LIT_FIELD_NAME))
            ent.m_sLiteralTranslation = getRHS(s,LIT_FIELD_NAME);
        else if (ent instanceof ChineseCountableNoun)
        {
            // A bodge - but all this stuff is due to go anyway.
            ChineseCountableNoun ccn = (ChineseCountableNoun)ent;
            ccn.oldFormatProcessField(s);
        }
        else
        {
            String msg = "Unknown old format field: " + Constants.NEWLINE + s;
            throw new Exception(msg);
        }
    }

    public static String getRHS(String sLine,String sCode)
    {
        return sLine.substring( sCode.length() );
    }
}

