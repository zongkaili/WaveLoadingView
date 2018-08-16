package com.kelly.waveloadingview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * author: zongkaili
 * data: 2018/8/8
 * desc: 波浪样式的圆形loading view
 * 实现原理：贝塞尔曲线
 */
public class WaveCircle extends View {
    private Paint mBgCirclePaint, mWavePaint, mCirclePaint, mTextPaint;
    private Path mPath;
    private int mWidth, mHeight, mRadius = 100;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float mTextSize;
    private int currentProgress;
    private int maxProgress = 100;
    private ValueAnimator mValueAnimator;
    /**
     * 五个数据点
     */
    private PointF dataP1, dataP2, dataP3, dataP4, dataP5;
    /**
     * 四个控制点
     */
    private PointF conP1, conP2, conP3, conP4;
    /**
     * 波浪的振动幅度
     */
    private int waveRipple;
    /**
     * 波浪上升的高度
     */
    private float mWaveHeight;

    public WaveCircle(Context context) {
        super(context, null);
    }

    public WaveCircle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray mTypeArray = context.obtainStyledAttributes(attrs, R.styleable.BazierCircle);
        int circleColor = mTypeArray.getColor(R.styleable.BazierCircle_circle_color, getResources().getColor(R.color.colorAccent));
        int circleBgColor = mTypeArray.getColor(R.styleable.BazierCircle_circle_background_color, getResources().getColor(R.color.colorPrimary));
        int waveColor = mTypeArray.getColor(R.styleable.BazierCircle_progress_color, getResources().getColor(R.color.colorPrimary));
        int textColor = mTypeArray.getColor(R.styleable.BazierCircle_text_color, getResources().getColor(R.color.colorPrimaryDark));
        mTextSize = mTypeArray.getDimension(R.styleable.BazierCircle_text_size, 30);
        mTypeArray.recycle();

        mBgCirclePaint = new Paint();
        mBgCirclePaint.setAntiAlias(true);
        mBgCirclePaint.setColor(circleBgColor);
        mBgCirclePaint.setStyle(Paint.Style.FILL);

        mWavePaint = new Paint();
        mWavePaint.setColor(waveColor);
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = mRadius * 2;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = mRadius * 2;
        }

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        initPointF();

        setMeasuredDimension(mWidth, mHeight);
    }

    private void initPointF() {
        dataP1 = new PointF(-mWidth, mHeight);
        dataP2 = new PointF(-mWidth / 2, mHeight);
        dataP3 = new PointF(0, mHeight);
        dataP4 = new PointF(mWidth / 2, mHeight);
        dataP5 = new PointF(mWidth, mHeight);

        conP1 = new PointF(-mWidth / 4, mHeight);
        conP2 = new PointF(-mWidth * 3 / 4, mHeight);
        conP3 = new PointF(mWidth / 4, mHeight);
        conP4 = new PointF(mWidth * 3 / 4, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCanvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mBgCirclePaint);
        //设置起点
        mPath.reset();
        mPath.moveTo(dataP1.x, dataP1.y);
        mPath.quadTo(conP1.x, conP1.y, dataP2.x, dataP2.y);
        mPath.quadTo(conP2.x, conP2.y, dataP3.x, dataP3.y);
        mPath.quadTo(conP3.x, conP3.y, dataP4.x, dataP4.y);
        mPath.quadTo(conP4.x, conP4.y, dataP5.x, dataP5.y);

        //连接点
        mPath.lineTo(dataP5.x, mHeight);
        mPath.lineTo(-mWidth, mHeight);

        //绘制路径
        mCanvas.drawPath(mPath, mWavePaint);

        //画在画布上
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mCirclePaint);

        if (currentProgress <= 0) {
            waveRipple = 0;
        } else if (currentProgress < maxProgress) {
            waveRipple = 35;
        } else if (currentProgress == maxProgress) {
            waveRipple = 0;
        } else if (currentProgress > maxProgress && mValueAnimator.isRunning()) {
            currentProgress = maxProgress;
            mValueAnimator.cancel();
        }

        //画进度文字
        String text = currentProgress + "%";
        canvas.drawText(text, mWidth / 2 - mTextSize, mHeight / 2 + mTextSize / 2, mTextPaint);
    }

    private void startAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(dataP1.x, 0);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(2 * 1000);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dataP1.x = (float) animation.getAnimatedValue();
                dataP1 = new PointF(dataP1.x, mHeight - mWaveHeight);
                dataP2 = new PointF(dataP1.x + mWidth / 2, mHeight - mWaveHeight);
                dataP3 = new PointF(dataP2.x + mWidth / 2, mHeight - mWaveHeight);
                dataP4 = new PointF(dataP3.x + mWidth / 2, mHeight - mWaveHeight);
                dataP5 = new PointF(dataP4.x + mWidth / 2, mHeight - mWaveHeight);

                conP1 = new PointF(dataP1.x + mWidth / 4, mHeight - mWaveHeight + waveRipple);
                conP2 = new PointF(dataP2.x + mWidth / 4, mHeight - mWaveHeight - waveRipple);
                conP3 = new PointF(dataP3.x + mWidth / 4, mHeight - mWaveHeight + waveRipple);
                conP4 = new PointF(dataP4.x + mWidth / 4, mHeight - mWaveHeight - waveRipple);

                invalidate();
            }
        });
        mValueAnimator.start();
    }

    /**
     * 开始动画
     */
    public void start() {
        if (mValueAnimator == null) {
            initPointF();
            startAnimator();
        }
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        mWaveHeight = (float) currentProgress / maxProgress * mHeight;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

}
