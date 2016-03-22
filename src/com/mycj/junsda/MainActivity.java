package com.mycj.junsda;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.laput.service.XBlueBondedUtils;
import com.laput.service.XBlueBroadcastReceiver;
import com.laput.service.XBlueBroadcastUtils;
import com.laput.service.XBlueService;
import com.mycj.junsda.activity.CameraActivity;
import com.mycj.junsda.activity.DeviceAcitivy;
import com.mycj.junsda.activity.HistorySleepActivity;
import com.mycj.junsda.activity.HistorySportActivity;
import com.mycj.junsda.activity.SettingPersonalActivity;
import com.mycj.junsda.activity.SettingRemindActivity;
import com.mycj.junsda.base.BaseActivity;
import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;
import com.mycj.junsda.bean.ProtolWrite;
import com.mycj.junsda.bean.StaticValue;
import com.mycj.junsda.broadcast.ImpXplBroadcastReceiver;
import com.mycj.junsda.fragment.HomeFragment;
import com.mycj.junsda.fragment.MeFragment;
import com.mycj.junsda.fragment.SettingFragment;
import com.mycj.junsda.service.XplBluetoothService;
import com.mycj.junsda.util.DataUtil;
import com.mycj.junsda.util.ShareUtil;
import com.mycj.junsda.view.ActionSheetDialog;
import com.mycj.junsda.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.junsda.view.ActionSheetDialog.SheetItemColor;
import com.mycj.junsda.view.DateUtil;
import com.mycj.junsda.view.FangRadioButton;
import com.mycj.junsda.view.LaputaAlertDialog;
import com.mycj.junsda.view.LaputaLoadingAlertDialog;
import com.mycj.junsda.view.XplAlertDialog;

/**
 * Created by zeej on 2015/11/19.
 */
public class MainActivity extends BaseActivity {

	private RadioGroup rgTab;
	private FangRadioButton rbHome;
	private FangRadioButton rbMe;
	private FangRadioButton rbSetting;
	private List<Fragment> fragments;
	private HomeFragment homeFragment;
	private MeFragment meFragment;
	private SettingFragment settingFragment;
	// private XplBluetoothService xplBluetoothService;
	private XBlueService xBlueService;
	private FragmentManager fragmentManager;
	private final int MAX_STEP = 2000;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StaticValue.MSG_SHARE:
				String path = (String) msg.obj;
				ShareUtil.shareImage(path, MainActivity.this, getString(R.string.share));
				break;

