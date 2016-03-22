package com.mycj.junsda.view;


import com.mycj.junsda.R;
import com.mycj.junsda.util.DataUtil;
import com.mycj.junsda.view.FreshCircleView.OnAnimatorCancelListener;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LaputaLoadingAlertDialog {
	private Context context;
	private Dialog dialog;
	private Display display;

	public LaputaLoadingAlertDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}
	
	Handler mHandler = new Handler(){};
	private FangTextView tvComplete;
	private ProgressBar pbSync;
	private ImageView imgCancel;
	
	public LaputaLoadingAlertDialog builder(String msg) {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(R.layout.view_laputa_loading_dialog, null);
		// 设置Dialog最小宽度为屏幕宽度
//		view.setMinimumWidth(display.getWidth());

		tvComplete = (FangTextView) view.findViewById(R.id.tv_progress);
		pbSync = (ProgressBar) view.findViewById(R.id.pb);
		pbSync.setMax(max);
		setProgress();
		setComplete();
		
		imgCancel = (ImageView) view.findViewById(R.id.img_cancel);
		
		// 定义Dialog布局和参数
		dialog = new Dialog(context,R.style.XplAlertDialog);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		return this;
	}
	
	
	private int max = 100;
	public LaputaLoadingAlertDialog max(int max){
		this.max = max;
		if (pbSync != null) {
			pbSync.setMax(this.max);
		}
		return this;
	}
	
	private int progress = 0;
	public void setProgress (int progress){
		if (progress >=max) {
			dismiss();
		}
		
		this.progress = progress;
		setProgress();
		setComplete();
	}
	
	
	public void setProgress(){
		if (pbSync != null) {
//			Log.e("xpl", "设置progress :" + progress + ",max :" + pbSync.getMax());
			pbSync.setProgress(this.progress);
//			pbSync.incrementProgressBy(20);
//			pbSync.invalidate();
		}
	}
	
	private int duration = 1000;
	
	public LaputaLoadingAlertDialog duration(int duration){
		this.duration = duration;
		return this;
	}
	public void incrementProgressBy(){
			if (pbSync != null) {
				int deff = max * 1000 / duration ;
//				pbSync.incrementProgressBy(max / duration );
				this.progress +=deff;
				setComplete();
				setProgress();
				if (progress >=max) {
					dismiss();
				}
			}
	}
	
	private Runnable increamentRunnable = new Runnable() {
		
		@Override
		public void run() {
			incrementProgressBy();
			mHandler.postDelayed(increamentRunnable, 1000);
		}
	};
	
	public void start(){
		mHandler.removeCallbacks(increamentRunnable);
		mHandler.postDelayed(increamentRunnable, 1000);
	}
	
	
	public void setComplete(){
		if (tvComplete != null) {
			tvComplete.setText((int)this.progress * 100 / this.max + "%" );
		}
	}
	
	public LaputaLoadingAlertDialog setListener(OnClickListener l){
		if (imgCancel !=null) {
			imgCancel.setOnClickListener(l);
		}
		
//		this.progress++;
//		setProgress();
//		setComplete();
//		Log.e("xpl", "progress :" + progress);
		return this;
	}
	
	
	public int getProgress(){
		Log.e("xpl", "progress :" + progress);
		return this.progress
				;
	}
	
	

	public LaputaLoadingAlertDialog setTitle(String title) {
		return this;
	}

	public LaputaLoadingAlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public LaputaLoadingAlertDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

	public void show() {
		start();
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}
	public boolean isShowing(){
		return dialog.isShowing();
	}

	public interface OnButtonClickListener {
		public void onPositiveClick(View v, WheelView wv, int number);

		public void onNegativeClick(View v, WheelView wv, int number);
	}

	public enum SheetItemColor {
		Blue("#037BFF"), Red("#FD4A2E");

		private String name;

		private SheetItemColor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
