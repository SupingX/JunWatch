package com.mycj.junsda.activity;

import java.util.Date;

import com.laput.service.XBlueService;
import com.mycj.junsda.R;
import com.mycj.junsda.R.layout;
import com.mycj.junsda.base.BaseActivity;
import com.mycj.junsda.bean.ProtolWrite;
import com.mycj.junsda.bean.StaticValue;
import com.mycj.junsda.service.JunsdaXplBluetoothService;
import com.mycj.junsda.util.SharedPreferenceUtil;
import com.mycj.junsda.view.DateUtil;
import com.mycj.junsda.view.DoubleDateWheelDialog;
import com.mycj.junsda.view.DoubleTimeWheelDialog;
import com.mycj.junsda.view.FangTextView;
import com.mycj.junsda.view.LaputaAlertDialog;
import com.mycj.junsda.view.XplAlertDialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class SettingRemindActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private ImageView imgBack;
	private FangTextView tvRemindAlarm;
	private FangTextView tvRemindBirthdayDate;
	private FangTextView tvRemindBirthdayTime;
	private CheckBox cbRemindIncoming;
	private CheckBox cbRemindAlarm;
	private CheckBox cbRemindLongsit;
	private CheckBox cbRemindBirthday;
	private CheckBox cbRemindPointTime;
	private DoubleTimeWheelDialog alarmDialog;
	private DoubleTimeWheelDialog birthdayDialog;
	private XplAlertDialog dialog;
	private Handler mHandler = new Handler() {
	};
//	private JunsdaXplBluetoothService xplBluetoothService;
	private LaputaAlertDialog dialogDisconnect;
	private XBlueService xBlueService;
	private boolean isIncoming;
	private boolean isAlarm;
	private boolean isLongSit;
	private boolean isBirthday;
	private boolean isPointTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_remind);
