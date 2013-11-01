package com.example.myweiboapp.support.settinghelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingHelper {

	private static SharedPreferences sharedPreferences = null;
	
	public static boolean getSharedPreferences(Context context,
			String paramString, boolean b) {
		getSharedPreferencesObject(context).getBoolean(paramString, b);
		return false;
	}

	private static SharedPreferences getSharedPreferencesObject(Context context) {
		if(sharedPreferences==null){
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return sharedPreferences;
	}

}
