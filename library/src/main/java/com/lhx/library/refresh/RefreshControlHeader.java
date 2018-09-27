package com.lhx.library.refresh;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.drawable.LoadingDrawable;
import com.lhx.library.util.SizeUtil;

/**
 * 默认的刷新控件头部
 */

public class RefreshControlHeader implements RefreshUIHandler{

    //内容视图
    private View mContentView;

    //菊花
    private ImageView mImageView;
    private LoadingDrawable mLoadingDrawable;

    //文本
    private TextView mTextView;

    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public RefreshControlHeader() {

    }

    @Override
    public void onPull(RefreshControl refreshControl, int offset) {
        mImageView.setVisibility(View.GONE);
        mLoadingDrawable.stop();
        mTextView.setText("下拉刷新");
    }

    @Override
    public void onReachCriticalPoint(RefreshControl refreshControl) {
        mTextView.setText("松开即可刷新");
        mImageView.setVisibility(View.GONE);
        mLoadingDrawable.stop();
    }

    @Override
    public void onRefresh(RefreshControl refreshControl) {
        mImageView.setVisibility(View.VISIBLE);
        mLoadingDrawable.start();
        mTextView.setText("加载中...");
    }

    @Override
    public void onRefreshFinish(RefreshControl refreshControl, boolean success) {
        mImageView.setVisibility(View.GONE);
        mLoadingDrawable.stop();
        mTextView.setText(success ? "刷新成功" : "刷新失败");
    }

    @Override
    public void onRefreshWillFinish(RefreshControl refreshControl, boolean success, int delay) {
        mImageView.setVisibility(View.GONE);
        mLoadingDrawable.stop();
        mTextView.setText(success ? "刷新成功" : "刷新失败");
    }

    @NonNull
    @Override
    public View getContentView(@NonNull Context context, @RefreshControl.Orientation int orientation) {

        if(mContentView == null){
            mContentView = View.inflate(context, R.layout.refresh_control_header, null);
            mImageView = mContentView.findViewById(R.id.loading);
            mLoadingDrawable = new LoadingDrawable(context);
            mLoadingDrawable.setColor(Color.GRAY);
            mImageView.setImageDrawable(mLoadingDrawable);

            mTextView = mContentView.findViewById(R.id.text_view);
            mImageView.setVisibility(View.GONE);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    SizeUtil.pxFormDip(50, context));
            mContentView.setLayoutParams(layoutParams);
        }
        return mContentView;
    }
}
