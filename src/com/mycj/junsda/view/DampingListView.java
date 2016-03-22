package com.mycj.junsda.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

public class DampingListView extends ListView{
	private static final int MAX_Y_OVERSCROLL_DISTANCE= 200;
	private int mMaxOverScrollDistanceY = 0;
	
	public DampingListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	

	public DampingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DampingListView(Context context) {
		super(context);
		init(context);
	}
	private void init(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float density = metrics.density;
		mMaxOverScrollDistanceY = (int) (density*MAX_Y_OVERSCROLL_DISTANCE);
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxOverScrollDistanceY, isTouchEvent);
	}

}
