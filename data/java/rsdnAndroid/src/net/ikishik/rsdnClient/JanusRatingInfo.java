package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class JanusRatingInfo extends WSObject
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
	private Integer _userId;
	public Integer getuserId(){
		return _userId;
	}
	public void setuserId(Integer value){
		_userId = value;
	}
	private Integer _userRating;
	public Integer getuserRating(){
		return _userRating;
	}
	public void setuserRating(Integer value){
		_userRating = value;
	}
	private Integer _rate;
	public Integer getrate(){
		return _rate;
	}
	public void setrate(Integer value){
		_rate = value;
	}
	private java.util.Date _rateDate;
	public java.util.Date getrateDate(){
		return _rateDate;
	}
	public void setrateDate(java.util.Date value){
		_rateDate = value;
	}
	
	public static JanusRatingInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		JanusRatingInfo result = new JanusRatingInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setmessageId(WSHelper.getInteger(root,"messageId",false));
		this.settopicId(WSHelper.getInteger(root,"topicId",false));
		this.setuserId(WSHelper.getInteger(root,"userId",false));
		this.setuserRating(WSHelper.getInteger(root,"userRating",false));
		this.setrate(WSHelper.getInteger(root,"rate",false));
		this.setrateDate(WSHelper.getDate(root,"rateDate",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("JanusRatingInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"messageId",String.valueOf(_messageId),false);
		WSHelper.addChild(e,"topicId",String.valueOf(_topicId),false);
		WSHelper.addChild(e,"userId",String.valueOf(_userId),false);
		WSHelper.addChild(e,"userRating",String.valueOf(_userRating),false);
		WSHelper.addChild(e,"rate",String.valueOf(_rate),false);
		WSHelper.addChild(e,"rateDate",WSHelper.stringValueOf(_rateDate),false);
	}
	
}
