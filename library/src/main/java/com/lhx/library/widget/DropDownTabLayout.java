package com.lhx.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.popupWindow.BasePopupWindow;
import com.lhx.library.popupWindow.ListInfo;
import com.lhx.library.popupWindow.ListPopupWindow;
import com.lhx.library.util.SizeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 带有下拉菜单的tab
 */

public class DropDownTabLayout extends FrameLayout implements View.OnClickListener {

    //未选中
    public static final int NO_POSITION = -1;

    private Context mContext;

    //按钮信息
    private List<TabInfo> mTabInfos;

    //下拉列表
    private ListPopupWindow mListPopupWindow;

    //下拉列表位置 空的时候为 this
    private View mListAnchor;

    //按钮字体大小 sp
    private int mNormalTextSize, mSelectedTextSize;

    //按钮文字颜色
    private @ColorInt int mNormalTextColor, mSelectedTextColor;

    //当前选中的按钮
    private int mSelectedPosition = NO_POSITION;

    //以前选中的
    private int mPreviousPosition = NO_POSITION;

    //当下拉列表消失时是否保持选中状态
    private boolean mKeepSelectedAfterDismissList = true;

    //是否可以选中
    private boolean mShouldSelected = true;

    ///下划线
    private View mSelectedIndicator;

    ///下划线颜色
    private @ColorInt int mSelectedIndicatorColor = Color.RED;

    ///下划线宽度padding
    private int mSelectedIndicatorWidthPadding = 10;

    //下划线高度
    private int mSelectedIndicatorHeight = 4;

    ///顶部分割线
    private View mTopSeparator;

    ///底部分割线
    private View mBottomSeparator;

    //菜单按钮容器
    private LinearLayout mTabContainer;

    ///分割线宽度
    private int mDividerWidth;

    //分割线高度
    private int mDividerHeight;

    ///分割线颜色
    private @ColorInt int mDividerColor;

    //tab点击回调
    private OnDropDownTapSelectedListener mOnDropDownTapSelectedListener;

    //UI回调
    private OnDropDownTabUIHandler mOnDropDownTabUIHandler;

    public void setNormalTextSize(int normalTextSize) {
        mNormalTextSize = normalTextSize;
    }

    public void setSelectedTextSize(int selectedTextSize) {
        mSelectedTextSize = selectedTextSize;
    }

    public void setNormalTextColor(int normalTextColor) {
        mNormalTextColor = normalTextColor;
    }

    public void setSelectedTextColor(int selectedTextColor) {
        mSelectedTextColor = selectedTextColor;
    }

    public void setKeepSelectedAfterDismissList(boolean keepSelectedAfterDismissList) {
        mKeepSelectedAfterDismissList = keepSelectedAfterDismissList;
    }

    public void setShouldSelected(boolean shouldSelected) {
        mShouldSelected = shouldSelected;
    }

    public void setSelectedIndicator(View selectedIndicator) {
        mSelectedIndicator = selectedIndicator;
    }

    public void setDividerWidth(int dividerWidth) {
        mDividerWidth = dividerWidth;
    }

    public DropDownTabLayout(@NonNull Context context) {
        this(context, null);
    }

    public DropDownTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initialize();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    //初始化
    private void initialize(){

        mNormalTextSize = 15;
        mSelectedTextSize = 15;
        mNormalTextColor = Color.BLACK;
        mSelectedTextColor = Color.RED;

        mDividerWidth = mContext.getResources().getDimensionPixelSize(R.dimen.divider_size);
        mDividerHeight = SizeUtil.pxFormDip(15, mContext);
        mDividerColor = ContextCompat.getColor(mContext, R.color.divider_color);

        mTabContainer = new LinearLayout(mContext);
        mTabContainer.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        addView(mTabContainer, params);

        mSelectedIndicator = new View(getContext());
        mSelectedIndicator.setVisibility(INVISIBLE);
        mSelectedIndicator.setBackgroundColor(mSelectedIndicatorColor);
        params = new FrameLayout.LayoutParams(120, mSelectedIndicatorHeight);
        params.gravity = Gravity.BOTTOM;
        mSelectedIndicator.setLayoutParams(params);
        addView(mSelectedIndicator);

        mListAnchor = this;
    }

