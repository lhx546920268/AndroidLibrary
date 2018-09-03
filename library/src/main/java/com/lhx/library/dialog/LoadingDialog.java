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

/**
 * 加载中弹窗
 */

public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.Theme_dialog_loading);
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

        View contentView;
        if(App.loadViewClass != null){
            try {
                contentView = App.loadViewClass.getConstructor(Context.class).newInstance(getContext());
            }catch (Exception e){
                throw new IllegalStateException("pageLoadingViewClass 无法通过context实例化");
            }
        }else {
            contentView = View.inflate(getContext(), R.layout.loading_view, null);
        }


        setContentView(contentView);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        Window window = getWindow();
        if(window != null){
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
    }
}
