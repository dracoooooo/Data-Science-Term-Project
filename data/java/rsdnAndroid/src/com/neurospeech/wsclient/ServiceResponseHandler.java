package com.neurospeech.wsclient;

import android.os.Handler;

public class ServiceResponseHandler<T>{
	
	private Handler currentHandler;
	private Exception error;
	private T result;
	
	public ServiceResponseHandler(){
		currentHandler = new Handler();
	}
	
	
	public final void onExecuteStart(){
		currentHandler.post(new Runnable(){
			public void run() {
				onStart();
			}			
		});
	}
	
	protected void onStart() {
	}


	public final void onExecuteError(Exception e)
	{
		error = e;
		currentHandler.post(new Runnable() {
			
			public void run() {
				onBeforeError(error);
				onError(error);				
			}
		});
	}
	
	protected void onBeforeError(Exception ex){
		
	}
	
	protected void onBeforeResult(T _result){
		
	}
	
	protected void onError(Exception ex) {
		
	}

	public final void onExecuteResult(T _result)
	{
		this.result = _result;
		currentHandler.post(new Runnable() {
			
			public void run() {
				onBeforeResult(result);
				onResult(result);
			}
		});
	}


	protected void onResult(T result) {
	}
}