//		xplBluetoothService = getXplBluetoothService();
		
		xBlueService = getXBlueService();
		initViews();
	}

	private void initViews() {
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvRemindAlarm = (FangTextView) findViewById(R.id.tv_remind_alarm);
		tvRemindBirthdayDate = (FangTextView) findViewById(R.id.tv_remind_birthday_day);
		tvRemindBirthdayTime = (FangTextView) findViewById(R.id.tv_remind_birthday_time);
		cbRemindIncoming = (CheckBox) findViewById(R.id.cb_remind_incoming);
		cbRemindAlarm = (CheckBox) findViewById(R.id.cb_remind_alarm);
		cbRemindLongsit = (CheckBox) findViewById(R.id.cb_remind_long_sit);
		cbRemindBirthday = (CheckBox) findViewById(R.id.cb_remind_birthday);
		cbRemindPointTime = (CheckBox) findViewById(R.id.cb_remind_point_time);
		initValues();
		imgBack.setOnClickListener(this);
		tvRemindAlarm.setOnClickListener(this);
		tvRemindBirthdayDate.setOnClickListener(this);
		tvRemindBirthdayTime.setOnClickListener(this);
		cbRemindLongsit.setOnCheckedChangeListener(this);
		cbRemindIncoming.setOnCheckedChangeListener(this);
		cbRemindAlarm.setOnCheckedChangeListener(this);
		cbRemindBirthday.setOnCheckedChangeListener(this);
		cbRemindPointTime.setOnCheckedChangeListener(this);
	}

	private void initValues() {
		isIncoming = (Boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_INCOMING, false);
		isAlarm = (Boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_ALARM, false);
		isLongSit = (Boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_LONG_SIT, false);
		isBirthday = (Boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_BIRTHDAY, false);
		isPointTime = (Boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_POINT_TIME, false);
		
		String alarmTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_ALARM_TIME, "06:00");
		String birthdayTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_BIRTHDAY_TIME, "08:00");
		String birthdayDate = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_BIRTHDAY, "2000-01-01");

		tvRemindAlarm.setText(alarmTime);
		tvRemindBirthdayTime.setText(birthdayTime);
		
		Log.e("", "birthdayDate : " + birthdayDate);
		String[] split2 = birthdayDate.split("-");
		String month = split2[1];
		String day = split2[2];
		tvRemindBirthdayDate.setText(month + "/" + day);

		cbRemindIncoming.setChecked(isIncoming);
		cbRemindAlarm.setChecked(isAlarm);
		cbRemindLongsit.setChecked(isLongSit);
		cbRemindBirthday.setChecked(isBirthday);
		cbRemindPointTime.setChecked(isPointTime);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.tv_remind_alarm:
			String alarmTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_ALARM_TIME, "06:00");
			String[] split = alarmTime.split(":");
			int hour = Integer.valueOf(split[0]);
			int min = Integer.valueOf(split[1]);
			alarmDialog = new DoubleTimeWheelDialog(this, hour, min).builder().setTitle(getString(R.string.remind_date)).setPositiveButton(getString(R.string.cancel), new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			}).setNegativeButton(getString(R.string.confirm), new OnClickListener() {

				@Override
				public void onClick(View v) {
					int hour = alarmDialog.getLeftValue();
					int min = alarmDialog.getRightValue();
					String hourStr = getStringForTwo(hour);
					String minStr = getStringForTwo(min);
					String alarmTime = hourStr + ":" + minStr;
					tvRemindAlarm.setText(alarmTime);
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_REMIND_ALARM_TIME, alarmTime);
				}
			});
			alarmDialog.show();

			break;
		case R.id.tv_remind_birthday_day:
			// String birthdayDate = (String) SharedPreferenceUtil.get(this,
			// StaticValue.SHARE_BIRTHDAY,"2000-01-01");
			// String[] split2 = birthdayDate.split("-");
			// String month = split2[1];
			// String day = split2[2];
			// tvRemindBirthdayDate.setText(month + "/" + day);

			// String birthdayDate = (String) SharedPreferenceUtil.get(this,
			// StaticValue.SHARE_REMIND_BIRTHDAY_DATE, "01/01");
			// String[] spliktBirhtdayDate = birthdayDate.split("/");
			// int month = Integer.valueOf(spliktBirhtdayDate[0]);
			// int day = Integer.valueOf(spliktBirhtdayDate[1]);
			// birthdayDateDialog = new DoubleDateWheelDialog(this, month,
			// day).builder().setTitle("提醒日期")
			// .setNegativeButton("取消", new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			//
			// }
			// }).setPositiveButton("确定", new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// int month = birthdayDateDialog.getLeftValue();
			// int day = birthdayDateDialog.getRightValue();
			// String monthStr = getStringForTwo(month);
			// String dayStr = getStringForTwo(day);
			// String birthday = monthStr + "/" + dayStr;
			// tvRemindBirthdayDate.setText(birthday);
			// SharedPreferenceUtil.put(getApplicationContext(),
			// StaticValue.SHARE_REMIND_BIRTHDAY_DATE, birthday);
			// }
			// });
			// birthdayDateDialog.show();

			break;
		case R.id.tv_remind_birthday_time:
			String remindBirthdayTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_BIRTHDAY_TIME, "08:00");
			String[] splitRemindBirthdayTime = remindBirthdayTime.split(":");
			int hourBirthday = Integer.valueOf(splitRemindBirthdayTime[0]);
			int minBirthday = Integer.valueOf(splitRemindBirthdayTime[1]);
			birthdayDialog = new DoubleTimeWheelDialog(this, hourBirthday, minBirthday).builder().setTitle(getString(R.string.remind_time)).setPositiveButton(getString(R.string.cancel), new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			}).setNegativeButton(getString(R.string.confirm), new OnClickListener() {
				@Override
				public void onClick(View v) {
					int hour = birthdayDialog.getLeftValue();
					int min = birthdayDialog.getRightValue();
					String hourStr = getStringForTwo(hour);
					String minStr = getStringForTwo(min);
					String birthday = hourStr + ":" + minStr;
					tvRemindBirthdayTime.setText(birthday);
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_REMIND_BIRTHDAY_TIME, birthday);
				
					
					if (xBlueService !=null && xBlueService.isAllConnected()) {
							isBirthday = !isBirthday;
							SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_REMIND_BIRTHDAY, isBirthday);
//							showDialogAtSetting();
							String date = tvRemindBirthdayDate.getText().toString();
							String[] dateSplit = date.split("/");
							int month = Integer.valueOf(dateSplit[0]);
							int day = Integer.valueOf(dateSplit[1]);
							byte[] writeBirthday = ProtolWrite.instance().writeBirthday(isBirthday?1:0, month, day, hour, min);
//							xplBluetoothService.writeCharacteristic(writeBirthday);
							xBlueService.write(writeBirthday);
					}else{
						showDialogDisconnect(null);
//						cbRemindBirthday.setChecked(!isChecked);
					}
					
				}
			});
			birthdayDialog.show();

			break;
		default:
			break;
		}
	}
	
	public void mRemindClick(View v){
		switch (v.getId()) {
		case R.id.cb_remind_incoming:
			if (xBlueService !=null && xBlueService.isAllConnected()) {
				try {
					isIncoming = !isIncoming;
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_REMIND_INCOMING, isIncoming);
//					showDialogAtSetting();
					byte[] writeIncoming = ProtolWrite.instance().writeIncoming(isIncoming?1:0);
//					xplBluetoothService.writeCharacteristic(writeIncoming);
					xBlueService.write(writeIncoming);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				showDialogDisconnect(cbRemindIncoming);
//				cbRemindIncoming.setChecked(!isChecked);
			}
			break;
		case R.id.cb_remind_alarm:
	
			if (xBlueService !=null && xBlueService.isAllConnected()) {
				try {
					isAlarm = !isAlarm;
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_REMIND_ALARM, isAlarm);
//					showDialogAtSetting();
					String alarm = tvRemindAlarm.getText().toString();
					String[] alarmSplit = alarm.split(":");
					byte[] writeAlarm = ProtolWrite.instance().writeAlarm(isAlarm?1:0, Integer.valueOf(alarmSplit[0]), Integer.valueOf(alarmSplit[1]));
//					xplBluetoothService.writeCharacteristic(writeAlarm);
					xBlueService.write(writeAlarm);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				showDialogDisconnect(cbRemindAlarm);
//				cbRemindAlarm.setChecked(!isChecked);
			}
			break;
		case R.id.cb_remind_long_sit:
			
//			if (xplBluetoothService!=null && xplBluetoothService.isBluetoothConnected()) {
			if (xBlueService !=null && xBlueService.isAllConnected()) {
				try {
					isLongSit = !isLongSit;
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_REMIND_LONG_SIT, isLongSit);
//					showDialogAtSetting();
					byte[] writeLongSit = ProtolWrite.instance().writeLongSit(isLongSit?1:0);
//					xplBluetoothService.writeCharacteristic(writeLongSit);
					xBlueService.write(writeLongSit);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				showDialogDisconnect(cbRemindLongsit);
//				cbRemindLongsit.setChecked(!isChecked);
			}
			break;
		case R.id.cb_remind_birthday:
		
//			if (xplBluetoothService!=null && xplBluetoothService.isBluetoothConnected()) {
			if (xBlueService !=null && xBlueService.isAllConnected()) {
				try {
					isBirthday = !isBirthday;
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_REMIND_BIRTHDAY, isBirthday);
//					showDialogAtSetting();
					String date = tvRemindBirthdayDate.getText().toString();
					String time = tvRemindBirthdayTime.getText().toString();
					String[] dateSplit = date.split("/");
					String[] timeSplit = time.split(":");
					int month = Integer.valueOf(dateSplit[0]);
					int day = Integer.valueOf(dateSplit[1]);
					int hour = Integer.valueOf(timeSplit[0]);
					int min = Integer.valueOf(timeSplit[1]);
					byte[] writeBirthday = ProtolWrite.instance().writeBirthday(isBirthday?1:0, month, day, hour, min);
//					xplBluetoothService.writeCharacteristic(writeBirthday);
					xBlueService.write(writeBirthday);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				showDialogDisconnect(cbRemindBirthday);
//				cbRemindBirthday.setChecked(!isChecked);
			}
			break;
		case R.id.cb_remind_point_time:
			
//			if (xplBluetoothService!=null && xplBluetoothService.isBluetoothConnected()) {
			if (xBlueService !=null && xBlueService.isAllConnected()) {
				try {
					isPointTime= !isPointTime;
					SharedPreferenceUtil.put(getApplicationContext(), StaticValue.SHARE_REMIND_POINT_TIME, isPointTime);
//					showDialogAtSetting();
					byte[] writeLongPointTime = ProtolWrite.instance().writeLongPointTime(isPointTime?1:0);
//					xplBluetoothService.writeCharacteristic(writeLongPointTime);
					xBlueService.write(writeLongPointTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				showDialogDisconnect(cbRemindPointTime);
//				cbRemindPointTime.setChecked(!isChecked);
			}
			break;
		default:
			break;
		}
	}
	
	
	@Override
	public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {


		switch (buttonView.getId()) {
			case R.id.cb_remind_incoming:
			
				break;
			case R.id.cb_remind_alarm:
			
				break;
			case R.id.cb_remind_long_sit:
				
				break;
			case R.id.cb_remind_birthday:
			
				break;
			case R.id.cb_remind_point_time:
				
				break;
	
			default:
				break;
		}
	}
	
	
	private void showDialogAtSetting(){
		if (dialog==null) {
			dialog = new XplAlertDialog(SettingRemindActivity.this).builder2(getString(R.string.success));
		}
		dialog.show();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				dialog.cancel();
				dialog = null;
			}
		}, 1000);
	}
	
	private void showDialogDisconnect(final CheckBox cb){
		if (dialogDisconnect==null) {
			dialogDisconnect = new LaputaAlertDialog(SettingRemindActivity.this).builder(getString(R.string.disconnect_info))
					.setCanceledOnTouchOutside(false)
					;
		}
		dialogDisconnect.show();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (dialogDisconnect!=null && dialogDisconnect.isShowing()) {
					dialogDisconnect.dismiss();
					dialogDisconnect = null;
				}
				if (cb!=null) {
					cb.setChecked(!cb.isChecked());
				}
				
			}
		}, 1000);
	}
}
