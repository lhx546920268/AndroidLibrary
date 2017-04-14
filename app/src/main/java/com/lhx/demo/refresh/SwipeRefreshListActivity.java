package com.lhx.demo.refresh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.viewHoler.ViewHolder;

import java.util.ArrayList;

/**
 * Created by luohaixiong on 17/4/13.
 */

public class SwipeRefreshListActivity extends AppCompatActivity {

    private ArrayList<String> strings = new ArrayList<>();

    Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addItems(20);
        View view = View.inflate(getBaseContext(), R.layout.swipe_refresh_activity, null);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        ListView listView = (ListView)view.findViewById(R.id.list_view);
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return strings.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = View.inflate(getBaseContext(), R.layout.main_list_item, null);
                }

                TextView textView = ViewHolder.get(convertView, R.id.text);
                textView.setText(strings.get(position));

                return convertView;
            }
        };
        listView.setAdapter(adapter);
        setContentView(view);
    }

    //添加信息
    private void addItems(int size){
        for(int i = 0;i < size;i ++){
            strings.add(Math.random() * 10000 + "");
        }
    }
}
