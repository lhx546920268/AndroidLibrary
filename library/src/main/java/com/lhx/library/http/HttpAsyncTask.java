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
public abstract class HttpAsyncTask extends AsyncTask<Void, Integer, byte[]> {

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

    //编码类型
    protected String mStringEncoding = "utf-8";


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

    public String getStringEncoding() {
        return mStringEncoding;
    }

    public void setStringEncoding(String stringEncoding) {
        mStringEncoding = stringEncoding;
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
            HttpRequest httpRequest = new HttpRequest(mURL);
            onConfigure(httpRequest);
            httpRequest.setStringEncoding(mStringEncoding);
            if(mParams != null){
                for(Map.Entry<String, Object> entry : mParams.valueSet()){
                    httpRequest.addPostValue(entry.getKey(), entry.getValue().toString());
                }
            }

            if(mFiles != null){
                for(Map.Entry<String, File> entry : mFiles.entrySet()){
                    httpRequest.addFile(entry.getKey(), entry.getValue());
                }
            }

            boolean result = httpRequest.startRequest();
            mHttpResponseCode = httpRequest.getHttpResponseCode();
            mErrorCode = httpRequest.mErrorCode;
            if(result){
                return httpRequest.getResponseData();
            }
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
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
