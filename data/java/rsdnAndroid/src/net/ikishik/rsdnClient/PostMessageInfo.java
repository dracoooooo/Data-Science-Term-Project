package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class PostMessageInfo extends WSObject
{
	
	private Integer _localMessageId;
	public Integer getlocalMessageId(){
		return _localMessageId;
	}
	public void setlocalMessageId(Integer value){
		_localMessageId = value;
	}
	private Integer _parentId;
	public Integer getparentId(){
		return _parentId;
	}
	public void setparentId(Integer value){
		_parentId = value;
	}
	private Integer _forumId;
	public Integer getforumId(){
		return _forumId;
	}
	public void setforumId(Integer value){
		_forumId = value;
	}
	private String _subject;
	public String getsubject(){
		return _subject;
	}
	public void setsubject(String value){
		_subject = value;
	}
	private String _message;
	public String getmessage(){
		return _message;
	}
	public void setmessage(String value){
		_message = value;
	}
	
	public static PostMessageInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		PostMessageInfo result = new PostMessageInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setlocalMessageId(WSHelper.getInteger(root,"localMessageId",false));
		this.setparentId(WSHelper.getInteger(root,"parentId",false));
		this.setforumId(WSHelper.getInteger(root,"forumId",false));
		this.setsubject(WSHelper.getString(root,"subject",false));
		this.setmessage(WSHelper.getString(root,"message",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("PostMessageInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"localMessageId",String.valueOf(_localMessageId),false);
		WSHelper.addChild(e,"parentId",String.valueOf(_parentId),false);
		WSHelper.addChild(e,"forumId",String.valueOf(_forumId),false);
		if(_subject != null)
			WSHelper.addChild(e,"subject",String.valueOf(_subject),false);
		if(_message != null)
			WSHelper.addChild(e,"message",String.valueOf(_message),false);
	}
	
}
