package com.mycj.junsda.activity;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.laput.service.XBlueBroadcastReceiver;
import com.laput.service.XBlueBroadcastUtils;
import com.laput.service.XBlueService;
import com.laput.service.XBlueUtils;
import com.mycj.junsda.R;
import com.mycj.junsda.adapter.DeviceAdapter;
import com.mycj.junsda.adapter.DeviceAdapterNew;
import com.mycj.junsda.base.BaseActivity;
import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;
import com.mycj.junsda.broadcast.ImpXplBroadcastReceiver;
import com.mycj.junsda.service.AddressSaved;
import com.mycj.junsda.service.XplBluetoothService;
import com.mycj.junsda.view.AlertDialog;
import com.mycj.junsda.view.FangTextView;
import com.mycj.junsda.view.LaputaAlertDialog;
import com.mycj.junsda.view.RadarView;
import com.mycj.junsda.view.XplAlertDialog;

public class DeviceAcitivy extends BaseActivity {
	private List<BluetoothDevice> deviceList;
	private ListView lvDeivce;
	private DeviceAdapterNew adapter;
	// private XplBluetoothService xplBluetoothService;
	private XplAlertDialog showProgressDialog;
	private RadarView radarView;
	private ImageView imgBack;
	private ImageView imgConnect;
	private FangTextView tvBindedName;
	private FangTextView tvBindedAddress;
	private LinearLayout llBindedInfo;
	private boolean isStopScan = true;
	// private RelativeLayout rlBindedInfo;
	private Handler mHandler = new Handler() {
	};

	private XBlueBroadcastReceiver xReceiver = new XBlueBroadcastReceiver() {

		@Override
		public void doSportSyncStateChanged(int state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void doSportChanged(HistorySport sport) {
			// TODO Auto-generated method stub

		}

		@Override
		public void doSleepSyncStateChanged(int state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void doSleepChanged(HistorySleep sleep) {
			// TODO Auto-generated method stub

		}

		@Override
		public void doServiceDiscovered(final BluetoothDevice device) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (showProgressDialog != null) {
						showProgressDialog.cancel();
					}
					if (device != null) {
						// initBindedDeviceInfo(device.getAddress(),
						// device.getName());
					}
					Toast.makeText(getApplicationContext(), getString(R.string.connected), 0).show();
					// deviceList.remove(device);

					adapter.setConnectPos(deviceList.indexOf(device));
					adapter.setIsConecting(false);
				}
			});
		}

		@Override
		public void doHeartRateChanged(int hr) {
			// TODO Auto-generated method stub

		}

		@Override
		public void doDeviceFound(final ArrayList<BluetoothDevice> devices) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (!isStopScan) {
						Log.e("", "=================================devices" + devices);
						deviceList.clear();
						if (devices != null) {
							deviceList.addAll(devices);
						}
						adapter.notifyDataSetChanged();
					}
				}
			});
		}

		@Override
		public void doConnectStateChange(BluetoothDevice device, final int state) {
			mHandler.post(new Runnable() {
				public void run() {
					if (state == BluetoothGatt.STATE_DISCONNECTED) {
						tvBindedAddress.setTextColor(Color.RED);
					} else {

					}
					// updateBlueState();
				}
			});
		}

		@Override
		public void doBluetoothEnable() {
			// TODO Auto-generated method stub

		}
	};
	private ImpXplBroadcastReceiver mReceiver = new ImpXplBroadcastReceiver() {
		@Override
		public void doDeviceFound(final BluetoothDevice device, int rssi) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (!deviceList.contains(device)) {
						deviceList.add(device);
						adapter.notifyDataSetChanged();
					}
				}
			});

			super.doDeviceFound(device, rssi);
		}

		public void doServiceDisCovered(final BluetoothDevice device) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (showProgressDialog != null) {
						showProgressDialog.cancel();
					}
					if (device != null) {
						// initBindedDeviceInfo(device.getAddress(),
						// device.getName());
					}
					// deviceList.remove(device);
					adapter.notifyDataSetChanged();
				}
			});
		};

	};
	private XBlueService xBlueService;
	private AlertDialog isDisBindedDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settting_device);
		xBlueService = getXBlueService();
		registerReceiver(xReceiver, XBlueBroadcastUtils.instance().getIntentFilter());
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		radarView.stop();
		unregisterReceiver(xReceiver);
		if (showProgressDialog != null) {
			showProgressDialog.dismiss();
		}
	}
	
	private Runnable changeConnectingStateRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (showProgressDialog != null) {
				showProgressDialog.cancel(getString(R.string.connect_fail));
			}
			if (xBlueService != null && xBlueService.isAllConnected()) {
				adapter.setIsConecting(false);
			} else {
				adapter.setConnectPos(-1);
				adapter.setIsConecting(false);
			}
		}
	};
	
	private void initViews() {
	
		imgBack = (ImageView) findViewById(R.id.img_back);
		imgConnect = (ImageView) findViewById(R.id.img_connect);
		tvBindedName = (FangTextView) findViewById(R.id.tv_binded_device_name);
		tvBindedAddress = (FangTextView) findViewById(R.id.tv_binded_device_address);
		llBindedInfo = (LinearLayout) findViewById(R.id.ll_binded_info);
		// rlBindedInfo = (RelativeLayout) findViewById(R.id.rl_binded_info);
		// String bindedAddress = AddressSaved.getBindedAddress(getApplicationContext());
		// String bindedName = AddressSaved.getBindedName(getApplicationContext());
		String bindedAddress = XBlueUtils.getBlueAddress(this);
		String bindedName = XBlueUtils.getBlueName(this);
		// initBindedDeviceInfo(bindedAddress, bindedName);
		lvDeivce = (ListView) findViewById(R.id.lv_device);
		deviceList = new ArrayList<BluetoothDevice>();
		adapter = new DeviceAdapterNew(this, deviceList);
	
		lvDeivce.setAdapter(adapter);
		lvDeivce.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
				// showProgressDialog = new
				// XplAlertDialog(DeviceAcitivy.this).builder2(getString(R.string.connect_success));
				// showProgressDialog.show();
				// showProgressDialog = showProgressDialog("正在连接设备...");
				// BluetoothDevice device = devices.get(i);
				// if (xBlueService!=null ) {
				// xBlueService.connect(device);
				// }
	
				// adapter.clear();
				mHandler.removeCallbacks(changeConnectingStateRunnable);
				
				mHandler.post(new Runnable() {
					@Override
					public void run() {
	
						if (xBlueService != null) {
							xBlueService.stopScan();
							radarView.stop();
							isStopScan = true;
							xBlueService.closeAll();
	
							adapter.setConnectPos(i);
							adapter.setIsConecting(true);
	
							BluetoothDevice device = deviceList.get(i);
							XBlueUtils.clear(DeviceAcitivy.this);
							XBlueUtils.saveBlue(DeviceAcitivy.this, device.getAddress());
							// setCurrentDevice();
							// initBindedDeviceInfo(device.getAddress(),
							// device.getName());
							// device.createBond();
							xBlueService.connect(device);
						}
					}
				});
				mHandler.postDelayed(changeConnectingStateRunnable, 10*1000);
			}
		});
	
		radarView = (RadarView) findViewById(R.id.scan_radar);
		radarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
	
				if (xBlueService != null && !xBlueService.isEnable()) {
					LaputaAlertDialog dialog = new LaputaAlertDialog(DeviceAcitivy.this).builder("请打开蓝牙功能");
					dialog.show();
					return;
				}
				
				adapter.setConnectPos(-1);
				adapter.setIsConecting(false);
	
				boolean rotating = radarView.isRotating();
				if (rotating) {
					radarView.stop();
					isStopScan = true;
	
				} else {
					isStopScan = false;
					deviceList.clear();
					radarView.start();
					if (xBlueService != null) {
						xBlueService.startScan();
					}
				}
			}
		});
	
		imgBack.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		imgConnect.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "重练",
				// Toast.LENGTH_SHORT).show();
			}
		});
		
