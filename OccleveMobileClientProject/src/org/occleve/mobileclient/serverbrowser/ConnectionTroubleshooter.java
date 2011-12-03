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
import javax.microedition.lcdui.*;

import org.occleve.mobileclient.*;
import org.occleve.mobileclient.qa.*;
import org.occleve.mobileclient.testing.*;
import org.occleve.mobileclient.testing.test.*;

/**One connection troubleshooter to rule them all.
Lets you try all conceivable combinations of parameters.*/
public class ConnectionTroubleshooter extends Form
implements CommandListener,ItemCommandListener,Runnable
{
    protected StringItem m_TestItem =
            new StringItem(null,"Test connection",Item.BUTTON);

    protected String HTTP_CONNECTION = "HTTP connection";
    protected String SOCKET_CONNECTION = "Plain socket connection";
    protected ChoiceGroup m_ConnectionTypeChoiceGroup;

    private ParamFieldCollection m_ParamFields;
    
    private class ParamField extends TextField
    {
    	public ParamField(String sParamName,String sDefaultValue)
    	{
    		super(sParamName,sDefaultValue,100,TextField.ANY);
    	}
    }

    private class ParamFieldCollection
    {
    	private Vector m_Fields = new Vector();
    	
    	public void add(String sParamName,String sDefaultValue)
    	{
    		ParamField field = new ParamField(sParamName,sDefaultValue);
    		m_Fields.addElement(field);
    		append(field);
    	}
    	
        public void setRequestProperties(HttpConnection cxn)
        throws Exception
        {
        	Enumeration e = m_Fields.elements();
        	while (e.hasMoreElements())
        	{
        		ParamField field = (ParamField)e.nextElement();
	        	String sParamValue = field.getString();
	        	if (sParamValue!=null)
	        	{
	        		if (sParamValue.length()!=0)
	        		{
	        			System.out.println("Setting " + field.getLabel() + "=" + field.getString());
	        			cxn.setRequestProperty(field.getLabel(),field.getString());
	        		}
	        	}
        	}
        }
    	
    }
    
    protected TextField m_URLField =
    	new TextField("URL","http://occleve.berlios.de/wiki/index.php?title=ListOfTests&action=raw",100,TextField.ANY);

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

        m_OKCommand = new Command("OK",Command.OK,0);
        m_CancelCommand = new Command("Cancel",Command.CANCEL,0);

        addCommand(m_OKCommand);
        addCommand(m_CancelCommand);
        setCommandListener(this);

        // Append items to this form.

        append(m_TestItem);
        m_TestItem.setItemCommandListener(this);
        m_TestItem.setDefaultCommand(m_OKCommand);

        String[] cxnTypeChoices = {HTTP_CONNECTION,SOCKET_CONNECTION};
        m_ConnectionTypeChoiceGroup =
            new ChoiceGroup(null,ChoiceGroup.POPUP,cxnTypeChoices,null);
        append(m_ConnectionTypeChoiceGroup);

        append(m_URLField);

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
    public void commandAction(Command c, Displayable s)
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
        int i = m_ConnectionTypeChoiceGroup.getSelectedIndex();
        String sChoice = m_ConnectionTypeChoiceGroup.getString(i);

        if (sChoice.equals(HTTP_CONNECTION)) runHttpConnectionTest();
    }

    protected void runHttpConnectionTest() throws Exception
    {
    	String sURL = m_URLField.getString();

    	Alert progressAlert = new Alert(null, "Connecting to " + sURL,null, null);
		progressAlert.setTimeout(Alert.FOREVER);
		StaticHelpers.safeAddGaugeToAlert(progressAlert);
		Displayable previousDisplayable =
			OccleveMobileMidlet.getInstance().getCurrentDisplayable();
		OccleveMobileMidlet.getInstance().setCurrentForm(progressAlert);
    	
    	HttpConnection cxn =
    		(HttpConnection)Connector.open(sURL,Connector.READ);
    	Thread.sleep(1000);

    	progressAlert.setString("Setting request properties");
    	m_ParamFields.setRequestProperties(cxn);
    	/*
    	setRequestProperty(cxn,m_ConnectionField);
    	setRequestProperty(cxn,m_ContentLanguageField);
    	setRequestProperty(cxn,m_ContentTypeField);
    	setRequestProperty(cxn,m_UserAgentField);
    	*/
    	Thread.sleep(1000);

    	progressAlert.setString("Getting response code");
        int rc = cxn.getResponseCode();
        String rm = cxn.getResponseMessage();
    	Thread.sleep(1000);

    	progressAlert.setString("Response code=" + rc + ", response message=" + rm);
    	Thread.sleep(5000);

    	progressAlert.setString("Closing connection");
        cxn.close();
    	Thread.sleep(1000);
        
        OccleveMobileMidlet.getInstance().setCurrentForm(previousDisplayable);
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
    
    /*Implementation of ItemCommandListener.*/
    public void commandAction(Command c, Item item)
    {
        try
        {
            if (item==m_TestItem)
            {
		        new Thread(this).start();
            }
        }
        catch (Exception e) {OccleveMobileMidlet.getInstance().onError(e);}
    }
}

