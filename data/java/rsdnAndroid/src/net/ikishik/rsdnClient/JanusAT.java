package net.ikishik.rsdnClient;

import com.neurospeech.wsclient.*;
import org.w3c.dom.*;

public class JanusAT extends SoapWebService{
	
	
	public JanusAT(){
		//this.setUrl("/ws/janusAT.asmx");
		this.setUrl("http://www.rsdn.ru/ws/janusAT.asmx");
	}
	
	
	public TopicResponse GetTopicByMessage(TopicRequest topicRequest) throws Exception 
	{
		SoapRequest req;
		req = buildSoapRequest("GetTopicByMessage","http://rsdn.ru/Janus/","http://rsdn.ru/Janus/GetTopicByMessage",null);
		Element __request = req.method;
		WSHelper.addChildNode(__request,"topicRequest",null,topicRequest);
		SoapResponse sr = getSoapResponse(req);
		Element __response = (Element)sr.body.getFirstChild().getFirstChild();
		return TopicResponse.loadFrom(__response);
	}
	
	public ChangeResponse GetNewData(ChangeRequest changeRequest) throws Exception 
	{
		SoapRequest req;
		req = buildSoapRequest("GetNewData","http://rsdn.ru/Janus/","http://rsdn.ru/Janus/GetNewData",null);
		Element __request = req.method;
		WSHelper.addChildNode(__request,"changeRequest",null,changeRequest);
		SoapResponse sr = getSoapResponse(req);
		Element __response = (Element)sr.body.getFirstChild().getFirstChild();
		return ChangeResponse.loadFrom(__response);
	}
	
	public ForumResponse GetForumList(ForumRequest forumRequest) throws Exception 
	{
		SoapRequest req;
		req = buildSoapRequest("GetForumList","http://rsdn.ru/Janus/","http://rsdn.ru/Janus/GetForumList",null);
		Element __request = req.method;
		WSHelper.addChildNode(__request,"forumRequest",null,forumRequest);
		SoapResponse sr = getSoapResponse(req);
		Element __response = (Element)sr.body.getFirstChild().getFirstChild();
		return ForumResponse.loadFrom(__response);
	}
	
	public UserResponse GetNewUsers(UserRequest userRequest) throws Exception 
	{
		SoapRequest req;
		req = buildSoapRequest("GetNewUsers","http://rsdn.ru/Janus/","http://rsdn.ru/Janus/GetNewUsers",null);
		Element __request = req.method;
		WSHelper.addChildNode(__request,"userRequest",null,userRequest);
		SoapResponse sr = getSoapResponse(req);
		Element __response = (Element)sr.body.getFirstChild().getFirstChild();
		return UserResponse.loadFrom(__response);
	}
	
}
