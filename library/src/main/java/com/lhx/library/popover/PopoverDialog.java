package com.lhx.library.popover;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lhx.library.dialog.SeaDialog;
import com.lhx.library.util.AppUtil;
import com.lhx.library.util.SizeUtil;

//气泡弹窗
public abstract class PopoverDialog extends SeaDialog implements PopoverContainer.PopoverHandler {

    //气泡容器
    private PopoverContainer mContainer;

    public PopoverContainer getContainer() {
        return mContainer;
    }

    public PopoverDialog(@NonNull Context context) {
        super(context);

        mContainer = new PopoverContainer(mContext);
        mContainer.setContentView(getContentView(mContainer.getPopoverLayout()));

        int padding = SizeUtil.pxFormDip(10, mContext);
        mContainer.setPadding(padding, SizeUtil.pxFormDip(3, mContext), padding, 0);
        mContainer.setPopoverHandler(this);
        setContentView(mContainer);

        //设置弹窗大小
        Window window = getWindow();
        if(window != null){

            window.setDimAmount(0.0f);
            window.setWindowAnimations(0);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = SizeUtil.getWindowHeight(mContext);
            window.setAttributes(params);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dismiss(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPopoverShow() {

    }

    @Override
    public void onPopoverDismiss() {
        dismiss();
    }

    public void show(View clickView, boolean animate){
        mContainer.show(clickView, animate);
        show();
    }

    public void dismiss(boolean animate){
        mContainer.dismiss(animate);
    }

    public abstract @NonNull
    View getContentView(PopoverLayout parent);
}
