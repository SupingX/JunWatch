package com.laput.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.litepal.crud.DataSupport;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;
import com.mycj.junsda.bean.Notify;
import com.mycj.junsda.bean.ProtolNotify;
import com.mycj.junsda.bean.ProtolWrite;
import com.mycj.junsda.bean.StaticValue;
import com.mycj.junsda.util.SharedPreferenceUtil;
import com.mycj.junsda.view.DateUtil;

public class XBlueManager implements IXBlue {

	// public final static int TYPE_ONE=0x01;
	// public final static int TYPE_MORE = 0x02;
	// private int type = TYPE_ONE;//1
	private static XBlueManager xBlue;
	private Context context;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private HashMap<String, BluetoothDevice> scanDeviceMap = new HashMap<String, BluetoothDevice>();
	private int scanState;
	private HashMap<String, XBlueConnectResult> connectResultsMap = new HashMap<String, XBlueConnectResult>();
	public final static int SCAN_PERIOD = 14 * 1000;
	private Handler mHandler = new Handler() {

	};

	private XBlueManager(Context context) {
		this.context = context;
		mBluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
//		music = MusicManager.instance(context);
	}

	public static XBlueManager instance(Context context) {
		if (xBlue == null) {
			xBlue = new XBlueManager(context);

		}
		return xBlue;
	};

	@Override
	public BluetoothManager getBluetoothManager() {
		return this.mBluetoothManager;
	}

	@Override
	public BluetoothAdapter getBluetoothAdapter() {
		return this.mBluetoothAdapter;
	}

