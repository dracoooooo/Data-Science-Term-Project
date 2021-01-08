package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class PostExceptionInfo extends WSObject
{
	
	private String _exception;
	public String getexception(){
		return _exception;
	}
	public void setexception(String value){
		_exception = value;
	}
	private Integer _localMessageId;
	public Integer getlocalMessageId(){
		return _localMessageId;
	}
	public void setlocalMessageId(Integer value){
		_localMessageId = value;
	}
	private String _info;
	public String getinfo(){
		return _info;
	}
	public void setinfo(String value){
		_info = value;
	}
	
	public static PostExceptionInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		PostExceptionInfo result = new PostExceptionInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setexception(WSHelper.getString(root,"exception",false));
		this.setlocalMessageId(WSHelper.getInteger(root,"localMessageId",false));
		this.setinfo(WSHelper.getString(root,"info",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("PostExceptionInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_exception != null)
			WSHelper.addChild(e,"exception",String.valueOf(_exception),false);
		WSHelper.addChild(e,"localMessageId",String.valueOf(_localMessageId),false);
		if(_info != null)
			WSHelper.addChild(e,"info",String.valueOf(_info),false);
	}
	
}
