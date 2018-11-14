package com.lhx.demo.nest;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.lhx.demo.R;
import com.lhx.library.util.SizeUtil;

public class NestedParentLinearLayout extends LinearLayout{

    int mTopViewHeight;

    public NestedParentLinearLayout(Context context) {
        this(context, null);
    }

    public NestedParentLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedParentLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTopViewHeight = SizeUtil.pxFormDip(265, context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean consume = super.dispatchTouchEvent(ev);

        Log.d("dispatchTouchEvent", "NestedParentLinearLayout = " + consume );
        return consume;
    }


    //    @Override
//    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
//        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
//    }
//
//    @Override
//    public int getNestedScrollAxes() {
//        return ViewCompat.SCROLL_AXIS_VERTICAL;
//    }
//
//    @Override
//    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//
//        boolean scrollEnable = false;
//        if(dy < 0 && getScrollY() > 0 && !target.canScrollVertically(-1)){
//            scrollEnable = true;
//        }else if(dy > 0 && getScrollY() < mTopViewHeight){
//            scrollEnable = true;
//        }
//
//        if(scrollEnable){
//            if(dy < 0 && dy + getScrollY() < 0){
//                dy = - getScrollY();
//            }
//            scrollBy(0, dy);
//            consumed[1] = dy;
//        }
//    }
//
//    @Override
//    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
//        return super.onNestedFling(target, velocityX, velocityY, consumed);
//    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
//        measureChild(findViewById(R.id.top), widthMeasureSpec, heightMeasureSpec);
//        measureChild(findViewById(R.id.list_view), widthMeasureSpec, heightMeasureSpec);
//
//        setMeasuredDimension(widthSize, findViewById(R.id.top).getMeasuredHeight() + heightSize);
//    }
}
