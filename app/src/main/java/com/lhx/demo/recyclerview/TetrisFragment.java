package com.lhx.demo.recyclerview;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.recyclerView.RecyclerViewAdapter;
import com.lhx.library.recyclerView.TetrisLayoutManager;
import com.lhx.library.viewHoler.RecyclerViewHolder;

/**
 *
 */

public class TetrisFragment extends AppBaseFragment {

    TetrisLayoutManager layoutManager;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    int count = 30;

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setContentView(R.layout.tetris_fragment);



        recyclerView = findViewById(R.id.recycler_view);

        adapter = new RecyclerViewAdapter(recyclerView) {
            @Override
            public void onLoadMore() {

            }

            @Override
            public boolean loadMoreEnable() {
                return false;
            }

            @Override
            public RecyclerViewHolder onCreateViewHolderForViewType(int viewType, ViewGroup parent) {
                return new RecyclerViewHolder(View.inflate(mContext, R.layout.tetris_item, null));
            }

            @Override
            public void onBindItemViewHolderForIndexPath(RecyclerViewHolder viewHolder, int indexInSection, int
                    section) {
                TextView textView = viewHolder.getView(R.id.text);

                textView.setText("tetris " + indexInSection);
            }

            @Override
            public int numberOfSection() {
                return 1;
            }

            @Override
            public int numberOfItemInSection(int section) {
                return count;
            }

            @Override
            public void onItemClick(int indexInSection, int section) {
                count = count == 30 ? 20 : 30;
                adapter.notifyDataSetChanged();
            }
        };

        recyclerView.setAdapter(adapter);

        layoutManager = new TetrisLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
    }


}
