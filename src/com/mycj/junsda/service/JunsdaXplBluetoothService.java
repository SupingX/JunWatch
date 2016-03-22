package com.mycj.junsda.service;

import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;
import com.mycj.junsda.bean.LitePalManager;
import com.mycj.junsda.bean.Notify;
import com.mycj.junsda.bean.ProtolNotify;
import com.mycj.junsda.bean.ProtolWrite;
import com.mycj.junsda.bean.StaticValue;
import com.mycj.junsda.util.SharedPreferenceUtil;
import com.mycj.junsda.view.DateUtil;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ShareActionProvider;

public class JunsdaXplBluetoothService extends XplBluetoothService{
	public static final String ACTION_SPORT = "ACTION_SPORT";
	public static final String ACTION_SPORT_STATE = "ACTION_SPORT_STATE";
	public static final String ACTION_SLEEP = "ACTION_SLEEP";
	public static final String ACTION_SLEEP_STATE = "ACTION_SLEEP_STATE";
	public static final String ACTION_HEART_RATE = "ACTION_HEART_RATE";
	public static final String EXTRA_SPORT = "EXTRA_SPORT";
	public static final String EXTRA_SPORT_STATE = "EXTRA_SPORT_STATE";
	public static final String EXTRA_SLEEP = "EXTRA_SLEEP";
	public static final String EXTRA_SLEEP_STATE = "EXTRA_SLEEP_STATE";
	public static final String EXTRA_HEART_RATE = "EXTRA_HEART_RATE";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		filter.addAction(ACTION_SPORT);
		filter.addAction(ACTION_SPORT_STATE);
		filter.addAction(ACTION_SLEEP);
		filter.addAction(ACTION_SLEEP_STATE);
		filter.addAction(ACTION_HEART_RATE);
	}

	private void sendbroadcastForHeadtRate(int heartRate) {
		Intent intent = new Intent(ACTION_HEART_RATE);
		intent.putExtra(EXTRA_HEART_RATE, heartRate);
		sendBroadcast(intent);
	}

	private void sendbroadcastForSyncSleep(int state) {
		Intent intent = new Intent(ACTION_SLEEP_STATE);
		intent.putExtra(EXTRA_SLEEP_STATE, state);
		sendBroadcast(intent);
	}

	private void sendbroadcastForHistorySleep(HistorySleep notifyHistorySleep) {
		Intent intent = new Intent(ACTION_SLEEP);
		intent.putExtra(EXTRA_SLEEP, notifyHistorySleep);
		sendBroadcast(intent);
	}

	private void sendbroadcastForSyncSport(int state) {
		Intent intent = new Intent(ACTION_SPORT_STATE);
		intent.putExtra(EXTRA_SPORT_STATE, state);
		sendBroadcast(intent);
	}

	private void sendbroadcastForHistorySport(HistorySport historySport) {
		Intent intent = new Intent(ACTION_SPORT);
		intent.putExtra(EXTRA_SPORT, historySport);
		sendBroadcast(intent);
	}
	@Override
	protected void doDisconnected(BluetoothGatt gatt, int newState) {
		
	}

	@Override
	protected void doConnected(BluetoothGatt gatt, int newState) {
		
	}

	@Override
	protected void doServiceDiscovered(BluetoothGatt gatt) {
		
		ProtolWrite pw = ProtolWrite.instance();
		// 0.同步时间
		byte[] hexDataForTimeSync = pw.hexDataForTimeSync(new Date(), this);
		// 1.睡眠设置
		byte[] sleepSetting = getSleepSetting(pw);
		// 2.来电提醒
		boolean iscoming = (boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_INCOMING, false);
		byte[] inComing = pw.writeIncoming(iscoming?1:0);
		// 3.每日闹铃
		byte[] alarmSetting = getAlarmSetting(pw);
		// 4.久坐提醒
		boolean  isLongSit = (boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_LONG_SIT, false);
		byte[] longSitSetting = pw.writeLongSit(isLongSit?1:0);
		// 5.生日提醒
		byte [] birthSetting = getBirthdaySetting(pw);
		// 6.整点报时
		boolean isPointTime = (Boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_POINT_TIME, false);
		byte[] longPointTime = pw.writeLongPointTime(isPointTime?1:0);
		
		new XplBluetoothMoreAsyncTask(currentGatt).execute(new byte[][]{
			hexDataForTimeSync,
			sleepSetting,
			inComing,
			alarmSetting,
			longSitSetting,
			birthSetting,
			longPointTime
		});
	}

	@Override
	protected void doCharacteristicChanged(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		byte[] data = characteristic.getValue();
		parseData(gatt, data);
	}
	
	/**
	 * <p>
	 * 解析数据
	 * </p>
	 * 
	 * @param data
	 * @param gatt
	 */
	private void parseData(BluetoothGatt gatt, byte[] data) {
//		Notify result;
//		ProtolNotify notify = ProtolNotify.instance();
//		try {
//			result = notify.getResult(data);
//			if (result != null) {
//				switch (result) {
//				case NOTIFY_STEP:
//					HistorySport historySport = notify.notifyHistorySport(data);
//					Log.i("Service", "获取运动数据：" + historySport.toString());
//					//save
//					saveHistorySport(historySport);
//					sendbroadcastForHistorySport(historySport);
//					break;
//				case NOTIFY_STEP_STATE:
//					int notifySyncSportState = notify
//							.notifySyncSportState(data);
//					if (notifySyncSportState == 0) {
//						Log.i("Service", "开始获取运动数据");
//					} else {
//						Log.i("Service", "获取运动数据结束");
//					}
//					sendbroadcastForSyncSport(notifySyncSportState);
//					break;
//				case NOTIFY_SLEEP:
//					HistorySleep notifyHistorySleep = notify
//							.notifyHistorySleep(data);
//					Log.i("Service", "获取睡眠数据：" + notifyHistorySleep.toString());
//					saveHistorySpleep(notifyHistorySleep);
//					sendbroadcastForHistorySleep(notifyHistorySleep);
//					break;
//				case NOTIFY_SLEEP_STATE:
//					int notifySyncSleepState = notify
//							.notifySyncSleepState(data);
//					if (notifySyncSleepState == 0) {
//						Log.i("Service", "开始获取睡眠数据");
//					} else {
//						Log.i("Service", "获取睡眠数据结束");
//					}
//					sendbroadcastForSyncSleep(notifySyncSleepState);
//					break;
//				case NOTIFY_HEART_RATE:
//					int notifyHeartRate = notify.notifyHeartRate(data);
//					Log.i("Service", "获取心率数据：" + notifyHeartRate);
//					sendbroadcastForHeadtRate(notifyHeartRate);
//					break;
//				default:
//					break;
//				}
//			}
//		} catch (Exception e) {
//
//		}

	}

	private void saveHistorySpleep(HistorySleep notifyHistorySleep) {
		new SaveSleepHistoryAsyncTask().execute(notifyHistorySleep);
	}

	private void saveHistorySport(HistorySport historySport) {
		new SaveSportHistoryAsyncTask().execute(historySport);
	}
	
	private class SaveSportHistoryAsyncTask extends AsyncTask<HistorySport, Void, Void>{
		
		@Override
		protected Void doInBackground(HistorySport... params) {
			HistorySport sport = params[0];
			Date date = new Date();
			String dateStr = DateUtil.dateToString(date, "yyyyMMdd");
			if (sport!=null) {
				List<HistorySport> sportAtDate = DataSupport.where("sportTime =?",dateStr).find(HistorySport.class);
				if (sportAtDate!=null && sportAtDate.size()>0) {	//说明存在记录 ， 更新值就行了
					ContentValues values = new ContentValues();
					values.put("sportTime", sport.getSportTime());
					values.put("step", sport.getStep());
					DataSupport.updateAll(HistorySport.class, values,"sportTime =?",dateStr);
				}else{
					sport.save(); //不存在直接保存记录
				}
				
			}
			return null;
		}
	} 
	
	private class SaveSleepHistoryAsyncTask extends AsyncTask<HistorySleep, Void, Void>{
		
		@Override
		protected Void doInBackground(HistorySleep... params) {
			HistorySleep sleep = params[0];
			Date date = new Date();
			String dateStr = DateUtil.dateToString(date, "yyyyMMdd");
			if (sleep!=null) {
				List<HistorySleep> sleepAtDate = DataSupport.where("sportTime =?",dateStr).find(HistorySleep.class);
				if (sleepAtDate!=null && sleepAtDate.size()>0) {	//说明存在记录 ， 更新值就行了
					ContentValues values = new ContentValues();
					values.put("deep", sleep.getDeep());
					values.put("light", sleep.getLight());
					DataSupport.updateAll(HistorySleep.class, values,"date =?",dateStr);
				}else{
					sleep.save(); //不存在直接保存记录
				}
				
			}
			return null;
		}
	}
	
	
	
	private byte[] getSleepSetting(ProtolWrite pw){
		boolean isSleepSettingOnoff = (boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_SLEEP_ON_OFF, false);
		String sleepTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_SLEEP_TIME, "22:00");
		String awakTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_AWAK_TIME, "06:00");
		String[] sleeps = sleepTime.split(":");
		int sleepHour = Integer.valueOf(sleeps[0]);
		int sleepMin = Integer.valueOf(sleeps[1]);
		String[] awaks = awakTime.split(":");
		int awakHour = Integer.valueOf(awaks[0]);
		int awakMin = Integer.valueOf(awaks[1]);
		byte[] writeSleepSetting = null ;
		try {
			writeSleepSetting = pw.writeSleepSetting(isSleepSettingOnoff?1:0, sleepHour, sleepMin, awakHour, awakMin);
			return writeSleepSetting;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeSleepSetting;
	
	}
	
	private byte[] getAlarmSetting(ProtolWrite pw){
		boolean isAlarm = (Boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_ALARM, false);
		String alarmTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_ALARM_TIME, "06:00");
		String[] alarms = alarmTime.split(":");
		int alarmHour = Integer.valueOf(alarms[0]);
		int alarmMin = Integer.valueOf(alarms[1]);
		return pw.writeAlarm(isAlarm?1:0, alarmHour, alarmMin);
	}
	
	private byte[] getBirthdaySetting(ProtolWrite pw) {
		boolean isBirthday = (Boolean) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_BIRTHDAY, false);
		String birthdayTime = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_REMIND_BIRTHDAY_TIME, "08:00");
		String birthdayDate = (String) SharedPreferenceUtil.get(this, StaticValue.SHARE_BIRTHDAY, "2000-01-01");
		String[] times = birthdayTime.split(":");
		String[] dates = birthdayDate.split("-");
		int month = Integer.valueOf(dates[1]);
		int day = Integer.valueOf(dates[2]);
		int hour = Integer.valueOf(times[0]);
		int min = Integer.valueOf(times[1]);
		byte[] writeBirthday = pw.writeBirthday(isBirthday?1:0, month, day, hour, min);
		return writeBirthday;
	}
}
