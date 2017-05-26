package com.lhx.library.section;

/**
 * item 点击回调
 */

public interface OnItemClickListener {

    /**
     * 点击item
     * @param indexInSection section中的行下标
     * @param section section下标
     */
    void onItemClick(int indexInSection, int section);

    /**
     * 点击头部
     * @param section 头部下标
     */
    void onHeaderClick(int section);
    /**
     * 点击底部
     * @param section 底部下标
     */
    void  onFooterClick(int section);
}
