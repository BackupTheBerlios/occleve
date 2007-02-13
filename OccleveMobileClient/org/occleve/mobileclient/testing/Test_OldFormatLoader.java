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

package org.occleve.mobileclient.testing;

import java.util.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.qa.language.*;

public class Test_OldFormatLoader
{
    public static void oldFormatLoadQuestions(Test test, Vector vFileContents)
    throws Exception
    {
        VectorReader in = new VectorReader(vFileContents);
        String sPossibleQuestion;
        do
        {
            sPossibleQuestion = in.readLine();
            if (sPossibleQuestion != null)
            {
                if (sPossibleQuestion.startsWith("<zhqa>"))
                {
                    oldFormatLoadQuestions_DoOneLanguageQA(test, in);
                }
                else if (sPossibleQuestion.startsWith("Q"))
                {
                    String sQuestion = sPossibleQuestion.substring(1);
                    oldFormatLoadQuestions_DoOneQA(test, in, sQuestion);
                }
            }
        } while (sPossibleQuestion != null);
    }

    private static void oldFormatLoadQuestions_DoOneLanguageQA(Test test,
            VectorReader in) throws Exception
    {
        LanguageQA cqa = new LanguageQA(in);
        test.m_QAs.addElement(cqa);
    }

    private static void oldFormatLoadQuestions_DoOneQA(Test test,
            VectorReader in, String sQuestion) throws Exception
    {
        String sNextLine = in.readLine();
        QA theQA;
        if (sNextLine.startsWith("A"))
        {
            String sAnswer = sNextLine.substring(1);
            theQA = new PlainQA(sQuestion, sAnswer);
            test.m_QAs.addElement(theQA);
        }
        else
        {
            String sErr =
                    "Error reading file. Answer line:" + sNextLine +
                    "...does not start with A" +
                    "The corresponding question is:" + sQuestion;
            throw new Exception(sErr);
        }
    }
}

