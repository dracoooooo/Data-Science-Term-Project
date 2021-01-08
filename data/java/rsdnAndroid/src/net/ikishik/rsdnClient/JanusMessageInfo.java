package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class JanusMessageInfo extends WSObject
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
	private Integer _parentId;
	public Integer getparentId(){
		return _parentId;
	}
	public void setparentId(Integer value){
		_parentId = value;
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
	private String _subject;
	public String getsubject(){
		return _subject;
	}
	public void setsubject(String value){
		_subject = value;
	}
	private String _messageName;
	public String getmessageName(){
		return _messageName;
	}
	public void setmessageName(String value){
		_messageName = value;
	}
	private String _userNick;
	public String getuserNick(){
		return _userNick;
	}
	public void setuserNick(String value){
		_userNick = value;
	}
	private String _message;
	public String getmessage(){
		return _message;
	}
	public void setmessage(String value){
		_message = value;
	}
	private Integer _articleId;
	public Integer getarticleId(){
		return _articleId;
	}
	public void setarticleId(Integer value){
		_articleId = value;
	}
	private java.util.Date _messageDate;
	public java.util.Date getmessageDate(){
		return _messageDate;
	}
	public void setmessageDate(java.util.Date value){
		_messageDate = value;
	}
	private java.util.Date _updateDate;
	public java.util.Date getupdateDate(){
		return _updateDate;
	}
	public void setupdateDate(java.util.Date value){
		_updateDate = value;
	}
	private String _userRole;
	public String getuserRole(){
		return _userRole;
	}
	public void setuserRole(String value){
		_userRole = value;
	}
	private String _userTitle;
	public String getuserTitle(){
		return _userTitle;
	}
	public void setuserTitle(String value){
		_userTitle = value;
	}
	private Integer _userTitleColor;
	public Integer getuserTitleColor(){
		return _userTitleColor;
	}
	public void setuserTitleColor(Integer value){
		_userTitleColor = value;
	}
	private java.util.Date _lastModerated;
	public java.util.Date getlastModerated(){
		return _lastModerated;
	}
	public void setlastModerated(java.util.Date value){
		_lastModerated = value;
	}
	private Boolean _closed;
	public Boolean getclosed(){
		return _closed;
	}
	public void setclosed(Boolean value){
		_closed = value;
	}
	
	public static JanusMessageInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		JanusMessageInfo result = new JanusMessageInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setmessageId(WSHelper.getInteger(root,"messageId",false));
		this.settopicId(WSHelper.getInteger(root,"topicId",false));
		this.setparentId(WSHelper.getInteger(root,"parentId",false));
		this.setuserId(WSHelper.getInteger(root,"userId",false));
		this.setforumId(WSHelper.getInteger(root,"forumId",false));
		this.setsubject(WSHelper.getString(root,"subject",false));
		this.setmessageName(WSHelper.getString(root,"messageName",false));
		this.setuserNick(WSHelper.getString(root,"userNick",false));
		this.setmessage(WSHelper.getString(root,"message",false));
		this.setarticleId(WSHelper.getInteger(root,"articleId",false));
		this.setmessageDate(WSHelper.getDate(root,"messageDate",false));
		this.setupdateDate(WSHelper.getDate(root,"updateDate",false));
		this.setuserRole(WSHelper.getString(root,"userRole",false));
		this.setuserTitle(WSHelper.getString(root,"userTitle",false));
		this.setuserTitleColor(WSHelper.getInteger(root,"userTitleColor",false));
		this.setlastModerated(WSHelper.getDate(root,"lastModerated",false));
		this.setclosed(WSHelper.getBoolean(root,"closed",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("JanusMessageInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"messageId",String.valueOf(_messageId),false);
		WSHelper.addChild(e,"topicId",String.valueOf(_topicId),false);
		WSHelper.addChild(e,"parentId",String.valueOf(_parentId),false);
		WSHelper.addChild(e,"userId",String.valueOf(_userId),false);
		WSHelper.addChild(e,"forumId",String.valueOf(_forumId),false);
		if(_subject != null)
			WSHelper.addChild(e,"subject",String.valueOf(_subject),false);
		if(_messageName != null)
			WSHelper.addChild(e,"messageName",String.valueOf(_messageName),false);
		if(_userNick != null)
			WSHelper.addChild(e,"userNick",String.valueOf(_userNick),false);
		if(_message != null)
			WSHelper.addChild(e,"message",String.valueOf(_message),false);
		WSHelper.addChild(e,"articleId",String.valueOf(_articleId),false);
		WSHelper.addChild(e,"messageDate",WSHelper.stringValueOf(_messageDate),false);
		WSHelper.addChild(e,"updateDate",WSHelper.stringValueOf(_updateDate),false);
		if(_userRole != null)
			WSHelper.addChild(e,"userRole",String.valueOf(_userRole),false);
		if(_userTitle != null)
			WSHelper.addChild(e,"userTitle",String.valueOf(_userTitle),false);
		WSHelper.addChild(e,"userTitleColor",String.valueOf(_userTitleColor),false);
		WSHelper.addChild(e,"lastModerated",WSHelper.stringValueOf(_lastModerated),false);
		WSHelper.addChild(e,"closed",(_closed ? "true" : "false"),false);
	}
	
}
