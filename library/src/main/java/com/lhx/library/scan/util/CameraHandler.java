package com.lhx.library.scan.util;

import android.os.Handler;
import android.os.Message;

import com.google.zxing.Result;

//相机事件回调
public class CameraHandler extends Handler {

    //事件类型

    //聚焦
    public static final int MESSAGE_AUTO_FOCUS = 1;

    //解码成功
    public static final int MESSAGE_DECODE_SUCCESS = 2;

    //解码失败
    public static final int MESSAGE_DECODE_FAIL = 3;

    //相机管理
    private CameraManager mCameraManager;

    public CameraHandler(CameraManager cameraManager) {
        mCameraManager = cameraManager;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case MESSAGE_AUTO_FOCUS : {
                mCameraManager.autoFocus();
            }
            break;
            case MESSAGE_DECODE_FAIL : {
                mCameraManager.startDecode();
            }
            break;
            case MESSAGE_DECODE_SUCCESS : {
                mCameraManager.decodeSuccess((Result) msg.obj);
            }
            break;
        }
    }
}
