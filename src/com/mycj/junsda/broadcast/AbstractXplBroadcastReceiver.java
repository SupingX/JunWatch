package com.mycj.junsda.broadcast;



import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;
import com.mycj.junsda.service.JunsdaXplBluetoothService;
import com.mycj.junsda.service.XplBluetoothService;



/**
 * <p>对于XplBluetoothService的BroadcastReceiver</p>
 * @author Administrator
 *
 */
public abstract class AbstractXplBroadcastReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(XplBluetoothService.ACTION_DEVICE_FOUND)) {
			BluetoothDevice device = intent.getExtras().getParcelable(XplBluetoothService.EXTRA_DEVICE);
			int rssi = intent.getExtras().getInt(XplBluetoothService.EXTRA_RSSI);
			doDeviceFound(device,rssi);
		}else if (action.equals(XplBluetoothService.ACTION_CONNECT_STATE)) {
			BluetoothDevice device = intent.getExtras().getParcelable(XplBluetoothService.EXTRA_DEVICE);
			int state = intent.getExtras().getInt(XplBluetoothService.EXTRA_CONNECT_STATE);
			doConnectStateChange(device, state);
		}else if (action.equals(XplBluetoothService.ACTION_SERVICE_DISCOVERED)) {
			BluetoothDevice device = intent.getExtras().getParcelable(XplBluetoothService.EXTRA_DEVICE);
			doServiceDisCovered(device);
		}
		//扩展
		else if (JunsdaXplBluetoothService.ACTION_SPORT.equals(action)) {
			HistorySport sport = intent.getParcelableExtra(JunsdaXplBluetoothService.EXTRA_SPORT);
			doSportChanged(sport);
		}
		else if (JunsdaXplBluetoothService.ACTION_SPORT_STATE.equals(action)) {
			int state = intent.getExtras().getInt((JunsdaXplBluetoothService.EXTRA_SPORT_STATE));
			doSportSyncStateChanged(state);
		}
		else if (JunsdaXplBluetoothService.ACTION_SLEEP.equals(action)) {
			HistorySleep sleep = intent.getParcelableExtra((JunsdaXplBluetoothService.EXTRA_SLEEP));
			doSleepChanged(sleep);
		}
		else if (JunsdaXplBluetoothService.ACTION_SLEEP_STATE.equals(action)) {
			int state = intent.getExtras().getInt((JunsdaXplBluetoothService.EXTRA_SLEEP_STATE));
			doSleepSyncStateChanged(state);
		}
		else if (JunsdaXplBluetoothService.ACTION_HEART_RATE.equals(action)) {
			int hr = intent.getExtras().getInt((JunsdaXplBluetoothService.EXTRA_HEART_RATE));
			doHeartRateChanged(hr);
		}
		
	}
	
	

	public abstract void doSportChanged(HistorySport sport) ;
	public abstract void doSportSyncStateChanged(int state) ;
	public abstract void doSleepChanged(HistorySleep sleep) ;
	public abstract void doSleepSyncStateChanged(int state) ;
	public abstract void doHeartRateChanged(int hr) ;



	/**
	 * <p>蓝牙设备找到后处理的回调</p>
	 * @param device
	 * @param rssi
	 */
	public abstract void doDeviceFound(BluetoothDevice device, int rssi) ;
	/**
	 * <p>蓝牙设备信号变化的回调</p>
	 * @param device
	 * @param state
	 */
	public abstract void doConnectStateChange(BluetoothDevice device, int state) ;
	/**
	 * <p>service找到（即设备完全连接好）的回调</p>
	 * @param device
	 */
	public abstract void doServiceDisCovered(BluetoothDevice device) ;
	
	
	
	public abstract void doBluetoothEnable() ;
}
