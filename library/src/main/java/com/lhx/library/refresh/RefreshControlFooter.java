package com.lhx.library.refresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.util.SizeUtil;

/**
 * 刷新控件底部 加载更多
 */

public class RefreshControlFooter implements LoadMoreUIHandler {

    public RefreshControlFooter() {
    }

    //内容视图
    private View mContentView;

    //菊花
    private ProgressBar mProgressBar;

    //文本
    private TextView mTextView;

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public TextView getTextView() {
        return mTextView;
    }

    @Override
    public void onPull(RefreshControl refreshControl) {
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText("加载更多");
    }

    @Override
    public void onReachCriticalPoint(RefreshControl refreshControl) {
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText("加载更多");
    }

    @Override
    public void onLoadMore(RefreshControl refreshControl) {
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setText("加载中...");
    }

    @Override
    public void onLoadMoreFinish(RefreshControl refreshControl, boolean success) {
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText(success ? "加载成功" : "加载失败，点击重新加载");
    }

    @NonNull
    @Override
    public View getContentView(@NonNull Context context, @RefreshControl.Orientation int orientation) {
        if(mContentView == null){
            mContentView = View.inflate(context, R.layout.refresh_control_footer, null);
            mProgressBar = (ProgressBar)mContentView.findViewById(R.id.progress_bar);
            mTextView = (TextView)mContentView.findViewById(R.id.text_view);
            mProgressBar.setVisibility(View.GONE);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    SizeUtil.pxFormDip(35, context));
            mContentView.setLayoutParams(layoutParams);
        }

        return mContentView;
    }
}
