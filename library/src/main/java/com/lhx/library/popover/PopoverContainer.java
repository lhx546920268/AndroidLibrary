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
public class PopoverContainer extends ViewGroup {

    //当前视图在屏幕中位置
    private int[] mLocations = new int[2];

    //气泡
    private PopoverLayout mPopoverLayout;

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

    //是否要执行动画
    private boolean mShouldExecuteAnimate = true;

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
        addView(mPopoverLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

        super.setPadding(0, 0, 0, 0);
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
            return mPopoverLayout.getArrowOffset();
        }else {
            return mPopoverLayout.getMeasuredWidth() / 2;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //测量子视图大小
        mOverlayView.measure(widthMeasureSpec, heightMeasureSpec);

        LayoutParams params = mPopoverLayout.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                mPaddingLeft + mPaddingRight, params.width);
        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                mPaddingTop + mPaddingBottom, params.height);
        mPopoverLayout.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        int width = 0;
        int height = 0;

        switch (widthMode){
            case MeasureSpec.EXACTLY : {
                width = widthSize;
            }
            break;
            case MeasureSpec.AT_MOST : {
                width = Math.min(widthSize, mPopoverLayout.getMeasuredWidth());
            }
            break;
            case MeasureSpec.UNSPECIFIED : {
                width = mPopoverLayout.getMeasuredWidth();
            }
            break;
            default :
                width = widthSize;
                break;
        }

        switch (heightMode){
            case MeasureSpec.EXACTLY : {
                height = heightSize;
            }
            break;
            case MeasureSpec.AT_MOST : {
                height = Math.min(heightSize, mPopoverLayout.getMeasuredHeight());
            }
            break;
            case MeasureSpec.UNSPECIFIED : {
                height = mPopoverLayout.getMeasuredHeight();
            }
            break;
            default :
                height = heightSize;
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int width = right - left;
        int height = bottom - top;
        mOverlayView.layout(0, 0, width, height);

        int pLeft = mPaddingLeft;
        int pTop = mPaddingTop;
        int pRight = pLeft + mPopoverLayout.getMeasuredWidth();
        int pBottom = pTop + mPopoverLayout.getMeasuredHeight();

        getLocationOnScreen(mLocations);

        if(mClickedView != null){

            int x1 = mClickedLocations[0];
            int y1 = mClickedLocations[1];
            int x2 = mLocations[0];
            int y2 = mLocations[1];

            //中心点位置
            int centerX = x1 - x2 + mClickedView.getWidth() / 2;

            pLeft = mPaddingLeft + centerX - mPopoverLayout.getMeasuredWidth() / 2;
            if(pLeft + mPopoverLayout.getMeasuredWidth() > width){
                pLeft = width - mPopoverLayout.getMeasuredWidth() - mPaddingRight;
            }


            mPopoverLayout.setArrowOffset(centerX - pLeft);
            pRight = pLeft + mPopoverLayout.getMeasuredWidth();

            if(y1 > y2){
                //点击视图在范围内
                if(y1 < y2 + getMeasuredHeight()){

                    if(y1 - y2 + mClickedView.getHeight() + mPopoverLayout.getMeasuredHeight() <
                            getMeasuredHeight()){
                        mPopoverLayout.setArrowDirection(PopoverLayout.ARROW_DIRECTION_TOP);
                        pTop = y1 - y2 + mClickedView.getHeight() + mPaddingTop;
                        pBottom = pTop + mPopoverLayout.getMeasuredHeight();

                    }else {
                        pBottom = y1 - y2 - mPaddingBottom;
                        pTop = pBottom - mPopoverLayout.getMeasuredHeight();
                        mPopoverLayout.setArrowDirection(PopoverLayout.ARROW_DIRECTION_BOTTOM);
                    }
                }else{
                    //点击视图在弹窗底部
                    mPopoverLayout.setArrowDirection(PopoverLayout.ARROW_DIRECTION_BOTTOM);
                    pBottom = getMeasuredHeight() - mPaddingBottom;
                    pTop = pBottom - mPopoverLayout.getMeasuredHeight();
                }

            }else{
                //点击视图在弹窗上面
                mPopoverLayout.setArrowDirection(PopoverLayout.ARROW_DIRECTION_TOP);
                pTop = mPaddingTop;
                pBottom = pTop + mPopoverLayout.getMeasuredHeight();
            }
        }

        mPopoverLayout.layout(pLeft, pTop, pRight, pBottom);
        executeAnimate();

    }

    //执行动画
    private void executeAnimate(){
        if(mShouldExecuteAnimate){
            mShouldExecuteAnimate = false;
            if(mShouldAnimate){
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
    }


    //回调
    public interface PopoverHandler{

        //显示
        void onPopoverShow();

        //消失
        void onPopoverDismiss();
    }
}
