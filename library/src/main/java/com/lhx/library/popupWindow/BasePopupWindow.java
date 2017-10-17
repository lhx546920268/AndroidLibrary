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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 基础弹窗
 */

public abstract class BasePopupWindow extends PopupWindow implements PopupWindow.OnDismissListener{

    protected Context mContext;

    //内容视图
    protected View mContentView;

    //点击弹窗以外的位置是否关闭弹窗
    private boolean mShouldDismissWhenTouchOutside = true;

    //弹窗消失回调
    private Set<OnDismissHandler> mOnDismissHandlers;

    //是否是自己设置消失回调
    private boolean mSetByThis;

    public void setShouldDismissWhenTouchOutside(boolean shouldDismissWhenTouchOutside) {
        mShouldDismissWhenTouchOutside = shouldDismissWhenTouchOutside;
    }

    public BasePopupWindow(Context context) {

        mContext = context;

        initialize();
    }

    //添加弹窗消失回调
    public void addOnDismissHandler(OnDismissHandler onDismissHandler){
        if(onDismissHandler == null)
            return;
        if(mOnDismissHandlers == null){
            mOnDismissHandlers = new HashSet<>();
        }
        mOnDismissHandlers.add(onDismissHandler);
    }

    //移除
    public void removeOnDismissHandler(OnDismissHandler onDismissHandler){
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

    ///初始化
    private void initialize(){

        mSetByThis = true;
        setOnDismissListener(this);
        mSetByThis = false;

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

    @Override
    public void onDismiss() {
        if(mOnDismissHandlers != null && mOnDismissHandlers.size() > 0){

            for(OnDismissHandler onDismissHandler : mOnDismissHandlers){
                onDismissHandler.onDismiss();
            }
        }
    }

    ///获取内容视图
    public abstract @NonNull View getContentView();

    //弹窗消失回调
    public interface OnDismissHandler{

        //弹窗消失
        void onDismiss();
    }
}