	@Override
	public boolean isSupportBle() {
		if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public boolean isBluetoothAdapterEnable() {
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {

			return false;
		} else {
			return true;
		}

	}

	public HashMap<String, XBlueConnectResult> getConnectResultsMap() {
		return this.connectResultsMap;
	}

	@Override
	public void enableBluetooth() {
		// Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		// context.startActivity(intent);
		if (isSupportBle()) {
			mBluetoothAdapter.enable();
		}
	}

	// private Runnable runnableScan = new Runnable() {
	//
	// @Override
	// public void run() {
	// scanDevice(false);
	// scanDevice(true);
	// mHandler.postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// boolean allConnected = isAllConnected();
	// if (!allConnected) {
	// mHandler.postDelayed(runnableScan, SCAN_PERIOD / 2);
	// } else {
	// stopScan();
	// }
	// }
	// }, XBlueManager.SCAN_PERIOD);
	// }
	// };

	public void startScan() {
		if (!isSupportBle()) {
			XBlueBroadcastUtils.instance().sendBroadcastDoNotSupportBle(context);
			return;
		}
		if (!isBluetoothAdapterEnable()) {
			XBlueBroadcastUtils.instance().sendBroadcastBluetoothAdapterDisable(context);
			return;
		}

		scanDevice(false);
		scanDevice(true);

		//
		// mHandler.removeCallbacks(runnableScan);
		// // boolean allConnected = isAllConnected();
		// // if (allConnected) {
		// // scanDevice(false);
		// // } else {
		// mHandler.post(runnableScan);
		// // }
	}

	public void stopScan() {
		scanDevice(false);
		// mHandler.removeCallbacks(runnableScan);
		// scanDevice(false);
	}

	@SuppressLint("NewApi")
	private XBlueLeScanCallback leScanCallback = new XBlueLeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			super.onLeScan(device, rssi, scanRecord);
			if (!scanDeviceMap.containsKey(device.getAddress())) {
				scanDeviceMap.put(device.getAddress(), device);
				printDevice();

				
				// if (XBlueUtils.isExist(device.getAddress(), context)) {
				String blue = XBlueUtils.getBlueAddress(context);
				if (!blue.equals("")) {
					if (blue.equals(device.getAddress()) && !isConnected(device.getAddress())) {
						XLog.sys("=========搜索到了，开始连接==============");
						// device.createBond();
						connect(device);
					}
				}
				XBlueBroadcastUtils.instance().sendBroadcastDeviceFound(context, getDevicesFromMap());
			}
			if (mXBlueManagerListener != null) {
				mXBlueManagerListener.doDeviceFound(scanDeviceMap);
			}
			
		}
	};

	// private BluetoothGatt

	@Override
	public void scanDevice(boolean enable) {

		scanDeviceMap.clear();
		if (enable) {
			mBluetoothAdapter.startLeScan(leScanCallback);
			scanState = SCAN_STATE_START;
			// mHandler.postDelayed(new Runnable() {
			//
			// @Override
			// public void run() {
			// mBluetoothAdapter.stopLeScan(leScanCallback);
			// scanState = SCAN_STATE_STOP;
			// }
			// }, SCAN_PERIOD);
		} else {
			mBluetoothAdapter.stopLeScan(leScanCallback);
			scanState = SCAN_STATE_STOP;

		}
	}

	@Override
	public void scanDevice(UUID[] serviceUuids, boolean enable) {

	}

	@Override
	public int getScanningState() {
		return scanState;
	}

	private void printBondedDevice() {
		Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
		if (bondedDevices != null) {

			Iterator<BluetoothDevice> iterator = bondedDevices.iterator();
			while (iterator.hasNext()) {
				BluetoothDevice device = iterator.next();
				System.out.println("绑定设备地址	：" + device);
			}
		}
	}

	@Override
	public void connect(BluetoothDevice device) {
		if (device == null) {
			return;
		}
		Log.e("", "链接前connectResultsMap大小 ：" + connectResultsMap.size());
		int size = getSizeFromMapWhenConnectStateAtServiceFound();
		if (size >= 1) {
			XLog.sys("超过1个了");
			return;
		}

		XBlueConnectResult oldResult = getXBlueConnectResultByAddress(device.getAddress());
		if (oldResult != null) {
			oldResult.close();
			connectResultsMap.remove(oldResult);
		}

		/**
		 * 新建ConnectResult
		 */
		XBluetoothGattCallBack callback = new XBluetoothGattCallBack() {
			@Override
			public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
				byte[] value = characteristic.getValue();
				XLog.sys("onCharacteristicChanged() -->" + XBlueUtils.byteToHexString(value));
				parseData(gatt, value);
				super.onCharacteristicChanged(gatt, characteristic);
			}

			@Override
			public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					byte[] value = characteristic.getValue();
					XLog.sys("onCharacteristicWrite() --> " + XBlueUtils.byteToHexString(value));
				} else {
					XLog.sys("onCharacteristicWrite() --> 失败");
				}

				super.onCharacteristicWrite(gatt, characteristic, status);
			}

			@Override
			public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (gatt == null) {
							return;
						}
						String address = gatt.getDevice().getAddress();
						XBlueConnectResult result = getXBlueConnectResultByAddress(address);
						if (status == BluetoothGatt.GATT_SUCCESS) {
							XBlueBroadcastUtils.instance().sendBroadcastConnectState(context, gatt.getDevice(), newState);

							if (newState == BluetoothGatt.STATE_CONNECTED) {
								XLog.sys("onConnectionStateChange() --> 连接上");
								if (result != null) {
									result.setConnectState(ConnectState.CONNECT);
									connectResultsMap.put(address, result);
								}
								gatt.discoverServices();
							} else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
								if (mXBlueManagerListener!=null) {
									mXBlueManagerListener.doDisconnected(gatt.getDevice());
								}
								scanDeviceMap.clear();
								XLog.sys("onConnectionStateChange() --> " + address + "断开连接，后的size" + connectResultsMap.size());
								isSetting = false;
								if (result != null) {
									result.close();
									connectResultsMap.remove(address);
								}
								logServiceSize();
								if (!isAllConnected()) {
									startScan();
									// startAutoConnecting();//
								}
								
								//
//								mHandler.removeCallbacks(ringRun);
//								mHandler.postDelayed(ringRun, 12 * 1000);

							}
						} else {
							scanDeviceMap.clear();
							XBlueBroadcastUtils.instance().sendBroadcastConnectState(context, gatt.getDevice(), BluetoothGatt.STATE_DISCONNECTED);
							XLog.sys("onConnectionStateChange() --> 失败");
							if (result != null) {
								result.close();
								connectResultsMap.remove(address);
							}
							isSetting = false;
							if (!isAllConnected()) {
								startScan();
							}
//							mBluetoothAdapter.disable();
//							mHandler.postDelayed(new Runnable() {
//								
//								@Override
//								public void run() {
//									mBluetoothAdapter.enable();
//								}
//							}, 5000);
						}
					}
				});

				super.onConnectionStateChange(gatt, status, newState);
			}

			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				super.onServicesDiscovered(gatt, status);
				if (status == BluetoothGatt.GATT_SUCCESS) {
					XLog.sys("onServicesDiscovered() --> 成功");
					parseGatt(gatt);
				} else {
					XLog.sys("onServicesDiscovered() --> 失败");
				}
			}

		};
		BluetoothGatt gatt = device.connectGatt(context, false, callback);
		XBlueConnectResult result = new XBlueConnectResult(callback, gatt, device.getAddress());
		result.setConnectState(ConnectState.CONNECTING);
		this.connectResultsMap.put(device.getAddress(), result);
		Log.e("", "链接后connectResultsMap大小 ：" + connectResultsMap.size());
	}

	@Override
	public void connect(String address) {
	}

	@Override
	public void close(BluetoothGatt gatt) {

	}

	@Override
	public void closeAll() {
		if (connectResultsMap.size() == 0) {
			return;
		}
		int size = scanDeviceMap.size();
		Set<Entry<String, XBlueConnectResult>> entrySet = connectResultsMap.entrySet();
		Iterator<Entry<String, XBlueConnectResult>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, XBlueConnectResult> next = iterator.next();
			XBlueConnectResult result = next.getValue();
			result.close();
		}
		connectResultsMap.clear();
		scanDeviceMap.clear();
	}

	@Override
	public void writeCharacteristic(BluetoothGatt gatt, byte[] order) {
		if (gatt == null) {
			return;
		}
		BluetoothGattService service = gatt.getService(UUID.fromString(UUID_SERVICE));
		BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_WRITE));
		characteristic.setValue(order);
		gatt.writeCharacteristic(characteristic);
	}

	public void writeChacteristic(String address, byte[] order) {
		if (address == null || address.equals("")) {
			return;
		}
		XBlueConnectResult result = getXBlueConnectResultByAddress(address);
		if (result != null) {
			BluetoothGatt gatt = result.getGatt();
			ConnectState connectState = result.getConnectState();
			if (connectState == ConnectState.SERVICE_FOUNDED) {
				this.writeCharacteristic(gatt, order);
			}
		}
	}

	// public void writeChacteristicAll(byte[] order) {
	// new WriteAllAsyncTask().execute(order);
	// }

	public void writeChacteristicAll(byte[][] order) {
		new WriteMoreAllAsyncTask().execute(order);
	}

	private class WriteAllAsyncTask extends AsyncTask<byte[], Void, Void> {

		@Override
		protected Void doInBackground(byte[]... params) {
			String blue = XBlueUtils.getBlueAddress(context);
			XBlueConnectResult xBlueConnectResult = connectResultsMap.get(blue);
			writeChacteristic(xBlueConnectResult.getAddress(), params[0]);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private class WriteMoreAllAsyncTask extends AsyncTask<byte[][], Void, Void> {
		@Override
		protected Void doInBackground(byte[][]... params) {
			byte[][] bs = params[0];
			for (int i = 0; i < bs.length; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String blue = XBlueUtils.getBlueAddress(context);
				XBlueConnectResult xBlueConnectResult = connectResultsMap.get(blue);
				if (xBlueConnectResult != null) {
					writeChacteristic(xBlueConnectResult.getAddress(), bs[i]);
				}
			}
			return null;
		}

	}

	private void printDevice() {
		if (scanDeviceMap != null) {
			int size = scanDeviceMap.size();
			if (size > 0) {
				Set<Entry<String, BluetoothDevice>> entrySet = scanDeviceMap.entrySet();
				Iterator<Entry<String, BluetoothDevice>> iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Entry<String, BluetoothDevice> next = iterator.next();
					XLog.sys(next.getKey());
				}
			}
		}
	}

	public XBlueConnectResult getXBlueConnectResultByAddress(String address) {
		return connectResultsMap.get(address);
	}

	private ArrayList<BluetoothDevice> getDevicesFromMap() {
		ArrayList<BluetoothDevice> devices = null;
		if (scanDeviceMap != null) {
			int size = scanDeviceMap.size();
			XLog.sys("XBlueManager--scanDeviceMap大小：" + size);
			if (size > 0) {
				devices = new ArrayList<BluetoothDevice>();
				Set<Entry<String, BluetoothDevice>> entrySet = scanDeviceMap.entrySet();
				Iterator<Entry<String, BluetoothDevice>> iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Entry<String, BluetoothDevice> next = iterator.next();
					devices.add(next.getValue());
				}
			}
		}
		return devices;
	}

	public interface XBlueManagerListener {
		public void doDeviceFound(HashMap<String, BluetoothDevice> scanDeviceMap);
		public void doPlayMusic(int order);
		public void doFindPhone(int order);
		public void doDisconnected(BluetoothDevice device);
	}

	private XBlueManagerListener mXBlueManagerListener;
	private boolean isSetting;

	public void setXBlueManagerListener(XBlueManagerListener l) {
		this.mXBlueManagerListener = l;
	}

	@Override
	public int getConnectState(BluetoothDevice device) {
		return 0;
	}

	public boolean isConnected(String address) {
		XBlueConnectResult result = connectResultsMap.get(address);
		if (result == null) {
			return false;
		}
		if (result.getConnectState() == ConnectState.SERVICE_FOUNDED) {
			return true;
		} else {
			return false;
		}

	}

	public boolean isAllConnected() {
		String blue = XBlueUtils.getBlueAddress(context);
		if (blue.equals("")) {
			return false;
		}
		boolean connected = isConnected(blue);
		// HashSet<String> addresses = XBlueUtils.getBlues(context);
		// if (addresses == null) {
		// XLog.sys("isAllConnected() --> false");
		// return false;
		// }
		// Iterator<String> iterator = addresses.iterator();
		// while (iterator.hasNext()) {
		// String address = iterator.next();
		// XBlueConnectResult reslut = connectResultsMap.get(address);
		// if (reslut == null) {
		// XLog.sys("isAllConnected() --> false");
		// return false;
		// }
		// if (reslut.getConnectState() != ConnectState.SERVICE_FOUNDED) {
		// XLog.sys("isAllConnected() --> false");
		// return false;
		// }
		// }
		XLog.sys("isAllConnected() --> " + connected);
		return connected;
	}

	private void parseGatt(BluetoothGatt gatt) {
		stopScan();
		String address = gatt.getDevice().getAddress();
		XBlueConnectResult result = getXBlueConnectResultByAddress(address);
		BluetoothGattService service = gatt.getService(UUID.fromString(UUID_SERVICE));
		if (service != null) {
			BluetoothGattCharacteristic characteristicNotify = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_NOTIFY));
			BluetoothGattCharacteristic characteristicWrite = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_WRITE));
			if (characteristicNotify != null && characteristicWrite != null) {
				characteristicWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);// ������Ӧ��
				gatt.setCharacteristicNotification(characteristicNotify, true);// ����characteristic���Ըı�ʱ����֪ͨ
				BluetoothGattDescriptor descriptor = characteristicNotify.getDescriptor(UUID.fromString(UUID_DESC_CCC));
				if (descriptor != null) {
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);// ����descriptor���Ըı�ʱ����֪ͨ
					gatt.writeDescriptor(descriptor);
					XLog.sys("------------找到服务");
					result.setConnectState(ConnectState.SERVICE_FOUNDED);
					connectResultsMap.put(address, result);
					XBlueBroadcastUtils.instance().sendBroadcastServiceDiscovered(context, gatt.getDevice());

					// 扩展
