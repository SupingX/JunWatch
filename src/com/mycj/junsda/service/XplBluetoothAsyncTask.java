package com.mycj.junsda.service;


import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.AsyncTask;

public class XplBluetoothAsyncTask extends AsyncTask<byte[], Void, Void> {
	private BluetoothGatt gatt;
	private long delayTime ;
	public XplBluetoothAsyncTask(BluetoothGatt gatt){
		this.gatt = gatt;
		this.delayTime = 200;
	}
	public XplBluetoothAsyncTask(BluetoothGatt gatt,long delayTime){
		this.delayTime = delayTime;
		this.gatt = gatt;
	}
	@Override
	protected Void doInBackground(byte[]... params) {
		try {
			Thread.sleep(delayTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BluetoothGattService service = gatt.getService(UUID.fromString(XplBluetoothService.UUID_SERVICE));
		BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(XplBluetoothService.UUID_CHARACTERISTIC_WRITE));
		characteristic.setValue(params[0]);
		gatt.writeCharacteristic(characteristic);
		return null;
	}

}
