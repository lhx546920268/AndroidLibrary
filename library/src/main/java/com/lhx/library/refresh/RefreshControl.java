package com.lhx.library.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Scroller;

import com.lhx.library.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 刷新控制器
 */

public class RefreshControl extends ViewGroup{

    public static final String TAG = "RefreshControl";

    ///内容视图
    private View mContentView;

    ///内容视图 xml id
    private int mContentViewId;

    ///下拉刷新头部
    private View mHeader;

    ///下拉刷新头部xml id
    private int mHeaderId;

    ///加载更多底部
    private View mFooter;

    ///加载更多底部 xml id
    private int mFooterId;

    ///滑动帮助类
    private RefreshControlScrollHelper mScrollHelper;

    ///正常状态
    final static int STATE_NORMAL = 0;

    ///到达临界点
    final static int STATE_REACH_CRITICAL = 1;

    ///加载中
    final static int STATE_LOADING = 2;

    ///加载完成
    final static int STATE_FINISH = 3;

    ///状态
    private int mState = STATE_NORMAL;

    ///是否正在触摸屏幕
    private boolean mTouching;

    ///滑动到哪个位置松开可以刷新
    private int mCriticalOffset;

    ///下拉刷新回调
    private RefreshUIHandler mRefreshUIHandler;

    ///加载更多回调
    private RefreshUIHandler mLoadMoreUIHandler;

    ///下拉刷新和加载更多 事件回调
    private RefreshHandler mRefreshHandler;

    ///是否是要下拉刷新
    private boolean mTargetRefresh;

    ///当刷新完成后未回到原来的位置，是否可以继续刷新
    private boolean mShouldRefreshBeforeScrollComplete = false;

    ///垂直
    public static final int ORIENTATION_VERTICAL = 0;

    ///水平
    public static final int ORIENTATION_HORIZONTAL = 1;

