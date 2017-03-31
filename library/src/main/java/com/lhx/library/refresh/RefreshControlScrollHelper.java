package com.lhx.library.refresh;

import android.support.annotation.IntDef;
import android.widget.Scroller;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 刷新控制器 滑动帮助类
 */
public class RefreshControlScrollHelper{

    ///当前偏移的距离
    private int mOffset;

    ///当前触摸的位置，y轴
    private float mTouchY;

    ///
    private Scroller mScroller;

    /// mScroller 当前y
    private int mCurY;

    ///关联刷新控制器
    private RefreshControl mRefreshControl;

    /// 当前滑动的状态
    public static final int SCROLL_STATE_NORMAL = 0; //正常
    public static final int SCROLL_STATE_REFRESH = 1; //刷新
    public static final int SCROLL_STATE_LOADMORE = 2; //加载更多
    @IntDef({SCROLL_STATE_NORMAL, SCROLL_STATE_REFRESH, SCROLL_STATE_LOADMORE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollState{}

    private int mScrollState = SCROLL_STATE_NORMAL;

    RefreshControlScrollHelper(RefreshControl refreshControl) {

        mRefreshControl = refreshControl;
        mScroller = new Scroller(refreshControl.getContext());
    }

    void setTouchY(float touchY) {
        mTouchY = touchY;
    }

    public int getOffset() {
        return mOffset;
    }

    ///获取滑动的距离
    private int getDistance(float y){
        int distance = (int)(y - mTouchY);
        if((mOffset > 0 && y > mTouchY) || (mOffset < 0 && y < mTouchY)){
            ///下拉刷新，恢复时不需要难度系数
            distance /= mRefreshControl.getScrollDifficult() + Math.abs(mOffset) * 5 / mRefreshControl.getHeight();
        }

        return distance;
    }

    ///获取需要滑动的距离
    int onMove(float y){

        int distance = getDistance(y);
        mTouchY = y;

        //防止上拉过程中 加载更多出现
        int offset = mOffset + distance;
        if(offset < 0 && mOffset > 0){
            offset = 0;
        }else if(offset > 0 && mOffset < 0){
            offset = 0;
        }

        mOffset = offset;
        return distance;
    }

    ///判断头部或底部是否能滑动
    public boolean isHeaderOrFooterScrollEnable(float y){
        return mOffset != 0;
    }

    ///松开回到原来的位置
    int onRelease(){
        return -mOffset;
    }

    //获取当前滑动状态
    public int getScrollState(){
        if(mOffset > 0)
            return SCROLL_STATE_REFRESH;
        else if(mOffset < 0)
            return SCROLL_STATE_LOADMORE;
        else
            return SCROLL_STATE_NORMAL;
    }

    ///滑动方向 正数为 右、下  负数为左、上
    int getDirection(float cur){
        return (int)(mTouchY - cur);
    }

    ///是否已在顶部
    public boolean atTheTop(){
        return mOffset == 0;
    }

    ///滑动到某个位置
    void scrollTo(int to, int duration){

        finishScroll();
        if(to != 0){
            mCurY = 0;
            if(duration > 0){
                mScroller.startScroll(0, 0, 0, to, duration);
                mRefreshControl.invalidate();
            }else {
                mOffset -= to;
                mRefreshControl.move(-to);
            }
        }
    }

    ///滑动计算
    void computeScroll(){
        if(mScroller.computeScrollOffset() && !mScroller.isFinished()){

            int offset = 0;
            if(mRefreshControl.getOrientation() == RefreshControl.ORIENTATION_VERTICAL){
                offset = mCurY - mScroller.getCurrY();
                mCurY = mScroller.getCurrY();
            }else {
                offset = mCurY - mScroller.getCurrY();
                mCurY = mScroller.getCurrY();
            }
            mRefreshControl.move(offset);
            mOffset += offset;
            mRefreshControl.postInvalidate();
        }else {

            mRefreshControl.scrollComplete();
        }
    }

    ///完成滚动
    void finishScroll(){

        if(!mScroller.isFinished()){
            mScroller.forceFinished(true);
        }
    }
}