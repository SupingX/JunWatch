package com.mycj.junsda.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView.OnScrollListener;

public class FreshCircleView extends View {

	private int size = 200;
	private final int MAX_LINE_SIZE = 12;// 小线条的数量
	private int mmWidth;
	private float perAngel;
	private Paint paint;

	/** 当前数量 **/
	private int currentLineSize = 0;
	private ValueAnimator loadingAnimator;

	public FreshCircleView(Context context) {
		super(context);
		init(context);
	}

	public FreshCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public FreshCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FreshCircleView(Context context, int size) {
		super(context);
		this.size = size;
		init(context);
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// setMeasuredDimension(size, size);
	// }
	
	private boolean isCancel = false;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mmWidth = getWidth();
		float innerRadius = mmWidth * 1.0f / 5;
		float outterRadius = innerRadius * 2;
		if (!isCancel) {
			
	
		if (!isOnce) {
			for (int i = 0; i < currentLineSize; i++) {
				double pi = Math.PI * i * perAngel / 180f;
				PointF innerPointF = getPointFromPi(pi, innerRadius);
				PointF outterPointF = getPointFromPi(pi, outterRadius);
				paint.setAlpha((int) ((currentLineSize - i) * 255 * 1.0f / MAX_LINE_SIZE));
				canvas.drawLine(innerPointF.x, innerPointF.y, outterPointF.x, outterPointF.y, paint);
			}
		} else {
			for (int i = 0; i < MAX_LINE_SIZE; i++) {
				double pi = Math.PI * i * perAngel / 180f;
				PointF innerPointF = getPointFromPi(pi, innerRadius);
				PointF outterPointF = getPointFromPi(pi, outterRadius);
				paint.setAlpha((int) ((currentLineSize - i) * 255 * 1.0f / MAX_LINE_SIZE));
				// LinearGradient shader = new LinearGradient(innerPointF.x,
				// innerPointF.y
				// , outterPointF.x, outterPointF.y, Color.WHITE,
				// Color.parseColor("#00FFFFFF"), TileMode.CLAMP);
				// paint.setShader(shader);
				canvas.drawLine(innerPointF.x, innerPointF.y, outterPointF.x, outterPointF.y, paint);
			}
		}
		}else{
//			Rect rect = new Rect();
//			String text = "连接超时";
//			paint.setTextSize(20);
//			paint.getTextBounds(text, 0, text.length(), rect);
//			canvas.drawText(text, mmWidth/2-rect.centerX(), mmWidth/2-rect.centerY(), paint);
		}

	}

	public void setCurrentLineSize(int size) {
		if (size >= MAX_LINE_SIZE) {
			size = MAX_LINE_SIZE;
			startLoading();
			return;
		}
		if (size < 0) {
			size = 0;
		}

		this.currentLineSize = size;
		invalidate();
	}
	private boolean isOnce = false;
	
	public void startLoading() {
		
		if (loadingAnimator == null) {
			loadingAnimator = ValueAnimator.ofInt(1, MAX_LINE_SIZE + 1);
			loadingAnimator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					currentLineSize = (Integer) animation.getAnimatedValue();
					if (currentLineSize>=MAX_LINE_SIZE) {
						isOnce = true	;
					}
					invalidate();
				}
			});
			loadingAnimator.setDuration(1500);
			loadingAnimator.setRepeatCount(-1);
			loadingAnimator.setRepeatMode(ValueAnimator.INFINITE);
			loadingAnimator.setInterpolator(new LinearInterpolator());
			loadingAnimator.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
					isCancel = true;
				
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							if (mOnAnimatorCancelListenerl!=null) {
								mOnAnimatorCancelListenerl.onCancel();
							}
							invalidate();
						}
					}, 1000);
	
					
				}
			});
			
		}
		loadingAnimator.start();
		isCancel = false;
	}
	
	private Handler mHandler = new Handler(){};

	public void stopLoading() {
		if (loadingAnimator != null) {
			loadingAnimator.cancel();
			loadingAnimator = null;
		}
	}

	private void init(Context context) {
		paint = new Paint();
		paint.setStrokeWidth(4);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAntiAlias(true);
		paint.setColor(Color.parseColor("#037BFF"));
		perAngel = 360 * 1.0F / MAX_LINE_SIZE;
		
	
	}

	/**
	 * 获取离中心点radius距离 角度Pi的点
	 * 
	 * @param pi
	 * @param radius
	 * @return
	 */
	private PointF getPointFromPi(double pi, float radius) {
		float x = 0F;
		float y = 0F;
		if (pi >= 0 && pi <= Math.PI / 2) {
			x = (float) (mmWidth / 2 + radius * Math.sin(pi));
			y = (float) (mmWidth / 2 - radius * Math.cos(pi));
		} else if (pi > Math.PI / 2 && pi <= Math.PI) {
			x = (float) (mmWidth / 2 + radius * Math.sin(Math.PI - pi));
			y = (float) (mmWidth / 2 + radius * Math.cos(Math.PI - pi));
		} else if (pi > Math.PI && pi <= Math.PI + Math.PI / 2) {
			x = (float) (mmWidth / 2 - radius * Math.sin(pi - Math.PI));
			y = (float) (mmWidth / 2 + radius * Math.cos(pi - Math.PI));
		} else if (pi > Math.PI + Math.PI / 2 && pi <= 2 * Math.PI) {
			x = (float) (mmWidth / 2 - radius * Math.sin(2 * Math.PI - pi));
			y = (float) (mmWidth / 2 - radius * Math.cos(2 * Math.PI - pi));
		}
		return new PointF(x, y);
	}
	
	public interface OnAnimatorCancelListener{
		public void onCancel();
	}
	private OnAnimatorCancelListener mOnAnimatorCancelListenerl;
	public void setOnAnimatorCancelListener(OnAnimatorCancelListener l){
		this.mOnAnimatorCancelListenerl = l;
	}
}
