package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;

import org.w3c.dom.*;
import org.apache.commons.codec.binary.Base64;

public class UserRequest extends WSObject
{
	
	private String _userName;
	public String getuserName(){
		return _userName;
	}
	public void setuserName(String value){
		_userName = value;
	}
	private String _password;
	public String getpassword(){
		return _password;
	}
	public void setpassword(String value){
		_password = value;
	}
	private byte[] _lastRowVersion;
	public byte[] getlastRowVersion(){
		return _lastRowVersion;
	}
	public void setlastRowVersion(byte[] value){
		_lastRowVersion = value;
	}
	private Integer _maxOutput;
	public Integer getmaxOutput(){
		return _maxOutput;
	}
	public void setmaxOutput(Integer value){
		_maxOutput = value;
	}
	
	public static UserRequest loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		UserRequest result = new UserRequest();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setuserName(WSHelper.getString(root,"userName",false));
		this.setpassword(WSHelper.getString(root,"password",false));
		
		byte[] lrw = Base64.decodeBase64(WSHelper.getString(root,"lastRowVersion",false));
		this.setlastRowVersion(lrw);
		
		this.setmaxOutput(WSHelper.getInteger(root,"maxOutput",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("UserRequest");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		if(_userName != null)
			WSHelper.addChild(e,"userName",String.valueOf(_userName),false);
		if(_password != null)
			WSHelper.addChild(e,"password",String.valueOf(_password),false);
		if(_lastRowVersion != null)
		{
			String value = new String(Base64.encodeBase64(_lastRowVersion));
			WSHelper.addChild(e,"lastRowVersion",value,false);
		}
		WSHelper.addChild(e,"maxOutput",String.valueOf(_maxOutput),false);
	}
	
}
