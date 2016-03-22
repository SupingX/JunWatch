package com.mycj.junsda.view;

import com.mycj.junsda.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PopView extends LinearLayout{
	
	private LinearLayout llShare;
	private LinearLayout llDelete;
	private TextView tvShare;
	private TextView tvDelete;
	private ImageView imgShare;
	private ImageView imgDelete;
	private TriangleView triangView;
	
	
	public PopView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}


	public PopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PopView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		
		this.setOrientation(VERTICAL);
	
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		this.setLayoutParams(lp);
		triangView = new TriangleView(context);
		triangView.setGravity(Gravity.CENTER);
		tvShare = new TextView(context);
		tvShare.setText("分享");
		tvShare.setBackgroundResource(R.drawable.ic_more_share);
		tvShare.setTextSize(12);
		tvShare.setTextColor(Color.WHITE);
		tvShare.setGravity(Gravity.CENTER_VERTICAL);
		tvDelete = new TextView(context);
		tvDelete.setText("删除");
		tvDelete.setBackgroundResource(R.drawable.ic_more_delete);
		tvDelete.setTextSize(12);
		tvDelete.setGravity(Gravity.CENTER_VERTICAL);
		tvDelete.setTextColor(Color.WHITE);
		addView(triangView);
		addView(tvShare);
		addView(tvDelete);
	
	}

}
