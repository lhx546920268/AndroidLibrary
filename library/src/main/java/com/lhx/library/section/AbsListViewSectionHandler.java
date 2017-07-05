package com.lhx.library.section;

import android.view.View;
import android.view.ViewGroup;

/**
 * AbsListView  分块
 */

public interface AbsListViewSectionHandler extends SectionHandler {

    /**
     * 获取某个section中的头部
     * @param section section下标
     * @param convertView 和getView 中的一样
     * @param parent 和getView中一样
     * @return 头部
     */
    View getSectionHeaderForSection(int section, View convertView, ViewGroup parent);

    /**
     * 获取某个section中的底部
     * @param section section下标
     * @param convertView 和getView 中的一样
     * @param parent 和getView中一样
     * @return 底部
     */
    View getSectionFooterForSection(int section, View convertView, ViewGroup parent);

    /**
     * 获取某个section中的行视图
     * @param indexInSection section中的行下标
     * @param section section下标
     * @param convertView 和getView 中的一样
     * @param parent 和getView中一样
     * @return section中的行
     */
    View getViewForIndexPath(int indexInSection, int section, View convertView, ViewGroup parent);

    /**
     * getItem 的重写方法
     * @param indexInSection section中的行下标
     * @param section section下标
     * @param type itemView类型 如果type不等于 ITEMTYPE_VIEW,则indexInSection 无效
     * @return getItem 的返回值
     */
    Object getItemForIndexPath(int indexInSection, int section, @ItemType int type);

    /**
     * getItemId 的重写方法
     * @param indexInSection section中的行下标
     * @param section section下标
     * @param type itemView类型 如果type不等于 ITEMTYPE_VIEW,则indexInSection 无效
     * @return getItemId 的返回值
     */
    int getItemIdForIndexPath(int indexInSection, int section, @ItemType int type);

    /**
     * getItemViewType 的重写方法
     * @param indexInSection section中的行下标
     * @param section section下标
     * @param type itemView类型 如果type不等于 ITEMTYPE_VIEW,则indexInSection 无效
     * @return getItemViewType 的返回值
     */
    int getItemViewTypeForIndexPath(int indexInSection, int section, @ItemType int type);

    /**
     * 获取列表item类型的数量,包括sectionHeader 和 footer
     * @return item类型数量
     */
    int numberOfItemViewTypes();
}
