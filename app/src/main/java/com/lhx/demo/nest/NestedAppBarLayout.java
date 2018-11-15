package com.lhx.demo.nest;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

//
public class NestedAppBarLayout extends AppBarLayout {

    //
    NestedParentLinearLayout mNestedParentLinearLayout;

    public void setNestedParentLinearLayout(NestedParentLinearLayout nestedParentLinearLayout) {
        mNestedParentLinearLayout = nestedParentLinearLayout;
    }

    public NestedAppBarLayout(Context context) {
        this(context, null);
    }

    public NestedAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        mNestedParentLinearLayout.dispatchTouchEvent(ev);
//
//        Log.d("dispatchTouchEvent", "action = " + ev.getAction());
//        return true;
//    }
//
//    @Override
//    public boolean onInterceptHoverEvent(MotionEvent event) {
//        Log.d("onTouchEvent", "action = " + event.getAction());
//        return super.onInterceptHoverEvent(event);
//    }
}
