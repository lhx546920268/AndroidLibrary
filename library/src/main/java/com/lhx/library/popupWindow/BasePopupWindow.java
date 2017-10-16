package com.lhx.library.popupWindow;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.lhx.library.R;

/**
 * 基础弹窗
 */

public abstract class BasePopupWindow extends PopupWindow {

    protected Context mContext;

    //内容视图
    protected View mContentView;

    //点击弹窗以外的位置是否关闭弹窗
    private boolean mShouldDismissWhenTouchOutside = true;

    public void setShouldDismissWhenTouchOutside(boolean shouldDismissWhenTouchOutside) {
        mShouldDismissWhenTouchOutside = shouldDismissWhenTouchOutside;
    }

    public BasePopupWindow(Context context) {

        mContext = context;
        initialize();
    }

    ///初始化
    private void initialize(){

        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE && !BasePopupWindow.this.isFocusable()) {
                    //如果焦点不在popupWindow上，且点击了外面，不再往下dispatch事件：
                    //不做任何响应,不 dismiss popupWindow
                    return mShouldDismissWhenTouchOutside;
                }
                //否则default，往下dispatch事件:关掉popupWindow，
                return false;
            }
        });

        mContentView = getContentView();
        setContentView(mContentView);

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    ///获取内容视图
    public abstract @NonNull View getContentView();
}
