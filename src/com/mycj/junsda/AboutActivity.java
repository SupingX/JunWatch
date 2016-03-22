package com.mycj.junsda;

import com.mycj.junsda.base.BaseActivity;
import com.mycj.junsda.view.AlphaImageView;
import com.mycj.junsda.view.FangTextView;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		FangTextView tvVersion = (FangTextView) findViewById(R.id.tv_version);
		RelativeLayout rlUpdate = (RelativeLayout) findViewById(R.id.rl_update);
		AlphaImageView imgBack = (AlphaImageView) findViewById(R.id.img_back);
		tvVersion.setText(getVersion());
		rlUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toast("检查更新");
			}
		});
		imgBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	  public String getVersion() {
	      try {
	         PackageManager manager = this.getPackageManager();
	          PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	          String version = info.versionName;
	         return "Version " + version;
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	      return "未知";
	 }
}
