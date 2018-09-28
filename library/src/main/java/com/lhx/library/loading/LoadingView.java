package com.lhx.library.loading;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

//加载中菊花
public abstract class LoadingView extends FrameLayout implements LoadingHandler{

    //延迟
    private long mDelay;

    //延迟handler
    private Handler mHandler;
    private Runnable mDelayRunnable;

    //回调
    private LoadingViewHandler mLoadingViewHandler;

    public LoadingView(@NonNull Context context) {
        this(context, null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDelay(long delayMills) {
        mDelay = delayMills;
    }

    public void setLoadingViewHandler(LoadingViewHandler loadingViewHandler) {
        mLoadingViewHandler = loadingViewHandler;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final View contentView = getContentView();
        if(mDelay > 0){
            contentView.setVisibility(INVISIBLE);
            if(mHandler == null){
                mHandler = new Handler();
                mDelayRunnable = new Runnable() {
                    @Override
                    public void run() {
                        contentView.setVisibility(VISIBLE);
                        if(mLoadingViewHandler != null){
                            mLoadingViewHandler.onShowAfterDelay();
                        }
                    }
                };
            }
            mHandler.postDelayed(mDelayRunnable, mDelay);
        }else {
            contentView.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public abstract @NonNull View getContentView();

    //视图回调
    public interface LoadingViewHandler{

        //视图已延迟显示
        void onShowAfterDelay();
    }
}
