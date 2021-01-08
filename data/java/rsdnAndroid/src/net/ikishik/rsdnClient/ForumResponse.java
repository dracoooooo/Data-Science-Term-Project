package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class ForumResponse extends WSObject
{
	
	private java.util.Vector<JanusForumInfo> _forumList = new java.util.Vector<JanusForumInfo>();
	public java.util.Vector<JanusForumInfo> getforumList(){
		return _forumList;
	}
	public void setforumList(java.util.Vector<JanusForumInfo> value){
		_forumList = value;
	}
	private java.util.Vector<JanusForumGroupInfo> _groupList = new java.util.Vector<JanusForumGroupInfo>();
	public java.util.Vector<JanusForumGroupInfo> getgroupList(){
		return _groupList;
	}
	public void setgroupList(java.util.Vector<JanusForumGroupInfo> value){
		_groupList = value;
	}
	private String _forumsRowVersion;
	public String getforumsRowVersion(){
		return _forumsRowVersion;
	}
	public void setforumsRowVersion(String value){
		_forumsRowVersion = value;
	}
	
	public static ForumResponse loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		ForumResponse result = new ForumResponse();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		NodeList list;
		int i;
		list = WSHelper.getElementChildren(root, "forumList");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_forumList.addElement(JanusForumInfo.loadFrom(nc));
			}
		}
		list = WSHelper.getElementChildren(root, "groupList");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_groupList.addElement(JanusForumGroupInfo.loadFrom(nc));
			}
		}
		this.setforumsRowVersion(WSHelper.getString(root,"forumsRowVersion",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("ForumResponse");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_forumList != null)
			WSHelper.addChildArray(e,"forumList",null,null, _forumList);
		if(_groupList != null)
			WSHelper.addChildArray(e,"groupList",null,null, _groupList);
		if(_forumsRowVersion != null)
			WSHelper.addChild(e,"forumsRowVersion",String.valueOf(_forumsRowVersion),false);
	}
	
}
