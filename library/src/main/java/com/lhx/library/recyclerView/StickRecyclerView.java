package com.lhx.library.recyclerView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

/**
 * 可悬浮item的RecyclerView
 */

public class StickRecyclerView extends RecyclerView {

    static final String TAG = "StickRecyclerView";

    //ui回调
    private StickRecyclerViewHandler mStickRecyclerViewHandler;

    private Recycler mRecycler;

    //外部 ViewCacheExtension
    private ViewCacheExtension mOuterViewCacheExtension;

    //滑动监听
    private OnScrollListener mOnScrollListener;

    //
    static final int NO_POSITION = -1;

    //是否可以悬浮
    private boolean mStickEnable;

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
    private PointF mTouchPoint;

    public StickRecyclerView(Context context) {
        this(context, null, 0);
    }

    public StickRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setStickRecyclerViewHandler(StickRecyclerViewHandler stickRecyclerViewHandler) {
        mStickRecyclerViewHandler = stickRecyclerViewHandler;
        setStickEnable(mStickRecyclerViewHandler != null);
    }

    public void setOuterViewCacheExtension(ViewCacheExtension viewCacheExtension){
        mOuterViewCacheExtension = viewCacheExtension;
    }

    //设置是否可以悬浮
    private void setStickEnable(boolean stickEnable){

        if(mStickEnable != stickEnable){
            mStickEnable = stickEnable;

            if(mStickEnable && mOnScrollListener == null){

                mTouchPoint = new PointF();
                mOnScrollListener = new OnScrollListener() {

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                        int childCount = recyclerView.getChildCount();
                        if(mStickRecyclerViewHandler != null && childCount > 0){

                            View child = recyclerView.getChildAt(0);
                            int firstVisibleItem = recyclerView.getChildLayoutPosition(child);

                            if(mStickRecyclerViewHandler.shouldStickAtPosition(firstVisibleItem)){
                                if(child.getTop() != getPaddingTop()){
                                    //当前的悬浮item已超出listView 顶部
                                    layoutStickItem(firstVisibleItem, firstVisibleItem);
                                }else {
                                    mStickItem = null;
                                    mStickPosition = NO_POSITION;
                                }
                            }else{

                                //悬浮的item已在 firstVisibleItem 前面了
                                int position = mStickRecyclerViewHandler.getCurrentStickPosition(firstVisibleItem);
                                if(position < firstVisibleItem && mStickRecyclerViewHandler.shouldStickAtPosition(position)){
                                    layoutStickItem(position, firstVisibleItem);
                                }else if(firstVisibleItem < position){
                                    mStickItem = null;
                                    mStickPosition = NO_POSITION;
                                }
                            }
                        }
                    }
                };

                setViewCacheExtension(new ViewCacheExtension() {
                    @Override
                    public View getViewForPositionAndType(Recycler recycler, int position, int type) {

                        mRecycler = recycler;
                        if (mOuterViewCacheExtension != null) {
                            return mOuterViewCacheExtension.getViewForPositionAndType(recycler, position, type);
                        }

                        return null;
                    }
                });

                addOnScrollListener(mOnScrollListener);
            }
        }
    }

    //布局固定的item
    private void layoutStickItem(int stickPosition, int firstVisibleItem){

        Adapter adapter = getAdapter();
        if(mStickItem == null || stickPosition != mStickPosition){


            mStickItem = mRecycler.getViewForPosition(stickPosition);
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
        if(nextPosition < adapter.getItemCount() && mStickRecyclerViewHandler.shouldStickAtPosition(nextPosition)){
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
    public interface StickRecyclerViewHandler{

        //是否需要悬浮固定
        boolean shouldStickAtPosition(int position);

        //根据当前第一个可见item获取当前可悬浮固定的item
        int getCurrentStickPosition(int firstVisibleItem);
    }
}
