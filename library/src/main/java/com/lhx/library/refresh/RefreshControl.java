package com.lhx.library.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
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
import java.lang.reflect.Constructor;

/**
 * 刷新控制器
 */

public class RefreshControl extends ViewGroup{

    public static final String TAG = "RefreshControl";

    ///内容视图
    private View mContentView;

    ///下拉刷新头部
    private RefreshUIHandler mHeaderHandler;
    private View mHeader;
    private static Class mHeaderClass = RefreshControlHeader.class;

    ///加载更多底部
    private LoadMoreUIHandler mFooterHandler;
    private View mFooter;
    private static Class mFooterClass = RefreshControlFooter.class;

    ///滑动帮助类
    private RefreshControlScrollHelper mScrollHelper;

    ///正常状态
    final static int STATE_NORMAL = 0;

    ///到达刷新临界点
    final static int STATE_REACH_REFRESH_CRITICAL = 1;

    ///刷新中
    final static int STATE_REFRESHING= 2;

    ///刷新完成
    final static int STATE_REFRESH_FINISH = 3;

    ///到达加载更多临界点
    final static int STATE_REACH_LOAD_MORE_CRITICAL = 4;

    ///刷新中
    final static int STATE_LOADING_MORE = 5;

    ///刷新完成
    final static int STATE_LOAD_MORE_FINISH = 6;

    ///状态
    private int mState = STATE_NORMAL;

    ///是否正在触摸屏幕
    private boolean mTouching;

    ///滑动到哪个位置松开可以刷新
    private int mRefreshCriticalOffset;

    ///滑动到哪个位置松开可以加载更多
    private int mLoadMoreCriticalOffset;

    ///下拉刷新和加载更多 事件回调
    private RefreshHandler mRefreshHandler;

    ///当刷新完成后未回到原来的位置，是否可以继续刷新
    private boolean mShouldRefreshBeforeScrollComplete = false;

    ///垂直
    public static final int ORIENTATION_VERTICAL = 0;

    ///水平
    public static final int ORIENTATION_HORIZONTAL = 1;

