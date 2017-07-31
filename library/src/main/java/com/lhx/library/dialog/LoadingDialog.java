package com.lhx.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;

import com.lhx.library.R;
import com.lhx.library.drawable.CornerBorderDrawable;

/**
 * 加载中弹窗
 */

public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
        initlization();
    }

    public LoadingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initlization();
    }

    public LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initlization();
    }

    //初始化
    private void initlization(){

        View contentView = View.inflate(getContext(), R.layout.loading_dialog, null);
        CornerBorderDrawable.setDrawable(contentView, 15, Color.parseColor("#4c4c4c"));

        setContentView(contentView);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}