			default:
				break;
			}

		};
	};
	private XBlueBroadcastReceiver xReceiver = new XBlueBroadcastReceiver() {

		@Override
		public void doSportSyncStateChanged(int state) {

		}

		@Override
		public void doSportChanged(final HistorySport sport) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {

					Log.e("", "______________sport : " + sport);
					if (sport != null) {
						int sportTime = sport.getSportTime();
						int step = sport.getStep();

						if (homeFragment != null) {
							homeFragment.freshCircleSport(MAX_STEP, step);
							homeFragment.freshSportInfo(step, sportTime);
						}
					}
				}
			});
		}

		@Override
		public void doSleepSyncStateChanged(int state) {

		}

		@Override
		public void doSleepChanged(HistorySleep sleep) {

		}

		@Override
		public void doServiceDiscovered(BluetoothDevice device) {
			runOnUiThread(new Runnable() {
				public void run() {
					toast(getString(R.string.connected));
					// rgTab.setBackgroundColor(Color.YELLOW); // for test code
					if (settingFragment != null) {
						settingFragment.updateSyncText(true);
					}
				}
			});
		}

		@Override
		public void doHeartRateChanged(final int hr) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (homeFragment != null) {
						homeFragment.freshHeartRateInfo(hr);
					}
				}
			});
		}

		@Override
		public void doDeviceFound(ArrayList<BluetoothDevice> devices) {

		}

		@Override
		public void doConnectStateChange(BluetoothDevice device, final int state) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					switch (state) {
					case BluetoothGatt.STATE_DISCONNECTED:
						rgTab.setBackgroundResource(R.color.bg_main_tab); // for
						if (settingFragment != null) {
							settingFragment.updateSyncText(false);
						}
						break;
					default:
						break;
					}
				}
			});
		}

		@Override
		public void doBluetoothEnable() {

		}
	};

	// private ImpXplBroadcastReceiver mReceiver = new ImpXplBroadcastReceiver()
	// {
	//
	// public void doServiceDisCovered(BluetoothDevice device) {
	// runOnUiThread(new Runnable() {
	// public void run() {
	// toast("已连接");
	// rgTab.setBackgroundColor(Color.YELLOW); // for test code
	// }
	// });
	// };
	//
	// public void doConnectStateChange(BluetoothDevice device, int state) {
	// switch (state) {
	// case BluetoothGatt.STATE_DISCONNECTED:
	// rgTab.setBackgroundResource(R.color.bg_main_tab); // for test
	// break;
	// default:
	// break;
	// }
	// };
	//
	// public void doSportChanged(final com.mycj.junsda.bean.HistorySport sport)
	// {
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// if (sport != null) {
	// int sportTime = sport.getSportTime();
	// int step = sport.getStep();
	//
	// if (homeFragment != null) {
	// homeFragment.freshCircleSport(MAX_STEP, step);
	// homeFragment.freshSportInfo(step, sportTime);
	// }
	// }
	// }
	// });
	// };
	//
	// public void doHeartRateChanged(final int hr) {
	// mHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// if (homeFragment != null) {
	// homeFragment.freshHeartRateInfo(hr);
	// }
	// }
	// });
	// };
	//
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// xplBluetoothService = getXplBluetoothService();
		xBlueService = getXBlueService();
		fragmentManager = getSupportFragmentManager();
		initFragments();
		initViews();
		setListener();
		rgTab.check(R.id.rb_tab_home);
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(xReceiver, XBlueBroadcastUtils.instance().getIntentFilter());
		// if (xplBluetoothService!=null &&
		// xplBluetoothService.isBluetoothConnected()) {
		// xplBluetoothService.writeCharacteristic(ProtolWrite.instance().writeForStep(0));//同步今天的计步
		// }

		if (xBlueService != null && xBlueService.isAllConnected()) {
			xBlueService.write(ProtolWrite.instance().writeForStep(0));
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		startScan();
	}

	private void startScan() {
		// if (xplBluetoothService == null) {
		// return;
		// }
		// if (xplBluetoothService.getCurrentAddress() == null ||
		// xplBluetoothService.getCurrentAddress().equals("")) {
		// return;
		// }
		// if (!xplBluetoothService.isBluetoothConnected()) {
		// xplBluetoothService.scanDevice(true);
		// }
		if (xBlueService != null) {
			if (xBlueService.isAllConnected()) {

			} else {
				xBlueService.startScan();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(xReceiver);
	}

	private void initFragments() {
		if (homeFragment == null) {
			homeFragment = new HomeFragment();
		}

		homeFragment.setOnHomeFragmentClickListener(new HomeFragment.OnHomeFragmentClickListener() {
			@Override
			public void doShare() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						share(mHandler);
					}
				});

				// List<HistorySport> findAll =
				// DataSupport.findAll(HistorySport.class);
				//
				// if (findAll != null) {
				// Log.e("", "------------------------");
				// for (HistorySport s : findAll) {
				//
				// Log.e("", s.toString());
				// }
				// Log.e("", "------------------------");
				// }

			}

			/**
			 *   
			 */
			@Override
			public void doRefresh() {
				// if (xplBluetoothService!=null &&
				// xplBluetoothService.isBluetoothConnected()) {
				// xplBluetoothService.writeCharacteristic(ProtolWrite.instance().writeForStep(0));//同步今天的计步
				// }

				if (xBlueService != null && xBlueService.isAllConnected()) {
					xBlueService.write(ProtolWrite.instance().writeForStep(0));
					// xBlueService.write(ProtolWrite.instance().w);
				}
				
//				setTestHistoryData();
				
				

				Toast.makeText(getApplicationContext(), getString(R.string.refresh), Toast.LENGTH_SHORT).show();
			}
		});
		if (meFragment == null) {
			meFragment = new MeFragment();
		}
		meFragment.setOnMeFragmentClickListener(new MeFragment.OnMeFragmentClickListener() {
			@Override
			public void doLookSleepHistory() {
				Intent intent = new Intent(MainActivity.this, HistorySleepActivity.class);
				startActivity(intent);
			}

			@Override
			public void doLookSportHistory() {
				Intent intent = new Intent(MainActivity.this, HistorySportActivity.class);
				startActivity(intent);
			}
		});

		if (settingFragment == null) {
			settingFragment = new SettingFragment();
		}
		settingFragment.setOnSettingFragmentListener(new SettingFragment.OnSettingFragmentListener() {
			private LaputaLoadingAlertDialog loadDialog;

			@Override
			public void onClick(View v) {
				Intent intent = null;
				switch (v.getId()) {
				case R.id.rl_setting_device:
					intent = new Intent(getApplicationContext(), DeviceAcitivy.class);
					break;
				case R.id.rl_setting_information:
					intent = new Intent(getApplicationContext(), SettingPersonalActivity.class);
					break;
				case R.id.rl_setting_sync:
					
					Toast.makeText(getApplicationContext(), getString(R.string.sync_data), Toast.LENGTH_SHORT).show();
					
					/*
					 * 判断是否连接，决定是否加载同步
					 */
					if (xBlueService != null && xBlueService.isAllConnected()) {
//					if (true) {
						xBlueService.write(ProtolWrite.instance().writeForSyncStep());
						loadDialog = new LaputaLoadingAlertDialog(MainActivity.this)
								.builder("")
								.max(200)
								.setListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										loadDialog.dismiss();
									}
								}).duration(5000);
						loadDialog.show();
//						loadDialog.start();
						xBlueService.write(new byte[][]{ProtolWrite.instance().writeForSyncSleep(),ProtolWrite.instance().writeForSyncStep()});
					}else{
						LaputaAlertDialog dialog = new LaputaAlertDialog(MainActivity.this).builder("请先连接Mefit手环");
						dialog.show();
					}
					
					break;
				case R.id.rl_setting_remind:
					// Toast.makeText(getApplicationContext(), "个人提醒",
					// Toast.LENGTH_SHORT).show();
					intent = new Intent(getApplicationContext(), SettingRemindActivity.class);
					break;
				case R.id.rl_setting_camera:

					// Toast.makeText(getApplicationContext(), "远程拍照",
					// Toast.LENGTH_SHORT).show();
					intent = new Intent(getApplicationContext(), CameraActivity.class);
					break;
				case R.id.rl_setting_about:
					Toast.makeText(getApplicationContext(), getString(R.string.about), Toast.LENGTH_SHORT).show();
					intent = new Intent(getApplicationContext(),AboutActivity.class);
					break;
				}
				if (intent != null) {
					startActivity(intent);
				}
			}

		});

		// fragments = new ArrayList<Fragment>();
		// fragments.add(homeFragment);
		// fragments.add(meFragment);
		// fragments.add(settingFragment);
	}

	protected void setTestHistoryData() {
		/**
		 * 模拟数据
		 */
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		for (int i = 0; i < 30; i++) {
			c.set(Calendar.DAY_OF_MONTH, i + 1);
			String dateStr = DateUtil.dateToString(c.getTime(), "yyyyMMdd");
			HistorySport sport = new HistorySport();
			int time = (int) (Math.random() * 60 * 24);
			int steps = (int) (Math.random() * 1000);
			sport.setDate(dateStr);
			sport.setSportTime(time);
			sport.setStep(steps);
			sport.save();
		}

		for (int i = 0; i < 30; i++) {
			c.set(Calendar.DAY_OF_MONTH, i + 1);
			String dateStr = DateUtil.dateToString(c.getTime(), "yyyyMMdd");
			HistorySleep sleep = new HistorySleep();
			int deep = (int) (Math.random() * 60 * 12);
			int light = (int) (Math.random() * 60 * 12);
			sleep.setDate(dateStr);
			sleep.setDeep(deep);
			sleep.setLight(light);
			;
			sleep.save();
		}
	}

	private void initViews() {
		rgTab = (RadioGroup) findViewById(R.id.rg_tab);
		rbHome = (FangRadioButton) findViewById(R.id.rb_tab_home);
		rbMe = (FangRadioButton) findViewById(R.id.rb_tab_me);
		rbSetting = (FangRadioButton) findViewById(R.id.rb_tab_setting);

	}

	private void setListener() {
		rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				switch (i) {
				case R.id.rb_tab_home:
					if (homeFragment == null) {
						homeFragment = new HomeFragment();
					}
					if (homeFragment.isAdded()) {
						transaction.show(homeFragment);
					} else {
						transaction.replace(R.id.frame_main, homeFragment, HomeFragment.class.getSimpleName());
					}

					if (xBlueService != null && xBlueService.isAllConnected()) {
						xBlueService.write(ProtolWrite.instance().writeForStep(0));
						// xBlueService.write(ProtolWrite.instance().w);
					}

					break;
				case R.id.rb_tab_me:
					if (meFragment == null) {
						meFragment = new MeFragment();
					}
					if (meFragment.isAdded()) {
						transaction.show(meFragment);
					} else {
						transaction.replace(R.id.frame_main, meFragment, MeFragment.class.getSimpleName());
					}
					break;
				case R.id.rb_tab_setting:
					if (settingFragment == null) {
						settingFragment = new SettingFragment();
					}
					if (settingFragment.isAdded()) {
						transaction.show(settingFragment);
					} else {
						transaction.replace(R.id.frame_main, settingFragment, SettingFragment.class.getSimpleName());
					}
					break;
				}
				transaction.commit();
			}
		});
	}

	@Override
	public void onBackPressed() {
		 ProtolWrite pw = ProtolWrite.instance();
//		 byte[] hexDataForTimeSync = pw.hexDataForTimeSync(new Date(), this);
//		byte[] writeForPhoneAndSmsCount = ProtolWrite.writeForPhoneAndSmsCount(0, 2);
//		
		 
//		 byte[] writeSleepSetting;
//		writeSleepSetting = ProtolWrite.instance().writeSleepSetting(1, 13, 13, 15, 15);
//		if (writeSleepSetting!=null) {
//			xBlueService.write(writeSleepSetting);
//		}
		
		
		ActionSheetDialog exitDialog = new ActionSheetDialog(this).builder();
		exitDialog.setTitle(getString(R.string.exit_app));
		exitDialog.addSheetItem(getString(R.string.confirm), SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// if (xplBluetoothService != null) {
						// xplBluetoothService.close();
						// }
						if (xBlueService != null && xBlueService.isAllConnected()) {
							xBlueService.closeAll();
						}
						
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								finish();
								System.exit(0);
								// android.os.Process.killProcess(android.os.Process.myPid());

							}
						}, 1000);
					}
				});

			}
		}).show();

	}
}
