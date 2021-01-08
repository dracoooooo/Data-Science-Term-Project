package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class JanusUserInfo extends WSObject
{
	
	private Integer _userId;
	public Integer getuserId(){
		return _userId;
	}
	public void setuserId(Integer value){
		_userId = value;
	}
	private String _userName;
	public String getuserName(){
		return _userName;
	}
	public void setuserName(String value){
		_userName = value;
	}
	private String _userNick;
	public String getuserNick(){
		return _userNick;
	}
	public void setuserNick(String value){
		_userNick = value;
	}
	private String _realName;
	public String getrealName(){
		return _realName;
	}
	public void setrealName(String value){
		_realName = value;
	}
	private String _publicEmail;
	public String getpublicEmail(){
		return _publicEmail;
	}
	public void setpublicEmail(String value){
		_publicEmail = value;
	}
	private String _homePage;
	public String gethomePage(){
		return _homePage;
	}
	public void sethomePage(String value){
		_homePage = value;
	}
	private String _specialization;
	public String getspecialization(){
		return _specialization;
	}
	public void setspecialization(String value){
		_specialization = value;
	}
	private String _whereFrom;
	public String getwhereFrom(){
		return _whereFrom;
	}
	public void setwhereFrom(String value){
		_whereFrom = value;
	}
	private String _origin;
	public String getorigin(){
		return _origin;
	}
	public void setorigin(String value){
		_origin = value;
	}
	private Integer _userClass;
	public Integer getuserClass(){
		return _userClass;
	}
	public void setuserClass(Integer value){
		_userClass = value;
	}
	
	public static JanusUserInfo loadFrom(Element root) throws Exception
	{
		if(root==null){
			return null;
		}
		JanusUserInfo result = new JanusUserInfo();
		result.load(root);
		return result;
	}
	
	
	protected void load(Element root) throws Exception
	{
		this.setuserId(WSHelper.getInteger(root,"userId",false));
		this.setuserName(WSHelper.getString(root,"userName",false));
		this.setuserNick(WSHelper.getString(root,"userNick",false));
		this.setrealName(WSHelper.getString(root,"realName",false));
		this.setpublicEmail(WSHelper.getString(root,"publicEmail",false));
		this.sethomePage(WSHelper.getString(root,"homePage",false));
		this.setspecialization(WSHelper.getString(root,"specialization",false));
		this.setwhereFrom(WSHelper.getString(root,"whereFrom",false));
		this.setorigin(WSHelper.getString(root,"origin",false));
		this.setuserClass(WSHelper.getInteger(root,"userClass",false));
	}
	
	
	
	public Element toXMLElement(Element root)
	{
		Element e = root.getOwnerDocument().createElement("JanusUserInfo");
		fillXML(e);
		return e;
	}
	
	public void fillXML(Element e)
	{
		WSHelper.addChild(e,"userId",String.valueOf(_userId),false);
		if(_userName != null)
			WSHelper.addChild(e,"userName",String.valueOf(_userName),false);
		if(_userNick != null)
			WSHelper.addChild(e,"userNick",String.valueOf(_userNick),false);
		if(_realName != null)
			WSHelper.addChild(e,"realName",String.valueOf(_realName),false);
		if(_publicEmail != null)
			WSHelper.addChild(e,"publicEmail",String.valueOf(_publicEmail),false);
		if(_homePage != null)
			WSHelper.addChild(e,"homePage",String.valueOf(_homePage),false);
		if(_specialization != null)
			WSHelper.addChild(e,"specialization",String.valueOf(_specialization),false);
		if(_whereFrom != null)
			WSHelper.addChild(e,"whereFrom",String.valueOf(_whereFrom),false);
		if(_origin != null)
			WSHelper.addChild(e,"origin",String.valueOf(_origin),false);
		WSHelper.addChild(e,"userClass",String.valueOf(_userClass),false);
	}
	
}
