package com.example.myweiboapp.support.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myweiboapp.bean.AccountBean;
import com.example.myweiboapp.bean.UserBean;
import com.example.myweiboapp.support.database.table.AccountTable;
import com.example.myweiboapp.ui.login.OAuthActivity;
import com.google.gson.Gson;

public class AccountDBTask {

	public static OAuthActivity.DBResult addOrUpdateAccount(AccountBean account) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(AccountTable.UID, account.getUid());
		cv.put(AccountTable.OAUTH_TOKEN, account.getAccess_token());
		cv.put(AccountTable.OAUTH_TOKEN_EXPIRES_TIME, account.getExpires_time());
		
		String strJson = new Gson().toJson(account.getUserInfo());
		cv.put(AccountTable.INFOJSON, strJson);
		
		Cursor c = getWsd().query(AccountTable.TABLE_NAME, null, AccountTable.UID+"=?", new String[]{account.getUid()}, null, null, null);
		if(c!=null && c.getCount()>0){
			getWsd().update(AccountTable.TABLE_NAME, cv, AccountTable.UID+"=?", new String[]{account.getUid()});
			return  OAuthActivity.DBResult.update_successfully;
		}else{
			getWsd().insert(AccountTable.TABLE_NAME, AccountTable.UID, cv);
			return OAuthActivity.DBResult.add_successfuly;
		}
	}
	
	public static SQLiteDatabase getWsd(){
		DatabaseHelper dbHelper = DatabaseHelper.getInstance();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db;
	}

	public static List<AccountBean> getAccountList() {
		// TODO Auto-generated method stub
		List<AccountBean> accounts = new ArrayList<AccountBean>();
		String sql = "select * from "+AccountTable.TABLE_NAME;
		Cursor c = getWsd().rawQuery(sql, null);
		while(c.moveToNext()){
			AccountBean account = new AccountBean();
			int index = c.getColumnIndex(AccountTable.OAUTH_TOKEN);
			account.setAccess_token(c.getString(index));
			index = c.getColumnIndex(AccountTable.OAUTH_TOKEN_EXPIRES_TIME);
			account.setExpires_time(Long.valueOf(c.getString(index)));
			Gson gson = new Gson();
			index = c.getColumnIndex(AccountTable.INFOJSON);
			UserBean userInfo = gson.fromJson(c.getString(index), UserBean.class);
			account.setUserInfo(userInfo); 
			accounts.add(account);
		}
		c.close();
		return accounts;
	}

}
