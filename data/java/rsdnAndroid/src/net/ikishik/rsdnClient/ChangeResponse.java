package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class ChangeResponse extends WSObject
{
	
	private java.util.Vector<JanusMessageInfo> _newMessages = new java.util.Vector<JanusMessageInfo>();
	public java.util.Vector<JanusMessageInfo> getnewMessages(){
		return _newMessages;
	}
	public void setnewMessages(java.util.Vector<JanusMessageInfo> value){
		_newMessages = value;
	}
	private java.util.Vector<JanusRatingInfo> _newRating = new java.util.Vector<JanusRatingInfo>();
	public java.util.Vector<JanusRatingInfo> getnewRating(){
		return _newRating;
	}
	public void setnewRating(java.util.Vector<JanusRatingInfo> value){
		_newRating = value;
	}
	private java.util.Vector<JanusModerateInfo> _newModerate = new java.util.Vector<JanusModerateInfo>();
	public java.util.Vector<JanusModerateInfo> getnewModerate(){
		return _newModerate;
	}
	public void setnewModerate(java.util.Vector<JanusModerateInfo> value){
		_newModerate = value;
	}
	private String _lastRatingRowVersion;
	public String getlastRatingRowVersion(){
		return _lastRatingRowVersion;
	}
	public void setlastRatingRowVersion(String value){
		_lastRatingRowVersion = value;
	}
	private String _lastForumRowVersion;
	public String getlastForumRowVersion(){
		return _lastForumRowVersion;
	}
	public void setlastForumRowVersion(String value){
		_lastForumRowVersion = value;
	}
	private String _lastModerateRowVersion;
	public String getlastModerateRowVersion(){
		return _lastModerateRowVersion;
	}
	public void setlastModerateRowVersion(String value){
		_lastModerateRowVersion = value;
	}
	private Integer _userId;
	public Integer getuserId(){
		return _userId;
	}
	public void setuserId(Integer value){
		_userId = value;
	}
	
	public static ChangeResponse loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		ChangeResponse result = new ChangeResponse();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		NodeList list;
		int i;
		list = WSHelper.getElementChildren(root, "newMessages");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_newMessages.addElement(JanusMessageInfo.loadFrom(nc));
			}
		}
		list = WSHelper.getElementChildren(root, "newRating");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_newRating.addElement(JanusRatingInfo.loadFrom(nc));
			}
		}
		list = WSHelper.getElementChildren(root, "newModerate");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_newModerate.addElement(JanusModerateInfo.loadFrom(nc));
			}
		}
		this.setlastRatingRowVersion(WSHelper.getString(root,"lastRatingRowVersion",false));
		this.setlastForumRowVersion(WSHelper.getString(root,"lastForumRowVersion",false));
		this.setlastModerateRowVersion(WSHelper.getString(root,"lastModerateRowVersion",false));
		this.setuserId(WSHelper.getInteger(root,"userId",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("ChangeResponse");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_newMessages != null)
			WSHelper.addChildArray(e,"newMessages",null,null, _newMessages);
		if(_newRating != null)
			WSHelper.addChildArray(e,"newRating",null,null, _newRating);
		if(_newModerate != null)
			WSHelper.addChildArray(e,"newModerate",null,null, _newModerate);
		if(_lastRatingRowVersion != null)
			WSHelper.addChild(e,"lastRatingRowVersion",String.valueOf(_lastRatingRowVersion),false);
		if(_lastForumRowVersion != null)
			WSHelper.addChild(e,"lastForumRowVersion",String.valueOf(_lastForumRowVersion),false);
		if(_lastModerateRowVersion != null)
			WSHelper.addChild(e,"lastModerateRowVersion",String.valueOf(_lastModerateRowVersion),false);
		WSHelper.addChild(e,"userId",String.valueOf(_userId),false);
	}
	
}
