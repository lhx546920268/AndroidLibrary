package com.lhx.library.refresh;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

public abstract class RefreshHeader extends FrameLayout implements com.scwang.smartrefresh.layout.api.RefreshHeader {

    //回调
    private RefreshOnScrollHandler mOnScrollHandler;

    //是否需要立刻关闭刷新
    protected boolean mShouldCloseImmediately;

    public void setOnScrollHandler(RefreshOnScrollHandler onScrollHandler) {
        mOnScrollHandler = onScrollHandler;
    }

    public RefreshHeader(@NonNull Context context) {
        this(context, null);
    }

    public RefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setShouldCloseImmediately(boolean shouldCloseImmediately) {
        mShouldCloseImmediately = shouldCloseImmediately;
    }

    @Override
    @CallSuper
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

        if(mOnScrollHandler != null){
            mOnScrollHandler.onScroll(isDragging, percent, offset);
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    //下拉刷新滑动监听
    public interface RefreshOnScrollHandler{

        //
        void onScroll(boolean isDragging, float percent, int offset);
    }
}
