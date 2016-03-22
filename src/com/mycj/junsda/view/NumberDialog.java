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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycj.junsda.R;
import com.mycj.junsda.util.DisplayUtil;




public class NumberDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private TextView txt_msg;
	private TextView btn_neg;
	private TextView btn_pos;
//	private ImageView img_line;
	private Display display;
	private boolean showTitle = true;
	private boolean showMsg = false;
	private boolean showPosBtn = false;
	private boolean showNegBtn = false;
	private View initNumberPicker;
	private int year;
	private int month;
	private int day;
	private WheelView yearWV;
	private WheelView monthWV;
	private WheelView dayWV;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private Calendar c;

	public NumberDialog(Context context,int year,int month,int day) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		this.year = year;
		this.month = month;
		this.day = day;
		c = getCalendar(year,month,day);
		Log.e("", "当前日期  ---〉" +DateUtil.dateToString(c.getTime(), "yyyy-MM-dd"));
	
	}
	
	public NumberDialog(Context context,Date date) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		c = Calendar.getInstance();
		c.setTime(date);
		Log.e("", "当前日期  ---〉" +DateUtil.dateToString(c.getTime(), "yyyy-MM-dd"));
	
	}
	public int getYear() {
		return yearWV.getCurrentItem()+1900;
	}

	public int getMonth() {
		return monthWV.getCurrentItem()+1;
	}

	public int getDay() {
		return dayWV.getCurrentItem()+1;
	}
	
	public Calendar getCalendar(int year ,int month ,int day) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.DAY_OF_MONTH, day);
		return c;
	}
	public void dismiss(){
		dialog.dismiss();
	}
	
	public Calendar getCalendar(){
		return this.c;
	}
	
	private static final int MIN_YEAR =1900;
	private static final int MAX_YEAR = 2200;
	private static final int MAX_MONTH = 12;
	private static final int MIN_MONTH = 1;
	
	public NumberDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_numberdialog, null);
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
//		img_line = (ImageView) view.findViewById(R.id.img_line);
//		img_line.setVisibility(View.GONE);
		ImageView img_left = (ImageView) view.findViewById(R.id.img_left);
		img_left.setVisibility(View.GONE);
		ImageView img_right = (ImageView) view.findViewById(R.id.img_right);
		img_right.setVisibility(View.GONE);
		
		
		
		llLeft = (LinearLayout) view.findViewById(R.id.ll_left);
		llRight = (LinearLayout) view.findViewById(R.id.ll_right);
			
		updateTitleText(c);
		//年
		yearWV = (WheelView) view.findViewById(R.id.year);
		yearWV.setAdapter(new NumberWheelAdapter(MIN_YEAR, MAX_YEAR));
		yearWV.setCyclic(false);// 可循环滚动
//		yearWV.setLabel(context.getResources().getString(R.string.year));// 文字
		yearWV.setCurrentItem(c.get(Calendar.YEAR)-MIN_YEAR);//加入 当前1901 ，位置为 1901-1900=1，0为1900；
		
		yearWV.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				c.set(Calendar.YEAR, newValue+1900);
				Log.e("", "当前日期  ---〉" +DateUtil.dateToString(c.getTime(), "yyyy-MM-dd"));
				int monthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (getDay()>monthMaxDay) {
					dayWV.setCurrentItem(monthMaxDay-1, true);
//					c.set(Calendar.DAY_OF_MONTH, monthMaxDay);
				}
				dayWV.setAdapter(new NumberWheelAdapter(1, monthMaxDay));
				updateTitleText(c);
			}
		});
		//月
		monthWV = (WheelView) view.findViewById(R.id.month);
		monthWV.setAdapter(new NumberWheelAdapter(MIN_MONTH, MAX_MONTH));
		monthWV.setCyclic(false);// 可循环滚动
//		monthWV.setLabel(context.getResources().getString(R.string.month));
		monthWV.setCurrentItem(c.get(Calendar.MONTH));//位置为当前月份 +1 - 1;
		monthWV.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				c.set(Calendar.MONTH, newValue);
				Log.e("", "当前日期  ---〉" +DateUtil.dateToString(c.getTime(), "yyyy-MM-dd"));
				int monthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (getDay()>monthMaxDay) {
					dayWV.setCurrentItem(monthMaxDay-1, true);
//					c.set(Calendar.DAY_OF_MONTH, monthMaxDay);
				}
				dayWV.setAdapter(new NumberWheelAdapter(1, monthMaxDay));
				updateTitleText(c);
			}
		});
		//日
		int monthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		dayWV = (WheelView) view.findViewById(R.id.day);
		dayWV.setAdapter(new NumberWheelAdapter(1, monthMaxDay));
		dayWV.setCyclic(false);// 可循环滚动
//		dayWV.setLabel(context.getResources().getString(R.string.day));
		dayWV.setCurrentItem(c.get(Calendar.DAY_OF_MONTH)-1);//位置为当前月份-1
		
		dayWV.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				c.set(Calendar.DAY_OF_MONTH, newValue+1);
				Log.e("", "当前日期  ---〉" +DateUtil.dateToString(c.getTime(), "yyyy-MM-dd"));
				updateTitleText(c);
			}
		});
		
		
		
		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = 0;
		int screenheight = DisplayUtil.getScreenMetrics(context).y;
		textSize = (int) ((screenheight / 100) * 2.5f);
		yearWV.TEXT_SIZE = textSize;
		monthWV.TEXT_SIZE = textSize;
		dayWV.TEXT_SIZE = textSize;
		//
		OnWheelChangedListener hourWheelListener;
		OnWheelScrollListener hourOnWheelScrollListener;
		
		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.AlertDialogStyle);
		dialog.setContentView(view);

		// 调整dialog背景大小
		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
				.getWidth() * 0.85), FrameLayout.LayoutParams.WRAP_CONTENT));

		return this;
	}
	
		
	protected void updateTitleText(Calendar c2) {
		Date time = c2.getTime();
		String text = DateUtil.dateToString(time, "yyyy-MM-dd");
		txt_title.setText(text );;
	}


	
	public NumberDialog setTitle(String title) {
	
		showTitle = true;
		updateTitleText(c);
		if ("".equals(title)) {
			txt_title.setText("标题");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	public NumberDialog setMsg(String msg) {
		showMsg = true;
		if ("".equals(msg)) {
			txt_msg.setText("内容");
		} else {
			txt_msg.setText(msg);
		}
		return this;
	}

	public NumberDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public NumberDialog setPositiveButton(String text,
			final OnClickListener listener) {
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

	public NumberDialog setNegativeButton(String text,
			final OnClickListener listener) {
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
//			btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
			btn_pos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}

		if (showPosBtn && showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
//			btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
			btn_neg.setVisibility(View.VISIBLE);
//			btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
//			img_line.setVisibility(View.VISIBLE);
		}

		if (showPosBtn && !showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
//			btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}

		if (!showPosBtn && showNegBtn) {
			btn_neg.setVisibility(View.VISIBLE);
//			btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}
	}

	public void show() {
		setLayout();
		dialog.show();
	}
}
