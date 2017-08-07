package com.lhx.library.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.activity.AppBaseActivity;
import com.lhx.library.bar.NavigationBar;
import com.lhx.library.dialog.LoadingDialog;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.widget.OnSingleClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("unchecked")
public abstract class AppBaseFragment extends Fragment {

    ///内容视图
    private View mContentView;

    ///内容容器
    private FrameLayout mContentContainer;

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
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d("AppBaseFragment", "onDestroy");
//    }
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
                        SizeUtil.pxFormDip(45.0f, getContext()));
                mContainer.addView(mNavigationBar, 0, layoutParams);
            }

            mContentContainer = (FrameLayout)mContainer.findViewById(R.id.content_container);
            initialize(inflater, container, savedInstanceState);

            if(mContentView.getLayoutParams() == null){

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
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
        setContentView(View.inflate(mContext, layoutResId, null));
    }

    //显示返回按钮
    public void setShowBackButton(boolean show){

        if(mNavigationBar != null){
            if(show){

                Drawable drawable = null;
                int icon = App.NavigationBarBackButtonIcon;
                String title = null;
                if(icon != 0){
                    drawable = ContextCompat.getDrawable(mContext, icon);
                }
                if(drawable == null){
                    title = App.NavigationBarBackButtonTitle;
                }

                Button button = mNavigationBar.setNavigationItem(title, null, NavigationBar
                        .NAVIGATIONBAR_ITEM_POSITION_LEFT);
                button.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        mActivity.finish();
                    }
                });

                if(drawable != null){
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    button.setCompoundDrawables(drawable, null, null, null);
                }
            }
        }
    }

    public NavigationBar getNavigationBar() {
        return mNavigationBar;
    }

    public LinearLayout getContainer() {
        return mContainer;
    }

    public FrameLayout getContentContainer(){
        return mContentContainer;
    }

    ///获取子视图
    public <T extends View> T findViewById(int resId){
        return (T)mContentView.findViewById(resId);
    }

    //启动一个带activity的fragment
    public void startActivity(Class fragmentClass){
        startActivity(fragmentClass, null);
    }

    public void startActivity(Class fragmentClass, Bundle bundle){
        startActivityForResult(fragmentClass, 0, bundle);
    }

    public void startActivityForResult(Class fragmentClass, int requestCode){
        startActivityForResult(fragmentClass, requestCode, null);
    }

    public void startActivityForResult(Class fragmentClass, int requestCode, Bundle bundle){
        Intent intent = AppBaseActivity.getIntentWithFragment(mContext, fragmentClass);
        if(bundle != null){
            intent.putExtras(bundle);
        }

        if(requestCode != 0){
            mActivity.startActivityForResult(intent, requestCode);
        }else {
            mActivity.startActivity(intent);
        }
    }

    ///重新载入页面 子类按需重写
    protected void onReloadPage(){

    }

    public void setPageLoading(boolean pageLoading){
        setPageLoading(pageLoading, pageLoading ? getString(R.string.common_page_loading_text) : null);
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

            }
            if(mPageLoading){
                mPageLoadingView = View.inflate(mContext, R.layout.common_page_loading, null);
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
        setPageLoadFail(pageLoadFail, 0, getString(R.string.common_page_load_fail_title),
                getString(R.string.common_page_load_fail_subtitle));
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
                mPageLoadFailView = View.inflate(mContext, R.layout.common_page_load_fail, null);
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
    public void setShowEmptyViwe(String text){

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

    //点击物理键
    public boolean onKeyDown(int keyCode, KeyEvent event) {

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


    ///获取bundle内容
    public String getExtraStringFromBundle(String key){

        Bundle nBundle = getBundle();
        if(nBundle == null) return "";
        return nBundle.getString(key);
    }

    public double getExtraDoubleFromBundle(String key){
        Bundle nBundle = getBundle();
        return nBundle.getDouble(key);
    }

    public int getExtraIntFromBundle(String key){
        Bundle nBundle = getBundle();
        return nBundle.getInt(key);
    }

    public long getExtraLongFromBundle(String key){
        Bundle nBundle = getBundle();
        return nBundle.getLong(key);
    }

    public boolean getExtraBooleanFromBundle(String key){
        Bundle nBundle = getBundle();
        return nBundle.getBoolean(key, false);
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
            return mActivity.getIntent().getExtras();
        }
        return null;
    }
}
