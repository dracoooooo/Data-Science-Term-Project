package com.neurospeech.wsclient;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class SoapResponse {
	
	HttpResponse response;
	
	String xmlResponse;
	
	public SoapResponse(SoapRequest request, HttpResponse response)
		throws Exception
	{
		this.response = response;
		parseResponse();
	}

	private void parseResponse()
		throws Exception
	{
		//HttpEntity entity = response.getEntity();
		//String ctype = entity.getContentType().getValue().toLowerCase();
		
		parseSimpleResponse();
		
		prepareResponse();
	}

//	private void parseMultipart(HttpEntity entity)
//		throws Exception
//	{
//		String boundary = "";
//		
//		for (HeaderElement he : entity.getContentType().getElements()) {
//			System.out.println("HE:" + he.getName());
//			if(he.getName().toLowerCase()=="boundary"){
//				boundary = he.getValue();
//			}
//		}
//		
//		if(boundary.length()==0)
//			throw new InvalidAlgorithmParameterException("Missing boundary:" + entity.getContentType().getValue() );
//		
//		boundary = "--" + boundary;
//		String endBoundary = boundary + "--";
//		
//		// split content by boundary
//		InputStreamReader isr = new InputStreamReader(entity.getContent(), "UTF-8");
//		BufferedReader br = new BufferedReader(isr);
//		
//		String line = "";
//		
//		Boolean startReading = false;
//		
//		StringBuilder sb = null;
//		
//		while((line = br.readLine())!=null){
//			if(line.equals(endBoundary))
//			{
//				processMimeContent(sb.toString());
//				break;
//			}
//			if(line.equals(boundary)){
//				if(startReading){
//					processMimeContent(sb.toString());
//					sb = null;
//				}
//				startReading = !startReading;
//				if(startReading){
//					sb = new StringBuilder();
//				}
//			}
//			if(startReading){
//				sb.append(line + "\r\n");
//			}
//		}
//		
//	}
//
//	private void processMimeContent(String str)
//		throws Exception
//	{
//		BufferedReader sr = new BufferedReader(new StringReader(str));
//		
//		WSMimeEntity mime = new WSMimeEntity();
//		
//		Boolean isSoapContent = false;
//		
//		String line = "";
//		while((line = sr.readLine())!=null)
//		{
//			String[] tokens = line.split(":", 2);
//			String key = tokens[0].toLowerCase();
//			String value = tokens[1];
//			if(key.contains("content-type")){
//				mime.contentType = value;
//			}
//			if(key.contains("content-transfer-encoding"))
//			{
//				mime.contentTransferEncoding = value;
//			}
//			if(key.contains("content-id")){
//				mime.contentID = value;
//			}
//			if(value.toLowerCase().contains("application/soap+xml"))
//			{
//				isSoapContent = true;
//			}
//		}
//
//		StringBuilder sb = new StringBuilder();
//		while((line = sr.readLine())!=null)
//		{
//			sb.append(line + "\r\n");
//		}
//		
//		if(isSoapContent){
//			xmlResponse = sb.toString();
//		}else{
//			mime.utf8Content = sb.toString();
//			MimeContents.add(mime);
//		}
//	}

	private void prepareResponse()
		throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setCoalescing(true);
		
		//dbf.setValidating(false);
		try{
			InputSource ins = new InputSource(new StringReader(xmlResponse));
			Document d = dbf.newDocumentBuilder().parse(ins);
			d.normalize();
			root = d.getDocumentElement();
		}catch(Exception ex){
			throw new SoapParseException(xmlResponse, ex);
		}
		//d.normalizeDocument();
	}

	private void parseSimpleResponse()
		throws Exception
	{
		HttpEntity e = response.getEntity();
		long n = e.getContentLength();
		ByteArrayOutputStream os;
		if(n>0)
		{
			os = new ByteArrayOutputStream((int)n);
		}else{
			os = new ByteArrayOutputStream();
		}
		
		e.writeTo(os);
		
		xmlResponse = os.toString();		
	}
	
	public Element header;
	public Element body;
	public Element root;
	
	public Vector<WSMimeEntity> MimeContents = new Vector<WSMimeEntity>();
	
	
	
}
