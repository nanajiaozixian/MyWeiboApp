package com.example.myweiboapp.support.asyncdrawable;

import java.lang.ref.WeakReference;

import android.graphics.drawable.ColorDrawable;


public class PictureBitmapDrawable extends ColorDrawable{

	private final WeakReference<IPictureWorker> bitmapDownloaderTaskReference;
	
	public PictureBitmapDrawable(IPictureWorker newTask) {
		bitmapDownloaderTaskReference = new WeakReference<IPictureWorker>(newTask);
	}

	public IPictureWorker getBitmapDownloaderTask() {
		return bitmapDownloaderTaskReference.get();
	}

}
