package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class JanusModerateInfo extends WSObject
{
	
	private Integer _messageId;
	public Integer getmessageId(){
		return _messageId;
	}
	public void setmessageId(Integer value){
		_messageId = value;
	}
	private Integer _topicId;
	public Integer gettopicId(){
		return _topicId;
	}
	public void settopicId(Integer value){
		_topicId = value;
	}
	private Integer _userId;
	public Integer getuserId(){
		return _userId;
	}
	public void setuserId(Integer value){
		_userId = value;
	}
	private Integer _forumId;
	public Integer getforumId(){
		return _forumId;
	}
	public void setforumId(Integer value){
		_forumId = value;
	}
	private java.util.Date _create;
	public java.util.Date getcreate(){
		return _create;
	}
	public void setcreate(java.util.Date value){
		_create = value;
	}
	
	public static JanusModerateInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		JanusModerateInfo result = new JanusModerateInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setmessageId(WSHelper.getInteger(root,"messageId",false));
		this.settopicId(WSHelper.getInteger(root,"topicId",false));
		this.setuserId(WSHelper.getInteger(root,"userId",false));
		this.setforumId(WSHelper.getInteger(root,"forumId",false));
		this.setcreate(WSHelper.getDate(root,"create",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("JanusModerateInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"messageId",String.valueOf(_messageId),false);
		WSHelper.addChild(e,"topicId",String.valueOf(_topicId),false);
		WSHelper.addChild(e,"userId",String.valueOf(_userId),false);
		WSHelper.addChild(e,"forumId",String.valueOf(_forumId),false);
		WSHelper.addChild(e,"create",WSHelper.stringValueOf(_create),false);
	}
	
}
