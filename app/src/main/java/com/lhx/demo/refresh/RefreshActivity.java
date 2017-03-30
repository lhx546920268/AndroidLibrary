package com.lhx.demo.refresh;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lhx.demo.MainActivity;
import com.lhx.demo.R;
import com.lhx.demo.viewPager.CyclePagerActivity;
import com.lhx.library.viewHoler.ViewHolder;

import java.util.ArrayList;

/**
 * 刷新控件demo
 */

public class RefreshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ArrayList<String> strings = new ArrayList<>();
        strings.add("recyclerView");
        strings.add("listView");
        strings.add("gridView");

        ListView listView = new ListView(getBaseContext());
        listView.setBackgroundColor(Color.WHITE);

        listView.setAdapter(new BaseAdapter() {
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
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Class clazz = null;
                switch (position){
                    case 0 :

                        break;
                    case 1 :
                        clazz = RefreshListViewActivity.class;
                        break;
                }

                if(clazz != null){
                    startActivity(new Intent(RefreshActivity.this, clazz));
                }
            }
        });

        setContentView(listView);
    }
}
