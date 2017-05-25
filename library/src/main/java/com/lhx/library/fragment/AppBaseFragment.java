package com.lhx.library.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lhx.library.activity.AppBaseActivity;
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

    ///关联的context
    protected Context mContext;

    public AppBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("AppBaseFragment", "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("AppBaseFragment", "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("AppBaseFragment", "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("AppBaseFragment", "onResume");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        Log.d("AppBaseFragment", "onAttach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AppBaseFragment", "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("AppBaseFragment", "onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("AppBaseFragment", "onDetach");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("AppBaseFragment", "onCreateView");
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
    public <T extends View> T findViewById(int resId){
        return (T)mContentView.findViewById(resId);
    }

    //启动一个带activity的fragment
    public void startActivity(Class fragmentClass){
        startActivity(fragmentClass, null);
    }

    public void startActivity(Class fragmentClass, Bundle bundle){
        Intent intent = AppBaseActivity.getIntentWithFragment(mContext, fragmentClass);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

}
