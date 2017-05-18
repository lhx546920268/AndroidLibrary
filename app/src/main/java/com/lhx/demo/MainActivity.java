package com.lhx.demo;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lhx.demo.dialog.DialogFragment;
import com.lhx.demo.drawable.CornerDrawableFragment;
import com.lhx.demo.http.HttpFragment;
import com.lhx.demo.refresh.RefreshActivity;
import com.lhx.demo.tab.TabLayoutFragment;
import com.lhx.demo.viewPager.CyclePagerActivity;
import com.lhx.library.activity.AppBaseActivity;
import com.lhx.library.util.ColorUtil;
import com.lhx.library.viewHoler.ViewHolder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> infos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infos.add("cycleViewPager(无限轮播viewPager)");
        infos.add("refreshControl(下拉刷新、上拉加载)");
        infos.add("alertController(弹窗 dialog)");
        infos.add("cornerDrawable(圆角 corner)");
        infos.add("TabLayout(横向条形菜单)");
        infos.add("HttpAsyncTask");


        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return infos.size();
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
                textView.setText(infos.get(position));
                textView.setBackgroundColor(ColorUtil.whitePercentColor(0.9f, 1.0f));

                return convertView;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class clazz = null;
                switch (position){
                    case 0 :
                        clazz = CyclePagerActivity.class;
                        break;
                    case 1 :
                        clazz = RefreshActivity.class;
                        break;
                    case 2 :
                        startActivity(AppBaseActivity.openActivityWithFragment(MainActivity.this, DialogFragment.class));
                        break;
                    case 3 :
                        startActivity(AppBaseActivity.openActivityWithFragment(MainActivity.this,
                                CornerDrawableFragment.class));
                        break;
                    case 4 :
                        startActivity(AppBaseActivity.openActivityWithFragment(MainActivity.this, TabLayoutFragment.class));
                        break;
                    case 5 :
                        startActivity(AppBaseActivity.openActivityWithFragment(MainActivity.this, HttpFragment.class));
                        break;
                }

                if(clazz != null){
                    startActivity(new Intent(MainActivity.this, clazz));
                }
            }
        });
    }
}
