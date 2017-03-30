package com.lhx.library.refresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * 下拉刷新UI回调接口
 */
public interface RefreshUIHandler {

    //正常下拉状态
    void onPull(RefreshControl refreshControl);

    //下拉达到临界点，释放可刷新
    void onReachCriticalPoint(RefreshControl refreshControl);

    //刷新中
    void onRefresh(RefreshControl refreshControl);

    ///刷新完成 是否成功
    void onRefreshFinish(RefreshControl refreshControl, boolean success);

    //内容视图
    @NonNull
    View getContentView(@NonNull Context context, @RefreshControl.Orientation int orientation);
}
