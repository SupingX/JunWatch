package com.laput.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.laput.service.XBlueManager.XBlueManagerListener;
import com.mycj.junsda.R;
import com.mycj.junsda.bean.Music;
import com.mycj.junsda.bean.ProtolWrite;
import com.mycj.junsda.bean.StaticValue;
import com.mycj.junsda.util.MessageUtil;
import com.mycj.junsda.util.SharedPreferenceUtil;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

public class XBlueService extends Service implements OnPreparedListener,OnCompletionListener{
	
	private XBlueManager xplBlueManager;
	private Handler xHandler = new Handler(){};
	private AbstractBluetoothStateBroadcastReceiver mBluetoothStateBroadcastReceiver = new AbstractBluetoothStateBroadcastReceiver() {

		@Override
		public void onBluetoothChange(int state, int previousState) {
			if (state == BluetoothAdapter.STATE_ON) {
				startScan();
			} else {

			}
		}
		
		
		
		@Override
		public void onBluetoothDisconnect(final BluetoothDevice device) {
			// xplBluetoothService.close(AdressSaved.getBindedAddress(getApplicationContext()));
			Log.e("", "&^^^^^^^^^^^^^^^^^^^^^^^短线" +device.getAddress());
//			playResource(R.raw.crystal);
			stop();
			xHandler.post(new Runnable() {
				@Override
				public void run() {
					String address = device.getAddress();
					XBlueConnectResult result = xplBlueManager.getXBlueConnectResultByAddress(address);
					if (result != null) {
						result.close();
						xplBlueManager.getConnectResultsMap().remove(address);
						XBlueBroadcastUtils.instance().sendBroadcastConnectState(getApplicationContext(),device, BluetoothGatt.STATE_DISCONNECTED);
					}
					if (!isAllConnected()) {
						startScan();
//						xplBlueManager.startAutoConnecting();//
					}
					
					//
//					mHandler.removeCallbacks(ringRun);
//					mHandler.postDelayed(ringRun, 12 * 1000);
				}
			});
		}



		@Override
		public void onBondStateChanged(BluetoothDevice device, int state) {
		}

	};
	private Handler mHandler = new Handler(){};
	private Runnable taskIncoming;
	private int phoneNo;
	private int smsMNo;
	@Override
	public void onCreate() {
		super.onCreate();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				musicList = initMusic();
			}
		}, 3000);
	
		xplBlueManager = XBlueManager.instance(getApplicationContext());
		xplBlueManager.setXBlueManagerListener(new XBlueManagerListener() {
			@Override
			public void doDeviceFound(HashMap<String, BluetoothDevice> scanDeviceMap) {
				
			}

			@Override
			public void doPlayMusic(int order) {
				playMusic(order);
			}

			@Override
			public void doFindPhone(int order) {
				if (order==1) {
					playResource(R.raw.crystal);
				}else{
					stop();
				}
			}

			@Override
			public void doDisconnected(BluetoothDevice device) {
				stop();
			}
		});
		
		mBluetoothStateBroadcastReceiver.registerBoradcastReceiverForCheckBlueToothState(getApplicationContext());
		
		
		
		/**
		 * 电话和短信
		 */
		taskIncoming = new Runnable() {
		

		

			@Override
			public void run() {
				int mmsCount = MessageUtil.getNewMmsCount(getApplicationContext());
				int msmCount = MessageUtil.getNewSmsCount(getApplicationContext());
				int phoneCount = MessageUtil.readMissCall(getApplicationContext());
				Log.e("BaseApp", "____电话数量 ： " + phoneNo + "-->" + phoneCount);
				Log.e("BaseApp", "____短信 ： " + smsMNo + "-->" + (msmCount + mmsCount) + (smsMNo != (mmsCount + msmCount)));
				// 数量只要有一个变化就发送
//				boolean isCallRemind;
//				isCallRemind = (boolean) SharedPreferenceUtil.get(getApplicationContext(), StaticValue.SHARE_REMIND_INCOMING, false);
				if (phoneNo != phoneCount || smsMNo != (mmsCount + msmCount)) {
					Log.e("xpl", "_______________________________________________________________________________________读取短信和电话数量 ： 有变化");
					// if (mmsCount == 0 && msmCount == 0 && phoneCount == 0) {
					// doWriteUnReadPhoneAndSmsToWatch(0, 0);
					// return;
					// } else {
					write(ProtolWrite.instance().writeForPhoneAndSmsCount(phoneCount, mmsCount + msmCount));
					// }
					
					//修改与10.28
					phoneNo = phoneCount;
					smsMNo = (mmsCount + msmCount);
					
					
					
					
				} else {
					Log.e("BaseApp", "__读取短信和电话数量 ： 无变化");
				}
				mHandler.postDelayed(taskIncoming, 8000);
			}
		};
		mHandler.postDelayed(taskIncoming, 8 * 1000);
	}
	
	public void startScan(){
		xplBlueManager.startScan();
	}
	
	public void write(byte [] data){
		xplBlueManager.writeChacteristicAll(new byte[][]{data});
	}
	public void write(byte [][] datas){
		xplBlueManager.writeChacteristicAll(datas);
	}
	
	public void stopScan(){
		xplBlueManager.stopScan();
	}
	
	public void connect(BluetoothDevice device){
		xplBlueManager.connect(device);
	}
	
	public boolean isAllConnected(){
		return xplBlueManager.isAllConnected();
	}
	
	public void closeAll(){
		
		xplBlueManager.stopScan();
		xplBlueManager.closeAll();
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new XBlueBinder();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	public class XBlueBinder extends Binder implements IBlueService{
		public XBlueService getXBlueService(){
			return XBlueService.this;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		closeAll();
		
		unregisterReceiver(mBluetoothStateBroadcastReceiver);
	}
	
	
	private int[] ids;// 保存音乐ID临时数组
	private String[] artists;
	private String[] titles;
	private Cursor cursor;
	private List<Music> musicList;
	private MediaPlayer mediaPlayer;
	private int playingPosition=0;
	/**
	 * 定义查找音乐信息数组，1.标题 2.音乐时间 3.艺术家 4.音乐id 5.显示名字 6.数据
	 */
	private String[] musicInfo = new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID

	};
	/**
	 * 音乐相关
	 * @return
	 */
	public List<Music> initMusic() {
		MusicLoader.instance(getContentResolver());
		cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicInfo, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		List<Music> listMusic;
		if (cursor != null) {
			cursor.moveToFirst();
			int size = cursor.getCount();
			ids = new int[size];
			artists = new String[size];
			titles = new String[size];
			for (int i = 0; i < size; i++) {
				ids[i] = cursor.getInt(3);
				artists[i] = cursor.getString(2);
				titles[i] = cursor.getString(0);
				cursor.moveToNext();
			}
			listMusic = MusicLoader.getMusicList();
			Log.d("TAG", "所有的音乐list : " + listMusic);
			return listMusic;
		}
		return null;
	}
	
	/**
	 * 停止播放
	 */
	public void stop() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	/**
	 * 释放
	 */
	public void release() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	public void reset() {
		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}
	}
	
	public void playResource(int resId){
		release();
		try {
			mediaPlayer = MediaPlayer.create(this, resId);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} 
//		mediaPlayer.setOnPreparedListener(this);
//		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.start();
	}
	
	
	public void play(Uri uri) {
		release();
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(this, uri);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.prepareAsync();
//			mediaPlayer.start();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	public void playMusic(int order) {
		if (order ==1) {
			if (musicList!=null) {
				Log.e("service", "playingPosition :" + playingPosition);
				Uri musicUriById = MusicLoader.getMusicUriById(musicList.get(playingPosition).getId());
				play(musicUriById);
			}
		}else{
			stop();
		}
		
	}
	
	public boolean isEnable(){
		return xplBlueManager.isBluetoothAdapterEnable();
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		playDown();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mediaPlayer.start();
	}
	
	/**
	 * 上一曲
	 */
	public void playUp() {
		if (musicList != null && musicList.size() > 0) {
			playingPosition--;
			if (playingPosition < 0) {
				playingPosition = musicList.size() - 1;
			}
			play(MusicLoader.getMusicUriById(musicList.get(playingPosition).getId()));
		}
	};

	/**
	 * 下一曲
	 */
	public void playDown() {
		if (musicList != null && musicList.size() > 0) {
			playingPosition++;
			if (playingPosition == musicList.size()) {
				playingPosition = 0;
			}
			play(MusicLoader.getMusicUriById(musicList.get(playingPosition).getId()));
		}
	};
	
}
