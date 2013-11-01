package com.example.myweiboapp.support.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.text.TextUtils;

import com.example.myweiboapp.R;
import com.example.myweiboapp.support.debug.AppLogger;
import com.example.myweiboapp.support.error.MyWeiboException;
import com.example.myweiboapp.support.file.FileDownloaderHttpHelper;
import com.example.myweiboapp.support.file.FileManager;
import com.example.myweiboapp.support.utils.GlobalContext;
import com.example.myweiboapp.support.utils.Utility;

public class JavaHttpUtility {

	private static final int CONNECT_TIMEOUT = 10*1000;
	private static final int READ_TIMEOUT = 10*1000;
	private static final int DOWNLOAD_CONNECT_TIMEOUT = 15 * 1000;
    private static final int DOWNLOAD_READ_TIMEOUT = 60 * 1000;
	
	public String executeNormalTask(HttpMethod httpMethod, String url,
			Map<String, String> params) throws MyWeiboException {
		 
		switch(httpMethod){
		case Get:
			return doGet(url, params);
		default:
			break;
		}
		return null;
	}

	private String doGet(String url, Map<String, String> params) throws MyWeiboException {
		
		StringBuilder urlSb = new StringBuilder(url);
		urlSb.append("?").append(Utility.encodeUrl(params));
		String errorMsg = GlobalContext.getInstance().getResources().getString(R.string.timeout);
		try {
			URL u = new URL(urlSb.toString());
			try {
				Proxy proxy = getProxy();
				HttpURLConnection conn;
				if (proxy != null)
					conn = (HttpURLConnection) u.openConnection(proxy);
				else
					conn = (HttpURLConnection) u.openConnection();

				conn.setRequestMethod("GET");
				conn.setConnectTimeout(CONNECT_TIMEOUT);
				conn.setReadTimeout(READ_TIMEOUT);
				conn.setRequestProperty("Charset", "UTF-8");
				conn.connect();
				return handleResponse(conn);
			} catch (IOException e) {
			
				e.printStackTrace();
				throw new MyWeiboException(errorMsg, e);
			}
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
			throw new MyWeiboException(errorMsg, e);
		}
		
	}

	private static Proxy getProxy() {
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort))
			return new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
					proxyHost, Integer.valueOf(proxyPort)));
		else
			return null;
	}

	private String handleResponse(HttpURLConnection conn) throws MyWeiboException {
		 
		int iState = 0;
		try {
			iState = conn.getResponseCode();
			if(iState!=HttpURLConnection.HTTP_OK){
				conn.disconnect();
				return null;
			}
			return readResult(conn);
		} catch (IOException e) {
			 
			e.printStackTrace();
			conn.disconnect();
		}
		return null;
	}

	private String readResult(HttpURLConnection conn) throws MyWeiboException {
	 
		InputStream is = null;
		BufferedReader br = null;
		String errormsg = GlobalContext.getInstance().getResources().getString(R.string.timeout);
		try {
			is = conn.getInputStream();
			String content_encode = conn.getContentEncoding();
			if(null!=content_encode && !"".equals(content_encode) && content_encode.equals("gzip")){
				is = new GZIPInputStream(is);
			}
			
			br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line=br.readLine())!=null){
				sb.append(line);
			}
			AppLogger.d("result="+sb);
			conn.disconnect();
			return sb.toString();
		} catch (IOException e) {
			 
			e.printStackTrace();
			throw new MyWeiboException(errormsg, e);
		}finally{
			try {
				is.close();
				br.close();
				conn.disconnect();
			} catch (IOException e) {
				 
				e.printStackTrace();
			}
			
		}
		
	}

	public boolean doGetSaveFile(String strUrl, String savePath, FileDownloaderHttpHelper.DownloadListener downloadListener){
		File newFile = FileManager.createNewFileInSDCard(savePath);
		if(newFile==null){
			return false;
		}
		
		try {
			URL url = new URL(strUrl);
			AppLogger.d("download request: "+strUrl);
			HttpURLConnection conn = null;
			BufferedOutputStream out = null;
			BufferedInputStream in = null;
			try {
				conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");//必须大写
				conn.setConnectTimeout(DOWNLOAD_CONNECT_TIMEOUT);
				conn.setReadTimeout(DOWNLOAD_READ_TIMEOUT);
				conn.setDoOutput(false);
				conn.setRequestProperty("charset", "utf-8");
				conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
				
				conn.connect();
				
				int state = conn.getResponseCode();
				if(state!=HttpURLConnection.HTTP_OK){
					return false;
				}
				out = new BufferedOutputStream(new FileOutputStream(newFile));
				in = new BufferedInputStream(conn.getInputStream());
				
				final Thread thread = Thread.currentThread();
				byte [] buffer = new byte[1444];
				int byteread = 0;
				while((byteread =in.read(buffer))!=-1){
					if(thread.isInterrupted()){
						newFile.delete();
						throw new InterruptedIOException();
					}
					out.write(buffer, 0, byteread);		
				}
				return true;
				
			} catch (IOException e) {				
				e.printStackTrace();
			}finally{
				Utility.closeSilently(out);
				Utility.closeSilently(in);
				if(conn!=null){
					conn.disconnect();
				}
				
			}
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
		
		
		return false;
	}
}
