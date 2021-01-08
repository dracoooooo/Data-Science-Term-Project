package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class PostRequest extends WSObject
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
	private java.util.Vector<PostMessageInfo> _writedMessages = new java.util.Vector<PostMessageInfo>();
	public java.util.Vector<PostMessageInfo> getwritedMessages(){
		return _writedMessages;
	}
	public void setwritedMessages(java.util.Vector<PostMessageInfo> value){
		_writedMessages = value;
	}
	private java.util.Vector<PostRatingInfo> _rates = new java.util.Vector<PostRatingInfo>();
	public java.util.Vector<PostRatingInfo> getrates(){
		return _rates;
	}
	public void setrates(java.util.Vector<PostRatingInfo> value){
		_rates = value;
	}
	private java.util.Vector<PostModerateInfo> _moderates = new java.util.Vector<PostModerateInfo>();
	public java.util.Vector<PostModerateInfo> getmoderates(){
		return _moderates;
	}
	public void setmoderates(java.util.Vector<PostModerateInfo> value){
		_moderates = value;
	}
	
	public static PostRequest loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		PostRequest result = new PostRequest();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		NodeList list;
		int i;
		this.setuserName(WSHelper.getString(root,"userName",false));
		this.setpassword(WSHelper.getString(root,"password",false));
		list = WSHelper.getElementChildren(root, "writedMessages");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_writedMessages.addElement(PostMessageInfo.loadFrom(nc));
			}
		}
		list = WSHelper.getElementChildren(root, "rates");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_rates.addElement(PostRatingInfo.loadFrom(nc));
			}
		}
		list = WSHelper.getElementChildren(root, "moderates");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_moderates.addElement(PostModerateInfo.loadFrom(nc));
			}
		}
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("PostRequest");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_userName != null)
			WSHelper.addChild(e,"userName",String.valueOf(_userName),false);
		if(_password != null)
			WSHelper.addChild(e,"password",String.valueOf(_password),false);
		if(_writedMessages != null)
			WSHelper.addChildArray(e,"writedMessages",null,null, _writedMessages);
		if(_rates != null)
			WSHelper.addChildArray(e,"rates",null,null, _rates);
		if(_moderates != null)
			WSHelper.addChildArray(e,"moderates",null,null, _moderates);
	}
	
}
