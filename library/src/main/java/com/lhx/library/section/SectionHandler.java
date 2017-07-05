package com.lhx.library.section;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * AbsListView 、RecyclerView 分块
 */
public interface SectionHandler {

    ///itemView类型 行
    int ITEM_TYPE_VIEW = 0;

    ///itemView类型 header
    int ITEM_TYPE_HEADER = 1;

    ///itemView类型 footer
    int ITEM_TYPE_FOOTER = 2;

    @IntDef({ITEM_TYPE_VIEW, ITEM_TYPE_HEADER, ITEM_TYPE_FOOTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ItemType{}  //public 不能去掉，否则会编译错误，找不到ItemType

    ///section数量
    int numberOfSection();

    /**
     * 每个section中的item数量
     * @param section section下标
     * @return 该section中item的数量
     */
    int numberOfItemInSection(int section);

    /**
     * 是否需要section的头部
     * @param section section下标
     * @return 是否需要
     */
    boolean shouldExistSectionHeaderForSection(int section);

    /**
     * 是否需要section的底部
     * @param section section下标
     * @return 是否需要
     */
    boolean shouldExistSectionFooterForSection(int section);
}
