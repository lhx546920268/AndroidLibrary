package com.lhx.library.refresh;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.drawable.LoadingDrawable;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * 默认下拉刷新控件
 */

public class DefaultPtrFrameLayout extends PtrFrameLayout implements PtrUIHandler {

    //内容视图
    private PtrUIHandler mPtrUIHandler;

    //头部
    protected View mHeader;

    //滑动回调
    private DefaultPtrFrameLayout.PtrFrameLayoutOnScrollHandler mPtrFrameLayoutOnScrollHandler;

    public void setPtrFrameLayoutOnScrollHandler(PtrFrameLayoutOnScrollHandler ptrFrameLayoutOnScrollHandler) {
        mPtrFrameLayoutOnScrollHandler = ptrFrameLayoutOnScrollHandler;
    }

    public DefaultPtrFrameLayout(Context context) {
        this(context, null);
    }

    public DefaultPtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultPtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if(App.refreshHeaderClass != null){
            try {
                mHeader = App.refreshHeaderClass.getConstructor(Context.class).newInstance(context);
                setHeaderView(mHeader);
            }catch (Exception e){
                throw new IllegalStateException("refreshHeaderClass 无法通过context实例化");
            }
        }else {
            DefaultRefreshHeader header = (DefaultRefreshHeader)LayoutInflater.from(context).inflate(R.layout
                            .refresh_control_header, this,
                    false);
            mHeader = header;
            setHeaderView(header);
        }

        mPtrUIHandler = (PtrUIHandler)mHeader;

        addPtrUIHandler(this);
        setDurationToCloseHeader(1500);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mPtrUIHandler.onUIReset(frame);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mPtrUIHandler.onUIRefreshPrepare(frame);

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mPtrUIHandler.onUIRefreshBegin(frame);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mPtrUIHandler.onUIRefreshComplete(frame);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        mPtrUIHandler.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
        if(mPtrFrameLayoutOnScrollHandler != null){
            mPtrFrameLayoutOnScrollHandler.onScroll(ptrIndicator);
        }
    }

    //下拉刷新滑动监听
    public interface PtrFrameLayoutOnScrollHandler{

        //
        void onScroll(PtrIndicator ptrIndicator);
    }
}
