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

package org.occleve.mobileclient.qa.wikiversity;

/**Encapsulates a response in a wikiversity quiz question.*/
public class WikiversityAnswer
{
	private String m_sAnswer;
	
	private String m_sFeedback;
	
	private Boolean m_bCorrect;
	
	public WikiversityAnswer(String sAnswer)
	{
		m_sAnswer = sAnswer;
	}
	
	public String getAnswer() {return m_sAnswer;}

	public Boolean getCorrect() {return m_bCorrect;}
	
	public String getFeedback() {return m_sFeedback;}
	
	public void setFeedback(String s) {m_sFeedback = s;}
	
	public void setCorrect(boolean bCorrect)
	{
		m_bCorrect = new Boolean(bCorrect);
	}
}
