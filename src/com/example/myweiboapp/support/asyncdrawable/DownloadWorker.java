package com.example.myweiboapp.support.asyncdrawable;

import com.example.myweiboapp.support.file.FileDownloaderHttpHelper;
import com.example.myweiboapp.support.file.FileLocationMethod;
import com.example.myweiboapp.support.imageutility.ImageUtility;

import android.os.AsyncTask;

public class DownloadWorker extends AsyncTask<String, Integer, Boolean>{

	private FileLocationMethod method;
	private String url;
	
	public DownloadWorker(String url, FileLocationMethod method){
		this.method = method;
		this.url = url;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		boolean downloaded = ImageUtility.getBitmapFromNetWork(url, params[0], new FileDownloaderHttpHelper.DownloadListener(){
			
		});
		return downloaded;
	}


}
