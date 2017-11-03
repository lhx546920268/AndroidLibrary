package com.lhx.library.timer;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * 计时器
 */

public abstract class CountDownTimer implements Handler.Callback{

    //没有倒计时长度限制
    public static final long COUNT_DOWN_UNLIMITED = Long.MAX_VALUE;

    //倒计时消息
    private static final int COUNT_DOWN_MSG_WHAT = 1;

    //倒计时总时间长度（毫秒），如果为 COUNT_DOWN_UNLIMITED 则 没有限制，倒计时不会停止 必须自己手动停止
    private long mMillisToCountDown;

    //倒计时间隔（毫秒）
    private long mMillisInterval;

    //倒计时停止时间（毫秒）
    private long mMillisToStop;

    //倒计时是否已取消
    private boolean mCanceled;

    //是否正在倒计时
    private boolean mExecuting;

    //
    private Handler mHandler = new Handler(this);

    public CountDownTimer(long millisToCountDown, long millisInterval) {
        mMillisToCountDown = millisToCountDown;
        mMillisInterval = millisInterval;
    }

    //开始倒计时
    public synchronized void start(){
        mCanceled = false;
        if(mMillisToCountDown <= 0 || mMillisInterval <= 0){
            finish();
            return;
        }

        mExecuting = true;
        if(mMillisToCountDown == COUNT_DOWN_UNLIMITED){
            //倒计时无时间限制
            mHandler.sendEmptyMessageDelayed(COUNT_DOWN_MSG_WHAT, mMillisInterval);
        }else {
            mMillisToStop = SystemClock.elapsedRealtime() + mMillisToCountDown;
            mHandler.sendEmptyMessage(COUNT_DOWN_MSG_WHAT);
        }
    }

    //停止倒计时
    public synchronized void stop(){
        if(mCanceled || !mExecuting)
            return;
        mCanceled = true;
        mExecuting = false;
        mHandler.removeMessages(COUNT_DOWN_MSG_WHAT);
    }

    //正在执行
    public boolean isExecuting(){
        return mExecuting;
    }

    //执行完成
    private void finish(){
        if(!mExecuting)
            return;
        mExecuting = false;
        mCanceled = false;
        onFinish();
    }

    @Override
    public boolean handleMessage(Message msg) {

        synchronized (this){
            if(mCanceled){
                return true;
            }

            if(mMillisToCountDown == COUNT_DOWN_UNLIMITED){
                //倒计时无时间限制
                triggerTick(COUNT_DOWN_UNLIMITED);
            }else {
                //倒计时剩余时间
                long millisLeft = mMillisToStop - SystemClock.elapsedRealtime();

                if(millisLeft <= 0){
                    //没时间了，倒计时停止
                    finish();
                }else if(millisLeft < mMillisInterval){
                    //剩余的时间已经不够触发一次倒计时间隔了
                    mHandler.sendEmptyMessageDelayed(COUNT_DOWN_MSG_WHAT, millisLeft);
                }else {
                    triggerTick(millisLeft);
                }
            }
        }
        return true;
    }

    //触发tick
    private void triggerTick(long millisLeft){
        long lastTickStart = SystemClock.elapsedRealtime();
        onTick(millisLeft);

        long delay = lastTickStart + mMillisInterval - SystemClock.elapsedRealtime();
        while (delay < 0){
            //当触发倒计时 onTick 方法耗时太多，将进行下一个倒计时间隔
            delay += mMillisInterval;
        }

        mHandler.sendEmptyMessageDelayed(COUNT_DOWN_MSG_WHAT, delay);
    }

    //倒计时结束
    public abstract void onFinish();

    /**
     * 每个间隔触发回调
     * @param millisLeft 倒计时剩余时间（毫秒）倒计时无限制时，为COUNT_DOWN_UNLIMITED
     */
    public abstract void onTick(long millisLeft);
}