//					music.stop();
					isSetting = true;
					XBlueBroadcastUtils.instance().sendbroadcastreceiverForSyncSetting(context, isSetting);
					doUpdateSetting();

					logServiceSize();
					if (isAllConnected()) {
					} else {
						// startAutoConnecting();
						startScan();
						// int serviceFoundSize =
						// getSizeFromMapWhenConnectStateAtServiceFound();
						// if (serviceFoundSize<=4) {
						// if(serviceFoundSize <=
						// XBlueUtils.getBluesSize(context)){
						// scanDevice(false);
						// }else{
						// scanDevice(true);
						// }
						// }
					}

				} else {
					isSetting = false;
					XLog.sys("------------没有找到服务----");
					if (result != null) {

						result.close();
						connectResultsMap.remove(address);
					}
					if (!isAllConnected()) {
						startScan();
						// startAutoConnecting();
					}

				}
			} else {
				isSetting = false;
				if (result != null) {
					result.close();
					connectResultsMap.remove(address);
				}
				if (!isAllConnected()) {
					startScan();
					// startAutoConnecting();
				}
			}
		} else {
			isSetting = false;
			XLog.sys("------------没有找到服务----");
			if (result != null) {
				result.close();
				connectResultsMap.remove(address);
			}
			if (!isAllConnected()) {
				startScan();
				// startAutoConnecting();
			}
		}
	}

	private void logServiceSize() {
		int serviceFoundSize = getSizeFromMapWhenConnectStateAtServiceFound();
		XLog.sys("找到服务的设备数量：" + serviceFoundSize);
	}

	public int getSizeFromMapWhenConnectStateAtServiceFound() {
		int size = 0;
		if (connectResultsMap.size() == 0) {
			return size;
		}

		Set<Entry<String, XBlueConnectResult>> entrySet = connectResultsMap.entrySet();
		Iterator<Entry<String, XBlueConnectResult>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, XBlueConnectResult> next = iterator.next();
			XBlueConnectResult result = next.getValue();
			if (result.getConnectState() == ConnectState.SERVICE_FOUNDED) {
				size++;
			}
		}
		XLog.sys("找到服务的设备数量：" + size);
		return size;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * <p>
	 * 解析数据
	 * </p>
	 * 
	 * @param data
	 * @param gatt
	 */
	private void parseData(BluetoothGatt gatt, byte[] data) {
		int result = 0;
		ProtolNotify notify = ProtolNotify.instance();
		XBlueBroadcastUtils broad = XBlueBroadcastUtils.instance();
//		try {
			result = notify.getResult(data);
			Log.i("Service", "type : " + result);
				switch (result) {
				case 1:
					Log.e("Service", "获取运动数据：=========================" );
					HistorySport historySport = notify.notifyHistorySport(data);
					Log.i("Service", "获取运动数据：" + historySport);
					if (historySport!=null) {
						//save
						saveHistorySport(historySport);
						broad.sendbroadcastForHistorySport(context, historySport);
					}
					
					break;
				case 2:
					int notifySyncSportState = notify
							.notifySyncSportState(data);
					if (notifySyncSportState == 0) {
						Log.i("Service", "开始获取运动数据");
					} else {
						Log.i("Service", "获取运动数据结束");
					}
					broad.sendbroadcastForSyncSport(context,notifySyncSportState);
					break;
				case 3:
					HistorySleep notifyHistorySleep = notify
							.notifyHistorySleep(data);
					Log.i("Service", "获取睡眠数据：" + notifyHistorySleep.toString());
					saveHistorySpleep(notifyHistorySleep);
					broad.sendbroadcastForHistorySleep(context,notifyHistorySleep);
					break;
				case 4:
					int notifySyncSleepState = notify
							.notifySyncSleepState(data);
					if (notifySyncSleepState == 0) {
						Log.i("Service", "开始获取睡眠数据");
					} else {
						Log.i("Service", "获取睡眠数据结束");
					}
					broad.sendbroadcastForSyncSleep(context,notifySyncSleepState);
					break;
				case 5:
					int notifyHeartRate = notify.notifyHeartRate(data);
					Log.i("Service", "获取心率数据：" + notifyHeartRate);
					if (notifyHeartRate!=-1) {
						broad.sendbroadcastForHeadtRate(context,notifyHeartRate);
					}
					break;
				case 6:
					int notifyFindPhone = notify.notifyFindPhone(data);
//					int notifyFindPhone = 1;
					Log.i("Service", "寻找手机！" +notifyFindPhone);
					if (notifyFindPhone!=-1) {
						broad.sendbroadcastForFindPhone(context,notifyFindPhone);
						if (mXBlueManagerListener!=null) {
							mXBlueManagerListener.doFindPhone(notifyFindPhone);
						}
					}
					break;
				case 7:
					Log.i("Service", "照相！" );
					int notifyTakePicture = notify.notifyTakePicture(data);
					if (notifyTakePicture!=-1) {
						broad.sendbroadcastForTakePicture(context);
					}
					break;
				case 8:
					int orderMusic = notify.notifyMusic(data);
					Log.i("Service", "音乐！"  + orderMusic);
					if (orderMusic!=-1) {
						if (mXBlueManagerListener!=null) {
							mXBlueManagerListener.doPlayMusic(orderMusic);
						}
					}
					break;
				
				default:
					break;
				}
//		} catch (Exception e) {
//
//		}
	}
	

	private static int previousMuteMode = -1;

	/**
	 * 来电静音
	 *
	 * @param context
	 */
	private void toggleRingerMute(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (previousMuteMode == -1) {
			previousMuteMode = am.getRingerMode();
			am.setRingerMode(0);
		}
		am.setRingerMode(previousMuteMode);
		previousMuteMode = -1;
	}

