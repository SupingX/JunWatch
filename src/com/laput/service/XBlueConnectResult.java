package com.laput.service;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class XBlueConnectResult {
	private XBluetoothGattCallBack callback;
	private BluetoothGatt gatt;
	private String address;
	private ConnectState connectState; //
	
	public ConnectState getConnectState() {
		return connectState;
	}
	public void setConnectState(ConnectState connectState) {
		this.connectState = connectState;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public XBluetoothGattCallBack getCallback() {
		return callback;
	}
	public void setCallback(XBluetoothGattCallBack callback) {
		this.callback = callback;
	}
	public BluetoothGatt getGatt() {
		return gatt;
	}
	public void setGatt(BluetoothGatt gatt) {
		this.gatt = gatt;
	}
//	public XBlueConnectResult(XBluetoothGattCallBack callback, BluetoothGatt gatt) {
//		super();
//		this.callback = callback;
//		this.gatt = gatt;
//	}
	public XBlueConnectResult(XBluetoothGattCallBack callback, BluetoothGatt gatt, String address) {
		super();
		this.callback = callback;
		this.gatt = gatt;
		this.address = address;
	}
	public XBlueConnectResult() {
		super();
	}
	@Override
	public String toString() {
		return "XBlueConnectResult [callback=" + callback + ", gatt=" + gatt + ", address=" + address + "]";
	}
	
	public void close(){
		try {
			if (gatt != null) {
				BluetoothDevice device = gatt.getDevice();
				int bondState = device.getBondState();
				if (bondState == BluetoothDevice.BOND_BONDED) {
					System.out.println("已绑定");
					removeBond(BluetoothDevice.class, device);
				} 
				else if (bondState == BluetoothDevice.BOND_BONDING) {
					System.out.println("绑定中");
					cancelBondProcess(BluetoothDevice.class, device);
					removeBond(BluetoothDevice.class, device);
				} else if (bondState == BluetoothDevice.BOND_NONE) {
					System.out.println("没绑定");
				}
				gatt.close();
				XLog.sys(this.address + "<--=== 掉线了关闭地址");
				connectState = ConnectState.DISCONNECT;
				callback = null;
				gatt = null;

			}
		} catch (Exception e) {
			gatt.close();
			connectState = ConnectState.DISCONNECT;
			callback = null;
			gatt = null;
		}
	}
	
		public boolean cancelBondProcess(Class<?> btClass, BluetoothDevice device) throws Exception  {
			Method createBondMethod = btClass.getMethod("cancelBondProcess");
			Boolean returnValue = (Boolean) createBondMethod.invoke(device);
			return returnValue.booleanValue();
		}

		public boolean removeBond(Class<?> btClass, BluetoothDevice btDevice) throws Exception {
			Method removeBondMethod = btClass.getMethod("removeBond");
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
			return returnValue.booleanValue();
		}
	
}
