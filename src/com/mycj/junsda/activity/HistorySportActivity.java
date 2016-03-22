package com.mycj.junsda.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mycj.junsda.R;
import com.mycj.junsda.R.layout;
import com.mycj.junsda.adapter.HistroySleepAdapter;
import com.mycj.junsda.adapter.HistroySportAdapter;
import com.mycj.junsda.base.BaseActivity;
import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;
import com.mycj.junsda.bean.LitePalManager;
import com.mycj.junsda.bean.StaticValue;
import com.mycj.junsda.util.DataUtil;
import com.mycj.junsda.util.ShareUtil;
import com.mycj.junsda.view.DateUtil;
import com.mycj.junsda.view.FangTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class HistorySportActivity extends BaseActivity implements OnClickListener {
	protected static final int MSG_SPORT_DATA = 0xb1;
	protected static final int MSG_SPORT_HISTROY = 0xb2;
	protected static final String KEY_SPORT_DATA = "KEY_SPORT_DATA";
	private ImageView imgBack, imgShare;
	private FangTextView tvUp, tvDown, tvSportDate, tvSportSteps, tvSportDistance, tvSportCount, tvSportTime, tvSportConsume;
	private LinearLayout llSportTime, llSportDistance, llSportConsume;
	private ListView lvHistorySport;
	private List<HistorySport> datas;
	private HistroySportAdapter adapter;
	private Date toMonth;
	private LitePalManager litePalManager;
	private LinearLayout llSportSteps;
	private boolean isLoading1 = false, isLoading2 = false;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StaticValue.MSG_SHARE:
				String path = (String) msg.obj;
				ShareUtil.shareImage(path, HistorySportActivity.this, getString(R.string.share));
				break;
			case MSG_SPORT_DATA:
				Bundle data = msg.getData();
				int[] sportData = data.getIntArray(KEY_SPORT_DATA);
				int count = sportData[0];
				int sportSteps = sportData[1];
				int time = sportData[2];// 单位：秒
				String sportDistance = DataUtil.getDistance(sportSteps);
				String sportKal = DataUtil.getKal(sportSteps);
				String sportTime = DataUtil.format(time / 60f);
				tvSportCount.setText(String.valueOf(count) + getString(R.string.times));
				tvSportSteps.setText(String.valueOf(sportSteps) + getString(R.string.step));
				tvSportTime.setText(sportTime + getString(R.string.minute));
				tvSportDistance.setText(sportDistance + getString(R.string.km));
				tvSportConsume.setText(sportKal + getString(R.string.kcal));
				isLoading1 = false;
				break;
			case MSG_SPORT_HISTROY:
				ArrayList<HistorySport> sports = (ArrayList<HistorySport>) msg.obj;
				if (sports == null || sports.size()==0) {
					datas.clear();
//					getTestData();
				} else {
					datas.clear();
					datas.addAll(sports);
				}
				adapter.notifyDataSetChanged();
				isLoading2 = false;
				break;
			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_sport);
		litePalManager = LitePalManager.instance();
		initViews();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.img_share:
			// toast("分享");
			share(mHandler);
			break;
		case R.id.up:
			if (!isLoading1 && !isLoading2) {
				toMonth = DateUtil.getDateOfDiffMonth(toMonth, -1);
				loadSportDataByDate(toMonth);
			}
			break;
		case R.id.down:
			if (!isLoading1 && !isLoading2) {
				toMonth = DateUtil.getDateOfDiffMonth(toMonth, 1);
				loadSportDataByDate(toMonth);
			}
			break;
		case R.id.ll_sport_steps:
			intent = new Intent(HistorySportActivity.this, CountActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_sport_time:
			intent = new Intent(HistorySportActivity.this, CountActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_sport_distance:
			intent = new Intent(HistorySportActivity.this, CountActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_sport_consume:
			intent = new Intent(HistorySportActivity.this, CountActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	private void initViews() {
		imgBack = (ImageView) findViewById(R.id.img_back);
		imgShare = (ImageView) findViewById(R.id.img_share);

		tvUp = (FangTextView) findViewById(R.id.up);
		tvDown = (FangTextView) findViewById(R.id.down);
		tvSportDate = (FangTextView) findViewById(R.id.tv_date);
		tvSportCount = (FangTextView) findViewById(R.id.tv_sport_count);
		tvSportSteps = (FangTextView) findViewById(R.id.tv_sport_steps);
		tvSportTime = (FangTextView) findViewById(R.id.tv_sport_time);
		tvSportDistance = (FangTextView) findViewById(R.id.tv_sport_distance);
		tvSportConsume = (FangTextView) findViewById(R.id.tv_sport_consume);

		llSportSteps = (LinearLayout) findViewById(R.id.ll_sport_steps);
		llSportTime = (LinearLayout) findViewById(R.id.ll_sport_time);
		llSportDistance = (LinearLayout) findViewById(R.id.ll_sport_distance);
		llSportConsume = (LinearLayout) findViewById(R.id.ll_sport_consume);
		// listView
		lvHistorySport = (ListView) findViewById(R.id.lv_history_sport);
		datas = new ArrayList<HistorySport>();
		adapter = new HistroySportAdapter(datas);
		lvHistorySport.setAdapter(adapter);
		// //日期
		toMonth = new Date();
		loadSportDataByDate(toMonth);
		// listener
		imgBack.setOnClickListener(this);
		imgShare.setOnClickListener(this);
		tvUp.setOnClickListener(this);
		tvDown.setOnClickListener(this);
		llSportSteps.setOnClickListener(this);
		llSportTime.setOnClickListener(this);
		llSportDistance.setOnClickListener(this);
		llSportConsume.setOnClickListener(this);
		lvHistorySport.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				HistorySport historySport = datas.get(arg2);
				Intent intent = new Intent(HistorySportActivity.this, SportInfoActivity.class);
				Bundle data = new Bundle();
				data.putParcelable("historySport", historySport);
				intent.putExtras(data);
				startActivity(intent);
			}
		});
	}

	private void loadSportDataByDate(final Date date) {
		tvSportDate.setText(DateUtil.dateToString(date, "yyyy/MM"));
		// 数据库查询<放在一个Thread里>查询头部信息
		Thread sportThread = new Thread(new Runnable() {
			@Override
			public void run() {
				isLoading1 = true;
				int count = litePalManager.findHistroySportCountByDate(date);
				int sportSteps = litePalManager.findHistroySportStepsByDate(date);
				int time = litePalManager.findHistroySportTimeByDate(date);// 单位秒
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_SPORT_DATA;
				Bundle b = new Bundle();
				b.putIntArray(KEY_SPORT_DATA, new int[] { count, sportSteps, time });
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		});
		sportThread.start();
		// 查询listview
		Thread sportHistroyThead = new Thread(new Runnable() {

			@Override
			public void run() {
				isLoading2 = true;
				List<HistorySport> result = litePalManager.findHistroySportByDate(date);
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_SPORT_HISTROY;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});
		sportHistroyThead.start();
	}
	
	public void openMoreInfo(View v){
		Intent intent = new Intent(HistorySportActivity.this, CountActivity.class);
		startActivity(intent);
	}
	
	// Test
	private void getTestData() {
		for (int i = 0; i < Math.random() * 100; i++) {
			datas.add(new HistorySport());
		}
	}
}
