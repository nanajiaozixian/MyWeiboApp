package com.example.myweiboapp.support.asyncdrawable;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import android.os.AsyncTask;

import com.example.myweiboapp.support.file.FileLocationMethod;
import com.example.myweiboapp.support.file.FileDownloaderHttpHelper.DownloadListener;

public class TaskCache {
	private static ConcurrentHashMap<String, DownloadWorker> downloadTasks = new ConcurrentHashMap<String, DownloadWorker>();
	private static Object backgroundWifiDownloadPicturesWorkLock = new Object();
	
	public static boolean waitForPictureDownload(String url, DownloadListener downloadListener, String savePath, FileLocationMethod method){
		DownloadWorker downloadWorker = downloadTasks.get(url);
		boolean bLocalFileExist = new File(savePath).exists();
		
		if(downloadWorker==null){
			if(!bLocalFileExist){
				DownloadWorker newWorker = new DownloadWorker(url, method);
				synchronized (backgroundWifiDownloadPicturesWorkLock){
					downloadWorker =  downloadTasks.putIfAbsent(url, newWorker);
				}
				if(downloadWorker==null){
					downloadWorker = newWorker;
					newWorker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, savePath);
					
				}
			}
		}
		return true;
		
	}
}
