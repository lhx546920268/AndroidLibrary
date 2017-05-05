package com.lhx.library.recyclerView;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.library.section.RecyclerViewSectionHandler;
import com.lhx.library.section.SectionInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Recycler 布局控制器
 */

@SuppressWarnings("unused")
public abstract class RecyclerViewSectionAdapter<T extends SectionInfo> extends RecyclerView.Adapter implements
        RecyclerViewSectionHandler{

    //关联的
    protected RecyclerView mRecyclerView;

    ///section信息数组
    protected ArrayList<T> mSections;

    ///数据是否需要刷新
    protected boolean mShouldReloadData = true;

    ///item数量
    protected int itemCount;

    ///构造方法
    public RecyclerViewSectionAdapter(RecyclerView recyclerView) {

        mRecyclerView = recyclerView;

        ///添加数据改变监听
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                ///数据改变，刷新数据
                mShouldReloadData = true;
            }
        });
    }


    @Override
    public void onBindHeaderViewHolderForSection(final RecyclerView.ViewHolder viewHolder, int section){

    }

    @Override
    public void onBindFooterViewHolderForSection(final RecyclerView.ViewHolder  viewHolder, int section){

    }

    @Override
    public int getItemIdForIndexPath(int indexInSection, int section, @ItemType int type){
        return 0;
    }

    @Override
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


    /**
     * 获取item
     * @param indexInSection section中的行下标
     * @param section section下标
     * @return 返回对应的item
     */
    public View itemAtIndexPath(int indexInSection, int section){
        SectionInfo sectionInfo = mSections.get(section);
        int position = sectionInfo.getItemPosition() + indexInSection;
        return mRecyclerView.getLayoutManager().findViewByPosition(position);
    }

    /**
     * 获取header
     * @param section section下标
     * @return 返回对应的header
     */
    public View headerAtSection(int section){
        SectionInfo sectionInfo = mSections.get(section);
        int position = sectionInfo.getHeaderPosition();
        return mRecyclerView.getLayoutManager().findViewByPosition(position);
    }

    /**
     * 获取footer
     * @param section section下标
     * @return 返回对应的footer
     */
    public View footerAtSection(int section){
        SectionInfo sectionInfo = mSections.get(section);
        int position = sectionInfo.getFooterPosition();
        return mRecyclerView.getLayoutManager().findViewByPosition(position);
    }

    /**
     * 获取item
     * @param indexInSection section中的行下标
     * @param section section下标
     * @return 返回对应的item
     */
    public RecyclerView.ViewHolder itemViewHolderAtIndexPath(int indexInSection, int section){
        return mRecyclerView.getChildViewHolder(itemAtIndexPath(indexInSection, section));
    }

    /**
     * 获取header viewHolder
     * @param section section下标
     * @return 返回对应的header viewHolder
     */
    public RecyclerView.ViewHolder headerViewHolderAtSection(int section){
        return mRecyclerView.getChildViewHolder(headerAtSection(section));
    }

    /**
     * 获取footer viewHolder
     * @param section section下标
     * @return 返回对应的footer viewHolder
     */
    public RecyclerView.ViewHolder footerViewHolderAtSection(int section){
        return mRecyclerView.getChildViewHolder(footerAtSection(section));
    }

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
                int position = holder.getAdapterPosition();
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

            return getItemIdForIndexPath(0, sectionInfo.section, ITEM_TYPE_HEADER);
        }

        ///存在底部
        if(sectionInfo.isFooterForPosition(position)){

            return getItemIdForIndexPath(0, sectionInfo.section, ITEM_TYPE_FOOTER);
        }

        return getItemIdForIndexPath(position - sectionInfo.getItemPosition(), sectionInfo.section, ITEM_TYPE_VIEW);
    }

    @Override
    public int getItemViewType(int position) {

        SectionInfo sectionInfo = sectionInfoForPosition(position);

        ///存在头部
        if(sectionInfo.isHeaderForPosition(position)){

            return getItemViewTypeForIndexPath(0, sectionInfo.section, ITEM_TYPE_HEADER);
        }

        ///存在底部
        if(sectionInfo.isFooterForPosition(position)){

            return getItemViewTypeForIndexPath(0, sectionInfo.section, ITEM_TYPE_FOOTER);
        }

        return getItemViewTypeForIndexPath(position - sectionInfo.getItemPosition(), sectionInfo.section,
                ITEM_TYPE_VIEW);
    }

    ///获取数据
    protected void getData(){

        if(mShouldReloadData){

            mShouldReloadData = false;
            if(mSections == null)
                mSections = new ArrayList<>();
            else
                mSections.clear();

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
                mSections.add((T)sectionInfo);

                itemCount += numberOfItem;
                if(sectionInfo.isExistHeader)
                    itemCount ++;
                if(sectionInfo.isExistFooter)
                    itemCount ++;
            }
        }
    }

    ///通过postion获取对应的sectionInfo
    protected T sectionInfoForPosition(int position){

        SectionInfo target = mSections.get(0);

        for(int i = 0;i < mSections.size();i ++){

            SectionInfo sectionInfo = mSections.get(i);
            if(sectionInfo.sectionBegin > position) {

                break;
            }else {

                target = sectionInfo;
            }
        }

        return (T)target;
    }
}
