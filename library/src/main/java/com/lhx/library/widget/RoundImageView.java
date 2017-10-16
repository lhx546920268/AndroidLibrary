package com.lhx.library.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Config;

/**
 * 圆角图片
 */

public class RoundImageView extends AppCompatImageView {

    ///圆角半径 px 设置这个会设置以下4个相同的数值
    private int mCornerRadius = 0;

    ///左上角圆角 px
    private int mLeftTopCornerRadius = 0;

    ///右上角圆角 px
    private int mRightTopCornerRadius = 0;

    ///左下角圆角 px
    private int mLeftBottomCornerRadius = 0;

    ///右下角圆角 px
    private int mRightBottomCornerRadius = 0;

    ///是否全圆
    private boolean mShouldAbsoluteCircle = false;

    ///边框线条厚度 px
    private int mBorderWidth = 0;

    ///边框线条颜色
    private @ColorInt
    int mBorderColor = Color.TRANSPARENT;

    ///背景填充颜色
    private @ColorInt int mFillColor = Color.TRANSPARENT;

    ///画笔
    private Paint mPaint;

    ///位图画笔
    private Paint mBitmapPaint;

    ///范围
    private RectF mRectF;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPaint = new Paint();
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true); ///设置抗锯齿
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        mRectF = new RectF(0, 0, w, h);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawBackground(canvas);
        drawBorder(canvas);
        drawBitmap(canvas, getDrawable());
    }

    //获取绘制路径
    private Path getPath(RectF bounds){
        Path path = new Path();

        if(mShouldAbsoluteCircle){
            //全员
            float radius = Math.max(bounds.width(), bounds.height()) / 2.0f;
            path.addCircle(mRectF.width() / 2.0f, mRectF.height() / 2.0f, radius, Path.Direction.CW);
        }else {
            //从左上角开始 绕一圈
            path.moveTo(bounds.left, bounds.top + mLeftTopCornerRadius);
            path.lineTo(bounds.left, bounds.bottom - mLeftBottomCornerRadius);
            if(mLeftBottomCornerRadius > 0){
                path.arcTo(new RectF(bounds.left, bounds.bottom - mLeftBottomCornerRadius * 2, bounds.left +
                        mLeftBottomCornerRadius * 2, bounds.bottom), 180, -90, false);
            }

            path.lineTo(bounds.right - mRightBottomCornerRadius, bounds.bottom);
            if(mRightBottomCornerRadius > 0){
                path.arcTo(new RectF(bounds.right - mRightBottomCornerRadius * 2, bounds.bottom -
                        mRightBottomCornerRadius * 2,
                        bounds.right, bounds.bottom), 90, -90, false);
            }

            path.lineTo(bounds.right, bounds.top + mRightTopCornerRadius);
            if(mRightTopCornerRadius > 0){
                path.arcTo(new RectF(bounds.right - mRightTopCornerRadius * 2, bounds.top,
                        bounds.right, bounds.top + mRightTopCornerRadius * 2), 0, -90, false);
            }

            path.lineTo(bounds.left + mLeftTopCornerRadius, bounds.top);
            if(mLeftTopCornerRadius > 0){
                path.arcTo(new RectF(bounds.left, bounds.top,
                                bounds.left + mLeftTopCornerRadius * 2, bounds.top + mLeftTopCornerRadius * 2), -90, -90,
                        false);
            }
        }

        return path;
    }

    ///是否完全是一个圆，设置这个为true时会忽略 cornerRadius
    public void setShouldAbsoluteCircle(boolean shouldAbsoluteCircle){
        if(shouldAbsoluteCircle != mShouldAbsoluteCircle){
            mShouldAbsoluteCircle = shouldAbsoluteCircle;
            postInvalidate();
        }
    }

    //设置圆角半径
    public void setCornerRadius(int leftTopCornerRadius, int leftBottomCornerRadius, int rightTopCornerRadius, int
            rightBottomCornerRadius){
        mCornerRadius = 0;
        mLeftTopCornerRadius = leftTopCornerRadius;
        mLeftBottomCornerRadius = leftBottomCornerRadius;
        mRightTopCornerRadius = rightTopCornerRadius;
        mRightBottomCornerRadius = rightBottomCornerRadius;
        postInvalidate();
    }

    ///设置圆角半径
    public void setCornerRadius(int cornerRadius) {
        if(mCornerRadius != cornerRadius){
            mCornerRadius = cornerRadius;
            mLeftBottomCornerRadius = mCornerRadius;
            mLeftTopCornerRadius = mCornerRadius;
            mRightBottomCornerRadius = mCornerRadius;
            mRightTopCornerRadius = mCornerRadius;
            postInvalidate();
        }
    }

    public void setLeftTopCornerRadius(int leftTopCornerRadius) {
        if(mLeftTopCornerRadius != leftTopCornerRadius){
            mLeftTopCornerRadius = leftTopCornerRadius;
            postInvalidate();
        }
    }

    public void setRightTopCornerRadius(int rightTopCornerRadius) {
        if(mRightTopCornerRadius != rightTopCornerRadius){
            mRightTopCornerRadius = rightTopCornerRadius;
            postInvalidate();
        }
    }

    public void setLeftBottomCornerRadius(int leftBottomCornerRadius) {
        if(mLeftBottomCornerRadius != leftBottomCornerRadius){
            mLeftBottomCornerRadius = leftBottomCornerRadius;
            postInvalidate();
        }
    }

    public void setRightBottomCornerRadius(int rightBottomCornerRadius) {
        if(mRightBottomCornerRadius != rightBottomCornerRadius){
            mRightBottomCornerRadius = rightBottomCornerRadius;
            postInvalidate();
        }
    }

    ///设置边框线条宽度 px
    public void setBorderWidth(int borderWidth) {
        if(mBorderWidth != borderWidth){
            mBorderWidth = borderWidth;
            postInvalidate();
        }
    }

    ///设置边框线条颜色
    public void setBorderColor(@ColorInt int borderColor) {
        if(mBorderColor != borderColor){
            mBorderColor = borderColor;
            postInvalidate();
        }
    }

    ///设置背景填充颜色
    public void setFillColor(@ColorInt int fillColor){
        if(fillColor != mFillColor){
            mFillColor = fillColor;
            postInvalidate();
        }
    }

    ///绘制边框
    private void drawBorder(Canvas canvas){

        boolean existBorder = mBorderWidth > 0 && Color.alpha(mBorderColor) != 0;

        ///绘制边框
        if(existBorder){

            RectF bounds = new RectF(mRectF);
            bounds.inset(mBorderWidth / 2.0f, mBorderWidth / 2.0f);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mBorderWidth);
            mPaint.setColor(mBorderColor);
            canvas.drawPath(getPath(bounds), mPaint);
        }
    }

    ///绘制背景
    private void drawBackground(Canvas canvas){

        ///绘制背景
        if(Color.alpha(mFillColor) != 0){

            RectF bounds = new RectF(mRectF);

            boolean existBorder = mBorderWidth > 0 && Color.alpha(mBorderColor) != 0;
            int margin = existBorder ? mBorderWidth : 0;
            bounds.inset(margin, margin);

            mPaint.setColor(mFillColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(getPath(bounds), mPaint);
        }
    }

    ///获取位图圆角半径
    private void drawBitmap(Canvas canvas, Drawable drawable){

        ///绘制位图
        if(drawable != null){

            RectF bounds = new RectF(mRectF);

            boolean existBorder = mBorderWidth > 0 && Color.alpha(mBorderColor) != 0;
            int margin = existBorder ? mBorderWidth / 2 : 0;
            bounds.inset(margin, margin);

            Bitmap bitmap = bitmapFromDrawable(drawable);
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapPaint.setShader(bitmapShader);

            ///如果绘制区域不等于位图大小，设置缩放矩阵
            if(bounds.width() != bitmap.getWidth() || bounds.height() != bitmap.getHeight()){
                Matrix matrix = new Matrix();
                matrix.setScale(bounds.width() / bitmap.getWidth(), bounds.height() / bitmap.getHeight());
                bitmapShader.setLocalMatrix(matrix);
            }

            canvas.drawPath(getPath(bounds), mBitmapPaint);
        }
    }

    //把drawable 转成bitmap
    private Bitmap bitmapFromDrawable(Drawable drawable){
        if(drawable == null)
            return null;

        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        return bitmap;
    }
}
