package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class UserResponse extends WSObject
{
	
	private String _lastRowVersion;
	public String getlastRowVersion(){
		return _lastRowVersion;
	}
	public void setlastRowVersion(String value){
		_lastRowVersion = value;
	}
	private java.util.Vector<JanusUserInfo> _users = new java.util.Vector<JanusUserInfo>();
	public java.util.Vector<JanusUserInfo> getusers(){
		return _users;
	}
	public void setusers(java.util.Vector<JanusUserInfo> value){
		_users = value;
	}
	
	public static UserResponse loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		UserResponse result = new UserResponse();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		NodeList list;
		int i;
		this.setlastRowVersion(WSHelper.getString(root,"lastRowVersion",false));
		list = WSHelper.getElementChildren(root, "users");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_users.addElement(JanusUserInfo.loadFrom(nc));
			}
		}
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("UserResponse");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_lastRowVersion != null)
			WSHelper.addChild(e,"lastRowVersion",String.valueOf(_lastRowVersion),false);
		if(_users != null)
			WSHelper.addChildArray(e,"users",null,null, _users);
	}
	
}
