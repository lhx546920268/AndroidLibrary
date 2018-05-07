package com.lhx.library.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * 圆角边框drawable
 */

public class CornerBorderDrawable extends BaseDrawable {

    private static final String TAG = "CornerBorderDrawable";

    //圆角半径 px 设置这个会设置以下4个相同的数值
    private int mCornerRadius = 0;

    //左上角圆角 px
    private int mLeftTopCornerRadius = 0;

    //右上角圆角 px
    private int mRightTopCornerRadius = 0;

    //左下角圆角 px
    private int mLeftBottomCornerRadius = 0;

    //右下角圆角 px
    private int mRightBottomCornerRadius = 0;

    //是否全圆
    private boolean mShouldAbsoluteCircle = false;

    //边框线条厚度 px
    private int mBorderWidth = 0;

    //边框线条颜色
    private @ColorInt int mBorderColor = Color.TRANSPARENT;

    //背景填充颜色
    private @ColorInt int mBackgroundColor = Color.TRANSPARENT;

    //位图
    private Bitmap mBitmap;

    //位图着色器
    private BitmapShader mBitmapShader;

    //位图画笔
    private Paint mBitmapPaint;


    public static CornerBorderDrawable setDrawable(View targetView, int cornerRadius, @ColorInt int backgroundColor){
        return setDrawable(targetView, cornerRadius, backgroundColor, 0, 0);
    }

    public static CornerBorderDrawable setDrawable(View targetView, int cornerRadius, @ColorInt int backgroundColor,
                                                   int borderWidth, @ColorInt int borderColor){
        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.setCornerRadius(cornerRadius);
        drawable.setBackgroundColor(backgroundColor);
        drawable.setBorderWidth(borderWidth);
        drawable.setBorderColor(borderColor);
        drawable.attatchView(targetView, false);
        return drawable;
    }

    public static CornerBorderDrawable setCircleDrawable(View targetView, @ColorInt int backgroundColor){
        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.setShouldAbsoluteCircle(true);
        drawable.setBackgroundColor(backgroundColor);
        drawable.attatchView(targetView);

        return drawable;
    }

    public static CornerBorderDrawable setCircleDrawable(View targetView, @ColorInt int backgroundColor,
                                                         int borderWidth, @ColorInt int borderColor){
        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.setShouldAbsoluteCircle(true);
        drawable.setBackgroundColor(backgroundColor);
        drawable.setBorderWidth(borderWidth);
        drawable.setBorderColor(borderColor);
        drawable.attatchView(targetView);

        return drawable;
    }

    public CornerBorderDrawable() {
        super();
        initialize();
    }


    /**
     * 通过位图构建
     * @param bitmap 位图
     */
    public CornerBorderDrawable(@NonNull Bitmap bitmap) {
        super();
        mBitmap = bitmap;
        initialize();
    }

    /**
     * 通过资源id构建
     * @param res 资源id
     * @param context context
     */
    public CornerBorderDrawable(int res, @NonNull Context context){
        super();
        if(res != 0){
            mBitmap = BitmapFactory.decodeResource(context.getResources(), res);
        }
        initialize();
    }

