package com.lhx.demo.loadmore;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.activity.AppBaseActivity;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.listView.AbsListViewAdapter;
import com.lhx.library.section.SectionHandler;
import com.lhx.library.util.SizeUtil;

import java.util.ArrayList;

/**
 * listView 加载更多
 */

public class ListViewLoadMoreFragment extends AppBaseFragment implements Handler.Callback{

    ListView listView;
    AbsListViewAdapter adapter;
    ArrayList<String> strings = new ArrayList<>();

    Handler handler = new Handler();

    boolean shouldFail = true;

    Handler mHandler = new Handler(this);

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        mContentView = inflater.inflate(R.layout.list_view_load_more_fragment, null);
        listView = findViewById(R.id.list_view);

        addData(20);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strings.clear();
                adapter.notifyDataSetChanged();
            }
        });

        final View header = new View(mContext);
        header.setBackgroundColor(Color.CYAN);
        header.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                100));
        header.setPadding(100, 100,100, 100);

        adapter = new AbsListViewAdapter(mContext) {

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
            public int numberOfSection() {
                return 1;
            }

            @Override
            public int numberOfItemInSection(int section) {
                return strings.size();
            }

            @Override
            public View getViewForIndexPath(int indexInSection, int section, View convertView, ViewGroup parent) {
                if(convertView == null){
                    TextView textView = new TextView(getContext());
                    textView.setTextSize(18);
                    int padding = SizeUtil.pxFormDip(10, getContext());
                    textView.setPadding(padding, padding, padding, padding);
                    textView.setTextColor(Color.BLACK);

                    convertView = textView;
                }

                TextView textView = (TextView)convertView;
                textView.setText(strings.get(indexInSection));

                return convertView;
            }

            @Override
            public int getItemViewTypeForIndexPath(int indexInSection, int section, int type) {
                return 0;
            }

            @Override
            public int numberItemViewTypes() {
                return 1;
            }

            @Override
            protected int getEmptyViewHeight() {
                return listView.getHeight() - header.getHeight();
            }
        };

        adapter.loadMoreComplete(true);



        listView.addHeaderView(header);


        listView.setAdapter(adapter);
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
}
