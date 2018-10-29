package com.lhx.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.util.StringUtil;

//角标
public class BadgeValueTextView extends AppCompatTextView {

    //背景
    CornerBorderDrawable mDrawable;

    //背景颜色
    private @ColorInt int mFillColor = Color.RED;

    //边框颜色
    private @ColorInt int mStrokeColor = Color.TRANSPARENT;

    //边框
    private int mStrokeWidth;

    public void setFillColor(int fillColor) {
        if(mFillColor != fillColor){
            mFillColor = fillColor;
            mDrawable.setBackgroundColor(mFillColor);
        }
    }

    public void setStrokeWidth(int strokeWidth) {
        if(mStrokeWidth != strokeWidth){
            mStrokeWidth = strokeWidth;
            mDrawable.setBorderWidth(mStrokeWidth);
        }
    }

    public void setStrokeColor(int strokeColor) {
        if(mStrokeColor != strokeColor){
            mStrokeColor = strokeColor;
            mDrawable.setBorderColor(mStrokeColor);
        }
    }

    public BadgeValueTextView(Context context) {
        super(context);
        init(context, null);
    }

    public BadgeValueTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BadgeValueTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //初始化
    private void init(Context context, @Nullable AttributeSet attrs){
        if(attrs != null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeValueTextView);
            mFillColor = array.getColor(R.styleable.BadgeValueTextView_badge_fill_color, Color.RED);
            mStrokeColor = array.getColor(R.styleable.BadgeValueTextView_badge_stroke_color, Color.TRANSPARENT);
            mStrokeWidth = array.getDimensionPixelOffset(R.styleable.BadgeValueTextView_badge_stroke_width, 0);

            array.recycle();
        }

        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);

        mDrawable = new CornerBorderDrawable();
        mDrawable.setBackgroundColor(mFillColor);
        mDrawable.setBorderColor(mStrokeColor);
        mDrawable.setBorderWidth(mStrokeWidth);
        mDrawable.setShouldAbsoluteCircle(true);
        mDrawable.attachView(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if(width < height){
            int size = Math.max(width, height);
            int widthSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            super.onMeasure(widthSpec, heightSpec);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        hideIfNeeded();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        int value = StringUtil.parseInt(text);
        if(value > 99){
            text = "99+";
        }
        super.setText(text, type);
        hideIfNeeded();
    }

    //判断是否需要隐藏
    private void hideIfNeeded(){
        CharSequence text = getText();
        if(TextUtils.isDigitsOnly(text)){
            int value = StringUtil.parseInt(text);
            setVisibility(value > 0 ? VISIBLE : INVISIBLE);
        }else {
            setVisibility(TextUtils.isEmpty(text) ? INVISIBLE : VISIBLE);
        }
    }
}
