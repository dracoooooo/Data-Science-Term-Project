package com.neurospeech.wsclient;

public class SoapParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String responseText;

	public String getResponseText() {
		return responseText;
	}
	
	public SoapParseException(String text, Exception innerExceptoin) {
		super("Parsing Exception", innerExceptoin);
		responseText = text;
	}

}
