package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class PostResponse extends WSObject
{
	
	private java.util.Vector<Integer> _commitedIds = new java.util.Vector<Integer>();
	public java.util.Vector<Integer> getcommitedIds(){
		return _commitedIds;
	}
	public void setcommitedIds(java.util.Vector<Integer> value){
		_commitedIds = value;
	}
	private java.util.Vector<PostExceptionInfo> _exceptions = new java.util.Vector<PostExceptionInfo>();
	public java.util.Vector<PostExceptionInfo> getexceptions(){
		return _exceptions;
	}
	public void setexceptions(java.util.Vector<PostExceptionInfo> value){
		_exceptions = value;
	}
	private java.util.Vector<Integer> _commitedRatingIds = new java.util.Vector<Integer>();
	public java.util.Vector<Integer> getcommitedRatingIds(){
		return _commitedRatingIds;
	}
	public void setcommitedRatingIds(java.util.Vector<Integer> value){
		_commitedRatingIds = value;
	}
	private java.util.Vector<RatingExceptionInfo> _ratingExceptions = new java.util.Vector<RatingExceptionInfo>();
	public java.util.Vector<RatingExceptionInfo> getratingExceptions(){
		return _ratingExceptions;
	}
	public void setratingExceptions(java.util.Vector<RatingExceptionInfo> value){
		_ratingExceptions = value;
	}
	private java.util.Vector<Integer> _commitedModerateIds = new java.util.Vector<Integer>();
	public java.util.Vector<Integer> getcommitedModerateIds(){
		return _commitedModerateIds;
	}
	public void setcommitedModerateIds(java.util.Vector<Integer> value){
		_commitedModerateIds = value;
	}
	private java.util.Vector<ModerateExceptionInfo> _moderateExceptions = new java.util.Vector<ModerateExceptionInfo>();
	public java.util.Vector<ModerateExceptionInfo> getmoderateExceptions(){
		return _moderateExceptions;
	}
	public void setmoderateExceptions(java.util.Vector<ModerateExceptionInfo> value){
		_moderateExceptions = value;
	}
	
	public static PostResponse loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		PostResponse result = new PostResponse();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		NodeList list;
		int i;
		list = WSHelper.getElementChildren(root, "commitedIds");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_commitedIds.addElement(WSHelper.getInteger(nc,null,false));
			}
		}
		list = WSHelper.getElementChildren(root, "exceptions");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_exceptions.addElement(PostExceptionInfo.loadFrom(nc));
			}
		}
		list = WSHelper.getElementChildren(root, "commitedRatingIds");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_commitedRatingIds.addElement(WSHelper.getInteger(nc,null,false));
			}
		}
		list = WSHelper.getElementChildren(root, "ratingExceptions");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_ratingExceptions.addElement(RatingExceptionInfo.loadFrom(nc));
			}
		}
		list = WSHelper.getElementChildren(root, "commitedModerateIds");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_commitedModerateIds.addElement(WSHelper.getInteger(nc,null,false));
			}
		}
		list = WSHelper.getElementChildren(root, "moderateExceptions");
		if(list != null)
		{
			for(i=0;i<list.getLength();i++)
			{
				Element nc = (Element)list.item(i);
				_moderateExceptions.addElement(ModerateExceptionInfo.loadFrom(nc));
			}
		}
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("PostResponse");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_commitedIds != null)
			WSHelper.addChildArray(e,null,"commitedIds","int",_commitedIds);
		if(_exceptions != null)
			WSHelper.addChildArray(e,"exceptions",null,null, _exceptions);
		if(_commitedRatingIds != null)
			WSHelper.addChildArray(e,null,"commitedRatingIds","int",_commitedRatingIds);
		if(_ratingExceptions != null)
			WSHelper.addChildArray(e,"ratingExceptions",null,null, _ratingExceptions);
		if(_commitedModerateIds != null)
			WSHelper.addChildArray(e,null,"commitedModerateIds","int",_commitedModerateIds);
		if(_moderateExceptions != null)
			WSHelper.addChildArray(e,"moderateExceptions",null,null, _moderateExceptions);
	}
	
}
