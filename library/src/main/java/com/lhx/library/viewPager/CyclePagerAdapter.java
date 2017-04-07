package com.lhx.library.viewPager;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 循环轮播器 view可复用
 */
public abstract class CyclePagerAdapter extends ReusablePagerAdapter implements ViewPager.OnPageChangeListener {

    public static final String TAG = "CyclePagerAdapter";

    //真实的数量
    private int mRealCount;

    //需要移动到的位置
    private int mTargetPosition = -1;


    //是否要自动轮播
    private boolean mShouldAutoPlay = false;

    //自动轮播间隔 毫秒
    private int mAutoPlayInterval = 5000;

    //自动轮播计时器
    private Handler mHandler;
    private Runnable mRunnable;

    public CyclePagerAdapter(ViewPager viewPager) {
        super(viewPager);

        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(mRealCount > 1){
            if(position == 0){
                mTargetPosition = mRealCount;
            }else if(position == mRealCount + 1){
                mTargetPosition = 1;
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        //当不是动画中改变当前显示view的位置
        if(state != ViewPager.SCROLL_STATE_SETTLING && mTargetPosition != -1){
            mViewPager.setCurrentItem(mTargetPosition, false);
            mTargetPosition = -1;
        }

        //用户滑动时关闭自动轮播
        if(state == ViewPager.SCROLL_STATE_DRAGGING){
            stopAutoPlayTimer();
        }else {
            startAutoPlayTimer();
        }
    }

    @Override
    public final int getCount() {
        mRealCount = getRealCount();

        //当只有一个view时，不需要循环
        return mRealCount > 1 ? mRealCount + 2 : mRealCount;
    }

    //移动到第一个位置
    @Override
    protected void scrollToFirstPosition() {

        if(mRealCount > 1){
            mViewPager.setCurrentItem(1, false);
            mTargetPosition = -1;
            startAutoPlayTimer();
        }
    }

    /**
     * 通过viewPager 位置获取真实的数据源位置
     * @param position viewPager 位置
     * @return 真实的数据源位置
     */
    @Override
    public int getRealPosition(int position) {
        if(mRealCount <= 1){
            return position;
        }else {
            if(position == 0){
                return mRealCount - 1;
            }else if(position == mRealCount + 1){
                return 0;
            }else {
                return  position - 1;
            }
        }
    }

    //跑到下一页
    private void nextPage(){
        int position = mViewPager.getCurrentItem();
        position ++;
        mViewPager.setCurrentItem(position, true);
    }

    public boolean shouldAutoPlay() {
        return mShouldAutoPlay;
    }

    public void setShouldAutoPlay(boolean shouldAutoPlay) {
        if(shouldAutoPlay != mShouldAutoPlay){
            this.mShouldAutoPlay = shouldAutoPlay;
            if(shouldAutoPlay){
                startAutoPlayTimer();
            }else {
                stopAutoPlayTimer();
            }
        }
    }

    public int getAutoPlayInterval() {
        return mAutoPlayInterval;
    }

    public void setAutoPlayInterval(int autoPlayInterval) {
        if(autoPlayInterval != mAutoPlayInterval){
            this.mAutoPlayInterval = autoPlayInterval;
        }
    }

    //开始自动轮播计时器
    private void startAutoPlayTimer(){
        if(!mShouldAutoPlay || mRealCount <= 1)
            return;
        if(mHandler == null){
            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    nextPage();
                    mHandler.postDelayed(mRunnable, mAutoPlayInterval);
                }
            };
        }

        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, mAutoPlayInterval);
    }

    //停止自动轮播计时器
    private void stopAutoPlayTimer(){
        if(mHandler != null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    /**
     * 获取真实的数量
     * @return 真实的数量
     */
    public abstract int getRealCount();
}
