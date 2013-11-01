package com.example.myweiboapp.ui.login;

import java.text.SimpleDateFormat;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweiboapp.R;
import com.example.myweiboapp.bean.AccountBean;
import com.example.myweiboapp.bean.UserBean;
import com.example.myweiboapp.dao.URLHelper;
import com.example.myweiboapp.dao.login.OAuthDao;
import com.example.myweiboapp.support.database.AccountDBTask;
import com.example.myweiboapp.support.debug.AppLogger;
import com.example.myweiboapp.support.error.MyWeiboException;
import com.example.myweiboapp.ui.login.ui.interfaces.AbstractAppActivity;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.util.AccessTokenKeeper;


public class OAuthActivity extends AbstractAppActivity{

	WebView webView = null; //浏览网页的view
	private MenuItem refreshItem;
	private Weibo mWeibo;
	  /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    TextView mText = null;
    SharedPreferences prefs = null;
	/* (non-Javadoc)
	 * @see com.example.myweiboapp.ui.login.ui.interfaces.AbstractAppActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.oauthactivity_layout);
		
		//左上角写“登录”标题
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.login);
		
		mText = (TextView) findViewById(R.id.show);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.actionbar_menu_browserwebfragment, menu);
		refreshItem = menu.findItem(R.id.menu_refresh);
		refresh();
		return true;
	}
	/*
	 * 刷新页面
	 */
	private void refresh() {
		
		//使右上角在页面加载的时候有加载的动画
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView imageView = (ImageView)layoutInflater.inflate(R.layout.refresh_action_view, null);
		Animation ratation = AnimationUtils.loadAnimation(this, R.anim.refresh);
		imageView.startAnimation(ratation);
		refreshItem.setActionView(imageView);
		
		//webView.loadUrl(getWeiboOAuthUrl());
		mWeibo = Weibo.getInstance(URLHelper.APP_KEY, URLHelper.DIRECT_URL, URLHelper.SCOPE);
		mWeibo.anthorize(OAuthActivity.this, new AuthDialogListener());
	}
	
	@SuppressLint("ValidFragment")
	/*
	 * 微博认证授权回调类
	 */
	class AuthDialogListener implements WeiboAuthListener{

		@Override
		public void onCancel() {
			
			Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onComplete(Bundle values) {
			
			handleRedirectUrl(values);
			
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(), 
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
			finish();
        }

		@Override
		public void onWeiboException(WeiboException e) {
			 Toast.makeText(getApplicationContext(), 
	                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
			 finish();
		}
				
		
		public void handleRedirectUrl(Bundle values){
			/** 显示认证后的信息，如AccessToken */
			String token = values.getString("access_token");
	        String expires_in = values.getString("expires_in");
	        
	        Intent intent = new Intent();
	        intent.putExtras(values);
	        setResult(RESULT_OK, intent);
			
	System.out.println("access_token: "+token+"expires_in: "+"expires_in");
	        mAccessToken = new Oauth2AccessToken(token, expires_in);
	        if (mAccessToken.isSessionValid()) {
	            String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
	                    .format(new java.util.Date(mAccessToken.getExpiresTime()));
	            mText.setText("认证成功: \r\n access_token: " + token + "\r\n" + "expires_in: "
	                    + expires_in + "\r\n有效期：" + date);
	            
	            //AccessTokenKeeper会自动保存access_token、expires_in等信息到SharedPreferences中
	            AccessTokenKeeper.keepAccessToken(OAuthActivity.this, mAccessToken);
	            Toast.makeText(OAuthActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
	        
	        //保存access_token、expires_in等信息
	            OAuthTask oauthTask = new OAuthTask();
	            oauthTask.execute(token, expires_in);
	        
	        }
		}
		
		class OAuthTask extends AsyncTask<String, UserBean, DBResult>{

			ProgressFragment progress = new ProgressFragment();
			MyWeiboException e;
		
			@Override
			protected DBResult doInBackground(String... params) {
				// TODO Auto-generated method stub
				
				//把账户信息保存到AccountBean里
				String access_token = params[0];
				AccountBean account = null;
				try {
					UserBean user = new OAuthDao(access_token)
							.getOAuthUserInfo();
					long expires_time = Long.valueOf(params[1]);
					account = new AccountBean();
					account.setAccess_token(access_token);
					account.setExpires_time(System.currentTimeMillis()
							+ expires_time * 1000);
					account.setUserInfo(user);
					AppLogger.i("Expires time: " + account.getExpires_time());
					return AccountDBTask.addOrUpdateAccount(account);
				} catch (MyWeiboException e) {
					AppLogger.e(e.getStrError());
					this.e = e;
					cancel(true);
					return null;
				}
				//把账户保存或更新到SQLite里
				
			}

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(DBResult dbResult) {
			
				if (progress.isVisible()) {
					progress.dismissAllowingStateLoss();
	            }
	            switch (dbResult) {
	                case add_successfuly:
	                    Toast.makeText(OAuthActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
	                    break;
	                case update_successfully:
	                    Toast.makeText(OAuthActivity.this, getString(R.string.update_account_success), Toast.LENGTH_SHORT).show();
	                    break;
	            }
	            finish();
			}

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onPreExecute()
			 */
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				
				//progress.setAsyncTask(this);
				progress.show(getFragmentManager(), "");
			}

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
			 */
			@Override
			protected void onProgressUpdate(UserBean... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}

			@Override
			protected void onCancelled(DBResult result) {
				
				super.onCancelled(result);
				
				if(progress.isVisible()){
					progress.dismissAllowingStateLoss();
				}
				if(e!=null){
					Toast.makeText(OAuthActivity.this, e.getStrError(), Toast.LENGTH_SHORT).show();
				}
			}
			
		}//class end:OAuthTask
		
		public class ProgressFragment extends DialogFragment {
			AsyncTask task;
			public  ProgressFragment(){
				setRetainInstance(true);
				Bundle args = new Bundle();
				setArguments(args);
				
			}
			public void setAsyncTask(OAuthTask oAuthTask) {
				// TODO Auto-generated method stub
				this.task = oAuthTask;
			}
			/* (non-Javadoc)
			 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
			 */
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				// TODO Auto-generated method stub
				ProgressDialog dialog = new ProgressDialog(getActivity());
				dialog.setMessage(getString(R.string.oauthing));
				dialog.setIndeterminate(false);
				dialog.setCancelable(true);
				return dialog;
			}
			@Override
			public void onCancel(DialogInterface dialog) {
				if(task!=null){
					task.cancel(true);
				}
				super.onCancel(dialog);
			}
			
		}//class end: ProgressFragment
		
	}//class end: AuthDialogListener
	
	

	
	

	
	public static enum DBResult {
        add_successfuly, update_successfully
    }
	
	
	
}
