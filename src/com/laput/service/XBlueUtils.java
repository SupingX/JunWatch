package com.laput.service;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.mycj.junsda.util.SharedPreferenceUtil;

public class XBlueUtils {
	
	
	
	public static String SHARE_DEVICE_ADDRESS = "SHARE_DEVICE_ADDRESS";
	public static String SHARE_DEVICE_NAME = "SHARE_DEVICE_NAME";
	
	
	public static String SHARE_DEVICES = "SHARE_DEVICES";
	public static String NON_DEVICES = "";
	public static String SPLIT_REG = ";";
	
	
	public static void saveBlue(Context context ,String address){
		if (address == null) {
			return;
		}
		SharedPreferenceUtil.put(context, SHARE_DEVICE_ADDRESS, address);
	}
	
	public static void saveBlue(Context context, BluetoothDevice device){
		if (device == null) {
			return;
		}
		SharedPreferenceUtil.put(context, SHARE_DEVICE_ADDRESS, device.getAddress());
		SharedPreferenceUtil.put(context, SHARE_DEVICE_NAME, device.getName());
	}
	public static String getBlueAddress(Context context ){
		String address = (String) SharedPreferenceUtil.get(context, SHARE_DEVICE_ADDRESS, NON_DEVICES);
	
		return address;
	}
	public static String getBlueName(Context context ){
		String name = (String) SharedPreferenceUtil.get(context,SHARE_DEVICE_NAME, NON_DEVICES);
		return name;
	}
	
	
//	public static void saveBlue(Context context ,String address){
//		if (address == null) {
//			return;
//		}
//		HashSet<String> blues = getBlues(context);
//		if (blues!=null && blues.contains(address)) {
//			return;
//		}
//	   StringBuffer sb = new StringBuffer();
//       String bluesJson = getBluesJson(context); //
//       if (bluesJson.equals(NON_DEVICES)) {
//    	   sb.append(address);
//       }else{
//    	   sb.append(bluesJson)
//    	   .append(SPLIT_REG)
//      		.append(address);
//       }
//       SharedPreferenceUtil.put(context, SHARE_DEVICES, sb.toString());
//	}
	
	
	
//	public static void clear(Context context){
//		 SharedPreferenceUtil.put(context, SHARE_DEVICES, "");
//	}
	
	public static void clear(Context context){
		 SharedPreferenceUtil.put(context, SHARE_DEVICE_ADDRESS, "");
		 SharedPreferenceUtil.put(context, SHARE_DEVICE_NAME, "");
	}

	
//	public static String getBluesJson(Context context){
//		String devicesJson = (String) SharedPreferenceUtil.get(context, SHARE_DEVICES, NON_DEVICES);
//		return devicesJson;
//	}
//	
//	public static HashSet<String> getBlues(Context context){
//		HashSet<String> set = new HashSet<String>();
//		String bluesJson = getBluesJson(context);
//		if (bluesJson.equals(NON_DEVICES)) {
//			return null;
//		}else{
//			String[] addresses = bluesJson.split(SPLIT_REG);
//			for (int i = 0; i < addresses.length; i++) {
//				set.add(addresses[i]);
//			}
//		}
//		return set;
//	}
//	
//	public static int getBluesSize(Context context){
//		return getBlues(context)==null ? 0 : getBlues(context).size();
//	}
//	
//	
//	public static boolean isExist(String addresss,Context context){
//		HashSet<String> blues = XBlueUtils.getBlues(context);
//		if (blues != null) {
//			Iterator<String> iterator = blues.iterator();
//			while (iterator.hasNext()) {
//				String address = iterator.next();
//				if (address.equals(addresss)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
	
	
	
	public static String byteToHexString(byte[] bytes) {
		String result = null;
		String hex = null;
		if (bytes != null && bytes.length > 0) {
			final StringBuilder stringBuilder = new StringBuilder(bytes.length);
			for (byte byteChar : bytes) {
				hex = Integer.toHexString(byteChar & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				stringBuilder.append(hex.toUpperCase());
			}
			result = stringBuilder.toString();
		}
		return result;
	}
	
	public static byte[] hexStringToByte(String data) {
		byte[] bytes = null;
		if (data != null) {
			data = data.toUpperCase();
			int length = data.length() / 2;
			char[] dataChars = data.toCharArray();
			bytes = new byte[length];
			for (int i = 0; i < length; i++) {
				int pos = i * 2;
				bytes[i] = (byte) (charToByte(dataChars[pos]) << 4 | charToByte(dataChars[pos + 1]));
			}
		}
		return bytes;
	}
	
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
	
	
}
