package com.lhx.library.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lhx.library.util.SizeUtil;

/**
 * 雷达图
 */

public class RadarView extends View{

    //圆心坐标
    private int mCenterX, mCenterY;

    //数据数量
    private int mCount = 5;

    //雷达线条颜色
    private @ColorInt int mRedarStrokeColor;

    //数据线条颜色
    private @ColorInt int mDataStrokeColor;

    //内部填充颜色
    private @ColorInt int mInnerFillColor;

    //外包填充颜色
    private @ColorInt int mOuterFillColor;

    public RadarView(Context context) {
        this(context, null, 0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRedarStrokeColor = Color.parseColor("#bcbcbc");
        mDataStrokeColor = Color.parseColor("#28cec1");
        mInnerFillColor = Color.parseColor("#a0d3da");
        mOuterFillColor = Color.parseColor("#afe2e9");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        //获取圆心坐标
        mCenterX = w / 2;
        mCenterY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Paint paint = new Paint();
        paint.setStrokeWidth(SizeUtil.pxFormDip(1, getContext()));

        Path path = new Path();
        int radius = Math.min(mCenterX, mCenterY);
        double radian = Math.PI * 2 / 5;
        double startRadin = getStartRadin();

        float x;
        float y;
        for(int i = 0;i < mCount;i ++){

            path.reset();
            for(int j = 0;j < mCount;j ++){
                x = (float)(mCenterX + Math.sin(startRadin + radian * j) * radius);
                y = (float)(mCenterY - Math.cos(startRadin + radian * j) * radius);
                if(j == 0){
                    path.moveTo(x, y);
                }else {
                    path.lineTo(x, y);
                }
            }

            paint.setColor(mRedarStrokeColor);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, paint);
            path.close();
        }
    }

    //获取开始弧度
    private double getStartRadin(){

        return mCount % 2 == 0 ? 0 : Math.PI / 2;
    }

    //雷达信息
    public static class RadarInfo{

        //值
        public int value;

        //标题
        public String title;

        public RadarInfo(int value, String title) {
            this.value = value;
            this.title = title;
        }
    }
}
