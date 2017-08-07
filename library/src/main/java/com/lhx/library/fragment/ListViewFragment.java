package com.lhx.library.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lhx.library.R;

/**
 * listview 相关的fragment
 */

public class ListViewFragment extends PageFragment {

    //
    protected ListView mListView;

    @Override
    @CallSuper
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        super.initialize(inflater, container, saveInstanceState);
        if(hasRefresh()){
            setContentView(R.layout.list_view_refresh_fragment);
            mRefreshLayout = findViewById(R.id.ptr_layout);
            mRefreshLayout.setPtrHandler(this);
        }else {
            setContentView(R.layout.list_view_fragment);
        }
        mListView = findViewById(R.id.list_view);
        setRefreshView(mListView);
    }
}
