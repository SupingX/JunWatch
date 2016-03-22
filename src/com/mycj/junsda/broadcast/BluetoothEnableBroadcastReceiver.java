package com.mycj.junsda.broadcast;

import com.mycj.junsda.service.XplBluetoothService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class BluetoothEnableBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(XplBluetoothService.ACTION_BLUETOOTH_ENABLE)) {
			doBluetoothEnable();
		}
	}
	public abstract void doBluetoothEnable() ;
}
