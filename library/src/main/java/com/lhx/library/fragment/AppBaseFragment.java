package com.lhx.library.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.activity.ActivityStack;
import com.lhx.library.activity.AppBaseActivity;
import com.lhx.library.bar.NavigationBar;
import com.lhx.library.dialog.LoadingDialog;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.widget.OnSingleClickListener;

import java.io.Serializable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("unchecked")
public abstract class AppBaseFragment extends Fragment {

    ///内容视图
    private View mContentView;

    ///内容容器
    private RelativeLayout mContentContainer;

    ///容器视图
    private LinearLayout mContainer;

    ///导航栏
    private NavigationBar mNavigationBar;

    ///关联的context
    protected Context mContext;
    protected Activity mActivity;

    ///页面是否正在载入
    private boolean mPageLoading = false;
    private View mPageLoadingView;

    ///页面是否载入失败
    private boolean mPageLoadFail = false;
    private View mPageLoadFailView;

    ///显示加载菊花
    private boolean mLoading = false;
    private LoadingDialog mLoadingDialog;

    //显示空视图信息
    private View mEmptyView;

    //添加固定在底部的视图
    private View mBottomView;

    //添加固定在顶部的视图
    private View mTopView;

    public AppBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
//        Log.d("AppBaseFragment", "onActivityCreated");
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d("AppBaseFragment", "onStart");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.d("AppBaseFragment", "onStop");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d("AppBaseFragment", "onResume");
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
//        Log.d("AppBaseFragment", "onAttach");
    }
