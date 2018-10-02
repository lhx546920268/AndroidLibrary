package com.lhx.library.popover;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import com.lhx.library.R;
import com.lhx.library.util.ViewUtil;
import com.lhx.library.widget.OnSingleClickListener;

//弹窗容器
public class PopoverContainer extends FrameLayout {

    //当前视图在屏幕中位置
    private int[] mLocations = new int[2];

    //气泡
    private PopoverLayout mPopoverLayout;

    //是否自动计算箭头方向
    private boolean mAutoDetectArrowDirection = true;

    //遮罩
    private View mOverlayView;

    //点击的视图
    private View mClickedView;
    private int[] mClickedLocations = new int[2];

    //是否需要动画
    private boolean mShouldAnimate;

    //当前padding
    private int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom;

    //回调
    private PopoverHandler mPopoverHandler;

    public PopoverContainer(@NonNull Context context) {
        this(context, null);
    }

    public PopoverContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopoverContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mOverlayView = new View(context);
        mOverlayView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss(true);
            }
        });
        mOverlayView.setBackgroundColor(ContextCompat.getColor(context, R.color.dialog_background));
        addView(mOverlayView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mPopoverLayout = new PopoverLayout(context);
        addView(mPopoverLayout, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT));
    }

    public PopoverLayout getPopoverLayout() {
        return mPopoverLayout;
    }

    public View getOverlayView() {
        return mOverlayView;
    }

    //设置内容视图
    public void setContentView(@NonNull View view){
        mPopoverLayout.addView(view);
    }

    public void setPopoverHandler(PopoverHandler popoverHandler) {
        mPopoverHandler = popoverHandler;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingRight = right;
        mPaddingTop = top;
        mPaddingBottom = bottom;

        LayoutParams params = (LayoutParams)mPopoverLayout.getLayoutParams();
        params.leftMargin = left;
        params.rightMargin = right;
        params.bottomMargin = bottom;
        params.topMargin = top;

        super.setPadding(0, 0, 0, 0);
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
            ScaleAnimation animation = new ScaleAnimation(1.0f, 0, 1.0f, 0, getCurrentPivotX(), 0);
            animation.setDuration(250);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ViewUtil.removeFromParent(PopoverContainer.this);
                    if(mPopoverHandler != null){
                        mPopoverHandler.onPopoverDismiss();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mPopoverLayout.startAnimation(animation);

            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0);
            alphaAnimation.setDuration(250);
            mOverlayView.startAnimation(alphaAnimation);
        }else {
            ViewUtil.removeFromParent(this);
        }
    }

    //获取动画x
    private float getCurrentPivotX(){
        if(mClickedView != null){

            return mClickedLocations[0] - mLocations[0] + mClickedView.getWidth() / 2 - mPaddingLeft;
        }else {
            return mPopoverLayout.getMeasuredWidth() / 2;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(mAutoDetectArrowDirection){
            mAutoDetectArrowDirection = false;

            getLocationOnScreen(mLocations);
            if(mClickedView != null){
                LayoutParams params = (LayoutParams)mPopoverLayout.getLayoutParams();
                params.leftMargin = mPaddingLeft;
                params.rightMargin = mPaddingRight;

                int x1 = mClickedLocations[0];
                int y1 = mClickedLocations[1];
                int x2 = mLocations[0];
                int y2 = mLocations[1];

                if(y1 > y2){
                    //点击视图在范围内
                    if(y1 < y2 + getMeasuredHeight()){

                        if(y1 - y2 + mClickedView.getHeight() + mPopoverLayout.getMeasuredHeight() <
                                getMeasuredHeight()){
                            mPopoverLayout.setArrowDirection(PopoverLayout.ARROW_DIRECTION_BOTTOM);
                            params.gravity = Gravity.BOTTOM;
                            params.bottomMargin = getMeasuredHeight() - mClickedView.getHeight() - y1 + y2 + mPaddingBottom;
                        }else {
                            mPopoverLayout.setArrowDirection(PopoverLayout.ARROW_DIRECTION_TOP);
                            params.gravity = Gravity.TOP;
                            params.topMargin = y1 - y2 + mClickedView.getHeight() + mPaddingTop;
                        }
                    }else{
                        //点击视图在弹窗底部
                        params.gravity = Gravity.BOTTOM;
                        params.bottomMargin = mPaddingBottom;
                    }

                }else{
                    //点击视图在弹窗上面
                    params.gravity = Gravity.TOP;
                    params.topMargin = mPaddingTop;
                }

                mPopoverLayout.setArrowOffset(x1 - x2 + mClickedView.getWidth() / 2 - mPaddingLeft);
            }
        }

        if(mShouldAnimate){
            mShouldAnimate = false;
            ScaleAnimation animation = new ScaleAnimation(0, 1.0f, 0, 1.0f, getCurrentPivotX(),
                    0);
            animation.setDuration(250);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mPopoverHandler != null){
                        mPopoverHandler.onPopoverShow();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mPopoverLayout.startAnimation(animation);

            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1.0f);
            alphaAnimation.setDuration(250);
            mOverlayView.startAnimation(alphaAnimation);
        }else {
            if(mPopoverHandler != null){
                mPopoverHandler.onPopoverShow();
            }
        }
    }

    //回调
    public interface PopoverHandler{

        //显示
        void onPopoverShow();

        //消失
        void onPopoverDismiss();
    }
}
