package com.neurospeech.wsclient;

public class VoidResultHandler extends ResultHandler {
	
	@Override
	protected void onServiceResult(){
		onResult();
	}
	
	
	protected void onResult() {
	}
}
