package com.lhx.library.loading;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.drawable.LoadingDrawable;

//加载中视图
public class LoadingView extends FrameLayout {

    //菊花
    protected ImageView mImageView;
    protected LoadingDrawable mLoadingDrawable;

    //文字
    protected TextView mTextView;

    //容器
    protected FrameLayout mContainer;

    public LoadingView(@NonNull Context context) {
        super(context);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImageView = findViewById(R.id.loading);
        mLoadingDrawable = new LoadingDrawable(getContext());
        mImageView.setImageDrawable(mLoadingDrawable);

        mTextView = findViewById(R.id.text_view);
        mContainer = findViewById(R.id.container);

        CornerBorderDrawable.setDrawable(mContainer, 15, Color.parseColor("#4c4c4c"));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLoadingDrawable.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLoadingDrawable.stop();
    }

    public TextView getTextView() {
        return mTextView;
    }
}
