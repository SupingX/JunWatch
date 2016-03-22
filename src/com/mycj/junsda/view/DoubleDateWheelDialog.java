package com.mycj.junsda.view;

import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.mycj.junsda.R;
import com.mycj.junsda.util.DisplayUtil;

public class DoubleDateWheelDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private TextView txt_msg;
	private TextView btn_neg;
	private TextView btn_pos;
	private ImageView img_line;
	private Display display;
	private boolean showTitle = false;
	private boolean showMsg = false;
	private boolean showPosBtn = false;
	private boolean showNegBtn = false;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private Calendar c;
	private WheelView leftWheel;
	private WheelView rightWheel;
	

	public DoubleDateWheelDialog(Context context,int month,int day) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		c = getCalendar(month, day);

	}
	
	public Calendar getCalendar(int month ,int day) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.DAY_OF_MONTH, day);
		return c;
	}

	public void dismiss() {
		dialog.dismiss();
	}
	
	public int getLeftValue(){
		return leftWheel.getCurrentItem()+1;
	}
	
	public int getRightValue(){
		return rightWheel.getCurrentItem()+1;
	}


	public DoubleDateWheelDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(R.layout.view_single_wheel_dialog, null);
		// 获取自定义Dialog布局中的控件
		lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_title.setVisibility(View.GONE);
		txt_msg = (TextView) view.findViewById(R.id.txt_msg);
		txt_msg.setVisibility(View.GONE);
		btn_neg = (TextView) view.findViewById(R.id.btn_neg);
		btn_neg.setVisibility(View.GONE);
		btn_pos = (TextView) view.findViewById(R.id.btn_pos);
		btn_pos.setVisibility(View.GONE);
		img_line = (ImageView) view.findViewById(R.id.img_line);
		img_line.setVisibility(View.GONE);
		ImageView img_left = (ImageView) view.findViewById(R.id.img_left);
		img_left.setVisibility(View.GONE);
		ImageView img_right = (ImageView) view.findViewById(R.id.img_right);
		img_right.setVisibility(View.GONE);

		llLeft = (LinearLayout) view.findViewById(R.id.ll_left);
		llRight = (LinearLayout) view.findViewById(R.id.ll_right);
		leftWheel = (WheelView) view.findViewById(R.id.wheel_left);
		rightWheel = (WheelView) view.findViewById(R.id.wheel_right);
		
		leftWheel.setAdapter(new NumberWheelAdapter(1, 12));
		leftWheel.setCyclic(false);// 可循环滚动
//		wheel.setLabel(context.getResources().getString(R.string.month));
//		wheel.setLabel(context.getResources().getString(R.string.year));// 文字
		leftWheel.setCurrentItem(c.get(Calendar.MONTH));// 加入 当前1901 ，位置为
																// 1901-1900=1，0为1900；

		leftWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				
			}
		});
		
		rightWheel.setAdapter(new NumberWheelAdapter(1, 31));
		rightWheel.setCyclic(false);// 可循环滚动
//		wheel.setLabel(context.getResources().getString(R.string.month));
//		wheel.setLabel(context.getResources().getString(R.string.year));// 文字
		rightWheel.setCurrentItem(c.get(Calendar.DAY_OF_MONTH)-1);// 加入 当前1901 ，位置为
		// 1901-1900=1，0为1900；
		
		rightWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		});
		

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = 0;
		int screenheight = DisplayUtil.getScreenMetrics(context).y;
		textSize = (int) ((screenheight / 100) * 2.5f);
		leftWheel.TEXT_SIZE = textSize;
		rightWheel.TEXT_SIZE = textSize;
		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.AlertDialogStyle);
		dialog.setContentView(view);

		// 调整dialog背景大小
		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), FrameLayout.LayoutParams.WRAP_CONTENT));

		return this;
	}

	public DoubleDateWheelDialog setTitle(String title) {
		showTitle = true;
		if ("".equals(title)) {
			txt_title.setText("标题");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	public DoubleDateWheelDialog setMsg(String msg) {
		showMsg = true;
		if ("".equals(msg)) {
			txt_msg.setText("内容");
		} else {
			txt_msg.setText(msg);
		}
		return this;
	}

	public DoubleDateWheelDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public DoubleDateWheelDialog setPositiveButton(String text, final OnClickListener listener) {
		showPosBtn = true;
		if ("".equals(text)) {
			btn_pos.setText("确定");
		} else {
			btn_pos.setText(text);
		}
		llRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}

	public DoubleDateWheelDialog setNegativeButton(String text, final OnClickListener listener) {
		showNegBtn = true;
		if ("".equals(text)) {
			btn_neg.setText("取消");
		} else {
			btn_neg.setText(text);
		}
		llLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}

	private void setLayout() {
		if (!showTitle && !showMsg) {
			txt_title.setText("提示");
			txt_title.setVisibility(View.VISIBLE);
		}

		if (showTitle) {
			txt_title.setVisibility(View.VISIBLE);
		}

		if (showMsg) {
			txt_msg.setVisibility(View.VISIBLE);
		}

		if (!showPosBtn && !showNegBtn) {
			btn_pos.setText("确定");
			btn_pos.setVisibility(View.VISIBLE);
			// btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
			btn_pos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}

		if (showPosBtn && showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			// btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
			btn_neg.setVisibility(View.VISIBLE);
			// btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
			img_line.setVisibility(View.VISIBLE);
		}

		if (showPosBtn && !showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			// btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}

		if (!showPosBtn && showNegBtn) {
			btn_neg.setVisibility(View.VISIBLE);
			// btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}
	}

	public void show() {
		setLayout();
		dialog.show();
	}
}
