package com.example.myweiboapp.support.http;

import java.util.Map;

import com.example.myweiboapp.support.error.MyWeiboException;
import com.example.myweiboapp.support.file.FileDownloaderHttpHelper;

public class HttpUtility {
	private static HttpUtility httpUtility  = new HttpUtility();
	
	public static HttpUtility getInstance(){
		return httpUtility;
	}

	public String executeNormalTask(HttpMethod httpMethod, String url,
			Map<String, String> params) throws MyWeiboException {
		// TODO Auto-generated method stub
		return new JavaHttpUtility().executeNormalTask(httpMethod, url, params);
	}
	
	public boolean executeDownloadTask(String url, String savePath, FileDownloaderHttpHelper.DownloadListener downloadListner){
		return !Thread.currentThread().isInterrupted() && new JavaHttpUtility().doGetSaveFile(url, savePath, downloadListner);
	}
}