    public void setTabInfos(List<TabInfo> tabInfos) {
        if(mTabInfos != tabInfos){
            mTabInfos = tabInfos;
            createTabs();
        }
    }

    //获取菜单按钮
    public Tab getTab(int position){
        if(position >= 0 && position < mTabContainer.getChildCount()){
            return (Tab)mTabContainer.getChildAt(position);
        }
        return null;
    }

    //创建菜单按钮
    private void createTabs(){

        int childCount = mTabContainer.getChildCount();
        if(mTabInfos == null || mTabInfos.size() == 0){
            mTabContainer.removeAllViews();
        }

        int count = Math.min(mTabInfos.size(), childCount);

        //复用以前创建的tab
        for(int i = 0; i < count;i ++){
            setTab(i);
        }

        if(childCount < mTabInfos.size()){
            //需要创建更多tab
            for(int i = childCount;i < mTabInfos.size();i ++){
                createTab();
                setTab(i);
            }
        }else if(childCount > mTabInfos.size()) {
            //删除多余的tab
            List<View> views = new ArrayList<>(childCount - mTabInfos.size());
            for(int i = mTabInfos.size();i < childCount;i ++){
                views.add(mTabContainer.getChildAt(i));
            }
            for(View view : views){
                mTabContainer.removeView(view);
            }
        }
    }

    //创建tab
    private Tab createTab(){

        Tab tab = new Tab(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        tab.setOnClickListener(this);
        tab.setLayoutParams(params);
        mTabContainer.addView(tab);
        return tab;
    }

    //设置tab
    private void setTab(int position){
        TabInfo tabInfo = mTabInfos.get(position);

        Tab tab = (Tab)mTabContainer.getChildAt(position);
        tab.mTextView.setText(tabInfo.getDisplayTitle());

        setTabSelected(position, mSelectedPosition == position);

        tab.mDivider.setBackgroundColor(mDividerColor);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)tab.mDivider.getLayoutParams();
        params.width = mDividerWidth;
        params.height = mDividerHeight;

        tab.mPosition = position;
    }

    //取消讯中
    public void deselectTab(){
        setTabSelected(mSelectedPosition, false);
        mSelectedPosition = NO_POSITION;
    }

