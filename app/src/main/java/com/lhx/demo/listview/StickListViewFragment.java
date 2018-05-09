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
import com.lhx.library.section.SectionInfo;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.util.ToastUtil;
import com.lhx.library.viewHoler.ViewHolder;
import com.lhx.library.widget.TabLayout;

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

        final AbsListViewAdapter adapter = new AbsListViewAdapter(mContext) {
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
            public View getSectionHeaderForSection(int section, View convertView, ViewGroup parent) {
                if(convertView == null){

                    convertView = LayoutInflater.from(mContext).inflate(R.layout.stick_header, parent, false);
                    TabLayout tabLayout = ViewHolder.get(convertView, R.id.tab_layout);
                    tabLayout.setTabLayoutDataSet(new TabLayout.TabLayoutDataSet() {
                        @Override
                        public int numberOfTabs(TabLayout tabLayout) {
                            return 2;
                        }

                        @Override
                        public void configureTabInfo(TabLayout tabLayout, TabLayout.TabInfo tab, int position) {
                            tab.setTitle("按钮" + position);
                        }
                    });
                    tabLayout.setShouldDisplayTopSeparator(true);
                    tabLayout.setShouldDisplayBottomSeparator(true);
                    tabLayout.setDividerWidth(SizeUtil.pxFormDip(0.5f, mContext));
                    tabLayout.setDividerColor(getColor(R.color.divider_color));
                    tabLayout.setDividerHeight(SizeUtil.pxFormDip(20, mContext));
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(int position, TabLayout tabLayout) {

                            ToastUtil.alert(mContext, "点击" + position);
                        }

                        @Override
                        public void onTabUnselected(int position, TabLayout tabLayout) {

                        }

                        @Override
                        public void onTabReselected(int position, TabLayout tabLayout) {

                        }
                    });
                }

                return convertView;
            }

            @Override
            public int getItemViewTypeForIndexPath(int indexInSection, int section, @ItemType int type) {
                return type;
            }

            @Override
            public int numberOfItemViewTypes() {
                return 2;
            }

            @Override
            public int numberOfSection() {
                return 10;
            }

            @Override
            public int numberOfItemInSection(int section) {
                return strings.size();
            }

            @Override
            public boolean shouldExistSectionHeaderForSection(int section) {
                return true;
            }

            @Override
            public void onItemClick(int indexInSection, int section) {
                ToastUtil.alert(mContext, "第" + indexInSection + "个");
            }
        };

        mStickListView.setStickListViewHandler(new StickListView.StickListViewHandler() {
            @Override
            public boolean shouldStickAtPosition(int position) {
                SectionInfo info = adapter.sectionInfoForPosition(position);
                return info.getHeaderPosition() == position;
            }

            @Override
            public int getCurrentStickPosition(int firstVisibleItem) {
                SectionInfo info = adapter.sectionInfoForPosition(firstVisibleItem);
                return info.getHeaderPosition();
            }
        });

        mStickListView.setAdapter(adapter);
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
