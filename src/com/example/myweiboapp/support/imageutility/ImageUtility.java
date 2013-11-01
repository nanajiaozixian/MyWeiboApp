package com.example.myweiboapp.support.imageutility;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.myweiboapp.support.file.FileDownloaderHttpHelper;
import com.example.myweiboapp.support.file.FileManager;
import com.example.myweiboapp.support.http.HttpUtility;

public class ImageUtility {
	public static boolean getBitmapFromNetWork(String url, String savePath, FileDownloaderHttpHelper.DownloadListener downloadListner){
		return HttpUtility.getInstance().executeDownloadTask(url, savePath, downloadListner);
	}
	
	public static Bitmap getRoundedCornerPic(String savePath, int height, int width, int cornerRadius){
		if(!FileManager.isExternalStorageMounted()){
			return null;
		}
		if(!savePath.endsWith(".jpg") && !savePath.endsWith(".png") && !savePath.endsWith(".gif")){
			savePath = savePath +".jpg";
		}
		File file = new File(savePath);
		if(!file.exists()){
			return null;
		}
		
		//处理分辨率很高的图片
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;	
		options.inSampleSize = calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(savePath, options);
		
		if(bitmap==null){
			file.delete();
			return null;
		}
		
		if(cornerRadius>0){
			int[] size = calResize(bitmap.getHeight(), bitmap.getWidth(), height, width);
			if(size[0]>0 && size[1]>0){
				Bitmap scaleBitmap = bitmap.createScaledBitmap(bitmap, size[0], size[1], true);
				if(scaleBitmap!=bitmap){
					bitmap.recycle();
					bitmap = scaleBitmap;
				}
				Bitmap roundBitmap = ImageEditUtility.getRoundedCornerBitmap(bitmap, cornerRadius);
				if(roundBitmap!=bitmap){
					bitmap.recycle();
					bitmap = roundBitmap;
				}
			}
		}
		return bitmap;
	}
	
	private static int[] calResize(int width, int height, int reqHeight,
			int reqWidth) {
		float h = ((float)reqHeight)/(float)height;
		float w = ((float)reqWidth)/(float)width;
		float m = Math.min(h, w);
		int[] size = {(int)(width*m), (int)(height*m)};
		return size;
	}

	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
}
}

