package com.lhx.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import com.lhx.library.R;

import java.util.HashSet;
import java.util.Set;

//弹窗
public class SeaDialog extends Dialog implements DialogInterface.OnDismissListener {

    protected Context mContext;

    //弹窗消失回调
    private Set<BaseDialog.OnDismissHandler> mOnDismissHandlers;

    //是否是自己设置消失回调
    private boolean mSetByThis;

    public SeaDialog(@NonNull Context context) {
        super(context, R.style.Theme_dialog_noTitle_noBackground);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        mContext = context;
        mSetByThis = true;
        setOnDismissListener(this);
        mSetByThis = false;

        //设置弹窗样式
        Window window = getWindow();
        if(window != null) {
            window.setDimAmount(0.4f);
            View view = window.getDecorView();
            if(view != null){
                view.setPadding(0, 0, 0 , 0);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        if(mOnDismissHandlers != null && mOnDismissHandlers.size() > 0){

            for(BaseDialog.OnDismissHandler onDismissHandler : mOnDismissHandlers){
                onDismissHandler.onDismiss(this);
            }
        }
    }

    //添加弹窗消失回调
    public void addOnDismissHandler(BaseDialog.OnDismissHandler onDismissHandler){
        if(onDismissHandler == null)
            return;
        if(mOnDismissHandlers == null){
            mOnDismissHandlers = new HashSet<>();
        }
        mOnDismissHandlers.add(onDismissHandler);
    }

    //移除
    public void removeOnDismissHandler(BaseDialog.OnDismissHandler onDismissHandler){
        if(onDismissHandler == null || mOnDismissHandlers == null)
            return;
        mOnDismissHandlers.remove(onDismissHandler);
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        if(!mSetByThis){
            try {
                throw new IllegalAccessException("请使用addOnDismissHandler");
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
            return;
        }

        super.setOnDismissListener(onDismissListener);
    }

    //弹窗消失回调
    public interface OnDismissHandler{

        //弹窗消失
        void onDismiss(DialogInterface dialog);
    }
}
