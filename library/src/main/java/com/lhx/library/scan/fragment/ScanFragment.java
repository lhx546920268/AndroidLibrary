package com.lhx.library.scan.fragment;

import android.Manifest;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lhx.library.R;
import com.lhx.library.dialog.AlertController;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.scan.util.CameraManager;
import com.lhx.library.util.AppUtil;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.util.ViewUtil;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

//二维码，条形码扫描
public abstract class ScanFragment extends AppBaseFragment implements TextureView.SurfaceTextureListener,
        CameraManager.CameraManagerHandler{

    //相机管理
    private CameraManager mCameraManager;

    //相机预览
    private TextureView mTextureView;

    //是否正在暂停
    private boolean mPausing;

    //是否正在预览
    private boolean mPreviewing;

    @Override
    protected void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setContentView(R.layout.scan_fragment);
        FrameLayout frameLayout = (FrameLayout)getContentView();
        View view = getContentView(inflater, frameLayout);
        if(view.getParent() == null || view.getParent() != frameLayout){
            ViewUtil.removeFromParent(view);
            frameLayout.addView(view);
        }

        mCameraManager = new CameraManager(mContext, this);

        mTextureView = findViewById(R.id.texture);
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraManager.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, mCameraManager);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCameraManager.setPreviewSize(width, height);

        mCameraManager.setScanRect(getScanRect(width, height));
        mCameraManager.openCamera(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {


    }

    //没有权限 返回true 说明自己提示权限信息
    @Override
    public boolean onPermissionsDenied(@NonNull List<String> permissions) {
        return false;
    }

    //获取扫码框位置
    public abstract @NonNull
    Rect getScanRect(int width, int height);

    //获取内容视图
    public abstract @NonNull
    View getContentView(LayoutInflater inflater, FrameLayout container);
}
