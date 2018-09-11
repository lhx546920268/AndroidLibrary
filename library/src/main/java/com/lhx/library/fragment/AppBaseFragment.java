package com.lhx.library.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lhx.library.activity.ActivityStack;
import com.lhx.library.activity.AppBaseActivity;
import com.lhx.library.bar.NavigationBar;
import com.lhx.library.util.DialogUtil;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.widget.AppBaseContainer;

import java.io.Serializable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("unchecked")
public abstract class AppBaseFragment extends Fragment implements AppBaseContainer.OnEventHandler {

    ///容器视图
    private AppBaseContainer mContainer;

    ///关联的context
    protected Context mContext;

    //该activity 必须在 onActivityCreated 之后使用，否则为空，最好是使用 mContext
    protected Activity mActivity;

    public AppBaseFragment() {
        // Required empty public constructor
    }

    //是否已初始化
    private boolean mInit;

    public boolean isInit() {
        return mInit;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        if(!mActivity.isTaskRoot()){
            setShowBackButton(true);
        }
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
            mContainer = new AppBaseContainer(mContext);
            mContainer.setOnEventHandler(this);

            ///创建导航栏
            mContainer.setShowNavigationBar(showNavigationBar());

            //内容视图
            initialize(inflater, mContainer, savedInstanceState);
            mInit = true;
        }

        return mContainer;
    }

    ///子类可重写这个方法设置 contentView
    protected abstract void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState);

    ///是否需要显示导航栏
    protected boolean showNavigationBar(){
        return true;
    }

    ///获取内容视图
    protected View getContentView(){
        return  mContainer.getContentView();
    }

    protected void setContentView(View contentView) {
        mContainer.setContentView(contentView);
    }

    protected void setContentView(@LayoutRes int layoutResId){
        mContainer.setContentView(layoutResId);
    }

    //显示返回按钮
    public void setShowBackButton(boolean show){
        mContainer.setShowBackButton(show);
    }

    //设置标题
    public void setTitle(String title){
        mContainer.setTitle(title);
    }

    public String getTitle(){
        return mContainer.getTitle();
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

    public void backToFragment(@NonNull Class<? extends AppBaseFragment> fragmentClass){
        backTo(fragmentClass.getName());
    }

    public void backToFragment(@NonNull Class<? extends AppBaseFragment> fragmentClass, int resultCode){
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
        ActivityStack.finishActivities(toName, resultCode);
    }

    /**
     * 返回到底部
     */
    public void backToRoot(){
        ActivityStack.finishActivitiesToRoot();
    }

    public NavigationBar getNavigationBar() {
        if(mContainer == null)
            return null;
        return mContainer.getNavigationBar();
    }

    public AppBaseContainer getContainer() {
        return mContainer;
    }


    ///获取子视图
    public <T extends View> T findViewById(int resId){
        return mContainer.sea_findViewById(resId);
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

    /**
     * 页面加载失败后重新加载 子类可重写
     */
    protected void onReloadPage() {

    }

    /**
     * 点击页面加载失败视图
     */
    @Override
    public final void onClickPageLoadFai() {
        onReloadPage();
    }

    /**
     * 点击返回按钮
     */
    @Override
    public final void onBack() {
        back();
    }

    /**
     * 页面加载视图显示
     *
     * @param pageLoadingView 页面加载视图
     * @param params          页面加载视图布局参数
     */
    @Override
    public void onPageLoadingShow(View pageLoadingView, RelativeLayout.LayoutParams params) {

    }

    /**
     * 页面加载失败视图显示
     *
     * @param pageLoadFailView 页面加载失败视图
     * @param params           布局参数
     */
    @Override
    public void onPageLoadFailShow(View pageLoadFailView, RelativeLayout.LayoutParams params) {

    }

    /**
     * 空视图显示
     *
     * @param emptyView 空视图
     * @param params    布局参数
     */
    @Override
    public void onShowEmptyView(View emptyView, RelativeLayout.LayoutParams params) {

    }

    //打开activity 不要动画
    public void closeAnimate(){
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void setPageLoading(boolean pageLoading){
        mContainer.setPageLoading(pageLoading);
    }

    /**
     * 设置页面载入
     * @param pageLoading 是否载入
     * @param loadingText 载入文字
     */
    public void setPageLoading(boolean pageLoading, String loadingText){
        mContainer.setPageLoading(pageLoading, loadingText);
    }

    public boolean isPageLoading(){
        return mContainer.isPageLoading();
    }

    public void setLoading(boolean loading){
        mContainer.setLoading(loading, "加载中...");
    }

    public void setLoading(boolean loading, String text){
        mContainer.setLoading(loading, text);
    }

    public void setPageLoadFail(boolean pageLoadFail){
        mContainer.setPageLoadFail(pageLoadFail);
    }

    /**
     * 设置页面载入失败
     * @param pageLoadFail 是否载入失败
     * @param logoResId logo
     * @param title 标题
     * @param subtitle 副标题
     */
    public void setPageLoadFail(boolean pageLoadFail, @DrawableRes int logoResId, String title, String subtitle){
        mContainer.setPageLoadFail(pageLoadFail, logoResId, title, subtitle);
    }

    public boolean isPageLoadFail(){
        return mContainer.isPageLoadFail();
    }

    public void setShowEmptyView(String text){
        mContainer.setShowEmptyView(true, text, 0);
    }

    public void showEmptyView(boolean show, String text){
        mContainer.setShowEmptyView(show, text, 0);
    }

    /**
     * 设置显示空视图
     * @param text 显示的信息
     * @param iconRes 图标logo
     */
    public void setShowEmptyView(boolean show, String text, @DrawableRes int iconRes){
        mContainer.setShowEmptyView(show, text, iconRes);
    }

    //显示空视图
    public void setShowEmptyView(boolean show, @LayoutRes int layoutRes){
        mContainer.setShowEmptyView(show, layoutRes);
    }

    public void setShowEmptyView(boolean show, View emptyView){
        mContainer.setShowEmptyView(show, emptyView);
    }

    //设置底部视图
    public void setBottomView(View bottomView){
        mContainer.setBottomView(bottomView);
    }

    public void setBottomView(View bottomView, int height){
        mContainer.setBottomView(bottomView, height);
    }

    public void setBottomView(@LayoutRes int res){
        mContainer.setBottomView(res);
    }

    public View getBottomView() {
        if(mContainer == null)
            return null;
        return mContainer.getBottomView();
    }

    public void setTopView(View topView){
        mContainer.setTopView(topView);
    }

    //设置顶部视图
    public void setTopView(View topView, int height){
        mContainer.setTopView(topView, height);
    }

    public void setTopView(@LayoutRes int res){
        mContainer.setTopView(res);
    }

    public View getTopView() {
        if(mContainer == null)
            return null;
        return mContainer.getTopView();
    }

    //点击物理键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Activity activity = getActivity();
        if(keyCode == KeyEvent.KEYCODE_BACK && !activity.isTaskRoot()){
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

    public int getDimenIntValue(@DimenRes int dimen){
        return mContext.getResources().getDimensionPixelOffset(dimen);
    }

    public int getInteger(@IntegerRes int intValue){
        return mContext.getResources().getInteger(intValue);
    }

    public int getDimensionPixelSize(@DimenRes int dimen){
        return mContext.getResources().getDimensionPixelSize(dimen);
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
