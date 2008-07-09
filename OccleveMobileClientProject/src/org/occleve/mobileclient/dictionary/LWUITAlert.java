package org.occleve.mobileclient.dictionary;

import com.sun.lwuit.*;

public class LWUITAlert extends Form
{
	private TextField m_TextField;
	
	public LWUITAlert(String sTitle,String sMessage)
	{
		m_TextField = new TextField(sMessage);
		addComponent(m_TextField);
	}

	public void setString(String s)
	{
		m_TextField.setText(s);
	}
}
