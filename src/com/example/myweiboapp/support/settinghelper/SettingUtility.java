package com.example.myweiboapp.support.settinghelper;

import android.content.Context;

import com.example.myweiboapp.support.utils.GlobalContext;
import com.example.myweiboapp.ui.preference.SettingActivity;


public class SettingUtility {

	public static boolean getEnableBigAvatar() {
		 return SettingHelper.getSharedPreferences(getContext(), SettingActivity.SHOW_BIG_AVATAR, false);
	}

	private static Context getContext() {
		
		return GlobalContext.getInstance();
	}

}
