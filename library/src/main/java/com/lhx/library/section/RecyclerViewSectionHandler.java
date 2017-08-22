package com.lhx.library.section;

import android.view.ViewGroup;

import com.lhx.library.viewHoler.RecyclerViewHolder;

/**
 * RecyclerView 分块
 */

public interface RecyclerViewSectionHandler extends SectionHandler {

    /**
     * 获取 viewHolder
     *
     * @param viewType 和onCreateViewHolder中一样
     * @param parent   和onCreateViewHolder中一样
     * @return viewHolder
     */
    RecyclerViewHolder onCreateViewHolderForViewType(int viewType, ViewGroup parent);

    /**
     * 给某个section的头部绑定数据
     *
     * @param viewHolder onCreateHeaderViewHolderForSection 创建的 和 onBindViewHolder中的一样
     * @param section    section下标
     */
    void onBindHeaderViewHolderForSection(final RecyclerViewHolder viewHolder, int section);

    /**
     * 给某个section的底部绑定数据
     *
     * @param viewHolder onCreateFooterViewHolderForSection 创建的 和 onBindViewHolder中的一样
     * @param section    section下标
     */
    void onBindFooterViewHolderForSection(final RecyclerViewHolder viewHolder, int section);

    /**
     * 给某个item绑定数据
     *
     * @param viewHolder     onCreateFooterViewHolderForSection 创建的 和 onBindViewHolder中的一样
     * @param indexInSection section中的行下标
     * @param section        section下标
     */
    void onBindItemViewHolderForIndexPath(final RecyclerViewHolder viewHolder, int indexInSection, int section);

    /**
     * getItemId 的重写方法
     *
     * @param indexInSection section中的行下标
     * @param section        section下标
     * @param type           itemView类型 如果type不等于 ITEMTYPE_VIEW,则indexPath.item 无效
     * @return getItemId 的返回值
     */
    int getItemIdForIndexPath(int indexInSection, int section, @ItemType int type);

    /**
     * getItemViewType 的重写方法 default is ITEMTYPE_VIEW , ITEMTYPE_FOOTER, ITEMTYPE_HEADER
     *
     * @param indexInSection section中的行下标
     * @param section        section下标
     * @param type           itemView类型 如果type不等于 ITEMTYPE_VIEW,则indexPath.item 无效
     * @return getItemViewType 的返回值
     */
    int getItemViewTypeForIndexPath(int indexInSection, int section, @ItemType int type);
}
