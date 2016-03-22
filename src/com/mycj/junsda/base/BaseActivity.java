package com.mycj.junsda.base;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.laput.service.XBlueBroadcastUtils;
import com.laput.service.XBlueService;
import com.mycj.junsda.bean.StaticValue;
import com.mycj.junsda.broadcast.BluetoothEnableBroadcastReceiver;
import com.mycj.junsda.service.XplBluetoothService;
import com.mycj.junsda.util.FileUtil;
import com.mycj.junsda.util.ScreenShot;
import com.mycj.junsda.view.AlertDialog;
import com.mycj.junsda.view.LaputaAlertDialog;
import com.mycj.junsda.view.XplAlertDialog;


/**
 * Created by Administrator on 2015/11/20.
 */
public class BaseActivity extends FragmentActivity {
	protected static final int REQUEST_ENABLE_BLUETOOTH = 0x123;
//	private BluetoothEnableBroadcastReceiver receiver = new BluetoothEnableBroadcastReceiver(){
//
//		@Override
//		public void doBluetoothEnable() {
//			toast("请开启蓝牙");
//			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
//		}
//		
//	};
	private  LaputaAlertDialog findDialog;
	private BroadcastReceiver commReceiver = new BroadcastReceiver(){

	

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(XBlueBroadcastUtils.ACTION_FIND_PHONE)) {
				int fd = intent.getExtras().getInt((XBlueBroadcastUtils.EXTRA_FIND_PHONE));
				if (fd==1) {
					if (findDialog == null) {
						findDialog = showIosAlert("查找手机");
						
					}
					findDialog.show();
				}else{
					if (findDialog!=null ) {
						findDialog.dismiss();
					}
				}
				
			}
		}
		
	};
	
//	private BluetoothStateBroadcastReceiver stateReceiver = new BluetoothStateBroadcastReceiver(){
//
//		@Override
//		public void onBluetoothChange(int state, int previousState) {
//			if (state == BluetoothAdapter.STATE_ON) {
//				XplBluetoothService xplBluetoothService = getXplBluetoothService();
//				if (xplBluetoothService!=null) {
//					xplBluetoothService.scanDevice(true);
//				}
//			}
//		}
//
//		@Override
//		public void onBluetoothDisconnect() {
//			
//		}
//		
//	};
	
	
	protected void onCreate(android.os.Bundle arg0) {
		super.onCreate(arg0);
		
		if (findDialog!=null) {
			findDialog.dismiss();
		}
//		registerReceiver(receiver, new IntentFilter(XplBluetoothService.ACTION_BLUETOOTH_ENABLE));
		registerReceiver(commReceiver, new IntentFilter(XBlueBroadcastUtils.ACTION_FIND_PHONE));
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		unregisterReceiver(receiver);
		unregisterReceiver(commReceiver);
		if (findDialog!=null ) {
			findDialog.dismiss();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
			if (resultCode==RESULT_OK) {
				//
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	public XplAlertDialog showXplDialog(String msg){
		XplAlertDialog dialog = new XplAlertDialog(this).builder2(msg);
		return dialog;
		
	}
	
//    public JunsdaXplBluetoothService getXplBluetoothService(){
//        BaseApp app = (BaseApp)getApplication();
//        return app.getXplBluetoothService();
//    }
	public XBlueService getXBlueService(){
		 BaseApp app = (BaseApp)getApplication();
       return app.getXBlueService();
	}

//    public boolean isConnected(XplBluetoothService xplBluetoothService){
//        if (xplBluetoothService==null){
//            return false;
//        }
//        return xplBluetoothService.isBluetoothConnected();
//    }
    
	public LaputaAlertDialog showIosAlert(String msg){
		LaputaAlertDialog dialog = new LaputaAlertDialog(this).builder(msg);
		return dialog;
		
	}
	
    protected void toast(String text){
    	Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
    
    protected String getStringForTwo(int value) {
		String result = String.valueOf(value);
		if (result.length() == 1) {
			result = "0" + result;
		}
		return result;
	}
    
public void share(Handler mHandler){
	Bitmap bitmap = ScreenShot.takeScreenShot(this);
	String path = FileUtil.getandSaveCurrentImage(this, bitmap);
	if (path != null) {
		Message msg = new Message();
		msg.what = StaticValue.MSG_SHARE;
		msg.obj = path;
		mHandler.sendMessage(msg);
	}
}
    
protected ProgressDialog showProgressDialog(String msg) {
	ProgressDialog pDialog;
	pDialog = new ProgressDialog(this);
	pDialog.setCancelable(false);
	pDialog.setMessage(msg);
	pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	pDialog.show();
	return pDialog;
}
//    /**
//	 * 字体:简体方正
//	 * @return
//	 */
//	public Typeface getTypefaceForJianti(){
//		 BaseApp app = (BaseApp)getApplication();
//		return app.getTypefaceForJianti();
//	}
//	/**
//	 * 字体:繁体方正
//	 * @return
//	 */
//	public Typeface getTypefaceForFanti(){
//		 BaseApp app = (BaseApp)getApplication();
//		return app.getTypefaceForFanti();
//	}
//	/**
//	 * 字体:数字字母 方正
//	 * @return
//	 */
//	public Typeface getTypefaceForNumber(){
//		 BaseApp app = (BaseApp)getApplication();
//		return app.getTypefaceForNumber();
//	}
    
}
