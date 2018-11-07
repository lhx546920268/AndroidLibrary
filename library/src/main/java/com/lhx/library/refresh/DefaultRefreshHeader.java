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

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

//默认下拉刷新头部
public class DefaultRefreshHeader extends FrameLayout implements PtrUIHandler{

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
    public void onUIReset(PtrFrameLayout frame) {
        mImageView.setVisibility(View.GONE);
        mLoadingDrawable.stop();
        mTextView.setText("下拉刷新");
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mImageView.setVisibility(View.VISIBLE);
        mLoadingDrawable.start();
        mTextView.setText("加载中...");
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mImageView.setVisibility(View.GONE);
        mLoadingDrawable.stop();
        mTextView.setText("刷新成功");
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        int offsetToRefresh = ptrIndicator.getOffsetToRefresh();
        int offset = ptrIndicator.getCurrentPosY();

        if(isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE){
            if(offset >= offsetToRefresh){
                mTextView.setText("松开即可刷新");
            }else {
                mTextView.setText("下拉刷新");
            }
        }
    }
}
