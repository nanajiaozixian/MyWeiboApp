package com.example.myweiboapp.support.asyncdrawable;




import com.example.myweiboapp.R;
import com.example.myweiboapp.bean.UserBean;
import com.example.myweiboapp.support.debug.AppLogger;
import com.example.myweiboapp.support.file.FileLocationMethod;
import com.example.myweiboapp.support.settinghelper.SettingUtility;
import com.example.myweiboapp.support.utils.GlobalContext;
import com.example.myweiboapp.support.utils.ThemeUtility;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

public class TimeLineBitmapDownloader {

	private static TimeLineBitmapDownloader instance;
	static volatile boolean pauseReadWork = false;
	private Handler handler;
	private int defaultPictureResId;
	static final Object pauseReadWorkLock = new Object();
	
	public TimeLineBitmapDownloader(Handler handler){
		this.handler = handler;
		this.defaultPictureResId = ThemeUtility.getResourceId(R.attr.listview_pic_bg);
	}
	
	public static TimeLineBitmapDownloader getInstance() {
		
		if(instance==null){
			return new TimeLineBitmapDownloader(new Handler(Looper.getMainLooper()));
		}
		return instance;
	}
	
	public void downloadAvatar(ImageView view, UserBean user, boolean isFling){
		if(user==null){
			view.setImageResource(defaultPictureResId);
			return;
		}
		String strUrl;
		FileLocationMethod method;
		if(SettingUtility.getEnableBigAvatar()){
			strUrl = user.getAvatar_large();
			method = FileLocationMethod.avatar_large;
		}else{
			strUrl = user.getProfile_image_url();
			method = FileLocationMethod.avatar_small;
		}
		displayImageView(view, strUrl, method, isFling, false);
	}

	private void displayImageView(final ImageView view, String strUrl,
			FileLocationMethod method, boolean isFling, boolean isMultiPictures) {
		
		view.clearAnimation();
		
		if (!shouldReloadPicture(view, strUrl))
            return;
		final Bitmap bitmap = getBitmapFromMemCache(strUrl);
		if(bitmap!=null){
			view.setImageBitmap(bitmap);
			view.setTag(strUrl);
			if(view.getAlpha()!=1.0f){
				view.setAlpha(1.0f);				
			}
			cancelPotentialDownload(strUrl, view);
		}else{
			if(isFling){
				view.setImageResource(defaultPictureResId);
				return;
			}
			if(!cancelPotentialDownload(strUrl, view)){
				return;
			}
			//新建一个下载图片资源的线程
			final ReadWorker newTask = new ReadWorker(view, strUrl, method, isMultiPictures);
			//新建一个画布
			PictureBitmapDrawable downloadedDrawable = new PictureBitmapDrawable(newTask);
			view.setImageDrawable(downloadedDrawable);
			
			//把下载图片的任务加入主线程队列中，过400ms后再执行
			handler.postDelayed(new Runnable(){

				@Override
				public void run() {
					if (getBitmapDownloaderTask(view) == newTask) {
                        newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
					
				}

				
				
			}, 400);
		}
	}

	/*
	 * 判断是否需要重新读取或下载图片，如果缓存中有就不需要
	 */
	private boolean shouldReloadPicture(ImageView view, String urlKey) {
		
		 if (urlKey.equals(view.getTag())
	                && view.getDrawable() != null
	                && view.getDrawable() instanceof BitmapDrawable
	                && ((BitmapDrawable) view.getDrawable() != null
	                && ((BitmapDrawable) view.getDrawable()).getBitmap() != null)) {
	            AppLogger.d("shouldReloadPicture=false");
	            return false;
	        } else {
	            view.setTag(null);
	            AppLogger.d("shouldReloadPicture=true");
	            return true;
	        }
	}

	private boolean cancelPotentialDownload(String strUrl, ImageView view) {
		IPictureWorker task = getBitmapDownloaderTask(view);
		if(task!=null){
			String taskUrl = task.getUrl();
			if(taskUrl==null || !(taskUrl.equals(strUrl))){
				if(task instanceof AsyncTask){
					((AsyncTask) task).cancel(true);
				}
			}else{
				return false;
			}
		}
		return true;
		
	}

	private IPictureWorker getBitmapDownloaderTask(ImageView view) {
		if(view != null){
			Drawable drawable = view.getDrawable();
			if(drawable instanceof PictureBitmapDrawable){
				PictureBitmapDrawable downloadedDrawable = (PictureBitmapDrawable)drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}
	private Bitmap getBitmapFromMemCache(String strUrl) {
		
		if(TextUtils.isEmpty(strUrl)){
			return null;
		}else{
			return GlobalContext.getInstance().getAvatarCache().get(strUrl);
		}
		
	}

}
