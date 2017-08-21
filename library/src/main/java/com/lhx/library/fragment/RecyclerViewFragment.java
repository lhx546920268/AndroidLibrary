package com.lhx.library.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lhx.library.R;
import com.lhx.library.recyclerView.StickRecyclerView;
import com.lhx.library.widget.BackToTopButton;

/**
 * recyclerview 相关的fragment
 */

public class RecyclerViewFragment extends PageFragment{

    //
    protected StickRecyclerView mRecyclerView;

    @Override
    @CallSuper
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        super.initialize(inflater, container, saveInstanceState);
        if(hasRefresh()){
            setContentView(R.layout.recycler_view_refresh_fragment);
            mRefreshLayout = findViewById(R.id.ptr_layout);
            mRefreshLayout.setPtrHandler(this);
        }else {
            setContentView(R.layout.recycler_view_fragment);
        }
        mRecyclerView = findViewById(R.id.recycler_view);
        setRefreshView(mRecyclerView);

        BackToTopButton backToTopButton = getBackToTopButton();
        if(backToTopButton != null){
            backToTopButton.setRecyclerView(mRecyclerView);
        }
    }

}
