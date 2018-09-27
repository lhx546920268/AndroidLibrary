package com.lhx.library.scan.util;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.HashMap;
import java.util.Vector;

//扫描图片解码
public class ScanDecoder extends Thread{


    //事件
    private Handler mHandler;

    //相机管理
    private CameraManager mCameraManager;

    //解码器
    private MultiFormatReader mReader;

    //是否已停止
    private boolean mStop;

    ///
    private Looper mLooper;

    public ScanDecoder(Handler handler, CameraManager cameraManager) {

        mCameraManager = cameraManager;
        mHandler = handler;

        HashMap<DecodeHintType, Object> hints = new HashMap<>();

        //解码格式
        Vector<BarcodeFormat> formats = new Vector<>();

        //条形码
        formats.add(BarcodeFormat.UPC_A);
        formats.add(BarcodeFormat.UPC_E);
        formats.add(BarcodeFormat.EAN_13);
        formats.add(BarcodeFormat.EAN_8);
        formats.add(BarcodeFormat.RSS_14);
        formats.add(BarcodeFormat.CODE_39);
        formats.add(BarcodeFormat.CODE_93);
        formats.add(BarcodeFormat.CODE_128);

        //二维码
        formats.add(BarcodeFormat.QR_CODE);

        hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);

        mReader = new MultiFormatReader();
        mReader.setHints(hints);
    }

    @Override
    public void run() {

        Looper.prepare();

        mLooper = Looper.myLooper();
        Looper.loop();
    }

    //停止解码
    public void stopDecode(){
        mStop = true;
        if(mLooper != null){
            mLooper.quit();
            mLooper = null;
        }
    }

    //获取解码所需的
    public YUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {

        Rect rect = mCameraManager.getScanRect();
        int previewFormat = mCameraManager.getPreviewFormat();

        switch (previewFormat) {
            case PixelFormat.YCbCr_420_SP :
            case PixelFormat.YCbCr_422_SP :
                return new YUVLuminanceSource(data, width, height,
                        rect.left, rect.top, rect.width(), rect.height());
            default :
                String previewFormatString = mCameraManager.getPreviewFormatString();
                if ("yuv420p".equals(previewFormatString))
                {
                    return new YUVLuminanceSource(data, width, height,
                            rect.left, rect.top, rect.width(), rect.height());
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: "
                + previewFormat + '/' + mCameraManager.getPreviewFormatString());
    }

    //解码
    void decode(byte[] data) {

        Result rawResult = null;

        Camera.Size size = mCameraManager.getPreviewSize();
        int width = 0;
        int height = 0;
        if(size != null){
            width = size.width;
            height = size.height;
        }

        //将相机获取的图片数据转化为binaryBitmap格式
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width;
        width = height;
        height = tmp;

        YUVLuminanceSource source = buildLuminanceSource(rotatedData, width, height);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            //解析转化后的图片，得到结果
            rawResult = mReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            // continue
        } finally {
            mReader.reset();
        }

        if(!mStop){
            if (rawResult != null) {

                //如果解析结果不为空，就是解析成功了，则发送成功消息，将结果放到message中
                Message message = mHandler.obtainMessage(CameraHandler.MESSAGE_DECODE_SUCCESS);
                message.obj = rawResult;
                message.sendToTarget();
            } else {
                //解码失败
                mHandler.sendEmptyMessage(CameraHandler.MESSAGE_DECODE_FAIL);
            }
        }
    }
}