//
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AppBaseFragment", "onDestroy");
    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Log.d("AppBaseFragment", "onDestroyView");
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        Log.d("AppBaseFragment", "onDetach");
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("AppBaseFragment", "onCreateView");
        if(mContainer == null){
            ///创建容器视图
            mContainer = (LinearLayout)inflater.inflate(R.layout.app_base_fragment, null);
            mContainer.setBackgroundColor(Color.WHITE);

            ///创建导航栏
            if(showNavigationBar())
            {
                mNavigationBar = new NavigationBar(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                        .MATCH_PARENT,
                        SizeUtil.pxFormDip(App.NavigationBarHeightDip, getContext()));
                mContainer.addView(mNavigationBar, 0, layoutParams);
            }

            mContentContainer = (RelativeLayout) mContainer.findViewById(R.id.content_container);
            initialize(inflater, mContentContainer, savedInstanceState);

            mContentView.setId(R.id.app_base_fragment_content_id);
            if(mContentView.getLayoutParams() == null){

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                mContentView.setLayoutParams(layoutParams);
            }


            mContentContainer.addView(mContentView, 0);
        }

        return mContainer;
    }

    ///子类可重写这个方法设置 contentView
    public abstract void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState);

    ///是否需要显示导航栏
    public boolean showNavigationBar(){
        return true;
    }

    ///获取内容视图
    public View getContentView(){
        return  mContentView;
    }

    public void setContentView(View contentView) {
        if(mContentView != null){
            throw new RuntimeException(this.getClass().getName() + "contentView 已经设置");
        }
        mContentView = contentView;
    }

    public void setContentView(@LayoutRes int layoutResId){
        setContentView(LayoutInflater.from(mContext).inflate(layoutResId, mContentContainer, false));
    }

    //显示返回按钮
    public void setShowBackButton(boolean show){

        if(mNavigationBar != null){
            TextView textView = mNavigationBar.setShowBackButton(show);
            if(textView != null){
                textView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        back();
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

    //返回
    public void back(){
        mActivity.finish();
    }

    public void back(int resultCode){
        mActivity.setResult(resultCode);
        mActivity.finish();
    }

    public void back(int resultCode, Intent data){
        mActivity.setResult(resultCode, data);
        mActivity.finish();
    }

    public void backToFragment(@NonNull Class fragmentClass){
        backTo(fragmentClass.getName());
    }

    public void backToFragment(@NonNull Class fragmentClass, int resultCode){
        backTo(fragmentClass.getName(), resultCode);
    }

    public void backTo(@NonNull String toName){
        backTo(toName, Integer.MAX_VALUE);
    }

    /**
     * 返回某个指定的 fragment
     * @param toName 对应的fragment类名 或者 activity类名 {@link AppBaseActivity#mName}
     * @param resultCode {@link android.app.Activity#setResult(int)}
     */
    public void backTo(@NonNull String toName, int resultCode){
        ActivityStack.finishActivies(toName, resultCode);
    }

    public NavigationBar getNavigationBar() {
        return mNavigationBar;
    }

    public LinearLayout getContainer() {
        return mContainer;
    }

    public RelativeLayout getContentContainer(){
        return mContentContainer;
    }

    ///获取子视图
    public <T extends View> T findViewById(int resId){
        T view = (T)mContentView.findViewById(resId);
        if(view == null && mBottomView != null){
            view = (T)mBottomView.findViewById(resId);
        }
        return view;
    }

    //启动一个带activity的fragment
    public void startActivity(Class<? extends AppBaseFragment> fragmentClass){
        startActivity(fragmentClass, null);
    }

    public void startActivity(Class<? extends AppBaseFragment> fragmentClass, Bundle bundle){
        startActivityForResult(fragmentClass, 0, bundle);
    }

    public void startActivityForResult(Class<? extends AppBaseFragment> fragmentClass, int requestCode){
        startActivityForResult(fragmentClass, requestCode, null);
    }

    public void startActivityForResult(Class<? extends AppBaseFragment> fragmentClass, int requestCode, Bundle bundle){

        Intent intent = AppBaseActivity.getIntentWithFragment(mContext, fragmentClass);
        if(bundle != null){
            bundle.remove(AppBaseActivity.FRAGMENT_STRING);
            intent.putExtras(bundle);
        }

        if(requestCode != 0){
            startActivityForResult(intent, requestCode);
        }else {
            startActivity(intent);
        }
    }

    //打开activity 不要动画
    public void closeAnimate(){
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    ///重新载入页面 子类按需重写
    protected void onReloadPage(){

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

            if(mPageLoadFail){
                setPageLoadFail(false);
            }
            if(mPageLoading){
                mPageLoadingView = LayoutInflater.from(mContext).inflate(R.layout.common_page_loading,
                        mContentContainer, false);
                TextView textView = (TextView)mPageLoadingView.findViewById(R.id.text_view);
                textView.setText(loadingText);

                mContentContainer.addView(mPageLoadingView);
            }else if(mPageLoadingView != null){
                mContentContainer.removeView(mPageLoadingView);
                mPageLoadingView = null;
            }
        }
    }

    public boolean isPageLoading(){
        return mPageLoading;
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
    public void setPageLoadFail(boolean pageLoadFail, @DrawableRes int logoResId, String title, String subtitle){
        if(mPageLoadFail != pageLoadFail){
            mPageLoadFail = pageLoadFail;

            if(mPageLoadFail){
                mPageLoadFailView = LayoutInflater.from(mContext).inflate(R.layout.common_page_load_fail,
                        mContentContainer, false);
                mPageLoadFailView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        setPageLoadFail(false);
                        onReloadPage();
                    }
                });

                ImageView imageView = (ImageView)mPageLoadFailView.findViewById(R.id.logo);
                imageView.setImageResource(logoResId);

                TextView textView = (TextView)mPageLoadFailView.findViewById(R.id.title);
                textView.setText(title);

                textView = (TextView)mPageLoadFailView.findViewById(R.id.subtitle);
                textView.setText(subtitle);
                mContentContainer.addView(mPageLoadFailView);

            }else if(mPageLoadFailView != null) {
                mContentContainer.removeView(mPageLoadFailView);
                mPageLoadFailView = null;
            }
        }
    }

    public boolean isPageLoadFail(){
        return mPageLoadFail;
    }

    //显示加载菊花
    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        if(loading != mLoading){
            mLoading = loading;
            if(mLoading){
                if(mLoadingDialog == null){
                    mLoadingDialog = new LoadingDialog(mContext);
                }

                mLoadingDialog.show();
            }else {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
            }
        }
    }

    /**
     * 设置显示空视图
     * @param text 显示的信息
     */
    public void setShowEmptyView(String text){

        if(mEmptyView == null){
            mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.common_empty_view, mContentContainer, false);
        }

        if(!TextUtils.isEmpty(text)){
            TextView textView = (TextView)mEmptyView.findViewById(R.id.text);
            textView.setText(text);
        }

        if(isPageLoading()){
            setPageLoading(false);
        }

        if(isPageLoadFail()){
            setPageLoadFail(false);
        }

        mContentContainer.addView(mEmptyView);
    }

    /**隐藏空视图
     */
    public void showEmptyView(boolean show,String text){

        if (show){

            setShowEmptyView(text);
        }
        else {

            if (mEmptyView != null){
                mContentContainer.removeView(mEmptyView);
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
                mContentContainer.removeView(mBottomView);
            }

            mBottomView = bottomView;

            if(mBottomView == null)
                return;

            RelativeLayout.LayoutParams params;
            if(mBottomView.getLayoutParams() != null && mBottomView.getLayoutParams() instanceof RelativeLayout
                    .LayoutParams){
                params = (RelativeLayout.LayoutParams)mBottomView.getLayoutParams();
            }else {
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mBottomView.setLayoutParams(params);
            mBottomView.setId(R.id.app_base_fragment_bottom_id);

            if(mBottomView.getParent() != mContentContainer){
                if(mBottomView.getParent() != null){
                    ViewGroup parent = (ViewGroup)mBottomView.getParent();
                    parent.removeView(mBottomView);
                }

                mContentContainer.addView(mBottomView);
            }

            params = (RelativeLayout.LayoutParams)mContentView.getLayoutParams();
            params.addRule(RelativeLayout.ABOVE, R.id.app_base_fragment_bottom_id);
            mContentView.setLayoutParams(params);
        }
    }

    public void setBottomView(@LayoutRes int res){
        if(res != 0){
            setBottomView(LayoutInflater.from(mContext).inflate(res, mContentContainer, false));
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
                mContentContainer.removeView(mTopView);
            }

            mTopView = topView;

            if(mTopView == null)
                return;

            RelativeLayout.LayoutParams params;
            if(mTopView.getLayoutParams() != null && mTopView.getLayoutParams() instanceof RelativeLayout
                    .LayoutParams){
                params = (RelativeLayout.LayoutParams)mTopView.getLayoutParams();
            }else {
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height);
            }

            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mTopView.setLayoutParams(params);
            mTopView.setId(R.id.app_base_fragment_top_id);

            if(mTopView.getParent() != mContentContainer){
                if(mTopView.getParent() != null){
                    ViewGroup parent = (ViewGroup)mTopView.getParent();
                    parent.removeView(mTopView);
                }

                mContentContainer.addView(mTopView);
            }

            params = (RelativeLayout.LayoutParams)mContentView.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.app_base_fragment_top_id);
            mContentView.setLayoutParams(params);
        }
    }

    public void setTopView(@LayoutRes int res){
        if(res != 0){
            setTopView(LayoutInflater.from(mContext).inflate(res, mContentContainer, false), 0);
        }
    }

    public View getTopView() {
        return mTopView;
    }

    //点击物理键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            back();
            return true;
        }
        return false;
    }

    //分发点击物理键事件
    public boolean dispatchKeyEvent(KeyEvent event) {

        return false;
    }

    //屏幕焦点改变
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    //获取颜色
    public @ColorInt int getColor(@ColorRes int colorRes){
        return ContextCompat.getColor(mContext, colorRes);
    }

    //获取drawable
    public Drawable getDrawable(@DrawableRes int drawableRes){
        return ContextCompat.getDrawable(mContext, drawableRes);
    }

    //获取dime
    public float getDimen(@DimenRes int dimen){
        return mContext.getResources().getDimension(dimen);
    }

    //获取px
    public int pxFromDip(float dip){
        return SizeUtil.pxFormDip(dip, mContext);
    }

    ///获取bundle内容
    public String getExtraStringFromBundle(String key){

        Bundle nBundle = getBundle();
        if(nBundle == null) return "";
        return nBundle.getString(key);
    }

    public double getExtraDoubleFromBundle(String key){
        Bundle nBundle = getBundle();
        if(nBundle != null){
            return nBundle.getDouble(key);
        }
        return 0;
    }

    public int getExtraIntFromBundle(String key){
        return getExtraIntFromBundle(key, 0);
    }

    public int getExtraIntFromBundle(String key, int defValue){
        Bundle nBundle = getBundle();
        if(nBundle != null){
            return nBundle.getInt(key, defValue);
        }
        return defValue;
    }

    public long getExtraLongFromBundle(String key){
        Bundle nBundle = getBundle();
        if(nBundle != null){
            return nBundle.getLong(key);
        }
        return 0;
    }

    public boolean getExtraBooleanFromBundle(String key, boolean def){
        Bundle nBundle = getBundle();
        if(nBundle != null){
            return nBundle.getBoolean(key, def);
        }
        return def;
    }

    public boolean getExtraBooleanFromBundle(String key){
        return getExtraBooleanFromBundle(key, false);
    }

    public List<String> getExtraStringListFromBundle(String key){
        Bundle nBundle = getBundle();
        return nBundle.getStringArrayList(key);
    }

    public Serializable getExtraSerializableFromBundle(String key){
        Bundle nBundle = getBundle();
        return nBundle.getSerializable(key);
    }

    //获取对应bundle
    public Bundle getBundle(){

        Bundle bundle = getArguments();
        if(bundle != null){
            return bundle;
        }

        if(mActivity != null){
            bundle = mActivity.getIntent().getExtras();
            if(bundle != null){
                return bundle;
            }
        }
        return null;
    }
}
