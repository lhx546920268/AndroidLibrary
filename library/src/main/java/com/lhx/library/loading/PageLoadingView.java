package com.lhx.library.loading;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhx.library.R;

//页面加载视图
public class PageLoadingView extends FrameLayout {

    //菊花
    protected ProgressBar mProgressBar;

    //文字
    protected TextView mTextView;

    public PageLoadingView(@NonNull Context context) {
        super(context);
    }

    public PageLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextView getTextView() {
        return mTextView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mProgressBar = findViewById(R.id.progress_bar);
        mTextView = findViewById(R.id.text_view);
    }
}
