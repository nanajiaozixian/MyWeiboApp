package com.example.myweiboapp.support.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

//单例模式
//该类不能被继承
public final class GlobalContext extends Application{

	private static GlobalContext globalContext = null;
	
	public boolean startedApp = false;

	private Activity activity = null;
	//image memory cache
    private LruCache<String, Bitmap> avatarCache = null;
	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		globalContext = this;
	}
	public static GlobalContext getInstance() {
		// TODO Auto-generated method stub
		return globalContext;
	}
	public void setActivity(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
	}
	public LruCache<String, Bitmap> getAvatarCache() {
		// TODO Auto-generated method stub
		if(avatarCache==null){
			buildCache();
		}
		return avatarCache;
	}
	private void buildCache() {
		//计算内存
		int memoryClass = ((ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = Math.max(1024 * 1024 * 8, 1024 * 1024 * memoryClass / 5);
		avatarCache = new LruCache<String, Bitmap>(cacheSize);
	}
	
	

}
