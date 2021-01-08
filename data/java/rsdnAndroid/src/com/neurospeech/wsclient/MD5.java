package com.neurospeech.wsclient;

import java.security.MessageDigest;

public class MD5 {

	public static String doMD5(String text)
	{
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			StringBuilder sb = new StringBuilder();
			byte[] bytes = md5.digest(text.getBytes("UTF-8"));
			for(byte b : bytes)
			{
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return "";
	}
	
}