/*		rlBindedInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialogForDisBindedDevice();
			}

		});*/
	}
	
	
	/**
	 * 初始化绑定设备信息
	 * @param bindedAddress
	 * @param bindedName
	 */
	private void initBindedDeviceInfo(String bindedAddress, String bindedName) {
		if (bindedAddress.equals("")) {
			llBindedInfo.setVisibility(View.GONE);
			tvBindedName.setText("");
			tvBindedAddress.setText("");
		} else {
			llBindedInfo.setVisibility(View.VISIBLE);
			if (bindedName == null || bindedName.equals("")) {
				tvBindedName.setText(R.string.nuknow);
			} else {
				tvBindedName.setText(bindedName);
			}
			tvBindedAddress.setText(bindedAddress);
			// if (!xplBluetoothService.isBluetoothConnected()) {
			if (xBlueService != null && xBlueService.isAllConnected()) {
				tvBindedAddress.setTextColor(Color.WHITE);
				tvBindedName.setTextColor(Color.WHITE);
			} else {
				tvBindedAddress.setTextColor(Color.RED);
				tvBindedName.setTextColor(Color.RED);
			}
		}
	}
	
	/**
	 * 提示框：是否移除绑定设备
	 */
	private void showDialogForDisBindedDevice() {
		if (isDisBindedDialog == null) {
			isDisBindedDialog = new AlertDialog(this).builder().setMsg("删除绑定的手环？").setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(View v) {
	
				}
			}).setPositiveButton("确定", new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					XBlueUtils.clear(DeviceAcitivy.this);
					if (xBlueService != null) {
						xBlueService.closeAll();
					}
					// initBindedDeviceInfo("", "");
				}
			});
		}
		isDisBindedDialog.show();
	
	}

}
