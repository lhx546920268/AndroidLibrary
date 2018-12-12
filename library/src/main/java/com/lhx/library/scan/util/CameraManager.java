package com.lhx.library.scan.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.zxing.Result;
import com.lhx.library.dialog.AlertController;
import com.lhx.library.util.AppUtil;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

//相机管理
public class CameraManager implements EasyPermissions.PermissionCallbacks{

    //权限
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1005;

    //相机
    private Camera mCamera;

    //自动聚焦回调
    private CameraAutoFocusCallback mAutoFocusCallback;

    //事件
    private CameraHandler mHandler;

    //
    private Context mContext;

    //预览大小
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    //预览回调
    private CameraPreviewCallback mPreviewCallback;

    //是否正在暂停
    private boolean mPausing;

    //是否正在预览
    private boolean mPreviewing;

    //关联的fragment
    private Fragment mFragment;

    //关联的
    private SurfaceTexture mSurfaceTexture;

    //回调
    private CameraManagerHandler mCameraManagerHandler;

    //扫码框
    private Rect mScanRect;

    //相机是否已创建
    private boolean mCameraInit;

    //是否已提示弹窗
    private boolean mAlert;

    public CameraManager(@NonNull Context context, @NonNull Fragment fragment) {
        mContext = context;
        mFragment = fragment;
        mAutoFocusCallback = new CameraAutoFocusCallback();
        mHandler = new CameraHandler(this);
        mPreviewCallback = new CameraPreviewCallback(mHandler, this);
    }

    public Rect getScanRect() {
        if(mScanRect == null){
            mScanRect = new Rect(mCameraManagerHandler.getScanRect(mSurfaceWidth, mSurfaceHeight));
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            float widthScale = (float)size.height / (float)mSurfaceWidth;
            float heightScale = (float)size.width / (float)mSurfaceHeight;
            mScanRect.left = (int)(mScanRect.left * widthScale);
            mScanRect.right = (int)(mScanRect.right * widthScale);
            mScanRect.top = (int)(mScanRect.top * heightScale);
            mScanRect.bottom = (int)(mScanRect.bottom * heightScale);
        }
        return mScanRect;
    }

