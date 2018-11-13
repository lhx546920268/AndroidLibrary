package com.lhx.library.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.refresh.DefaultRefreshHeader;
import com.lhx.library.refresh.RefreshHeader;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.widget.AppBaseContainer;
import com.lhx.library.widget.BackToTopButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * 可翻页的 fragment
 */

public abstract class PageFragment extends AppBaseFragment implements OnRefreshListener,
        RefreshHeader.RefreshOnScrollHandler {


    //<editor-fold desc="变量">

    //当前第几页
    protected int mCurPage;

    //下拉刷新控件
    protected SmartRefreshLayout mRefreshLayout;

    //是否正在下拉刷新
    private boolean mRefreshing;

    //回到顶部按钮
    private BackToTopButton mBackToTopButton;

    //回到顶部按钮图标
    private @DrawableRes int mScrollToTopIconRes;

    //当前用来下拉刷新的视图
    private View mRefreshView;

    //刷新头部
    protected RefreshHeader mRefreshHeader;

    //是否有下拉刷新功能
    public boolean hasRefresh(){
        return false;
    }

    //</editor-fold>


    //<editor-fold desc="刷新">
    //开始下拉刷新，子类重写
    @Override
    public final void onRefresh(@NonNull RefreshLayout refreshLayout) {

        mRefreshHeader.setShouldCloseImmediately(false);
        mRefreshing = true;
        onRefresh();
    }

    //
    public void onRefresh(){

    }

    @Override
    @CallSuper
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mCurPage = App.HttpFirstPage;
        if(shouldDisplayBackToTop()){
            mScrollToTopIconRes = App.BackToTopIcon;
        }
    }

    @Override
    protected void setContentView(View contentView) {
        super.setContentView(contentView);
        initRefreshLayout();
    }

    @Override
    protected void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        initRefreshLayout();
    }

    //设置是否可以刷新
    public void setRefreshEnable(boolean enable){
        if(mRefreshLayout != null){
            mRefreshLayout.setEnableRefresh(enable);
        }
    }

    //初始化刷新控件
    private void initRefreshLayout(){
        if(hasRefresh() && mRefreshLayout == null){

            mRefreshHeader = getRefreshHeader();
            mRefreshHeader.setOnScrollHandler(this);
            mRefreshLayout = findViewById(R.id.smart_layout);
            mRefreshLayout.setHeaderHeight(50);
            mRefreshLayout.setRefreshHeader(mRefreshHeader);
            mRefreshLayout.setOnRefreshListener(this);
            mRefreshLayout.setEnableAutoLoadMore(false);
            mRefreshLayout.setEnableLoadMore(false);
        }
    }

    //获取下拉刷新头部
    protected @NonNull RefreshHeader getRefreshHeader(){
        RefreshHeader refreshHeader;
        if(App.refreshHeaderClass != null){
            try {
                refreshHeader = App.refreshHeaderClass.getConstructor(Context.class).newInstance(mContext);
            }catch (Exception e){
                throw new IllegalStateException("refreshHeaderClass 无法通过context实例化");
            }
        }else {
            refreshHeader = (DefaultRefreshHeader)LayoutInflater.from(mContext).inflate(R.layout
                    .refresh_control_header, null);
        }
        return refreshHeader;
    }

    //停止下拉刷新
    @CallSuper
    public void stopRefresh(){
        stopRefresh(false);
    }

    //停止下拉刷新
    @CallSuper
    public void stopRefresh(boolean closeImmediately){
        if(mRefreshLayout != null && mRefreshing){
            mRefreshing = false;
            mRefreshHeader.setShouldCloseImmediately(closeImmediately);
            mRefreshLayout.finishRefresh();
        }
    }

    //手动刷新
    public void autoRefresh(){
        if(mRefreshLayout != null && !mRefreshing){
            mRefreshLayout.autoRefresh();
        }
    }

    //是否正在刷新
    public boolean isRefreshing(){
        return mRefreshing;
    }

    public void setRefreshView(View refreshView){
        if(mRefreshView != refreshView){
            mRefreshView = refreshView;
        }
    }

    @Override
    public void onScroll(boolean isDragging, float percent, int offset) {

    }

    //</editor-fold>

    //<editor-fold desc="回到顶部">

    //是否需要显示回到顶部按钮
    public boolean shouldDisplayBackToTop(){
        return true;
    }

    public void setScrollToTopIconRes(int scrollToTopIconRes) {
        if(mScrollToTopIconRes != scrollToTopIconRes){
            mScrollToTopIconRes = scrollToTopIconRes;
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

    //</editor-fold>

    //返回自定义的 layout res
    public @LayoutRes int getContentRes(){
        return 0;
    }
}
