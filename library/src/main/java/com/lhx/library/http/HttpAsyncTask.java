package com.lhx.library.http;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * http 异步任务
 */
public abstract class HttpAsyncTask extends AsyncTask<Void, Float, byte[]> implements HttpProgressHandler<HttpRequest> {

    ///请求URL
    protected String mURL;

    ///请求参数
    protected ContentValues mParams;

    ///要上传的文件
    protected HashMap<String, File> mFiles;

    //http响应码
    protected int mHttpResponseCode;

    //错误码
    protected int mErrorCode = HttpRequest.ERROR_CODE_NONE;

    //请求完成回调
    protected HttpRequestHandler mHttpRequestHandler;

    //请求进度回调 必须在 onConfigure 设置 httpRequest showUploadProgress showDownloadProgress 为true
    protected HttpProgressHandler<HttpAsyncTask> mHttpProgressHandler;

    //编码类型
    protected String mStringEncoding = "utf-8";

    //用来识别是哪个请求
    private String mName;

    //http请求
    protected HttpRequest mHttpRequest;

    //进度类型
    private static final float PROGRESS_UPLOAD = 0.0f;
    private static final float PROGRESS_DOWNLOAD = 1.0f;

    public HttpAsyncTask(String URL, ContentValues params, HashMap<String, File> files) {
        mURL = URL;
        mParams = params;
        mFiles = files;
    }

    public HttpAsyncTask(String URL, ContentValues params) {
        mURL = URL;
        mParams = params;
    }

    public HttpAsyncTask(String URL) {
        mURL = URL;
    }

    public void setHttpRequestHandler(HttpRequestHandler httpRequestHandler) {
        mHttpRequestHandler = httpRequestHandler;
    }

    public void setHttpProgressHandler(HttpProgressHandler<HttpAsyncTask> httpProgressHandler) {
        mHttpProgressHandler = httpProgressHandler;
    }

    public String getStringEncoding() {
        return mStringEncoding;
    }

    public void setStringEncoding(String stringEncoding) {
        mStringEncoding = stringEncoding;
    }

    public String getURL() {
        return mURL;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    //把结果转成string
    public String resultToString(byte[] reulst){
        if(reulst != null && reulst.length > 0){
            try {
                return new String(reulst, mStringEncoding);
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }

        return "";
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        if(!TextUtils.isEmpty(mURL) && !isCancelled()){
            mHttpRequest = new HttpRequest(mURL);
            onConfigure(mHttpRequest);

            if(mHttpRequest.isShowDownloadProgress() || mHttpRequest.isShowUploadProgress()){
                mHttpRequest.setHttpProgressHandler(this);
            }

            mHttpRequest.setStringEncoding(mStringEncoding);
            if(mParams != null){
                for(Map.Entry<String, Object> entry : mParams.valueSet()){
                    mHttpRequest.addPostValue(entry.getKey(), entry.getValue().toString());
                }
            }

            if(mFiles != null){
                for(Map.Entry<String, File> entry : mFiles.entrySet()){
                    mHttpRequest.addFile(entry.getKey(), entry.getValue());
                }
            }

            boolean result = mHttpRequest.startRequest();
            mHttpResponseCode = mHttpRequest.getHttpResponseCode();
            mErrorCode = mHttpRequest.mErrorCode;
            byte[] data = null;
            if(result){
                data = mHttpRequest.getResponseData();
            }

            publishProgress();
            mHttpRequest.close();
            mHttpRequest = null;
            return data;
        }

        return null;
    }

    //配置 http
    public abstract void onConfigure(HttpRequest request);

    @Override
    protected void onPostExecute(byte[] bytes) {
        if(mHttpRequestHandler != null){
            if(mErrorCode == HttpRequest.ERROR_CODE_NONE && bytes != null){
                mHttpRequestHandler.onSuccess(this, bytes);
            }else {
                mHttpRequestHandler.onFail(this, mErrorCode, mHttpResponseCode);
            }
            mHttpRequestHandler.onComplete(this);
        }
    }

    @Override
    protected void onProgressUpdate(Float... values) {

        //更新进度条
        if(mHttpProgressHandler != null && values.length > 1){
            float type = values[1];
            if(type == PROGRESS_UPLOAD){
                mHttpProgressHandler.onUpdateUploadProgress(this, values[0]);
            }else if(type == PROGRESS_DOWNLOAD){
                mHttpProgressHandler.onUpdateDownloadProgress(this, values[0]);
            }
        }
    }

    @Override
    final public void onUpdateUploadProgress(HttpRequest target, float progress) {
        publishProgress(progress, PROGRESS_UPLOAD);
    }

    @Override
    final public void onUpdateDownloadProgress(HttpRequest target, float progress) {
        publishProgress(progress, PROGRESS_DOWNLOAD);
    }

    @Override
    protected void onCancelled() {
        if(mHttpRequest != null){
            mHttpRequest.close();
            mHttpRequest = null;
        }
    }
}
