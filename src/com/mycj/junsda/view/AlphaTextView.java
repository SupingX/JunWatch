package com.mycj.junsda.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class AlphaTextView extends TextView {

	public AlphaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public AlphaTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlphaTextView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			this.setAlpha(0.5f);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			this.setAlpha(1f);
			invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}
}
