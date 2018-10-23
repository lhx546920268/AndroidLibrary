package com.lhx.library.progress;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

//进度条
public class ProgressBar extends View {

    //画笔
    private Paint mPaint;

    //进度条颜色
    private int mProgressColor = Color.BLUE;

    //当前进度 0 ~ 1.0f
    private float mCurrentProgress;

    //目标进度
    private float mTargetProgress;

    //动画
    private ValueAnimator mAnimator;

    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVisibility(INVISIBLE);
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setColor(mProgressColor);
    }

    public void setProgress(@FloatRange(from = 0, to = 1.0f) float progress) {

        if(mTargetProgress != progress){
            mTargetProgress = progress;
            if(mCurrentProgress > mTargetProgress){
                mCurrentProgress = 0;
            }
            setVisibility(VISIBLE);

            Log.d("progress", mCurrentProgress + ", " + mTargetProgress);
            if(mTargetProgress > mCurrentProgress){
                startAnimate();
            }
        }
    }

    public void setProgressColor(int progressColor) {
        if(mProgressColor != progressColor){
            mProgressColor = progressColor;
            mPaint.setColor(mProgressColor);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.drawRect(0, 0, mCurrentProgress * width, height, mPaint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    //开始动画
    private void startAnimate(){
        stopAnimate();

        mAnimator = ValueAnimator.ofFloat(mCurrentProgress, mTargetProgress);

        if(mTargetProgress >= 0.95f){
            mAnimator.setDuration((int)(450 * (1.0f - mCurrentProgress)));
            mAnimator.setInterpolator(new DecelerateInterpolator());
        }else {
            mAnimator.setDuration((int)(8000 * (1.0f - mCurrentProgress)));
            mAnimator.setInterpolator(new LinearInterpolator());
        }

        mAnimator.addUpdateListener(mAnimatorUpdateListener);
        mAnimator.addListener(mAnimatorListenerAdapter);
        mAnimator.start();
    }

    //停止动画
    private void stopAnimate(){
        if(mAnimator != null && mAnimator.isStarted()){
            mAnimator.start();
        }
    }

    //动画监听
    private AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            if(mTargetProgress >= 1.0f){
                setVisibility(INVISIBLE);
            }
        }
    };

    //动画更新
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float)animation.getAnimatedValue();
            mCurrentProgress = value;
            invalidate();
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimate();
    }
}
