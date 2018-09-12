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

    //是否需要重用
    private boolean mShouldReuse = true;

    //是否需要自动轮播
    private boolean mShouldAutoPlay = true;

    //当前位置
    private int mPosition = NO_POSITION;

    //相反方向
    private boolean mReverse = false;

    //当前显示出来的view
    private SparseArray<HashSet<View>> mVisibleViews = new SparseArray<>();

    //可重用的view
    private SparseArray<HashSet<View>> mReusedViews = new SparseArray<>();

    public void setMillisInterval(long millisInterval) {
        mMillisInterval = millisInterval;
    }

    public void setShouldAutoPlay(boolean shouldAutoPlay) {

        mShouldAutoPlay = shouldAutoPlay;
        if(!mShouldAutoPlay){
            stopTimer();
        }
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

    public void setShouldReuse(boolean shouldReuse) {
        mShouldReuse = shouldReuse;
    }

    //初始化
    private void initialization(){
        setClipChildren(true);
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

                    int position = mPosition + 1;
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
            if(mShouldAutoPlay){
                startTimer();
            }
        }
    }

    //滑动到某个位置
    public void scrollTo(final int position, boolean animate){

        if(mAdapter == null || !isPositionValid(position))
            return;

        View currentView = getCurrentView();

        if(currentView != null && mPosition == position)
            return;

        final int currentPosition = mPosition;
        int type = mAdapter.getViewType(position);

        View convertView = null;

        //获取可重用的view
        HashSet<View> views = mReusedViews.get(type);
        if(mShouldReuse){
            if(views != null && views.size() > 0){
                Iterator<View> iterator = views.iterator();
                convertView = iterator.next();
                views.remove(convertView);
            }
        }

        convertView = mAdapter.getView(convertView, position, type);
        convertView.setTag(R.id.view_pager_position_tag_key, position);

        //加入可见队列
        if(mShouldReuse){
            views = mVisibleViews.get(type);
            if(views == null){
                views = new HashSet<>();
                mVisibleViews.put(type, views);
            }
            views.add(convertView);
        }

        mPosition = position;
        if(currentView == null){
            //刚开始显示
            addView(convertView);
            mAdapter.onAttachToParent(convertView, position, type);
        }else {
            if(currentPosition > position){
                addView(convertView, 0);
            }else {
                addView(convertView);
            }

            mAdapter.onAttachToParent(convertView, position, type);

            if(animate){
                final View view1 = convertView;
                final View view2 = currentView;
                mReverse = currentPosition > position;
                int translate = currentPosition > position ? getMeasuredHeight() : -getMeasuredHeight();

                mAnimating = true;
                TranslateAnimation animation = new TranslateAnimation(0, 0, 0, translate);
                animation.setDuration(mAnimateDuration);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mReverse = false;
                        view1.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                view1.startAnimation(animation);

                animation = new TranslateAnimation(0, 0, 0, translate);
                animation.setDuration(mAnimateDuration);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mReverse = false;
                        view2.clearAnimation();
                        addToReuse(currentPosition, view2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view2.startAnimation(animation);
            }else {
                addToReuse(currentPosition, currentView);
            }
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(mShouldAutoPlay){
            if(visibility == VISIBLE){
                startTimer();
            }else {
                stopTimer();
            }
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

    //加入重用队列
    private void addToReuse(int position, View view){
        if(mShouldReuse){
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
        }
        removeView(view);
        mAdapter.onDetachToParent(view, position, mAdapter.getViewType(position));
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
                if(mReverse){
                    top -= height;
                }
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

        //添加到父视图
        public void onAttachToParent(View convertView, int position, int viewType){

        }

        //添加到父视图
        public void onDetachToParent(View convertView, int position, int viewType){

        }
    }
}
