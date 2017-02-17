package com.lhx.library.recyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * 网格布局
 */
@SuppressWarnings("unused")
public abstract class GridRecyclerViewLayoutController {

    ///水平
    public static final int HORIZONTAL_LIST = 0;

    ///垂直
    public static final int VERTICAL_LIST = 1;

    @IntDef({HORIZONTAL_LIST, VERTICAL_LIST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation{}

    ///关联的RecyclerView
    private RecyclerView mRecyclerView;

    ///滑动方向
    private final @Orientation
    int mOrientation;

    ///以下的值是设置整个 RecyclerView的

    ///item之间的间隔 px
    int itemSpace;

    ///item和header之间的间隔 px
    int itemHeaderSpace;

    ///item和footer之间的间隔 px
    int itemFooterSpace;

    ///section偏移量
    EdgeInsets sectionInsets = new EdgeInsets();

    ///布局方式
    private GridLayoutManager mLayoutManager;

    ///所有不同的列的乘积
    private int mDifferentColumnProduct;

    ///itemView类型 行
    public static final int ITEMTYPE_VIEW = 0;

    ///itemView类型 header
    public static final int ITEMTYPE_HEADER = 1;

    ///itemView类型 footer
    public static final int ITEMTYPE_FOOTER = 2;

    ///section信息数组
    private ArrayList<SectionInfo> mSections;

    ///布局信息
    SparseArray<LayoutInfo> layoutInfos;

    ///是否需要绘制分割线
    public boolean shouldDrawDivider = false;

    ///数据是否需要刷新
    private boolean shouldReloadData = true;

    ///item数量
    private int itemCount;

    ///构造方法
    public GridRecyclerViewLayoutController(@Orientation int orientation, RecyclerView recyclerView) {
        this.mOrientation = orientation;
        this.mRecyclerView = recyclerView;

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

                return GridRecyclerViewLayoutController.this.spanCountForPosition(position);
            }

            @Override
            public void setSpanIndexCacheEnabled(boolean cacheSpanIndices) {
                super.setSpanIndexCacheEnabled(true);
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridItemDecoration(mOrientation));
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

    /**列数
     * @param section section下标
     * @return 列数
     */
    public abstract int numberOfColumnInSection(int section);

    /**不同的列数的乘积
     * @return 乘积
     */
    public abstract int getDifferentColumnProduct();

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
    public int getItemIdForIndexPath(int indexInSection, int section, int type){
        return 0;
    }

    /**
     * getItemViewType 的重写方法 default is ITEMTYPE_VIEW , ITEMTYPE_FOOTER, ITEMTYPE_HEADER
     * @param indexInSection section中的行下标
     * @param section section下标
     * @param type itemView类型 如果type不等于 ITEMTYPE_VIEW,则indexPath.item 无效
     * @return getItemViewType 的返回值
     */
    public int getItemViewTypeForIndexPath(int indexInSection, int section, int type){

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
     * item之间的间隔
     * @param section section下标
     * @return item之间的间隔
     */
    public int getItemSpaceAtSection(int section){

        return itemSpace;
    }

    /**
     * item和 header之间的间隔
     * @param section section下标
     * @return item和 header之间的间隔
     */
    public int getItemHeaderSpaceAtSection(int section){

        return itemHeaderSpace;
    }

    /**
     * item和 footer之间的间隔
     * @param section section下标
     * @return item和 footer之间的间隔
     */
    public int getItemFooterSpaceAtSection(int section){

        return itemFooterSpace;
    }

    /**
     * section的偏移量
     * @param section section下标
     * @return section的偏移量
     */
    public EdgeInsets getSectionInsetsAtSection(int section) {

        return sectionInsets;
    }

    ///刷新数据
    private void reloadData(){

        shouldReloadData = true;
        if(layoutInfos != null)
            layoutInfos.clear();
        if(mSections != null)
            mSections.clear();

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    ///获取spanCount
    private int spanCountForPosition(int position){

        SectionInfo sectionInfo = sectionInfoForPosition(position);

        if(sectionInfo.isHeaderForPosition(position) || sectionInfo.isFooterForPosition(position)) {
            return mDifferentColumnProduct;
        }
        else {
            return mDifferentColumnProduct / sectionInfo.numberOfColumns;
        }
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

                    if(sectionInfo.isHeaderForPosition(position)){
                        ///存在头部
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
                if(layoutInfos == null)
                    layoutInfos = new SparseArray<>();

                ///计算列表行数量
                int numberOfSection = numberOfSection();
                itemCount = 0;
                for(int section = 0;section < numberOfSection;section ++){

                    int numberOfItem = numberOfItemInSection(section);

                    ///保存section信息
                    SectionInfo sectionInfo = new SectionInfo();
                    sectionInfo.section = section;
                    sectionInfo.numberItems = numberOfItem;
                    sectionInfo.numberOfColumns = numberOfColumnInSection(section);
                    sectionInfo.isExistHeader = shouldExistSectionHeaderForSection(section);
                    sectionInfo.isExistFooter = shouldExistSectionFooterForSection(section);
                    sectionInfo.itemSpace = getItemSpaceAtSection(section);
                    sectionInfo.itemHeaderSpace = getItemHeaderSpaceAtSection(section);
                    sectionInfo.itemFooterSpace = getItemFooterSpaceAtSection(section);
                    sectionInfo.sectionInsets = getSectionInsetsAtSection(section);
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

        ///列数
        int numberOfColumns;

        ///是否存在 footer 和 header
        boolean isExistHeader, isExistFooter;

        ///item之间的间隔 px
        int itemSpace;

        ///item和header之间的间隔 px
        int itemHeaderSpace;

        ///item和footer之间的间隔 px
        int itemFooterSpace;

        ///section偏移量
        EdgeInsets sectionInsets;

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

    ///布局信息
    class LayoutInfo{

        ///是否靠左
        boolean onTheLeft;

        ///是否靠右
        boolean onTheRight;

        ///是否靠顶
        boolean onTheTop;

        ///是否靠底
        boolean onTheBottom;

        ///偏移量
        int left, top, right, bottom;

        public LayoutInfo(int position) {

            SectionInfo sectionInfo = sectionInfoForPosition(position);

            onTheTop = isOnTheTop(sectionInfo, position);
            onTheLeft = isOnTheLeft(sectionInfo, position);
            onTheRight = isOnTheRight(sectionInfo, position);
            onTheBottom = isOnTheBottom(sectionInfo, position);

            left = onTheLeft ? sectionInfo.sectionInsets.left : 0;
            top = onTheTop ? sectionInfo.sectionInsets.top : 0;
            right = onTheRight ? sectionInfo.sectionInsets.right : 0;
            bottom = onTheBottom ? sectionInfo.sectionInsets.bottom : 0;

            if(sectionInfo.isHeaderForPosition(position)){

                ///头部和item之间的间隔，如果该section内没有item,则忽略间隔
                if(sectionInfo.numberItems > 0)
                    bottom = sectionInfo.itemHeaderSpace;

            }else if(sectionInfo.isFooterForPosition(position)){

                ///存在item
                if(sectionInfo.numberItems > 0){
                    top = sectionInfo.itemFooterSpace;
                }
            }else {

                ///如果不是最右的item，则添加间隔，否则添加section右边的偏移量
                if(!onTheRight){
                    right = sectionInfo.itemSpace;
                }

                ///如果不是最后一行，添加item间隔
                if(!onTheBottom){
                    bottom = itemSpace;
                }else if(!sectionInfo.isExistFooter){

                    ///不存在底部，设置section偏移量
                    bottom = sectionInfo.sectionInsets.bottom;
                }
            }
        }

        ///是否靠右
        private boolean isOnTheRight(SectionInfo sectionInfo, int position){

            if(sectionInfo.isFooterForPosition(position) || sectionInfo.isHeaderForPosition(position))
                return true;
            int index = position - sectionInfo.getItemPosition();
            return (index + 1) % sectionInfo.numberOfColumns == 0;
        }

        ///是否靠左
        private boolean isOnTheLeft(SectionInfo sectionInfo, int position){

            if(sectionInfo.isFooterForPosition(position) || sectionInfo.isHeaderForPosition(position))
                return true;
            int index = position - sectionInfo.getItemPosition();
            return index % sectionInfo.numberOfColumns == 0;
        }

        ///是否靠顶
        private boolean isOnTheTop(SectionInfo sectionInfo, int position){

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
        boolean isOnTheBottom(SectionInfo sectionInfo, int position){

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

    ///上下左右偏移量
    public class EdgeInsets {

        int left, top, right, bottom;

        public EdgeInsets() {
            this.left = 0;
            this.top = 0;
            this.right = 0;
            this.bottom = 0;;
        }

        public EdgeInsets(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    ///网格分割线
    private class GridItemDecoration extends RecyclerView.ItemDecoration{

        ///分割线
        Drawable mDivider;

        ///滚动方向
        private int mOrientation;

        ///构造方法
        GridItemDecoration(int orientation){

            mOrientation = orientation;
            ColorDrawable drawable = new ColorDrawable();
            drawable.setColor(Color.RED);
            mDivider = drawable;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

            if(!shouldDrawDivider)
                return;

            ///绘制分割线
            int count = parent.getChildCount();

            for(int i = 0;i < count;i ++){

                View child = parent.getChildAt(i);
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams)child.getLayoutParams();

                int position = layoutParams.getViewLayoutPosition();
                LayoutInfo layoutInfo = getLayoutInfoAtPosition(position);

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

            LayoutInfo layoutInfo = layoutInfos.get(position);
            if(layoutInfo != null)
                return layoutInfo;

            layoutInfo = new LayoutInfo(position);
            layoutInfos.put(position, layoutInfo);

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
