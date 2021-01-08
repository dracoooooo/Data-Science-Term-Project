package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class PostRatingInfo extends WSObject
{
	
	private Integer _localRatingId;
	public Integer getlocalRatingId(){
		return _localRatingId;
	}
	public void setlocalRatingId(Integer value){
		_localRatingId = value;
	}
	private Integer _messageId;
	public Integer getmessageId(){
		return _messageId;
	}
	public void setmessageId(Integer value){
		_messageId = value;
	}
	private Integer _rate;
	public Integer getrate(){
		return _rate;
	}
	public void setrate(Integer value){
		_rate = value;
	}
	
	public static PostRatingInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		PostRatingInfo result = new PostRatingInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setlocalRatingId(WSHelper.getInteger(root,"localRatingId",false));
		this.setmessageId(WSHelper.getInteger(root,"messageId",false));
		this.setrate(WSHelper.getInteger(root,"rate",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("PostRatingInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"localRatingId",String.valueOf(_localRatingId),false);
		WSHelper.addChild(e,"messageId",String.valueOf(_messageId),false);
		WSHelper.addChild(e,"rate",String.valueOf(_rate),false);
	}
	
}
