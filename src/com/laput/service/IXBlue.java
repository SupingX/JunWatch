package com.laput.service;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;


public interface IXBlue {
	public final static int SCAN_STATE_START = 0x1;
	public final static int SCAN_STATE_STOP = 0x0;
	public final static int CONNECT_STATE_CONNECTED = 0x1;
	public final static int CONNECT_STATE_CONNECTING = 0x2;
	public final static int CONNECT_STATE_DISCONNECTED = 0x0;
	public final static String UUID_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
	public final static String UUID_CHARACTERISTIC_NOTIFY = "0000fff1-0000-1000-8000-00805f9b34fb";
	public final static String UUID_CHARACTERISTIC_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";
	public final static String UUID_DESC_CCC = "00002902-0000-1000-8000-00805f9b34fb";
	public BluetoothManager getBluetoothManager();

	public BluetoothAdapter getBluetoothAdapter();
	
	public boolean isSupportBle();
	
	public boolean isBluetoothAdapterEnable();
	
	public void enableBluetooth();
	
	public void scanDevice(boolean enable);
	public void scanDevice(UUID[] serviceUuids,boolean enable);
	
	public int getScanningState();
	
	public int getConnectState(BluetoothDevice device);
	
	public void connect(BluetoothDevice device);
	public void connect(String address);
	
	public void close(BluetoothGatt gatt);
	
	public void closeAll();
	
	public void writeCharacteristic(BluetoothGatt gatt, byte[] order);
	
}
