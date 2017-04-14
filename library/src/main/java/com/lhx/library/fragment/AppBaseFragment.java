package com.lhx.library.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lhx.library.bar.NavigationBar;
import com.lhx.library.util.SizeUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AppBaseFragment extends Fragment {

    ///内容视图
    protected View mContentView;

    ///容器视图
    protected LinearLayout mContainer;

    ///导航栏
    protected NavigationBar mNavigationBar;

    ///关联的activity
    protected FragmentActivity mActivity;


    public AppBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mContainer == null){
            ///创建容器视图
            mContainer = new LinearLayout(getContext());
            mContainer.setBackgroundColor(Color.WHITE);
            mContainer.setOrientation(LinearLayout.VERTICAL);

            ///创建导航栏
            if(showNavigationBar())
            {
                mNavigationBar = new NavigationBar(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                        .MATCH_PARENT,
                        SizeUtil.pxFormDip(45.0f, getContext()));
                mContainer.addView(mNavigationBar,layoutParams);
            }

            initialize(inflater, container, savedInstanceState);
            if(mContentView.getLayoutParams() == null){

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                mContentView.setLayoutParams(layoutParams);
            }

            mContainer.addView(mContentView);
        }

        return mContainer;
    }

    ///子类可重写这个方法设置 contentView
    public abstract void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState);

    ///是否需要显示导航栏 default is 'true'
    public boolean showNavigationBar(){
        return true;
    }

    ///获取内容视图
    public View getContentView(){
        return  mContentView;
    }

    public NavigationBar getNavigationBar() {
        return mNavigationBar;
    }

    public LinearLayout getContainer() {
        return mContainer;
    }

    ///获取子视图
    public View findViewById(int resId){
        return mContentView.findViewById(resId);
    }
}