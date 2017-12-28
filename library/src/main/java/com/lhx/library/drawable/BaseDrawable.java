package com.lhx.library.drawable;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.lhx.library.util.ViewUtil;

/**
 * 基础drawable
 */

public abstract class BaseDrawable extends Drawable {

    //画笔
    protected Paint mPaint;

    //范围
    protected RectF mRectF;

    //绘制路径
    private Path mPath;

    //内在的宽度
    private int mIntrinsicWidth = -1;

    //内在的高度
    private int mIntrinsicHeight = -1;

    public BaseDrawable() {

        mPaint = new Paint();
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true); //设置抗锯齿
        mRectF = new RectF();
    }

    public Path getPath() {
        if(mPath == null){
            mPath = new Path();
        }
        mPath.reset();
        return mPath;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        //必须的，否则会出现不可预料的bug，如键盘弹出后消失，直接getBounds() 返回越来越小的rect
        mRectF.left = left;
        mRectF.top = top;
        mRectF.right = right;
        mRectF.bottom = bottom;
    }

    public void setIntrinsicWidth(int intrinsicWidth) {
        mIntrinsicWidth = intrinsicWidth;
        mRectF.right = intrinsicWidth;
    }

    public void setIntrinsicHeight(int intrinsicHeight) {
        mIntrinsicHeight = intrinsicHeight;
        mRectF.bottom = intrinsicHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }

    public void attachView(View view){
        attachView(view, false);
    }

    //如果drawable用于多个view, 使用这个方法 关联view 将copy一份
    public void attachView(View view, boolean shouldCopy){
        ViewUtil.setBackground(shouldCopy ? copy() : this, view);
    }

    //复制一份
    public abstract BaseDrawable copy();
}
