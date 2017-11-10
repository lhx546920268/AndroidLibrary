package com.lhx.library.listView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.loadmore.LoadMoreControl;
import com.lhx.library.loadmore.LoadMoreHandler;
import com.lhx.library.section.AbsListViewSectionHandler;
import com.lhx.library.section.OnItemClickListener;
import com.lhx.library.section.SectionHandler;
import com.lhx.library.section.SectionInfo;
import com.lhx.library.widget.OnSingleClickListener;

import java.util.ArrayList;

;

/**
 * 把AbsListView 支持分成多个section，加载更多，没有数据是显示空视图
 */
public abstract class AbsListViewAdapter extends BaseAdapter implements AbsListViewSectionHandler,
        LoadMoreHandler, LoadMoreControl.LoadMoreControlHandler, OnItemClickListener{

    //没有位置
    private static final int NO_POSITION = -1;

    ///section信息数组
    private ArrayList<SectionInfo> mSections = new ArrayList<>();

    ///是否需要刷新数据
    private boolean mShouldReloadData = true;

    ///item数量
    private int mCount = 0;

    ///数据源item的数量
    private int mRealCount = 0;

    //加载更多控制器
    private LoadMoreControl mLoadMoreControl;
    private int mLoadMoreType;
    private int mLoadMorePosition = NO_POSITION;

    //空视图
    private View mEmptyView;
    private int mEmptyViewType;
    private int mEmptyViewPosition = NO_POSITION;

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

    //获取空视图ImageView
    public ImageView getEmptyImageView(){
        View view = getEmptyView();
        return (ImageView)view.findViewById(R.id.icon);
    }

    //获取空视图textView
    public TextView getEmptyTextView(){
        View view = getEmptyView();
        return (TextView) view.findViewById(R.id.text);
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
    public int numberOfSection() {
        return 1;
    }

    @Override
    public int getItemIdForIndexPath(int indexInSection, int section, @ItemType int type){
        return type;
    }

    @Override
    public int getItemViewTypeForIndexPath(int indexInSection, int section, @ItemType int type) {
        return 0;
    }

    @Override
    public int numberOfItemViewTypes() {
        return 1;
    }

    @Override
    public void onItemClick(int indexInSection, int section) {

    }

    @Override
    public void onItemClick(int indexInSection, int section, View view) {

    }

    @Override
    public void onHeaderClick(int section) {

    }

    @Override
    public void onFooterClick(int section) {

    }

    @Override
    public final void onClickLoadMore() {
        getLoadMoreControl().setLoadingStatus(LoadMoreControl.LOAD_MORE_STATUS_LOADING);
        onLoadMore();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public boolean loadMoreEnable() {
        return false;
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
        return mRealCount > 0 && position == mLoadMorePosition && loadMoreEnable() && getLoadMoreControl().shouldDisplay();
    }

    //是否正在加载更多
    public boolean isLoadingMore(){
        return loadMoreEnable() && getLoadMoreControl().isLoadingMore();
    }

    //是否是空视图
    private boolean isEmptyView(int position){
        return mRealCount == 0 && mEmptyViewPosition == position && shouldDisplayEmptyView();
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
                mLoadMorePosition = count;
                count ++;
            }

            if(mRealCount == 0 && shouldDisplayEmptyView() && getLoadMoreControl().displayEmptyViewEnable()){
                mEmptyViewPosition = count;
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

        if(isEmptyView(position)){
            return mEmptyViewType;
        }

        if(isLoadMoreItem(position)){
            return mLoadMoreType;
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

        int count = numberOfItemViewTypes();
        if(loadMoreEnable()){
            mLoadMoreType = count;
            count ++;
        }

        if(shouldDisplayEmptyView()){
            mEmptyViewType = count;
            count ++;
        }

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
            Object tag = convertView.getTag(R.id.list_view_type_tag_key);

            if(tag != null){
                int viewType = (int)tag;
                if(viewType != type){
                    convertView = null;
                }
            }else {
                convertView = null;
            }
        }

        View view;

        ///存在头部
        if(sectionInfo.isHeaderForPosition(position)){

            view = getSectionHeaderForSection(sectionInfo.section, convertView, parent);
        }else if(sectionInfo.isFooterForPosition(position)){

            ///存在底部
            view = getSectionFooterForSection(sectionInfo.section, convertView, parent);
        }else {
            ///行item
            view = getViewForIndexPath(position - sectionInfo.getItemPosition(), sectionInfo.section, convertView,
                    parent);
        }
        view.setTag(R.id.list_view_type_tag_key, type);
        view.setTag(R.id.list_view_item_position_tag_key, position);

        if(view.getTag(R.id.list_view_item_onclick_tag_key) == null){
            //添加点击事件
            view.setTag(R.id.list_view_item_onclick_tag_key, true);
            view.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    int position = (int)v.getTag(R.id.list_view_item_position_tag_key);
                    SectionInfo sectionInfo = sectionInfoForPosition(position);

                    ///存在头部
                    if(sectionInfo.isHeaderForPosition(position)){

                        onHeaderClick(sectionInfo.section);
                    }else if(sectionInfo.isFooterForPosition(position)){
                        ///存在底部
                        onFooterClick(sectionInfo.section);
                    }else {

                        onItemClick(sectionInfo.getItemPosition(position), sectionInfo.section);
                        onItemClick(sectionInfo.getItemPosition(position), sectionInfo.section, v);
                    }
                }
            });
        }

        return view;
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
}
