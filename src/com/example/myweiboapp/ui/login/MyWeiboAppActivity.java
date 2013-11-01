package com.example.myweiboapp.ui.login;



import com.example.myweiboapp.R;
import com.example.myweiboapp.support.utils.GlobalContext;
import com.example.myweiboapp.ui.login.ui.interfaces.AbstractAppActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MyWeiboAppActivity extends AbstractAppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!GlobalContext.getInstance().startedApp) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_weibo_app, menu);
		return true;
	}

}
