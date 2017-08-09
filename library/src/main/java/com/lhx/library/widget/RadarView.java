package com.lhx.library.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.lhx.library.dialog.AlertController;
import com.lhx.library.util.SizeUtil;

import java.util.ArrayList;

/**
 * 雷达图
 */

public class RadarView extends View{

    //圆心坐标
    private int mCenterX, mCenterY;

    //雷达线条颜色
    private @ColorInt int mRedarStrokeColor;

    //数据线条颜色
    private @ColorInt int mDataStrokeColor;

    //内部填充颜色
    private @ColorInt int mInnerFillColor;

    //外包填充颜色
    private @ColorInt int mOuterFillColor;

    //雷达数据
    private ArrayList<RadarInfo> mInfos;

    //最大指数
    private int mMaxValue = 100;

    //y轴坐标便宜
    private int mOffsetY;

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
        mOffsetY = SizeUtil.pxFormDip(10, context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        //获取圆心坐标
        mCenterX = w / 2;
        mCenterY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setInfos(ArrayList<RadarInfo> infos) {
        if(mInfos != infos){
            mInfos = infos;
            postInvalidate();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if(mInfos == null || mInfos.size() == 0)
            return;

        int count = mInfos.size();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(SizeUtil.pxFormDip(1, getContext()));

        Path path = new Path();

        int centerX = mCenterX;
        int centerY = mCenterY + mOffsetY;

        int outRadius = Math.min(mCenterX, mCenterY);
        int radius = outRadius - SizeUtil.pxFormDip(15, getContext());

        double radian = Math.PI * 2 / 5;
        double startRadin = count % 2 == 0 ? 0 : Math.PI / 2;


        //绘制蛛网
        float x;
        float y;
        for(int i = count;i > 0;i --){

            int tmpRaidus = radius / count * i;
            path.reset();
            x = (float)(centerX + Math.cos(startRadin) * tmpRaidus);
            y = (float)(centerY - Math.sin(startRadin) * tmpRaidus);
            path.moveTo(x, y);

            for(int j = 1;j <= count;j ++){
                x = (float)(centerX + Math.cos(startRadin + radian * j) * tmpRaidus);
                y = (float)(centerY - Math.sin(startRadin + radian * j) * tmpRaidus);
                path.lineTo(x, y);
            }

            paint.setColor(mRedarStrokeColor);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, paint);
            if(i % 2 == 0){
                paint.setColor(mInnerFillColor);
            }else {
                paint.setColor(mOuterFillColor);
            }
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, paint);

            path.close();
        }

        path.reset();
        //绘制多边形角上的点和中心连线
        for(int i = 0;i < count;i ++){

            path.moveTo(centerX, centerY);
            x = (float)(centerX + Math.cos(startRadin + radian * i) * radius);
            y = (float)(centerY - Math.sin(startRadin + radian * i) * radius);
            path.lineTo(x, y);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(mRedarStrokeColor);
            canvas.drawPath(path, paint);

        }
        path.close();


        //绘制数据线
        float startX = 0;
        float startY = 0;

        path.reset();
        for(int i = 0;i < count;i ++){


            RadarInfo info = mInfos.get(i);
            int tmpRadius = info.value / mMaxValue * radius;
            x = (float)(centerX + Math.cos(startRadin + radian * i) * tmpRadius);
            y = (float)(centerY - Math.sin(startRadin + radian * i) * tmpRadius);

            if(i == 0){
                startX = x;
                startY = y;
                path.moveTo(startX, startY);
            }else {
                path.lineTo(x, y);
                if(i == count - 1){
                    path.lineTo(startX, startY);
                }
            }
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mDataStrokeColor);
        canvas.drawPath(path, paint);

        path.close();

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(mDataStrokeColor);
        textPaint.setTextSize(17);

        //文字高度
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;

        //绘制文字
        for(int i = 0;i < count;i ++){
            RadarInfo info = mInfos.get(i);

            double tmpRadian = startRadin + radian * i;

            x = (float)(centerX + Math.cos(tmpRadian) * (radius + fontHeight / 2));
            y = (float)(centerY - Math.sin(tmpRadian) * (radius + fontHeight / 2));

            if(tmpRadian > Math.PI * 2){
                tmpRadian -= Math.PI * 2;
            }

            if(tmpRadian >= 0 && tmpRadian <= Math.PI / 2){//第1象限

                if(tmpRadian == Math.PI / 2){
                    float dis = textPaint.measureText(info.title);
                    x -= dis / 2;
                }
                canvas.drawText(info.title, x, y, textPaint);

            }else if(tmpRadian >= 3 * Math.PI / 2 && tmpRadian <= Math.PI * 2){//第4象限
                
                canvas.drawText(info.title, x + 10,y,textPaint);
            }else if(tmpRadian > Math.PI / 2 && tmpRadian <= Math.PI){//第2象限
                
                float dis = textPaint.measureText(info.title);//文本长度
                canvas.drawText(info.title, x - dis, y, textPaint);
            }else if(tmpRadian >= Math.PI && tmpRadian < 3 * Math.PI / 2){//第3象限
                
                float dis = textPaint.measureText(info.title);//文本长度
                canvas.drawText(info.title, x - dis - 10, y, textPaint);
            }
        }


        path.reset();
        //绘制点
        for(int i = 0;i < count;i ++){

            RadarInfo info = mInfos.get(i);
            int tmpRadius = info.value / mMaxValue * radius;
            x = (float)(centerX + Math.cos(startRadin + radian * i) * tmpRadius);
            y = (float)(centerY - Math.sin(startRadin + radian * i) * tmpRadius);
            path.addCircle(x, y, 3, Path.Direction.CCW);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(mDataStrokeColor);
            canvas.drawPath(path, paint);
        }

        path.close();
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