    @IntDef({ORIENTATION_VERTICAL, ORIENTATION_HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation{}

    ///滑动方向
    private @Orientation int mOrientation = ORIENTATION_VERTICAL;

    public RefreshControl(Context context) {
        this(context, null);
    }

    public RefreshControl(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScrollHelper = new RefreshControlScrollHelper(this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RefreshControl);
        if(array != null){

            mHeaderId = array.getResourceId(R.styleable.RefreshControl_header_id, 0);
            mContentViewId = array.getResourceId(R.styleable.RefreshControl_content_id, 0);
            mFooterId = array.getResourceId(R.styleable.RefreshControl_footer_id, 0);

            array.recycle();
        }
    }

    /**
     * 设置下拉刷新的头部
     * @param header 下拉刷新头部
     */
    public void setHeader(View header){

        ///移除旧的头部
        if(mHeader != null){

            removeView(mHeader);
        }
        mHeader = header;
        if(mHeader != null){

            addView(mHeader, 0);
        }
    }

    public View getHeader() {
        return mHeader;
    }

    public void setHeaderId(int headerId) {
        mHeaderId = headerId;
    }

    public int getHeaderId() {
        return mHeaderId;
    }

    public void setFooter(View footer) {
        mFooter = footer;
    }

    public View getFooter() {
        return mFooter;
    }

    public void setFooterId(int footerId) {
        mFooterId = footerId;
    }

    public int getFooterId(){
        return mFooterId;
    }

    public void setContentView(View contentView) {
        mContentView = contentView;
    }

    public View getContentView() {
        return mContentView;
    }

    public int getContentViewId() {
        return mContentViewId;
    }

    public void setContentViewId(int contentViewId) {
        mContentViewId = contentViewId;
    }

    public void setRefreshUIHandler(RefreshUIHandler refreshUIHandler) {
        mLoadMoreUIHandler = refreshUIHandler;
    }

    public void setLoadMoreUIHandler(RefreshUIHandler loadMoreUIHandler) {
        mLoadMoreUIHandler = loadMoreUIHandler;
    }

    public void setRefreshHandler(RefreshHandler refreshHandler) {
        mRefreshHandler = refreshHandler;
    }

    public void setOrientation(@Orientation int orientation){
        mOrientation = orientation;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ///计算子视图的大小
        if(mHeader != null){

            measureChild(mHeader, widthMeasureSpec, heightMeasureSpec);
            mCriticalOffset = mOrientation == ORIENTATION_VERTICAL ? mHeader.getMeasuredHeight() : mHeader.getMeasuredWidth();
        }

        if(mContentView != null){

            mContentView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        ///布局子视图
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if(mHeader != null){

            switch (mOrientation){
                case ORIENTATION_VERTICAL : {
                    int left = paddingLeft;
                    int width = mHeader.getMeasuredWidth();
                    int height = mHeader.getMeasuredHeight();
                    int right = left + width;
                    int top = - height - paddingTop;
                    int bottom = 0;

                    mHeader.layout(left, top, right, bottom);
                    break;
                }
                case ORIENTATION_HORIZONTAL : {
                    int width = mHeader.getMeasuredWidth();
                    int height = mHeader.getMeasuredHeight();
                    int left = - width - paddingLeft;
                    int right = 0;
                    int top = paddingTop;
                    int bottom = paddingTop + height;

                    mHeader.layout(left, top, right, bottom);
                    break;
                }
            }
        }

        if(mContentView != null){

            int left = paddingLeft;
            int width = mContentView.getMeasuredWidth();
            int height = mContentView.getMeasuredHeight();
            int right = left + width;
            int top = 0;
            int bottom = top + height;
            mContentView.layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onFinishInflate() {

        ///获取头部、底部、内容视图，使用xml布局
        int count = getChildCount();
        if(count > 3){

            ///子视图不能超过3个
            throw new IllegalStateException("RefreshControl 的子视图不能超过3个");
        }else {

            if(mHeader == null && mHeaderId != 0){
                mHeader = findViewById(mHeaderId);
            }

            if(mContentView == null && mContentViewId != 0){
                mContentView = findViewById(mContentViewId);
            }

            if(mFooter == null && mFooterId != 0){
                mFooter = findViewById(mFooterId);
            }

            mHeader = getChildAt(0);
            mContentView = getChildAt(1);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(mContentView == null || (mHeader == null && mFooter == null))
            return super.dispatchTouchEvent(ev);

        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP :
            {
                release();
                return true;
            }
            case MotionEvent.ACTION_MOVE :
            {
                float position = mOrientation == ORIENTATION_VERTICAL ? ev.getY() : ev.getX();
                if(checkChildsEnableToScroll(mScrollHelper.getDirection(position))){
                    return super.dispatchTouchEvent(ev);
                }

                int offset = mScrollHelper.onMove(position);
                move(offset);

                mScrollHelper.setTouchY(position);
                mTouching = true;
                return true;
            }
            case MotionEvent.ACTION_DOWN :
            {
                float position = mOrientation == ORIENTATION_VERTICAL ? ev.getY() : ev.getX();
                mTargetRefresh = true;
                mScrollHelper.finishScroll();
                mScrollHelper.setTouchY(position);
                super.dispatchTouchEvent(ev);
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    ///子视图是否可以滑动
    private boolean checkChildsEnableToScroll(int direction){

        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mContentView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mContentView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 ||
                        (mOrientation == ORIENTATION_VERTICAL ? absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop() : absListView.getChildAt(0)
                        .getLeft() < absListView.getPaddingLeft()));
            } else {
                return mOrientation == ORIENTATION_VERTICAL ? mContentView.getScrollY() > 0 : mContentView.getScrollX
                        () > 0;
            }
        } else {

//            Log.v(TAG, mContentView.canScrollHorizontally(direction) + "");
            return mOrientation == ORIENTATION_VERTICAL ? mContentView.canScrollVertically(direction) : mContentView
                    .canScrollHorizontally(direction);
        }
    }

    ///松开
    private void release(){

        mTouching = false;
        if(enableRefresh()){
            ///已到临界点，可以刷新了
            int offset = (int)(mScrollHelper.getOffset() - mCriticalOffset);
            mScrollHelper.scrollTo(offset, 1500);

            if(mState != STATE_LOADING){
                mState = STATE_LOADING;
                refreshUI();
            }
        }else {

            ///没有在加载，回到原来位置
            if(mState != STATE_LOADING){
                backToOrigin();
            }
        }
    }

    ///是否可以刷新
    private boolean enableRefresh(){

        ///滑动到达临界点， 并且刷新完成后允许刷新
        if(isReachCritical()){
            if(mState == STATE_FINISH){
                return mShouldRefreshBeforeScrollComplete;
            }
            return true;
        }
        return false;
    }

    ///是否已到达临界点
    private boolean isReachCritical(){
        return mScrollHelper.getOffset() >= mCriticalOffset;
    }

    ///完成刷新
    public void onFinishLoading(){

        if(mState != STATE_LOADING)
            return;

        mState = STATE_FINISH;
        refreshUI();
        if(!mTouching){

            ///刷新完成后 没回到顶部也可以继续刷新
            if(mShouldRefreshBeforeScrollComplete)
            {
                mState = isReachCritical() ? STATE_REACH_CRITICAL : STATE_NORMAL;
                refreshUI();
            }
            backToOrigin();
        }
    }

    ///回到原来位置
    private void backToOrigin(){

        int offset = mScrollHelper.onRelease();
        mScrollHelper.scrollTo(-offset, 1500);
    }

    ///移动
    private void move(int offset){

        switch (mOrientation){
            case ORIENTATION_VERTICAL : {
                mHeader.offsetTopAndBottom(offset);
                mContentView.offsetTopAndBottom(offset);
                break;
            }
            case ORIENTATION_HORIZONTAL : {
                mHeader.offsetLeftAndRight(offset);
                mContentView.offsetLeftAndRight(offset);
                break;
            }
        }

        ///不是加载中，刷新头部或底部的UI
        if(mState != STATE_LOADING && mState != STATE_FINISH){

            mState = isReachCritical() ? STATE_REACH_CRITICAL : STATE_NORMAL;
            refreshUI();
        }
    }

    ///刷新UI
    private void refreshUI(){

        if(mTargetRefresh){
            if(mRefreshUIHandler != null){

                switch (mState){
                    case STATE_NORMAL :
                        mRefreshUIHandler.onMove(mScrollHelper.getOffset(), this);
                        break;
                    case STATE_LOADING :
                        mRefreshUIHandler.onLoading(this);
                        break;
                    case STATE_FINISH :
                        mRefreshUIHandler.onFinish(this);
                        break;
                }
            }
            if(mState == STATE_LOADING && mRefreshHandler != null){
                mRefreshHandler.onBeginRefresh(this);
            }
        }else {
            if(mLoadMoreUIHandler != null){

                switch (mState){
                    case STATE_NORMAL :
                        mLoadMoreUIHandler.onMove(mScrollHelper.getOffset(), this);
                        break;
                    case STATE_LOADING :
                        mLoadMoreUIHandler.onLoading(this);
                        break;
                    case STATE_FINISH :
                        mLoadMoreUIHandler.onFinish(this);
                        break;
                }
            }
            if(mState == STATE_LOADING && mRefreshHandler != null){
                mRefreshHandler.onLoadMore(this);
            }
        }
    }

    @Override
    public void computeScroll() {

        mScrollHelper.computeScroll();
        super.computeScroll();
    }

    /**
     * 刷新控制器 滑动帮助类
     */

    private class RefreshControlScrollHelper{

        ///当前偏移的距离
        private int mOffset;

        ///当前触摸的位置，y轴
        private float mTouchY;

        ///起点
        private int mStartPoint = 0;

        ///
        Scroller mScroller;

        /// mScroller 当前y
        int mCurY;

        RefreshControlScrollHelper(RefreshControl refreshControl) {

            mScroller = new Scroller(refreshControl.getContext());
        }

        void setTouchY(float touchY) {
            mTouchY = touchY;
        }

        public float getOffset() {
            return mOffset;
        }

        void setStartPoint(int startPoint){
            mStartPoint = startPoint;
        }

        ///获取需要滑动的距离
        int onMove(float y){

            boolean pullUp = isPullUp(y);
            int distance = (int)(y - mTouchY);
            if(pullUp){
                distance = Math.max(distance, mStartPoint - mOffset);
            }

            mTouchY = y;
            mOffset += distance;
            return distance;
        }

        ///松开回到原来的位置
        int onRelease(){

            int offset = mStartPoint - mOffset;
            return offset;
        }

        ///滑动方向 正数为 右、下  负数为左、上
        int getDirection(float cur){
            return (int)(mTouchY - cur);
        }

        ///是否已在顶部
        public boolean atTheTop(){
            return mOffset == 0;
        }

        ///是否是向上拉
        boolean isPullUp(float y){
            return y < mTouchY;
        }

        ///滑动到某个位置
        void scrollTo(int to, int duration){

            finishScroll();
            mCurY = 0;
            mScroller.startScroll(0, 0, 0, to, duration);
            invalidate();
        }

        ///滑动计算
        void computeScroll(){
            if(mScroller.computeScrollOffset() && !mScroller.isFinished()){

                int offset = 0;
                if(mOrientation == ORIENTATION_VERTICAL){
                    offset = mCurY - mScroller.getCurrY();
                    mCurY = mScroller.getCurrY();
                }else {
                    offset = mCurY - mScroller.getCurrY();
                    mCurY = mScroller.getCurrY();
                }
                move(offset);
                mOffset += offset;
                postInvalidate();
            }else {

                ///回到原来的位置，把状态改成正常的
             if(mState == STATE_FINISH){
                    mState = STATE_NORMAL;
                }
            }
        }

        ///完成滚动
        void finishScroll(){

            if(!mScroller.isFinished()){
                mScroller.forceFinished(true);
            }
        }
    }
}
