package com.lhx.library.refresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * 加载更多UI接口回调
 */

public interface LoadMoreUIHandler {

    //正常上拉状态
    void onPull(RefreshControl refreshControl);

    //刷新中
    void onLoadMore(RefreshControl refreshControl);

    ///加载完成 是否成功
    void onLoadMoreFinish(RefreshControl refreshControl, boolean success);

    //上拉达到临界点，释放可加重更多
    void onReachCriticalPoint(RefreshControl refreshControl);

    //内容视图
    @NonNull
    View getContentView(@NonNull Context context, @RefreshControl.Orientation int orientation);
}
