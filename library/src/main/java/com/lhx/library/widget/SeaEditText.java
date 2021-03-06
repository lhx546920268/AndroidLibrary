package com.lhx.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lhx.library.R;

/**
 * 带删除按钮输入框
 */

public class SeaEditText extends AppCompatEditText implements TextWatcher, View.OnFocusChangeListener {

    ///清空按钮图标
    private Drawable mClearDrawable;

    ///是否聚焦
    private boolean mHasFocus;

    public SeaEditText(Context context) {
        super(context);
        initialization(null);
    }

    public SeaEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization(attrs);
    }

    public SeaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization(attrs);
    }

    ///初始化
    private void initialization(AttributeSet attrs) {

        if(attrs != null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SeaEditText);
            int res = array.getResourceId(R.styleable.SeaEditText_clear_icon, 0);
            if(res != 0){
                setClearDrawable(ContextCompat.getDrawable(getContext(), res));
            }
            array.recycle();
        }

        addTextChangedListener(this);
        setOnFocusChangeListener(this);
        setHintTextColor(ContextCompat.getColor(getContext(), R.color.hint_color));
    }

    public void setClearDrawable(Drawable clearDrawable) {
        if(clearDrawable != mClearDrawable){
            mClearDrawable = clearDrawable;
            mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        }
    }

    //显示删除按钮
    private void showClearBtn() {
        if(length() > 0 && mHasFocus)
            setCompoundDrawablesWithIntrinsicBounds(null, null, mClearDrawable, null);
        else
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        showClearBtn();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mClearDrawable != null) {
                int x = (int)event.getX();
                int y = (int)event.getY();

                Rect rect = mClearDrawable.getBounds();
                int height = rect.height();
                int distance = (getHeight() - height) / 2;
                boolean isInnerWidth = x > (getWidth() - getTotalPaddingRight()) && x < (getWidth() - getPaddingRight());
                boolean isInnerHeight = y > distance && y <(distance + height);

                if (isInnerWidth && isInnerHeight) {
                    this.setText("");
                }else {
                    performClick();
                }
            }else {
                performClick();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

    }

    /**
     * 当ClearEditText焦点发生变化的时候，
     * 输入长度为零，隐藏删除图标，否则，显示删除图标
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        mHasFocus = hasFocus;
        showClearBtn();
    }
}
