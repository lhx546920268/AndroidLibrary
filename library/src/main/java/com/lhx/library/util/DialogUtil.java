package com.lhx.library.util;

import android.content.Context;
import android.content.DialogInterface;

import com.lhx.library.dialog.LoadingDialog;

/**
 * 弹窗工具类
 */

public class DialogUtil {

    private static LoadingDialog mLoadingDialog;

    //隐藏加载菊花
    public static void dismissLoadingDialog(){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    public static boolean isShowing(){
        return mLoadingDialog != null && mLoadingDialog.isShowing();
    }

    public static void showLoadingDialog(Context context){
        showLoadingDialog(context, false, null);
    }

    //显示加载菊花
    public static void showLoadingDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener
            onCancelListener) {

        if(mLoadingDialog != null && mLoadingDialog.isShowing())
            return;
        mLoadingDialog = new LoadingDialog(context);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(cancelable);
        mLoadingDialog.setOnCancelListener(onCancelListener);
        mLoadingDialog.show();
    }
}
