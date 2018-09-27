package com.lhx.library.scan.util;

import android.hardware.Camera;
import android.os.Handler;

//相机预览回调
public class CameraPreviewCallback implements Camera.PreviewCallback {

    //解码线程
    private ScanDecoder mDecoder;

    //事件
    private Handler mHandler;

    //是否正在解码
    private boolean mDecoding;

    //相机管理
    private CameraManager mCameraManager;

    public CameraPreviewCallback(Handler handler, CameraManager cameraManager) {
        mHandler = handler;
        mCameraManager = cameraManager;
    }

    //开始解码
    public void startDecode(){
        if(mDecoder == null){
            mDecoder = new ScanDecoder(mHandler, mCameraManager);
            mDecoder.start();
        }
        mDecoding = false;
    }

    //停止解码
    public void stopDecode(){
        if(mDecoder != null){
            mDecoder.stopDecode();
            mDecoder = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if(!mCameraManager.isAutoFocusing()){
            mCameraManager.autoFocus();
        }

        if(!mDecoding && mDecoder != null){
            mDecoding = true;
            mDecoder.decode(data);
        }
    }
}
