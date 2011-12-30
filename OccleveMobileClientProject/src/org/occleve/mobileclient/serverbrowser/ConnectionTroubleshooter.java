/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2009-11  Joe Gittings

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
@version 0.9.10
*/

package org.occleve.mobileclient.serverbrowser;

import java.util.*;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

/**One connection troubleshooter to rule them all.
Lets you try all conceivable combinations of parameters.*/
public class ConnectionTroubleshooter extends Form
	implements Runnable,ActionListener
{
    protected Button m_TestItem = new Button("Test connection");
    protected String HTTP_CONNECTION = "HTTP connection";
    protected String SOCKET_CONNECTION = "Plain socket connection";
    protected ComboBox m_ConnectionTypeChoiceGroup;

    private ParamFieldCollection m_ParamFields;
    
    private class ParamField extends TextField
    {
    	private Label m_Label;
    	public Label getLabel() {return m_Label;}
    	public String getLabelText() {return m_Label.getText();}
    	
    	public ParamField(String sParamName,String sDefaultValue)
    	{
    		super(sDefaultValue);
    		
    		m_Label = new Label(sParamName);
    	}
    }

    private class ParamFieldCollection
    {
    	private Vector m_Fields = new Vector();
    	
    	public void add(String sParamName,String sDefaultValue)
    	{
    		ParamField field = new ParamField(sParamName,sDefaultValue);
    		m_Fields.addElement(field);
    		addComponent(field.getLabel());
    		addComponent(field);
    	}
    	
        public void setRequestProperties(HttpConnection cxn)
        throws Exception
        {
        	Enumeration e = m_Fields.elements();
        	while (e.hasMoreElements())
        	{
        		ParamField field = (ParamField)e.nextElement();
	        	String sParamValue = field.getText();
	        	if (sParamValue!=null)
	        	{
	        		if (sParamValue.length()!=0)
	        		{
	        			System.out.println("Setting " + field.getLabelText() + "=" + field.getText());
	        			cxn.setRequestProperty(field.getLabelText(),field.getText());
	        		}
	        	}
        	}
        }
    	
    }
    
    protected TextField m_URLField =
    	new TextField("http://occleve.berlios.de/wiki/index.php?title=ListOfTests&action=raw");

    /*
    protected ParamField m_ConnectionField = new ParamField("Connection","close");
    protected ParamField m_ContentLanguageField = new ParamField("Content-Language","en-US");
    protected ParamField m_ContentTypeField = new ParamField("Content-Type","text/html");
    protected ParamField m_UserAgentField =
    	new ParamField("User-Agent","Profile/MIDP-1.0 Configuration/CLDC-1.0");
    */

    protected Command m_OKCommand;
    protected Command m_CancelCommand;

    public ConnectionTroubleshooter() throws Exception
    {
        super(Constants.PRODUCT_NAME);

        m_OKCommand = new Command("OK");
        m_CancelCommand = new Command("Cancel");

        addCommand(m_OKCommand);
        addCommand(m_CancelCommand);

        // Append items to this form.

        addComponent(m_TestItem);
        m_TestItem.addActionListener(this);

        String[] cxnTypeChoices = {HTTP_CONNECTION,SOCKET_CONNECTION};
        m_ConnectionTypeChoiceGroup = new ComboBox(cxnTypeChoices);
        addComponent(m_ConnectionTypeChoiceGroup);

        addComponent(m_URLField);

        /*
        append(m_ConnectionField);
        append(m_ContentLanguageField);
        append(m_ContentTypeField);
        append(m_UserAgentField);
        */

        m_ParamFields = new ParamFieldCollection();

        // From http://java.sun.com/developer/J2METechTips/2001/tt0820.html#tip2:
        // "The MIDP Specification requires that you set the
        // User-Agent and Content-Language headers"
        m_ParamFields.add("User-Agent","Profile/MIDP-1.0 Configuration/CLDC-1.0");
        m_ParamFields.add("Content-Language","en-US");

        m_ParamFields.add("Accept","text/html" );
        m_ParamFields.add("Connection","close");
        m_ParamFields.add("Content-Type","text/html");

    }

    /**Implementation of CommandListener.*/
    public void actionCommand(Command c)
    {
        if (c==m_OKCommand)
        {
            try
            {
		        new Thread(this).start();
            }
            catch (Throwable t) {
            	OccleveMobileMidlet.getInstance().
            		onError("ConnectionTroubleshooter.commandAction",t);
            }
        }
        else if (c==m_CancelCommand)
        {
            OccleveMobileMidlet.getInstance().displayFileChooser();
        }
        else
        {
            OccleveMobileMidlet.getInstance().onError("Unknown command");
        }
    }

    /**Implementation of Runnable.*/
    public void run()
    {
    	try
    	{
    		runTest();
        }
        catch (Throwable t) {
        	OccleveMobileMidlet.getInstance().
        		onError("ConnectionTroubleshooter.run",t);
        }
    }

    protected void runTest() throws Exception
    {
        String sChoice = (String)m_ConnectionTypeChoiceGroup.getSelectedItem();

        if (sChoice.equals(HTTP_CONNECTION)) runHttpConnectionTest();
    }

    protected void runHttpConnectionTest() throws Exception
    {
    	String sURL = m_URLField.getText();

    	// Alert progressAlert = new Alert(null, "Connecting to " + sURL,null, null);
		// progressAlert.setTimeout(Alert.FOREVER);
		// StaticHelpers.safeAddGaugeToAlert(progressAlert);
		// Displayable previousDisplayable =
		// 	OccleveMobileMidlet.getInstance().getCurrentDisplayable();
		// OccleveMobileMidlet.getInstance().setCurrentForm(progressAlert);

    	ctAlert("Connecting to " + sURL);

    	HttpConnection cxn =
    		(HttpConnection)Connector.open(sURL,Connector.READ);
    	Thread.sleep(1000);

    	ctAlert("Setting request properties");
    	m_ParamFields.setRequestProperties(cxn);
    	/*
    	setRequestProperty(cxn,m_ConnectionField);
    	setRequestProperty(cxn,m_ContentLanguageField);
    	setRequestProperty(cxn,m_ContentTypeField);
    	setRequestProperty(cxn,m_UserAgentField);
    	*/
    	Thread.sleep(1000);

    	ctAlert("Getting response code");
        int rc = cxn.getResponseCode();
        String rm = cxn.getResponseMessage();
    	Thread.sleep(1000);

    	ctAlert("Response code=" + rc + ", response message=" + rm);
    	Thread.sleep(5000);

    	ctAlert("Closing connection");
        cxn.close();
    	Thread.sleep(1000);
        
        // OccleveMobileMidlet.getInstance().setCurrentForm(previousDisplayable);
    }

    /*
    protected void setRequestProperty(HttpConnection cxn,ParamField paramField)
    throws Exception
    {
    	String sParamValue = paramField.getString();
    	if (sParamValue!=null)
    	{
    		if (sParamValue.length()!=0)
    		{
    			cxn.setRequestProperty(paramField.getLabel(),paramField.getString());
    		}
    	}
    }
    */

    /**Implementation of ActionListener.*/
    public void actionPerformed(ActionEvent ae)
    {
        try
        {
            if (ae.getSource()==(Object)m_TestItem)
            {
		        new Thread(this).start();
            }
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }
    
    protected void ctAlert(String msg)
    {
    	Dialog.show(Constants.PRODUCT_NAME,msg,"OK","");
    }    
}

