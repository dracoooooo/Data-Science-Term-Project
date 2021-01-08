package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class ModerateExceptionInfo extends WSObject
{
	
	private String _ExceptionMessage;
	public String getExceptionMessage(){
		return _ExceptionMessage;
	}
	public void setExceptionMessage(String value){
		_ExceptionMessage = value;
	}
	private Integer _LocalModerateId;
	public Integer getLocalModerateId(){
		return _LocalModerateId;
	}
	public void setLocalModerateId(Integer value){
		_LocalModerateId = value;
	}
	private String _Info;
	public String getInfo(){
		return _Info;
	}
	public void setInfo(String value){
		_Info = value;
	}
	
	public static ModerateExceptionInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		ModerateExceptionInfo result = new ModerateExceptionInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setExceptionMessage(WSHelper.getString(root,"ExceptionMessage",false));
		this.setLocalModerateId(WSHelper.getInteger(root,"LocalModerateId",false));
		this.setInfo(WSHelper.getString(root,"Info",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("ModerateExceptionInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_ExceptionMessage != null)
			WSHelper.addChild(e,"ExceptionMessage",String.valueOf(_ExceptionMessage),false);
		WSHelper.addChild(e,"LocalModerateId",String.valueOf(_LocalModerateId),false);
		if(_Info != null)
			WSHelper.addChild(e,"Info",String.valueOf(_Info),false);
	}
	
}
