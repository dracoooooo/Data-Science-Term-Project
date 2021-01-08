package net.ikishik.rsdnClient;
import org.apache.commons.codec.binary.Base64;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class ChangeRequest extends WSObject
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
	private java.util.Vector<RequestForumInfo> _subscribedForums = new java.util.Vector<RequestForumInfo>();
	public java.util.Vector<RequestForumInfo> getsubscribedForums(){
		return _subscribedForums;
	}
	public void setsubscribedForums(java.util.Vector<RequestForumInfo> value){
		_subscribedForums = value;
	}
	private byte[] _ratingRowVersion;
	public byte[] getratingRowVersion(){
		return _ratingRowVersion;
	}
	public void setratingRowVersion(byte[] value){
		_ratingRowVersion = value;
	}
	private byte[] _messageRowVersion;
	public byte[] getmessageRowVersion(){
		return _messageRowVersion;
	}
	public void setmessageRowVersion(byte[] value){
		_messageRowVersion = value;
	}
	private byte[] _moderateRowVersion;
	public byte[] getmoderateRowVersion(){
		return _moderateRowVersion;
	}
	public void setmoderateRowVersion(byte[] value){
		_moderateRowVersion = value;
	}
	private java.util.Vector<Integer> _breakMsgIds = new java.util.Vector<Integer>();
	public java.util.Vector<Integer> getbreakMsgIds(){
		return _breakMsgIds;
	}
	public void setbreakMsgIds(java.util.Vector<Integer> value){
		_breakMsgIds = value;
	}
	private java.util.Vector<Integer> _breakTopicIds = new java.util.Vector<Integer>();
	public java.util.Vector<Integer> getbreakTopicIds(){
		return _breakTopicIds;
	}
	public void setbreakTopicIds(java.util.Vector<Integer> value){
		_breakTopicIds = value;
	}
	private Integer _maxOutput;
	public Integer getmaxOutput(){
		return _maxOutput;
	}
	public void setmaxOutput(Integer value){
		_maxOutput = value;
	}
	
	public static ChangeRequest loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		ChangeRequest result = new ChangeRequest();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		NodeList list;
		int i;
		this.setuserName(WSHelper.getString(root,"userName",false));
		this.setpassword(WSHelper.getString(root,"password",false));
		list = WSHelper.getElementChildren(root, "subscribedForums");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_subscribedForums.addElement(RequestForumInfo.loadFrom(nc));
			}
		}
		byte[] rrw = Base64.decodeBase64(WSHelper.getString(root,"ratingRowVersion",false));
		this.setratingRowVersion(rrw);
		
		byte[] mrw = Base64.decodeBase64(WSHelper.getString(root,"messageRowVersion",false));
		this.setmessageRowVersion(mrw);
		
		byte[] morw = Base64.decodeBase64(WSHelper.getString(root,"moderateRowVersion",false));
		this.setmoderateRowVersion(morw);
		
		list = WSHelper.getElementChildren(root, "breakMsgIds");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_breakMsgIds.addElement(WSHelper.getInteger(nc,null,false));
			}
		}
		list = WSHelper.getElementChildren(root, "breakTopicIds");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_breakTopicIds.addElement(WSHelper.getInteger(nc,null,false));
			}
		}
		this.setmaxOutput(WSHelper.getInteger(root,"maxOutput",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("ChangeRequest");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_userName != null)
			WSHelper.addChild(e,"userName",String.valueOf(_userName),false);
		if(_password != null)
			WSHelper.addChild(e,"password",String.valueOf(_password),false);
		if(_subscribedForums != null)
			WSHelper.addChildArray(e,null,"subscribedForums",null, _subscribedForums);
		if(_ratingRowVersion != null)
		{
			String value = new String(Base64.encodeBase64(_ratingRowVersion));
			WSHelper.addChild(e,"ratingRowVersion",value,false);
		}
		if(_messageRowVersion != null)
		{
			String value = new String(Base64.encodeBase64(_messageRowVersion));
			WSHelper.addChild(e,"messageRowVersion",value,false);
		}
		if(_moderateRowVersion != null)
		{
			String value = new String(Base64.encodeBase64(_moderateRowVersion));
			WSHelper.addChild(e,"moderateRowVersion",value,false);
		}
		if(_breakMsgIds != null)
			WSHelper.addChildArray(e,null,"breakMsgIds","int",_breakMsgIds);
		if(_breakTopicIds != null)
			WSHelper.addChildArray(e,null,"breakTopicIds","int",_breakTopicIds);
		WSHelper.addChild(e,"maxOutput",String.valueOf(_maxOutput),false);
	}
	
}
