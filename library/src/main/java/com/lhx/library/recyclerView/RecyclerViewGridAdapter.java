package com.lhx.library.recyclerView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.lhx.library.R;
import com.lhx.library.section.EdgeInsets;
import com.lhx.library.section.GridSectionInfo;
import com.lhx.library.section.SectionInfo;
import com.lhx.library.util.SizeUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * 网格布局
 */
@SuppressWarnings("unused")
public abstract class RecyclerViewGridAdapter extends RecyclerViewAdapter{

    ///水平
    public static final int HORIZONTAL_LIST = 0;

    ///垂直
    public static final int VERTICAL_LIST = 1;

    @IntDef({HORIZONTAL_LIST, VERTICAL_LIST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation{}

    ///滑动方向
    private final @Orientation int mOrientation;

    ///以下的值是设置整个 RecyclerView的

    ///item之间的间隔 px
    protected int mItemSpace;

    ///item和header之间的间隔 px
    protected int mItemHeaderSpace;

    ///item和footer之间的间隔 px
    protected int mItemFooterSpace;

    ///section偏移量
    protected EdgeInsets mSectionInsets = new EdgeInsets();

    ///布局方式
    private GridLayoutManager mLayoutManager;

    ///所有不同的列的最小公倍数
    private int mDifferentColumnProduct;

    ///布局信息
    SparseArray<LayoutInfo> mLayoutInfos;

    ///是否需要绘制分割线
    public boolean mShouldDrawDivider = false;

    ///分割线颜色
    public @ColorInt int mDividerColor;

    ///构造方法
    public RecyclerViewGridAdapter(@Orientation int orientation, RecyclerView recyclerView) {
        super(recyclerView);

        mItemFooterSpace = mItemSpace = mItemHeaderSpace = SizeUtil.pxFormDip(0.5f, mRecyclerView.getContext());
        mDividerColor = ContextCompat.getColor(mRecyclerView.getContext(), R.color.divider_color);

        mOrientation = orientation;
        mDifferentColumnProduct = getDifferentColumnProduct();
        mLayoutManager = new GridLayoutManager(recyclerView.getContext(), mDifferentColumnProduct);

        ///设置滑动方向
        if(orientation == HORIZONTAL_LIST){
            mLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        }else {
            mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        }

        ///通过设置这里来确定分区
        mLayoutManager.setSpanCount(mDifferentColumnProduct);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                int type = getItemViewType(position);
                if(type == LOAD_MORE_VIEW_TYPE || type == EMPTY_VIEW_TYPE){
                    return mDifferentColumnProduct;
                }else {
                    return RecyclerViewGridAdapter.this.spanCountForPosition(position);
                }
            }

            @Override
            public void setSpanIndexCacheEnabled(boolean cacheSpanIndices) {
                super.setSpanIndexCacheEnabled(true);
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridItemDecoration(mOrientation));
    }

    public void setItemSpace(int itemSpace) {
        mItemSpace = itemSpace;
    }

    public void setItemHeaderSpace(int itemHeaderSpace) {
        mItemHeaderSpace = itemHeaderSpace;
    }

    public void setItemFooterSpace(int itemFooterSpace) {
        mItemFooterSpace = itemFooterSpace;
    }

    public void setSectionInsets(EdgeInsets sectionInsets) {
        mSectionInsets = sectionInsets;
    }

    public void setShouldDrawDivider(boolean shouldDrawDivider) {
        mShouldDrawDivider = shouldDrawDivider;
    }

    public void setDividerColor(@ColorInt int dividerColor) {
        mDividerColor = dividerColor;
    }

    /**列数
     * @param section section下标
     * @return 列数
     */
    public abstract int numberOfColumnInSection(int section);

    /**不同的列数的最小公倍数
     * @return 最小公倍数
     */
    public abstract int getDifferentColumnProduct();

    /**
     * item之间的间隔
     * @param section section下标
     * @return item之间的间隔
     */
    public int getItemSpaceAtSection(int section){

        return mItemSpace;
    }

    /**
     * item和 header之间的间隔
     * @param section section下标
     * @return item和 header之间的间隔
     */
    public int getItemHeaderSpaceAtSection(int section){

        return mItemHeaderSpace;
    }

    /**
     * item和 footer之间的间隔
     * @param section section下标
     * @return item和 footer之间的间隔
     */
    public int getItemFooterSpaceAtSection(int section){

        return mItemFooterSpace;
    }

    /**
     * section的偏移量
     * @param section section下标
     * @return section的偏移量
     */
    public EdgeInsets getSectionInsetsAtSection(int section) {

        return mSectionInsets;
    }

    /**
     * divider颜色
     * @param section 下标
     * @return 颜色
     */
    public @ColorInt int getDividerColor(int section){
        return mDividerColor;
    }

    public boolean headerShouldUseSectionInsets(int section){
        return true;
    }

    public boolean footerShouldUseSectionInsets(int section){
        return true;
    }

    ///获取spanCount
    private int spanCountForPosition(int position){

        GridSectionInfo sectionInfo = (GridSectionInfo)sectionInfoForPosition(position);

        if(sectionInfo.isHeaderForPosition(position) || sectionInfo.isFooterForPosition(position)) {
            return mDifferentColumnProduct;
        }
        else {
            return mDifferentColumnProduct / sectionInfo.numberOfColumns;
        }
    }

    ///获取数据
    @Override
    protected void getData(){

        if(mShouldReloadData){

            mShouldReloadData = false;
            if(mSections == null)
                mSections = new ArrayList<>();
            else
                mSections.clear();

            if(mLayoutInfos == null)
                mLayoutInfos = new SparseArray<>();
            else
                mLayoutInfos.clear();

            ///计算列表行数量
            int numberOfSection = numberOfSection();
            int count = 0;
            for(int section = 0;section < numberOfSection;section ++){

                int numberOfItem = numberOfItemInSection(section);

                ///保存section信息
                GridSectionInfo sectionInfo = new GridSectionInfo();
                sectionInfo.section = section;
                sectionInfo.numberItems = numberOfItem;
                sectionInfo.numberOfColumns = numberOfColumnInSection(section);
                sectionInfo.isExistHeader = shouldExistSectionHeaderForSection(section);
                sectionInfo.isExistFooter = shouldExistSectionFooterForSection(section);
                sectionInfo.itemSpace = getItemSpaceAtSection(section);
                sectionInfo.itemHeaderSpace = getItemHeaderSpaceAtSection(section);
                sectionInfo.itemFooterSpace = getItemFooterSpaceAtSection(section);
                sectionInfo.footerUseSectionInsets = footerShouldUseSectionInsets(section);
                sectionInfo.headerUseSectionInsets = headerShouldUseSectionInsets(section);

                EdgeInsets insets = getSectionInsetsAtSection(section);
                if(insets == null){
                    insets = mSectionInsets;
                }
                sectionInfo.sectionInsets = insets;
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

    ///布局信息
    class LayoutInfo{

        ///偏移量
        int left, top, right, bottom;

        public LayoutInfo(int position) {

            int type = getItemViewType(position);
            switch (type){
                case EMPTY_VIEW_TYPE :
                    left = top = right = bottom = 0;
                    break;
                case LOAD_MORE_VIEW_TYPE :
                    left = right = bottom = 0;
                    EdgeInsets insets = getSectionInsetsAtSection(mSections.size() - 1);
                    if(insets.bottom == 0){
                        top = getItemSpaceAtSection(mSections.size() - 1);
                    }
                    break;
                default : {
                    GridSectionInfo sectionInfo = (GridSectionInfo)sectionInfoForPosition(position);

                    boolean onTheTop = isOnTheTop(sectionInfo, position);
                    boolean onTheLeft = isOnTheLeft(sectionInfo, position);
                    boolean onTheRight = isOnTheRight(sectionInfo, position);
                    boolean onTheBottom = isOnTheBottom(sectionInfo, position);

                    left = onTheLeft ? sectionInfo.sectionInsets.left : 0;
                    top = onTheTop ? sectionInfo.sectionInsets.top : 0;
                    right = onTheRight ? sectionInfo.sectionInsets.right : 0;
                    bottom = onTheBottom ? sectionInfo.sectionInsets.bottom : 0;

                    if(sectionInfo.isHeaderForPosition(position)){

                        if(!sectionInfo.headerUseSectionInsets){
                            left = 0;
                            top = 0;
                            right = 0;
                            bottom = 0;
                        }

                        ///头部和item之间的间隔，如果该section内没有item,则忽略间隔
                        if(sectionInfo.numberItems > 0)
                            bottom = sectionInfo.itemHeaderSpace;

                    }else if(sectionInfo.isFooterForPosition(position)){

                        if(!sectionInfo.footerUseSectionInsets){
                            left = 0;
                            top = 0;
                            right = 0;
                            bottom = 0;
                        }
                        ///存在item
                        if(sectionInfo.numberItems > 0){
                            top = sectionInfo.itemFooterSpace;
                        }
                    }else {

                        ///中间的添加两边间隔，旁边的添加一边间隔， 低于1px无法显示，所以只添加一边间隔
                        if(sectionInfo.itemSpace >= 2){
                            if(onTheRight){
                                left = sectionInfo.itemSpace / 2;
                            }else if(onTheLeft){
                                right = sectionInfo.itemSpace / 2;
                            }else {
                                left = sectionInfo.itemSpace / 2;
                                right = sectionInfo.itemSpace / 2;
                            }
                        }else {
                            ///如果不是最右的item，则添加间隔，否则添加section右边的偏移量
                            if(!onTheRight){
                                right = sectionInfo.itemSpace;
                            }
                        }

                        ///如果不是最后一行，添加item间隔
                        if(!onTheBottom){
                            bottom = sectionInfo.itemSpace;
                        }else if(!sectionInfo.isExistFooter){

                            ///不存在底部，设置section偏移量
                            bottom = sectionInfo.sectionInsets.bottom;
                        }
                    }
                    break;
                }
            }
        }

        ///是否靠右
        private boolean isOnTheRight(GridSectionInfo sectionInfo, int position){

            if(sectionInfo.isFooterForPosition(position) || sectionInfo.isHeaderForPosition(position))
                return true;
            int index = position - sectionInfo.getItemPosition();
            return (index + 1) % sectionInfo.numberOfColumns == 0;
        }

        ///是否靠左
        private boolean isOnTheLeft(GridSectionInfo sectionInfo, int position){

            if(sectionInfo.isFooterForPosition(position) || sectionInfo.isHeaderForPosition(position))
                return true;
            int index = position - sectionInfo.getItemPosition();
            return index % sectionInfo.numberOfColumns == 0;
        }

        ///是否靠顶
        private boolean isOnTheTop(GridSectionInfo sectionInfo, int position){

            ///头部靠顶
            if(sectionInfo.isHeaderForPosition(position))
                return true;

            ///当不存在item和顶部时，如果是底部
            if(!sectionInfo.isExistHeader && sectionInfo.numberItems == 0 && sectionInfo.isFooterForPosition(position))
                return true;

            ///存在头部，item都不靠顶
            if(sectionInfo.isExistHeader)
                return false;

            ///不存在头部，第一行靠顶
            int index = position - sectionInfo.getItemPosition();
            return index < sectionInfo.numberOfColumns;
        }

        ///是否靠底
        boolean isOnTheBottom(GridSectionInfo sectionInfo, int position){

            ///底部靠底
            if(sectionInfo.isFooterForPosition(position))
                return true;

            ///当不存在item和底部时，如果是顶部
            if(!sectionInfo.isExistFooter && sectionInfo.numberItems == 0 && sectionInfo.isHeaderForPosition(position))
                return true;

            int itemCount = sectionInfo.numberItems;
            int totalRow = (itemCount - 1) / sectionInfo.numberOfColumns + 1;
            int curRow = (position - sectionInfo.getItemPosition()) / sectionInfo.numberOfColumns + 1;

            if (curRow == totalRow)
                return true;

            return false;
        }
    }



    ///网格分割线
    private class GridItemDecoration extends RecyclerView.ItemDecoration{

        ///分割线
        ColorDrawable mDivider;

        ///滚动方向
        private int mOrientation;

        ///构造方法
        GridItemDecoration(int orientation){

            mOrientation = orientation;
            mDivider = new ColorDrawable();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

            if(!mShouldDrawDivider)
                return;

            ///绘制分割线
            int count = parent.getChildCount();

            for(int i = 0;i < count;i ++){

                View child = parent.getChildAt(i);
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams)child.getLayoutParams();

                int position = layoutParams.getViewLayoutPosition();
                LayoutInfo layoutInfo = getLayoutInfoAtPosition(position);

                if(mSections.size() > 0){
                    SectionInfo sectionInfo = sectionInfoForPosition(position);
                    mDivider.setColor(getDividerColor(sectionInfo.section));
                }else {
                    mDivider.setColor(mDividerColor);
                }

                int left;
                int right;
                int top;
                int bottom;

                if(layoutInfo.left > 0){

                    left = child.getLeft() - layoutParams.leftMargin - layoutInfo.left;
                    top = child.getTop() - layoutInfo.top;
                    right = left + layoutInfo.left;
                    bottom = child.getBottom() + layoutInfo.bottom;

                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }

                if(layoutInfo.right > 0){

                    left = child.getRight() + layoutParams.rightMargin;
                    top = child.getTop() - layoutInfo.top;
                    right = left + layoutInfo.right;
                    bottom = child.getBottom() + layoutInfo.bottom;

                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }

                if(layoutInfo.bottom > 0){

                    left = child.getLeft() - layoutInfo.left;
                    top = child.getBottom() + layoutParams.bottomMargin;
                    right = child.getRight() + layoutInfo.right;
                    bottom = top + layoutInfo.bottom;

                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }

                if(layoutInfo.top > 0){

                    left = child.getLeft() - layoutInfo.left;
                    top = child.getTop() - layoutParams.topMargin - layoutInfo.top;
                    right = child.getRight() + layoutInfo.right;
                    bottom = top + layoutInfo.top;

                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }

        ///获取item布局信息
        private LayoutInfo getLayoutInfoAtPosition(int position){

            LayoutInfo layoutInfo = mLayoutInfos.get(position);
            if(layoutInfo != null)
                return layoutInfo;


            layoutInfo = new LayoutInfo(position);
            mLayoutInfos.put(position, layoutInfo);

            return layoutInfo;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams)view.getLayoutParams();

            int position = layoutParams.getViewLayoutPosition();

            LayoutInfo info = getLayoutInfoAtPosition(position);
            if(mOrientation == HORIZONTAL_LIST){
                outRect.set(info.top, info.left, info.bottom, info.right);
            }else {
                outRect.set(info.left, info.top, info.right, info.bottom);
            }
        }
    }
}
