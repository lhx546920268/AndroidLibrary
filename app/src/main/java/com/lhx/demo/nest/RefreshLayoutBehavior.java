package com.lhx.demo.nest;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public class RefreshLayoutBehavior extends AppBarLayout.ScrollingViewBehavior {

    SmartRefreshLayout mRefreshLayout;

    AppBarLayout mAppBarLayout;

    RecyclerView mRecyclerView;

    float mTouchY;

    public void setRefreshLayout(SmartRefreshLayout refreshLayout) {
        mRefreshLayout = refreshLayout;
    }

    public void setAppBarLayout(AppBarLayout appBarLayout) {
        mAppBarLayout = appBarLayout;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public RefreshLayoutBehavior() {
    }

    public RefreshLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {

//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN : {
//                mTouchY = ev.getY();
//                if(mAppBarLayout.getTop() == 0){
//                    boolean consume = mRefreshLayout.dispatchTouchEvent(ev);
//                    Log.d("onTouchEvent ", "consume = " + consume);
//                    return consume;
//                }
//            }
//            break;
//            case MotionEvent.ACTION_MOVE :
//            case MotionEvent.ACTION_CANCEL :
//            case MotionEvent.ACTION_UP : {
//                if(mAppBarLayout.getTop() == 0){
//                    boolean consume = mRefreshLayout.dispatchTouchEvent(ev);
//                    Log.d("onTouchEvent ", "consume = " + consume + " offset = " + mRecyclerView.getTop());
//                    return consume;
//                }
//                break;
//            }
//        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {

        Log.d("MotionEvent", "action = " + ev.getAction());

//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_DOWN: {
//
//                if(mTouchY == 0){
//                    mTouchY = ev.getY();
//                }
//                Log.d("onInterceptTouchEvent ", "consume = " + (ev.getY() >= mTouchY) + "y = " + mTouchY + ", newY " +
//                        "= " + ev.getY());
//                return ev.getY() >= mTouchY;
//            }
//
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP: {
//                mTouchY = 0;
//            }
//            break;
//        }

        return false;
    }
}
