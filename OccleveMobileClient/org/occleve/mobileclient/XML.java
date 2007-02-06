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

public class XML
{
    public static final String TEST = "Test";
    public static final String QA = "QA";

    public static final String EN = "EN";
    public static final String EN_CLOSE = "/EN";

    public static final String ZH = "ZH";
    public static final String ZH_CLOSE = "/ZH";
    public static final String ZH_CNOUN = "ZhCNoun";

    public static final String ROMAN = "Roman";
    public static final String SCRIPT = "Script";
    public static final String LITERAL = "LiteralTranslation";

    public static final String CNOUN = "CNoun";
    public static final String PNOUN = "PNoun";
    public static final String UNOUN = "UNoun";

    public static final String ADJECTIVE = "Adjective";
    public static final String ADVERB = "Adverb";
    public static final String EXAMPLE = "Example";
    public static final String LINKWORD = "LinkWord";
    public static final String OTHER = "Other";
    public static final String PARTICLE = "Particle";
    public static final String PHRASE = "Phrase";
    public static final String PREPOSITION = "Preposition";
    public static final String UNKNOWN = "Unknown";
    public static final String VERB = "Verb";

    public static boolean isOpeningTag(String sTestMe,String sTagName)
    {
        String sLowerTag = sTagName.toLowerCase();
        return (sTestMe.toLowerCase().equals( sLowerTag ));
    }

    public static boolean isClosingTag(String sTestMe,String sTagName)
    {
        String sCloser = "/" + sTagName.toLowerCase();
        return (sTestMe.toLowerCase().equals(sCloser));
    }

    public static void appendStartTag(StringBuffer sb,String sTagName)
    {
        sb.append("<"+sTagName+">");
    }

    public static void appendEndTag(StringBuffer sb,String sTagName)
    {
        sb.append("</"+sTagName+">");
    }
}

