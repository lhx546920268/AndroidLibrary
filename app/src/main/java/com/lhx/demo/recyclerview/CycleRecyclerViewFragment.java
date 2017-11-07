package com.lhx.demo.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.fragment.RecyclerViewFragment;
import com.lhx.library.recyclerView.RecyclerViewAdapter;
import com.lhx.library.timer.CountDownTimer;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.util.ViewUtil;
import com.lhx.library.viewHoler.RecyclerViewHolder;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

/**
 *
 */

public class CycleRecyclerViewFragment extends AppBaseFragment {

    Adapter mAdapter;

    RecyclerView mRecyclerView;

    int numberOfItems = 5;

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setContentView(R.layout.cycle_recycler_view_fragment);
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new Adapter(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

//        ViewUtil.setViewSize(mRecyclerView, SizeUtil.dipFromPx(SizeUtil.getWindowWidth(mContext), mContext), 100);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mRecyclerView.setNestedScrollingEnabled(false);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int position = linearLayoutManager.findLastVisibleItemPosition();

//                    Log.d("Tag", "position = " + position + ",p = " + p);
                    if(position == 0){
                        mRecyclerView.scrollToPosition(numberOfItems);
                    }else if(position > numberOfItems) {
                        mRecyclerView.scrollToPosition(1);
                    }else {
                        int p = linearLayoutManager.findFirstVisibleItemPosition();
                        if(p != position){



                            View view = linearLayoutManager.findViewByPosition(position);
                            View v = linearLayoutManager.findViewByPosition(p);
                            if(v != null && view != null){

                                int visiabe = ViewUtil.getViewVisiableHeight(view, mRecyclerView.getHeight());
                                int vis = ViewUtil.getViewVisiableHeight(v, mRecyclerView.getHeight());
                                mRecyclerView.smoothScrollToPosition(visiabe > vis ? position : p);
                            }
                        }
                    }
                }
            }
        });
        mRecyclerView.scrollToPosition(1);

        final CountDownTimer countDownTimer = new CountDownTimer(CountDownTimer.COUNT_DOWN_UNLIMITED, 2000) {
            @Override
            public void onFinish() {

            }

            @Override
            public void onTick(long millisLeft) {
                int position = linearLayoutManager.findFirstVisibleItemPosition();
                position ++;
                mRecyclerView.smoothScrollToPosition(position);
            }
        };


        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

                countDownTimer.start();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                countDownTimer.stop();
            }
        });
    }

    private class ScrollLinearLayoutManager extends LinearLayoutManager{

        public ScrollLinearLayoutManager(Context context) {
            super(context);
        }

        public ScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public ScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }

    private class Adapter extends RecyclerViewAdapter{

        public Adapter(@NonNull RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public RecyclerViewHolder onCreateViewHolderForViewType(int viewType, ViewGroup parent) {
            return new RecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_demo, parent, false));
        }

        @Override
        public int numberOfItemInSection(int section) {
            return numberOfItems + 2;
        }

        @Override
        public void onBindItemViewHolderForIndexPath(RecyclerViewHolder viewHolder, int indexInSection, int section) {

            int index = getRealIndex(indexInSection);

            switch (index){
                case 0 :
                 {

                        viewHolder.itemView.setBackgroundColor(Color.RED);
                }
                break;
                case 1 :
                    viewHolder.itemView.setBackgroundColor(Color.YELLOW);
                    break;
                case 2 :
                    viewHolder.itemView.setBackgroundColor(Color.GREEN);
                    break;
                case 3 :
                    viewHolder.itemView.setBackgroundColor(Color.CYAN);
                    break;
                case 4 :
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                    break;
            }

            TextView textView = viewHolder.getView(R.id.title);
            textView.setText("第" + index + "个");
        }

        private int getRealIndex(int index){
            if(index <= 0){
                return 4;
            }else if(index > 5){
                return 0;
            }
            index --;
            return index;
        }

        @Override
        public void onItemClick(int indexInSection, int section) {

            notifyDataSetChanged();
            mRecyclerView.scrollToPosition(1);
        }
    }
}
