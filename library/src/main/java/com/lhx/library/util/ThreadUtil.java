package com.lhx.library.util;

import android.os.Handler;
import android.os.Looper;

/**
 * 线程工具类
 */

public class ThreadUtil {

    /**
     * 在主线程上执行
     * @param runnable 要执行的、、
     */
    public static void runOnMainThread(Runnable runnable){
        if(runnable != null){
            if(isRunOnMainThread()){
                runnable.run();
            }else {
                getMainHandler().post(runnable);
            }
        }
    }

    /**
     * 是否在主线程上
     * @return 是否
     */
    public static boolean isRunOnMainThread(){
        return Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId();
    }


    //主线程handler
    private static Handler mMainHandler;

    /**
     * 获取主线程handler 单例
     */
    public synchronized static Handler getMainHandler(){
        if(mMainHandler == null){
            mMainHandler = new Handler(Looper.getMainLooper());
        }

        return mMainHandler;
    }

}
