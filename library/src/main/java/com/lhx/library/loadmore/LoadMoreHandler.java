package com.lhx.library.loadmore;

/**
 * 加载更多回调
 */

public interface LoadMoreHandler {

    //触发加载更多
    void onLoadMore();

    //加载更多完成
    void loadMoreComplete(boolean hasMore);

    //加载更多失败了
    void loadMoreFail();

    //是否可以加载更多
    boolean loadMoreEnable();
}
