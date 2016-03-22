package com.mycj.junsda.view;

import com.mycj.junsda.R;
import com.mycj.junsda.util.DisplayUtil;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class SportCircleView extends View {
	private Paint paintCircle;
	private Paint paintRing;
	private int backgroundColor;
	private int backgroundWidth;
	private int ringColor;
	private int ringWidth;
	private int progress;// 进度
	private int max;// 最大进度
	private ObjectAnimator animator;

	public SportCircleView(Context context) {
		super(context);
		init(context, null, 0);
	}

	public SportCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public SportCircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		if (attrs != null) {
			TypedArray typedArray = context.getResources().obtainAttributes(
					attrs, R.styleable.SportCircleView);
			backgroundColor = getResources().getColor(
					typedArray.getResourceId(
							R.styleable.SportCircleView_circle_backgroundColor,
							-1));
			backgroundWidth = DisplayUtil.dip2px(typedArray.getDimension(
					R.styleable.SportCircleView_circle_backgroundWidth, 10),
					1.0f);
			ringColor = getResources().getColor(
					typedArray.getResourceId(
							R.styleable.SportCircleView_circle_ringColor, -1));
			ringWidth = DisplayUtil.dip2px(typedArray.getDimension(
					R.styleable.SportCircleView_circle_ringWidth, 10), 1.0f);
			max = typedArray.getInteger(R.styleable.SportCircleView_circle_max,
					100);
			progress = typedArray.getInteger(
					R.styleable.SportCircleView_circle_progress, 0);
			if (backgroundColor == -1) {
				backgroundColor = Color.GRAY;
			}
			if (ringColor == -1) {
				backgroundColor = Color.GREEN;
			}
		} else {
			backgroundColor = Color.GRAY;
			backgroundWidth = 12;
			ringColor = Color.GREEN;
			ringWidth = 7;
			max = 100;
			progress = 0;
		}

		paintCircle = new Paint();
		paintCircle.setAntiAlias(true); // 消除锯齿
		paintCircle.setDither(true); // 消除？
		paintCircle.setStyle(Paint.Style.STROKE); // 绘制空心圆
		paintCircle.setStrokeJoin(Paint.Join.ROUND);
		paintCircle.setStrokeCap(Paint.Cap.ROUND); // 设置圆角
		paintCircle.setStrokeWidth(backgroundWidth); // 设置进度条宽度
		paintCircle.setColor(backgroundColor);

		paintRing = new Paint();
		paintRing.setAntiAlias(true);
		paintRing.setDither(true);
		paintRing.setStyle(Paint.Style.STROKE);
		paintRing.setStrokeJoin(Paint.Join.ROUND);
		paintRing.setStrokeCap(Paint.Cap.ROUND);
		paintRing.setStrokeWidth(ringWidth);
		paintRing.setColor(ringColor);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = View.MeasureSpec.getSize(heightMeasureSpec);
//		Log.e("SportCircleView", "onMeasure");
		int mode = MeasureSpec.getMode(heightMeasureSpec);
		if (mode == MeasureSpec.AT_MOST) {
			height = Math.min(DisplayUtil.dip2px(getContext(), 200), height);
		}else if (mode==MeasureSpec.EXACTLY) {
			height = Math.min(DisplayUtil.dip2px(getContext(), 200), height);
	
		}
		int width = height;
		
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		Log.e("SportCircleView", "onDraw");
		int width = getWidth();
		int height = getHeight();
		int radius = (height - backgroundWidth) / 2;
		canvas.drawCircle(width / 2, height / 2, radius, paintCircle);

		float top = (backgroundWidth - ringWidth) / 2 + ringWidth / 2;
		float left = (backgroundWidth - ringWidth) / 2 + ringWidth / 2;
		float right = left + radius * 2;
		float bottom = top + radius * 2;

		RectF rectF = new RectF(left, top, right, bottom);
		canvas.drawArc(rectF, -90, getSweepAngleByProgress(progress), false,
				paintRing);

	}

	/**
	 * 根据当前进度获得 画arc的弧度[0，360]
	 *
	 * @param progress
	 * @return sweepAngle
	 */
	private float getSweepAngleByProgress(int progress) {
		float sweepAngle = 0;
		sweepAngle = progress * 360f / max;
		return sweepAngle;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		invalidate();
	}

	public int getProgress() {
		return this.progress;
	}

	public void setProgressWithAnimation(int progress, int duration) {
		valueAnimator = ValueAnimator.ofInt(0, progress);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int progress = (int) animation.getAnimatedValue();
				setProgress(progress);
			}
		});
		valueAnimator.setDuration(duration);
		// valueAnimator.setInterpolator(new LinearInterpolator());
		// valueAnimator.setInterpolator( new
		// AnticipateOvershootInterpolator(1));
		// valueAnimator.setInterpolator( new BounceInterpolator());
		// valueAnimator.setInterpolator( new CycleInterpolator(20));
		valueAnimator.setInterpolator(new OvershootInterpolator());
		valueAnimator.start();

	}

	public void stopAnimator() {
		if (valueAnimator != null) {
			valueAnimator.cancel();
		}
	}

	public interface OnProgressChangeListener {
		public void onChange(int progress);
	}

	private OnProgressChangeListener mOnProgressChangeListener;
	private ValueAnimator valueAnimator;

	public void setOnProgressChangeListener(
			OnProgressChangeListener mOnProgressChangeListener) {
		this.mOnProgressChangeListener = mOnProgressChangeListener;
	}
}
