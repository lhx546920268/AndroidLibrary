package com.lhx.library.viewPager;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Scroller;

import com.lhx.library.timer.CountDownTimer;

import java.lang.reflect.Field;

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
    private CountDownTimer mCountDownTimer;

    //是否需要循环
    private boolean mShouldCycle = true;

    private boolean mDetachedFromWindow;

    public CyclePagerAdapter(ViewPager viewPager) {
        super(viewPager);

        viewPager.addOnPageChangeListener(this);
        viewPager.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                startAutoPlayTimer();
                if(mDetachedFromWindow){
                    //ViewPager 会在 AttachedToWindow 需要重新布局，会导致第一次smoothScroll没有动画
                    mViewPager.requestLayout();
                    mDetachedFromWindow = false;
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mDetachedFromWindow = true;
                stopAutoPlayTimer();
            }
        });

        setScroller();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(mShouldCycle){
            if(mRealCount > 1){
                if(position == 0){
                    mTargetPosition = mRealCount;
                }else if(position == mRealCount + 1){
                    mTargetPosition = 1;
                }
            }
        }else {
            mTargetPosition = position;
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
        return mRealCount > 1 && mShouldCycle ? mRealCount + 2 : mRealCount;
    }

    //移动到第一个位置
    @Override
    protected void scrollToFirstPosition() {

        if(mRealCount > 1){
            mViewPager.setCurrentItem(mShouldCycle ? 1 : 0, false);
            mTargetPosition = -1;
            startAutoPlayTimer();
        }
    }

    //移动到某个位置
    public void scrollToPosition(int position, boolean smooth){
        if(mRealCount > 1 && position >= 0 && position < mRealCount){
            if(mShouldCycle){
                mViewPager.setCurrentItem(position + 1, smooth);
            }else {
                mViewPager.setCurrentItem(position, smooth);
            }
        }
    }


    /**
     * 通过viewPager 位置获取真实的数据源位置
     * @param position viewPager 位置
     * @return 真实的数据源位置
     */
    @Override
    public int getRealPosition(int position) {
        if(mRealCount <= 1 || !mShouldCycle){
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

    /**
     * 通过数据源位置获取布局位置
     * @param position 数据源位置
     * @return 布局位置
     */
    public int getAdapterPosition(int position){
        if(mRealCount <= 1 || !mShouldCycle){
            return position;
        }else {
            return position + 1;
        }
    }

    //跑到下一页
    private void nextPage(){
        int position = mViewPager.getCurrentItem();
        position ++;
        if(position >= getCount()){
            position = getRealCount() != getCount() ? 1 : 0;
        }
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

    public void setShouldCycle(boolean shouldCycle) {
        if(mShouldCycle != shouldCycle){
            mShouldCycle = shouldCycle;
            notifyDataSetChanged();
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
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(CountDownTimer.COUNT_DOWN_UNLIMITED, mAutoPlayInterval) {
                @Override
                public void onFinish() {

                }

                @Override
                public void onTick(long millisLeft) {
                    nextPage();
                }
            };
        }
        if(mCountDownTimer.isExecuting())
            return;
        mCountDownTimer.start();
    }

    //停止自动轮播计时器
    private void stopAutoPlayTimer(){
        if(mCountDownTimer != null){
            mCountDownTimer.stop();
            mCountDownTimer = null;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(mShouldAutoPlay && mRealCount <= 1){
            stopAutoPlayTimer();
        }
    }

    /**
     * 获取真实的数量
     * @return 真实的数量
     */
    public abstract int getRealCount();

    //设置scroller
    private void setScroller( ){
        try {
            Field field = null;
            field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);

            PagerScroller scroller = new PagerScroller(mViewPager.getContext());
            field.set(mViewPager, scroller);

        }catch(NoSuchFieldException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }


    public class PagerScroller extends Scroller {
        private int mDuration = 1000;

        public void setDuration(int mDuration) {
            this.mDuration = mDuration;
        }

        public PagerScroller(Context context) {
            super(context);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}
