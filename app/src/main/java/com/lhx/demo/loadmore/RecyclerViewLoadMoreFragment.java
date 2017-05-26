package com.lhx.demo.loadmore;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.recyclerView.RecyclerViewAdapter;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.viewHoler.RecyclerViewHolder;

import java.util.ArrayList;

/**
 * recyclerView load more
 */

public class RecyclerViewLoadMoreFragment extends AppBaseFragment implements Handler.Callback{

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

    ArrayList<String> strings = new ArrayList<>();

    Handler handler = new Handler();

    boolean shouldFail = true;

    Handler mHandler = new Handler(this);

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mContentView = inflater.inflate(R.layout.recycler_view_load_more_fragment, null);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

//        addData(20);
        adapter = new Adapter(recyclerView);
        adapter.loadMoreComplete(true);

        recyclerView.setAdapter(adapter);
    }

    //添加数据
    private void addData(int size){
        ArrayList<String> list = new ArrayList<>(size);
        for(int i = 0;i < size;i ++){
            list.add("第" + (strings.size() + list.size() + 1) + "个");
        }
        strings.addAll(list);
    }

    //


    @Override
    public boolean handleMessage(Message msg) {
        if(shouldFail){
            shouldFail = false;
            adapter.loadMoreFail();
        }else {

            addData(20);
            adapter.loadMoreComplete(strings.size() < 100);
        }
        return true;
    }

    private class Adapter extends RecyclerViewAdapter{

        public Adapter(@NonNull RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public void onLoadMore() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mHandler.sendEmptyMessage(1);
                }
            }, 2000);
        }

        @Override
        public boolean loadMoreEnable() {
            return true;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolderForViewType(int viewType, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(18);
            int padding = SizeUtil.pxFormDip(10, getContext());
            textView.setPadding(padding, padding, padding, padding);
            textView.setTextColor(Color.BLACK);
            return new RecyclerViewHolder(textView, viewType);
        }

        @Override
        public void onBindItemViewHolderForIndexPath(RecyclerViewHolder viewHolder, int indexInSection, int
                section) {
            TextView textView = (TextView)viewHolder.itemView;
            textView.setText(strings.get(indexInSection));
        }

        @Override
        public int numberOfSection() {
            return 1;
        }

        @Override
        public int numberOfItemInSection(int section) {
            return strings.size();
        }

        @Override
        public void onItemClick(int indexInSection, int section) {
            strings.clear();
            notifyDataSetChanged();
        }
    }
}
