package com.lhx.library.media.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.util.ColorUtil;
import com.lhx.library.util.SizeUtil;

/**
 * 媒体 checkbox
 */

public class MediaCheckBox extends FrameLayout implements Checkable, View.OnClickListener{

    //圈圈背景
    CornerBorderDrawable mDrawable;

    //文字
    TextView mTextView;

    //是否选中
    boolean mChecked;

    //选中颜色
    private @ColorInt int mCheckedBackgroundColor;

    //正常颜色
    private @ColorInt int mNormalBackgroundColor;

    //回调
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public MediaCheckBox(Context context) {
        this(context, null);
    }

    public MediaCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTextView = new TextView(context);
        mTextView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextSize(13);
        mTextView.setTextColor(ContextCompat.getColor(context, R.color.theme_tint_color));
        addView(mTextView);

        mDrawable = new CornerBorderDrawable();
        mDrawable.setShouldAbsoluteCircle(true);
        mDrawable.setBorderWidth(SizeUtil.pxFormDip(1.5f, context));
        mDrawable.setBorderColor(Color.WHITE);
        mDrawable.attachView(mTextView);


        mCheckedBackgroundColor = ContextCompat.getColor(context, R.color.theme_color);
        mNormalBackgroundColor = ColorUtil.whitePercentColor(0, 0.2f);

        changeBackground();
        changeContent();

        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mChecked = !mChecked;
        if(mOnCheckedChangeListener != null){
            mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
        }
        changeContent();
        changeBackground();
    }

    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if(mChecked != checked){
            mChecked = checked;
            changeBackground();
        }
        changeContent();
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    private void changeContent(){

        if(mTextView != null){
            if(mChecked){
                if(mOnCheckedChangeListener != null){
                    mTextView.setText(mOnCheckedChangeListener.getCheckedText(this));
                }
            }else {
                mTextView.setText("");
            }
        }
    }

    //改变背景
    private void changeBackground(){
        if(mDrawable != null){

            mDrawable.setBackgroundColor(mChecked ? mCheckedBackgroundColor : mNormalBackgroundColor);
        }
    }

    public interface OnCheckedChangeListener{

        //checked 状态改变
        void onCheckedChanged(MediaCheckBox checkBox, boolean isChecked);

        //获取选中的文字
        String getCheckedText(MediaCheckBox checkBox);
    }
}
