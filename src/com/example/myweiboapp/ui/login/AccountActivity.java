package com.example.myweiboapp.ui.login;

import java.util.ArrayList;
import java.util.List;


import android.accounts.AccountsException;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myweiboapp.R;
import com.example.myweiboapp.bean.AccountBean;
import com.example.myweiboapp.bean.UserBean;
import com.example.myweiboapp.support.database.AccountDBTask;
import com.example.myweiboapp.support.utils.GlobalContext;
import com.example.myweiboapp.ui.login.ui.interfaces.AbstractAppActivity;

public class AccountActivity extends AbstractAppActivity implements LoaderManager.LoaderCallbacks<List<AccountBean>>{

	/* (non-Javadoc)
	 * @see com.example.myweiboapp.ui.login.ui.interfaces.AbstractAppActivity#onCreate(android.os.Bundle)
	 */
	private final int ADD_ACCOUNT_REQUEST_CODE = 0;
	private List<AccountBean> accountList = new ArrayList<AccountBean>();
	private AccountAdapter listAdapter = null;
	private ListView listview = null;
	private final int LOADER_ID = 0;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		GlobalContext.getInstance().startedApp = true;
		
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.accountactivity_layout);
		listAdapter = new AccountAdapter();
		listview = (ListView)findViewById(R.id.account_listview);
		listview.setAdapter(listAdapter);
		
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.actionbar_menu_accountactivity, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.menu_add_account:
			String values[] = new String[1];
			values[0] = getString(R.string.oauth_login);
			//values[1] = getString(R.string.official_app_login);
			
			new AlertDialog.Builder(this).setItems(values, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent intent;
					switch(which){
					case 0:
						intent = new Intent(AccountActivity.this, OAuthActivity.class);
						startActivityForResult(intent, ADD_ACCOUNT_REQUEST_CODE);
						break;
					}
					
				}
				
			}).show();
			break;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == ADD_ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK){
			 refresh();
	            if (data == null)
	                return;
		}
		
	}

	private void refresh() {
		getLoaderManager().getLoader(LOADER_ID).forceLoad();
				
	}

	@Override
	public Loader<List<AccountBean>> onCreateLoader(
			int id, Bundle args) {		
		return new AccountDataLoader(AccountActivity.this, args);
	}

	@Override
	public void onLoadFinished(Loader<List<AccountBean>> arg0,
			List<AccountBean> data) {
		accountList = data;
		listAdapter.notifyDataSetChanged();
		
	}

	@Override
	public void onLoaderReset(Loader<List<AccountBean>> arg0) {
		accountList = new ArrayList<AccountBean>();
		listAdapter.notifyDataSetChanged();
		
	}

	
	@Override
	public void onBackPressed() {
		GlobalContext.getInstance().startedApp = false;
		super.onBackPressed();
	}


	private static class AccountDataLoader extends AsyncTaskLoader<List<AccountBean>>{

		public AccountDataLoader(Context context, Bundle args) {
            super(context);
        }
		
		
		@Override
		protected void onStartLoading() {
			forceLoad();
		}


		@Override
		public List<AccountBean> loadInBackground() {
		
			 return AccountDBTask.getAccountList();
		}
		
	}
	
	private class AccountAdapter extends BaseAdapter{

		int defaultBG;
		
		public AccountAdapter(){
			defaultBG = getResources().getColor(R.color.transparent);
		}
		@Override
		public int getCount() {
			
			return accountList.size();
		}

		@Override
		public Object getItem(int arg0) {
			
			return accountList.get(arg0);
		}

		@Override
		public long getItemId(int index) {
			// TODO Auto-generated method stub
			return Long.valueOf(accountList.get(index).getUid());
		}

		@Override
		public View getView(int index, View arg1, ViewGroup viewGroup) {
			// TODO Auto-generated method stub
			LayoutInflater lf = getLayoutInflater();
			View view = lf.inflate(R.layout.accountactivity_listview_item_layout, viewGroup, false);
			view.findViewById(R.id.listview_root).setBackgroundColor(defaultBG);
			
			TextView accountname = (TextView)view.findViewById(R.id.account_name);
			AccountBean account = accountList.get(index);
			UserBean userInfo = account.getUserInfo();
			if(userInfo!=null){
				accountname.setText(userInfo.getScreen_name());
				ImageView image = (ImageView)view.findViewById(R.id.imageView_avatar);
				if(!TextUtils.isEmpty(userInfo.getProfile_image_url())){
					getBitmapDownloader().downloadAvatar(image, userInfo, false);
				}
			}
			
			
			return view;
		}
		
	}

}
