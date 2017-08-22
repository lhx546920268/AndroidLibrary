package com.lhx.library.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lhx.library.R;
import com.lhx.library.listView.StickListView;
import com.lhx.library.widget.BackToTopButton;

/**
 * listview 相关的fragment
 */

public class ListViewFragment extends PageFragment {

    //
    protected StickListView mListView;

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

        BackToTopButton backToTopButton = getBackToTopButton();
        if(backToTopButton != null){
            backToTopButton.setAbsListView(mListView);
        }
    }
}
