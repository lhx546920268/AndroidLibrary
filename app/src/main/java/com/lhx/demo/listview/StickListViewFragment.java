package com.lhx.demo.listview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.listView.AbsListViewAdapter;
import com.lhx.library.listView.StickListView;
import com.lhx.library.section.SectionHandler;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.util.ToastUtil;

import java.util.ArrayList;

/**
 * 悬浮固定listView
 */

public class StickListViewFragment extends AppBaseFragment {

    StickListView mStickListView;
    ArrayList<String> strings = new ArrayList<>();

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setContentView(R.layout.stick_list_view_fragment);
        mStickListView = findViewById(R.id.list_view);

        addData(100);

//        View header = new View(mContext);
//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
//        header.setLayoutParams(params);
//        header.setBackgroundColor(Color.GRAY);
//        mStickListView.addHeaderView(header);
//
//        header = new View(mContext);
//        params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
//        header.setLayoutParams(params);
//        header.setBackgroundColor(Color.GREEN);
//        mStickListView.addHeaderView(header);

        mStickListView.setStickListViewHandler(new StickListView.StickListViewHandler() {
            @Override
            public boolean shouldStickAtPosition(int position) {
                return position == 1 || position == 10;
            }

            @Override
            public int getCurrentStickPosition(int firstVisibleItem) {
                return firstVisibleItem < 10 ? 1 : 10;
            }
        });

        mStickListView.setAdapter(new AbsListViewAdapter(mContext) {
            @Override
            public void onLoadMore() {

            }

            @Override
            public boolean loadMoreEnable() {
                return false;
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

                switch (indexInSection){
                    case 1 :
                        convertView.setBackgroundColor(Color.RED);
                        break;
                    case 10 :
                        convertView.setBackgroundColor(Color.CYAN);
                        break;
                    default:
                        convertView.setBackgroundColor(Color.WHITE);
                        break;
                }

                return convertView;
            }

            @Override
            public int getItemViewTypeForIndexPath(int indexInSection, int section, @ItemType int type) {
                return 1;
            }

            @Override
            public int numberOfItemViewTypes() {
                return 1;
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
                ToastUtil.alert(mContext, "第" + indexInSection + "个");
            }
        });
    }

    //添加数据
    private void addData(int size){
        ArrayList<String> list = new ArrayList<>(size);
        for(int i = 0;i < size;i ++){
            list.add("第" + (strings.size() + list.size() + 1) + "个");
        }
        strings.addAll(list);
    }

}
