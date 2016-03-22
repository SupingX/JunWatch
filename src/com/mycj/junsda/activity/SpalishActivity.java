package com.mycj.junsda.activity;

import com.mycj.junsda.MainActivity;
import com.mycj.junsda.R;
import com.mycj.junsda.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class SpalishActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spalish);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SpalishActivity.this,MainActivity.class));
				finish();
			}
		}, 2000);
	}
}
