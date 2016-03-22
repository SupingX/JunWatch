package com.mycj.junsda.bean;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.mycj.junsda.util.DataUtil;
import com.mycj.junsda.view.DateUtil;

public class ProtolWrite {
	private static ProtolWrite protolWrite;
	private ProtolWrite(){
		
	}
	public static  ProtolWrite instance(){
		if (protolWrite==null) {
			protolWrite = new ProtolWrite();
		}
		return protolWrite;
	}
	
	/**
	 * <p>请求计步</P>
	 * 0x A0 F0 00  请求今天的计步数据<br>
	 * 0x A0 F0 01  请求昨天天的计步数据<br>
	 * 0x A0 F0 02  请求前天的计步数据<br>
	 * ... ...
	 * @param day
	 * @return
	 * @throws Exception 
	 */
	public byte[] writeForStep(int day) {
		if (day>6 || day<0) {
//			throw new Exception("写入协议有错误");
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("A0");
		sb.append("F0");
		sb.append(DataUtil.integerToHexString(day));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * <P>同步所有计步</P>
	 * 当手机发送 0x A0 F1 至手环成功后，后环会先返回 0x A0 F1 00 表示即将开始发送计步历史数据<br>
	 * 当手环发送计步历史数据结束后，会发送 0x A0 F1 11 表示计步历史记录发送完毕。<br>
	 * @return
	 */
	public byte[] writeForSyncStep(){
		return DataUtil.getBytesByString("A0F1");
	}
	
	/**
	 * <p>请求睡眠</P>
	 * 0x B0 F0 00  请求今天的睡眠数据<br>
	 * 0x B0 F0 01  请求昨天的睡眠数据<br>
	 * 0x B0 F0 02  请求前天的睡眠数据<br>
	 * ... ...
	 * @param day
	 * @return
	 * @throws Exception 
	 */
	public byte[] writeForSleep(int day) throws Exception{
		if (day>6 || day<0) {
			throw new Exception("写入协议有错误");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("B0");
		sb.append("F0");
		sb.append(DataUtil.integerToHexString(day));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * <P>同步所有睡眠</P>
	 * 当手机发送 0x B0 F1 至手环成功后，后环会先返回 0x B0 F1 00 表示即将开始发送睡眠历史数据<br>
	 * 当手环发送计步历史数据结束后，会发送 0x B0 F1 11 表示睡眠历史记录发送完毕。<br>
	 * @return
	 */
	public byte[] writeForSyncSleep(){
		return DataUtil.getBytesByString("B0F1");
	}
	
	/**
	 * <p>设置睡眠</p>
	 * 
	 * @param onoff  睡眠监测开关：0x 00 关；0x 01 开
	 * @param startHour 睡眠监测开始：小时 (0 ~ 23)
	 * @param startMin 睡眠监测开始：分钟 （0 ~ 59）
	 * @param endHour 睡眠监测结束：小时 （0 ~ 23）
	 * @param endMin 睡眠监测结束： 分钟 （0 ~ 59）
	 * @return
	 * @throws Exception
	 */
	public byte[] writeSleepSetting(int onoff,int startHour,int startMin ,int endHour,int endMin) {
		if (!(onoff==0||onoff==1)
				||startHour<0||startHour>23
				||endHour<0||endHour>23
				||startMin<0||startMin>59
				||endMin<0||endMin>59
				) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("B0");
		sb.append("F2");
		sb.append(DataUtil.integerToHexString(onoff));
		sb.append(DataUtil.integerToHexString(startHour));
		sb.append(DataUtil.integerToHexString(startMin));
		sb.append(DataUtil.integerToHexString(endHour));
		sb.append(DataUtil.integerToHexString(endMin));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * <p>来电提醒</p>
	 * @param onoff 00 来电提醒关；01 来电提醒开
	 * @return 
	 * @throws Exception
	 */
	public byte[] writeIncoming(int onoff) {
		if (!(onoff==0||onoff==1)){
//			throw new Exception("写入协议有错误");
			return null;
		}
		StringBuffer sb =  new StringBuffer();
		sb.append("F0F0");
		sb.append(DataUtil.integerToHexString(onoff));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * <p>每日闹铃</p>
	 * @param onoff 00 关；01 开
	 * @param startHour 闹铃时
	 * @param startMin 闹铃分
	 * @return
	 * @throws Exception
	 */
	public byte[] writeAlarm(int onoff,int startHour,int startMin ) {
		if (!(onoff==0||onoff==1)
				||startHour<0||startHour>23
				||startMin<0||startMin>59
				) {
//			throw new Exception("写入协议有错误");
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("F0");
		sb.append("F1");
		sb.append(DataUtil.integerToHexString(onoff));
		sb.append(DataUtil.integerToHexString(startHour));
		sb.append(DataUtil.integerToHexString(startMin));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * <p>久坐提醒</p>
	 * @param onoff 00 关；01 开
	 * @return 
	 * @throws Exception
	 */
	public byte[] writeLongSit(int onoff) {
		if (!(onoff==0||onoff==1)){
//			throw new Exception("写入协议有错误");
			return null;
		}
		StringBuffer sb =  new StringBuffer();
		sb.append("F0F2");
		sb.append(DataUtil.integerToHexString(onoff));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * <p>生日提醒</P>
	 * @param onoff
	 * @param month
	 * @param day
	 * @param hour
	 * @param min
	 * @return
	 * @throws Exception
	 */
	public byte[] writeBirthday(int onoff,int month,int day,int hour, int min ){
		if (!(onoff==0 || onoff==1)
				|| month<1 || month>12
				|| day<1   || day>31
				|| hour<0  || hour>23
				|| min<0   || hour>59
				) {
//			throw new Exception("写入协议有错误");
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("F0");
		sb.append("F3");
		sb.append(DataUtil.integerToHexString(onoff));
		sb.append(DataUtil.integerToHexString(month));
		sb.append(DataUtil.integerToHexString(day));
		sb.append(DataUtil.integerToHexString(hour));
		sb.append(DataUtil.integerToHexString(min));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * <p>整点报时</p>
	 * @param onoff
	 * @return
	 * @throws Exception
	 */
	public byte[] writeLongPointTime(int onoff) {
		if (!(onoff==0||onoff==1)){
//			throw new Exception("写入协议有错误");
			return null;
		}
		StringBuffer sb =  new StringBuffer();
		sb.append("F0F4");
		sb.append(DataUtil.integerToHexString(onoff));
		return DataUtil.getBytesByString(sb.toString());
	}
	/**
	 * <p>来电短信提醒</p>
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public byte[] writeIncomingForAndroid(int type) throws Exception{
		if (!(type==1||type==2)){
			throw new Exception("写入协议有错误");
		}
		StringBuffer sb =  new StringBuffer();
		sb.append("AAF7");
		sb.append(DataUtil.integerToHexString(type));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	
	/**
	 * 取同步时间发送协议的 hexData  F0F800E00701140D371F
	 * 
	 * @return
	 */
	public  byte[] hexDataForTimeSync(Date date, Context context) {
		StringBuffer sb = new StringBuffer();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int type = DateUtil.getTimeFormat(context);
	
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);

		String yearStr = toHexStringForUpdateTime(year);
		yearStr = yearStr.substring(2, 4) + yearStr.substring(0, 2);

		String yearHighStr = yearStr.substring(0, 2);
		String yearLowStr = yearStr.substring(2, 4);
		//Log.v("", "yearStr : " + yearStr);
		//Log.v("", "yearHighStr : " + yearHighStr);
		//Log.v("", "yearLowStr : " + yearLowStr);

		String monthStr = DataUtil.integerToHexString(month);
		String dayStr = DataUtil.integerToHexString(day);
		String hourStr = DataUtil.integerToHexString(hour);
		String minuteStr = DataUtil.integerToHexString(minute);
		String secondStr = DataUtil.integerToHexString(second);
		//Log.v("DataUtilForProject", year + "-" + month + "-" + day + "  " + hour + ":" + minute + ":" + second);
		String hex = "F0F8";
		sb.append(hex);
		sb.append(yearHighStr);
		sb.append(yearLowStr);
		sb.append(monthStr);
		sb.append(dayStr);
		sb.append(hourStr);
		sb.append(minuteStr);
		sb.append(secondStr);
		//Log.v("", "同步日期协议 : " + sb.toString());
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * 00 :退出 ；01：进入
	 * @param type
	 * @return
	 */
	public  byte[] writeForCamera(int type) {
		StringBuffer sb =new StringBuffer();
		sb.append("F0F6");
		sb.append(DataUtil.integerToHexString(type));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * 未接短信和电话
	 * @param phone
	 * @param sms
	 * @return
	 */
	public  byte[] writeForPhoneAndSmsCount(int phone,int sms) {
		StringBuffer sb = new StringBuffer();
		sb.append("AAF7");
		sb.append(getMissingCallSmsHexString(phone));
		sb.append(getMissingCallSmsHexString(sms));
		return DataUtil.getBytesByString(sb.toString());
	}
	
	/**
	 * 来电号码显示 《android》
	 * @param number
	 * @return
	 */
	public  byte[] writeForIncomingNumber(String number){
		StringBuffer sb = new StringBuffer();
		sb.append("F0");
		sb.append("F9");
		for (int i = 0; i < number.length(); i++) {
			int num = Integer.valueOf(number.charAt(i));
			sb.append(DataUtil.toHexString(num));
		}
		Log.i("xpl", "通知来电 : " + sb.toString());
		return DataUtil.getBytesByString(sb.toString());
	}
	
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private  String toHexStringForUpdateTime(int value) {
		String result = Integer.toHexString(value);
		if (result.length() == 1) {
			result = "000" + result;
		} else if (result.length() == 2) {
			result = "00" + result;
		} else if (result.length() == 3) {
			result = "0" + result;
		}
		return result;
	}
	
	
	private  String getMissingCallSmsHexString(int value) {
		String result = "";
		String hex = Integer.toHexString(value);
		if (hex.length() == 1) {
			result = "000" + hex;
		} else if (hex.length() == 2) {
			result = "00" + hex;
		} else if (hex.length() == 3) {
			result = "0" + hex;
		}
		Log.e("", "未接电话result  : " + result);
		return result;
	}
	
	
	
	
	//责任链模式。
	
}
