package com.lhx.library.fragment;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.refresh.DefaultPtrFrameLayout;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.widget.AppBaseContainer;
import com.lhx.library.widget.BackToTopButton;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 可翻页的 fragment
 */

public abstract class PageFragment extends AppBaseFragment implements PtrHandler {

    //当前第几页
    protected int mCurPage;

    //下拉刷新控件
    protected DefaultPtrFrameLayout mRefreshLayout;

    //回到顶部按钮
    private BackToTopButton mBackToTopButton;

    //回到顶部按钮图标
    private static @DrawableRes int mScrollToTopIconRes;

    //当前用来下拉刷新的视图
    private View mRefreshView;

    //是否有下拉刷新功能
    public boolean hasRefresh(){
        return false;
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        if(mRefreshView == null)
            return false;
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRefreshView, header);
    }

    //开始下拉刷新，子类重写
    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {

    }

    @Override
    @CallSuper
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mCurPage = App.HttpFirstPage;
    }

    //停止下拉刷新
    public final void refreshComplete(){

        if(mRefreshLayout != null && mRefreshLayout.isRefreshing()){
            mRefreshLayout.refreshComplete();
        }
    }

    //手动刷新
    public void autoRefresh(){
        if(mRefreshLayout != null && !mRefreshLayout.isRefreshing()){
            mRefreshLayout.autoRefresh();
        }
    }

    //是否正在刷新
    public boolean isRefreshing(){

        if(mRefreshLayout != null){
            return mRefreshLayout.isRefreshing();
        }
        return false;
    }

    public static void setScrollToTopIconRes(int scrollToTopIconRes) {
        if(mScrollToTopIconRes != scrollToTopIconRes){
            mScrollToTopIconRes = scrollToTopIconRes;
        }
    }

    public void setRefreshView(View refreshView){
        if(mRefreshView != refreshView){
            mRefreshView = refreshView;
        }
    }

    public BackToTopButton getBackToTopButton(){

        if(mScrollToTopIconRes == 0)
            return null;

        if(mBackToTopButton == null){
            mBackToTopButton = new BackToTopButton(mContext);
            mBackToTopButton.setImageResource(mScrollToTopIconRes);
            AppBaseContainer.LayoutParams params = new AppBaseContainer.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, SizeUtil.pxFormDip(20, mContext), SizeUtil.pxFormDip(20, mContext));
            params.addRule(AppBaseContainer.ALIGN_PARENT_RIGHT);
            params.addRule(AppBaseContainer.ABOVE, R.id.app_base_fragment_bottom_id);
            params.alignWithParent = true;
            mBackToTopButton.setVisibility(View.GONE);
            mBackToTopButton.setLayoutParams(params);
            getContainer().addView(mBackToTopButton);
        }

        return mBackToTopButton;
    }
}
