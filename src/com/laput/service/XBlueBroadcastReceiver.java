package com.laput.service;

import java.util.ArrayList;

import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;
import com.mycj.junsda.service.JunsdaXplBluetoothService;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class XBlueBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (XBlueBroadcastUtils.ACTION_DEVICE_FOUND.equals(action)) {
			ArrayList<BluetoothDevice> devices = intent.getParcelableArrayListExtra(XBlueBroadcastUtils.EXTRA_DEVICES);
			doDeviceFound(devices);
		} else if (XBlueBroadcastUtils.ACTION_SERVICE_DISCOVERED.equals(action)) {
			BluetoothDevice device = intent.getParcelableExtra(XBlueBroadcastUtils.EXTRA_DEVICE);
			doServiceDiscovered(device);
		} else if (action.equals(XBlueBroadcastUtils.ACTION_CONNECT_STATE)) {
			BluetoothDevice device = intent.getExtras().getParcelable(XBlueBroadcastUtils.EXTRA_DEVICE);
			int state = intent.getExtras().getInt(XBlueBroadcastUtils.EXTRA_CONNECT_STATE);
			doConnectStateChange(device, state);
		}

		// 扩展
		else if (XBlueBroadcastUtils.ACTION_SPORT.equals(action)) {
			HistorySport sport = intent.getParcelableExtra(XBlueBroadcastUtils.EXTRA_SPORT);
			doSportChanged(sport);
		} else if (XBlueBroadcastUtils.ACTION_SPORT_STATE.equals(action)) {
			int state = intent.getExtras().getInt((XBlueBroadcastUtils.EXTRA_SPORT_STATE));
			doSportSyncStateChanged(state);
		} else if (XBlueBroadcastUtils.ACTION_SLEEP.equals(action)) {
			HistorySleep sleep = intent.getParcelableExtra((XBlueBroadcastUtils.EXTRA_SLEEP));
			doSleepChanged(sleep);
		} else if (XBlueBroadcastUtils.ACTION_SLEEP_STATE.equals(action)) {
			int state = intent.getExtras().getInt((XBlueBroadcastUtils.EXTRA_SLEEP_STATE));
			doSleepSyncStateChanged(state);
		} else if (XBlueBroadcastUtils.ACTION_HEART_RATE.equals(action)) {
			int hr = intent.getExtras().getInt((XBlueBroadcastUtils.EXTRA_HEART_RATE));
			doHeartRateChanged(hr);
		}

	}

	public abstract void doServiceDiscovered(BluetoothDevice device);
	public abstract void doDeviceFound(ArrayList<BluetoothDevice> devices);
	public abstract void doConnectStateChange(BluetoothDevice device, int state);
	public abstract void doBluetoothEnable();
	
	//扩展
	public abstract void doSportChanged(HistorySport sport);
	public abstract void doSportSyncStateChanged(int state);
	public abstract void doSleepChanged(HistorySleep sleep);
	public abstract void doSleepSyncStateChanged(int state);
	public abstract void doHeartRateChanged(int hr);

}
