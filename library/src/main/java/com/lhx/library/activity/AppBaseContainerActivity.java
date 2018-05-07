package com.lhx.library.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lhx.library.bar.NavigationBar;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.util.DialogUtil;
import com.lhx.library.widget.AppBaseContainer;


/**
 * 基础视图activity 和 appBaseFragment 类似 不要通过 setContentView 设置内容视图
 */

public abstract class AppBaseContainerActivity extends AppBaseActivity implements AppBaseContainer.OnEventHandler {

    ///容器视图
    private AppBaseContainer mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(mContainer == null){
            ///创建容器视图
            mContainer = new AppBaseContainer(this);
            mContainer.setOnEventHandler(this);

            ///创建导航栏
            mContainer.setShowNavigationBar(showNavigationBar());

            setContentView(mContainer);
            //内容视图
            initialize(getLayoutInflater(), mContainer, savedInstanceState);
        }


        setName(getClass().getName());
    }

    @Override
    public final int getContentViewRes() {
        return 0;
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if(mContainer != view){
            mContainer.setContentView(view);
            return;
        }
        super.setContentView(view, params);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        mContainer.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        if(mContainer != view){
            mContainer.setContentView(view);
            return;
        }
        super.setContentView(view);
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

    //显示返回按钮
    public void setShowBackButton(boolean show){
        mContainer.setShowBackButton(show);
    }

    //设置标题
    public void setNavigationBarTitle(String title){
        mContainer.setTitle(title);
    }

    public String getNavigationBarTitle(){
        return mContainer.getTitle();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    public void backToActivity(@NonNull Class<? extends AppBaseActivity> activityClass){
        backTo(activityClass.getName());
    }

    public void backToActivity(@NonNull Class<? extends AppBaseActivity> activityClass, int resultCode){
        backTo(activityClass.getName(), resultCode);
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

    public NavigationBar getNavigationBar() {
        if(mContainer == null)
            return null;
        return mContainer.getNavigationBar();
    }

    public AppBaseContainer getContainer() {
        return mContainer;
    }

    ///获取子视图
    public <T extends View> T sea_findViewById(int resId){
        return mContainer.sea_findViewById(resId);
    }

    //启动一个带activity的fragment
    public void startFragment(Class<? extends AppBaseFragment> fragmentClass){
        startFragment(fragmentClass, null);
    }

    public void startFragment(Class<? extends AppBaseFragment> fragmentClass, Bundle bundle){
        startFragmentForResult(fragmentClass, 0, bundle);
    }

    public void startFragmentForResult(Class<? extends AppBaseFragment> fragmentClass, int requestCode){
        startFragmentForResult(fragmentClass, requestCode, null);
    }

    public void startFragmentForResult(Class<? extends AppBaseFragment> fragmentClass, int requestCode, Bundle bundle){

        Intent intent = AppBaseActivity.getIntentWithFragment(this, fragmentClass);
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
        finish();
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
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    //显示加载菊花
    public boolean isLoading() {
        return DialogUtil.isShowing();
    }

    public void setLoading(boolean loading) {
        if(loading){
            DialogUtil.showLoadingDialog(this, false, null);
        }else {
            DialogUtil.dismissLoadingDialog();
        }
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
}
