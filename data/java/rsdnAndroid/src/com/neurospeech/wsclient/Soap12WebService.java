package com.neurospeech.wsclient;

public class Soap12WebService extends SoapWebService {
	
	@Override
	protected String getSoapEnvelopeNS() {
		return "http://www.w3.org/2003/05/soap-envelope";
	}
	
	@Override
	protected String getContentType(String action) {
		if(action!=null)
			return "application/soap+xml; charset=utf-8; action=\""+ action +"\"";
		return "application/soap+xml; charset=utf-8";
	}
	
	@Override
	protected boolean IsSoapActionRequired() {
		return true;
	}

}