//	private Runnable ringRun = new Runnable() {
//
//		@Override
//		public void run() {
//			boolean isRemindOpen = (Boolean) SharedPreferenceUtil.get(context, I2WatchProtocolDataForWrite.SHARE_NON, false);
//			if (isRemindOpen) {
//				if (!isAllConnected()) {
//					// 断线提醒
//					// MusicManager.instance(context).start(R.raw.crystal);
//					music.start();
//
//					// mHandler.postDelayed(new Runnable() {
//					//
//					// @Override
//					// public void run() {
//					// music.stop();
//					// }
//					// }, 3000);
//				}
//			}
//
//		}
//	};

	/**
	 * 同步手机设置
	 */
	private void doUpdateSetting() {
		ProtolWrite pw = ProtolWrite.instance();
		// 0.同步时间
		byte[] hexDataForTimeSync = pw.hexDataForTimeSync(new Date(), context);
		// 1.睡眠设置
		byte[] sleepSetting = getSleepSetting(pw);
		// 2.来电提醒
		boolean iscoming = (boolean) SharedPreferenceUtil.get(context, StaticValue.SHARE_REMIND_INCOMING, false);
		byte[] inComing = pw.writeIncoming(iscoming?1:0);
		// 3.每日闹铃
		byte[] alarmSetting = getAlarmSetting(pw);
		// 4.久坐提醒
		boolean  isLongSit = (boolean) SharedPreferenceUtil.get(context, StaticValue.SHARE_REMIND_LONG_SIT, false);
		byte[] longSitSetting = pw.writeLongSit(isLongSit?1:0);
		// 5.生日提醒
		byte [] birthSetting = getBirthdaySetting(pw);
		// 6.整点报时
		boolean isPointTime = (Boolean) SharedPreferenceUtil.get(context, StaticValue.SHARE_REMIND_POINT_TIME, false);
		byte[] longPointTime = pw.writeLongPointTime(isPointTime?1:0);
		
		writeChacteristicAll(new byte[][]{
				hexDataForTimeSync,
				sleepSetting,
				inComing,
				alarmSetting,
				longSitSetting,
				birthSetting,
				longPointTime
			});
		
		
//		isSetting = true;
//		XBlueBroadcastUtils.instance().sendbroadcastreceiverForSyncSetting(context, isSetting);
//		// new SettingAsyncTask().execute();
//		// writeChacteristicAll(DataUtil.getBytesByString("FB"));//
//		// 告诉手环是android手机
//		// 1.运动提醒
//		byte[] byteActivityRemindSync = I2WatchProtocolDataForWrite.protocolDataForActivityRemindSync(context).toByte();
//		// 2.来电提醒
//		byte[] protocolForCallingAlarmPeriodSync = I2WatchProtocolDataForWrite.protocolForCallingAlarmPeriodSync(context).toByte();
//		// 3.防丢提醒
//		byte[] hexDataForLostOnoffI2Watch = I2WatchProtocolDataForWrite.hexDataForLostOnoffI2Watch(context);
//		// 4.闹钟
//		byte[] protocolDataForClockSync = I2WatchProtocolDataForWrite.protocolDataForClockSync(context).toByte();
//		// 5.睡眠时间
//		byte[] hexDataForSleepPeriodSync = I2WatchProtocolDataForWrite.hexDataForSleepPeriodSync(context);
//		// 6.亮度
//		byte[] hexDataDecrease = I2WatchProtocolDataForWrite.hexDataForUpdateBrightness(context);
//		// 7.个性签名
//		byte[] hexDataForSignatureSync = I2WatchProtocolDataForWrite.hexDataForSignatureSync(context);
//		// 8.时间同步
//		byte[] hexDataForTimeSync = I2WatchProtocolDataForWrite.hexDataForTimeSync(new Date(), context);
//
//		writeChacteristicAll(new byte[][] { DataUtil.getBytesByString("FB"), byteActivityRemindSync, protocolForCallingAlarmPeriodSync, hexDataForLostOnoffI2Watch, protocolDataForClockSync,
//				hexDataForSleepPeriodSync, hexDataDecrease, hexDataForSignatureSync, hexDataForTimeSync });
//
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				isSetting = false;
//				XBlueBroadcastUtils.instance().sendbroadcastreceiverForSyncSetting(context, isSetting);
//			}
//		}, 30 * 1000);
	}
	
	private byte[] getSleepSetting(ProtolWrite pw){
		boolean isSleepSettingOnoff = (boolean) SharedPreferenceUtil.get(context, StaticValue.SHARE_SLEEP_ON_OFF, false);
		String sleepTime = (String) SharedPreferenceUtil.get(context, StaticValue.SHARE_SLEEP_TIME, "22:00");
		String awakTime = (String) SharedPreferenceUtil.get(context, StaticValue.SHARE_AWAK_TIME, "06:00");
		String[] sleeps = sleepTime.split(":");
		int sleepHour = Integer.valueOf(sleeps[0]);
		int sleepMin = Integer.valueOf(sleeps[1]);
		String[] awaks = awakTime.split(":");
		int awakHour = Integer.valueOf(awaks[0]);
		int awakMin = Integer.valueOf(awaks[1]);
		byte[] writeSleepSetting = null ;
		try {
			writeSleepSetting = pw.writeSleepSetting(isSleepSettingOnoff?1:0, sleepHour, sleepMin, awakHour, awakMin);
			return writeSleepSetting;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeSleepSetting;
	
	}
	
	private byte[] getAlarmSetting(ProtolWrite pw){
		boolean isAlarm = (Boolean) SharedPreferenceUtil.get(context, StaticValue.SHARE_REMIND_ALARM, false);
		String alarmTime = (String) SharedPreferenceUtil.get(context, StaticValue.SHARE_REMIND_ALARM_TIME, "06:00");
		String[] alarms = alarmTime.split(":");
		int alarmHour = Integer.valueOf(alarms[0]);
		int alarmMin = Integer.valueOf(alarms[1]);
		return pw.writeAlarm(isAlarm?1:0, alarmHour, alarmMin);
	}
	
	private byte[] getBirthdaySetting(ProtolWrite pw) {
		boolean isBirthday = (Boolean) SharedPreferenceUtil.get(context, StaticValue.SHARE_REMIND_BIRTHDAY, false);
		String birthdayTime = (String) SharedPreferenceUtil.get(context, StaticValue.SHARE_REMIND_BIRTHDAY_TIME, "08:00");
		String birthdayDate = (String) SharedPreferenceUtil.get(context, StaticValue.SHARE_BIRTHDAY, "2000-01-01");
		String[] times = birthdayTime.split(":");
		String[] dates = birthdayDate.split("-");
		int month = Integer.valueOf(dates[1]);
		int day = Integer.valueOf(dates[2]);
		int hour = Integer.valueOf(times[0]);
		int min = Integer.valueOf(times[1]);
		byte[] writeBirthday = pw.writeBirthday(isBirthday?1:0, month, day, hour, min);
		return writeBirthday;
	}

	
	private void saveHistorySpleep(HistorySleep notifyHistorySleep) {
		new SaveSleepHistoryAsyncTask().execute(notifyHistorySleep);
	}

	private void saveHistorySport(HistorySport historySport) {
		new SaveSportHistoryAsyncTask().execute(historySport);
	}
	
	private class SaveSportHistoryAsyncTask extends AsyncTask<HistorySport, Void, Void>{
		
		@Override
		protected Void doInBackground(HistorySport... params) {
			HistorySport sport = params[0];
			Date date = new Date();
			String dateStr = DateUtil.dateToString(date, "yyyyMMdd");
			if (sport!=null) {
				List<HistorySport> sportAtDate = DataSupport.where("date =?",dateStr).find(HistorySport.class);
				if (sportAtDate!=null && sportAtDate.size()>0) {	//说明存在记录 ， 更新值就行了
					Log.e("xpl", dateStr + "已存记录");
					ContentValues values = new ContentValues();
					values.put("sportTime", sport.getSportTime());
					values.put("step", sport.getStep());
					DataSupport.updateAll(HistorySport.class, values,"date =?",dateStr);
				}else{
					sport.save(); //不存在直接保存记录
				}
				
			}
			return null;
		}
	} 
	
	private class SaveSleepHistoryAsyncTask extends AsyncTask<HistorySleep, Void, Void>{
		
		@Override
		protected Void doInBackground(HistorySleep... params) {
			HistorySleep sleep = params[0];
			Date date = new Date();
			String dateStr = DateUtil.dateToString(date, "yyyyMMdd");
			if (sleep!=null) {
				List<HistorySleep> sleepAtDate = DataSupport.where("date =?",dateStr).find(HistorySleep.class);
				if (sleepAtDate!=null && sleepAtDate.size()>0) {	//说明存在记录 ， 更新值就行了
					Log.e("xpl", dateStr + "已存记录");
					ContentValues values = new ContentValues();
					values.put("deep", sleep.getDeep());
					values.put("light", sleep.getLight());
					DataSupport.updateAll(HistorySleep.class, values,"date =?",dateStr);
				}else{
					sleep.save(); //不存在直接保存记录
				}
				
			}
			return null;
		}
	}
}
