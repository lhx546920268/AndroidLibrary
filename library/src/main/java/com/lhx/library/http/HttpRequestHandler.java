package com.lhx.library.http;

/**
 * http 请求回调
 */

public interface HttpRequestHandler {

    //请求成功
    void onSuccess(String result);

    //请求失败
    void onFail(int errorCode, int httpCode);

    //请求完成，无论是成功还是失败都会调用
    void onComplete();
}
