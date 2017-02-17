package com.lhx.library.refresh;

/**
 * 下拉刷新和上拉加载UI回调接口
 */

public interface RefreshHandler {

    ///开始刷新
    void onBeginRefresh(RefreshControl refreshControl);

    ///下拉刷新被加载更多终止
    void onStopRefresh(RefreshControl refreshControl);

    ///开始加载更多
    void onLoadMore(RefreshControl refreshControl);

    ///加载更多被下拉刷新终止
    void onStopLoadMore(RefreshControl refreshControl);
}
