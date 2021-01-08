package com.neurospeech.wsclient;


import javax.xml.parsers.*;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class SoapWebService {
	
	public static String globalBaseUrl = null;
	
	private String _baseUrl = null;
	public String getBaseUrl(){
		return _baseUrl;
	}
	public void setBaseUrl(String b){
		_baseUrl = b;
	}
	
	private String _url = null;
	public String getUrl()
	{
		return _url;
	}
	public void setUrl(String url)
	{
		_url = url;
	}
	
	protected void executeAsync(Runnable r){
		Thread thread = Executors.defaultThreadFactory().newThread(r);
		thread.start();
	}
	
	private UINotifyListener _listener;
	public void setUINotifyListener(UINotifyListener l){
		_listener = l;
	}
	UINotifyListener getUINotifyListener(){
		return _listener;
	}
	
	
	protected SoapRequest buildSoapRequest(String methodName, String nsUri, String action, String header) throws Exception
	{
		
		SoapRequest req = new SoapRequest();
		
		req.methodName = methodName;
		if(action==null)
			req.soapAction = nsUri + methodName;
		else
			req.soapAction = action;
		
		
		String soapDoc  = "<?xml version='1.0' encoding='utf-8'?>" +
				"<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>" +
				"<s:Body><SOAPREQUEST xmlns='"+nsUri+"' xmlns:i='http://www.w3.org/2001/XMLSchema-instance'></SOAPREQUEST></s:Body></s:Envelope>";
		if(header!=null){
			soapDoc = soapDoc.replaceAll("HSOAPREQUEST", header);
		}
		soapDoc = soapDoc.replaceAll("SOAPREQUEST", methodName);
		
		StringReader sr = new StringReader(soapDoc);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setCoalescing(true);
		//dbf.setValidating(false);
		Document doc = dbf.newDocumentBuilder().parse(new InputSource(sr));
		doc.normalize();
		req.document = doc;
		//doc.normalizeDocument();
		Element root = doc.getDocumentElement();
		if(header!=null){
			req.header = WSHelper.getElementNS(root,getSoapEnvelopeNS() , "Header");
		}
		//root = WSHelper.getElementNS(root,getSoapEnvelopeNS(), "Body");
		root = WSHelper.getElement(root, "Body");
		req.method = WSHelper.getElement(root, methodName);
		return req;
	
	}
	
	// single cookie container for entire application
	private static DefaultHttpClient client = new DefaultHttpClient();
	
	protected DefaultHttpClient GetHttpClient(){
		return client;
	}

	protected void setHeaders(SoapRequest request, HashMap<String, String> headers)
	{
		
	}
	
	SoapResponse postWS(SoapRequest request) throws Exception{
		String xml = WSHelper.getString(request.document);
		
		String url = getServiceUrl();
		String userAgent = getUserAgent();
		String contentType = getContentType(request.soapAction);
		HttpPost post = new HttpPost(url);
		
		post.setHeader("Content-Type", contentType);
		request.rawHttpRequest = url + "\r\n";
		request.rawHttpRequest += "Content-Type: " + contentType + "\r\n";
		if(userAgent!=null){
			request.rawHttpRequest += "User-Agent: " + userAgent + "\r\n";
			post.setHeader("User-Agent", userAgent);
		}
		if(IsSoapActionRequired()){
			post.setHeader("SOAPAction",request.soapAction);

			request.rawHttpRequest += "SOAPAction: " + request.soapAction + "\r\n";
		
		}
		
		HashMap<String, String> headers = new HashMap<String, String>();
		setHeaders(request, headers);

		for(String key : headers.keySet()){
			post.setHeader(key, headers.get(key));
		}
		
		post.setEntity(new StringEntity(xml));
		
		request.rawHttpRequest += "\r\n";
		request.rawHttpRequest += xml;
		
		HttpUriRequest req = post;
		
		DefaultHttpClient c = GetHttpClient();
		if(c==null)
			c = client;
		HttpResponse response = c.execute(req);
		
		return new SoapResponse(request, response);
		
	}
	
	protected String getUserAgent(){
		return "neurospeech.wsclient.android";
	}
	
	protected String getContentType(String action) {
		return "text/xml; charset=utf-8";
	}
	
	protected String getSoapEnvelopeNS(){
		return "http://schemas.xmlsoap.org/soap/envelope/";
	}
	
	protected boolean IsSoapActionRequired(){
		return true;
	}
	
//	Element postXML(SoapRequest request, SoapResponse response) throws Exception{
//		
//		String text = WSHelper.getString(request.document);
//		//System.out.println(text);
//		
//		HttpResponse hr = postWS(request, text);
//		//InputStream stream = postWS(request, text);
//		
//		
//		StringBuilder sb = new StringBuilder();
//		
//		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
//		do{
//			String line = br.readLine();
//			if(line==null)
//				break;
//			sb.append(line + "\r\n");
//		}while(true);
//		
//		String textResponse = sb.toString();
//		
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		dbf.setNamespaceAware(true);
//		dbf.setIgnoringComments(true);
//		dbf.setIgnoringElementContentWhitespace(true);
//		dbf.setCoalescing(true);
//		
//		
//		
//		//dbf.setValidating(false);
//		try{
//			InputSource ins = new InputSource(new StringReader(textResponse));
//			Document d = dbf.newDocumentBuilder().parse(ins);
//			d.normalize();
//			return d.getDocumentElement();
//		}catch(Exception ex){
//			throw new SoapParseException(textResponse, ex);
//		}
//		//d.normalizeDocument();
//		
//		//System.out.println(getString(d));
//		
//	}
	
	
	protected void addParameter(Element root, String name, Object value)
	{
		if(value==null)
			return;
		Document doc = root.getOwnerDocument();
		Element e = doc.createElement(name);
		//e.setTextContent(value.toString());
		String val = value.toString();
		if(value.getClass().equals(Date.class)){
			val = WSHelper.stringValueOf((Date)value);
		}
		Text txt = doc.createTextNode(val);
		e.appendChild(txt);
		root.appendChild(e);
	}
	
	protected String getServiceUrl(){
		if(_baseUrl!=null && _baseUrl.length()>0){
			return _baseUrl + _url;
		}
		if(globalBaseUrl!=null && globalBaseUrl.length()>0)
		{
			return globalBaseUrl + _url;
		}
		return _url;
	}
	
	
	protected SoapResponse getSoapResponse(SoapRequest request) throws Exception
	{
		SoapResponse rs = postWS(request);
		
		// search for fault first...
		Element fault = WSHelper.getElementNS(rs.root, getSoapEnvelopeNS(), "Fault");
		if(fault!=null)	{
			String code = WSHelper.getString(fault, "faultcode", false);
			String text = WSHelper.getString(fault, "faultstring", false);
			SoapFaultException fe = new SoapFaultException(code, text, request.rawHttpRequest);
			throw fe;
		}
		
		//rs.header = WSHelper.getElementNS(rs.root,getSoapEnvelopeNS(), "Header");
		//rs.body = WSHelper.getElementNS(rs.root,getSoapEnvelopeNS(), "Body");
		
		rs.header = WSHelper.getElement(rs.root, "Header");
		rs.body = WSHelper.getElement(rs.root, "Body");
		return rs;
	}
}
