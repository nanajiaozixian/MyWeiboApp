package com.example.myweiboapp.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountBean implements Parcelable{

	private String access_token;
    private long expires_time;
    private UserBean userInfo;
	/**
	 * @return the userInfo
	 */
	public UserBean getUserInfo() {
		return userInfo;
	}

	/**
	 * @param userInfo the userInfo to set
	 */
	public void setUserInfo(UserBean userInfo) {
		this.userInfo = userInfo;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeString(access_token);
		dest.writeLong(expires_time);
	}

	/**
	 * @return the access_token
	 */
	public String getAccess_token() {
		return access_token;
	}

	/**
	 * @param access_token the access_token to set
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	/**
	 * @return the expires_time
	 */
	public long getExpires_time() {
		return expires_time;
	}

	/**
	 * @param expires_time the expires_time to set
	 */
	public void setExpires_time(long expires_time) {
		this.expires_time = expires_time;
	}

	public String getUid() {
		
		return (userInfo != null ? userInfo.getId() : "");
	}

	

	
}
