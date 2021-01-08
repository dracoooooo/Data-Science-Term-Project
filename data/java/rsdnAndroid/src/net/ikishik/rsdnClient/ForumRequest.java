package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class ForumRequest extends WSObject
{
	
	private String _userName;
	public String getuserName(){
		return _userName;
	}
	public void setuserName(String value){
		_userName = value;
	}
	private String _password;
	public String getpassword(){
		return _password;
	}
	public void setpassword(String value){
		_password = value;
	}
	private String _forumsRowVersion;
	public String getforumsRowVersion(){
		return _forumsRowVersion;
	}
	public void setforumsRowVersion(String value){
		_forumsRowVersion = value;
	}
	
	public static ForumRequest loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		ForumRequest result = new ForumRequest();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setuserName(WSHelper.getString(root,"userName",false));
		this.setpassword(WSHelper.getString(root,"password",false));
		this.setforumsRowVersion(WSHelper.getString(root,"forumsRowVersion",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("ForumRequest");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_userName != null)
			WSHelper.addChild(e,"userName",String.valueOf(_userName),false);
		if(_password != null)
			WSHelper.addChild(e,"password",String.valueOf(_password),false);
		if(_forumsRowVersion != null)
			WSHelper.addChild(e,"forumsRowVersion",String.valueOf(_forumsRowVersion),false);
	}
	
}
