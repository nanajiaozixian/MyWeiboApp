package com.example.myweiboapp.support.file;

import java.io.File;
import java.io.IOException;

import com.example.myweiboapp.support.utils.GlobalContext;

import android.os.Environment;
import android.text.TextUtils;

public class FileManager {

	private static final String AVATAR_SMAll = "avatar_small";
	
	//从url获取存储文件的路径
	public static String getFilePathFromUrl(String url,
			FileLocationMethod method) {
		//判断设备的SDCard是否可用
		if (!isExternalStorageMounted())
            return "";
		
		if(TextUtils.isEmpty(url)){
			return "";
		}
		
		String s = url.substring(url.indexOf("//")+2);
		s = s.substring(s.indexOf("/"));
		
		switch(method){
		case avatar_small:
			s = AVATAR_SMAll + s;
			break;
		}
		
		String result = getSdCardPath() + File.separator + s;
		if(!result.endsWith(".jpg") && !result.endsWith(".gif") && !result.endsWith(".png")){
			result = result + ".jpg";
		}
		
		return result;
	}

	private static String getSdCardPath() {
		if(isExternalStorageMounted()){
			File path = GlobalContext.getInstance().getExternalCacheDir();
			if(path!=null){
				return path.getAbsolutePath();
			}
		}
		return null;
	}

	public static boolean isExternalStorageMounted() {
		boolean bCanRead = Environment.getExternalStorageDirectory().canRead();
		boolean bOnlyRead = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
		boolean bNotMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED);
		return !(!bCanRead || bOnlyRead || bNotMounted);
	}
	
	public static File createNewFileInSDCard(String absoluteUrl){
		if(!isExternalStorageMounted()){
			return null;
		}
		File file = new File(absoluteUrl);
		if(file.exists()){
			return file;
		}else{
			File parent = file.getParentFile();
			if(!parent.exists()){
				parent.mkdirs();
			}
			
			try {
				if(file.createNewFile()){
					return file;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}

}
