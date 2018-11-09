package com.lhx.library.refresh;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.drawable.LoadingDrawable;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;


//默认下拉刷新头部
public class DefaultRefreshHeader extends RefreshHeader{

    //菊花
    protected ImageView mImageView;
    private LoadingDrawable mLoadingDrawable;

    //文本
    protected TextView mTextView;


    public DefaultRefreshHeader(@NonNull Context context) {
        super(context);
    }

    public DefaultRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImageView = findViewById(R.id.loading);
        mLoadingDrawable = new LoadingDrawable(getContext());
        mLoadingDrawable.setColor(Color.GRAY);
        mImageView.setImageDrawable(mLoadingDrawable);
        mImageView.setVisibility(GONE);

        mTextView = findViewById(R.id.text_view);
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mImageView.setVisibility(View.GONE);
        mLoadingDrawable.stop();
        mTextView.setText("下拉刷新");
    }


    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        mImageView.setVisibility(View.GONE);
        mLoadingDrawable.stop();
        mTextView.setText("刷新成功");
        return mShouldCloseImmediately ? 0 : 200;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState){
            case None :
            case PullDownToRefresh :
                mImageView.setVisibility(View.GONE);
                mLoadingDrawable.stop();
                mTextView.setText("下拉刷新");
                break;
            case Refreshing :
                mImageView.setVisibility(View.VISIBLE);
                mLoadingDrawable.start();
                mTextView.setText("加载中...");
                break;
            case ReleaseToRefresh :
                mTextView.setText("松开即可刷新");
                break;
        }
    }


}
