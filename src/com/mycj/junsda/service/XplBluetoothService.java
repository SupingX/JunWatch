package com.mycj.junsda.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;
import com.mycj.junsda.bean.Notify;
import com.mycj.junsda.bean.ProtolNotify;
import com.mycj.junsda.bean.ProtolWrite;
import com.mycj.junsda.broadcast.AbstractBluetoothStateBroadcastReceiver;
import com.mycj.junsda.util.DataUtil;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public abstract class XplBluetoothService extends Service {
	private final String TAG = XplBluetoothService.class.getSimpleName();
	public final static String UUID_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
	public final static String UUID_CHARACTERISTIC_NOTIFY = "0000fff1-0000-1000-8000-00805f9b34fb";
	public final static String UUID_CHARACTERISTIC_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";
	public final static String UUID_DESC_CCC = "00002902-0000-1000-8000-00805f9b34fb";
	public static final int REQUEST_ENABLE_BLUETOOTH = 0xaa;
	protected BluetoothManager mBluetoothManager;
	protected BluetoothAdapter mbleBluetoothAdapter;
	private boolean isServiceDiscovered;
	private List<BluetoothDevice> scanDevices = new ArrayList<BluetoothDevice>();
	protected BluetoothGatt currentGatt;
	protected boolean isScanning;
	private AbstractBluetoothStateBroadcastReceiver mBluetoothStateBroadcastReceiver = new AbstractBluetoothStateBroadcastReceiver() {

		@Override
		public void onBluetoothChange(int state, int previousState) {
			if (state == BluetoothAdapter.STATE_ON) {
				scanDevice(true);
			} else {

			}
		}

		@Override
		public void onBluetoothDisconnect(final BluetoothDevice device) {
			// xplBluetoothService.close(AdressSaved.getBindedAddress(getApplicationContext()));
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (device.getAddress().equals(currentAddress) || !isBluetoothConnected()) {
						isServiceDiscovered = false;
						close();
						sendBroadcastConnectState(device, BluetoothGatt.STATE_DISCONNECTED);
						scanDevice(false);
						scanDevices.clear();
						scanDevice(true);
					}
				}
			});
		}

	};

	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
		}
	};

	/**
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> service
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 **/
	/*
	 * 主要是service的生命周期方法，一些初始化值
	 */
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		super.onCreate();
		mbleBluetoothAdapter = getBluetoothAdapter();
		currentAddress = AddressSaved.getBindedAddress(getApplicationContext());
		Log.e("", "onCreate() 当前存贮设备地址：" + currentAddress);
		filter = new IntentFilter();
		filter.addAction(ACTION_DEVICE_FOUND);
		filter.addAction(ACTION_ADAPTER_DISNABLE);
		filter.addAction(ACTION_CONNECT_STATE);
		filter.addAction(ACTION_SERVICE_DISCOVERED);

		mBluetoothStateBroadcastReceiver.registerBoradcastReceiverForCheckBlueToothState(getApplicationContext());
	}

	public String getCurrentAddress() {
		return this.currentAddress;
	}

	private String currentAddress;

	public void setCurrentAddressEmpty() {
		this.currentAddress = "";
		if (currentGatt != null) {
			this.currentGatt.close();
		}
		isServiceDiscovered = false;
	}

	public void disconnect() {
		if (currentGatt != null) {
			this.currentGatt.disconnect();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		return new XplBinder();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		close();
		currentAddress = "";
		scanDevices.clear();
		unregisterReceiver(mBluetoothStateBroadcastReceiver);
		super.onDestroy();
	}

	public class XplBinder extends Binder {
		public XplBluetoothService getXplBluetoothService() {
			return XplBluetoothService.this;
		}
	}

	/**
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> service
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 **/

	/**
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> bluetooth
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 **/
	/**
	 * <p>
	 * 是否支持 Ble
	 * </p>
	 * <b> {uses-feature android:name="android.hardware.bluetooth_le"
	 * android:required="true"/></B> required为true时，则应用只能在支持BLE的Android设备上安装运行；<br/>
	 * required为false时，Android设备均可正常安装运行，需要在代码运行时判断设备是否支持BLE feature： <br/>
	 * 
	 * @return isSupport
	 */
	public boolean isSupportBle() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}

	/**
	 * <p>
	 * 获取BluetoothAdapter
	 * </p>
	 * 注：这里通过getSystemService获取BluetoothManager，
	 * 再通过BluetoothManager获取BluetoothAdapter。BluetoothManager在Android4.3以上支持(API
	 * level 18)。
	 *
	 * @return BluetoothAdapter
	 */
	public BluetoothAdapter getBluetoothAdapter() {
		mBluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(BLUETOOTH_SERVICE);
		return mBluetoothManager.getAdapter();
	}

	/**
	 * <p>
	 * 判断是否打开蓝牙
	 * </p>
	 * 
	 * @return isEnable
	 */
	public boolean isBluetoothEnable() {
		if (mbleBluetoothAdapter == null || !mbleBluetoothAdapter.isEnabled()) {
			// Toast.makeText(getApplicationContext(), "蓝牙未打开",
			// Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(ACTION_BLUETOOTH_ENABLE);
			sendBroadcast(intent);
			Log.e(TAG, "mbleBluetoothAdapter 无效");
			return false;
		}
		return true;
	}

	/**
	 * <p>
	 * 通过 intent打开 蓝牙
	 * </p>
	 * 
	 * 在onActivityResult获取结果
	 * 
	 * @param ac
	 */
	public void enableBluetooth(Activity ac) {
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		ac.startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
	}

	// 取消配对
	public boolean cancelBondProcess(Class<?> btClass, BluetoothDevice device) throws Exception {
		Method createBondMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		System.out.println(" 取消配对进程" + returnValue.booleanValue());
		return returnValue.booleanValue();
	}

	//
	public boolean removeBond(Class<?> btClass, BluetoothDevice btDevice) throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		System.out.println(" 取消配对" + returnValue.booleanValue());
		return returnValue.booleanValue();
	}

	public void isDeviceBindedRemote() {
		try {
			BluetoothDevice remoteDevice = mbleBluetoothAdapter.getRemoteDevice(currentAddress);
			if (remoteDevice != null) {
				int bondState = remoteDevice.getBondState();
				if (bondState == BluetoothDevice.BOND_BONDED) {
					System.out.println("已绑定");
					removeBond(BluetoothDevice.class, remoteDevice);
				} else if (bondState == BluetoothDevice.BOND_BONDING) {
					System.out.println("绑定中");
					cancelBondProcess(BluetoothDevice.class, remoteDevice);
					// removeBond(BluetoothDevice.class, remoteDevice);
				} else if (bondState == BluetoothDevice.BOND_NONE) {
					System.out.println("没有绑定");
					// cancelBondProcess(BluetoothDevice.class, remoteDevice);
					// removeBond(BluetoothDevice.class, remoteDevice);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * <p>
	 * 搜索回调
	 * </p>
	 * 
	 */
	private LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
			sendBroadcastDeviceFound(device, rssi);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (isServiceDiscovered) {
						return;
					}

					if (currentAddress == null || currentAddress.equals("")) {
						return;
					}

//					isDeviceBindedRemote();
					// if (isDeviceBindedRemote()) {
					// scanDevice(false);
					// if (device.getAddress().equals(currentAddress)) {
					// connect(currentAddress);
					// }
					// mHandler.postDelayed(new Runnable() {
					// @Override
					// public void run() {
					// if (!isBluetoothConnected()) {
					// Log.e("", "20秒后还是没有连接成功");
					// scanDevices.remove(device);
					// scanDevice(true);
					// }
					// }
					// }, 20 * 1000);
					// return ;
					// }

					if (!device.getAddress().equals(currentAddress)) {
						return;
					}
					if (!scanDevices.contains(device)) {
						scanDevices.add(device);
						connect(currentAddress);
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (!isBluetoothConnected()) {
									Log.e("", "20秒后还是没有连接成功");
									scanDevices.remove(device);
								}
							}
						}, 20 * 1000);
					}
				}
			}, 500);
		}
	};

	/**
	 * <p>
	 * 搜索设备
	 * </p>
	 * 
	 * @param enable
	 */
	public void scanDevice(boolean enable) {
		if (!isSupportBle()) {
			return;
		}
		if (!isBluetoothEnable()) {
			return;
		}
		if (enable) {
			Log.e(TAG, "开始搜索");
			mbleBluetoothAdapter.stopLeScan(mLeScanCallback);
			mbleBluetoothAdapter.startLeScan(mLeScanCallback);
			isScanning = true;
		} else {
			Log.e(TAG, "结束搜索");
			mbleBluetoothAdapter.stopLeScan(mLeScanCallback);
			isScanning = false;
		}
		sendBroadcastScanState(isScanning);
	}

	/**
	 * <p>
	 * 搜索指定serviceUuids设备
	 * </p>
	 * 
	 * @param serviceUuids
	 */
	public void scanDevice(UUID[] serviceUuids) {
		if (!isSupportBle()) {
			return;
		}
		if (!isBluetoothEnable()) {
			return;
		}
		Log.i(TAG, "结束搜索 制定serviceUuid ：" + serviceUuids.toString());
		isScanning = true;
		mbleBluetoothAdapter.startLeScan(serviceUuids, mLeScanCallback);
	}

	private class XplBluetoothGattCallback extends BluetoothGattCallback {
		// private BluetoothGattCallback mBluetoothGattCallback = new
		// BluetoothGattCallback() {

		@Override
		public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
			Log.i(TAG, "onCharacteristicChanged() ");
			super.onCharacteristicChanged(gatt, characteristic);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					doCharacteristicChanged(gatt, characteristic);
				}
			});
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "onCharacteristicRead() 成功");
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				Log.i(TAG, "onCharacteristicRead() 失败");
			}
			super.onCharacteristicRead(gatt, characteristic, status);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "onCharacteristicWrite() 成功 --> "+gatt.getDevice().getAddress() +"<>"+ DataUtil.byteToHexString(characteristic.getValue()));
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				Log.i(TAG, "onCharacteristicWrite() 失败");
			}
			super.onCharacteristicWrite(gatt, characteristic, status);
		}

		@Override
		public void onConnectionStateChange(final BluetoothGatt gatt, int status, final int newState) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "onConnectionStateChange() 成功");
				sendBroadcastConnectState(gatt.getDevice(), newState);
				switch (newState) {
				case BluetoothProfile.STATE_CONNECTED:
					Log.e(TAG, "已连接 state ：" + newState);
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							gatt.discoverServices();
							doConnected(gatt, newState);
						}
					}, 200);

					break;
				case BluetoothProfile.STATE_DISCONNECTED:
					Log.e(TAG, "已断开 state ：" + newState);
					mHandler.post(new Runnable() {
						public void run() {
							scanDevice(false);
							if (currentGatt != null) {//当掉线的Gatt和当前的Gatt是同一个时，说明此手环掉线了
								if (gatt.getDevice().getAddress().equals(currentGatt.getDevice().getAddress())) {
									currentGatt.close();
									currentGatt = null;
									isServiceDiscovered = false;
									doDisconnected(gatt, newState);
									scanDevices.clear();
									scanDevice(true);
								
								}else{
									gatt.close();
								}
							}else{
								gatt.close();
							}
						
						}
					});  
					break;
				default:
					break;
				}
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				Log.i(TAG, "onConnectionStateChange() 失败");
			}
			super.onConnectionStateChange(gatt, status, newState);
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "onDescriptorRead() 成功");
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				Log.i(TAG, "onDescriptorRead() 失败");
			}
			super.onDescriptorRead(gatt, descriptor, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "onDescriptorWrite() 成功");
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				Log.i(TAG, "onDescriptorWrite() 失败");
			}
			super.onDescriptorWrite(gatt, descriptor, status);
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "onReadRemoteRssi() 成功");
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				Log.i(TAG, "onReadRemoteRssi() 失败");
			}
			super.onReadRemoteRssi(gatt, rssi, status);
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "onReliableWriteCompleted() 成功");
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				Log.i(TAG, "onReliableWriteCompleted() 失败");
			}
			super.onReliableWriteCompleted(gatt, status);
		}

		@Override
		public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				mHandler.post(new Runnable() {
					public void run() {

						parseGatt(gatt);
					}
				});
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				Log.i(TAG, "onServicesDiscovered() 失败");
			}
			super.onServicesDiscovered(gatt, status);
		}

	};

	public BluetoothGatt connect(BluetoothDevice device) {
		if (!isSupportBle()) {
			return null;
		}
		if (!isBluetoothEnable()) {
			return null;
		}
		if (device == null) {
			return null;
		}

		// BluetoothGatt gatt = device.connectGatt(getApplicationContext(),
		// false,
		// mBluetoothGattCallback);
		BluetoothGatt gatt = device.connectGatt(getApplicationContext(), false, new XplBluetoothGattCallback());
		Log.e(TAG, "<<<<<<<<开始连接>>>>>>>>>" + device.getAddress());
		return gatt;
	}

	public BluetoothGatt connect(String address) {
		if (address == null || address.equals("")) {
			Log.e(TAG, "蓝牙地址为空");
			return null;
		}
		boolean checkBluetoothAddress = BluetoothAdapter.checkBluetoothAddress(address);
		if (!checkBluetoothAddress) {
			Log.e(TAG, "蓝牙地址不可用");
			return null;
		}
		BluetoothDevice remoteDevice = mbleBluetoothAdapter.getRemoteDevice(address);
		BluetoothGatt gatt = connect(remoteDevice);
		return gatt;
	}

	public boolean isBluetoothConnected() {
		boolean is = isServiceDiscovered && !currentAddress.equals("") && currentGatt != null;
		Log.e("", "是否连接 : " + is);
		return is;
	}

	public boolean checkBluetoothAddress(String address) {
		return BluetoothAdapter.checkBluetoothAddress(address) || address == null;
	}

	public void close() {
		if (currentGatt != null) {
			currentGatt.close();
		}
	}

	public void writeCharacteristic(byte[] order) {
		if (currentGatt == null) {
			return;
		}
		BluetoothGattService service = currentGatt.getService(UUID.fromString(UUID_SERVICE));
		BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_WRITE));
		characteristic.setValue(order);
		currentGatt.writeCharacteristic(characteristic);
	}

	/**
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 * 蓝牙<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 **/

	public static final String ACTION_DEVICE_FOUND = "ACTION_DEVICE_FOUND";
	public static final String ACTION_ADAPTER_DISNABLE = "ACTION_ADAPTER_DISNABLE";
	public static final String ACTION_CONNECT_STATE = "ACTION_CONNECT_STATE";
	public static final String ACTION_SERVICE_DISCOVERED = "ACTION_SERVICE_DISCOVERED";
	public static final String ACTION_BLUETOOTH_ENABLE = "ACTION_BLUETOOTH_ENABLE";
	public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
	public static final String EXTRA_RSSI = "EXTRA_RSSI";
	public static final String EXTRA_CONNECT_STATE = "EXTRA_CONNECT_STATE";
	protected static IntentFilter filter;

	/**
	 * <p>
	 * 获取所有关于此service的Action
	 * </p>
	 * 
	 * @return IntentFilter
	 */
	public static IntentFilter getIntentFilter() {
		return filter;
	}

	protected void sendBroadcastDeviceFound(BluetoothDevice device, int rssi) {
		// Log.i(TAG, "发送通知 ：DeviceFound");
		Intent intent = new Intent(ACTION_DEVICE_FOUND);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_RSSI, rssi);
		sendBroadcast(intent);
	}

	protected void sendBroadcastBleDisnable() {
		Intent intent = new Intent(ACTION_ADAPTER_DISNABLE);
		sendBroadcast(intent);
	}

	protected void sendBroadcastConnectState(BluetoothDevice device, int newState) {
		Intent intent = new Intent(ACTION_CONNECT_STATE);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_CONNECT_STATE, newState);
		sendBroadcast(intent);
	};

	protected void sendBroadcastScanState(boolean isScanning) {
	}

	protected void sendBroadcastForServiceDiscoveredWriteDevice(BluetoothGatt gatt) {
		Intent intent = new Intent(ACTION_SERVICE_DISCOVERED);
		intent.putExtra(EXTRA_DEVICE, gatt.getDevice());
		sendBroadcast(intent);
	}

	/**
	 * <p>
	 * characteristic变化时 ，发送通知
	 * </p>
	 */
	private void sendBroadcastCharacteristicChanged(BluetoothGatt gatt, byte[] data) {

	}

	/*
	 * 蓝牙一些状态的回调，可分装成抽象方法 ，供子类实现
	 */
	protected abstract void doDisconnected(BluetoothGatt gatt, int newState);

	protected abstract void doConnected(BluetoothGatt gatt, int newState);

	protected abstract void doServiceDiscovered(BluetoothGatt gatt);

	protected abstract void doCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

	/**
	 * <p>
	 * servicediscovered时 解析gatt是否是我们需要找的gatt
	 * </p>
	 * 
	 * @param gatt
	 */
	private void parseGatt(BluetoothGatt gatt) {
		scanDevice(false);

	

		BluetoothGattService service = gatt.getService(UUID.fromString(UUID_SERVICE));
		if (service != null) {
			BluetoothGattCharacteristic characteristicNotify = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_NOTIFY));
			BluetoothGattCharacteristic characteristicWrite = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_WRITE));
			if (characteristicNotify != null && characteristicWrite != null) {
				characteristicWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);// 不带相应？
				gatt.setCharacteristicNotification(characteristicNotify, true);// 设置characteristic属性改变时发送通知
				BluetoothGattDescriptor descriptor = characteristicNotify.getDescriptor(UUID.fromString(UUID_DESC_CCC));
				if (descriptor != null) {
					
				
					
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);// 设置descriptor属性改变时发送通知
					gatt.writeDescriptor(descriptor);
					AddressSaved.saveDevice(getApplicationContext(), gatt.getDevice());// 保存一个蓝牙
					
					if (currentGatt != null) {
						if (gatt.getDevice().getAddress() != currentGatt.getDevice().getAddress()) {
							System.out.println("找到的服务不是当前服务 ， 所以要关闭当前服务");
							currentGatt.close();
							currentGatt = null;
						}
					}
					
					currentAddress = gatt.getDevice().getAddress();
					isServiceDiscovered = true;
					currentGatt = gatt;
					sendBroadcastForServiceDiscoveredWriteDevice(currentGatt);

					doServiceDiscovered(gatt);
				} else {
					Log.i(TAG, "无法找到descriptor");
					scanDevices.clear();
					gatt.close();
					isServiceDiscovered = false;
					gatt = null;
					scanDevice(true);
				}
			} else {
				Log.i(TAG, "无法找到characteristic");
				scanDevices.clear();
				gatt.close();
				isServiceDiscovered = false;
				gatt = null;
				scanDevice(true);
			}
		} else {
			Log.i(TAG, "无法找到service");
			scanDevices.clear();
			gatt.close();
			isServiceDiscovered = false;
			gatt = null;
			scanDevice(true);
		}
	}

	/**
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 扩展
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 **/

}
