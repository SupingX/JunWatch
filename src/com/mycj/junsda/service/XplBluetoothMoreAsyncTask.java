package com.mycj.junsda.service;


import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.AsyncTask;

public class XplBluetoothMoreAsyncTask extends AsyncTask<byte[][], Void, Void> {
	private BluetoothGatt gatt;
	private long delayTime ;
	public XplBluetoothMoreAsyncTask(BluetoothGatt gatt){
		this.gatt = gatt;
		this.delayTime = 200;
	}
	public XplBluetoothMoreAsyncTask(BluetoothGatt gatt,long delayTime){
		this.delayTime = delayTime;
		this.gatt = gatt;
	}
	@Override
	protected Void doInBackground(byte[][]... params) {
		if (gatt==null) {
			return null;
		}
		try {
			
		byte[][] bs = params[0];
		for (int i = 0; i < bs.length; i++) {
			BluetoothGattService service = gatt.getService(UUID.fromString(XplBluetoothService.UUID_SERVICE));
			BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(XplBluetoothService.UUID_CHARACTERISTIC_WRITE));
			characteristic.setValue(bs[i]);
			gatt.writeCharacteristic(characteristic);
			Thread.sleep(delayTime);
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		return null;
	}

}
