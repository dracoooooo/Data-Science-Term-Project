package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class JanusForumGroupInfo extends WSObject
{
	
	private Integer _forumGroupId;
	public Integer getforumGroupId(){
		return _forumGroupId;
	}
	public void setforumGroupId(Integer value){
		_forumGroupId = value;
	}
	private String _forumGroupName;
	public String getforumGroupName(){
		return _forumGroupName;
	}
	public void setforumGroupName(String value){
		_forumGroupName = value;
	}
	private Integer _sortOrder;
	public Integer getsortOrder(){
		return _sortOrder;
	}
	public void setsortOrder(Integer value){
		_sortOrder = value;
	}
	
	public static JanusForumGroupInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		JanusForumGroupInfo result = new JanusForumGroupInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setforumGroupId(WSHelper.getInteger(root,"forumGroupId",false));
		this.setforumGroupName(WSHelper.getString(root,"forumGroupName",false));
		this.setsortOrder(WSHelper.getInteger(root,"sortOrder",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("JanusForumGroupInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"forumGroupId",String.valueOf(_forumGroupId),false);
		if(_forumGroupName != null)
			WSHelper.addChild(e,"forumGroupName",String.valueOf(_forumGroupName),false);
		WSHelper.addChild(e,"sortOrder",String.valueOf(_sortOrder),false);
	}
	
}
