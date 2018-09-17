package com.lhx.library.scan.util;

import android.hardware.Camera;
import android.os.Handler;
import android.support.annotation.NonNull;

//相机自动聚焦 循环延迟调用 让相机聚焦
public class CameraAutoFocusCallback implements Camera.AutoFocusCallback {

    //自动聚焦间隔
    private long mAutoFocusInterval = 15000;

    //延迟
    private Handler mHandler;

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

        if(mHandler != null){
            mHandler.sendEmptyMessageDelayed(CameraHandler.MESSAGE_AUTO_FOCUS, mAutoFocusInterval);
        }
    }

    //开始聚焦
    public void startFocus(@NonNull Handler handler){
        mHandler = handler;
    }

    //是否已聚焦
    public boolean isAutoFocusing(){
        return mHandler != null;
    }

    //停止聚焦
    public void stopFocus(){
        if(mHandler != null){
            mHandler.removeMessages(CameraHandler.MESSAGE_AUTO_FOCUS);
            mHandler = null;
        }
    }
}

