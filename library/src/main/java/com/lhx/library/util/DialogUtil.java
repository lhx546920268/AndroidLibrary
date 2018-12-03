package com.lhx.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import com.lhx.library.dialog.LoadingDialog;

/**
 * 弹窗工具类
 */

public class DialogUtil {

    private static LoadingDialog mLoadingDialog;

    //隐藏加载菊花
    public static void dismissLoadingDialog(){
        if(mLoadingDialog != null){
            if(mLoadingDialog.isShowing()){
                Context context = mLoadingDialog.getContext();
                Activity activity = ContextUtil.getActivity(context);
                if(activity != null){
                    if(activity.isFinishing())
                        return;

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())
                        return;
                }

                mLoadingDialog.dismiss();
            }
            mLoadingDialog = null;
        }
    }

    public static boolean isShowing(){
        return mLoadingDialog != null && mLoadingDialog.isShowing();
    }

    public static void showLoadingDialog(Context context){
        showLoadingDialog(context, 0,false, null);
    }

    //显示加载菊花
    public static void showLoadingDialog(Context context, long delay, boolean cancelable, DialogInterface
            .OnCancelListener
            onCancelListener) {

        if(mLoadingDialog != null && mLoadingDialog.isShowing())
            return;
        mLoadingDialog = new LoadingDialog(context, delay);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setOnCancelListener(onCancelListener);
        mLoadingDialog.show();
    }
}
