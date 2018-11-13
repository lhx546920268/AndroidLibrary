package com.lhx.demo.nest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.fragment.PageFragment;
import com.lhx.library.listView.AbsListViewAdapter;
import com.lhx.library.recyclerView.RecyclerViewAdapter;
import com.lhx.library.viewHoler.RecyclerViewHolder;
import com.lhx.library.viewHoler.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.Random;

//嵌套滚动
public class NestScrollViewFragment extends PageFragment {

    ListView mListView;
    private ArrayList<ArrayList<String>> strings = new ArrayList<>();

    RecyclerView mRecyclerView;

    //e
    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        super.initialize(inflater, container, saveInstanceState);
        setContentView(R.layout.nest_scroll_view_fragment);

        mListView = findViewById(R.id.list_view);

        addItems(50);
        mListView.setAdapter(new Adapter(mContext));

        mRecyclerView = findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new RecyclerAdater(mRecyclerView));

        LinearLayout linearLayout = findViewById(R.id.container);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)linearLayout.getLayoutParams();
        RefreshLayoutBehavior behavior = (RefreshLayoutBehavior)params.getBehavior();
        behavior.setRefreshLayout(mRefreshLayout);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        behavior.setAppBarLayout(appBarLayout);

        behavior.setRecyclerView(mRecyclerView);
    }

    @Override
    public boolean hasRefresh() {
        return true;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
            }
        }, 2000);
    }

    //添加信息
    private void addItems(int size){

        Random random = new Random();
        for(int i = 0;i < size;i ++){
            int count = 5;
            ArrayList<String> list = new ArrayList<>();
            for(int j = 0;j < count;j ++){
                list.add(Math.random() * 10000 + "");
            }
            strings.add(list);
        }
    }

    private class Adapter extends AbsListViewAdapter {

        public Adapter(@NonNull Context context) {
            super(context);
        }

        @Override
        public int numberOfSection() {
            return strings.size();
        }

        @Override
        public int numberOfItemInSection(int section) {
            ArrayList list = strings.get(section);
            return list.size();
        }

        @Override
        public View getViewForIndexPath(int indexInSection, int section, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = View.inflate(mContext, R.layout.main_list_item, null);
            }

            ArrayList<String> list = strings.get(section);

            TextView textView = ViewHolder.get(convertView, R.id.text);
            textView.setText(list.get(indexInSection));

            return convertView;
        }

        @Override
        public boolean shouldExistSectionHeaderForSection(int section) {
            return true;
        }

        @Override
        public boolean shouldExistSectionFooterForSection(int section) {
            return true;
        }

        @Override
        public View getSectionHeaderForSection(int section, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = View.inflate(mContext, R.layout.main_list_item, null);
                convertView.setBackgroundColor(Color.RED);
            }

            TextView textView = ViewHolder.get(convertView, R.id.text);
            textView.setText("第" + section + "个 section header");

            return convertView;
        }

        @Override
        public View getSectionFooterForSection(int section, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = View.inflate(mContext, R.layout.main_list_item, null);
                convertView.setBackgroundColor(Color.CYAN);
            }

            TextView textView = ViewHolder.get(convertView, R.id.text);
            textView.setText("第" + section + "个 section footer");

            return convertView;
        }

        @Override
        public int getItemViewTypeForIndexPath(int indexInSection, int section, int type) {
            return type;
        }

        @Override
        public int numberOfItemViewTypes() {
            return 3;
        }

        @Override
        public void onLoadMore() {

        }

        @Override
        public boolean loadMoreEnable() {
            return false;
        }
    }

    private class RecyclerAdater extends RecyclerViewAdapter {

        public RecyclerAdater(@NonNull RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public RecyclerViewHolder onCreateViewHolderForViewType(int viewType, ViewGroup parent) {
            return new RecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_demo, parent, false));
        }

        @Override
        public int numberOfItemInSection(int section) {
            return 50 + 2;
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