    //设置选中
    private void setTabSelected(int position, boolean selected){
        if(!positionIsValid(position))
            return;

        selected = selected && mShouldSelected;

        TabInfo tabInfo = mTabInfos.get(position);

        Tab tab = (Tab)mTabContainer.getChildAt(position);
        tab.mTextView.setTextColor(selected ? mSelectedTextColor : mNormalTextColor);
        tab.mTextView.setTextSize(selected ? mSelectedTextSize : mNormalTextSize);

        tab.mTextView.setCompoundDrawables(null, null, tabInfo.getDisplayIcon(selected), null);
        tab.setBackgroundColor(selected ? tabInfo.selectedBackgroundColor : Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {

        Tab tab = (Tab)v;
        if(tab.mPosition != mSelectedPosition){
            TabInfo tabInfo = mTabInfos.get(tab.mPosition);

            boolean enable = true;
            if(mOnDropDownTabUIHandler != null){
                enable = mOnDropDownTabUIHandler.shouldSelectTab(tabInfo, this);
            }

            if(!enable)
                return;

            setTabSelected(mSelectedPosition, false);
            mSelectedPosition = tab.mPosition;
            setTabSelected(mSelectedPosition, true);

            if(mOnDropDownTapSelectedListener != null){
                mOnDropDownTapSelectedListener.onDropDownTabSelected(tabInfo, this);
            }

            if(tabInfo.showListEnable()){
                boolean show = false;
                if(mOnDropDownTabUIHandler != null){
                    show = mOnDropDownTabUIHandler.shouldShowList(tabInfo, this);
                }

                if(show){
                    showList();
                }else {
                    dismissList();
                }
            }else {
                dismissList();
            }
        }else {
            TabInfo tabInfo = mTabInfos.get(mSelectedPosition);
            if(tabInfo.showListEnable()){

                if(getListPopupWindow().isShowing()){
                    dismissList();
                }else {
                    showList();
                }
            }else {
                if(tabInfo.rightSelectedIcon2 != null){
                    setTabSelected(tab.mPosition, true);
                }else {
                    dismissList();
                }
            }
        }

        updateIndicator();
    }

    //显示列表
    public void showList(){
        if(positionIsValid(mSelectedPosition)){
            getListPopupWindow();

            TabInfo tabInfo = mTabInfos.get(mSelectedPosition);
            mListPopupWindow.setInfos(tabInfo.infos);

            if(!mListPopupWindow.isShowing()){
                mListPopupWindow.showAsDropDown(mListAnchor);
            }
        }
    }

    //关闭列表
    public void dismissList(){

        if(mListPopupWindow != null && mListPopupWindow.isShowing()){
            mListPopupWindow.dismiss();
        }
    }

    //更新下划线位置
    private void updateIndicator(){
        if(positionIsValid(mSelectedPosition)){

            mSelectedIndicator.setVisibility(VISIBLE);
            TabInfo tabInfo = mTabInfos.get(mSelectedPosition);

        }else {
            mSelectedIndicator.setVisibility(GONE);
        }
    }

    //是否有效
    private boolean positionIsValid(int position){
        return mTabInfos != null && position >= 0 && position < mTabInfos.size();
    }

    public ListPopupWindow getListPopupWindow(){
        if(mListPopupWindow == null){
            mListPopupWindow = new ListPopupWindow(mContext);
            mListPopupWindow.setOnItemClickListener(new ListPopupWindow.OnItemClickListener() {
                @Override
                public void onItemClick(int position, ListInfo info) {

                    TabInfo tabInfo = mTabInfos.get(mSelectedPosition);
                    tabInfo.selectedPosition = position;

                    Tab tab = getTab(mSelectedPosition);
                    tab.mTextView.setText(tabInfo.title);

                    if(mOnDropDownTapSelectedListener != null){
                        mOnDropDownTapSelectedListener.onDropDownTabListItemSelected(tabInfo, DropDownTabLayout.this);
                    }
                }
            });
            mListPopupWindow.setShouldDismissAfterSelect(true);
            mListPopupWindow.setShouldDismissWhenTouchOutside(true);
            mListPopupWindow.setNormalColor(mNormalTextColor);
            mListPopupWindow.setSelectedColor(mSelectedTextColor);
            mListPopupWindow.addOnDismissHandler(new BasePopupWindow.OnDismissHandler() {
                @Override
                public void onDismiss() {
                    listDidDismiss();
                }
            });
        }

        return mListPopupWindow;
    }

    //弹窗消失处理
    private void listDidDismiss(){
        if(positionIsValid(mSelectedPosition)){
            boolean keep = mKeepSelectedAfterDismissList;
            if(mOnDropDownTabUIHandler != null){
                keep = mOnDropDownTabUIHandler.shouldKeepSelectedAfterDismissList(mTabInfos.get(mSelectedPosition), this);
            }
            if(!keep){
                TabInfo tabInfo = mTabInfos.get(mSelectedPosition);
                deselectTab();
                if(mOnDropDownTapSelectedListener != null){
                    mOnDropDownTapSelectedListener.onDropDownTabUnselected(tabInfo, this);
                }
            }
        }
    }

    public void setSelectedIndicatorColor(int selectedIndicatorColor) {
        if(mSelectedIndicatorColor != selectedIndicatorColor){
            mSelectedIndicatorColor = selectedIndicatorColor;
            mSelectedIndicator.setBackgroundColor(mSelectedIndicatorColor);

        }
    }

    ///设置分割线颜色
    public void setDividerColor(@ColorInt int dividerColor) {
        if (mDividerColor != dividerColor) {
            mDividerColor = dividerColor;
        }
    }

    ///设置分割线宽度
    public void setDividderWidth(int dividderWidth){
        if(dividderWidth != mDividerWidth){
            mDividerWidth = dividderWidth;
        }
    }

    public void setSelectedIndicatorWidthPadding(int selectedIndicatorWidthPadding) {
        if(selectedIndicatorWidthPadding != mSelectedIndicatorWidthPadding){
            mSelectedIndicatorWidthPadding = selectedIndicatorWidthPadding;
            updateIndicator();
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

    public void setDividerHeight(int dividerHeight) {
        if(mDividerHeight != dividerHeight){
            mDividerHeight = dividerHeight;
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

    public void setOnDropDownTapSelectedListener(OnDropDownTapSelectedListener listener) {
        mOnDropDownTapSelectedListener = listener;
    }

    public void setListAnchor(View listAnchor) {
        mListAnchor = listAnchor;
        if(mListAnchor == null){
            mListAnchor = this;
        }
    }

    public void setOnDropDownTabUIHandler(OnDropDownTabUIHandler onDropDownTabUIHandler) {
        mOnDropDownTabUIHandler = onDropDownTabUIHandler;
    }

    //菜单 tab
    private static class Tab extends FrameLayout{

        //文字
        TextView mTextView;

        //分割线
        View mDivider;

        //位置
        int mPosition;

        public Tab(@NonNull Context context) {
            this(context, null);
        }

        public Tab(@NonNull Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Tab(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            mTextView = new TextView(context);
            mTextView.setMaxLines(1);
            mTextView.setEllipsize(TextUtils.TruncateAt.END);
            mTextView.setCompoundDrawablePadding(SizeUtil.pxFormDip(2, context));
            mTextView.setPadding(SizeUtil.pxFormDip(5, context), 0, SizeUtil.pxFormDip(5, context), 0);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mTextView.setLayoutParams(params);
            addView(mTextView);

            mDivider = new View(context);

            params = new FrameLayout.LayoutParams(1,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
            mDivider.setLayoutParams(params);
            addView(mDivider);
        }
    }

    //菜单按钮信息
    public static class TabInfo {

        //标题，如果为空，将使用 infos中第一个
        public String title;

        //在菜单中的位置
        public int position;

        //下拉列表中选中的item
        public int selectedPosition;

        //下拉菜单信息
        public ArrayList<ListInfo> infos;

        //选中的按钮背景颜色
        public @ColorInt int selectedBackgroundColor;

        //按钮文字右边图标
        public Drawable rightNormalIcon;

        //按钮右边图标（高亮）
        public Drawable rightSelectedIcon1;

        //按钮右边图标 (高亮 如果不为null，点击的时候与 rightSelectedIcon1 来回切换
        public Drawable rightSelectedIcon2;

        //当前显示的drawable
        public Drawable displayRightIcon;

        //获取要显示的标题
        public String getDisplayTitle() {
            if(infos != null && selectedPosition >= 0 && selectedPosition < infos.size()){
                ListInfo listInfo = infos.get(selectedPosition);
                return listInfo.title;
            }
            return title;
        }

        //获取要显示的图标
        public Drawable getDisplayIcon(boolean selected){

            Drawable drawable = rightNormalIcon;
            if(selected){
                if(rightSelectedIcon1 != null && rightSelectedIcon2 != null){
                    if(displayRightIcon == rightSelectedIcon1){
                        drawable = rightSelectedIcon2;
                    }else {
                        drawable = rightSelectedIcon1;
                    }
                }else if(rightSelectedIcon1 != null){
                    drawable = rightSelectedIcon1;
                }
            }

            if(drawable != null){
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }

            displayRightIcon = drawable;
            return drawable;
        }

        //是否可以显示下拉列表
        public boolean showListEnable(){
            return infos != null && infos.size() > 0;
        }
    }

    //按钮点击回调
    public interface OnDropDownTapSelectedListener{

        //选择某个tab
        void onDropDownTabSelected(TabInfo tabInfo, DropDownTabLayout tabLayout);

        //取消选择某个tab
        void onDropDownTabUnselected(TabInfo tabInfo, DropDownTabLayout tabLayout);

        //选择下拉菜单中的某个值
        void onDropDownTabListItemSelected(TabInfo tabInfo, DropDownTabLayout tabLayout);
    }

    //按钮UI回调
    public interface OnDropDownTabUIHandler{

        //选中一级菜单的时候是否需要显示下拉列表 default is 'false'，只有当选中的一级菜单是高亮状态的时候才显示下拉
        boolean shouldShowList(TabInfo tabInfo, DropDownTabLayout tabLayout);

        //是否可以选中某个一级菜单
        boolean shouldSelectTab(TabInfo tabInfo, DropDownTabLayout tabLayout);

        //是否保持选中 当列表关闭时
        boolean shouldKeepSelectedAfterDismissList(TabInfo tabInfo, DropDownTabLayout tabLayout);
    }
}
