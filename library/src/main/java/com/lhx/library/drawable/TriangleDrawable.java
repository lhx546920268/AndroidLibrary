package com.lhx.library.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 三角形
 */

public class TriangleDrawable extends BaseDrawable {

    //方向
    public static final int DIRECTION_LEFT = 0; //左边
    public static final int DIRECTION_UP = 1; //向上
    public static final int DIRECTION_RIGHT = 2; //右
    public static final int DIRECTION_DOWN = 3; //向下

    @IntDef({DIRECTION_LEFT, DIRECTION_UP, DIRECTION_RIGHT, DIRECTION_DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction{}

    //方向
    private @Direction int mDirection;

    //填充颜色
    private @ColorInt int mFillColor;

    public static TriangleDrawable setDrawable(View targetView, @Direction int direction, @ColorInt int fillColor){
        TriangleDrawable drawable = new TriangleDrawable(direction, fillColor);
        drawable.attachView(targetView);
        return drawable;
    }

    public TriangleDrawable(int direction, int fillColor) {

        super();
        mDirection = direction;
        mFillColor = fillColor;
    }

    public void setDirection(@Direction int direction) {
        if(mDirection != direction){
            mDirection = direction;
            invalidateSelf();
        }
    }

    public void setFillColor(@ColorInt int fillColor) {
        if(mFillColor != fillColor){
            mFillColor = fillColor;
            invalidateSelf();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        Path path = new Path();
        mPaint.setColor(mFillColor);

        switch (mDirection){
            case DIRECTION_LEFT : {
                path.moveTo(mRectF.right, 0);
                path.lineTo(mRectF.right, mRectF.bottom);
                path.lineTo(0, mRectF.centerY());
                path.lineTo(mRectF.right, 0);
            }
            break;
            case DIRECTION_UP : {
                path.moveTo(0, mRectF.bottom);
                path.lineTo(mRectF.right, mRectF.bottom);
                path.lineTo(mRectF.centerX(), 0);
                path.lineTo(0, mRectF.bottom);
            }
            break;
            case DIRECTION_RIGHT : {
                path.moveTo(0, 0);
                path.lineTo(0, mRectF.bottom);
                path.lineTo(mRectF.right, mRectF.centerY());
                path.lineTo(0, 0);
            }
            break;
            case DIRECTION_DOWN : {
                path.moveTo(0, 0);
                path.lineTo(mRectF.right, 0);
                path.lineTo(mRectF.centerX(), mRectF.bottom);
                path.lineTo(0, 0);
            }
            break;
        }

        canvas.drawPath(path, mPaint);
    }

    @Override
    public BaseDrawable copy() {
        TriangleDrawable drawable = new TriangleDrawable(mDirection, mFillColor);

        return drawable;
    }
}
