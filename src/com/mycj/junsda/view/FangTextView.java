package com.mycj.junsda.view;

import com.mycj.junsda.R;
import com.mycj.junsda.base.BaseApp;
import com.mycj.junsda.base.CustomTypeface;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/11/16.
 */
public class FangTextView extends TextView {
	private Typeface typeface;
	private boolean isCanPressed;

	public FangTextView(Context context) {
		super(context);
		init(context, null);
	}

	public FangTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public FangTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);

	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FantTextView);
			isCanPressed = ta.getBoolean(R.styleable.FantTextView_fang_alpha, false);
			String typeface = ta.getString(R.styleable.FantTextView_fang_typeface);
			try {
				if (typeface!=null) {
					if (typeface.equals(CustomTypeface.JIAN.getName())) {
						setTypeface(BaseApp.TYPEFACE_JIAN);
					}else if (typeface.equals(CustomTypeface.FAN.getName())) {
						setTypeface(BaseApp.TYPEFACE_FAN);
					}else if (typeface.equals(CustomTypeface.NUM.getName())) {
						setTypeface(BaseApp.TYPEFACE_NUM);
					}
				}
			
			} catch (Exception e) {
			}
			ta.recycle();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (isCanPressed) {
				this.setAlpha(0.5f);
				invalidate();
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (isCanPressed) {
				this.setAlpha(1f);
				invalidate();
			}
			break;
		}

		return super.onTouchEvent(event);
	}

}
