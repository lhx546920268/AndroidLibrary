package com.lhx.library.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.library.R;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * recyclerview 相关的fragment
 */

public class RecyclerViewFragment extends AppBaseFragment implements PtrHandler{

    //下拉刷新控件
    protected PtrClassicFrameLayout mRefreshLayout;

    //是否可以下拉刷新
    private boolean mRefreshEnable = true;

    //
    protected RecyclerView mRecyclerView;

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        if(hasRefresh()){
            setContentView(R.layout.recycler_view_refresh_fragment);
            mRefreshLayout = findViewById(R.id.ptr_layout);
        }else {
            setContentView(R.layout.recycler_view_fragment);
        }
        mRecyclerView = findViewById(R.id.recycler_view);

        mRefreshLayout.setPtrHandler(this);
    }

    //是否有下拉刷新功能
    public boolean hasRefresh(){
        return false;
    }

    public boolean isRefreshEnable() {
        return mRefreshEnable;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        this.mRefreshEnable = refreshEnable;
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return mRefreshEnable;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {

    }
}
