package com.lhx.library.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridView;

import com.lhx.library.R;
import com.lhx.library.widget.BackToTopButton;

/**
 * 网格视图
 */

public class GridViewFragment extends PageFragment {

    //网格
    GridView mGridView;

    @Override
    @CallSuper
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        super.initialize(inflater, container, saveInstanceState);
        if(hasRefresh()){
            setContentView(R.layout.grid_view_refresh_fragment);
            mRefreshLayout = findViewById(R.id.ptr_layout);
            mRefreshLayout.setPtrHandler(this);
        }else {
            setContentView(R.layout.grid_view_fragment);
        }
        mGridView = findViewById(R.id.grid_view);
        setRefreshView(mGridView);

        BackToTopButton backToTopButton = getBackToTopButton();
        if(backToTopButton != null){
            backToTopButton.setAbsListView(mGridView);
        }
    }
}
