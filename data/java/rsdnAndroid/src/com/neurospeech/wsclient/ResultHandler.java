package com.neurospeech.wsclient;

import android.os.Handler;



public class ResultHandler {
	private Exception error;
	
	private Handler handler;
	
	public ResultHandler(){
		handler = new Handler();		
	}
	
	void onExecuteStart()
	{
		handler.post(
			new Runnable()
			{
				public void run()
				{
					onStart();
				}
			}
		);
	}
	void onExecuteError(Exception e)
	{
		error = e;
		handler.post(
			new Runnable()
			{
				public void run()
				{
					onBeforeError(error);
					onError(error);
				}
			}
		);
	}
	protected Object __result;
	void onExecuteResult(Object result)
	{
		__result = result;
		handler.post(
			new Runnable()
			{
				public void run()
				{
					onBeforeResult();
					onServiceResult();
				}
			}
		);
	}
	
	protected void onServiceResult(){}
	
	protected void onStart(){
		
	}
	
	protected void onBeforeError(Exception ex){}
	
	protected void onBeforeResult(){}
	
	protected void onError(Exception ex) {}
	
	
}
