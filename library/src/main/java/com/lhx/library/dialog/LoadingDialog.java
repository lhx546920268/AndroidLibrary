package com.lhx.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.loading.LoadingView;

/**
 * 加载中弹窗
 */

public class LoadingDialog extends Dialog {

    //延迟
    private long mDelay;

    public LoadingDialog(@NonNull Context context, long delay) {
        super(context, R.style.Theme_dialog_loading);
        mDelay = delay;
        initialization();
    }

    public LoadingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initialization();
    }

    public LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initialization();
    }

    //初始化
    private void initialization(){

        LoadingView contentView;
        if(App.loadViewClass != null){
            try {
                contentView = (LoadingView)App.loadViewClass.getConstructor(Context.class).newInstance(getContext());
            }catch (Exception e){
                throw new IllegalStateException("pageLoadingViewClass 无法通过context实例化");
            }
        }else {
            contentView = (LoadingView)View.inflate(getContext(), R.layout.default_loading_view, null);
        }

        contentView.setDelay(mDelay);
        setContentView(contentView);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        Window window = getWindow();
        if(window != null){
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
    }
}
