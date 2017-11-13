package com.lhx.library.viewPager;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.lhx.library.R;
import com.lhx.library.timer.CountDownTimer;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 垂直 viewPager
 */

public class VerticalViewPager extends ViewGroup {

    private static final int NO_POSITION = -1;

    //计时器
    private CountDownTimer mCountDownTimer;

    //倒计时间隔 毫秒
    private long mMillisInterval = 1000 * 3;

    private Adapter mAdapter;

    //item数量
    private int mItemCount;

    //动画时间
    private long mAnimateDuration = 300;

    //是否正在动画中
    private boolean mAnimating = false;

    //当前显示出来的view
    private SparseArray<HashSet<View>> mVisibleViews = new SparseArray<>();

    //可重用的view
    private SparseArray<HashSet<View>> mReusedViews = new SparseArray<>();

    public void setMillisInterval(long millisInterval) {
        mMillisInterval = millisInterval;
    }

    public void setAnimateDuration(long animateDuration) {
        mAnimateDuration = animateDuration;
    }

    public VerticalViewPager(Context context) {
        this(context, null);
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalViewPager(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialization();
    }

    //初始化
    private void initialization(){

    }

    //开始计时器
    private void startTimer(){

        if(mMillisInterval <= 0 || mItemCount <= 1 || getWindowVisibility() != VISIBLE)
            return;

        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(CountDownTimer.COUNT_DOWN_UNLIMITED, mMillisInterval) {
                @Override
                public void onFinish() {

                }

                @Override
                public void onTick(long millisLeft) {
                    animateDidEnd();
                    int position = getCurrentPosition() + 1;
                    if(position >= mItemCount){
                        position = 0;
                    }
                    scrollTo(position, true);
                }
            };
        }
        mCountDownTimer.setMillisInterval(mMillisInterval);
        if(!mCountDownTimer.isExecuting()){
            mCountDownTimer.start();
        }
    }

    //结束计时器
    private void stopTimer(){
        if(mCountDownTimer != null){
            mCountDownTimer.stop();
        }
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        reloadData();
    }

    //刷新数据
    public void reloadData(){

        removeAllViews();
        mItemCount = 0;
        stopTimer();

        if(mAdapter != null){
            mItemCount = mAdapter.getCount();
            scrollTo(0, false);
            startTimer();
        }
    }

    //滑动到某个位置
    public void scrollTo(final int position, boolean animate){

        if(mAdapter == null || !isPositionValid(position))
            return;

        View currentView = getCurrentView();
        int currentPosition = getCurrentPosition();

        if(currentView != null && currentPosition == position)
            return;

        int type = mAdapter.getViewType(position);

        //获取可重用的view
        HashSet<View> views = mReusedViews.get(type);
        View convertView = null;
        if(views != null && views.size() > 0){
            Iterator<View> iterator = views.iterator();
            convertView = iterator.next();
            views.remove(convertView);
        }

        convertView = mAdapter.getView(convertView, position, type);

        //加入可见队列
        views = mVisibleViews.get(type);
        if(views == null){
            views = new HashSet<>();
            mVisibleViews.put(type, views);
        }

        convertView.setTag(R.id.view_pager_position_tag_key, position);
        views.add(convertView);

        if(currentView == null){
            //刚开始显示
            currentView = convertView;
            addView(convertView);
            currentPosition = position;
        }else {
            addView(convertView);

            final View view = convertView;
            mAnimating = true;
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -getMeasuredHeight());
            animation.setDuration(mAnimateDuration);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animateDidEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            currentView.startAnimation(animation);

            animation = new TranslateAnimation(0, 0, 0, -getMeasuredHeight());
            animation.setDuration(mAnimateDuration);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility == VISIBLE){
            startTimer();
        }else {
            stopTimer();
        }
    }

    //动画结束处理
    private void animateDidEnd(){

        if(!mAnimating)
            return;
        mAnimating = false;
        if(getChildCount() > 1){
            View view = getChildAt(0);
            view.clearAnimation();
            View view1 = getChildAt(1);
            view1.clearAnimation();
            addToReuse(getCurrentPosition(), view);
        }
    }


    //获取当前视图
    public View getCurrentView() {
        int childCount = getChildCount();
        if(childCount == 0){
            return null;
        }

        if(childCount == 1){
            View view = getChildAt(0);
            return view;
        }

        View view = getChildAt(1);
        return view;
    }

    //获取当前位置
    public int getCurrentPosition(){
        View view = getCurrentView();
        if(view != null){
            return (int)view.getTag(R.id.view_pager_position_tag_key);
        }
        return NO_POSITION;
    }

    //加入重用队列
    private void addToReuse(int position, View view){
        int type = mAdapter.getViewType(position);

        //移出可见队列
        HashSet<View> views = mVisibleViews.get(type);
        views.remove(view);

        //加入重用队列
        views = mReusedViews.get(type);
        if(views == null){
            views = new HashSet<>();
            mReusedViews.put(type, views);
        }
        views.add(view);
        removeView(view);
    }

    //判断position是否有效
    private boolean isPositionValid(int position){
        return position < mItemCount && position >= 0;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        if(childCount > 0){

            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom();

            int width = r - l - paddingLeft - paddingRight;
            int height = b - t - paddingTop - paddingBottom;

            for(int i = 0;i < childCount;i ++){
                View child = getChildAt(i);

                LayoutParams params = child.getLayoutParams();
                int left = paddingLeft;
                int right = left + width;
                int top = paddingTop + height * i;
                int bottom = top + height;
                child.layout(left, top, right, bottom);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        int childCount = getChildCount();
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        for(int i = 0;i < childCount;i ++){
            View child = getChildAt(i);
            final int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            final int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            child.measure(widthSpec, heightSpec);
        }
    }

    public static abstract class Adapter{

        //获取数量
        public abstract int getCount();

        //获取view
        public abstract View getView(View convertView, int position, int viewType);

        //获取视图类型
        public abstract int getViewType(int position);
    }
}
