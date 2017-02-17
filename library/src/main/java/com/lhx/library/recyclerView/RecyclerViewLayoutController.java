package com.lhx.library.recyclerView;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Recycler 布局控制器
 */

@SuppressWarnings("unused")
public abstract class RecyclerViewLayoutController{

    ///关联的RecyclerView
    private RecyclerView mRecyclerView;

    ///itemView类型 行
    static final int ITEMTYPE_VIEW = 0;

    ///itemView类型 header
    static final int ITEMTYPE_HEADER = 1;

    ///itemView类型 footer
    static final int ITEMTYPE_FOOTER = 2;

    @IntDef({ITEMTYPE_VIEW, ITEMTYPE_HEADER, ITEMTYPE_FOOTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ItemType{}

    ///section信息数组
    private ArrayList<SectionInfo> mSections;

    ///数据是否需要刷新
    private boolean shouldReloadData = true;

    ///item数量
    private int itemCount;

    ///构造方法
    public RecyclerViewLayoutController(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;

        mRecyclerView.setAdapter(new Adapter());

        ///添加数据改变监听
        mRecyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {

                ///数据改变，刷新数据
                reloadData();
                super.onChanged();
            }
        });
    }

    ///section数量
    public abstract int numberOfSection();

    /**
     * 每个section中的item数量
     * @param section section下标
     * @return 该section中item的数量
     */
    public abstract int numberOfItemInSection(int section);

    /**
     * 是否需要section的头部
     * @param section section下标
     * @return 是否需要
     */
    public boolean shouldExistSectionHeaderForSection(int section){

        return false;
    }

    /**
     * 是否需要section的底部
     * @param section section下标
     * @return 是否需要
     */
    public boolean shouldExistSectionFooterForSection(int section){

        return false;
    }

    /**
     * 获取 viewHolder
     * @param viewType 和onCreateViewHolder中一样
     * @param parent 和onCreateViewHolder中一样
     * @return viewHolder
     */
    public abstract RecyclerView.ViewHolder onCreateViewHolderForViewType(int viewType, ViewGroup parent);

    /**
     * 给某个section的头部绑定数据
     * @param viewHolder onCreateHeaderViewHolderForSection 创建的 和 onBindViewHolder中的一样
     * @param section section下标
     */
    public void onBindHeaderViewHolderForSection(final RecyclerView.ViewHolder viewHolder, int section){

    }

    /**
     * 给某个section的底部绑定数据
     * @param viewHolder onCreateFooterViewHolderForSection 创建的 和 onBindViewHolder中的一样
     * @param section section下标
     */
    public void onBindFooterViewHolderForSection(final RecyclerView.ViewHolder  viewHolder, int section){

    }

    /**
     * 给某个item绑定数据
     * @param viewHolder onCreateFooterViewHolderForSection 创建的 和 onBindViewHolder中的一样
     * @param indexInSection section中的行下标
     * @param section section下标
     */
    public abstract void onBindItemViewHolderForIndexPath(final RecyclerView.ViewHolder  viewHolder, int indexInSection, int section);

    /**
     * getItemId 的重写方法
     * @param indexInSection section中的行下标
     * @param section section下标
     * @param type itemView类型 如果type不等于 ITEMTYPE_VIEW,则indexPath.item 无效
     * @return getItemId 的返回值
     */
    public int getItemIdForIndexPath(int indexInSection, int section, @ItemType int type){
        return 0;
    }

    /**
     * getItemViewType 的重写方法 default is ITEMTYPE_VIEW , ITEMTYPE_FOOTER, ITEMTYPE_HEADER
     * @param indexInSection section中的行下标
     * @param section section下标
     * @param type itemView类型 如果type不等于 ITEMTYPE_VIEW,则indexPath.item 无效
     * @return getItemViewType 的返回值
     */
    public int getItemViewTypeForIndexPath(int indexInSection, int section, @ItemType int type){

        return type;
    }

    /**
     * 点击item
     * @param indexInSection section中的行下标
     * @param section section下标
     */
    public void onClickItemAtIndexPath(int indexInSection, int section){

    }

    /**
     * 点击头部
     * @param section 头部下标
     */
    public void onClickHeaderAtSection(int section){

    }

    /**
     * 点击底部
     * @param section 底部下标
     */
    public void  onClickFooterAtSection(int section){

    }


