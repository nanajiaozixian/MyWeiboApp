package com.example.myweiboapp.support.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.text.TextUtils;

public class Utility {
	
	/*
	 * 把map里的参数变成url编码的字符串
	 */
	public static String encodeUrl(Map<String, String> params){
		if(params==null){
			return "";
		}
		
		Set<String> keys = params.keySet();
		StringBuilder sb = new StringBuilder();
		boolean bFirst = true;
		for(String key : keys){
			String value = params.get(key);
			if(!TextUtils.isEmpty(value)){
				if(bFirst){
					bFirst = false;
				}else{
					sb.append("&");		
				}
				try {
					sb = sb.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public static Bundle parseUrl(String url) {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();
		try {
			URL u = new URL(url);
			b = decoder(u.getQuery());
			b.putAll(decoder(u.getRef()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return b;
	}

	private static Bundle decoder(String s) {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();
		if(s!=null){
			String array[] = s.split("&");
			for(String temp:array){
				String p[] = temp.trim().split("=");
				try {
					b.putString(URLDecoder.decode(p[0].trim(), "UTF-8"), URLDecoder.decode(p[1].trim(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return b;
	}
	
	public static void closeSilently(Closeable c){
		if(c!=null){
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static int dip2px(int i) {
		float size = GlobalContext.getInstance().getResources().getDisplayMetrics().density;
		return (int)(size*i+0.5);
	}
}
