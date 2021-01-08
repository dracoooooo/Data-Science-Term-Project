package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class TopicResponse extends WSObject
{
	
	private java.util.Vector<JanusMessageInfo> _Messages = new java.util.Vector<JanusMessageInfo>();
	public java.util.Vector<JanusMessageInfo> getMessages(){
		return _Messages;
	}
	public void setMessages(java.util.Vector<JanusMessageInfo> value){
		_Messages = value;
	}
	private java.util.Vector<JanusRatingInfo> _Rating = new java.util.Vector<JanusRatingInfo>();
	public java.util.Vector<JanusRatingInfo> getRating(){
		return _Rating;
	}
	public void setRating(java.util.Vector<JanusRatingInfo> value){
		_Rating = value;
	}
	private java.util.Vector<JanusModerateInfo> _Moderate = new java.util.Vector<JanusModerateInfo>();
	public java.util.Vector<JanusModerateInfo> getModerate(){
		return _Moderate;
	}
	public void setModerate(java.util.Vector<JanusModerateInfo> value){
		_Moderate = value;
	}
	
	public static TopicResponse loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		TopicResponse result = new TopicResponse();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		NodeList list;
		int i;
		list = WSHelper.getElementChildren(root, "Messages");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_Messages.addElement(JanusMessageInfo.loadFrom(nc));
			}
		}
		list = WSHelper.getElementChildren(root, "Rating");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_Rating.addElement(JanusRatingInfo.loadFrom(nc));
			}
		}
		list = WSHelper.getElementChildren(root, "Moderate");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_Moderate.addElement(JanusModerateInfo.loadFrom(nc));
			}
		}
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("TopicResponse");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_Messages != null)
			WSHelper.addChildArray(e,"Messages",null,null, _Messages);
		if(_Rating != null)
			WSHelper.addChildArray(e,"Rating",null,null, _Rating);
		if(_Moderate != null)
			WSHelper.addChildArray(e,"Moderate",null,null, _Moderate);
	}
	
}