    //刷新方向注解
    @IntDef({ORIENTATION_VERTICAL, ORIENTATION_HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation{}

    ///滑动方向
    private @Orientation int mOrientation = ORIENTATION_VERTICAL;

    ///是否可以下拉刷新
    private boolean mRefreshEnable = true;

    ///是否可以上拉加载
    private boolean mLoadMoreEnable = true;

    ///动画时间 毫秒
    private int mAnimateDuration = 1500;

    ///滑动的难度系数 越大越难滑动
    private float mScrollDifficult = 1.7f;

    public RefreshControl(Context context) {
        this(context, null);
    }

    public RefreshControl(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(!isInEditMode()){
            mScrollHelper = new RefreshControlScrollHelper(this);
        }
    }

    /**
     * 设置默认的刷新头部
     * @param headerClass 、
     */
    public static void setDefaultHeader(@NonNull Class headerClass){
        mHeaderClass = headerClass;
    }

    /**
     * 设置默认的加载更多的头部
     * @param footerClass 、
     */
    public static void setDefaultFooter(@NonNull Class footerClass){
        mFooterClass = footerClass;
    }

    /**
     * 设置下拉刷新的头部，如果没有显示地设置，将使用默认头部
     * @param header 下拉刷新头部
     */
    public void setHeader(RefreshUIHandler header){

        ///移除旧的头部
        if(mHeader != null){
            removeView(mHeader);
            mHeader = null;
        }
        mHeaderHandler = header;
        if(mHeaderHandler != null){

            mHeader = mHeaderHandler.getContentView(getContext(), mOrientation);
            addView(mHeader, 0);
        }
    }

    /**
     * 设置上拉加载底部，如果没有显示地设置，将使用默认底部
     * @param footer 上拉加载底部
     */
    public void setFooter(LoadMoreUIHandler footer) {
        if(mFooter != null){
            removeView(mFooter);
        }
        mFooterHandler = footer;

        if(mFooterHandler != null){
            mFooter = mFooterHandler.getContentView(getContext(), mOrientation);
            addView(mFooter, 1);
        }
    }

    /**
     * 设置内容视图
     * @param contentView 内容视图
     */
    public void setContentView(View contentView) {
        if(mContentView != null){
            removeView(mContentView);
        }
        mContentView = contentView;

        if(mContentView != null){
            addView(mContentView, 2);
        }
    }

    public View getContentView() {
        return mContentView;
    }

    ///设置状态
    private void setState(int state){
        if(mState != state){
            mState = state;

            refreshUI();
        }
    }

    public void setRefreshHandler(RefreshHandler refreshHandler) {
        mRefreshHandler = refreshHandler;
    }

    public void setOrientation(@Orientation int orientation){
        mOrientation = orientation;
    }

    public boolean isRefreshEnable() {
        return mRefreshEnable;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        mRefreshEnable = refreshEnable;
    }

    public boolean isLoadMoreEnable() {
        return mLoadMoreEnable;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        mLoadMoreEnable = loadMoreEnable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ///计算子视图的大小
        if(isRefreshEnable()){
            initHeader();
            if(mHeader != null){
                measureChild(mHeader, widthMeasureSpec, heightMeasureSpec);
                mRefreshCriticalOffset = mOrientation == ORIENTATION_VERTICAL ? mHeader.getMeasuredHeight() : mHeader.getMeasuredWidth();
            }
        }

        if(isLoadMoreEnable()){
            initFooter();
            if(mFooter != null){
                measureChild(mFooter, widthMeasureSpec, heightMeasureSpec);
                mLoadMoreCriticalOffset = mOrientation == ORIENTATION_VERTICAL ? mFooter.getMeasuredHeight() :
                        mFooter.getMeasuredWidth();
            }
        }

        if(mContentView != null){
            mContentView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    //初始化头部
    private void initHeader(){
        if(mHeader == null){
            try {

                mHeaderHandler = (RefreshUIHandler)mHeaderClass.newInstance();
                mHeader = mHeaderHandler.getContentView(getContext(), mOrientation);

                addView(mHeader, 0);
            }catch (Exception e){
                e.printStackTrace();
                throw new IllegalStateException("RefreshControl 默认的mHeaderClass 无法实例化");
            }
        }
    }

    //初始化加载更多
    private void initFooter(){
        if(mFooter == null){
            try {

                mFooterHandler = (LoadMoreUIHandler) mFooterClass.newInstance();
                mFooter = mFooterHandler.getContentView(getContext(), mOrientation);

                addView(mFooter, 2);
            }catch (Exception e){
                e.printStackTrace();
                throw new IllegalStateException("RefreshControl 默认的mFooterClass 无法实例化");
            }
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
                    int top = - height - paddingTop + Math.max(0, mScrollHelper.getOffset());
                    int bottom = top + height;

                    mHeader.layout(left, top, right, bottom);
                    break;
                }
                case ORIENTATION_HORIZONTAL : {
                    int width = mHeader.getMeasuredWidth();
                    int height = mHeader.getMeasuredHeight();
                    int left = - width - paddingLeft + Math.max(0, mScrollHelper.getOffset());
                    int right = left + width;
                    int top = paddingTop;
                    int bottom = top + height;

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
            int top = paddingTop + mScrollHelper.getOffset();
            int bottom = top + height;
            mContentView.layout(left, top, right, bottom);
        }

        if(mFooter != null){
            switch (mOrientation){
                case ORIENTATION_VERTICAL : {
                    int left = paddingLeft;
                    int width = mFooter.getMeasuredWidth();
                    int height = mFooter.getMeasuredHeight();
                    int right = left + width;
                    int top = mContentView.getBottom();
                    int bottom = top + height;

                    mFooter.layout(left, top, right, bottom);
                    break;
                }
                case ORIENTATION_HORIZONTAL : {
                    int width = mFooter.getMeasuredWidth();
                    int height = mFooter.getMeasuredHeight();
                    int left = mContentView.getRight();
                    int right = left + width;
                    int top = paddingTop;
                    int bottom = top + height;

                    mFooter.layout(left, top, right, bottom);
                    break;
                }
            }
        }

        Log.d(TAG, "mHeader.top = " + mHeader.getTop() + "," + "mContentView.top = " + mContentView.getTop());
    }

    @Override
    protected void onFinishInflate() {

        ///获取内容视图，使用xml布局
        int count = getChildCount();
        if(count > 1){

            ///子视图不能超过1个
            throw new IllegalStateException("RefreshControl 的子视图不能超过1个");
        }else {
            mContentView = getChildAt(0);
        }

        super.onFinishInflate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(mContentView == null)
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
                if(isChildrenScrollEnable(mScrollHelper.getDirection(position))){
                    mScrollHelper.setTouchY(position);
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

                mScrollHelper.finishScroll();
                mScrollHelper.setTouchY(position);
                super.dispatchTouchEvent(ev);
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    ///子视图是否可以滑动
    private boolean isChildrenScrollEnable(int position){

        //头部或底部还在可见状态
        if(mScrollHelper.isHeaderOrFooterScrollEnable(position)){
            return false;
        }

        return mOrientation == ORIENTATION_VERTICAL ? mContentView.canScrollVertically(position) : mContentView
                    .canScrollHorizontally(position);
    }

    ///松开
    private void release(){

        mTouching = false;

        int state = mScrollHelper.getScrollState();
        switch (state) {
            case RefreshControlScrollHelper.SCROLL_STATE_REFRESH: {
                if (enableRefresh()) {
                    ///已到临界点，可以刷新了
                    int offset = (int) (mScrollHelper.getOffset() - mRefreshCriticalOffset);
                    mScrollHelper.scrollTo(offset, mAnimateDuration);

                    setState(STATE_REFRESHING);
                    return;
                }
                break;
            }
            case RefreshControlScrollHelper.SCROLL_STATE_LOADMORE: {

                ///可以加载更多
                if(enableLoadMore()){
                    int offset = (int)(mScrollHelper.getOffset() + mLoadMoreCriticalOffset);
                    mScrollHelper.scrollTo(offset, mAnimateDuration);

                    setState(STATE_LOADING_MORE);
                    return;
                }
                break;
            }
        }

        ///没有在加载，回到原来位置
        if(mState != STATE_REFRESH_FINISH){
            backToOrigin();
        }
    }

    ///是否可以刷新
    private boolean enableRefresh(){

        if(!mRefreshEnable)
            return false;

        ///滑动到达临界点， 并且刷新完成后允许刷新
        if(isReachRefreshCritical()){
            if(mState == STATE_REFRESH_FINISH){
                return mShouldRefreshBeforeScrollComplete;
            }
            return true;
        }
        return false;
    }

    ///是否可以加载更多
    private boolean enableLoadMore(){

        if(!mLoadMoreEnable)
            return false;

        return isReachLoadMoreCritical();
    }

    ///是否已到达刷新临界点
    private boolean isReachRefreshCritical(){
        return mScrollHelper.getOffset() >= mRefreshCriticalOffset;
    }

    ///是否已到达加载更多临界点
    private boolean isReachLoadMoreCritical(){
        return -mScrollHelper.getOffset() >= mLoadMoreCriticalOffset;
    }

    ///完成刷新
    public void refreshComplete(){

        if(mState != STATE_REFRESHING)
            return;

        setState(STATE_REFRESH_FINISH);
        if(!mTouching){

            backToOrigin();
        }else if(mShouldRefreshBeforeScrollComplete && mTouching){
            ///刷新完成后 没回到顶部也可以继续刷新
            setState(isReachRefreshCritical() ? STATE_REACH_REFRESH_CRITICAL : STATE_NORMAL);
        }
    }

    ///加载更多完成
    public void loadMoreComplete(){
        if(mState != STATE_LOADING_MORE)
            return;

        setState(STATE_LOAD_MORE_FINISH);
        if(!mTouching){
            backToOrigin();
        }else {
            setState(isReachLoadMoreCritical() ? STATE_REACH_LOAD_MORE_CRITICAL : STATE_NORMAL);
        }
    }

    ///回到原来位置
    private void backToOrigin(){

        int offset = mScrollHelper.onRelease();
        if(offset != 0){
            mScrollHelper.scrollTo(-offset, mAnimateDuration);
        }else {
            setState(STATE_NORMAL);
        }
    }

    ///移动
    private void move(int offset){

        Log.v(TAG, "offset = " + offset);
        int state = mScrollHelper.getScrollState();

        switch (mOrientation){
            case ORIENTATION_VERTICAL : {
                if(mHeader != null && (state == RefreshControlScrollHelper.SCROLL_STATE_REFRESH || mHeader.getBottom
                        () != 0)){
                    mHeader.offsetTopAndBottom(offset);
                }

                if(mFooter != null && state == RefreshControlScrollHelper.SCROLL_STATE_LOADMORE){
                    mFooter.offsetTopAndBottom(offset);
                }
                mContentView.offsetTopAndBottom(offset);

                break;
            }
            case ORIENTATION_HORIZONTAL : {
                if(mHeader != null){
                    mHeader.offsetLeftAndRight(offset);
                }
                if(mFooter != null){
                    mFooter.offsetLeftAndRight(offset);
                }
                mContentView.offsetLeftAndRight(offset);
                break;
            }
        }

        ///不是加载中，刷新头部或底部的UI
        if(mTouching){
            switch (state) {
                case RefreshControlScrollHelper.SCROLL_STATE_REFRESH: {
                    if(mState != STATE_REFRESHING && mState != STATE_REFRESH_FINISH){
                        setState(isReachRefreshCritical() ? STATE_REACH_REFRESH_CRITICAL : STATE_NORMAL);
                    }
                    break;
                }
                case RefreshControlScrollHelper.SCROLL_STATE_LOADMORE: {
                    if(mState != STATE_LOADING_MORE && mState != STATE_LOAD_MORE_FINISH){
                        setState(isReachLoadMoreCritical() ? STATE_REACH_LOAD_MORE_CRITICAL : STATE_NORMAL);
                    }
                    break;
                }
            }
        }
    }

    ///刷新UI
    private void refreshUI(){

        int state = mScrollHelper.getScrollState();
        switch (state){
            case RefreshControlScrollHelper.SCROLL_STATE_REFRESH : {
                if (mHeaderHandler != null) {

                    switch (mState) {
                        case STATE_NORMAL:
                            mHeaderHandler.onPull(this);
                            break;
                        case STATE_REACH_REFRESH_CRITICAL:
                            mHeaderHandler.onReachCriticalPoint(this);
                            break;
                        case STATE_REFRESHING:
                            mHeaderHandler.onRefresh(this);
                            if (mRefreshHandler != null) {
                                mRefreshHandler.onRefresh(this);
                            }
                            break;
                        case STATE_REFRESH_FINISH:
                            mHeaderHandler.onRefreshFinish(this, true);
                            break;
                    }
                }
                break;
            }
            case RefreshControlScrollHelper.SCROLL_STATE_LOADMORE : {

                if(mFooterHandler != null){

                    switch (mState){
                        case STATE_NORMAL :
                            mFooterHandler.onPull(this);
                            break;
                        case STATE_LOADING_MORE :
                            mFooterHandler.onLoadMore(this);
                            if(mRefreshHandler != null){
                                mRefreshHandler.onLoadMore(this);
                            }
                            break;
                        case STATE_LOAD_MORE_FINISH :
                            mFooterHandler.onLoadMoreFinish(this, true);
                            break;
                    }
                }
                break;
            }
            case RefreshControlScrollHelper.SCROLL_STATE_NORMAL : {
                if(mHeaderHandler != null){
                    mHeaderHandler.onPull(this);
                }

                if(mFooterHandler != null){
                    mFooterHandler.onPull(this);
                }
                break;
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

        ///
        Scroller mScroller;

        /// mScroller 当前y
        int mCurY;

        /// 当前滑动的状态
        public static final int SCROLL_STATE_NORMAL = 0; //正常
        public static final int SCROLL_STATE_REFRESH = 1; //刷新
        public static final int SCROLL_STATE_LOADMORE = 2; //加载更多
//        @IntDef({SCROLL_STATE_NORMAL, SCROLL_STATE_REFRESH, SCROLL_STATE_LOADMORE})
//        @Retention(RetentionPolicy.SOURCE)
//        public @interface ScrollState{}

        private int mScrollState = SCROLL_STATE_NORMAL;

        RefreshControlScrollHelper(RefreshControl refreshControl) {

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
            if(mOffset > 0){
                ///下拉刷新，恢复时不需要难度系数
                if(y > mTouchY){
                    distance /= mScrollDifficult + Math.abs(mOffset) * 5 / getHeight();
                }
            }else if(mOffset < 0){
                ///上拉加载，恢复时不需要难度系数
                if(y < mTouchY){
                    distance /= mScrollDifficult + Math.abs(mOffset) * 5 / getHeight();
                }
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
                mScroller.startScroll(0, 0, 0, to, duration);
                invalidate();
            }
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
             if(mState == STATE_REFRESH_FINISH || mState == STATE_LOAD_MORE_FINISH){
                    setState(STATE_NORMAL);
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