    //设置预览大小
    public void setPreviewSize(int width, int height){
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    //设置预览视图
    public void setSurfaceTexture(@NonNull SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }

    //获取当前预览大小
    public Camera.Size getPreviewSize(){
        if(mCamera != null){
            return mCamera.getParameters().getPreviewSize();
        }
        return null;
    }

    public int getSurfaceWidth() {
        return mSurfaceWidth;
    }

    public int getSurfaceHeight() {
        return mSurfaceHeight;
    }

    //获取预览格式
    public int getPreviewFormat(){
        if(mCamera != null){
            return mCamera.getParameters().getPreviewFormat();
        }

        return 0;
    }

    //获取预览格式字符串
    public String getPreviewFormatString(){
        if(mCamera != null){
            return mCamera.getParameters().get("preview-format");
        }

        return null;
    }

    public void setCameraManagerHandler(CameraManagerHandler cameraManagerHandler) {
        mCameraManagerHandler = cameraManagerHandler;
    }

    public boolean isCameraInit() {
        return mCameraInit;
    }

    //打开相机
    public void openCamera(){

        String[] permissions = neededPermissions();
        if(EasyPermissions.hasPermissions(mContext, permissions)){
            if(mCamera == null){
                mCamera = Camera.open();
            }
            if(mCamera == null){
                AlertController.buildActionSheet(mContext, "摄像头不可用", "确定").show();
            }else {
                try {
                    mCamera.setPreviewTexture(mSurfaceTexture);
                    setOptimalPreviewSize(mSurfaceWidth, mSurfaceHeight);
                    mCamera.setDisplayOrientation(90);
                    mCamera.setPreviewCallback(mPreviewCallback);
                    mCamera.startPreview();
                    mPreviewing = true;
                    startDecode();
                    if(mCameraManagerHandler != null){
                        mCameraManagerHandler.onCameraStart();
                    }
                    mCameraInit = true;
                }catch (IOException e){
                    e.printStackTrace();
                    Log.d("ScanFragment", "相机预览失败");
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
            }
        }else {
            EasyPermissions.requestPermissions(mFragment, "需要打开相机扫描",
                    CAMERA_PERMISSION_REQUEST_CODE, permissions);
        }
    }

    //暂停相机
    public void onPause(){
        if(mCamera != null && mPreviewing){
            mPausing = true;
            mPreviewing = false;
            stopDecode();
            stopFocus();
            mCamera.stopPreview();
        }
    }

    //关闭相机
    public void onDestroy(){
        if(mCamera != null ){

            stopDecode();
            mPreviewing = false;
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mPausing = false;
            stopFocus();
            mCamera.release();
            mCamera = null;
        }
    }

    //重启相机
    public void onResume(){
        if(mPausing && mCamera != null && !mPreviewing){
            mCamera.startPreview();
            mPreviewing = true;
            startDecode();
            if(mCameraManagerHandler != null){
                mCameraManagerHandler.onCameraStart();
            }
        }
    }

    //聚焦
    public void autoFocus(){
        if(mCamera != null && mPreviewing){
            mAutoFocusCallback.startFocus(mHandler);
            mCamera.autoFocus(mAutoFocusCallback);
        }
    }

    //是否已聚焦
    public boolean isAutoFocusing(){
        return mAutoFocusCallback.isAutoFocusing();
    }

    //停止聚焦
    private void stopFocus(){
        mAutoFocusCallback.stopFocus();
    }

    //开始解码
    public void startDecode(){
        mPreviewCallback.startDecode();
    }

    //停止解码
    private void stopDecode(){
        mPreviewCallback.stopDecode();
    }

    //设置开灯状态
    public String setOpenLamp(boolean open){
        if(isCameraInit()){

            //判断设备是否支持闪光灯
            boolean support = false;
            FeatureInfo[] featureInfos = mContext.getPackageManager().getSystemAvailableFeatures();
            for(FeatureInfo info : featureInfos){
                if (PackageManager.FEATURE_CAMERA_FLASH.equals(info.name)){
                    support = true;
                    break;
                }
            }

            if(support){
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(open ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(parameters);
                return null;
            }

            return "该设备不支持开灯";
        }

        return "相机正在初始化";
    }

    //所需权限
    private String[] neededPermissions(){
        return new String[]{Manifest.permission.CAMERA};
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        openCamera();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        boolean alert = true;
        if(mCameraManagerHandler != null){
            alert = !mCameraManagerHandler.onPermissionsDenied(perms);
        }
        if(alert && !mAlert){
            mAlert = true;
            AlertController controller = AlertController.buildAlert(mContext, "扫一扫需要您的相机权限才能使用",
                    "取消", "去打开");
            controller.setOnItemClickListener(new AlertController.OnItemClickListener() {
                @Override
                public void onItemClick(AlertController controller, int index) {
                    mAlert = false;
                    if(index == 1){
                        AppUtil.openAppSettings(mContext);
                    }
                }
            });
            controller.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {

    }

    //设置最优预览尺寸
    private void setOptimalPreviewSize(int width, int height){

        if(mCamera != null){
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

            int landScapeWidth = width;
            int landScapeHeight = height;
            if(isPortrait()){
                landScapeWidth = height;
                landScapeHeight = width;
            }


            Camera.Size optimalSize = null;

            //如果存在相等的尺寸 直接设置
            for(Camera.Size size : sizes){
                if(size.width == landScapeWidth && size.height == landScapeHeight){
                    optimalSize = size;
                    break;
                }
            }

            if(optimalSize == null){
                //使用宽高比例最接近的
                float ratio = (float)landScapeWidth / (float)landScapeHeight;
                float differ = Float.MAX_VALUE;

                for(Camera.Size size : sizes){
                    float r = (float)size.width / (float)size.height;
                    float d = Math.abs(ratio - r);
                    if(d < differ){
                        differ = d;
                        optimalSize = size;
                    }
                }
            }

            if(optimalSize != null){
                parameters.setPreviewSize(optimalSize.width, optimalSize.height);
                mCamera.setParameters(parameters);
            }
        }
    }

    //调用扫码成功代理
    protected void decodeSuccess(Result result){
        if(mCameraManagerHandler != null){
            onPause();
            mCameraManagerHandler.onScanSuccess(result);
            mCameraManagerHandler.onCameraStop();
        }
    }

    //获取屏幕方向 判断是否是竖屏
    private boolean isPortrait(){
        Configuration configuration = mContext.getResources().getConfiguration();
        int orientation = configuration.orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    //回调
    public interface CameraManagerHandler{

        //没有权限 返回true 说明自己提示权限信息
        boolean onPermissionsDenied(@NonNull List<String> permissions);

        //获取扫描框位置 width、height预览surface的宽高
        @NonNull Rect getScanRect(int width, int height);

        //解码成功
        void onScanSuccess(@NonNull Result result);

        //开始
        void onCameraStart();

        //停止
        void onCameraStop();
    }
}
