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

package org.occleve.mobileclient.qa.language;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.languageentity.*;
////import org.occleve.mobileclient.languageentity.chinese.*;

/**Static helper class - due to be phased out.*/
public class LanguageQA_OldFormatLoader
{
    /**Deprecated - loads from the old format file.*/
    public static void load(LanguageQA lqa,VectorReader in)
    throws Exception
    {
        boolean bContinue = true;
        while (bContinue)
        {
            bContinue = true;
            String s = in.readLine();

            if (s.startsWith("</zhqa>"))
            {
                // It's the end of the LanguageQA.
                bContinue = false;
            }
            else if (s.startsWith("EN"))
                oldFormat_ReadEnglishLanguageEntity(lqa,s);
            else if (s.startsWith("ZH"))
                oldFormat_ReadChineseLanguageEntity(lqa,s);
            else if (s.equals(Constants.LF) || s.equals(Constants.CRLF))
            {
                String sErr = "Blank line within zhqa";
                throw new Exception(sErr);
            }
            else if (s.startsWith("<zhqa>"))
            {
                String sErr = "Started another zhqa without closing the last one";
                throw new Exception(sErr);
            }
            else
            {
                String sErr = "Invalid line in chinese question:" +
                              Constants.NEWLINE + s;
                throw new Exception(sErr);
            }
        }
    }

    private static void oldFormat_ReadEnglishLanguageEntity(LanguageQA lqa,
                                                            String sLine)
    throws Exception
    {
        if (testEnglishField(lqa,sLine,"ENADJ##",XML.ADJECTIVE))
            return;
        else if (testEnglishField(lqa,sLine,"ENCNOUN##",XML.CNOUN))
            return;
        else if (testEnglishField(lqa,sLine,"ENPHRASE##",XML.PHRASE))
            return;
        else if (testEnglishField(lqa,sLine,"ENUNOUN##",XML.UNOUN))
            return;
        else if (testEnglishField(lqa,sLine,"ENVERB##",XML.VERB))
            return;
        else
        {
            String sErr = "Unknown english type code: " + sLine;
            throw new Exception(sErr);
        }
    }

    /**sXmlElementName is to enable automatic conversion to XML.*/
    private static boolean testEnglishField(LanguageQA lqa,String sLine,
                                            String sFieldCode,
                                            String sXmlElementName) throws
            Exception
    {
        if (sLine.startsWith(sFieldCode))
        {
            LanguageEntity langEnt = new LanguageEntity( sLine,sXmlElementName );
            lqa.m_vFirsteseEntities.addElement(langEnt);
            return true;
        }
        else
            return false;
    }

    private static void oldFormat_ReadChineseLanguageEntity(LanguageQA lqa,String s)
    throws Exception
    {
        LanguageEntity entity = null;

        if (s.startsWith("ZHCNOUN"))
            entity = new CountableNoun(s);
        else if (s.startsWith("ZHPNOUN"))
            entity = new ProperNoun(s);
        else if (s.startsWith("ZHUNOUN"))
            entity = new UncountableNoun(s);
        else if (s.startsWith("ZHADJ"))
            entity = new Adjective(s);
        else if (s.startsWith("ZHVERB"))
            entity = new Verb(s);
        else if (s.startsWith("ZHPHRASE"))
            entity = new Phrase(s);
        else
        {
            String sMsg =
               "Unrecognized LanguageEntity type in test file. " +
               "Source line is:" + Constants.NEWLINE + s;
            throw new Exception(sMsg);
        }

        lqa.m_vSecondeseEntities.addElement(entity);
    }

    /*
    protected String getRHS(String sLine,String sCode)
    {
        return sLine.substring( sCode.length() );
    }
    */
}
