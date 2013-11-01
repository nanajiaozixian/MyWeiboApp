package com.example.myweiboapp.dao;

public class URLHelper {
	public static final String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";
	public static final String APP_KEY = "862385209";
    public static final String APP_SECRET = "c877fb3f9aba1e713ece38426149d9e2";
    public static final String DIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String DISPLAY = "mobile";
    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    public static final String OAUTH_ACCESS_TOKEN = "access_token";
    public static final String OAUTH_EXPIRES_IN = "expires_in";
    
    //微博API接口的URL
    public static final String UID = "https://api.weibo.com/2/account/get_uid.json";
    public static final String USER_SHOW = "https://api.weibo.com/2/users/show.json";
}
