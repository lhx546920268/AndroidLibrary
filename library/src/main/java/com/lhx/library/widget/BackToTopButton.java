package com.lhx.library.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

/**
 * 回到顶部按钮
 */

public class BackToTopButton extends AppCompatImageView implements AbsListView.OnScrollListener {

    //关联的recyclerView
    private  RecyclerView mRecyclerView;
    BackToTopListener mBackToTopListener;

    //关联的listView gridView
    private AbsListView mListView;

    //显示第几个时显示回到顶部按钮
    private int mBackToTopPosition = 20;

    public BackToTopButton(Context context) {
        this(context, null, 0);
    }

    public BackToTopButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackToTopButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecyclerView != null){
                    mRecyclerView.scrollToPosition(0);
                }else if(mListView != null){
                    mListView.setSelection(0);
                }

                setVisibility(GONE);
            }
        });
    }

    public void setBackToTopPosition(int backToTopPosition) {
        mBackToTopPosition = backToTopPosition;
    }

    public int getBackToTopPosition() {
        return mBackToTopPosition;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        if(mRecyclerView != recyclerView){
            if(mRecyclerView != null && mBackToTopListener != null){
                mRecyclerView.removeOnScrollListener(mBackToTopListener);
            }

            mRecyclerView = recyclerView;
            if(mRecyclerView != null){

                if(mBackToTopListener == null){
                    mBackToTopListener = new BackToTopListener();
                }
                mRecyclerView.addOnScrollListener(mBackToTopListener);
            }else {
                mBackToTopListener = null;
            }
        }
    }

    public void setAbsListView(AbsListView listView) {

        if(mListView != listView){
            mListView = listView;
            if(mListView != null){
                mListView.setOnScrollListener(this);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE){
            int position = view.getFirstVisiblePosition();
            if(position >= mBackToTopPosition){
                setVisibility(VISIBLE);
            }else {
                setVisibility(GONE);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    class BackToTopListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            //滑动停下来了
            if(newState == RecyclerView.SCROLL_STATE_IDLE){

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if(layoutManager instanceof LinearLayoutManager){
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager)layoutManager;
                    int position = linearLayoutManager.findFirstVisibleItemPosition();

                    if(position >= mBackToTopPosition){
                        setVisibility(VISIBLE);
                    }else {
                        setVisibility(GONE);
                    }
                }
            }
        }
    }
}
