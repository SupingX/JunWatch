package com.laput.service;

import java.util.ArrayList;

import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class XBlueBroadcastUtils {
	public static final String ACTION_DO_NOT_SUPPORT_BLE = "ACTION_DO_NOT_SUPPORT_BLE";
	public static final String ACTION_BLUETOOTH_ADAPTER_DISABLE = "ACTION_BLUETOOTH_ADAPTER_DISABLE";
	public static final String ACTION_DEVICE_FOUND = "ACTION_DEVICE_FOUND";
	public static final String ACTION_SERVICE_DISCOVERED = "ACTION_SERVICE_DISCOVER";
	public static final String ACTION_CONNECT_STATE = "ACTION_CONNECT_STATE";
	public static final String EXTRA_CONNECT_STATE = "EXTRA_CONNECT_STATE";
	public static final String EXTRA_DEVICES = "EXTRA_DEVICES";
	public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
	//扩展
	public static final String ACTION_TAKE_PICTURE = "ACTION_TAKE_PICTURE";
	public static final String ACTION_FIND_PHONE = "ACTION_FIND_PHONE";
	public static final String ACTION_SPORT = "ACTION_SPORT";
	public static final String ACTION_SPORT_STATE = "ACTION_SPORT_STATE";
	public static final String ACTION_SLEEP = "ACTION_SLEEP";
	public static final String ACTION_SLEEP_STATE = "ACTION_SLEEP_STATE";
	public final static String ACTION_SYNC_SETTING = "ACTION_SYNC_SETTING";
	public static final String ACTION_HEART_RATE = "ACTION_HEART_RATE";
	public static final String EXTRA_SPORT = "EXTRA_SPORT";
	public static final String EXTRA_SPORT_STATE = "EXTRA_SPORT_STATE";
	public static final String EXTRA_SLEEP = "EXTRA_SLEEP";
	public static final String EXTRA_SLEEP_STATE = "EXTRA_SLEEP_STATE";
	public static final String EXTRA_HEART_RATE = "EXTRA_HEART_RATE";
	public final static String EXTRA_SYNC_SETTING = "EXTRA_SYNC_SETTING";
	public final static String EXTRA_FIND_PHONE = "EXTRA_FIND_PHONE";
	
	
	
	public void sendbroadcastreceiverForSyncSetting(Context context,boolean isSetting) {
		Intent intent = new Intent(ACTION_SYNC_SETTING);
		intent.putExtra(EXTRA_SYNC_SETTING, isSetting);
		context.sendBroadcast(intent);
	}
	public void sendbroadcastForHeadtRate(Context context,int heartRate) {
		Intent intent = new Intent(ACTION_HEART_RATE);
		intent.putExtra(EXTRA_HEART_RATE, heartRate);
		context.sendBroadcast(intent);
	}

	public void sendbroadcastForSyncSleep(Context context,int state) {
		Intent intent = new Intent(ACTION_SLEEP_STATE);
		intent.putExtra(EXTRA_SLEEP_STATE, state);
		context.sendBroadcast(intent);
	}

	public void sendbroadcastForHistorySleep(Context context,HistorySleep notifyHistorySleep) {
		Intent intent = new Intent(ACTION_SLEEP);
		intent.putExtra(EXTRA_SLEEP, notifyHistorySleep);
		context.sendBroadcast(intent);
	}

	public void sendbroadcastForSyncSport(Context context,int state) {
		Intent intent = new Intent(ACTION_SPORT_STATE);
		intent.putExtra(EXTRA_SPORT_STATE, state);
		context.sendBroadcast(intent);
	}

	public void sendbroadcastForHistorySport(Context context,HistorySport historySport) {
		Intent intent = new Intent(ACTION_SPORT);
		intent.putExtra(EXTRA_SPORT, historySport);
		context.sendBroadcast(intent);
	}
	
	public void sendbroadcastForFindPhone(Context context,int order) {
		Intent intent = new Intent(ACTION_FIND_PHONE);
		intent.putExtra(EXTRA_FIND_PHONE, order);
		context.sendBroadcast(intent);
	}
	
	public void sendbroadcastForMusic(Context context) {
	
	}
	public void sendbroadcastForTakePicture(Context context) {
		Intent intent = new Intent(ACTION_TAKE_PICTURE);
		context.sendBroadcast(intent);
	}
	
//	//扩展
//	public final static String ACTION_DATA_STEP_AND_CAL = "lite_data_step";
//	public final static String ACTION_VIR = "ACTION_VIR";
//	public final static String ACTION_DATA_SYNC_TIME = "lite_data_sync_time";
//	public final static String ACTION_CAMERA = "lite_data_camera";
//	public final static String ACTION_DATA_MUSIC = "lite_data_music";
//	public final static String ACTION_DATA_REMIND = "lite_data_remind";
//	public final static String ACTION_DATA_HEART_RATE = "lite_data_heart_rate";
//	public final static String ACTION_DATA_HISTORY_HEART_RATE = "lite_data_history_heart_rate";
//	public final static String ACTION_DATA_HISTORY_STEP = "lite_data_history_step";
//	public final static String ACTION_DATA_HISTORY_SLEEP = "lite_data_history_sleep";
//	public final static String ACTION_DATA_HISTORY_DISTANCE = "lite_data_history_distance";
//	public final static String ACTION_DATA_HISTORY_CAL = "lite_data_history_cal";
//	public final static String ACTION_DATA_HISTORY_SPORT_TIME = "lite_data_history_sport_time";
//	public final static String ACTION_DATA_HISTORY_SLEEP_FOR_TODAY = "lite_data_history_sleep_for_today";
//	public final static String ACTION_SYNC_START = "ACTION_SYNC_START";
//	public final static String ACTION_SYNC_SETTING = "ACTION_SYNC_SETTING";
//	public final static String ACTION_SYNC_END = "ACTION_SYNC_END";
//	public final static String EXTRA_STEP_AND_CAL = "extra_step";
////	public final static String EXTRA_SLEEP = "extra_sleep";
////	public final static String EXTRA_HEART_RATE = "EXTRA_HEART_RATE";
//	public final static String EXTRA_CAMERA = "EXTRA_CAMERA";
//	public final static String EXTRA_SYNC_SETTING = "EXTRA_SYNC_SETTING";
//	
//	
	private static XBlueBroadcastUtils mBroadcastUtils;
	private XBlueBroadcastUtils(){
		
	}
	public static XBlueBroadcastUtils instance(){
		if (mBroadcastUtils ==null) {
			mBroadcastUtils = new XBlueBroadcastUtils();
		}
		return mBroadcastUtils;
	}
	
	public void sendBroadcastDoNotSupportBle(Context context){
		Intent intent = new Intent(ACTION_DO_NOT_SUPPORT_BLE);
		context.sendBroadcast(intent);
	}
	
	public void sendBroadcastBluetoothAdapterDisable(Context context){
		Intent intent = new Intent(ACTION_BLUETOOTH_ADAPTER_DISABLE);
		context.sendBroadcast(intent);
	}
	
	public void sendBroadcastDeviceFound(Context context,ArrayList<BluetoothDevice> devices){
		Intent intent = new Intent(ACTION_DEVICE_FOUND);
		intent.putExtra(EXTRA_DEVICES, devices);
		context.sendBroadcast(intent);
	}
	public void sendBroadcastServiceDiscovered(Context context,BluetoothDevice device){
		Intent intent = new Intent(ACTION_SERVICE_DISCOVERED);
		intent.putExtra(EXTRA_DEVICE, device);
		context.sendBroadcast(intent);
	}
	public void sendBroadcastConnectState(Context context,BluetoothDevice device, int newState) {
		Intent intent = new Intent(ACTION_CONNECT_STATE);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_CONNECT_STATE, newState);
		context.sendBroadcast(intent);
	};
	public IntentFilter getIntentFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_BLUETOOTH_ADAPTER_DISABLE);
		filter.addAction(ACTION_DEVICE_FOUND);
		filter.addAction(ACTION_DO_NOT_SUPPORT_BLE);
		filter.addAction(ACTION_SERVICE_DISCOVERED);
		// 扩展
		filter.addAction(ACTION_SPORT);
		filter.addAction(ACTION_SPORT_STATE);
		filter.addAction(ACTION_SLEEP);
		filter.addAction(ACTION_SLEEP_STATE);
		filter.addAction(ACTION_HEART_RATE);
		filter.addAction(ACTION_SYNC_SETTING);
		
		return filter;
	}
	
}
