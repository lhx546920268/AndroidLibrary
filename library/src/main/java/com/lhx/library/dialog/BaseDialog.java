package com.lhx.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lhx.library.R;

/**
 * 基础dailog
 */

public abstract class BaseDialog extends Dialog {

    //内容
    View mContentView;

    public BaseDialog(@NonNull Context context, @LayoutRes int layoutRes) {

        this(context, View.inflate(context, layoutRes, null));
    }

    public BaseDialog(@NonNull Context context, @NonNull View contentView) {
        super(context, R.style.Theme_dialog_noTitle_noBackground);

        mContentView = contentView;

        initialization();
    }

    public View getContentView() {
        return mContentView;
    }

    //初始化
    private void initialization(){
        setContentView(mContentView);

        //设置弹窗大小
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        onConfigure(layoutParams);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        window.getDecorView().setPadding(0, 0, 0 , 0);
        window.setAttributes(layoutParams);
    }

    public abstract void onConfigure(WindowManager.LayoutParams params);
}
