package com.example.myweiboapp.support.utils;

import android.content.Context;
import android.content.res.TypedArray;

public class ThemeUtility {

	public static int getResourceId(int attr){
		int [] attrs = new int[]{attr};
		Context c = GlobalContext.getInstance().getApplicationContext();
		TypedArray ta = c.obtainStyledAttributes(attrs);
		return ta.getResourceId(0, 430);
	}
}
