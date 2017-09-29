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
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.widget.OnSingleClickListener;

/**
 * 基础dailog
 */

public abstract class BaseDialog extends Dialog {

    protected Context mContext;

    //容器
    private FrameLayout mContainer;

    //内容
    private View mContentView;

    ///页面是否正在载入
    private boolean mPageLoading = false;
    private View mPageLoadingView;

    ///页面是否载入失败
    private boolean mPageLoadFail = false;
    private View mPageLoadFailView;

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.Theme_dialog_noTitle_noBackground);
        mContext = context;
        initialization();
    }

    //初始化
    private void initialization(){

        mContainer = new FrameLayout(mContext);
        mContentView = getContentView();
        mContentView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        mContainer.addView(mContentView);

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
        setPageLoading(pageLoading, pageLoading ? mContext.getString(R.string
                .common_page_loading_text) : null);
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
                mPageLoadingView = LayoutInflater.from(mContext).inflate(R.layout.common_page_loading, mContainer, false);
                TextView textView = (TextView)mPageLoadingView.findViewById(R.id.text_view);
                textView.setText(loadingText);

                onPageLoadingViewShow(mPageLoadingView);

                mContainer.addView(mPageLoadingView);
            }else if(mPageLoadingView != null){
                mContainer.removeView(mPageLoadingView);
                mPageLoadingView = null;
            }
        }
    }

    public void onPageLoadingViewShow(View pageLoadingView){

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
                mPageLoadFailView = LayoutInflater.from(mContext).inflate(R.layout.common_page_load_fail, mContainer, false);
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

                onPageLoadFailShow(mPageLoadFailView);

                mContainer.addView(mPageLoadFailView);

            }else if(mPageLoadFailView != null) {
                mContainer.removeView(mPageLoadFailView);
                mPageLoadFailView = null;
            }
        }
    }

    public void onPageLoadFailShow(View pageLoadFailView){

    }

    ///重新加载
    public void onReloadPage(){

    }

    public abstract void onConfigure(Window window);

    public abstract @NonNull View getContentView();
}
