package com.lhx.library.recyclerView;

import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 类似俄罗斯方块的布局
 */

public class TetrisLayoutManager extends RecyclerView.LayoutManager {

    static final String TAG = "TetrisLayoutManager";

    //布局状态信息
    LayoutState mLayoutState = new LayoutState();

    //滑动方向帮助类
    OrientationHelper mOrientationHelper = OrientationHelper.createVerticalHelper(this);

    //布局方向
    static final int LAYOUT_TO_TOP = -1; //向上
    static final int LAYOUT_TO_BOTTOM = 1; //向下
    int mLayoutDirection;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        //如果已存在子视图，并且数据源不小于新的数据源
        int childCount = getChildCount();

        mLayoutState.reset();
        if(childCount > 0){
            View child = getChildAt(0);
            int position = getPosition(child);
            if(position < state.getItemCount()){
                mLayoutState.mCurrentPosition = position;
                mLayoutState.mOffset = mOrientationHelper.getDecoratedStart(child);
            }
        }

        mLayoutDirection = LAYOUT_TO_BOTTOM;
        detachAndScrapAttachedViews(recycler);

        fill(recycler, state);

        if(!state.isPreLayout()){
            mOrientationHelper.onLayoutComplete();
        }
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if(getChildCount() == 0 || dy == 0)
            return 0;

        mLayoutDirection = dy > 0 ? LAYOUT_TO_BOTTOM : LAYOUT_TO_TOP;

        int absDy = Math.abs(dy);
        updateLayoutState(absDy);

        int comsume = mLayoutState.mScrollingOffset + fill(recycler, state);
        if(comsume < 0)
            return 0;

        int offset = absDy > comsume ? mLayoutDirection * comsume : dy;
        mOrientationHelper.offsetChildren(-offset);

        return offset;
    }

    //更新布局状态
    private void updateLayoutState(int dy){

        int childCount = getChildCount();
        if(mLayoutDirection == LAYOUT_TO_BOTTOM){
            View child = getChildAt(childCount - 1);
            mLayoutState.mCurrentPosition = getPosition(child) + 1;
            mLayoutState.mOffset = mOrientationHelper.getDecoratedEnd(child);
            mLayoutState.mScrollingOffset = mLayoutState.mOffset - mOrientationHelper.getEndAfterPadding();
            mLayoutState.mAvailable = dy - mLayoutState.mScrollingOffset;

        }else {
            View child = getChildAt(0);
            mLayoutState.mCurrentPosition = getPosition(child) - 1;
            mLayoutState.mOffset = mOrientationHelper.getDecoratedStart(child);
            mLayoutState.mScrollingOffset = mOrientationHelper.getStartAfterPadding() - mLayoutState.mOffset;
            mLayoutState.mAvailable = dy - mLayoutState.mScrollingOffset;
        }

        Log.d(TAG, mLayoutState.mScrollingOffset + "," + mLayoutState.mAvailable + "," + getChildCount());
    }

    //填充item
    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state){
        //布局child
        int offset = mLayoutState.mOffset;

        int totalConsume = 0;
        while (mLayoutState.mAvailable > 0 && mLayoutState.hasMore(state)){
            View view = recycler.getViewForPosition(mLayoutState.mCurrentPosition);
            if(mLayoutDirection == LAYOUT_TO_BOTTOM){
                addView(view);
            }else {
                addView(view, 0);
            }
            measureChildWithMargins(view, 0, 0);

            int left = 0;
            int top = 0;
            int bottom = 0;
            int right = mOrientationHelper.getDecoratedMeasurementInOther(view);
            int height = mOrientationHelper.getDecoratedMeasurement(view);
            if(mLayoutDirection == LAYOUT_TO_BOTTOM){
                top = offset;
                bottom = top + height;
                offset = bottom;
                mLayoutState.mCurrentPosition ++;
            }else {
                bottom = offset;
                top = bottom - height;
                offset = top;
                mLayoutState.mCurrentPosition --;
            }

            layoutDecoratedWithMargins(view, left, top, right, bottom);

            mLayoutState.mAvailable -= height;
            totalConsume += height;

            if(mLayoutState.mScrollingOffset != 0){
                recycleChildren(recycler);
            }
        }

        return totalConsume;
    }

    //回收child
    private void recycleChildren(RecyclerView.Recycler recycler){

        int childCount = getChildCount();
        if(mLayoutDirection == LAYOUT_TO_BOTTOM){
            for(int i = 0;i < childCount;i ++){
                View child = getChildAt(i);
                if(mOrientationHelper.getDecoratedEnd(child) > 0  || mOrientationHelper.getTransformedEndWithDecoration
                        (child) > 0){
                    recycleChildren(recycler, 0, i);
                    return;
                }
            }
        }else {

            int limit = mOrientationHelper.getEndAfterPadding();
            for(int i = childCount - 1;i >= 0;i --){
                View child = getChildAt(i);
                if(mOrientationHelper.getDecoratedStart(child) < limit || mOrientationHelper
                        .getTransformedEndWithDecoration(child) < limit){
                    recycleChildren(recycler, i, childCount);
                    return;
                }
            }
        }
    }

    private void recycleChildren(RecyclerView.Recycler recycler, int startIndex, int endIndex){
        if(startIndex == endIndex)
            return;

        for(int i = endIndex - 1;i >= startIndex;i --){
            removeAndRecycleViewAt(i, recycler);
        }
    }

    //布局状态信息
    private class LayoutState{

        //当前item位置
        int mCurrentPosition = RecyclerView.NO_POSITION;

        //当前空闲区域
        int mAvailable;

        //布局开始位置
        int mOffset;

        //正在滑动的偏移量
        int mScrollingOffset;

        //是否有更多item
        boolean hasMore(RecyclerView.State state){
            return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
        }

        //重置
        void reset(){
            mCurrentPosition = 0;
            mAvailable = mOrientationHelper.getEndAfterPadding() - mOrientationHelper.getStartAfterPadding();
            mOffset = mOrientationHelper.getStartAfterPadding();
            mScrollingOffset = 0;
        }
    }
}
