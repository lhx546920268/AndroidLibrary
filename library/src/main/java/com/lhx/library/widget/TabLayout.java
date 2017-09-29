package com.lhx.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lhx.library.util.SizeUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 菜单栏
 */
public class TabLayout extends FrameLayout {

    ///菜单列表
    private RecyclerView mRecyclerView;

    ///线性布局
    private LinearLayoutManager mLinearLayoutManager;

    ///适配器
    private TabAdapter mAdapter;

    ///分割线
    private TabDecoration mTabDecoration;

    ///下划线
    private View mSelectedIndicator;

    ///下划线颜色
    private int mSelectedIndicatorColor = Color.RED;

    ///下划线宽度padding
    private int mSelectedIndicatorWidthPadding = 10;
    
    //下划线高度
    private int mSelectedIndicatorHeight = 4;

    ///顶部分割线
    private View mTopSeparator;

    ///底部分割线
    private View mBottomSeparator;

    ///菜单按钮信息
    private ArrayList<TabInfo> mTabInfos;

    ///选中的下标
    private int mSelectedPosition;

    ///没有选中
    public static final int NO_SELECT = -1;

    ///内容宽度
    private int mContentWidth;
    private int mContentHeight;

    ///回调
    private Set<OnTabSelectedListener> mOnTabSelectedListeners = new HashSet<>();

    ///数据源
    private TabLayoutDataSet mTabLayoutDataSet;

    ///分割线颜色
    private int mDividerColor = Color.LTGRAY;

    ///分割线宽度
    private int mDividerWidth = 0;
    
    //分割线高度
    private int mDivierHeight = 30;

    ///是否正则动画
    private boolean mAnimating;

    ///菜单按钮宽度扩展
    private int mTabPadding = 40;

    ///按钮正常时标题颜色
    private int mNormalTitleColor = Color.BLACK;

    ///按钮正常时标题字体
    private int mNormalTitleSize = 16;

    ///按钮选中时标题颜色
    private int mSelectedTitleColor = Color.RED;

    ///按钮选中时标题字体
    private int mSelectedTitleSize = 16;

    ///将要选中的位置
    private int mWillSelectedPosition = NO_SELECT;

    ///根据屏幕等分，按钮宽度一样
    public final static int STYLE_UNIFORM = 0;

    ///根据按钮内容来自适应按钮宽度
    public final static int STYLE_WARP_CONTENT = 1;

    @IntDef({STYLE_UNIFORM, STYLE_WARP_CONTENT})
    @Retention(RetentionPolicy.SOURCE)
    @interface TabStyle {}

    ///列表样式
    private @TabStyle int mStyle = STYLE_UNIFORM;

    ///是否根据内容自动识别样式
    private boolean mAutomaticallyDetectStyle = true;

