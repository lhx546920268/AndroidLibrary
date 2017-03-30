package com.lhx.demo.refresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.refresh.RefreshControl;
import com.lhx.library.refresh.RefreshHandler;
import com.lhx.library.viewHoler.ViewHolder;

import java.util.ArrayList;

/**
 * listView下拉刷新 上拉加载
 */

public class RefreshListViewActivity extends AppCompatActivity {

    private ArrayList<String> strings = new ArrayList<>();
    private ListView listView;
    private BaseAdapter adapter;
    private RefreshControl refreshControl;
    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshControl = (RefreshControl)View.inflate(getBaseContext(), R.layout.refresh_list_view_activity, null);
        refreshControl.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onLoadMore(final RefreshControl refreshControl) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        addItems(20);
                        refreshControl.loadMoreComplete();
                    }
                }, 2000);
            }

            @Override
            public void onRefresh(final RefreshControl refreshControl) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshControl.refreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMoreCancel(RefreshControl refreshControl) {

            }

            @Override
            public void onRefreshCancel(RefreshControl refreshControl) {

            }
        });

        listView = (ListView)refreshControl.findViewById(R.id.list_view);
        adapter = new BaseAdapter() {
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        setContentView(refreshControl);

        addItems(30);
    }

    //添加信息
    private void addItems(int size){
        for(int i = 0;i < size;i ++){
            strings.add(Math.random() * 10000 + "");
        }

        adapter.notifyDataSetChanged();
    }
}
