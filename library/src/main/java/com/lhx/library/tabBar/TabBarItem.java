package com.lhx.library.tabBar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.widget.BadgeValueTextView;

//选项卡按钮
public class TabBarItem extends RelativeLayout {

    //图标
    private ImageView mImageView;

    //标题
    private TextView mTextView;

    //角标
    private BadgeValueTextView mBadgeValueTextView;

    //当前角标
    private String mBadgeValue;

    //是否选中
    private boolean mChecked = false;

    public TabBarItem(@NonNull Context context) {
        this(context, null);
    }

    public TabBarItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBarItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImageView = findViewById(R.id.img);
        mTextView = findViewById(R.id.title);
    }

    //设置角标
    public void setBadgeValue(String value){
        if(mBadgeValue != value){
            mBadgeValue = value;

            initBadge();
            mBadgeValueTextView.setText(mBadgeValue);
        }
    }

    //创建角标
    public void initBadge(){
        if(mBadgeValueTextView == null){
            mBadgeValueTextView = new BadgeValueTextView(getContext());

            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                    .WRAP_CONTENT);
            params.topMargin = SizeUtil.pxFormDip(-3, getContext());
            params.addRule(RIGHT_OF, mImageView.getId());
            params.leftMargin = SizeUtil.pxFormDip(-5, getContext());

            addView(mBadgeValueTextView, params);
        }
    }

    public void setChecked(boolean checked) {
        if(mChecked != checked){
            mChecked = checked;
            mImageView.setSelected(mChecked);
            mTextView.setSelected(mChecked);
        }
    }

    //设置图片和按钮的间隔
    public void setImageTextPadding(int padding){
        LayoutParams params = (LayoutParams)mTextView.getLayoutParams();
        params.topMargin = padding;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public BadgeValueTextView getBadgeValueTextView() {
        initBadge();
        return mBadgeValueTextView;
    }
}
