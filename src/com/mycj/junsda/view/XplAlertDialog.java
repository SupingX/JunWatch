package com.mycj.junsda.view;


import com.mycj.junsda.R;
import com.mycj.junsda.view.FreshCircleView.OnAnimatorCancelListener;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class XplAlertDialog {
	private Context context;
	private Dialog dialog;
	private Display display;
	private FreshCircleView fcv;
	private TextView tvInfo;

	public XplAlertDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		
	}
	Handler mHandler = new Handler(){};
	
	public XplAlertDialog builder(String msg) {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(R.layout.view_xpl_alert_dialog, null);
		fcv = (FreshCircleView) view.findViewById(R.id.fcv);
		tvInfo = (TextView) view.findViewById(R.id.tv_info);
		fcv.setVisibility(View.GONE);
		tvInfo.setVisibility(View.VISIBLE);
		tvInfo.setText(msg);
//		fcv.setCurrentLineSize(12);
		// 设置Dialog最小宽度为屏幕宽度
//		view.setMinimumWidth(display.getWidth());
		// 获取View控件

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
	
	public XplAlertDialog builder2(String msg) {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(R.layout.view_xpl_alert_dialog, null);
		fcv = (FreshCircleView) view.findViewById(R.id.fcv);
		tvInfo = (TextView) view.findViewById(R.id.tv_info);
		tvInfo.setText(msg);
		fcv.setVisibility(View.VISIBLE);
		tvInfo.setVisibility(View.INVISIBLE);
//		fcv.setCurrentLineSize(12);
		// 设置Dialog最小宽度为屏幕宽度
//		view.setMinimumWidth(display.getWidth());
		// 获取View控件

		// 定义Dialog布局和参数
		dialog = new Dialog(context,R.style.XplAlertDialog);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		
		fcv.setOnAnimatorCancelListener(new OnAnimatorCancelListener() {
			
			@Override
			public void onCancel() {
				fcv.setVisibility(View.INVISIBLE);
				tvInfo.setVisibility(View.VISIBLE);
				final ObjectAnimator a = ObjectAnimator.ofFloat(tvInfo, "alpha", 0.1f, 1f);
				a.setDuration(200);
				a.addListener(new AnimatorListener() {
					
					@Override
					public void onAnimationStart(Animator animation) {
						
					}
					
					@Override
					public void onAnimationRepeat(Animator animation) {
						
					}
					
					@Override
					public void onAnimationEnd(Animator animation) {
						mHandler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								dismiss();
							}
						}, 1500);
					}
					
					@Override
					public void onAnimationCancel(Animator animation) {
						
					}
				});
				a.start();
			
			
			}
		});
		fcv.startLoading();
		return this;
	}
	
	public void cancel(){
		
		fcv.stopLoading();
	}
	public void cancel(String cancelString){
		tvInfo.setText(cancelString);
		fcv.stopLoading();
	}
	
	public void initListener() {
	}

	public XplAlertDialog setTitle(String title) {
		return this;
	}

	public XplAlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public XplAlertDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

	public void show() {
		initListener();
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