    ///添加点击tab回调
    public void addOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener){
        if(!mOnTabSelectedListeners.contains(onTabSelectedListener)){
            mOnTabSelectedListeners.add(onTabSelectedListener);
        }
    }

    ///移除点击tab回调
    public void removeTabSelectedListener(OnTabSelectedListener onTabSelectedListener){
        mOnTabSelectedListeners.remove(onTabSelectedListener);
    }

    ///设置数据源
    public void setTabLayoutDataSet(TabLayoutDataSet tabLayoutDataSet){
        if(tabLayoutDataSet != mTabLayoutDataSet){
            mTabLayoutDataSet = tabLayoutDataSet;
            reloadData();
        }
    }

    public void setSelectedIndicatorColor(int selectedIndicatorColor) {
        if(mSelectedIndicatorColor != selectedIndicatorColor){
            mSelectedIndicatorColor = selectedIndicatorColor;
            mSelectedIndicator.setBackgroundColor(mSelectedIndicatorColor);
            refreshUI();
        }
    }

    ///设置分割线颜色
    public void setDividerColor(int dividerColor) {
        if (mDividerColor != dividerColor) {
            mDividerColor = dividerColor;
            if (mRecyclerView != null) {
                mTabDecoration.setDividerColor(mDividerColor);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    ///设置分割线宽度
    public void setDividderWidth(int dividderWidth){
        if(dividderWidth != mDividerWidth){
            mDividerWidth = dividderWidth;
            refreshUI();
        }
    }

    ///设置菜单按钮宽度padding
    public void setTabWidthPadding(int TabWidthPadding){
        if(mTabPadding != TabWidthPadding){
            mTabPadding = TabWidthPadding;
            reloadData();
        }
    }

    public void setSelectedIndicatorWidthPadding(int selectedIndicatorWidthPadding) {
        if(selectedIndicatorWidthPadding != mSelectedIndicatorWidthPadding){
            mSelectedIndicatorWidthPadding = selectedIndicatorWidthPadding;
            if(mSelectedPosition != NO_SELECT && mSelectedPosition < mTabInfos.size()){
                mAdapter.notifyItemChanged(mSelectedPosition);
            }
        }
    }

    public void setSelectedIndicatorHeight(int selectedIndicatorHeight) {
        if(mSelectedIndicatorHeight != selectedIndicatorHeight){
            mSelectedIndicatorHeight = selectedIndicatorHeight;
            ViewGroup.LayoutParams params = mSelectedIndicator.getLayoutParams();
            params.height = selectedIndicatorHeight;
            mSelectedIndicator.setLayoutParams(params);
        }
    }

    public void setDivierHeight(int divierHeight) {
        if(mDivierHeight != divierHeight){
            mDivierHeight = divierHeight;
            mAdapter.notifyDataSetChanged();
        }
    }

    ///设置是否显示顶部分割线
    public void setShouldDisplayTopSeparator(boolean display){
        if(!display && mTopSeparator == null)
            return;

        if(mTopSeparator == null){
            mTopSeparator = new View(getContext());
            mTopSeparator.setBackgroundColor(Color.LTGRAY);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    SizeUtil.pxFormDip(0.5f, getContext()));
            layoutParams.gravity = Gravity.TOP;
            mTopSeparator.setLayoutParams(layoutParams);
            addView(mTopSeparator);
        }

        mTopSeparator.setVisibility(display ? VISIBLE : GONE);
    }

    ///设置是否显示底部分割线
    public void setShouldDisplayBottomSeparator(boolean display){
        if(!display && mBottomSeparator == null)
            return;

        if(mBottomSeparator == null){
            mBottomSeparator = new View(getContext());
            mBottomSeparator.setBackgroundColor(Color.LTGRAY);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    SizeUtil.pxFormDip(0.5f, getContext()));
            layoutParams.gravity = Gravity.BOTTOM;
            mBottomSeparator.setLayoutParams(layoutParams);
            addView(mBottomSeparator);
        }

        mBottomSeparator.setVisibility(display ? VISIBLE : GONE);
    }

    ///设置正常字体颜色
    public void setNormalTitleColor(int normalTitleColor) {
        if(mNormalTitleColor != normalTitleColor){
            mNormalTitleColor = normalTitleColor;
            refreshUI();
        }
    }

    ///设置选中字体大小
    public void setSelectedTitleSize(int selectedTitleSize) {
        if(mSelectedTitleSize != selectedTitleSize){
            mSelectedTitleSize = selectedTitleSize;
            reloadData();
        }
    }

    ///设置选中字体颜色
    public void setSelectedTitleColor(int selectedTitleColor) {
        if(mSelectedTitleColor != selectedTitleColor){
            mSelectedTitleColor = selectedTitleColor;
            refreshUI();
        }
    }

    ///设置选中字体大小
    public void setNormalTitleSize(int normalTitleSize) {
        if(mNormalTitleSize != normalTitleSize){
            mNormalTitleSize = normalTitleSize;
            refreshUI();
        }
    }

    ///设置是否自动识别样式
    public void setAutomaticallyDetectStyle(boolean automaticallyDetectStyle){
        if(mAutomaticallyDetectStyle != automaticallyDetectStyle){
            mAutomaticallyDetectStyle = automaticallyDetectStyle;
            if(mTabLayoutDataSet != null){
                reloadData();
            }
        }
    }

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, int style) {
        this(context, null);
        mStyle = style;
        mAutomaticallyDetectStyle = false;
    }

    public TabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {

            mRecyclerView = new RecyclerView(context);
            mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.MATCH_PARENT);
            mRecyclerView.setLayoutParams(layoutParams);

            mLinearLayoutManager = new LinearLayoutManager(context);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mAdapter = new TabAdapter();
            mRecyclerView.setAdapter(mAdapter);

            mTabDecoration = new TabDecoration();
            mRecyclerView.addItemDecoration(mTabDecoration);

            mTabInfos = new ArrayList<>();

            addView(mRecyclerView);

            mSelectedIndicator = new View(getContext());
            mSelectedIndicator.setVisibility(INVISIBLE);
            mSelectedIndicator.setBackgroundColor(mSelectedIndicatorColor);
            layoutParams = new FrameLayout.LayoutParams(120, mSelectedIndicatorHeight);
            layoutParams.gravity = Gravity.BOTTOM;
            mSelectedIndicator.setLayoutParams(layoutParams);
            addView(mSelectedIndicator);
        }
    }


    public void setStyle(int style) {
        if(mStyle != style){
            mStyle = style;
            reloadData();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ///宽度改变时刷新数据
        int width = getMeasuredWidth();
        mContentHeight = getMeasuredHeight();
        if (width != mContentWidth) {
            mContentWidth = width;
            reloadData();
        }
    }

    ///刷新UI
    public void refreshUI(){
        if(mRecyclerView != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    ///刷新数据
    public void reloadData() {

        if (mTabLayoutDataSet != null && mContentWidth > 0) {

            mTabInfos.clear();
            if(mAutomaticallyDetectStyle){
                fetchDataAndDetectStyle();
            }else {
                fetchData();
            }

            mAdapter.notifyDataSetChanged();
            selectDefaultMenuTab();
        }
    }

    ///获取数据
    private void fetchData(){
        Paint paint = new Paint();
        int count = mTabLayoutDataSet.numberOfTabs();

        for (int i = 0; i < count; i++) {
            TabInfo info = new TabInfo();
            mTabLayoutDataSet.configureTabInfo(info, i);

            ///计算按钮的宽度
            switch (mStyle) {
                case STYLE_WARP_CONTENT:
                    paint.setTextSize(SizeUtil.pxFromSp(Math.max(mNormalTitleSize, mSelectedTitleSize), getContext()));
                    info.mTabWidth = (int) paint.measureText(info.mTitle) + 1 + mTabPadding;
                    break;
                case STYLE_UNIFORM:
                    info.mTabWidth = mContentWidth / count;
                    break;
            }

            paint.setTextSize(SizeUtil.pxFromSp(mSelectedTitleSize, getContext()));
            info.mSelectedIndicatorWidth = (int) paint.measureText(info.mTitle) + 1 + mSelectedIndicatorWidthPadding * 2;

            mTabInfos.add(info);
        }
    }

    ///获取数据并自动识别样式
    private void fetchDataAndDetectStyle(){
        Paint paint = new Paint();
        int count = mTabLayoutDataSet.numberOfTabs();

        int totalContentWidth = 0;
        for (int i = 0; i < count; i++) {
            TabInfo info = new TabInfo();
            mTabLayoutDataSet.configureTabInfo(info, i);

            ///计算按钮的宽度
            paint.setTextSize(SizeUtil.pxFromSp(Math.max(mNormalTitleSize, mSelectedTitleSize), getContext()));
            info.mTabWidth = (int) paint.measureText(info.mTitle) + 1 + mTabPadding;
            totalContentWidth += info.mTabWidth;

            paint.setTextSize(SizeUtil.pxFromSp(mSelectedTitleSize, getContext()));
            info.mSelectedIndicatorWidth = (int) paint.measureText(info.mTitle) + 1 + mSelectedIndicatorWidthPadding * 2;

            mTabInfos.add(info);
        }

        ///内容太少，样式使用固定宽度
        if(totalContentWidth < mContentWidth){
            mStyle = STYLE_UNIFORM;
            for(int i = 0;i < count;i ++){
                TabInfo info = mTabInfos.get(i);
                info.mTabWidth = mContentWidth / count;
            }
        }else {
            mStyle = STYLE_WARP_CONTENT;
        }
    }

    ///选中默认值
    private void selectDefaultMenuTab(){
        if (mTabInfos.size() > 0 && mContentWidth > 0) {
            if(mWillSelectedPosition != NO_SELECT && mWillSelectedPosition < mTabInfos.size()){
                selectMenuTab(mWillSelectedPosition);
                mWillSelectedPosition = NO_SELECT;
            }
        }
    }

    public void setTabTitle(String title, int position){

        if(position >= 0 && position < mTabInfos.size()){
            TabInfo info = mTabInfos.get(position);
            info.setTitle(title);
            mAdapter.notifyItemChanged(position);
        }
    }

    public int getSelectedPosition(){
        return mSelectedPosition;
    }

    /**
     * 选中某个按钮，无动画
     * @param position 按钮下标
     */
    public void selectMenuTab(int position) {
        this.selectMenuTab(position, false);
    }

    /**
     * 设置所有未选中
     */
    public void selectNoneMenuTab(){
        this.selectMenuTab(NO_SELECT, false);
    }

    /**
     * 选中某个按钮，可选择是否动画
     * @param position    按钮下标
     * @param animated 是否动画
     */
    public void selectMenuTab(final int position, boolean animated) {

        if(position >= 0 && mContentWidth == 0){
            mWillSelectedPosition = position;
            return;
        }
        if (position == mSelectedPosition)
            return;

        if(position >= mTabInfos.size() || position < 0){
            int previousPosition = mSelectedPosition;
            mSelectedPosition = position;
            if(previousPosition >= 0 && previousPosition < mTabInfos.size()){
                if(mRecyclerView.getChildCount() > 0){
                    mAdapter.notifyItemChanged(previousPosition);
                }
            }
            return;
        }

        int previousLeft = 0;
        ViewHolder holder = null;
        int previousPosition = mSelectedPosition;
        mSelectedPosition = position;

        boolean scroll = scrollToVisibleRect();
        mAnimating = animated && !scroll;
        if(!scroll){
            mRecyclerView.scrollToPosition(mSelectedPosition);
        }

        if (mRecyclerView.getChildCount() > 0) {

            holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(previousPosition);
            if(holder != null){
                TabInfo info = mTabInfos.get(mSelectedPosition);
                previousLeft = (info.mTabWidth - info.mSelectedIndicatorWidth) / 2;
                holder.mTab.setSelectedState(false);
                previousLeft += holder.mTab.getLeft();
            }else {
                previousLeft += getTabXForPosition(previousPosition);
            }
            if(previousPosition != NO_SELECT && previousPosition < mTabInfos.size()){
                mAdapter.notifyItemChanged(previousPosition);
            }

            holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
            if(holder != null){
                holder.mTab.setSelectedState(true);
            }
        }

        ///调整下划线的位置
        if (mSelectedIndicatorHeight > 0) {

            if (holder == null) {
                holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
            }

            TabInfo info = mTabInfos.get(position);
            final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSelectedIndicator.getLayoutParams();
            layoutParams.width = info.mSelectedIndicatorWidth;
            mSelectedIndicator.setLayoutParams(layoutParams);

            int left = (info.mTabWidth - info.mSelectedIndicatorWidth) / 2;
            if (holder != null) {
                left += holder.mTab.getLeft();
            } else {

                ///Tab还没渲染
                left += getTabXForPosition(position);
            }


            if (mAnimating) {
                mSelectedIndicator.setVisibility(VISIBLE);

                if (previousLeft > mContentWidth || (previousPosition < position && previousLeft > left) ||
                        (previousPosition > position && previousLeft < left)) {
                    previousLeft = previousPosition < position ? 0 : mContentWidth;
                }
                final int x = left;

                TranslateAnimation animation = new TranslateAnimation(previousLeft, x,
                        0, 0);
                animation.setDuration(250);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        mSelectedIndicator.setVisibility(GONE);
                        mAnimating = false;
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mSelectedIndicator.startAnimation(animation);
            }
        } else {
            mAdapter.notifyItemChanged(position);
        }

    }

    ///滚动到可见位置
    private boolean scrollToVisibleRect(){

        if(mStyle == STYLE_WARP_CONTENT){

            int count = mRecyclerView.getChildCount();
            if(count > 2) {
                int last = mLinearLayoutManager.findLastVisibleItemPosition();
                if(mSelectedPosition <= last){
                    if(last - 1 == mSelectedPosition) {
                        return scrollTopPosition(last);
                    } else {
                        int first = mLinearLayoutManager.findFirstVisibleItemPosition();
                        if(first + 1 == mSelectedPosition) {
                            return scrollTopPosition(first);
                        } else {
                            if(last == mSelectedPosition) {
                                if(last + 1 < mTabInfos.size()) {
                                    last ++;
                                }
                                return scrollTopPosition(last);
                            } else {
                                if(first == mSelectedPosition) {
                                    if(first - 1 >= 0) {
                                        first --;
                                    }
                                    return scrollTopPosition(first);
                                }
                            }
                        }
                    }
                }else {
                    last = mSelectedPosition;
                    if(last + 1 < mTabInfos.size()){
                        last ++;
                    }
                    return scrollTopPosition(last);
                }
            }
        }

        return false;
    }

    ///滚动到某个位置
    private boolean scrollTopPosition(int position){

        ViewHolder holder = (ViewHolder)mRecyclerView.findViewHolderForAdapterPosition(position);
        if(holder != null){

            ///判断是否已完全可以见
            if(holder.mTab.getLeft() < 0 || holder.mTab.getRight() > mContentWidth){

                mRecyclerView.scrollToPosition(position);
                return true;
            }
        }else if(position > 0 && position < mTabInfos.size()) {

            mRecyclerView.scrollToPosition(position);
            return true;
        }

        return false;
    }

    ///获取某个Tab的x
    private int getTabXForPosition(int position){

        int x = 0;
        for (int i = 0; i < position; i++) {
            TabInfo TabInfo = mTabInfos.get(i);
            x += TabInfo.mTabWidth + mDividerWidth;
        }

        return x;
    }

    ///菜单数据适配器
    private class TabAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            TabInfo info = mTabInfos.get(position);
            Tab Tab = holder.mTab;
            Tab.setInfo(info);
            Tab.setSelectedState(mSelectedPosition == position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final Tab Tab = new Tab(getContext());

            final ViewHolder holder = new ViewHolder(Tab);

            mLinearLayoutManager.generateDefaultLayoutParams();
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            Tab.setLayoutParams(layoutParams);

            ///添加点击事件
            Tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = holder.getLayoutPosition();
                    if (position != mSelectedPosition) {
                        selectMenuTab(position, true);
                        onTabSelected(position, mSelectedPosition);
                    }else {
                        onTabReselected(position);
                    }
                }
            });

            return holder;
        }

        @Override
        public int getItemCount() {
            return mTabInfos.size();
        }
    }

    ///菜单按钮holder
    private class ViewHolder extends RecyclerView.ViewHolder {

        Tab mTab;

        public ViewHolder(View TabView) {
            super(TabView);
            mTab = (Tab) TabView;
        }
    }

    ///菜单按钮信息
    public class TabInfo {

        ///标题
        String mTitle;

        //图标
        int mIcon;

        ///按钮宽度
        int mTabWidth;

        ///选中下划线宽度
        int mSelectedIndicatorWidth;

        Drawable mDrawable;

        //图片是否当初模板渲染
        boolean mIconShouldRenderAsTemplate = true;

        public void setIconShouldRenderAsTemplate(boolean iconShouldRenderAsTemplate) {
            this.mIconShouldRenderAsTemplate = iconShouldRenderAsTemplate;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public void setIcon(int icon) {
            mIcon = icon;
        }

        public Drawable getDrawable(@NonNull Context context){
            if(mIcon != 0){
                if(mDrawable == null){
                    mDrawable = ContextCompat.getDrawable(context, mIcon);
//                    if(mIconShouldRenderAsTemplate){
//                        int[] colors = new int[]{mSelectedTitleColor, mNormalTitleColor};
//                        int[][] states = new int[2][];
//                        states[0] = new int[]{android.R.attr.state_selected};
//                        states[1] = new int[]{};
//
//                        DrawableCompat.setTintList(mDrawable, new ColorStateList(states, colors));
//                    }
                }
                return mDrawable;
            }
            return null;
        }
    }

    ///菜单按钮
    private class Tab extends FrameLayout {

        ///菜单文本
        TextView mTextView;

        TabInfo mInfo;

        public Tab(Context context) {
            super(context);



            mTextView = new TextView(context);
            mTextView.setLines(1);
            mTextView.setPadding(SizeUtil.pxFormDip(5, context), 0, SizeUtil.pxFormDip(5, context), 0);
            mTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(mTextView);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            mTextView.setLayoutParams(layoutParams);
        }

        ///设置Tab信息
        void setInfo(TabInfo info) {

            mInfo = info;
            mTextView.setText(info.mTitle);
            Drawable drawable = info.getDrawable(getContext());
            if(drawable != null){
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mTextView.setCompoundDrawablePadding(SizeUtil.pxFormDip(5, getContext()));
                mTextView.setCompoundDrawables(null, null, drawable, null);
            }else {
                mTextView.setCompoundDrawables(null, null, null, null);
            }

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) getLayoutParams();
            layoutParams.width = info.mTabWidth;
            layoutParams.height = mContentHeight;
            setLayoutParams(layoutParams);
        }

        ///设置是否选中
        public void setSelectedState(boolean selected) {

            mTextView.setTextSize(selected ? mSelectedTitleSize : mNormalTitleSize);
            mTextView.setTextColor(selected ? mSelectedTitleColor : mNormalTitleColor);
            Drawable drawable = mInfo.getDrawable(getContext());
            if(drawable != null && mInfo.mIconShouldRenderAsTemplate){
                DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, selected ? mSelectedTitleColor : mNormalTitleColor);
            }
        }
    }

    ///分割线
    class TabDecoration extends RecyclerView.ItemDecoration {

        ///分割线绘制
        ColorDrawable mDividerDrawable;

        ///下划线绘制
        ColorDrawable mSelectedIndicatorDrawable;

        public TabDecoration() {

            mDividerDrawable = new ColorDrawable(mDividerColor);

            mSelectedIndicatorDrawable = new ColorDrawable(mSelectedIndicatorColor);
        }

        ///设置颜色
        void setDividerColor(int color) {
            mDividerDrawable.setColor(color);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

            if ((mDividerWidth > 0 && mDivierHeight > 0) || mSelectedIndicatorHeight > 0) {

                ///绘制分割线
                int count = parent.getChildCount();
                for (int i = 0; i < count; i++) {

                    View child = parent.getChildAt(i);
                    int position = parent.getChildLayoutPosition(child);
                    if(position < mTabInfos.size() - 1 && mDividerWidth > 0){

                        int top = child.getTop();
                        int bottom = child.getBottom();
                        int padding = (bottom - top - mDivierHeight) / 2;

                        mDividerDrawable.setColor(mDividerColor);
                        mDividerDrawable.setBounds(child.getRight() - mDividerWidth, top + padding, child.getRight(),
                                bottom - padding);
                        mDividerDrawable.draw(c);
                    }

                    ///绘制下划线
                    if(mSelectedPosition == position && mSelectedIndicatorHeight > 0 && !mAnimating){

                        TabInfo info = mTabInfos.get(position);

                        int left = (info.mTabWidth - info.mSelectedIndicatorWidth) / 2 + child.getLeft();
                        mSelectedIndicatorDrawable.setColor(mSelectedIndicatorColor);
                        mSelectedIndicatorDrawable.setBounds(left, child.getBottom() - mSelectedIndicator.getHeight(), left + info
                                .mSelectedIndicatorWidth, child.getBottom());
                        mSelectedIndicatorDrawable.draw(c);
                    }
                }
            }
        }
    }

    ///点击
    private void onTabSelected(int selectedPosition, int unselectedPosition){
        for(OnTabSelectedListener listener : mOnTabSelectedListeners){
            listener.onTabSelected(selectedPosition, this);
            if(unselectedPosition < mTabInfos.size() && unselectedPosition != NO_SELECT){
                listener.onTabUnselected(unselectedPosition, this);
            }
        }
    }

    //重复点击
    private void onTabReselected(int position){
        for(OnTabSelectedListener listener : mOnTabSelectedListeners){
            listener.onTabReselected(position, this);
        }
    }


    //点击按钮回调
    public interface OnTabSelectedListener{

        //选择某个tab
        void onTabSelected(int position, TabLayout tabLayout);

        //取消选择某个tab
        void onTabUnselected(int position, TabLayout tabLayout);

        //重复选择某个tab
        void onTabReselected(int position, TabLayout tabLayout);
    }

    //按钮数据源
    public interface TabLayoutDataSet{

        //按钮数量
        int numberOfTabs();

        //配置按钮信息
        void configureTabInfo(TabInfo tab, int position);
    }
}
