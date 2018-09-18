package com.lhx.library.popover;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.lhx.library.util.SizeUtil;

//气泡弹窗
public class PopoverLayout extends FrameLayout {

    //箭头方向
    public static final int ARROW_DIRECTION_TOP = 0; //向上
    public static final int ARROW_DIRECTION_LEFT = 0; //向左
    public static final int ARROW_DIRECTION_RIGHT = 0; //向右
    public static final int ARROW_DIRECTION_BOTTOM = 0; //向下

    //气泡颜色
    private @ColorInt int mPopoverColor = Color.WHITE;

    //圆角 px
    private int mCornerRadius;

    //箭头高度 px
    private int mArrowHeight;

    //箭头宽度 px
    private int mArrowWidth;

    //箭头方向
    private int mArrowDirection = ARROW_DIRECTION_TOP;

    //设置箭头偏移量 0为居中
    private int mArrowOffset;

    //画笔
    private Paint mPaint;

    //绘制路径
    private Path mPath;

    //大小
    private int mWidth, mHeight;

    public PopoverLayout(@NonNull Context context) {
        this(context, null);
    }

    public PopoverLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopoverLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true); //设置抗锯齿
        mPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();

        mCornerRadius = SizeUtil.pxFormDip(5, context);
        mArrowWidth = SizeUtil.pxFormDip(15, context);
        mArrowHeight = SizeUtil.pxFormDip(15, context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        configurePath();
    }

    public void setPopoverColor(int popoverColor) {
        if(mPopoverColor != popoverColor){
            mPopoverColor = popoverColor;
            postInvalidate();
        }
    }

    public void setCornerRadius(int cornerRadius) {
        if(mCornerRadius != cornerRadius){
            mCornerRadius = cornerRadius;
            postInvalidate();
        }
    }

    public void setArrowHeight(int arrowHeight) {
        if(mArrowHeight != arrowHeight){
            mArrowHeight = arrowHeight;
            postInvalidate();
        }
    }

    public void setArrowWidth(int arrowWidth) {
        if(arrowWidth != mArrowWidth){
            mArrowWidth = arrowWidth;
            postInvalidate();
        }
    }

    public void setArrowDirection(int arrowDirection) {
        if(mArrowDirection != arrowDirection){
            mArrowDirection = arrowDirection;
            postInvalidate();
        }
    }

    @Override
    public void invalidate() {
        configurePath();
        super.invalidate();
    }

    @Override
    public void postInvalidate() {
        configurePath();
        super.postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mWidth != 0 && mHeight != 0){
            int saveCount = canvas.save();
            mPaint.setColor(mPopoverColor);

            canvas.drawPath(mPath, mPaint);
            canvas.restoreToCount(saveCount);
        }
    }

    //配置路径
    private void configurePath(){

        if(mWidth == 0 || mHeight == 0)
            return;

        int left = 0;
        int top = 0;
        int width = mWidth;
        int height = mHeight;
        mPath.reset();

        switch (mArrowDirection){
            case ARROW_DIRECTION_LEFT : {

                left += mArrowHeight;

                //从左边箭头开始
                mPath.moveTo(0, height / 2);
                mPath.lineTo(mArrowHeight, height / 2 + mArrowWidth / 2);

                //绘制左下角
                mPath.lineTo(left, height - mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, height - mCornerRadius * 2, left +
                            mCornerRadius * 2, height), 180, -90, false);
                }

                //绘制右下角
                mPath.lineTo(width, width - mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(width - mCornerRadius * 2, height - mCornerRadius * 2, width, height), 90,
                            -90, false);
                }

                //绘制右上角
                mPath.lineTo(width, top + mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(width - mCornerRadius * 2, top,
                            width, top + mCornerRadius * 2), 0, -90, false);
                }

                //绘制左上角
                mPath.lineTo(left + mCornerRadius, top);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, top,
                                    left + mCornerRadius * 2, top + mCornerRadius * 2), -90, -90,
                            false);
                }

                //链接箭头
                mPath.lineTo(left, height / 2 - mArrowWidth / 2);
                mPath.lineTo(0, height / 2);
            }
            break;
        }

        mPath.close();
    }
}
