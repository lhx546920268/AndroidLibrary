package com.lhx.library.widget;

import android.view.View;

/**
 * 单击，防止UI卡或者机器响应慢时出现多次点击
 */

public abstract class OnSingleClickListener implements View.OnClickListener {

    ///最后的点击时间
    private long mLastTouchTime = 0;

    @Override
    public void onClick(View v) {

        long time = System.currentTimeMillis();
        if(time - mLastTouchTime > 1000){
            mLastTouchTime = time;
            onSingleClick(v);
        }
    }

    public abstract void onSingleClick(View v);
}
