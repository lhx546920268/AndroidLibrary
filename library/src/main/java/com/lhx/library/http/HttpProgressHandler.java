package com.lhx.library.http;

/**
 * http 请求 进度
 */

public interface HttpProgressHandler<T> {

    //上传进度 0.0 ~ 1.0
    void onUpdateUploadProgress(T target, float progress);

    //下载进度 0.0 ~ 1.0
    void onUpdateDownloadProgress(T target, float progress);
}
