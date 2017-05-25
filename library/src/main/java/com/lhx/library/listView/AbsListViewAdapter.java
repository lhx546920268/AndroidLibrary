package com.lhx.library.listView;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.loadmore.LoadMoreControl;
import com.lhx.library.loadmore.LoadMoreHandler;
import com.lhx.library.section.AbsListViewSectionHandler;
import com.lhx.library.section.SectionInfo;

import java.util.ArrayList;

;

/**
 * 把AbsListView 支持分成多个section，加载更多，没有数据是显示空视图
 */
public abstract class AbsListViewAdapter extends BaseAdapter implements AbsListViewSectionHandler,
        LoadMoreHandler, LoadMoreControl.LoadMoreControlHandler{

    ///section信息数组
    private ArrayList<SectionInfo> mSections = new ArrayList<>();

    ///是否需要刷新数据
    private boolean mShouldReloadData = true;

    ///item数量
    private int mCount = 0;

    ///数据源item的数量
    private int mRealCount = 0;

    //加载更多控制器
    LoadMoreControl mLoadMoreControl;

    //空视图
    private View mEmptyView;

    ///上下文
    private Context mContext;

    public AbsListViewAdapter(@NonNull Context context) {

        mContext = context;
    }

    @Override
    public void notifyDataSetChanged() {
        mShouldReloadData = true;
        super.notifyDataSetChanged();
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
    public boolean shouldExistSectionHeaderForSection(int section){
        return false;
    }

    @Override
    public boolean shouldExistSectionFooterForSection(int section){
        return false;
    }

    @Override
    public View getSectionHeaderForSection(int section, View convertView, ViewGroup parent){
        return null;
    }

    @Override
    public View getSectionFooterForSection(int section, View convertView, ViewGroup parent){
        return null;
    }

    @Override
    public Object getItemForIndexPath(int indexInSection, int section, @ItemType int type){
        return null;
    };

    @Override
    public int getItemIdForIndexPath(int indexInSection, int section, @ItemType int type){
        return type;
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

    //是否是加载更多的UI
    private boolean isLoadMoreItem(int position){
        return mRealCount > 0 && position == mRealCount && loadMoreEnable() && getLoadMoreControl().shouldDisplay();
    }

    //是否是空视图
    private boolean isEmptyView(int position){
        return mRealCount == 0 && shouldDisplayEmptyView();
    }

    @Override
    public final int getCount() {

        if(mShouldReloadData){
            mShouldReloadData = false;
            mSections.clear();
            ///计算列表行数量
            int numberOfSection = this.numberOfSection();
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

            mCount = count;
        }

        return mCount;
    }

    @Override
    public final Object getItem(int position) {

        if(isEmptyView(position) || isLoadMoreItem(position))
            return null;

        SectionInfo sectionInfo = sectionInfoForPosition(position);

        ///存在头部
        if(position == sectionInfo.getHeaderPosition() && sectionInfo.isExistHeader){

            return getItemForIndexPath(0, sectionInfo.section, ITEM_TYPE_HEADER);
        }

        ///存在底部
        if(sectionInfo.isExistFooter && position == sectionInfo.getFooterPosition()){

            return getItemForIndexPath(0, sectionInfo.section, ITEM_TYPE_FOOTER);
        }

        return getItemForIndexPath(position - sectionInfo.getItemPosition(), sectionInfo.section, ITEM_TYPE_VIEW);
    }

    @Override
    public final int getItemViewType(int position) {

        if(isEmptyView(position) || isLoadMoreItem(position)){
            return numberItemViewTypes();
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

    @Override
    public final int getViewTypeCount() {

        int count = numberItemViewTypes();
        if(loadMoreEnable() && mLoadMoreControl.shouldDisplay())
            count ++;

        if(mRealCount == 0 && shouldDisplayEmptyView())
            count ++;

        return count;
    }

    @Override
    public final long getItemId(int position) {

        if(isEmptyView(position) || isLoadMoreItem(position))
            return 0;

        SectionInfo sectionInfo = sectionInfoForPosition(position);

        ///存在头部
        if(position == sectionInfo.getHeaderPosition() && sectionInfo.isExistHeader){

            return getItemIdForIndexPath(0, sectionInfo.section, ITEM_TYPE_HEADER);
        }

        ///存在底部
        if(sectionInfo.isExistFooter && position == sectionInfo.getFooterPosition()){

            return getItemIdForIndexPath(0, sectionInfo.section, ITEM_TYPE_FOOTER);
        }

        return getItemIdForIndexPath(position - sectionInfo.getItemPosition(), sectionInfo.section, ITEM_TYPE_VIEW);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {

        //触发加载更多
        if(loadMoreEnable() && getLoadMoreControl().loadMoreEnable()){
            if(mRealCount - position - 1 <= getLoadMoreControl().getReciprocalToLoadMore()){
                getLoadMoreControl().setLoadingStatus(LoadMoreControl.LOAD_MORE_STATUS_LOADING);
                onLoadMore();
            }
        }

        //显示空视图
        if(isEmptyView(position)){
            View emptyView = getEmptyView();
            AbsListView.LayoutParams params = null;
            if(emptyView.getLayoutParams() instanceof  AbsListView.LayoutParams){
                params = (AbsListView.LayoutParams)emptyView.getLayoutParams();
            }

            if(params == null){
                params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams
                        .WRAP_CONTENT);
            }

            int height = getEmptyViewHeight();
            if(height < 0){
                height = parent.getHeight();
            }
            params.height = height;

            emptyView.setLayoutParams(params);
            return emptyView;
        }


        if(isLoadMoreItem(position))
            return getLoadMoreControl().getContentView();

        SectionInfo sectionInfo = sectionInfoForPosition(position);

        //判断重用的view是否正确
        int type = getItemViewType(position);
        if(convertView != null){
            int viewType = (int)convertView.getTag(R.integer.list_view_tag_key);
            if(viewType != type){
                convertView = null;
            }
        }

        View view;

        ///存在头部
        if(position == sectionInfo.getHeaderPosition() && sectionInfo.isExistHeader){

            view = getSectionHeaderForSection(sectionInfo.section, convertView, parent);
        }else if(sectionInfo.isExistFooter && position == sectionInfo.getFooterPosition()){

            ///存在底部
            view = getSectionFooterForSection(sectionInfo.section, convertView, parent);
        }else {
            ///行item
            view = getViewForIndexPath(position - sectionInfo.getItemPosition(), sectionInfo.section, convertView,
                    parent);
        }
        view.setTag(R.integer.list_view_tag_key, type);
        return view;
    }

    ///通过postion获取对应的sectionInfo
    protected SectionInfo sectionInfoForPosition(int position){

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
}
