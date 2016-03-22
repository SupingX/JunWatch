package com.laput.service;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;

public class XBlueBondedUtils {
	public static boolean cancelBondProcess(Class<?> btClass, BluetoothDevice device) throws Exception  {
		Method createBondMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		return returnValue.booleanValue();
	}

	public static boolean removeBond(Class<?> btClass, BluetoothDevice btDevice) throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}
}
