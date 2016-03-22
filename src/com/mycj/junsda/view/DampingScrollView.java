package com.mycj.junsda.view;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.ScrollView;

public class DampingScrollView extends ScrollView {
	private final static int MAX_SCROLL_HEIGHT = 200;// 最大滑动距离
	private final static float DAMPING_RATIO = 0.5f;// 阻尼系数
	private Context mContext;
	private View mView;
	private Rect mRect = new Rect();
	private float touchY;


	ListView v;
	public DampingScrollView(Context context) {
		super(context);
		this.mContext = context;
	}

	public DampingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
	}

	public DampingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			this.mView = getChildAt(0);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touchY = event.getY();
		}
		if (event.getAction()==MotionEvent.ACTION_MOVE){
			float y = event.getY();
			if(Math.abs(y-touchY)>20){
				return true;
			}
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mView == null) {
			return super.onTouchEvent(ev);
		} else {
			commonOnTouchEvent(ev);
		}


		return super.onTouchEvent(ev);
	}

	private void commonOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			touchY = ev.getY();
			break;
		case MotionEvent.ACTION_UP:
			if (isNeedAnimation()) {
				animation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			final float preY = touchY;
			float nowY = ev.getY();
			int deltaY = (int) (preY - nowY);
			touchY = nowY;
			if (isNeedMove()) {
				if (mRect.isEmpty()) {
					mRect.set(mView.getLeft(), mView.getTop(), mView.getRight(), mView.getBottom());
				}
				int offset = mView.getTop() - deltaY;
				if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {
					mView.layout(mView.getLeft(), mView.getTop() - (int) (deltaY * DAMPING_RATIO), mView.getRight(), mView.getBottom() - (int) (deltaY * DAMPING_RATIO));
				}
			}

			break;
		default:
			break;
		}
	}

	private boolean isNeedMove() {
		int viewHight = mView.getMeasuredHeight();
		int srollHight = getHeight();
		int offset = viewHight - srollHight;
		int scrollY = getScrollY();

		if (scrollY == 0 || scrollY == offset) {
			return true;
		}
		return false;
	}

	private boolean isNeedAnimation() {
		return !mRect.isEmpty();
	}

	private void animation() {
		TranslateAnimation ta = new TranslateAnimation(0, 0, mView.getTop(), mRect.top);
		ta.setDuration(200);
		mView.startAnimation(ta);
		mView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
		mRect.setEmpty();

	}

}
