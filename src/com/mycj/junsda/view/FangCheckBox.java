package com.mycj.junsda.view;

import com.mycj.junsda.R;
import com.mycj.junsda.base.BaseApp;
import com.mycj.junsda.base.CustomTypeface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/11/16.
 */
public class FangCheckBox extends CheckBox {
	private boolean isCanPressed;

	public FangCheckBox(Context context) {
		super(context);
		init(context, null);
	}

	public FangCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public FangCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);

	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FangCheckBox);
			String typeface = ta.getString(R.styleable.FangCheckBox_fbox_typeface);
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


}
