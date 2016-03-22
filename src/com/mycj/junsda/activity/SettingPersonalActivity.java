package com.mycj.junsda.activity;

import java.util.Calendar;
import java.util.Date;

import com.laput.service.XBlueService;
import com.mycj.junsda.R;
import com.mycj.junsda.base.BaseActivity;
import com.mycj.junsda.bean.ProtolWrite;
import com.mycj.junsda.bean.StaticValue;
import com.mycj.junsda.bean.Unit;
import com.mycj.junsda.service.JunsdaXplBluetoothService;
import com.mycj.junsda.service.XplBluetoothService;
import com.mycj.junsda.util.DataUtil;
import com.mycj.junsda.util.SharedPreferenceUtil;
import com.mycj.junsda.view.AbstraceDialog;
import com.mycj.junsda.view.DateUtil;
import com.mycj.junsda.view.DoubleTimeWheelDialog;
import com.mycj.junsda.view.FangCheckBox;
import com.mycj.junsda.view.FangRadioButton;
import com.mycj.junsda.view.FangTextView;
import com.mycj.junsda.view.NumberDialog;
import com.mycj.junsda.view.SingleWheelDialog;
import com.mycj.junsda.view.XplAlertDialog;

import android.net.wifi.WifiConfiguration.Protocol;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioGroup;

public class SettingPersonalActivity extends BaseActivity implements OnClickListener {

	private FangTextView tvSex;
	private FangTextView tvWeight;
	private FangTextView tvHeight;
	private FangTextView tvBirthday;
	private FangTextView tvSleepTime;
	private FangTextView tvWakeTime;
	private RadioGroup rgUnit;
	private FangRadioButton rbUnitEn;
	private FangRadioButton rbUnitZh;
	private FangCheckBox cbSleepOnoff;
	private NumberDialog birthdayDialog;
	private String unit;
	private SingleWheelDialog weightWheel;
	private SingleWheelDialog heightWheel;
	private DoubleTimeWheelDialog sleepDialog;
	private DoubleTimeWheelDialog wakeDialog;
	private ImageView imgBack;
//	private JunsdaXplBluetoothService xplBluetoothService;
	private Handler mHandler = new Handler (){};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_personal);
		initViews();
//		xplBluetoothService = getXplBluetoothService();
		
		xBlueService = getXBlueService();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}
	private XplAlertDialog dialog;
	private XBlueService xBlueService;
	private void initViews() {

		imgBack = (ImageView) findViewById(R.id.img_back);
		rgUnit = (RadioGroup) findViewById(R.id.rg_unit);
		rbUnitEn = (FangRadioButton) findViewById(R.id.rb_unit_en);
		rbUnitZh = (FangRadioButton) findViewById(R.id.rb_unit_zh);
		rbUnitEn.setChecked(true);
		cbSleepOnoff = (FangCheckBox) findViewById(R.id.rb_sleep_onoff);
		boolean isSleepOn = (boolean) SharedPreferenceUtil.get(SettingPersonalActivity.this, StaticValue.SHARE_SLEEP_ON_OFF, false);
		cbSleepOnoff.setChecked(isSleepOn);
		cbSleepOnoff.setText(isSleepOn?R.string.open:R.string.close);
		tvSex = (FangTextView) findViewById(R.id.tv_sex_value);
		tvWeight = (FangTextView) findViewById(R.id.tv_weight_value);
		tvHeight = (FangTextView) findViewById(R.id.tv_height_value);
		tvBirthday = (FangTextView) findViewById(R.id.tv_birthday_value);
		tvSleepTime = (FangTextView) findViewById(R.id.tv_sleep_time);
		tvWakeTime = (FangTextView) findViewById(R.id.tv_wake_time);
		initValues();
		// 设置监听
		imgBack.setOnClickListener(this);
		tvSex.setOnClickListener(this);
		tvWeight.setOnClickListener(this);
		tvHeight.setOnClickListener(this);
		tvBirthday.setOnClickListener(this);
		tvSleepTime.setOnClickListener(this);
		tvWakeTime.setOnClickListener(this);
		rgUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int weight = 0;
				int height = 0;
				int weightOld = (Integer) SharedPreferenceUtil.get(getApplicationContext(), StaticValue.SHARE_WEIGHT, 60);
				int heightOld = (Integer) SharedPreferenceUtil.get(getApplicationContext(), StaticValue.SHARE_HEIGHT, 170);
				if (checkedId == R.id.rb_unit_en) {//
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_UNIT, Unit.EN.getName());
					weight = DataUtil.kgToLb(weightOld);
					height = DataUtil.cmToInch(heightOld);

				} else {
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_UNIT, Unit.ZH.getName());
					weight = DataUtil.lbToKg(weightOld);
					height = DataUtil.inchToCm(heightOld);
				}
				updateWeihgtAndHeight(weight, height);
				SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_WEIGHT, weight);
				SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_HEIGHT, height);
			}
		});
	
		// 睡眠检测
		cbSleepOnoff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
