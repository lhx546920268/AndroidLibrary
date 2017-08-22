package com.lhx.library.listView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 可悬浮item的listView
 */

public class StickListView extends ListView {

    static final String TAG = "StickListView";

    //ui回调
    private StickListViewHandler mStickListViewHandler;

    //外部的滑动监听
    private OnScrollListener mOuterOnScrollListener;

    //滑动监听
    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(mOuterOnScrollListener != null){
                mOuterOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if(mOuterOnScrollListener != null){
                mOuterOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }

            if(mStickListViewHandler != null){

                firstVisibleItem -= getHeaderViewsCount();

                if(mStickListViewHandler.shouldStickAtPosition(firstVisibleItem)){
                    View child = getChildAt(0);
                    if(child.getTop() != getPaddingTop()){
                        //当前的悬浮item已超出listView 顶部
                        layoutStickItem(firstVisibleItem, firstVisibleItem);
                    }else {
                        mStickItem = null;
                        mStickPosition = NO_POSITION;
                    }
                }else {

                    //悬浮的item已在 firstVisibleItem 前面了
                    int position = mStickListViewHandler.getCurrentStickPosition(firstVisibleItem);
                    if(position < firstVisibleItem && mStickListViewHandler.shouldStickAtPosition(position)){
                        layoutStickItem(position, firstVisibleItem);
                    }else if(firstVisibleItem < position){
                        mStickItem = null;
                        mStickPosition = NO_POSITION;
                    }
                }
            }
        }
    };

    //
    static final int NO_POSITION = -1;

    //当前要绘制的item
    private View mStickItem;

    //当前悬浮的position
    private int mStickPosition = NO_POSITION;

    //绘制时需要便宜的y，当两个悬浮item接触时，下一个会把上一个顶上去
    private int mTranslateY;

    //保存按下事件
    private MotionEvent mTouchDownEvent;

    //可以滑动的最小距离
    private int mTouchSlop;

    //开始的点击位置
    private PointF mTouchPoint = new PointF();

    public StickListView(Context context) {
        this(context, null, 0);
    }

    public StickListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnScrollListener(mOnScrollListener);
    }


    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        if(onScrollListener != mOnScrollListener){
            //保存外部的滑动监听
            mOuterOnScrollListener = onScrollListener;
        }else {
            super.setOnScrollListener(onScrollListener);
        }
    }

    public StickListViewHandler getStickListViewHandler() {
        return mStickListViewHandler;
    }

    public void setStickListViewHandler(StickListViewHandler stickListViewHandler) {
        mStickListViewHandler = stickListViewHandler;
    }

    //布局固定的item
    private void layoutStickItem(int stickPosition, int firstVisibleItem){

        stickPosition += getHeaderViewsCount();

        ListAdapter adapter = getAdapter();
        if(mStickItem == null || stickPosition != mStickPosition){
            mStickItem = adapter.getView(stickPosition, null, this);
            ViewGroup.LayoutParams params = mStickItem.getLayoutParams();
            if(params == null){
                params = generateDefaultLayoutParams();
                mStickItem.setLayoutParams(params);
            }

            //计算item大小
            int heightMode = MeasureSpec.getMode(params.height);
            int heightSize = MeasureSpec.getSize(params.height);
            if(heightMode == MeasureSpec.UNSPECIFIED)
                heightMode = MeasureSpec.EXACTLY;


            int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth() - getPaddingLeft() - getPaddingRight(),
                    MeasureSpec.EXACTLY);
            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(getHeight() - getPaddingTop() - getPaddingBottom
                    (), heightSize), heightMode);
            mStickItem.measure(widthMeasureSpec, heightMeasureSpec);
            mStickItem.layout(0, 0, mStickItem.getMeasuredWidth(), mStickItem.getMeasuredHeight());
        }

        mStickPosition = stickPosition;


        //判断下一个item
        int nextPosition = firstVisibleItem + 1;
        if(nextPosition < adapter.getCount() && mStickListViewHandler.shouldStickAtPosition(nextPosition)){
            View child = getChildAt(1);
            mTranslateY = child.getTop() - mStickItem.getBottom();
        }else {
            mTranslateY = 0;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if(mStickItem != null){

            //保存系统本身的画板属性
            canvas.save();

            canvas.translate(0, mTranslateY);
            drawChild(canvas, mStickItem, getDrawingTime());

            //恢复以前的属性
            canvas.restore();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(mStickItem != null){
            int bottom = mStickItem.getBottom();

            if(ev.getY() < bottom){

                int action = ev.getAction();
                switch (action){
                    case MotionEvent.ACTION_UP :

                        //是点击 悬浮的item
                        if(mTouchDownEvent != null){
                            mStickItem.dispatchTouchEvent(ev);
                            mTouchDownEvent.recycle();
                            mTouchDownEvent = null;

                            playSoundEffect(SoundEffectConstants.CLICK);
                            mStickItem.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
                        }

                        //必须的，否则 悬浮的item 的OnClick事件不会触发
                        super.dispatchTouchEvent(ev);
                        break;
                    case MotionEvent.ACTION_DOWN :
                        mStickItem.dispatchTouchEvent(ev);
                        mTouchDownEvent = MotionEvent.obtain(ev);
                        mTouchPoint.x = ev.getX();
                        mTouchPoint.y = ev.getY();
                        break;
                    case MotionEvent.ACTION_MOVE :

                        //只有大于 mTouchSlop 才算滑动
                        if(Math.abs(ev.getY() - mTouchPoint.y) >= mTouchSlop){

                            //滑动了，发送取消事件
                            MotionEvent event = MotionEvent.obtain(ev);
                            event.setAction(MotionEvent.ACTION_CANCEL);
                            mStickItem.dispatchTouchEvent(event);

                            //发送按下事件，否则无法滑动
                            if(mTouchDownEvent != null){
                                super.dispatchTouchEvent(mTouchDownEvent);
                                mTouchDownEvent.recycle();
                                mTouchDownEvent = null;
                            }
                            super.dispatchTouchEvent(ev);
                        }else {
                            mStickItem.dispatchTouchEvent(ev);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL :
                        mStickItem.dispatchTouchEvent(ev);
                        if(mTouchDownEvent != null){
                            mTouchDownEvent.recycle();
                            mTouchDownEvent = null;
                        }
                        break;
                }

                return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    //悬浮固定item回调
    public interface StickListViewHandler{

        //是否需要悬浮固定
        boolean shouldStickAtPosition(int position);

        //根据当前第一个可见item获取当前可悬浮固定的item
        int getCurrentStickPosition(int firstVisibleItem);
    }
}
