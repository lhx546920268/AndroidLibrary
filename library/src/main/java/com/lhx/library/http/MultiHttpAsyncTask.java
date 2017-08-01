package com.lhx.library.http;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.annotation.CallSuper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 多个http任务管理
 */

public abstract class MultiHttpAsyncTask implements HttpRequestHandler {

    //http请求任务
    private ArrayList<HttpAsyncTask> mTasks = new ArrayList<>();

    //是否有个请求失败了
    private boolean mHasOneFail;

    //是否取消所有请求当有个请求失败时
    private boolean mShouldCancelWhileOneFail = true;

    public void setShouldCancelWhileOneFail(boolean shouldCancelWhileOneFail) {
        this.mShouldCancelWhileOneFail = shouldCancelWhileOneFail;
    }

    public void setHasOneFail(boolean hasOneFail) {
        this.mHasOneFail = hasOneFail;
    }

    /**
     * 添加任务
     * @param URL 请求链接
     * @param params 请求参数
     * @param files 要上传的文件
     * @return http任务
     */
    public HttpAsyncTask addTask(String URL, ContentValues params, HashMap<String, File> files) {

        HttpAsyncTask task = new HttpAsyncTask(URL, params, files) {
            @Override
            public void onConfigure(HttpRequest request) {

            }
        };
        task.addHttpRequestHandler(this);
        mTasks.add(task);

        return task;
    }

    public HttpAsyncTask addTask(String URL, ContentValues params) {
        return addTask(URL, params, null);
    }

    public HttpAsyncTask addTask(String URL) {
        return addTask(URL, null, null);
    }

    public void addTask(HttpAsyncTask task){

        if(task != null && !mTasks.contains(task)){
            mTasks.add(task);
        }
    }

    public void startSerially(){

        mHasOneFail = false;
        for(HttpAsyncTask task : mTasks){
            if(task.getStatus() != AsyncTask.Status.PENDING)
                continue;
            task.execute();
        }
    }

    public void startConcurrently(){

        mHasOneFail = false;
        for(HttpAsyncTask task : mTasks){
            if(task.getStatus() != AsyncTask.Status.PENDING)
                continue;
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    //取消所有任务
    public void cancelAllTask(){
        for(HttpAsyncTask task : mTasks){
            task.cancel();
        }
        mTasks.clear();
    }

    @Override
    @CallSuper
    public void onComplete(HttpAsyncTask task) {
        mTasks.remove(task);
        if(mTasks.size() == 0){
            onAllTaskComplete(mHasOneFail);
        }
    }

    @Override
    public void onSuccess(HttpAsyncTask task, byte[] result) {

    }

    @Override
    public void onFail(HttpAsyncTask task, int errorCode, int httpCode) {
        mHasOneFail = true;
    }

    ///所有任务执行完成
    public abstract void onAllTaskComplete(boolean oneFail);
}
