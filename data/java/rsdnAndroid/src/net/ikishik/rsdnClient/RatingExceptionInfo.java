package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class RatingExceptionInfo extends WSObject
{
	
	private String _exception;
	public String getexception(){
		return _exception;
	}
	public void setexception(String value){
		_exception = value;
	}
	private Integer _localRatingId;
	public Integer getlocalRatingId(){
		return _localRatingId;
	}
	public void setlocalRatingId(Integer value){
		_localRatingId = value;
	}
	private String _info;
	public String getinfo(){
		return _info;
	}
	public void setinfo(String value){
		_info = value;
	}
	
	public static RatingExceptionInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		RatingExceptionInfo result = new RatingExceptionInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setexception(WSHelper.getString(root,"exception",false));
		this.setlocalRatingId(WSHelper.getInteger(root,"localRatingId",false));
		this.setinfo(WSHelper.getString(root,"info",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("RatingExceptionInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_exception != null)
			WSHelper.addChild(e,"exception",String.valueOf(_exception),false);
		WSHelper.addChild(e,"localRatingId",String.valueOf(_localRatingId),false);
		if(_info != null)
			WSHelper.addChild(e,"info",String.valueOf(_info),false);
	}
	
}
