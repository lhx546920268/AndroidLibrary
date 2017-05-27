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
        task.setHttpRequestHandler(this);
        mTasks.add(task);

        return task;
    }

    public HttpAsyncTask addTask(String URL, ContentValues params) {
        return addTask(URL, params, null);
    }

    public HttpAsyncTask addTask(String URL) {
        return addTask(URL, null, null);
    }

    public void startSerially(){
        for(HttpAsyncTask task : mTasks){
            task.execute();
        }
    }

    public void startConcurrently(){
        for(HttpAsyncTask task : mTasks){
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    @CallSuper
    public void onComplete(HttpAsyncTask task) {
        mTasks.remove(task);
        if(mTasks.size() == 0){
            onAllTaskComplete();
        }
    }

    ///所有任务执行完成
    public abstract void onAllTaskComplete();
}
