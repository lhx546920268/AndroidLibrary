package com.lhx.library.http;

import android.content.ContentValues;
import android.content.Context;

import com.lhx.library.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;


/**
 * http json请求
 */

public abstract class HttpJsonAsyncTask implements HttpRequestHandler{

    //请求api错误
    private boolean mApiError;

    //http异步任务
    private HttpAsyncTask mTask;

    //错误信息
    private String mErrorMessage;

    protected Context mContext;

    public boolean isApiError() {
        return mApiError;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    public HttpJsonAsyncTask(Context context) {

        mContext = context;
    }

    //取消任务
    public void cancel(){
        if(mTask != null && mTask.isExecuting()){
            mTask.cancel();
        }
    }

    //是否正在执行
    public boolean isExecuting(){
        return mTask != null && mTask.isExecuting();
    }

    //开始请求
    public void start(){
        onStart(this);
        getTask().startConcurrently();
    }

    public HttpAsyncTask getTask(){
        if(mTask != null && mTask.isExecuting()){
            mTask.cancel();
        }

        mApiError = false;
        mErrorMessage = null;
        String url = getRequestURL();

        ContentValues params = getParams();
        Map<String, File> files = getFiles();
        processParams(params, files);

        mTask = new HttpAsyncTask(url, params , files) {
            @Override
            public void onConfigure(HttpRequest request) {

                request.setTimeoutInterval(15000);
            }
        };

        mTask.setName(getClass().getName());
        mTask.addHttpRequestHandler(this);
        return mTask;
    }

    @Override
    public final void onSuccess(HttpAsyncTask task, byte[] result) {
        String string = StringUtil.stringFromBytes(result, mTask.getStringEncoding());
        try {
            JSONObject object = new JSONObject(string);
            if(resultFromJSONObject(object)){
                onSuccess(this, object);
            }else {
                onFail(this);
            }
        }catch (JSONException e){
            e.printStackTrace();
            mErrorMessage = "JSON解析错误";
            onFail(this);
        }
    }

    @Override
    public final void onFail(HttpAsyncTask task, int errorCode, int httpCode) {
        mApiError = false;
        mErrorMessage = HttpRequest.getErrorStringFromCode(errorCode, httpCode);
        onFail(this);
    }

    @Override
    public final void onComplete(HttpAsyncTask task) {
        onComplete(this);
    }

    //获取参数
    public ContentValues getParams(){
        return null;
    }

    //获取文件
    public Map<String, File> getFiles(){
        return null;
    }

    //处理参数 比如签名
    public void processParams(ContentValues values, Map<String, File> files){

    }

    //请求路径
    public abstract String getRequestURL();

    //请求成功
    public abstract void onSuccess(HttpJsonAsyncTask task, JSONObject object);

    //请求失败
    public abstract void onFail(HttpJsonAsyncTask task);

    //请求完成无论是成功还是失败
    public abstract void onComplete(HttpJsonAsyncTask task);

    //请求开始
    public abstract void onStart(HttpJsonAsyncTask task);

    //请求是否成功
    public abstract boolean resultFromJSONObject(JSONObject object);
}
