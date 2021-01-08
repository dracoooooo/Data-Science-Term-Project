package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class PostModerateInfo extends WSObject
{
	
	private Integer _LocalModerateId;
	public Integer getLocalModerateId(){
		return _LocalModerateId;
	}
	public void setLocalModerateId(Integer value){
		_LocalModerateId = value;
	}
	private Integer _MessageId;
	public Integer getMessageId(){
		return _MessageId;
	}
	public void setMessageId(Integer value){
		_MessageId = value;
	}
	private String _ModerateAction;
	public String getModerateAction(){
		return _ModerateAction;
	}
	public void setModerateAction(String value){
		_ModerateAction = value;
	}
	private Integer _ModerateToForumId;
	public Integer getModerateToForumId(){
		return _ModerateToForumId;
	}
	public void setModerateToForumId(Integer value){
		_ModerateToForumId = value;
	}
	private String _Description;
	public String getDescription(){
		return _Description;
	}
	public void setDescription(String value){
		_Description = value;
	}
	private Boolean _AsModerator;
	public Boolean getAsModerator(){
		return _AsModerator;
	}
	public void setAsModerator(Boolean value){
		_AsModerator = value;
	}
	
	public static PostModerateInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		PostModerateInfo result = new PostModerateInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setLocalModerateId(WSHelper.getInteger(root,"LocalModerateId",false));
		this.setMessageId(WSHelper.getInteger(root,"MessageId",false));
		this.setModerateAction(WSHelper.getString(root,"ModerateAction",false));
		this.setModerateToForumId(WSHelper.getInteger(root,"ModerateToForumId",false));
		this.setDescription(WSHelper.getString(root,"Description",false));
		this.setAsModerator(WSHelper.getBoolean(root,"AsModerator",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("PostModerateInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"LocalModerateId",String.valueOf(_LocalModerateId),false);
		WSHelper.addChild(e,"MessageId",String.valueOf(_MessageId),false);
		if(_ModerateAction != null)
			WSHelper.addChild(e,"ModerateAction",String.valueOf(_ModerateAction),false);
		WSHelper.addChild(e,"ModerateToForumId",String.valueOf(_ModerateToForumId),false);
		if(_Description != null)
			WSHelper.addChild(e,"Description",String.valueOf(_Description),false);
		WSHelper.addChild(e,"AsModerator",(_AsModerator ? "true" : "false"),false);
	}
	
}
