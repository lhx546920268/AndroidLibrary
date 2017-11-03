package com.lhx.demo.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.demo.R;
import com.lhx.library.fragment.RecyclerViewFragment;
import com.lhx.library.recyclerView.RecyclerViewAdapter;
import com.lhx.library.timer.CountDownTimer;
import com.lhx.library.viewHoler.RecyclerViewHolder;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

/**
 *
 */

public class CycleRecyclerViewFragment extends RecyclerViewFragment {

    Adapter mAdapter;

    int numberOfItems = 5;

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        super.initialize(inflater, container, saveInstanceState);

        mAdapter = new Adapter(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int position = linearLayoutManager.findFirstVisibleItemPosition();
                    if(position == 0){
                        mRecyclerView.scrollToPosition(numberOfItems);
                    }else if(position >= numberOfItems) {
                        mRecyclerView.scrollToPosition(0);
                    }
                }
            }
        });
        mRecyclerView.scrollToPosition(1);

        CountDownTimer countDownTimer = new CountDownTimer(CountDownTimer.COUNT_DOWN_UNLIMITED, 1000) {
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
        countDownTimer.start();
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


            switch (indexInSection){
                case 0 :
                    case 5 : {

                        viewHolder.itemView.setBackgroundColor(Color.RED);
                }
                break;
                case 1 :
                case 6 :
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
            textView.setText("第" + getRealIndex(indexInSection) + "个");
        }

        private int getRealIndex(int index){
            if(index <= 0){
                return 5;
            }else if(index >= 5){
                return 0;
            }
            index --;
            return index;
        }
    }
}
