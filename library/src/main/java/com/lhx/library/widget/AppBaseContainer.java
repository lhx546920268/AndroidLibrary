package com.lhx.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.bar.NavigationBar;
import com.lhx.library.loading.DefaultLoadingView;
import com.lhx.library.loading.LoadingView;
import com.lhx.library.loading.PageLoadingView;

/**
 * 基础视图容器
 */

@SuppressWarnings("unchecked")
public class AppBaseContainer extends RelativeLayout {

    //loading 范围
    public static final int OVERLAY_AREA_FAIL_TOP = 1; //失败视图将遮住header
    public static final int OVERLAY_AREA_FAIL_BOTTOM = 1 << 1; //失败视图将遮住footer
    public static final int OVERLAY_AREA_PAGE_LOADING_TOP = 1 << 2; //pageLoading视图将遮住header
    public static final int OVERLAY_AREA_PAGE_LOADING_BOTTOM = 1 << 3; //pageLoading视图将遮住footer
    public static final int OVERLAY_AREA_EMPTY_TOP = 1 << 4; //空视图将遮住header
    public static final int OVERLAY_AREA_EMPTY_BOTTOM = 1 << 5; //空视图将遮住footer

    //内容视图
    private View mContentView;

    //导航栏
    private NavigationBar mNavigationBar;

    //关联的context
    protected Context mContext;

    //页面是否正在载入
    private boolean mPageLoading = false;
    private View mPageLoadingView;

    //显示菊花
    private boolean mLoading = false;
    private LoadingView mLoadingView;

    //页面是否载入失败
    private boolean mPageLoadFail = false;
    private View mPageLoadFailView;

    //显示空视图信息
    private View mEmptyView;

    //添加固定在底部的视图
    private View mBottomView;

    //添加固定在顶部的视图
    private View mTopView;

    //事件回调
    private OnEventHandler mOnEventHandler;

    //加载视图覆盖区域 默认都不覆盖
    public int mOverlayArea = 0;

    public AppBaseContainer(Context context) {
        this(context, null);
    }

