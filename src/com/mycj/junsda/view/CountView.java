package com.mycj.junsda.view;

import java.text.DecimalFormat;

import com.mycj.junsda.base.BaseApp;
import com.mycj.junsda.util.DataUtil;

import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CountView extends View {

	private Paint linePaint;
	private Paint pathPaint;
	private Paint pointPaint;
	private Paint textPaint;
	private static final int SPACE_Y = 20;
	private static final int SPACE_X = 10;
	private static final int LINE_WIDTH = 2;
	private static final int MAX_SIZE = 31;
	private float spaceY;
	private float spaceX;
	private int spanX;
	private float[] datas;
	private int width;
	private int height;
	private float maxValue = 1000f;
	private int pointPos = 0;
	private float donwX;
	private Paint textPointPaint;
	private int size;

	public CountView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public CountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CountView(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float density = displayMetrics.density;
		spaceY = SPACE_Y * density;
		spaceX = SPACE_X * density;

		linePaint = new Paint();
		linePaint.setAntiAlias(true); // 消除锯齿
		linePaint.setStrokeWidth(LINE_WIDTH); //
		linePaint.setColor(Color.parseColor("#C2842D")); //
		linePaint.setStrokeJoin(Paint.Join.ROUND);
		linePaint.setStrokeCap(Paint.Cap.ROUND); // 设置圆角
		linePaint.setDither(true);

		textPaint = new Paint();
		textPaint.setAntiAlias(true); // 消除锯齿
		textPaint.setStrokeWidth(LINE_WIDTH); //
		textPaint.setColor(Color.parseColor("#90ffffff")); //
		textPaint.setStrokeJoin(Paint.Join.ROUND);
		textPaint.setStrokeCap(Paint.Cap.ROUND); // 设置圆角
		textPaint.setDither(true);
		textPaint.setTypeface(BaseApp.TYPEFACE_NUM);

		textPointPaint = new Paint();
		textPointPaint.setAntiAlias(true); // 消除锯齿
		textPointPaint.setStrokeWidth(LINE_WIDTH); //
		textPointPaint.setColor(Color.WHITE); //
		textPointPaint.setStrokeJoin(Paint.Join.ROUND);
		textPointPaint.setStrokeCap(Paint.Cap.ROUND); // 设置圆角
		textPointPaint.setDither(true);
		textPointPaint.setTypeface(BaseApp.TYPEFACE_NUM);

		pathPaint = new Paint();
		pathPaint.setAntiAlias(true); // 消除锯齿
		pathPaint.setStrokeWidth(LINE_WIDTH);
		pathPaint.setColor(Color.parseColor("#F5AB19"));
		pathPaint.setDither(true);
		pathPaint.setStrokeCap(Paint.Cap.ROUND);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setStrokeJoin(Paint.Join.ROUND);

		pointPaint = new Paint();
		pointPaint.setAntiAlias(true); // 消除锯齿
		pointPaint.setStrokeWidth(LINE_WIDTH);
		pointPaint.setColor(Color.WHITE);
		pointPaint.setDither(true);
		pointPaint.setStrokeCap(Paint.Cap.ROUND);
		pointPaint.setStyle(Paint.Style.FILL);
		pointPaint.setStrokeJoin(Paint.Join.ROUND);
		
		
		datas = new float[MAX_SIZE];
	}
	
	public void setDatas(float[] datas,int pos) {
		this.datas = datas;
		size = datas.length;
//		pointPos = size - 1;
		pointPos = pos;
		invalidate();
	}
	public void setDatas(float[] datas) {
		this.datas = datas;
		size = datas.length;
		pointPos = size - 1;
		invalidate();
	}
	
	public void setMaxValue(float max){
		this.maxValue = max;
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		width = getWidth();
		height = getHeight();
		canvas.drawLine(spaceX + LINE_WIDTH, height - spaceY, width - spaceX, height - spaceY, linePaint);
		canvas.drawLine(spaceX + LINE_WIDTH, height - spaceY, spaceX + LINE_WIDTH, spaceY, linePaint);
		if (datas != null && datas.length > 0 && datas.length <= MAX_SIZE) {
			spanX = (int) ((width - 2 * spaceX) * 1.0 / (MAX_SIZE - 1));
			drawPath(canvas);
			drawText(canvas);
			drawPoint(canvas);
		}
	}

	class PointItem {
		private PointF point;
		private int index;

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public PointF getPoint() {
			return point;
		}

		public void setPoint(PointF point) {
			this.point = point;
		}
	}

	public class PointEvaluator implements TypeEvaluator<PointItem> {

		@Override
		public PointItem evaluate(float fraction, PointItem startValue, PointItem endValue) {
			return null;
		}
	}

	private void drawText(Canvas canvas) {
		for (int i = 0; i < datas.length; i++) {
			String text = String.valueOf(i + 1);
			Rect rectTextCenter = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), rectTextCenter);
			float x = 0;
			float y = height - spaceY / 2 - rectTextCenter.centerY();
			if (i == 0) {// 第一个坐标 (0,y)
				x = 0f;
				canvas.drawText(text, x + spaceX, y, textPaint);
			} else if (i == datas.length - 1) {// 最后一个坐标(31,y)
				x = i * spanX - rectTextCenter.centerX();
				canvas.drawText(text, x + spaceX, y, textPaint);
			} else {
				if ((i + 1) % 2 != 0 ) {// 只绘制奇数
					if (i != datas.length - 2) {// 并且最后一个数不是奇数时，不绘制最后一个奇数
						x = i * spanX - rectTextCenter.centerX();
						canvas.drawText(text, x + spaceX, y, textPaint);
					}	
				} 
			}

		}
	}

	private void drawPath(Canvas canvas) {
		Path path = new Path();
		for (int i = 0; i < datas.length; i++) {
			PointF p = getPointByIndex(i);
			if (i == 0) {
				path.moveTo(p.x, p.y);
			}
			path.lineTo(p.x, p.y);

		}
		canvas.drawPath(path, pathPaint);
	}

	private void drawPoint(Canvas canvas) {
		PointF point = getPointByIndex(pointPos);
		canvas.drawCircle(point.x, point.y, 5, pointPaint);
		//
		Rect rectTextCenter = new Rect();

		String text = format(datas[pointPos]) + unit;
		textPointPaint.getTextBounds(text, 0, text.length(), rectTextCenter);
		float x = 0;
		float y = point.y - rectTextCenter.height();
		if (point.x + rectTextCenter.centerX() > width) {
			x = width - rectTextCenter.width();
		} else if (point.x - rectTextCenter.centerX() < 0) {
			x = 0;
		} else {
			x = point.x - rectTextCenter.centerX();
		}

		canvas.drawText(text, x, y, textPointPaint);
	}

	private PointF getPointByIndex(int i) {

		float x = spaceX + LINE_WIDTH + i * spanX * 1.0f;
		float y = (1 - 1.0f * datas[i] / maxValue) * (height - spaceY * 2) + spaceY;
		return new PointF(x, y);
	}

	private FormatType formatType = FormatType.FORMAT_1;

	public enum FormatType {
		FORMAT_0("0"), FORMAT_1("0.0"), FORMAT_2("0.00");
		private String value;

		FormatType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public void setFormat(FormatType formatType) {
		this.formatType = formatType;
	}
	
	public String unit="";
	public void setUnit(String unit){
		this.unit = unit;
	}

	private String format(float value) {
		
		DecimalFormat df = new DecimalFormat(formatType.getValue());
		return df.format(value);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			donwX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if (datas!=null) {
				float moveX = event.getX();
				if (Math.abs(moveX - donwX) > spanX / 3) {
					if (moveX - donwX > 0) {// 右
						if (pointPos < datas.length - 1) {
							pointPos++;
							invalidate();
							donwX = moveX;
						}
					} else {// 左
						if (pointPos > 0) {
							pointPos--;
							invalidate();
							donwX = moveX;
						}
					}
				}
			}
			break;

		default:
			break;
		}

		return super.onTouchEvent(event);
	}

}
