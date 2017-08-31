package com.lhx.library.section;

import android.view.View;

/**
 * item 点击回调
 */

public interface OnItemClickListener {

    /**
     * 点击item
     * @param indexInSection section中的行下标
     * @param section section下标
     */
    @Deprecated
    void onItemClick(int indexInSection, int section);

    void onItemClick(int indexInSection, int section, View view);

    /**
     * 是否添加点击事件
     * @param indexInSection section中的行下标
     * @param section section下标
     * @return bool
     */
//    boolean shouldSetOnClickListenerAtItem(int indexInSection, int section);

    /**
     * 点击头部
     * @param section 头部下标
     */
    void onHeaderClick(int section);

    /**
     * 是否添加点击事件
     * @param section section下标
     * @return bool
     */
//    boolean shouldSetOnClickListenerAtHeader(int section);


    /**
     * 点击底部
     * @param section 底部下标
     */
    void onFooterClick(int section);

    /**
     * 是否添加点击事件
     * @param section section下标
     * @return bool
     */
//    boolean shouldSetOnClickListenerAtFooter(int section);
}
