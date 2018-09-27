package com.lhx.library.drawable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.lhx.library.util.ColorUtil;
import com.lhx.library.util.MathUtil;
import com.lhx.library.util.SizeUtil;

///加载菊花
public class LoadingDrawable extends BaseDrawable implements Animatable{

    //是否在执行
    private boolean mRunning;

    //动画对象
    private ValueAnimator mValueAnimator;

    //花瓣数量
    private int mPetalsCount = 13;

    //花瓣大小
    private float mPetalsStrokeWidth;

    //颜色
    private int mColor = Color.WHITE;

    //渐变数量
    private int mFadeCount = 5;

    //当前开始位置
    private int mStart;

    //花瓣路径
    private Path mPetalsPath;

    //内圆比例
    private float mInnerCircleRatio = 3.0f / 5.0f;

    private Context mContext;

    //监听动画改变
    ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float progress = (float)animation.getAnimatedValue();
            mStart = (int)(progress * mPetalsCount);
            invalidateSelf();
        }
    };

    public LoadingDrawable(Context context) {

        super();

        mPaint.setPathEffect(new CornerPathEffect(mPetalsStrokeWidth / 2));
        mPaint.setStyle(Paint.Style.STROKE);

        mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.addUpdateListener(mAnimatorUpdateListener);
        mValueAnimator.setDuration(1000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                mStart = 0;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mStart = 0;
            }
        });

        mPetalsPath = new Path();
    }

    @Override
    public void start() {
        if(isRunning())
            return;

        mRunning = true;
        mValueAnimator.start();
        invalidateSelf();
    }

    @Override
    public void stop() {
        if(!isRunning())
            return;

        mRunning = false;
        mValueAnimator.end();
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    public void setPetalsStrokeWidth(float petalsStrokeWidth) {
        if(mPetalsStrokeWidth != petalsStrokeWidth){
            mPetalsStrokeWidth = petalsStrokeWidth;
            invalidateSelf();
        }
    }

    public void setColor(@ColorInt int color) {
        if(mColor != color){
            mColor = color;
            invalidateSelf();
        }
    }

    public void setInnerCircleRatio(float innerCircleRatio) {
        if(mInnerCircleRatio != innerCircleRatio){
            if(innerCircleRatio > 1.0 || innerCircleRatio < 0){
                innerCircleRatio = 3.0f / 5;
            }
            mInnerCircleRatio = innerCircleRatio;
            invalidateSelf();
        }
    }

    public void setPetalsCount(int petalsCount) {
        if(mPetalsCount != petalsCount){
            if(petalsCount <= 0){
                petalsCount = 13;
            }
            mPetalsCount = petalsCount;
            invalidateSelf();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int saveCount = canvas.save();

        float strokeWidth = mPetalsStrokeWidth;


        //线占一份，间隔占2份
        if(strokeWidth == 0){
            int radius = (int)Math.min(mRectF.width(), mRectF.height());
            float length = (float)(Math.PI * radius * 2);
            strokeWidth = length / ((mPetalsCount - 1) * 6 + mPetalsCount);
        }

        RectF rectF = new RectF(mRectF);
        rectF.inset(strokeWidth, strokeWidth);

        mPaint.setPathEffect(new CornerPathEffect(strokeWidth / 2));
        mPaint.setStrokeWidth(strokeWidth);

        //绘制菊花
        mPetalsPath.reset();
        Point center = new Point((int)rectF.centerX(), (int)rectF.centerY());
        int radius1 = (int)(Math.min(rectF.width(), rectF.height()) / 2);
        int radius2 = (int)(radius1 * mInnerCircleRatio);

        float arc = 360.0f / mPetalsCount;

        int start = mStart;
        int end = start + mFadeCount;
        if(end > mPetalsCount){
            end -= mPetalsCount;
        }


        for(int i = 0; i < mPetalsCount;i ++){
            Point point1 = MathUtil.pointInCircle(center, radius1, arc * i);
            Point point2 = MathUtil.pointInCircle(center, radius2, arc * i);

            mPetalsPath.moveTo(point1.x, point1.y);
            mPetalsPath.lineTo(point2.x, point2.y);

            boolean normal;
            if(start < end){
                normal = i < start || i >= end;
            }else {
                normal = i >= end && i < start;
            }

            if(normal){
                mPaint.setColor(ColorUtil.colorWithAlpha(mColor, 0.5f));
            }else {
                float alpha;
                if(i >= start){
                    alpha = (1.0f - mFadeCount * 0.1f) + 0.1f + (i - start) * 0.1f;
                }else {
                    alpha = 1.0f - i * 0.1f;
                }

//                Log.d("alpha", "start = " + start + ", end = " + end + ", i = " + i + ", alpha = " + alpha);
                mPaint.setColor(ColorUtil.colorWithAlpha(mColor, alpha));
            }

            canvas.drawPath(mPetalsPath, mPaint);

            mPetalsPath.reset();
        }

        canvas.restoreToCount(saveCount);
    }

    @Override
    public BaseDrawable copy() {
        LoadingDrawable drawable = new LoadingDrawable(mContext);

        return drawable;
    }
}
