package com.lhx.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lhx.library.R;
import com.lhx.library.util.SizeUtil;

//虚线
public class DashView extends View {

    //虚线形状
    public static final int SHAPE_LINE = 0; //只是一条线
    public static final int SHAPE_RECT = 1; //矩形 可设置圆角

    //方向
    public static final int HORIZONTAL = 0; //水平
    public static final int VERTICAL = 1; //垂直

    //画笔
    private Paint mPaint;

    //路径
    private Path mPath;

    //方向
    private int mOrientation = HORIZONTAL;

    //虚线每段宽度
    private int mDashLength;

    //虚线间隔宽度
    private int mDashInterval;

    //虚线颜色
    private @ColorInt int mDashesColor;

    //线宽度
    private int mStrokeWidth;

    //形状
    private int mShape = SHAPE_LINE;

    //圆角 SHAPE_LINE 无效
    private int mCornerRadius;

    //矩形范围
    private RectF mRectF = new RectF();

    public DashView(Context context) {
        this(context, null);
    }

    public DashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(attrs != null){
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DashView);

            mOrientation = array.getInt(R.styleable.DashView_orientation, HORIZONTAL);
            mDashLength = array.getDimensionPixelOffset(R.styleable.DashView_dash_length,
                    SizeUtil.pxFormDip(10, context));
            mDashInterval = array.getDimensionPixelOffset(R.styleable.DashView_dash_interval,
                    SizeUtil.pxFormDip(5, context));

            mDashesColor = array.getColor(R.styleable.DashView_dash_color, Color.GRAY);
            mStrokeWidth = array.getDimensionPixelOffset(R.styleable.DashView_dash_stroke_width,
                    SizeUtil.pxFormDip(1, context));
            mShape = array.getInt(R.styleable.DashView_shape, SHAPE_LINE);
            mCornerRadius = array.getDimensionPixelOffset(R.styleable.DashView_rect_corner_radius,
                    0);

            array.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        DashPathEffect effect = new DashPathEffect(new float[]{mDashLength, mDashInterval}, 0);
        mPaint.setPathEffect(effect);
        mPath = new Path();
        setWillNotDraw(false);
    }

    public void setOrientation(int orientation) {
        if(mOrientation != orientation){
            mOrientation = orientation;
            postInvalidate();
        }
    }

    public void setDashLength(int dashLength) {
        if(mDashLength != dashLength){
            mDashLength = dashLength;
            mPaint.setPathEffect(new DashPathEffect(new float[]{mDashLength, mDashInterval}, 0));
            postInvalidate();
        }
    }

    public void setDashInterval(int dashInterval) {
        if(mDashInterval != dashInterval){
            mDashLength = dashInterval;
            mPaint.setPathEffect(new DashPathEffect(new float[]{mDashLength, mDashInterval}, 0));
            postInvalidate();
        }
    }

    public void setDashesColor(int dashesColor) {
        if(mDashesColor != dashesColor){
            mDashesColor = dashesColor;
            postInvalidate();
        }
    }

    public void setStrokeWidth(int strokeWidth) {
        if(mStrokeWidth != strokeWidth){
            mStrokeWidth = strokeWidth;
            postInvalidate();
        }
    }

    public void setShape(int shape) {
        if(mShape != shape){
            mShape = shape;
            postInvalidate();
        }
    }

    public void setCornerRadius(int cornerRadius) {
        if(mCornerRadius != cornerRadius){
            mCornerRadius = cornerRadius;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int saveCount = canvas.save();
        mPath.reset();

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mDashesColor);

        switch (mShape){
            case SHAPE_LINE : {
                switch (mOrientation){

                    //画直线
                    case HORIZONTAL : {
                        mPath.moveTo(0, height / 2);
                        mPath.lineTo(width, height / 2);
                    }
                    break;
                    case VERTICAL : {
                        mPath.moveTo(width / 2, 0);
                        mPath.lineTo(width / 2, height);
                    }
                    break;
                }
            }
            break;
            case SHAPE_RECT : {
                //画矩形
                mRectF.left = mStrokeWidth / 2;
                mRectF.top = mStrokeWidth / 2;
                mRectF.right = width - mStrokeWidth;
                mRectF.bottom = height - mStrokeWidth;

                mPath.addRoundRect(mRectF, mCornerRadius, mCornerRadius, Path.Direction.CCW);
            }
            break;
        }

        canvas.drawPath(mPath, mPaint);
        canvas.restoreToCount(saveCount);
    }
}
