package com.mycj.junsda.view;

import com.mycj.junsda.base.BaseApp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class TriangleView extends TextView {
	private Paint pathPaint;
	private PointF p1;
	private PointF p2;
	private PointF p3;
	private Path p;
	
	public TriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public TriangleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TriangleView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		pathPaint = new Paint();
		pathPaint.setAntiAlias(true); // 消除锯齿
		pathPaint.setStrokeWidth(1); //
		pathPaint.setColor(Color.parseColor("#2A4266")); //
		pathPaint.setStrokeJoin(Paint.Join.ROUND);
		pathPaint.setStrokeCap(Paint.Cap.ROUND); // 设置圆角
		pathPaint.setDither(true);
		p = new Path();
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	
	
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float height = getHeight();
		p.moveTo(0,height);
		p.lineTo(height,height);
		p.lineTo(height/2,0);
		canvas.drawPath(p, pathPaint);
	}

}