    public AppBaseContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppBaseContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initialize();
    }

    public void setOverlayArea(int overlayArea) {
        mOverlayArea = overlayArea;
    }

    //初始化
    private void initialize(){

        setBackgroundColor(Color.WHITE);

    }

    public OnEventHandler getOnEventHandler() {
        return mOnEventHandler;
    }

    public void setOnEventHandler(OnEventHandler onEventHandler) {
        mOnEventHandler = onEventHandler;
    }

    //设置是否需要显示导航栏
    public void setShowNavigationBar(boolean show){
        if(show){
            //创建导航栏
            if(mNavigationBar == null){
                mNavigationBar = new NavigationBar(mContext);
                LayoutParams layoutParams = new LayoutParams(LayoutParams
                        .MATCH_PARENT, mContext.getResources().getDimensionPixelOffset(R.dimen.navigation_bar_height));
                mNavigationBar.setId(R.id.app_base_fragment_navigation_bar_id);
                addView(mNavigationBar, 0, layoutParams);
                layoutChildren();
            }

            mNavigationBar.setVisibility(VISIBLE);
        }else {
            if(mNavigationBar != null){
                mNavigationBar.setVisibility(GONE);
            }
        }
    }

    public boolean isNavigationBarShowing(){
        return mNavigationBar != null && mNavigationBar.getVisibility() == VISIBLE;
    }

    //获取内容视图
    public View getContentView(){
        return  mContentView;
    }

    public void setContentView(View contentView) {
        if(mContentView != null){
            throw new RuntimeException(this.getClass().getName() + "contentView 已经设置");
        }
        mContentView = contentView;

        mContentView.setId(R.id.app_base_fragment_content_id);
        LayoutParams layoutParams;
        if(mContentView.getLayoutParams() instanceof LayoutParams){
            layoutParams = (LayoutParams)mContentView.getLayoutParams();
        }else {
            layoutParams = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
        }
        layoutParams.alignWithParent = true;
        layoutParams.addRule(BELOW, R.id.app_base_fragment_navigation_bar_id);

        addView(mContentView, 0, layoutParams);
        layoutChildren();
    }

    public void setContentView(@LayoutRes int layoutResId){
        setContentView(LayoutInflater.from(mContext).inflate(layoutResId, null, false));
    }

    //获取子视图
    public <T extends View> T sea_findViewById(int resId){
        T view = (T)mContentView.findViewById(resId);
        if(view == null && mBottomView != null){
            view = (T)mBottomView.findViewById(resId);
        }

        if(view == null && mTopView != null){
            view = (T)mTopView.findViewById(resId);
        }

        if(view == null){
            view = (T)findViewById(resId);
        }

        return view;
    }

    //设置topView 浮动
    public void setTopViewFloat(boolean flag){
        if(mContentView != null && mTopView != null){
            LayoutParams params = (LayoutParams)mContentView.getLayoutParams();
            if(flag){
                params.addRule(BELOW, R.id.app_base_fragment_navigation_bar_id);
            }else {
                params.addRule(BELOW, mTopView.getId());
            }
        }
    }

    //重新布局子视图
    private void layoutChildren(){

        if(mContentView != null){

            LayoutParams params = (LayoutParams)mContentView.getLayoutParams();
            if(mTopView != null){
                params.addRule(BELOW, 0);
                params.addRule(BELOW, mTopView.getId());
            }else {
                params.addRule(BELOW, R.id.app_base_fragment_navigation_bar_id);
            }

            if(mBottomView != null){
                params.addRule(ABOVE, mBottomView.getId());
            }
        }
    }

    //显示返回按钮
    public void setShowBackButton(boolean show){

        if(mNavigationBar != null){
            TextView textView = mNavigationBar.setShowBackButton(show);
            if(textView != null){
                textView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        if(mOnEventHandler != null){
                            mOnEventHandler.onBack();
                        }
                    }
                });
            }
        }
    }

    //设置标题
    public void setTitle(String title){
        if(mNavigationBar != null){
            mNavigationBar.setTitle(title);
        }
    }

    public String getTitle(){
        if(mNavigationBar != null){
            return mNavigationBar.getTitle();
        }
        return "";
    }


    public NavigationBar getNavigationBar() {
        return mNavigationBar;
    }

    public void setPageLoading(boolean pageLoading){
        setPageLoading(pageLoading, pageLoading ? "正在载入..." : null);
    }

    /**
     * 设置页面载入
     * @param pageLoading 是否载入
     * @param loadingText 载入文字
     */
    public void setPageLoading(boolean pageLoading, String loadingText){
        if(mPageLoading != pageLoading){
            mPageLoading = pageLoading;

            if(mPageLoading){
                if(App.pageLoadingViewClass != null){
                    try {
                        mPageLoadingView = App.pageLoadingViewClass.getConstructor(Context.class).newInstance(mContext);
                    }catch (Exception e){
                        throw new IllegalStateException("pageLoadingViewClass 无法通过context实例化");
                    }

                }else {
                    mPageLoadingView = LayoutInflater.from(mContext).inflate(R.layout.common_page_loading,
                            this, false);
                }

                if(mPageLoadingView instanceof PageLoadingView){
                    ((PageLoadingView) mPageLoadingView).getTextView().setText(loadingText);
                }

                mPageLoadingView.setClickable(true);
                LayoutParams params = (LayoutParams)mPageLoadingView.getLayoutParams();
                if(params == null){
                    params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    mPageLoadingView.setLayoutParams(params);
                }
                params.alignWithParent = true;

                if(mTopView == null || (mOverlayArea & OVERLAY_AREA_PAGE_LOADING_TOP) == OVERLAY_AREA_PAGE_LOADING_TOP){
                    params.addRule(BELOW, R.id.app_base_fragment_navigation_bar_id);
                }else {
                    params.addRule(BELOW, mTopView.getId());
                }

                if(mBottomView == null || (mOverlayArea & OVERLAY_AREA_PAGE_LOADING_BOTTOM) ==
                        OVERLAY_AREA_PAGE_LOADING_BOTTOM){
                    params.addRule(ALIGN_PARENT_BOTTOM);
                }else {
                    params.addRule(ABOVE, mBottomView.getId());
                }

                if(mOnEventHandler != null){
                    mOnEventHandler.onPageLoadingShow(mPageLoadingView, params);
                }

                addView(mPageLoadingView);
                if (mBottomView != null && (mOverlayArea & OVERLAY_AREA_PAGE_LOADING_BOTTOM) !=
                        OVERLAY_AREA_PAGE_LOADING_BOTTOM) {
                    mBottomView.bringToFront();
                }

                setPageLoadFail(false);
            }else if(mPageLoadingView != null){
                removeView(mPageLoadingView);
                mPageLoadingView = null;
            }
        }
    }

    public boolean isPageLoading(){
        return mPageLoading;
    }

    public View getPageLoadFailView() {
        return mPageLoadFailView;
    }

    public void setPageLoadFail(boolean pageLoadFail){
        setPageLoadFail(pageLoadFail, 0, "加载失败", "轻触屏幕重新加载");
    }

    /**
     * 设置页面载入失败
     * @param pageLoadFail 是否载入失败
     * @param logoResId logo
     * @param title 标题
     * @param subtitle 副标题
     */
    public void setPageLoadFail(boolean pageLoadFail, @DrawableRes int logoResId, String title, String subtitle) {
        if (mPageLoadFail != pageLoadFail) {
            mPageLoadFail = pageLoadFail;

            if (mPageLoadFail) {
                mPageLoadFailView = LayoutInflater.from(mContext).inflate(R.layout.common_page_load_fail,
                        this, false);
                mPageLoadFailView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        setPageLoadFail(false);

                        if (mOnEventHandler != null) {
                            mOnEventHandler.onClickPageLoadFai();
                        }
                    }
                });

                ImageView imageView = mPageLoadFailView.findViewById(R.id.logo);
                if(imageView != null){
                    imageView.setImageResource(logoResId);
                }

                TextView textView = mPageLoadFailView.findViewById(R.id.title);
                if(textView != null){
                    textView.setText(title);
                }

                textView = mPageLoadFailView.findViewById(R.id.subtitle);
                if(textView != null){
                    textView.setText(subtitle);
                }

                LayoutParams params = (LayoutParams)
                        mPageLoadFailView.getLayoutParams();
                params.alignWithParent = true;

                if(mTopView == null || (mOverlayArea & OVERLAY_AREA_FAIL_TOP) == OVERLAY_AREA_FAIL_TOP){
                    params.addRule(BELOW, R.id.app_base_fragment_navigation_bar_id);
                }else {
                    params.addRule(BELOW, mTopView.getId());
                }

                if(mBottomView == null || (mOverlayArea & OVERLAY_AREA_FAIL_BOTTOM) ==
                        OVERLAY_AREA_FAIL_BOTTOM){
                    params.addRule(ALIGN_PARENT_BOTTOM);
                }else {
                    params.addRule(ABOVE, mBottomView.getId());
                }

                if(mOnEventHandler != null){
                    mOnEventHandler.onPageLoadFailShow(mPageLoadFailView, params);
                }

                addView(mPageLoadFailView);

                setLoading(false, 0, null);
                setPageLoading(false);
                if (mBottomView != null && (mOverlayArea & OVERLAY_AREA_FAIL_BOTTOM) !=
                        OVERLAY_AREA_FAIL_BOTTOM) {
                    mBottomView.bringToFront();
                }

            } else if (mPageLoadFailView != null) {
                removeView(mPageLoadFailView);
                mPageLoadFailView = null;
            }
        }
    }

    public boolean isPageLoadFail(){
        return mPageLoadFail;
    }

    //显示菊花
    public void setLoading(boolean loading, long delay, String text){
        if(mLoading != loading){
            mLoading = loading;

            if(mLoading){
                if(App.loadViewClass != null){
                    try {
                        mLoadingView = (LoadingView)App.loadViewClass.getConstructor(Context.class).newInstance
                                (mContext);
                    }catch (Exception e){
                        throw new IllegalStateException("loadViewClass 无法通过context实例化");
                    }

                }else {
                    mLoadingView = (LoadingView)LayoutInflater.from(mContext).inflate(R.layout.default_loading_view,
                            this, false);
                }

                mLoadingView.setDelay(delay);
                if(mLoadingView instanceof DefaultLoadingView){
                    ((DefaultLoadingView) mLoadingView).getTextView().setText(text);
                }

                LayoutParams params = (LayoutParams)mLoadingView.getLayoutParams();
                if(params == null){
                    params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    mLoadingView.setLayoutParams(params);
                }
                params.alignWithParent = true;
                params.addRule(BELOW, R.id.app_base_fragment_navigation_bar_id);
                params.addRule(ALIGN_PARENT_BOTTOM);

                addView(mLoadingView);
            }else if(mLoadingView != null){
                removeView(mLoadingView);
                mLoadingView = null;
            }
        }
    }

    public boolean isLoading(){
        return mLoading;
    }

    /**
     * 设置显示空视图
     * @param text 显示的信息
     * @param iconRes 图标logo
     */
    public void setShowEmptyView(boolean show, String text, @DrawableRes int iconRes){

        View view = null;
        if(show){
            view = LayoutInflater.from(mContext).inflate(R.layout.common_empty_view, this, false);

            if(!TextUtils.isEmpty(text)){
                TextView textView = view.findViewById(R.id.text);
                if(textView != null){
                    textView.setText(text);
                }
            }

            if(iconRes != 0){
                ImageView imageView = view.findViewById(R.id.icon);
                if(imageView != null){
                    imageView.setImageResource(iconRes);
                }
            }
        }
        setShowEmptyView(show, view);
    }

    //显示空视图
    public void setShowEmptyView(boolean show, @LayoutRes int layoutRes){
        View view = null;
        if(show && layoutRes != 0.0){
            view = LayoutInflater.from(mContext).inflate(layoutRes, this, false);
        }
        setShowEmptyView(show, view);
    }

    //显示空视图
    public void setShowEmptyView(boolean show, View emptyView){

        if(show && emptyView != null){
            if(mEmptyView != null){
                removeView(mEmptyView);
            }
            mEmptyView = emptyView;
            mEmptyView.setClickable(true);

            if(isPageLoading()){
                setPageLoading(false);
            }

            if(isPageLoadFail()){
                setPageLoadFail(false);
            }

            LayoutParams params = (LayoutParams)
                    mEmptyView.getLayoutParams();
            if(params == null){
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mEmptyView.setLayoutParams(params);
            }
            params.alignWithParent = true;

            if(mTopView == null || (mOverlayArea & OVERLAY_AREA_EMPTY_TOP) == OVERLAY_AREA_EMPTY_TOP){
                params.addRule(BELOW, R.id.app_base_fragment_navigation_bar_id);
            }else {
                params.addRule(BELOW, mTopView.getId());
            }

            if(mBottomView == null || (mOverlayArea & OVERLAY_AREA_EMPTY_BOTTOM) ==
                    OVERLAY_AREA_EMPTY_BOTTOM){
                params.addRule(ALIGN_PARENT_BOTTOM);
            }else {
                params.addRule(ABOVE, mBottomView.getId());
            }

            if(mOnEventHandler != null){
                mOnEventHandler.onShowEmptyView(mEmptyView, params);
            }

            addView(mEmptyView);

            if(mBottomView != null && (mOverlayArea & OVERLAY_AREA_EMPTY_BOTTOM) !=
                    OVERLAY_AREA_EMPTY_BOTTOM){
                mBottomView.bringToFront();
            }
        }else {
            if(mEmptyView != null){
                removeView(mEmptyView);
                mEmptyView = null;
            }
        }
    }

    //设置底部视图
    public void setBottomView(View bottomView){
        setBottomView(bottomView, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setBottomView(View bottomView, int height){
        if(mBottomView != bottomView){
            if(mBottomView != null){
                removeView(mBottomView);
            }

            mBottomView = bottomView;

            if(mBottomView == null)
                return;

            LayoutParams params;
            if(mBottomView.getLayoutParams() != null && mBottomView.getLayoutParams() instanceof
                    LayoutParams){
                params = (LayoutParams)mBottomView.getLayoutParams();
            }else {
                params = new LayoutParams(LayoutParams.MATCH_PARENT,
                        height);
            }

            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mBottomView.setLayoutParams(params);
            mBottomView.setId(R.id.app_base_fragment_bottom_id);

            if(mBottomView.getParent() != this){
                if(mBottomView.getParent() != null){
                    ViewGroup parent = (ViewGroup)mBottomView.getParent();
                    parent.removeView(mBottomView);
                }

                addView(mBottomView);
            }

            layoutChildren();
        }
    }

    public void setBottomView(@LayoutRes int res){
        if(res != 0){
            setBottomView(LayoutInflater.from(mContext).inflate(res, this, false));
        }
    }

    public View getBottomView() {
        return mBottomView;
    }

    public void setTopView(View topView){
        setTopView(topView, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //设置顶部视图
    public void setTopView(View topView, int height){
        if(mTopView != topView){
            if(mTopView != null){
                removeView(mTopView);
            }

            mTopView = topView;

            if(mTopView == null)
                return;

            LayoutParams params;
            if(mTopView.getLayoutParams() != null && mTopView.getLayoutParams() instanceof LayoutParams){
                params = (LayoutParams)mTopView.getLayoutParams();
            }else {
                params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
            }

            params.addRule(BELOW, R.id.app_base_fragment_navigation_bar_id);
            mTopView.setLayoutParams(params);
            mTopView.setId(R.id.app_base_fragment_top_id);

            if(mTopView.getParent() != this){
                if(mTopView.getParent() != null){
                    ViewGroup parent = (ViewGroup)mTopView.getParent();
                    parent.removeView(mTopView);
                }

                addView(mTopView);
            }

            layoutChildren();
        }
    }

    public void setTopView(@LayoutRes int res){
        if(res != 0){
            setTopView(LayoutInflater.from(mContext).inflate(res, this, false), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public View getTopView() {
        return mTopView;
    }


    //事件回调
    public interface OnEventHandler{

        /**
         * 点击页面加载失败视图
         */
        void onClickPageLoadFai();

        /**
         * 点击返回按钮
         */
        void onBack();

        /**
         * 页面加载视图显示
         * @param pageLoadingView 页面加载视图
         * @param params 页面加载视图布局参数
         */
        void onPageLoadingShow(View pageLoadingView, RelativeLayout.LayoutParams params);

        /**
         * 页面加载失败视图显示
         * @param pageLoadFailView 页面加载失败视图
         * @param params 布局参数
         */
        void onPageLoadFailShow(View pageLoadFailView, RelativeLayout.LayoutParams params);

        /**
         * 空视图显示
         * @param emptyView 空视图
         * @param params 布局参数
         */
        void onShowEmptyView(View emptyView, RelativeLayout.LayoutParams params);
    }
}
