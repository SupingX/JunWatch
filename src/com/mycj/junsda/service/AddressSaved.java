package com.mycj.junsda.service;


import com.mycj.junsda.util.SharedPreferenceUtil;

import android.bluetooth.BluetoothDevice;
import android.content.Context;



public class AddressSaved {
	public final static String SHARE_DEVICE_ADDRESS ="SHARE_DEVICE_ADDRESS";
	public final static String SHARE_DEVICE_NAME ="SHARE_DEVICE_NAME";
	/**
	 * <p>清空本地蓝牙数据</p>
	 * @param context
	 */
	public static void  disBindDevice(Context context){
		SharedPreferenceUtil.put(context, SHARE_DEVICE_ADDRESS, "");
		SharedPreferenceUtil.put(context, SHARE_DEVICE_NAME, "");
	}
	
	/**
	 * <p>保存蓝牙数据到本地</p>
	 * @param device
	 */
	public static void saveDevice(Context context,BluetoothDevice device) {
		SharedPreferenceUtil.put(context, SHARE_DEVICE_ADDRESS, device.getAddress());
		SharedPreferenceUtil.put(context, SHARE_DEVICE_NAME, device.getName());
	}
	
	public static String getBindedAddress(Context context){
		return (String) SharedPreferenceUtil.get(context, SHARE_DEVICE_ADDRESS, "");
	}
	public static String getBindedName(Context context){
		return (String) SharedPreferenceUtil.get(context, SHARE_DEVICE_NAME, "");
	}
}