//				dialog = new XplAlertDialog(SettingPersonalActivity.this).builder2(getString(R.string.success));
//				dialog.show();
//				mHandler.postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						dialog.cancel();
//					}
//				}, 2000);
				
				
				if (!isChecked) {
					cbSleepOnoff.setText(getString(R.string.close));
					SharedPreferenceUtil.put(SettingPersonalActivity.this, StaticValue.SHARE_SLEEP_ON_OFF, false);
//					if (xplBluetoothService != null && xplBluetoothService.isBluetoothConnected()) {
						if (xBlueService !=null && xBlueService.isAllConnected()) {
							
						try {
							byte[] writeSleepSetting = ProtolWrite.instance().writeSleepSetting(0, 0, 0, 0, 0);
//							xplBluetoothService.writeCharacteristic(writeSleepSetting);
							xBlueService.write(writeSleepSetting);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					cbSleepOnoff.setText(getString(R.string.open));
					SharedPreferenceUtil.put(SettingPersonalActivity.this, StaticValue.SHARE_SLEEP_ON_OFF, true);
//					if (xplBluetoothService != null && xplBluetoothService.isBluetoothConnected()) {
					if (xBlueService !=null && xBlueService.isAllConnected()) {
						try {
							String sleepTime = tvSleepTime.getText().toString();
							String awakTime = tvWakeTime.getText().toString();
							String[] splitSleepTime = sleepTime.split(":");
							int sleepHour = Integer.valueOf(splitSleepTime[0]);
							int sleepMin = Integer.valueOf(splitSleepTime[1]);
							String[] splitWakeTime = awakTime.split(":");
							int awakHour = Integer.valueOf(splitWakeTime[0]);
							int awakMin = Integer.valueOf(splitWakeTime[1]);
							byte[] writeSleepSetting;
							writeSleepSetting = ProtolWrite.instance().writeSleepSetting(1, sleepHour, sleepMin, awakHour, awakMin);
//							xplBluetoothService.writeCharacteristic(writeSleepSetting);
							xBlueService.write(writeSleepSetting);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

	}

	private void initValues() {
		// 单位
		String unit = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_UNIT, Unit.ZH.getName());
		if (unit.equals(Unit.ZH.getName())) {
			rbUnitZh.setChecked(true);
		} else {
			rbUnitEn.setChecked(true);
		}
		// 性别 0：女；1：男

		int set = (Integer) SharedPreferenceUtil.get(this, StaticValue.SHARE_SEX, 0);
		if (set == 1) {
			tvSex.setText(R.string.man);
		} else {
			tvSex.setText(R.string.woman);
		}
		// //体重 升高

		int weight = (Integer) SharedPreferenceUtil.get(this, StaticValue.SHARE_WEIGHT, 60);
		int height = (Integer) SharedPreferenceUtil.get(this, StaticValue.SHARE_HEIGHT, 170);
		updateWeihgtAndHeight(weight, height);
		// 生日
		String birthday = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_BIRTHDAY, "2000-01-01");
		tvBirthday.setText(birthday);
		// 睡眠时间
		String sleepTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_SLEEP_TIME, "22:00");
		tvSleepTime.setText(sleepTime);
		// 起床时间
		String wakeTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_AWAK_TIME, "06:00");
		tvWakeTime.setText(wakeTime);
	}

	private void updateWeihgtAndHeight(int weight, int height) {
		//

		String weightStr = "";
		String heightStr = "";
		weightStr = String.valueOf(weight);
		heightStr = String.valueOf(height);
		tvWeight.setText(weightStr);
		tvHeight.setText(heightStr);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.tv_sex_value:
			AbstraceDialog dialogSex = new AbstraceDialog(this).builder().setTitle(getString(R.string.sex)).setPositiveButton(getString(R.string.woman), new OnClickListener() {
				@Override
				public void onClick(View v) {
					tvSex.setText(R.string.woman);
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_SEX, 0);
				}
			}).setNegativeButton(getString(R.string.man), new OnClickListener() {

				@Override
				public void onClick(View v) {
					tvSex.setText(R.string.man);
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_SEX, 1);
				}
			});
			dialogSex.show();

			break;
		case R.id.tv_weight_value:
			StringBuffer sb = new StringBuffer();
			sb.append(getString(R.string.weight));
			unit = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_UNIT, StaticValue.UNIT_ZH);
			int maxWeight = 450;
			int minWeight = 20;
			int weight = (Integer) SharedPreferenceUtil.get(this, StaticValue.SHARE_WEIGHT, 60);
			Log.e("", "体重 weight : " + weight);

			if (Unit.ZH.getName().equals(unit)) {
				Log.e("", "ZH~~~~~~~~~~~~~~~~~");
				maxWeight = 200;
				minWeight = 10;
				sb.append(Unit.ZH.getWeight());
			} else {
				Log.e("", "EN~~~~~~~~~~~~~~~~~");
				maxWeight = 450;
				minWeight = 20;
				sb.append(Unit.EN.getWeight());
			}
			weightWheel = new SingleWheelDialog(this, weight).setMax(maxWeight).setMin(minWeight).builder().setTitle(sb.toString()).setPositiveButton(getString(R.string.cancel), new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			}).setNegativeButton(getString(R.string.confirm), new OnClickListener() {

				@Override
				public void onClick(View v) {
					int value = weightWheel.getValue();
					tvWeight.setText(String.valueOf(value));
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_WEIGHT, value);
				}
			});
			weightWheel.show();

			break;
		case R.id.tv_height_value:
			StringBuffer sbHeight = new StringBuffer();
			sbHeight.append(getString(R.string.height));
			String unit = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_UNIT, StaticValue.UNIT_ZH);
			int maxHeight = 250;
			int minHeight = 40;
			int height = (Integer) SharedPreferenceUtil.get(this, StaticValue.SHARE_HEIGHT, 170);
			if (Unit.ZH.getName().equals(unit)) {
				sbHeight.append(Unit.ZH.getHeight());
				maxHeight = 250;
				minHeight = 40;
			} else {
				sbHeight.append(Unit.EN.getHeight());
				maxHeight = 100;
				minHeight = 10;
			}

			heightWheel = new SingleWheelDialog(this, height).setMax(maxHeight).setMin(minHeight).builder().setTitle(sbHeight.toString()).setPositiveButton(getString(R.string.cancel), new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			}).setNegativeButton(getString(R.string.confirm), new OnClickListener() {

				@Override
				public void onClick(View v) {
					int value = heightWheel.getValue();
					tvHeight.setText(String.valueOf(value));
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_HEIGHT, value);
				}
			});
			heightWheel.show();

			break;

		case R.id.tv_birthday_value:
			String birthday = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_BIRTHDAY, "2000-01-01");
			Date date = DateUtil.stringToDate(birthday, "yyyy-MM-dd");
			birthdayDialog = new NumberDialog(this, date).builder()
					.setTitle(getString(R.string.birthday))
					.setNegativeButton(getString(R.string.confirm), new OnClickListener() {
				@Override
				public void onClick(View v) {
					Calendar c = birthdayDialog.getCalendar();
					String date = DateUtil.dateToString(c.getTime(), "yyyy-MM-dd");
					tvBirthday.setText(date);
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_BIRTHDAY, date);
				}
			}).setPositiveButton(getString(R.string.cancel), new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			birthdayDialog.show();
			break;
		case R.id.tv_sleep_time:
			String sleepTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_SLEEP_TIME, "22:00");
			String[] split = sleepTime.split(":");
			int hour = Integer.valueOf(split[0]);
			int min = Integer.valueOf(split[1]);
			sleepDialog = new DoubleTimeWheelDialog(this, hour, min).builder().setTitle(getString(R.string.sleep_time)).setPositiveButton(getString(R.string.cancel), new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			}).setNegativeButton(getString(R.string.confirm), new OnClickListener() {

				@Override
				public void onClick(View v) {

					int hour = sleepDialog.getLeftValue();
					int min = sleepDialog.getRightValue();
					String hourStr = getStringForTwo(hour);
					String minStr = getStringForTwo(min);

					String sleepTime = hourStr + ":" + minStr;
					tvSleepTime.setText(sleepTime);
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_SLEEP_TIME, sleepTime);
//					if (xplBluetoothService!=null) {
//						xplBluetoothService.writeCharacteristic(ProtolWrite.instance()
//								.writeSleepSetting(onoff, startHour, startMin, endHour, endMin));
//					}
				}
			});
			sleepDialog.show();

			break;
		case R.id.tv_wake_time:
			String wakeTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_AWAK_TIME, "06:00");
			String[] splitWake = wakeTime.split(":");
			int hourWake = Integer.valueOf(splitWake[0]);
			int minWake = Integer.valueOf(splitWake[1]);
			wakeDialog = new DoubleTimeWheelDialog(this, hourWake, minWake).builder().setTitle(getString(R.string.wake_up)).setPositiveButton(getString(R.string.cancel), new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			}).setNegativeButton(getString(R.string.confirm), new OnClickListener() {

				@Override
				public void onClick(View v) {
					int hour = wakeDialog.getLeftValue();
					int min = wakeDialog.getRightValue();
					String hourStr = getStringForTwo(hour);
					String minStr = getStringForTwo(min);
					String wakeTime = hourStr + ":" + minStr;
					tvWakeTime.setText(wakeTime);
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_AWAK_TIME, wakeTime);
				}
			});
			wakeDialog.show();
			break;
		default:
			break;
		}
	}

}
