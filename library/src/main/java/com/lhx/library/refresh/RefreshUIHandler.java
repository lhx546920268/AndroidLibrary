package com.lhx.library.refresh;

/**
 * 下拉刷新和上拉加载UI回调接口
 */
public interface RefreshUIHandler {

    ///滑动
    void onMove(float offset, RefreshControl refreshControl);

    ///刷新完成
    void onFinish(RefreshControl refreshControl);

    ///加载中
    void onLoading(RefreshControl refreshControl);
}
