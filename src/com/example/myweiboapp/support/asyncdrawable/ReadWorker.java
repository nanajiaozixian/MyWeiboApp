package com.example.myweiboapp.support.asyncdrawable;

/*
 * 下载资源的线程
 */
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import com.example.myweiboapp.R;
import com.example.myweiboapp.support.file.FileDownloaderHttpHelper;
import com.example.myweiboapp.support.file.FileLocationMethod;
import com.example.myweiboapp.support.file.FileManager;
import com.example.myweiboapp.support.imageutility.ImageUtility;
import com.example.myweiboapp.support.utils.GlobalContext;
import com.example.myweiboapp.support.utils.Utility;

public class ReadWorker extends AsyncTask<String, Integer, Bitmap> implements IPictureWorker{

	private LruCache<String, Bitmap> lruCache;
	private String data = "";
	private WeakReference<ImageView> viewWeakReference; 
	private GlobalContext globalContex;
	private FileLocationMethod method;
	private boolean isMultiPictures = false;
	private FailedResult failedResult;
	
	private FileDownloaderHttpHelper.DownloadListener downloadeListener = new FileDownloaderHttpHelper.DownloadListener();
	public ReadWorker(ImageView view, String strUrl, FileLocationMethod method,
			boolean isMultiPictures) {
		this.data = strUrl;
		this.globalContex = GlobalContext.getInstance();
		this.viewWeakReference = new WeakReference<ImageView>(view);
		this.method = method;
		this.isMultiPictures = isMultiPictures;
		this.lruCache = globalContex.getAvatarCache();
		
	}

	@Override
	protected Bitmap doInBackground(String... url) {
		
		synchronized (TimeLineBitmapDownloader.pauseReadWorkLock){
			while(TimeLineBitmapDownloader.pauseReadWork && !isCancelled()){
				try{
					TimeLineBitmapDownloader.pauseReadWorkLock.wait();
				}catch(InterruptedException e){
					Thread.currentThread().interrupt();
				}
			}
		}
		
		String path = FileManager.getFilePathFromUrl(data, method);
		boolean downloaded = TaskCache.waitForPictureDownload(data, downloadeListener, path, method);
		if (!downloaded) {
            failedResult = FailedResult.downloadFailed;
            return null;
        }

		
		int height = 0;
		int width = 0;
		
		switch(method){
		case avatar_small:
			height = globalContex.getResources().getDimensionPixelSize(R.dimen.timeline_avatar_height)-Utility.dip2px(5)*2;//Utility.dip2px(5)为圆角半径
			width = globalContex.getResources().getDimensionPixelSize(R.dimen.timeline_avatar_width)-Utility.dip2px(5)*2;
			break;
		default:
			height = globalContex.getResources().getDimensionPixelSize(R.dimen.timeline_avatar_height)-Utility.dip2px(5)*2;//Utility.dip2px(5)为圆角半径
			width = globalContex.getResources().getDimensionPixelSize(R.dimen.timeline_avatar_width)-Utility.dip2px(5)*2;
			break;
		
		}
		
		synchronized (TimeLineBitmapDownloader.pauseReadWorkLock){
			while(TimeLineBitmapDownloader.pauseReadWork && !isCancelled()){
				try{
					TimeLineBitmapDownloader.pauseReadWorkLock.wait();
				}catch(InterruptedException e){
					Thread.currentThread().interrupt();
				}
			}
		}
		Bitmap bitmap = null;
		switch(method){
		case avatar_small:
			bitmap = ImageUtility.getRoundedCornerPic(path, height, width, Utility.dip2px(2));
			break;
		default:
			bitmap = ImageUtility.getRoundedCornerPic(path, height, width, Utility.dip2px(2));
			break;
		
		}
		if (bitmap == null) {
            this.failedResult = FailedResult.readFailed;
        }
		return bitmap;
	}

	
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		super.onPostExecute(bitmap);
		displayBitmap(bitmap);
	}

	private void displayBitmap(Bitmap bitmap) {
		ImageView view = viewWeakReference.get();
		if(view!=null){
			if(canDisplay(view)){//只显示本线程的东西
				if(bitmap!=null){
					playImageViewAnimation(view, bitmap);
					lruCache.put(data, bitmap);
				}else if(failedResult!=null){
					switch(failedResult){
					case downloadFailed:
						view.setImageDrawable(new ColorDrawable(R.color.transparent));
                        break;
                    case readFailed:
                    	view.setImageDrawable(new ColorDrawable(R.color.transparent));
                        break;
                    case taskCanceled:
                    	view.setImageDrawable(new ColorDrawable(R.color.transparent));
                        break;
					}
				}
			}
		}
		
	}

	@Override
	protected void onCancelled(Bitmap result) {
		super.onCancelled(result);
		this.failedResult = FailedResult.taskCanceled;
		displayBitmap(result);
	}

	private void playImageViewAnimation(ImageView view, Bitmap bitmap) {
		view.setImageBitmap(bitmap);
		AlphaAnimation alpha = new AlphaAnimation(0f,1.0f);
		alpha.setDuration(500);
		view.startAnimation(alpha);
		view.setTag(getUrl());
		
		
	}

	private boolean canDisplay(ImageView view) {
		if(view!=null){
			IPictureWorker picWorkerTask = getBitmapDownloaderTask(view);
			if(this==picWorkerTask && picWorkerTask!=null){
				return true;
			}
		}
		return false;
	}

	private IPictureWorker getBitmapDownloaderTask(ImageView view) {
		if(view!=null){
			Drawable drawable = view.getDrawable();
			if(drawable instanceof PictureBitmapDrawable){
				return ((PictureBitmapDrawable)drawable).getBitmapDownloaderTask();
			}
		}
		return null;
	}

	@Override
	public String getUrl() {		
		return data;
	}

	
	
}
