package com.lhx.demo.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.*;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.recyclerView.RecyclerViewAdapter;
import com.lhx.library.recyclerView.StickRecyclerView;
import com.lhx.library.section.SectionHandler;
import com.lhx.library.section.SectionInfo;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.util.ToastUtil;
import com.lhx.library.viewHoler.RecyclerViewHolder;
import com.lhx.library.widget.OnSingleClickListener;
import com.lhx.library.widget.TabLayout;

/**
 * Created by luohaixiong on 17/8/21.
 */

public class StickRecyclerViewFragment extends AppBaseFragment {

    @Override
    public void initialize(final LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setContentView(R.layout.stick_recycler_view_fragment);


        StickRecyclerView recyclerView = findViewById(R.id.recycler_view);
        final Adapter adapter = new Adapter(recyclerView);

        recyclerView.setStickRecyclerViewHandler(new StickRecyclerView.StickRecyclerViewHandler() {
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


//        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setLayoutManager(ChipsLayoutManager.newBuilder(mContext).build());
        recyclerView.setAdapter(adapter);
    }

    class Adapter extends RecyclerViewAdapter{

        public Adapter(@NonNull RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public RecyclerViewHolder onCreateViewHolderForViewType(int viewType, ViewGroup parent) {
            switch (viewType){
                case ITEM_TYPE_HEADER : {
                    RecyclerViewHolder holder = new RecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout
                            .stick_header, parent, false));
                    TabLayout tabLayout = holder.getView(R.id.tab_layout);
                    tabLayout.setTabLayoutDataSet(new TabLayout.TabLayoutDataSet() {
                        @Override
                        public int numberOfTabs() {
                            return 2;
                        }

                        @Override
                        public void configureTabInfo(TabLayout.TabInfo tab, int position) {
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

                    return holder;
                }

                default : {
                    TextView textView = new TextView(getContext());
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setTextSize(18);
                    int padding = SizeUtil.pxFormDip(10, getContext());
                    textView.setPadding(padding, padding, padding, padding);
                    textView.setTextColor(Color.BLACK);
                    textView.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            TextView textView1 = (TextView)v;
                            ToastUtil.alert(mContext, textView1.getText().toString());
                        }
                    });
                    return new RecyclerViewHolder(textView);
                }
            }
        }

        @Override
        public void onBindItemViewHolderForIndexPath(RecyclerViewHolder viewHolder, int indexInSection, int
                section) {
            TextView textView = (TextView)viewHolder.itemView;
            textView.setText("第" + indexInSection + "个item");
        }

        @Override
        public boolean shouldExistSectionHeaderForSection(int section) {
            return true;
        }

        @Override
        public void onBindHeaderViewHolderForSection(RecyclerViewHolder viewHolder, int section) {

        }

        @Override
        public int getItemViewTypeForIndexPath(int indexInSection, int section, @ItemType int type) {
            return type;
        }

        @Override
        public int numberOfSection() {
            return 10;
        }

        @Override
        public int numberOfItemInSection(int section) {
            return 100;
        }
    }
}
