package com.lhx.demo.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.widget.TabLayout;

/**
 *  tab layout
 */

public class TabLayoutFragment extends AppBaseFragment {

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mContentView = inflater.inflate(R.layout.tab_layout_fragment, null);

        final String[] datas = new String[]{"环球厨房", "环球创意厨具", "健康专家", "洗护",
                "口腔护", "环球健康食品", "环球盐灯", "品质活物语", "彩电家具", "海尔红条", "手机数码", "笔记本", "外设", "大屏显示器"};

        final String[] datas1 = new String[]{"环球厨房", "环球创意厨具"};

        final String[] datas2 = new String[]{"环球厨房", "环球创意厨具", "健康专家", "洗护",
                "口腔护", "环球健康食品", "环球盐灯", "品质活物语", "彩电家具", "海尔红条", "手机数码", "笔记本", "外设", "大屏显示器"};

        TabLayout tabLayout = findViewById(R.id.tab_layout1);
        tabLayout.setAutomaticallyDetectStyle(true);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, TabLayout tabLayout) {
                Toast.makeText(mActivity, datas[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(int position, TabLayout tabLayout) {
                Toast.makeText(mActivity, datas[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(int position, TabLayout tabLayout) {
                Toast.makeText(mActivity, datas[position], Toast.LENGTH_SHORT).show();
            }
        });
        tabLayout.setTabLayoutDataSet(new TabLayout.TabLayoutDataSet() {
            @Override
            public int numberOfTabs() {
                return datas.length;
            }

            @Override
            public void configureTabInfo(TabLayout.TabInfo tab, int pos) {
                tab.setTitle(datas[pos]);
            }
        });

        TabLayout tabLayout1 = findViewById(R.id.tab_layout2);
        tabLayout1.setAutomaticallyDetectStyle(true);
        tabLayout1.setDividderWidth(1);
        tabLayout1.setShouldDisplayBottomSeparator(true);
        tabLayout1.setTabLayoutDataSet(new TabLayout.TabLayoutDataSet() {
            @Override
            public int numberOfTabs() {
                return datas1.length;
            }

            @Override
            public void configureTabInfo(TabLayout.TabInfo tab, int pos) {
                tab.setTitle(datas1[pos]);
            }
        });

        TabLayout tabLayout2 = findViewById(R.id.tab_layout3);
        tabLayout2.setAutomaticallyDetectStyle(true);
        tabLayout2.setTabLayoutDataSet(new TabLayout.TabLayoutDataSet() {
            @Override
            public int numberOfTabs() {
                return datas2.length;
            }

            @Override
            public void configureTabInfo(TabLayout.TabInfo tab, int pos) {
                tab.setTitle(datas2[pos]);
            }
        });
    }
}
