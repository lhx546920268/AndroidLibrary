package com.lhx.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.lhx.library.R;
import com.lhx.library.widget.AppBaseContainer;

/**
 * 基础dailog
 */

public abstract class BaseDialog extends Dialog implements AppBaseContainer.OnEventHandler{

    protected Context mContext;

    //容器
    private AppBaseContainer mContainer;

    public AppBaseContainer getContainer() {
        return mContainer;
    }

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.Theme_dialog_noTitle_noBackground);

        mContext = context;
        initialize();
    }

    //初始化
    private void initialize(){

        mContainer = new AppBaseContainer(mContext);
        mContainer.setShowNavigationBar(showNavigationBar());

        View contentView = getContentView(mContainer);
        if(!(contentView.getLayoutParams() instanceof AppBaseContainer.LayoutParams)){
            contentView.setLayoutParams(new AppBaseContainer.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        mContainer.setContentView(contentView);
        mContainer.setOnEventHandler(this);

        setContentView(mContainer);

        //设置弹窗大小
        Window window = getWindow();
        if(window != null){
            onConfigure(window);
            onConfigure(window, (AppBaseContainer.LayoutParams)mContainer.getContentView().getLayoutParams());

            setCancelable(true);
            setCanceledOnTouchOutside(true);

            View view = window.getDecorView();
            if(view != null){
                view.setPadding(0, 0, 0 , 0);
            }
        }
    }

    @Override
    public void show() {
        super.show();
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

    ///重新加载
    public void onReloadPage(){

    }

    /**
     * 点击页面加载失败视图
     */
    @Override
    public void onClickPageLoadFai() {
        onReloadPage();
    }

    /**
     * 点击返回按钮
     */
    @Override
    public void onBack() {
        dismiss();
    }

    /**
     * 页面加载视图显示
     *
     * @param pageLoadingView 页面加载视图
     * @param params          页面加载视图布局参数
     */
    @Override
    public void onPageLoadingShow(View pageLoadingView, AppBaseContainer.LayoutParams params) {

    }

    /**
     * 页面加载失败视图显示
     *
     * @param pageLoadFailView 页面加载失败视图
     * @param params           布局参数
     */
    @Override
    public void onPageLoadFailShow(View pageLoadFailView, AppBaseContainer.LayoutParams params) {

    }

    /**
     * 空视图显示
     *
     * @param emptyView 空视图
     * @param params    布局参数
     */
    @Override
    public void onShowEmptyView(View emptyView, AppBaseContainer.LayoutParams params) {

    }

    /**
     * 配置弹窗信息
     * @param window 弹窗
     */
    @Deprecated
    public void onConfigure(Window window){

    }

    /**
     * 配置弹窗信息
     * @param window 弹窗
     * @param contentViewLayoutParams 内容视图布局
     */
    public abstract void onConfigure(Window window, AppBaseContainer.LayoutParams contentViewLayoutParams);

    /**
     * 获取内容视图
     * @return 内容视图
     */
    public abstract @NonNull View getContentView(AppBaseContainer parent);

    /**
     * 是否显示导航栏
     */
    public abstract boolean showNavigationBar();
}
