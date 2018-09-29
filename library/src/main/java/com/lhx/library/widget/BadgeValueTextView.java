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

    public void setFillColor(int fillColor) {
        if(mFillColor != fillColor){
            mFillColor = fillColor;
            mDrawable.setBackgroundColor(mFillColor);
        }
    }

    public BadgeValueTextView(Context context) {
        this(context, null);
    }

    public BadgeValueTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeValueTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(attrs != null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeValueTextView);
            mFillColor = array.getColor(R.styleable.BadgeValueTextView_fill_color, Color.RED);
            array.recycle();
        }

        mDrawable = new CornerBorderDrawable();
        mDrawable.setBackgroundColor(mFillColor);
        mDrawable.attachView(this);

        setIncludeFontPadding(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mDrawable.setCornerRadius(height / 2);
        setMeasuredDimension(Math.max(width, height), height);
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
