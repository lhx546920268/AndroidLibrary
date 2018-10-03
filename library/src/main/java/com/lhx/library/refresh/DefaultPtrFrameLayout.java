package com.lhx.library.refresh;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.drawable.LoadingDrawable;

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
    protected ImageView mImageView;
    private LoadingDrawable mLoadingDrawable;

    //文本
    protected TextView mTextView;

    private PtrFrameLayoutOnScrollHandler mPtrFrameLayoutOnScrollHandler;

    public void setPtrFrameLayoutOnScrollHandler(PtrFrameLayoutOnScrollHandler ptrFrameLayoutOnScrollHandler) {
        mPtrFrameLayoutOnScrollHandler = ptrFrameLayoutOnScrollHandler;
    }

    public DefaultPtrFrameLayout(Context context) {
        this(context, null);
    }

    public DefaultPtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultPtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mHeader = LayoutInflater.from(context).inflate(R.layout.refresh_control_header, this, false);
        mImageView = mHeader.findViewById(R.id.loading);
        mLoadingDrawable = new LoadingDrawable(context);
        mLoadingDrawable.setColor(Color.GRAY);
        mImageView.setImageDrawable(mLoadingDrawable);
        mImageView.setVisibility(GONE);

        mTextView = mHeader.findViewById(R.id.text_view);

        setHeaderView(mHeader);
        addPtrUIHandler(this);
        setDurationToCloseHeader(2000);
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
        if(mPtrFrameLayoutOnScrollHandler != null){
            mPtrFrameLayoutOnScrollHandler.onScroll(ptrIndicator);
        }
    }

    //下拉刷新滑动监听
    public interface PtrFrameLayoutOnScrollHandler{

        //
        void onScroll(PtrIndicator ptrIndicator);
    }
}