    ///刷新数据
    private void reloadData(){

        shouldReloadData = true;
        if(mSections != null)
            mSections.clear();

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter{

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            SectionInfo sectionInfo = sectionInfoForPosition(position);

            ///存在头部
            if(sectionInfo.isHeaderForPosition(position)){

                onBindHeaderViewHolderForSection(holder, sectionInfo.section);
                return;
            }

            ///存在底部
            if(sectionInfo.isFooterForPosition(position)){

                onBindFooterViewHolderForSection(holder, sectionInfo.section);
                return;
            }

            ///item
            onBindItemViewHolderForIndexPath(holder, position - sectionInfo.getItemPosition(), sectionInfo
                    .section);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

            final RecyclerView.ViewHolder holder = onCreateViewHolderForViewType(viewType, parent);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ///添加点击事件
                    int position = holder.getLayoutPosition();
                    SectionInfo sectionInfo = sectionInfoForPosition(position);

                    ///存在头部
                    if(sectionInfo.isHeaderForPosition(position)){

                        onClickHeaderAtSection(sectionInfo.section);
                    }else if(sectionInfo.isFooterForPosition(position)){
                        ///存在底部
                        onClickFooterAtSection(sectionInfo.section);
                    }else {

                        onClickItemAtIndexPath(position - sectionInfo.getItemPosition(), sectionInfo.section);
                    }
                }
            });

            return holder;
        }

        @Override
        public int getItemCount() {

            getData();
            return itemCount;
        }

        @Override
        public final long getItemId(int position) {

            SectionInfo sectionInfo = sectionInfoForPosition(position);

            ///存在头部
            if(sectionInfo.isHeaderForPosition(position)){

                return getItemIdForIndexPath(0, sectionInfo.section, ITEMTYPE_HEADER);
            }

            ///存在底部
            if(sectionInfo.isFooterForPosition(position)){

                return getItemIdForIndexPath(0, sectionInfo.section, ITEMTYPE_FOOTER);
            }

            return getItemIdForIndexPath(position - sectionInfo.getItemPosition(), sectionInfo.section, ITEMTYPE_VIEW);
        }

        @Override
        public int getItemViewType(int position) {

            SectionInfo sectionInfo = sectionInfoForPosition(position);

            ///存在头部
            if(sectionInfo.isHeaderForPosition(position)){

                return getItemViewTypeForIndexPath(0, sectionInfo.section, ITEMTYPE_HEADER);
            }

            ///存在底部
            if(sectionInfo.isFooterForPosition(position)){

                return getItemViewTypeForIndexPath(0, sectionInfo.section, ITEMTYPE_FOOTER);
            }

            return getItemViewTypeForIndexPath(position - sectionInfo.getItemPosition(), sectionInfo.section, ITEMTYPE_VIEW);
        }

        ///获取数据
        void getData(){

            if(shouldReloadData){

                shouldReloadData = false;
                if(mSections == null)
                    mSections = new ArrayList<>();

                ///计算列表行数量
                int numberOfSection = numberOfSection();
                itemCount = 0;
                for(int section = 0;section < numberOfSection;section ++){

                    int numberOfItem = numberOfItemInSection(section);

                    ///保存section信息
                    SectionInfo sectionInfo = new SectionInfo();
                    sectionInfo.section = section;
                    sectionInfo.numberItems = numberOfItem;
                    sectionInfo.isExistHeader = shouldExistSectionHeaderForSection(section);
                    sectionInfo.isExistFooter = shouldExistSectionFooterForSection(section);
                    sectionInfo.sectionBegin = itemCount;
                    mSections.add(sectionInfo);

                    itemCount += numberOfItem;
                    if(sectionInfo.isExistHeader)
                        itemCount ++;
                    if(sectionInfo.isExistFooter)
                        itemCount ++;
                }
            }
        }
    }

    ///通过postion获取对应的sectionInfo
    private SectionInfo sectionInfoForPosition(int position){

        SectionInfo target = mSections.get(0);

        for(int i = 0;i < mSections.size();i ++){

            SectionInfo sectionInfo = mSections.get(i);
            if(sectionInfo.sectionBegin > position) {

                break;
            }else {

                target = sectionInfo;
            }
        }

        return target;
    }

    ///section信息
    private class SectionInfo{

        ///section下标 section中行数 该section在listView中的起点
        int section, numberItems, sectionBegin;

        ///是否存在 footer 和 header
        boolean isExistHeader, isExistFooter;

        ///获取头部位置
        int getHeaderPosition() {

            return isExistHeader ? sectionBegin : -1;
        }

        ///获取底部位置
        int getFooterPosition(){

            if(!isExistFooter)
                return -1;

            int footerPosition = sectionBegin;
            if(isExistHeader)
                footerPosition ++;

            return footerPosition + numberItems;
        }

        ///获取item的起始位置
        int getItemPosition(){

            if(isExistHeader){

                return sectionBegin + 1;
            }else {

                return sectionBegin;
            }
        }

        ///判断position是否是头部
        boolean isHeaderForPosition(int position){

            return isExistHeader && position == getHeaderPosition();
        }

        ///判断position是否是底部
        boolean isFooterForPosition(int position){

            return isExistFooter && position == getFooterPosition();
        }
    }
}
