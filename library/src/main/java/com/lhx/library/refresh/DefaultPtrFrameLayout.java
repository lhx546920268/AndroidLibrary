package com.lhx.library.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhx.library.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * 默认下拉刷新控件
 */

public class DefaultPtrFrameLayout extends PtrFrameLayout implements PtrUIHandler {

    //内容视图
    private View mHeader;

    //菊花
    private ProgressBar mProgressBar;

    //文本
    private TextView mTextView;

    public DefaultPtrFrameLayout(Context context) {
        this(context, null);
    }

    public DefaultPtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultPtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mHeader = LayoutInflater.from(context).inflate(R.layout.refresh_control_header, this, false);
        mProgressBar = (ProgressBar)mHeader.findViewById(R.id.progress_bar);
        mTextView = (TextView)mHeader.findViewById(R.id.text_view);
        mProgressBar.setVisibility(View.GONE);

        setHeaderView(mHeader);
        addPtrUIHandler(this);
        setDurationToCloseHeader(2000);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText("下拉刷新");
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setText("加载中...");
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mProgressBar.setVisibility(View.GONE);
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