    //初始化
    private void initialize(){
        if(mBitmap != null){
            mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapPaint = new Paint();
            mBitmapPaint.setShader(mBitmapShader);
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        drawBorder(canvas);
        drawBackground(canvas);
        drawBitmap(canvas);
    }

    //获取绘制路径
    private Path getPath(RectF bounds){
        Path path = new Path();

        if(mShouldAbsoluteCircle){
            //全圆
            float radius = Math.min(bounds.width(), bounds.height()) / 2.0f;
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

    //是否完全是一个圆，设置这个为true时会忽略 cornerRadius
    public void setShouldAbsoluteCircle(boolean shouldAbsoluteCircle){
        if(shouldAbsoluteCircle != mShouldAbsoluteCircle){
            mShouldAbsoluteCircle = shouldAbsoluteCircle;
            invalidateSelf();
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
        invalidateSelf();
    }

    //设置圆角半径
    public void setCornerRadius(int cornerRadius) {
        if(mCornerRadius != cornerRadius){
            mCornerRadius = cornerRadius;
            mLeftBottomCornerRadius = mCornerRadius;
            mLeftTopCornerRadius = mCornerRadius;
            mRightBottomCornerRadius = mCornerRadius;
            mRightTopCornerRadius = mCornerRadius;
            invalidateSelf();
        }
    }

    public void setLeftTopCornerRadius(int leftTopCornerRadius) {
        if(mLeftTopCornerRadius != leftTopCornerRadius){
            mLeftTopCornerRadius = leftTopCornerRadius;
            invalidateSelf();
        }
    }

    public void setRightTopCornerRadius(int rightTopCornerRadius) {
        if(mRightTopCornerRadius != rightTopCornerRadius){
            mRightTopCornerRadius = rightTopCornerRadius;
            invalidateSelf();
        }
    }

    public void setLeftBottomCornerRadius(int leftBottomCornerRadius) {
        if(mLeftBottomCornerRadius != leftBottomCornerRadius){
            mLeftBottomCornerRadius = leftBottomCornerRadius;
            invalidateSelf();
        }
    }

    public void setRightBottomCornerRadius(int rightBottomCornerRadius) {
        if(mRightBottomCornerRadius != rightBottomCornerRadius){
            mRightBottomCornerRadius = rightBottomCornerRadius;
            invalidateSelf();
        }
    }

    //设置边框线条宽度 px
    public void setBorderWidth(int borderWidth) {
        if(mBorderWidth != borderWidth){
            mBorderWidth = borderWidth;
            invalidateSelf();
        }
    }

    //设置边框线条颜色
    public void setBorderColor(@ColorInt int borderColor) {
        if(mBorderColor != borderColor){
            mBorderColor = borderColor;
            invalidateSelf();
        }
    }

    //设置背景填充颜色
    public void setBackgroundColor(@ColorInt int backgroundColor){
        if(backgroundColor != mBackgroundColor){
            mBackgroundColor = backgroundColor;
            invalidateSelf();
        }
    }

    @Deprecated
    public void attatchView(View view){
        attatchView(view, false);
    }

    //如果drawable用于多个view, 使用这个方法 关联view 将copy一份
    @Deprecated
    public void attatchView(View view, boolean shouldCopy){
       attachView(view, shouldCopy);
    }

    //复制一份
    public CornerBorderDrawable copy(){

        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.mCornerRadius = mCornerRadius;
        drawable.mShouldAbsoluteCircle = mShouldAbsoluteCircle;
        drawable.mBackgroundColor = mBackgroundColor;
        drawable.mBorderColor = mBorderColor;
        drawable.mBorderWidth = mBorderWidth;
        drawable.mBitmap = mBitmap;
        drawable.mBitmapShader = mBitmapShader;
        drawable.mPaint = mPaint;
        drawable.mBitmapPaint = mBitmapPaint;
        drawable.mLeftBottomCornerRadius = mLeftBottomCornerRadius;
        drawable.mRightBottomCornerRadius = mRightBottomCornerRadius;
        drawable.mLeftTopCornerRadius = mLeftTopCornerRadius;
        drawable.mRightTopCornerRadius = mRightTopCornerRadius;

        return drawable;
    }

    //绘制边框
    private void drawBorder(Canvas canvas){

        boolean existBorder = mBorderWidth > 0 && Color.alpha(mBorderColor) != 0;

        //绘制边框
        if(existBorder){

            RectF bounds = new RectF(mRectF);
            bounds.inset(mBorderWidth / 2.0f, mBorderWidth / 2.0f);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mBorderWidth);
            mPaint.setColor(mBorderColor);
            canvas.drawPath(getPath(bounds), mPaint);
        }
    }

    //绘制背景
    private void drawBackground(Canvas canvas){

        //绘制背景
        if(Color.alpha(mBackgroundColor) != 0){

            RectF bounds = new RectF(mRectF);

            boolean existBorder = mBorderWidth > 0 && Color.alpha(mBorderColor) != 0;
            int margin = existBorder ? mBorderWidth : 0;
            bounds.inset(margin, margin);

            mPaint.setColor(mBackgroundColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(getPath(bounds), mPaint);
        }
    }

    //获取位图圆角半径
    private void drawBitmap(Canvas canvas){

        //绘制位图
        if(mBitmapPaint != null){

            RectF bounds = new RectF(mRectF);

            boolean existBorder = mBorderWidth > 0 && Color.alpha(mBorderColor) != 0;
            int margin = existBorder ? mBorderWidth / 2 : 0;
            bounds.inset(margin, margin);

            //如果绘制区域不等于位图大小，设置缩放矩阵
            if(bounds.width() != mBitmap.getWidth() || bounds.height() != mBitmap.getHeight()){
                Matrix matrix = new Matrix();
                matrix.setScale(bounds.width() / mBitmap.getWidth(), bounds.height() / mBitmap.getHeight());
                mBitmapShader.setLocalMatrix(matrix);
            }

            canvas.drawPath(getPath(bounds), mBitmapPaint);
        }
    }

    //以下是父类方法
    @Override
    public int getIntrinsicHeight() {
        if(mBitmap != null){
            return mBitmap.getHeight();
        }else {
            return super.getIntrinsicHeight();
        }
    }

    @Override
    public int getIntrinsicWidth() {
        if(mBitmap != null){
            return mBitmap.getWidth();
        }else {
            return super.getIntrinsicWidth();
        }
    }
}
