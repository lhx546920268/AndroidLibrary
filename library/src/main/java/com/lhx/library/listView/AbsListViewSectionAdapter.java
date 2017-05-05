package com.lhx.library.listView;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.lhx.library.R;
import com.lhx.library.section.AbsListViewSectionHandler;
import com.lhx.library.section.SectionInfo;;

import java.util.ArrayList;

/**
 * 把AbsListView 分成多个section
 */
public abstract class AbsListViewSectionAdapter extends BaseAdapter implements AbsListViewSectionHandler {

    ///section信息数组
    private ArrayList<SectionInfo> mSections = new ArrayList<>();

    ///是否需要刷新数据
    private boolean mShouldReloadData = true;

    ///item数量
    private int mCount = 0;

    public AbsListViewSectionAdapter() {

        //注册数据改变监听
        registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mShouldReloadData = true;
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        });
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
            mCount = count;
        }

        return mCount;
    }

    @Override
    public final Object getItem(int position) {

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

        return numberItemViewTypes();
    }

    @Override
    public final long getItemId(int position) {

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
