package com.lhx.library.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.library.R;
import com.lhx.library.loadmore.LoadMoreControl;
import com.lhx.library.loadmore.LoadMoreHandler;
import com.lhx.library.section.OnItemClickListener;
import com.lhx.library.section.RecyclerViewSectionHandler;
import com.lhx.library.section.SectionInfo;
import com.lhx.library.viewHoler.RecyclerViewHolder;
import com.lhx.library.widget.OnSingleClickListener;

import java.util.ArrayList;

/**
 * Recycler 布局控制器
 */

@SuppressWarnings("unused")
public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> implements
        RecyclerViewSectionHandler, LoadMoreControl.LoadMoreControlHandler, LoadMoreHandler, OnItemClickListener{


    //关联的
    protected RecyclerView mRecyclerView;

    ///section信息数组
    protected ArrayList<SectionInfo> mSections;

    ///数据是否需要刷新
    protected boolean mShouldReloadData = true;

    ///item数量
    protected int itemCount;

    ///数据源item的数量
    protected int mRealCount = 0;

    //加载更多控制器
    LoadMoreControl mLoadMoreControl;

    //空视图
    private View mEmptyView;

    ///上下文
    private Context mContext;

    static final int LOAD_MORE_VIEW_TYPE = 9999; //加载更多视图类型
    static final int EMPTY_VIEW_TYPE = 9998; //空视图类型

    ///构造方法
    public RecyclerViewAdapter(@NonNull RecyclerView recyclerView) {

        mRecyclerView = recyclerView;
        mContext = mRecyclerView.getContext();

        ///添加数据改变监听
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                ///数据改变，刷新数据
                mShouldReloadData = true;
            }
        });
    }

    public LoadMoreControl getLoadMoreControl() {
        if(mLoadMoreControl == null){
            mLoadMoreControl = new LoadMoreControl(mContext);
            mLoadMoreControl.setLoadMoreControlHandler(this);
        }
        return mLoadMoreControl;
    }

    public View getEmptyView() {
        if(mEmptyView == null){
            mEmptyView = View.inflate(mContext, R.layout.common_empty_view, null);
        }
        return mEmptyView;
    }

    public void setEmptyView(View emptyView) {
        if(mEmptyView != emptyView){
            mEmptyView = emptyView;
        }
    }

    //当没有数据的时候是否显示空视图
    protected boolean shouldDisplayEmptyView(){
        return true;
    }

    //空视图高度 默认和listView一样高
    protected int getEmptyViewHeight(){
        return -1;
    }

    @Override
    public final void onClickLoadMore() {
        getLoadMoreControl().setLoadingStatus(LoadMoreControl.LOAD_MORE_STATUS_LOADING);
        onLoadMore();
    }

    @Override
    public final void loadMoreComplete(boolean hasMore) {
        if(!loadMoreEnable())
            return;

        if(hasMore){
            getLoadMoreControl().setLoadingStatus(LoadMoreControl.LOAD_MORE_STATUS_HAS_MORE);
        }else {
            getLoadMoreControl().setLoadingStatus(LoadMoreControl.LOAD_MORE_STATUS_NO_MORE_DATA);
        }

        notifyDataSetChanged();
    }

    @Override
    public final void loadMoreFail() {
        if(!loadMoreEnable())
            return;
        getLoadMoreControl().setLoadingStatus(LoadMoreControl.LOAD_MORE_STATUS_FAIL);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public boolean loadMoreEnable() {
        return false;
    }

    //是否是加载更多的UI
    protected boolean isLoadMoreItem(int position){
        return mRealCount > 0 && position == mRealCount && loadMoreEnable() && getLoadMoreControl().shouldDisplay();
    }

    //是否正在加载更多
    public boolean isLoadingMore(){
        return loadMoreEnable() && getLoadMoreControl().isLoadingMore();
    }

    //是否是空视图
    protected boolean isEmptyView(int position){
        return mRealCount == 0 && shouldDisplayEmptyView();
    }

    @Override
    public void onBindHeaderViewHolderForSection(final RecyclerViewHolder viewHolder, int section){

    }

    @Override
    public void onBindFooterViewHolderForSection(final RecyclerViewHolder  viewHolder, int section){

    }

    @Override
    public int getItemIdForIndexPath(int indexInSection, int section, @ItemType int type){
        return 0;
    }

    @Override
    public int getItemViewTypeForIndexPath(int indexInSection, int section, @ItemType int type){
        return type;
    }

    @Override
    public boolean shouldExistSectionHeaderForSection(int section) {
        return false;
    }

    @Override
    public boolean shouldExistSectionFooterForSection(int section) {
        return false;
    }

    @Override
    public void onItemClick(int indexInSection, int section) {

    }

    @Override
    public void onHeaderClick(int section) {

    }

    @Override
    public void onFooterClick(int section) {

    }

//    @Override
//    public boolean shouldSetOnClickListenerAtItem(int indexInSection, int section) {
//        return false;
//    }
//
//    @Override
//    public boolean shouldSetOnClickListenerAtHeader(int section) {
//        return false;
//    }
//
//    @Override
//    public boolean shouldSetOnClickListenerAtFooter(int section) {
//        return false;
//    }

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
    public RecyclerViewHolder itemViewHolderAtIndexPath(int indexInSection, int section){
        return (RecyclerViewHolder)mRecyclerView.getChildViewHolder(itemAtIndexPath(indexInSection, section));
    }

    /**
     * 获取header viewHolder
     * @param section section下标
     * @return 返回对应的header viewHolder
     */
    public RecyclerViewHolder headerViewHolderAtSection(int section){
        return (RecyclerViewHolder)mRecyclerView.getChildViewHolder(headerAtSection(section));
    }

    /**
     * 获取footer viewHolder
     * @param section section下标
     * @return 返回对应的footer viewHolder
     */
    public RecyclerViewHolder footerViewHolderAtSection(int section){
        return (RecyclerViewHolder)mRecyclerView.getChildViewHolder(footerAtSection(section));
    }


    @Override
    public final void onBindViewHolder(RecyclerViewHolder holder, int position) {

        //触发加载更多
        if(loadMoreEnable() && getLoadMoreControl().loadMoreEnable()){
            if(mRealCount - position - 1 <= getLoadMoreControl().getReciprocalToLoadMore()){
                getLoadMoreControl().setLoadingStatus(LoadMoreControl.LOAD_MORE_STATUS_LOADING);
                onLoadMore();
            }
        }

        int viewType = holder.getItemViewType();
        switch (viewType){
            case LOAD_MORE_VIEW_TYPE :

                if(holder.itemView.getLayoutParams() instanceof RecyclerView.LayoutParams){

                }else {
                    RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams
                            .MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                    holder.itemView.setLayoutParams(params);
                }

                break;
            case EMPTY_VIEW_TYPE :

                RecyclerView.LayoutParams params = null;
                if(holder.itemView.getLayoutParams() instanceof RecyclerView.LayoutParams){
                    params = (RecyclerView.LayoutParams)holder.itemView.getLayoutParams();
                }else {
                    params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams
                            .MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                }
                int height = getEmptyViewHeight();
                if(height < 0){
                    height = mRecyclerView.getHeight();
                }
                params.height = height;

                holder.itemView.setLayoutParams(params);

                break;
            default : {
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
                break;
            }
        }
    }

    @Override
    public final RecyclerViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        switch (viewType){
            case LOAD_MORE_VIEW_TYPE : {

                return new RecyclerViewHolder(getLoadMoreControl().getContentView());
            }
            case EMPTY_VIEW_TYPE : {

                return new RecyclerViewHolder(getEmptyView());
            }
            default : {
                final RecyclerViewHolder holder = onCreateViewHolderForViewType(viewType, parent);
                holder.itemView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        ///添加点击事件
                        int position = holder.getAdapterPosition();
                        SectionInfo sectionInfo = sectionInfoForPosition(position);

                        ///存在头部
                        if(sectionInfo.isHeaderForPosition(position)){

                            onHeaderClick(sectionInfo.section);
                        }else if(sectionInfo.isFooterForPosition(position)){
                            ///存在底部
                            onFooterClick(sectionInfo.section);
                        }else {

                            onItemClick(position - sectionInfo.getItemPosition(), sectionInfo.section);
                        }
                    }
                });

                return holder;
            }

        }
    }

    @Override
    public final int getItemCount() {

        getData();
        return itemCount;
    }

    @Override
    public final long getItemId(int position) {
        if(isEmptyView(position) || isEmptyView(position))
            return 0;

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
    public final int getItemViewType(int position) {

        if(isEmptyView(position)){
            return EMPTY_VIEW_TYPE;
        }

        if(isLoadMoreItem(position)){
            return LOAD_MORE_VIEW_TYPE;
        }

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
            int count = 0;
            for(int section = 0;section < numberOfSection;section ++){

                int numberOfItem = numberOfItemInSection(section);

                ///保存section信息
                SectionInfo sectionInfo = new SectionInfo();
                sectionInfo.section = section;
                sectionInfo.numberItems = numberOfItem;
                sectionInfo.isExistHeader = shouldExistSectionHeaderForSection(section);
                sectionInfo.isExistFooter = shouldExistSectionFooterForSection(section);
                sectionInfo.sectionBegin = count;
                mSections.add(sectionInfo);

                count += numberOfItem;
                if(sectionInfo.isExistHeader)
                    count ++;
                if(sectionInfo.isExistFooter)
                    count ++;
            }

            mRealCount = count;

            if(loadMoreEnable() && getLoadMoreControl().shouldDisplay()){
                count ++;
            }

            if(count == 0 && shouldDisplayEmptyView()){
                count ++;
            }

            itemCount = count;
        }
    }

    ///通过postion获取对应的sectionInfo
    public SectionInfo sectionInfoForPosition(int position){

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

    //滚到对应的位置
    public void scrollTo(int section){
        scrollTo(section, false);
    }

    public void scrollTo(int section, boolean smooth){
        scrollTo(section, -1, smooth);
    }

    public void scrollTo(int section, int indexInSection, boolean smooth){
        if(mRecyclerView != null && section < mSections.size()){
            SectionInfo info = mSections.get(section);
            int position = info.getHeaderPosition();
            if(indexInSection >= 0){
                position = info.getItemPosition() + indexInSection;
            }

            if(smooth){
                mRecyclerView.smoothScrollToPosition(position);
            }else {
                mRecyclerView.scrollToPosition(position);
            }
        }
    }


    //移动到对应位置，如果能置顶则置顶
    public void scrollToWithOffset(int section, int offset){
        scrollToWithOffset(section, -1, offset);
    }

    public void scrollToWithOffset(int section, int indexInSection, int offset){

        if(mRecyclerView.getLayoutManager() instanceof LinearLayoutManager){
            LinearLayoutManager layoutManager = (LinearLayoutManager)mRecyclerView.getLayoutManager();

            SectionInfo info = mSections.get(section);
            int position = info.getHeaderPosition();
            if(indexInSection >= 0){
                position = info.getItemPosition() + indexInSection;
            }

            layoutManager.scrollToPositionWithOffset(position, offset);
        }else {
            throw new RuntimeException("scrollToWithOffset 仅仅支持 LinearLayoutManager");
        }
    }
}
