package com.lhx.library.popover;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import com.lhx.library.R;
import com.lhx.library.util.ViewUtil;

//弹窗容器
public abstract class PopoverContainer extends FrameLayout {

    //气泡
    private PopoverLayout mPopoverLayout;

    //是否自动计算箭头方向
    private boolean mAutoDetectArrowDirection = true;

    //遮罩
    private View mOverlay;

    //点击的视图
    private View mClickedView;
    private int[] mClickedLocations = new int[2];

    //是否需要动画
    private boolean mShouldAnimate;

    public PopoverContainer(@NonNull Context context) {
        this(context, null);
    }

    public PopoverContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopoverContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mOverlay = new View(context);
        mOverlay.setBackgroundColor(ContextCompat.getColor(context, R.color.dialog_background));
        addView(mOverlay, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mPopoverLayout = new PopoverLayout(context);
        addView(mPopoverLayout, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mPopoverLayout.addView(getContentView(mPopoverLayout));
    }

    public void setAutoDetectArrowDirection(boolean autoDetectArrowDirection) {
        mAutoDetectArrowDirection = autoDetectArrowDirection;
    }

    //显示
    public void show(View clickedView, boolean animate){

        mClickedView = clickedView;
        if(mClickedView != null){
            mClickedView.getLocationOnScreen(mClickedLocations);
        }
        mShouldAnimate = animate;
    }

    //移除
    public void dismiss(boolean animate){
        if(animate){
            ViewUtil.removeFromParent(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(mAutoDetectArrowDirection){
            mAutoDetectArrowDirection = false;
            ScaleAnimation animation = new ScaleAnimation(0, 1.0f, 0, 1.0f, mPopoverLayout.getMeasuredWidth() / 2,
                    0);
            animation.setDuration(11250);
            mPopoverLayout.startAnimation(animation);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //获取内容视图
    public abstract @NonNull View getContentView(PopoverLayout parent);
}
