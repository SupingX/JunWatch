package com.mycj.junsda.view;

import java.util.Calendar;
import java.util.Date;

import com.mycj.junsda.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateDialog extends AbstraceDialog {
	private Context context;
	private WheelView yearWV;
	private WheelView monthWV;
	private WheelView dayWV;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private TextView txt_msg;
	private Button btn_neg;
	private Button btn_pos;
	private ImageView img_line;
	
	private int year;
	private int month;
	private int day;
	
	public DateDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	public DateDialog(Context context,int year ,int month,int day) {
		this(context);
		this.year = year;
		this.month = month;
		this.day = day;
	}

	@Override
	public View setContontView() {
		
		
		//年月日视图
		View view = LayoutInflater.from(context).inflate(R.layout.view_date, null);
		//年
		yearWV = (WheelView) view.findViewById(R.id.year);
		yearWV.setAdapter(new NumberWheelAdapter(1970, 2100));
		yearWV.setCyclic(true);// 可循环滚动
		yearWV.setLabel(context.getResources().getString(R.string.year));// 文字
		yearWV.setCurrentItem(2000);
		//月
		monthWV = (WheelView) view.findViewById(R.id.month);
		monthWV.setAdapter(new NumberWheelAdapter(1, 12));
		monthWV.setCyclic(true);// 可循环滚动
		monthWV.setLabel(context.getResources().getString(R.string.month));
		monthWV.setCurrentItem(3);
		//日
		int monthMaxDay = DateUtil.getMonthMaxDay(getYear(),getMonth());
		dayWV = (WheelView) view.findViewById(R.id.day);
		dayWV.setAdapter(new NumberWheelAdapter(1, monthMaxDay));
		dayWV.setCyclic(true);// 可循环滚动
		dayWV.setLabel(context.getResources().getString(R.string.day));
		dayWV.setCurrentItem(4);
		
		return view;
	}
	
	public int getYear() {
		return yearWV.getCurrentItem();
	}

	public int getMonth() {
		return monthWV.getCurrentItem();
	}

	public int getDay() {
		return dayWV.getCurrentItem();
	}
	
	public Date getDate() {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, getYear());
		c.set(Calendar.MONTH, getMonth() - 1);
		c.set(Calendar.DAY_OF_MONTH, getDay());
		return c.getTime();
	}

}
