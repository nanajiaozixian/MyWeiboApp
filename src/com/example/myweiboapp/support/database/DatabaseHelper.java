package com.example.myweiboapp.support.database;

import com.example.myweiboapp.support.database.table.AccountTable;
import com.example.myweiboapp.support.utils.GlobalContext;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

//单例模式
public class DatabaseHelper extends SQLiteOpenHelper{

	private static DatabaseHelper dbHelper;
	
	private static final String DATABASE_NAME = "weibo.db";
	private static final int DATABASE_VERSION = 1;
	
	//新建accounttable
	private static final String CREATE_ACCOUNT_TABLE_SQL = "create table "+ AccountTable.TABLE_NAME
			+"("
			+AccountTable.UID+" integer primary key autoincrement, "
			+AccountTable.OAUTH_TOKEN+" text, "
			+AccountTable.OAUTH_TOKEN_EXPIRES_TIME+" text, "
			+AccountTable.OAUTH_TOKEN_SECRET+" text, "
			+AccountTable.INFOJSON+" text"
			+");";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_ACCOUNT_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	public static synchronized DatabaseHelper getInstance(){
		if(dbHelper==null){
			dbHelper = new DatabaseHelper(GlobalContext.getInstance());
		}
		return dbHelper;		
	}
}
