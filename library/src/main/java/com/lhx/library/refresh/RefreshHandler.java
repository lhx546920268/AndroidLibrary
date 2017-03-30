package com.lhx.library.refresh;

/**
 * 下拉刷新和上拉加载回调接口
 */

public interface RefreshHandler {


    ///开始加载更多
    void onLoadMore(RefreshControl refreshControl);

    ///取消加载更多
    void onLoadMoreCancel(RefreshControl refreshControl);

    ///开始刷新
    void onRefresh(RefreshControl refreshControl);

    ///取消刷新
    void onRefreshCancel(RefreshControl refreshControl);
}
