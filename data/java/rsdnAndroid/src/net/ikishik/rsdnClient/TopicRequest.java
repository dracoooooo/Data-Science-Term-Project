package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class TopicRequest extends WSObject
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
	private java.util.Vector<Integer> _messageIds = new java.util.Vector<Integer>();
	public java.util.Vector<Integer> getmessageIds(){
		return _messageIds;
	}
	public void setmessageIds(java.util.Vector<Integer> value){
		_messageIds = value;
	}
	
	public static TopicRequest loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		TopicRequest result = new TopicRequest();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		NodeList list;
		int i;
		this.setuserName(WSHelper.getString(root,"userName",false));
		this.setpassword(WSHelper.getString(root,"password",false));
		list = WSHelper.getElementChildren(root, "messageIds");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_messageIds.addElement(WSHelper.getInteger(nc,null,false));
			}
		}
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("TopicRequest");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_userName != null)
			WSHelper.addChild(e,"userName",String.valueOf(_userName),false);
		if(_password != null)
			WSHelper.addChild(e,"password",String.valueOf(_password),false);
		if(_messageIds != null)
			WSHelper.addChildArray(e,null,"messageIds","int",_messageIds);
	}
	
}
