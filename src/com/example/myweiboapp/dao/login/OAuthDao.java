package com.example.myweiboapp.dao.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.myweiboapp.bean.UserBean;
import com.example.myweiboapp.dao.URLHelper;
import com.example.myweiboapp.support.debug.AppLogger;
import com.example.myweiboapp.support.error.MyWeiboException;
import com.example.myweiboapp.support.http.HttpMethod;
import com.example.myweiboapp.support.http.HttpUtility;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class OAuthDao {
	private String access_token;
	
	public OAuthDao(String access_token){
		this.access_token = access_token;
	}
	
	public UserBean getOAuthUserInfo() throws MyWeiboException{
		String uidJson = getOAuthUserUIDJsonData();
        String uid = "";
        
        try {
			JSONObject jsonObject = new JSONObject(uidJson);
			uid = jsonObject.optString("uid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Map<String, String> params = new HashMap<String,String>();
        params.put("uid", uid);
        params.put("access_token", access_token);
        
        String strURL = URLHelper.USER_SHOW;
        String strResult = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, strURL, params);
        UserBean user = new UserBean();
        Gson gson = new Gson();
        try{
        	user = gson.fromJson(strResult, UserBean.class);
        }catch(JsonSyntaxException e){
        	AppLogger.e(strResult);
        }
		return user;
	}

	private String getOAuthUserUIDJsonData() throws MyWeiboException {
		// TODO Auto-generated method stub
		String url = URLHelper.UID;
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", access_token);
		return HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, params);
	}
}
