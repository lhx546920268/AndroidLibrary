package com.lhx.library.popover;

import android.content.Context;
import android.content.res.TypedArray;
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

import com.lhx.library.R;
import com.lhx.library.util.SizeUtil;

//气泡弹窗
public class PopoverLayout extends FrameLayout {

    //箭头方向
    public static final int ARROW_DIRECTION_TOP = 0; //向上
    public static final int ARROW_DIRECTION_LEFT = 1; //向左
    public static final int ARROW_DIRECTION_BOTTOM = 2; //向下
    public static final int ARROW_DIRECTION_RIGHT = 3; //向右


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

    //当前padding
    private int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom;

    public PopoverLayout(@NonNull Context context) {
        this(context, null);
    }

    public PopoverLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopoverLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
        mPaddingRight = getPaddingRight();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PopoverLayout);
        mPopoverColor = typedArray.getColor(R.styleable.PopoverLayout_popover_color, Color.WHITE);
        mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.PopoverLayout_corner_radius, SizeUtil.pxFormDip
                (5, context));
        mArrowWidth = typedArray.getDimensionPixelSize(R.styleable.PopoverLayout_arrow_width, SizeUtil.pxFormDip(20,
                context));
        mArrowHeight = typedArray.getDimensionPixelSize(R.styleable.PopoverLayout_arrow_height, SizeUtil.pxFormDip(12,
                context));
        mArrowOffset = typedArray.getDimensionPixelSize(R.styleable.PopoverLayout_arrow_offset, 0);
        mArrowDirection = typedArray.getInt(R.styleable.PopoverLayout_arrow_direction, ARROW_DIRECTION_TOP);
        typedArray.recycle();

        setWillNotDraw(false);

        mPaint = new Paint();
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true); //设置抗锯齿
        mPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();

        adjustPadding();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingTop = top;
        mPaddingRight = right;
        mPaddingBottom = bottom;
        adjustPadding();
    }

    //调整padding
    private void adjustPadding(){
        int left = mPaddingLeft;
        int top = mPaddingTop;
        int right = mPaddingRight;
        int bottom = mPaddingBottom;

        switch (mArrowDirection){
            case ARROW_DIRECTION_TOP : {
                top += mArrowHeight;
            }
            break;
            case ARROW_DIRECTION_LEFT : {
                left += mArrowHeight;
            }
            break;
            case ARROW_DIRECTION_RIGHT : {
                right += mArrowHeight;
            }
            break;
            case ARROW_DIRECTION_BOTTOM : {
                bottom += mArrowHeight;
            }
            break;
        }

        super.setPadding(left, top, right, bottom);
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
            adjustPadding();
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

    public void setArrowOffset(int arrowOffset) {
        if(mArrowOffset != arrowOffset){
            mArrowOffset = arrowOffset;
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
        int right = mWidth;
        int bottom = mHeight;
        int width = mWidth;
        int height = mHeight;
        int arrowX = 0;
        int arrowY = 0;

        mPath.reset();

        switch (mArrowDirection){
            case ARROW_DIRECTION_LEFT : {

                left += mArrowHeight;
                arrowY = mArrowOffset == 0 ? height / 2 : mArrowOffset;

                //从左边箭头开始
                mPath.moveTo(arrowX, arrowY);
                mPath.lineTo(left, arrowY + mArrowWidth / 2);

                //绘制左下角
                mPath.lineTo(left, bottom - mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, bottom - mCornerRadius * 2, left +
                            mCornerRadius * 2, bottom), 180, -90, false);
                }

                //绘制右下角
                mPath.lineTo(right - mCornerRadius, bottom);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(right - mCornerRadius * 2, bottom - mCornerRadius * 2, right, bottom), 90,
                            -90, false);
                }

                //绘制右上角
                mPath.lineTo(width, top + mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(right - mCornerRadius * 2, top,
                            right, top + mCornerRadius * 2), 0, -90, false);
                }

                //绘制左上角
                mPath.lineTo(left + mCornerRadius, top);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, top,
                                    left + mCornerRadius * 2, top + mCornerRadius * 2), -90, -90,
                            false);
                }

                //链接箭头
                mPath.lineTo(left, arrowY - mArrowWidth / 2);
                mPath.lineTo(arrowX, arrowY);
            }
            break;
            case ARROW_DIRECTION_TOP : {

                top += mArrowHeight;
                arrowX = mArrowOffset == 0 ? width / 2 : mArrowOffset;

                //从顶部箭头开始开始
                mPath.moveTo(arrowX, arrowY);
                mPath.lineTo(arrowX - mArrowWidth / 2, top);

                //绘制左上角
                mPath.lineTo(left + mCornerRadius, top);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, top,
                                    left + mCornerRadius * 2, top + mCornerRadius * 2), -90, -90,
                            false);
                }

                //绘制左下角
                mPath.lineTo(left, bottom - mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, bottom - mCornerRadius * 2, left +
                            mCornerRadius * 2, bottom), 180, -90, false);
                }

                //绘制右下角
                mPath.lineTo(right - mCornerRadius, bottom);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(right - mCornerRadius * 2, bottom - mCornerRadius * 2, right, bottom), 90,
                            -90, false);
                }

                //绘制右上角
                mPath.lineTo(width, top + mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(right - mCornerRadius * 2, top,
                            right, top + mCornerRadius * 2), 0, -90, false);
                }

                //链接箭头
                mPath.lineTo(arrowX + mArrowWidth / 2, top);
                mPath.lineTo(arrowX, arrowY);
            }
            break;
            case ARROW_DIRECTION_RIGHT : {

                right -= mArrowHeight;
                arrowX = width;
                arrowY = mArrowOffset == 0 ? height / 2 : mArrowOffset;

                //从右边箭头开始开始
                mPath.moveTo(arrowX, arrowY);
                mPath.lineTo(right, arrowY - mArrowWidth / 2);

                //绘制右上角
                mPath.lineTo(width, top + mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(right - mCornerRadius * 2, top,
                            right, top + mCornerRadius * 2), 0, -90, false);
                }

                //绘制左上角
                mPath.lineTo(left + mCornerRadius, top);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, top,
                                    left + mCornerRadius * 2, top + mCornerRadius * 2), -90, -90,
                            false);
                }

                //绘制左下角
                mPath.lineTo(left, bottom - mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, bottom - mCornerRadius * 2, left +
                            mCornerRadius * 2, bottom), 180, -90, false);
                }

                //绘制右下角
                mPath.lineTo(right, bottom - mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(right - mCornerRadius * 2, bottom - mCornerRadius * 2, right, bottom), 90,
                            -90, false);
                }

                //链接箭头
                mPath.lineTo(right, arrowY + mArrowWidth / 2);
                mPath.lineTo(arrowX, arrowY);
            }
            break;

            case ARROW_DIRECTION_BOTTOM : {

                bottom -= mArrowHeight;
                arrowY = height;
                arrowX = mArrowOffset == 0 ? width / 2 : mArrowOffset;

                //从底部箭头开始开始
                mPath.moveTo(arrowX, arrowY);
                mPath.lineTo(arrowX + mArrowWidth / 2, bottom);

                //绘制右下角
                mPath.lineTo(right - mCornerRadius, bottom);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(right - mCornerRadius * 2, bottom - mCornerRadius * 2, right, bottom), 90,
                            -90, false);
                }

                //绘制右上角
                mPath.lineTo(width, top + mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(right - mCornerRadius * 2, top,
                            right, top + mCornerRadius * 2), 0, -90, false);
                }

                //绘制左上角
                mPath.lineTo(left + mCornerRadius, top);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, top,
                                    left + mCornerRadius * 2, top + mCornerRadius * 2), -90, -90,
                            false);
                }

                //绘制左下角
                mPath.lineTo(left, bottom - mCornerRadius);
                if(mCornerRadius > 0){
                    mPath.arcTo(new RectF(left, bottom - mCornerRadius * 2, left +
                            mCornerRadius * 2, bottom), 180, -90, false);
                }


                //链接箭头
                mPath.lineTo(arrowX - mArrowWidth / 2, bottom);
                mPath.lineTo(arrowX, arrowY);
            }
            break;
        }

        mPath.close();
    }
}
