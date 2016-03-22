package com.mycj.junsda.view;


import com.mycj.junsda.R;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * TODO: document your custom view class.
 */
public class RadarView extends View {

    private Bitmap bgBitmap;
    private Bitmap ringBitmap;
    private Paint paintBg;
    private Paint mTextCenterPaint;
    private Paint paintRing;
    private int width;
    private int height;
    private Matrix matrix;
    private   String TIPS_ING = "";
    private   String TIPS_START = "搜索设备"; 
    private String tips = TIPS_START;

    public RadarView(Context context) {
        super(context);
        init(context);
        
    }



    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        Resources resources = context.getResources();
        // 这里2张图片尺寸大小是一样的。
        bgBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_search_front);
        ringBitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_search_back);
        paintBg = new Paint();
        paintBg.setAlpha(168);
        paintBg.setAntiAlias(true);
        paintBg.setDither(true);
        paintRing = new Paint();
        paintRing.setAntiAlias(true);
        paintRing.setDither(true);

        TIPS_ING  = context.getString(R.string.searching);
        TIPS_START  = context.getString(R.string.start_search);

        mTextCenterPaint = new Paint();
        mTextCenterPaint.setTextSize(20);
        mTextCenterPaint.setAntiAlias(true);
        mTextCenterPaint.setDither(true);
        mTextCenterPaint.setColor(Color.WHITE);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/zhun_yuan_jian.ttf");
        mTextCenterPaint.setTypeface(typeface);

        matrix = new Matrix();
        matrix.postRotate(degree, width / 2, height / 2);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = ringBitmap.getWidth();
        int height = ringBitmap.getHeight();
        int size = Math.max(width,height);
        setMeasuredDimension(size,size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
//      canvas.concat(matrix);
//      canvas.drawBitmap(ringBitmap,0,0,paintBg);
        canvas.drawBitmap(ringBitmap,matrix,paintRing);
        canvas.drawBitmap(bgBitmap,0,0,paintBg);
        Rect rectTextCenter = new Rect();
        mTextCenterPaint.getTextBounds(tips, 0, tips.length(), rectTextCenter);
        canvas.drawText(tips, width/2-rectTextCenter.centerX(), height/2-rectTextCenter.centerY(), mTextCenterPaint);

    }

    private  int degree;
    private boolean isRotating;
    private Handler mHandler = new Handler(){};

    public boolean isRotating(){
        return this.isRotating;
    }


    public void start(){
        tips = TIPS_ING;
        isRotating= true;
//        mHandler.post(rotateRunnable);
        startNew();
        if (mOnScanStateChangeListener!=null){
            mOnScanStateChangeListener.onChange(isRotating);
        }
    }
    
    public void startNew(){
    	animator = ValueAnimator.ofFloat(0f,360f);
    	animator.setDuration(2000);
    	animator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float angel = (float) animation.getAnimatedValue();
				matrix.reset();
				matrix.setRotate(angel, getHeight()/2,getHeight()/2);
				invalidate();
			}
		});
    	LinearInterpolator lin = new LinearInterpolator();
    	animator.setInterpolator(lin);
    	animator.setRepeatCount(ValueAnimator.INFINITE);
    	animator.start();
    }
    
    public void stopNew(){
    	if (animator!=null) {
    		animator.cancel();
    		animator = null;
		}
    }
    

    public void stop(){
        tips = TIPS_START;
        isRotating= false;
        invalidate();
//         mHandler.removeCallbacks(rotateRunnable);
        stopNew();
        if (mOnScanStateChangeListener!=null){
            mOnScanStateChangeListener.onChange(isRotating);
        }
    }

    public interface OnScanStateChangeListener{
        void onChange(boolean isRotating);
    }
    private OnScanStateChangeListener mOnScanStateChangeListener;
	private ValueAnimator animator;

    public void setOnScanStateChangeListener(OnScanStateChangeListener mOnScanStateChangeListener){
        this.mOnScanStateChangeListener = mOnScanStateChangeListener;
    }


}
