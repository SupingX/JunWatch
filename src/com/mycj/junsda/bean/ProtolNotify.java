package com.mycj.junsda.bean;

import java.util.Date;

import android.util.Log;

import com.mycj.junsda.util.DataUtil;
import com.mycj.junsda.view.DateUtil;

public class ProtolNotify {
	private static ProtolNotify protolNotify;

	private ProtolNotify() {
	}

	public static ProtolNotify instance() {
		if (protolNotify == null) {
			protolNotify = new ProtolNotify();
		}
		return protolNotify;
	}

	public int getResult(byte[] data) {
		if (data.length >= 2) {
			String head = DataUtil.byteToHexString(new byte[] { data[0], data[1] });// 头
			if (head.equals(Notify.NOTIFY_STEP.getProtol())) { // 计步
				return 1;
			} else if (head.equals(Notify.NOTIFY_STEP_STATE.getProtol())) {
				return 2;
			} else if (head.equals(Notify.NOTIFY_SLEEP.getProtol())) {
				return 3;
			} else if (head.equals(Notify.NOTIFY_SLEEP_STATE.getProtol())) {
				return 4;
			} else if (head.equals(Notify.NOTIFY_HEART_RATE.getProtol())) {
				return 5;
			} else if (head.equals(Notify.NOTIFY_MUSIC.getProtol())) {
				return Notify.NOTIFY_MUSIC.getIndex();
			} else if (head.equals(Notify.NOTIFY_FIND_PHONE.getProtol())) {
				return Notify.NOTIFY_FIND_PHONE.getIndex();
			} else if (head.equals(Notify.NOTIFY_CAMERA.getProtol())) {
				return Notify.NOTIFY_CAMERA.getIndex();
			} 
		}
		return 0;
	}

	/**
	 * 
	 * <p>
	 * 计步数据
	 * </p>
	 * A0 F0 00 01 86 A0 00 00 A8 今天 计步 运动时间
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public HistorySport notifyHistorySport(byte[] data) {
		if (data.length != 9) {
			Log.e("", "接受的协议长度有错误");
			return null;
		}
		// try {
		// A0 F0 00 00 00 1D 00 00 06
		Date today = new Date();
		String index = DataUtil.byteToHexString(new byte[] { data[2] });// 天数
		Date date = DateUtil.getDateOfDiffDay(today, Integer.valueOf(index));
		String value = DataUtil.byteToHexString(data);
		String step = value.substring(6, 12);
		String time = value.substring(12, 18);
		// String step = DataUtil.byteToHexString(new byte[] { data[3], data[4],
		// data[5] });// 步数
		// String time = DataUtil.byteToHexString(new byte[] { data[6], data[7],
		// data[8] });//
		HistorySport historySport = new HistorySport();
		historySport.setDate(DateUtil.dateToString(date, "yyyyMMdd"));
		historySport.setStep(Integer.valueOf(step, 16));
		historySport.setSportTime(Integer.valueOf(time, 16));
		return historySport;
		// } catch (Exception e) {
		// }
		// return null;
	}

	/**
	 * <p>
	 * 发送所有计步数据前后状态
	 * </p>
	 * 
	 * @param data
	 * @return 0：开始 ；1：结束
	 * @throws Exception
	 */
	public int notifySyncSportState(byte[] data) {
		if (data.length != 3) {
			// throw new Exception("接受的协议长度有错误");
			return -1;
		}
		String state = DataUtil.byteToHexString(new byte[] { data[2] });// 发送开始/结束
		return Integer.valueOf(state);
	}

	/**
	 * 
	 * <p>
	 * 睡眠数据
	 * </p>
	 * 0x B0 F0 01 01 68 00 46
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public HistorySleep notifyHistorySleep(byte[] data) {
		if (data.length != 7) {
			// throw new Exception("接受的协议长度有错误");
			return null;
		}
		try {
			Date today = new Date();
			String index = DataUtil.byteToHexString(new byte[] { data[2] });// 天数
			Date date = DateUtil.getDateOfDiffDay(today, Integer.valueOf(index));
			String deep = DataUtil.byteToHexString(new byte[] { data[3], data[4] });// 深睡
			String light = DataUtil.byteToHexString(new byte[] { data[5], data[6] });// 浅睡
			HistorySleep historySleep = new HistorySleep();
			historySleep.setDate(DateUtil.dateToString(date, "hhhhMMdd"));
			historySleep.setDeep(Integer.valueOf(deep));
			historySleep.setLight(Integer.valueOf(light));
			return historySleep;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * <p>
	 * 发送所有睡眠数据前后状态
	 * </p>
	 * 
	 * @param data
	 * @return 0：开始 ；1：结束
	 * @throws Exception
	 */
	public int notifySyncSleepState(byte[] data) {
		if (data.length != 3) {
			// throw new Exception("接受的协议长度有错误");
			return -1;
		}
		String state = DataUtil.byteToHexString(new byte[] { data[2] });// 发送开始/结束
		return Integer.valueOf(state);
	}

	/**
	 * <p>
	 * 心率数据/p> 只有当手环处于测试心率界面，开始测试心率并得到测试结果时，才会发送此协议
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public int notifyHeartRate(byte[] data) {
		if (data.length != 3) {
			// throw new Exception("接受的协议长度有错误");
			return -1;
		}
		String value = DataUtil.byteToHexString(data);
		String substring = value.substring(0, 4);
		if (substring.equals(Notify.NOTIFY_HEART_RATE.getProtol())) {
			return data[2];
		}
		return -1;
	}

	/**
	 * 查找手机
	 * 
	 * @param data
	 * @return
	 */
	public int notifyFindPhone(byte[] data) {
		if (data.length != 3) {
			return -1;
		}
		String value = DataUtil.byteToHexString(data);
		String substring = value.substring(0, 4);
		if (substring.equals(Notify.NOTIFY_FIND_PHONE.getProtol())) {
			return data[2];
		}
		return -1;
	}

	/**
	 * 拍照
	 * 
	 * @param data
	 * @return
	 */
	public int notifyTakePicture(byte[] data) {
		if (data.length != 2) {
			return -1;
		}
		String value = DataUtil.byteToHexString(data);
		if (value.equals(Notify.NOTIFY_CAMERA.getProtol())) {
			return 1;
		}
		return -1;
	}

	/**
	 * 音乐 0 ：停止 ；1 ：开始；2：上一曲；3：下一曲；
	 * 
	 * @param data
	 * @return
	 */
	public int notifyMusic(byte[] data) {
		if (data.length != 3) {
			return -1;
		}
		String value = DataUtil.byteToHexString(data);
		String substring = value.substring(0, 4);
		if (substring.equals(Notify.NOTIFY_MUSIC.getProtol())) {
			return data[2];
		}
		return -1;
	}

}
