package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class JanusForumInfo extends WSObject
{
	
	private Integer _forumId;
	public Integer getforumId(){
		return _forumId;
	}
	public void setforumId(Integer value){
		_forumId = value;
	}
	private Integer _forumGroupId;
	public Integer getforumGroupId(){
		return _forumGroupId;
	}
	public void setforumGroupId(Integer value){
		_forumGroupId = value;
	}
	private String _shortForumName;
	public String getshortForumName(){
		return _shortForumName;
	}
	public void setshortForumName(String value){
		_shortForumName = value;
	}
	private String _forumName;
	public String getforumName(){
		return _forumName;
	}
	public void setforumName(String value){
		_forumName = value;
	}
	private Integer _rated;
	public Integer getrated(){
		return _rated;
	}
	public void setrated(Integer value){
		_rated = value;
	}
	private Integer _inTop;
	public Integer getinTop(){
		return _inTop;
	}
	public void setinTop(Integer value){
		_inTop = value;
	}
	private Integer _rateLimit;
	public Integer getrateLimit(){
		return _rateLimit;
	}
	public void setrateLimit(Integer value){
		_rateLimit = value;
	}
	
	public static JanusForumInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		JanusForumInfo result = new JanusForumInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setforumId(WSHelper.getInteger(root,"forumId",false));
		this.setforumGroupId(WSHelper.getInteger(root,"forumGroupId",false));
		this.setshortForumName(WSHelper.getString(root,"shortForumName",false));
		this.setforumName(WSHelper.getString(root,"forumName",false));
		this.setrated(WSHelper.getInteger(root,"rated",false));
		this.setinTop(WSHelper.getInteger(root,"inTop",false));
		this.setrateLimit(WSHelper.getInteger(root,"rateLimit",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("JanusForumInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"forumId",String.valueOf(_forumId),false);
		WSHelper.addChild(e,"forumGroupId",String.valueOf(_forumGroupId),false);
		if(_shortForumName != null)
			WSHelper.addChild(e,"shortForumName",String.valueOf(_shortForumName),false);
		if(_forumName != null)
			WSHelper.addChild(e,"forumName",String.valueOf(_forumName),false);
		WSHelper.addChild(e,"rated",String.valueOf(_rated),false);
		WSHelper.addChild(e,"inTop",String.valueOf(_inTop),false);
		WSHelper.addChild(e,"rateLimit",String.valueOf(_rateLimit),false);
	}
	
}
