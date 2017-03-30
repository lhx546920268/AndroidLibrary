package com.lhx.library.refresh;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.util.SizeUtil;

/**
 * 默认的刷新控件头部
 */

public class RefreshControlHeader implements RefreshUIHandler{

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

    public RefreshControlHeader() {

    }

    @Override
    public void onPull(RefreshControl refreshControl) {
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText("下拉刷新");
    }

    @Override
    public void onReachCriticalPoint(RefreshControl refreshControl) {
        mTextView.setText("释放刷新");
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh(RefreshControl refreshControl) {
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setText("加载中...");
    }

    @Override
    public void onRefreshFinish(RefreshControl refreshControl, boolean success) {
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText(success ? "刷新成功" : "刷新失败");
    }

    @NonNull
    @Override
    public View getContentView(@NonNull Context context, @RefreshControl.Orientation int orientation) {

        if(mContentView == null){
            mContentView = View.inflate(context, R.layout.refresh_control_header, null);
            mProgressBar = (ProgressBar)mContentView.findViewById(R.id.progress_bar);
            mTextView = (TextView)mContentView.findViewById(R.id.text_view);
            mProgressBar.setVisibility(View.GONE);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    SizeUtil.pxFormDip(50, context));
            mContentView.setLayoutParams(layoutParams);
        }
        return mContentView;
    }
}
