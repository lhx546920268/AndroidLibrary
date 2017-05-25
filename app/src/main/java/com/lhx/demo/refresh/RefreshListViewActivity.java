package com.lhx.demo.refresh;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.listView.AbsListViewAdapter;
import com.lhx.library.refresh.RefreshControl;
import com.lhx.library.refresh.RefreshHandler;
import com.lhx.library.viewHoler.ViewHolder;

import java.util.ArrayList;
import java.util.Random;

/**
 * listView下拉刷新 上拉加载
 */

public class RefreshListViewActivity extends AppCompatActivity {

    final static String TAG = "RefreshListViewActivity";
    private ArrayList<ArrayList<String>> strings = new ArrayList<>();
    private ListView listView;
    private BaseAdapter adapter;
    private RefreshControl refreshControl;
    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshControl = (RefreshControl)View.inflate(getBaseContext(), R.layout.refresh_list_view_activity, null);
//        refreshControl.setRefreshEnable(false);
//        refreshControl.setLoadMoreEnable(false);
        refreshControl.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onLoadMore(final RefreshControl refreshControl) {

                Log.d(TAG, "onLoadMore");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addItems(10);
                        adapter.notifyDataSetChanged();
                        refreshControl.loadMoreComplete(strings.size() < 100);
                        refreshControl.setRefreshEnable(true);
                    }
                }, 2000);
            }

            @Override
            public void onRefresh(final RefreshControl refreshControl) {


                Log.d(TAG, "onRefresh");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        strings.clear();
                        addItems(30);
                        adapter.notifyDataSetChanged();
                        refreshControl.refreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMoreCancel(RefreshControl refreshControl) {

                Log.d(TAG, "onLoadMoreCancel");
            }

            @Override
            public void onRefreshCancel(RefreshControl refreshControl) {

                Log.d(TAG, "onRefreshCancel");
            }
        });

        listView = (ListView)refreshControl.findViewById(R.id.list_view);
        adapter = new Adapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                refreshControl.startRefresh();
            }
        });

        setContentView(refreshControl);

        addItems(10);
    }

    //添加信息
    private void addItems(int size){

        Random random = new Random();
        for(int i = 0;i < size;i ++){
            int count = random.nextInt() % 9;
            ArrayList<String> list = new ArrayList<>();
            for(int j = 0;j < count;j ++){
                list.add(Math.random() * 10000 + "");
            }
            strings.add(list);
        }

        adapter.notifyDataSetChanged();
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
                convertView = View.inflate(getBaseContext(), R.layout.main_list_item, null);
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
                convertView = View.inflate(getBaseContext(), R.layout.main_list_item, null);
                convertView.setBackgroundColor(Color.RED);
            }

            TextView textView = ViewHolder.get(convertView, R.id.text);
            textView.setText("第" + section + "个 section header");

            return convertView;
        }

        @Override
        public View getSectionFooterForSection(int section, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = View.inflate(getBaseContext(), R.layout.main_list_item, null);
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
        public int numberItemViewTypes() {
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
}
