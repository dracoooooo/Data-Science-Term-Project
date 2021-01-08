package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class RequestForumInfo extends WSObject
{
	
	private Integer _forumId;
	public Integer getforumId(){
		return _forumId;
	}
	public void setforumId(Integer value){
		_forumId = value;
	}
	private Boolean _isFirstRequest;
	public Boolean getisFirstRequest(){
		return _isFirstRequest;
	}
	public void setisFirstRequest(Boolean value){
		_isFirstRequest = value;
	}
	
	public static RequestForumInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		RequestForumInfo result = new RequestForumInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setforumId(WSHelper.getInteger(root,"forumId",false));
		this.setisFirstRequest(WSHelper.getBoolean(root,"isFirstRequest",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("RequestForumInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"forumId",String.valueOf(_forumId),false);
		WSHelper.addChild(e,"isFirstRequest",(_isFirstRequest ? "true" : "false"),false);
	}
	
}
