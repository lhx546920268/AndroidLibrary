package com.lhx.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.widget.AppBaseContainer;
import com.lhx.library.widget.OnSingleClickListener;

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
        mContainer.setContentView(getContentView());
        mContainer.setOnEventHandler(this);

        setContentView(mContainer);
    }

    @Override
    public void show() {
        super.show();

        //设置弹窗大小
        Window window = getWindow();
        if(window != null){
            onConfigure(window);

            setCancelable(true);
            setCanceledOnTouchOutside(true);

            View view = window.getDecorView();
            if(view != null){
                view.setPadding(0, 0, 0 , 0);
            }
        }
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

    /**
     * 配置弹窗信息
     * @param window 弹窗
     */
    public abstract void onConfigure(Window window);

    /**
     * 获取内容视图
     * @return 内容视图
     */
    public abstract @NonNull View getContentView();

    /**
     * 是否显示导航栏
     */
    public abstract boolean showNavigationBar();
}
