package com.lhx.library.permission;

//权限回调
public interface PermissionHandler {

    //权限拒绝
    void onPermissionGranted();

    //同意
    void onPermissionDenied();
}
