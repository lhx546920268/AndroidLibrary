package com.lhx.library.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
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
    final static int STATE_REFRESHING = 2;

    ///刷新将要完成
    final static int STATE_REFRESH_WILL_FINISH = 3;

    ///刷新完成
    final static int STATE_REFRESH_FINISH = 4;

    ///到达加载更多临界点
    final static int STATE_REACH_LOAD_MORE_CRITICAL = 5;

    ///将要加载更多
    final static int STATE_WILL_LOADING_MORE = 6;

    ///加载中
    final static int STATE_LOADING_MORE = 7;

    ///加载完成
    final static int STATE_LOAD_MORE_FINISH = 8;

    ///状态
    private int mState = STATE_NORMAL;

    ///是否在刷新
    private boolean mRefreshing = false;

    ///是否在加载更多
    private boolean mLoadingMore = false;

    ///是否将要加载更多
    private boolean mWillLoadingMore = false;

    ///是否正在触摸屏幕
    private boolean mTouching;

    ///最新按下事件
    private MotionEvent mLastDownEvent;

    ///是否在手动刷新
    private boolean mRefreshingManually = false;

    ///滑动到哪个位置松开可以刷新
    private int mRefreshCriticalOffset;

    ///滑动到哪个位置松开可以加载更多
    private int mLoadMoreCriticalOffset;

    ///下拉刷新和加载更多 事件回调
    private RefreshHandler mRefreshHandler;

    ///当刷新完成后未回到原来的位置，是否可以继续刷新
    private boolean mRefreshEnableBeforeScrollComplete = false;

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

    ///是否还有
    private boolean mHasMore = true;

    ///是否自动加载更多 将忽略mLoadMoreDelay
    private boolean mShouldAutoLoadMore = true;

    ///动画时间 毫秒
    private int mAnimateDuration = 1500;

    ///滑动的难度系数 越大越难滑动
    private float mScrollDifficult = 1.7f;

    ///刷新完成停留延迟 毫秒
    private int mRefreshStayDelay = 1000;

    ///加载延迟 释放后延迟加载更多 毫秒 如果mShouldAutoLoadMore 为true，将忽略
    private int mLoadMoreDelay = 1000;

    ///延迟执行
    private Handler mDelayHandler = new Handler();

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
            if(mContentView.getParent() == null)
                addView(mContentView);
            mContentView.setOverScrollMode(OVER_SCROLL_NEVER);
        }
    }

    public View getContentView() {
        return mContentView;
    }

    public void setRefreshHandler(RefreshHandler refreshHandler) {
        mRefreshHandler = refreshHandler;
    }

    public void setOrientation(@Orientation int orientation){
        if(mOrientation != orientation){
            mOrientation = orientation;
        }
    }

    public int getOrientation() {
        return mOrientation;
    }

    public float getScrollDifficult() {
        return mScrollDifficult;
    }

    public void setScrollDifficult(float scrollDifficult) {
        mScrollDifficult = scrollDifficult;
    }

    public boolean refreshEnable() {
        return mRefreshEnable;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        if(mRefreshEnable != refreshEnable){
            mRefreshEnable = refreshEnable;
            if(mHeader != null){
                mHeader.setVisibility(mRefreshEnable ? VISIBLE : INVISIBLE);
            }
        }
    }

    public boolean loadMoreEnable() {
        return mLoadMoreEnable;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        if(loadMoreEnable != mLoadMoreEnable){
            mLoadMoreEnable = loadMoreEnable;
            if(mFooter != null){
                mFooter.setVisibility((mHasMore && mLoadMoreEnable) ? VISIBLE : INVISIBLE);
            }
        }
    }

    public boolean hasMore() {
        return mHasMore;
    }

    public void setHasMore(boolean hasMore) {
        if(hasMore != mHasMore){
            mHasMore = hasMore;
            if(mFooter != null){
                mFooter.setVisibility((mHasMore && mLoadMoreEnable) ? VISIBLE : INVISIBLE);
            }
        }
    }

    public boolean shouldAutoLoadMore() {
        return mShouldAutoLoadMore;
    }

    public void setShouldAutoLoadMore(boolean shouldAutoLoadMore) {
        mShouldAutoLoadMore = shouldAutoLoadMore;
    }

    public int getRefreshStayDelay() {
        return mRefreshStayDelay;
    }

    public void setRefreshStayDelay(int mRefreshStayDelay) {
        this.mRefreshStayDelay = mRefreshStayDelay;
    }

    public int getLoadMoreDelay() {
        return mLoadMoreDelay;
    }

    public void setLoadMoreDelay(int mLoadMoreDelay) {
        this.mLoadMoreDelay = mLoadMoreDelay;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ///计算子视图的大小
        if(refreshEnable()){
            initHeader();
            if(mHeader != null){
                measureChild(mHeader, widthMeasureSpec, heightMeasureSpec);
                mRefreshCriticalOffset = mOrientation == ORIENTATION_VERTICAL ? mHeader.getMeasuredHeight() : mHeader.getMeasuredWidth();
            }
        }

        if(loadMoreEnable()){
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
            }catch (InstantiationException e){
                e.printStackTrace();
                throw new IllegalStateException("RefreshControl 默认的mFooterClass 无法实例化");
            }catch (IllegalAccessException e){
                e.printStackTrace();
                throw new IllegalStateException("RefreshControl 默认的mFooterClass 无法实例化");
            }
        }
    }

    //初始化加载更多
    private void initFooter(){
        if(mFooter == null){
            try {

                mFooterHandler = (LoadMoreUIHandler) mFooterClass.newInstance();
                mFooter = mFooterHandler.getContentView(getContext(), mOrientation);

                addView(mFooter);
                mFooter.setVisibility(mHasMore ? VISIBLE : INVISIBLE);
            }catch (InstantiationException e){
                e.printStackTrace();
                throw new IllegalStateException("RefreshControl 默认的mFooterClass 无法实例化");
            }catch (IllegalAccessException e){
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
    }

    @Override
    protected void onFinishInflate() {

        ///获取内容视图，使用xml布局
        int count = getChildCount();
        if(count > 1){

            ///子视图不能超过1个
            throw new IllegalStateException("RefreshControl 的子视图不能超过1个");
        }else {
            setContentView(getChildAt(0));
        }

        super.onFinishInflate();
    }

    ///分发取消事件
    private void dispatchCancelEvent(){
        if(mLastDownEvent == null)
            return;

        MotionEvent event = MotionEvent.obtain(mLastDownEvent.getDownTime(), mLastDownEvent.getEventTime(),
                MotionEvent.ACTION_CANCEL, mLastDownEvent.getX(), mLastDownEvent.getY(), mLastDownEvent.getMetaState());
        super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(mContentView == null)
            return super.dispatchTouchEvent(ev);

        ///手动刷新动画过程中不让用户点击
        if(mRefreshingManually)
            return true;

        int action = ev.getAction();

        switch (action){
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP :
            {
                mTouching = false;
                if(mScrollHelper.getScrollState() != RefreshControlScrollHelper.SCROLL_STATE_NORMAL && mScrollHelper.isTouchChange){
                    release();

                    //当松开手时要告诉父view是取消的，否则会触发contentView上的点击事件
                    dispatchCancelEvent();
                    return true;
                }else {
                    return super.dispatchTouchEvent(ev);
                }
            }
            case MotionEvent.ACTION_MOVE :
            {
                mTouching = true;
                float position = mOrientation == ORIENTATION_VERTICAL ? ev.getY() : ev.getX();
                if(isChildrenScrollEnable(mScrollHelper.getDirection(position))){
                    mScrollHelper.setTouchY(position);
                    return super.dispatchTouchEvent(ev);
                }

                int offset = mScrollHelper.onMove(position);

                ///自动加载更多
                if(mScrollHelper.getScrollState() == RefreshControlScrollHelper
                        .SCROLL_STATE_LOADMORE){
//                    if(mShouldAutoLoadMore && mLoadMoreEnable && mHasMore && !mLoadingMore && !mWillLoadingMore){
//                        mScrollHelper.scrollTo(mLoadMoreCriticalOffset, 0);
//                        setState(STATE_LOADING_MORE);
//                        return super.dispatchTouchEvent(ev);
//                    }
                }


                move(offset);

                mScrollHelper.setTouchY(position);

                return true;
            }
            case MotionEvent.ACTION_DOWN :
            {
                //取消刷新停留延迟
                if(mState == STATE_REFRESH_WILL_FINISH){
                    mDelayHandler.removeCallbacksAndMessages(null);
                    setState(STATE_REFRESH_FINISH);
                }
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

        int state = mScrollHelper.getScrollState();
        switch (state) {
            case RefreshControlScrollHelper.SCROLL_STATE_REFRESH: {
                if (enableRefresh()) {
                    ///已到临界点，可以刷新了
                    int offset = (int) (mScrollHelper.getOffset() - mRefreshCriticalOffset);
                    mScrollHelper.scrollTo(offset, mAnimateDuration);

                    cancelLoadMore();

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

                    //刷新中 取消刷新
                    if(mRefreshing && mRefreshHandler != null){
                        mRefreshHandler.onRefreshCancel(this);
                    }
                    setState(mLoadMoreDelay > 0 ? STATE_WILL_LOADING_MORE : STATE_LOADING_MORE);
                    return;
                }
                break;
            }
        }

        ///没有在加载，回到原来位置
        backToOrigin(0, mAnimateDuration);
    }

    ///取消加载更多
    private void cancelLoadMore(){
        //加载更多中 取消加载
        if(mLoadingMore){
            if(mRefreshHandler != null){
                mRefreshHandler.onLoadMoreCancel(this);
            }
            mScrollHelper.scrollTo(-mLoadMoreCriticalOffset, 0);
        }

        //将要加载更多种
        if(mWillLoadingMore){
            mScrollHelper.scrollTo(-mLoadMoreCriticalOffset, 0);
            mWillLoadingMore = false;
            mDelayHandler.removeCallbacksAndMessages(null);
        }
    }

    ///是否可以刷新
    private boolean enableRefresh(){

        if(!mRefreshEnable || mState == STATE_REFRESHING)
            return false;

        ///滑动到达临界点， 并且刷新完成后允许刷新
        if(isReachRefreshCritical()){
            if(mState == STATE_REFRESH_FINISH){
                return mRefreshEnableBeforeScrollComplete;
            }
            return true;
        }
        return false;
    }

    ///是否可以加载更多
    private boolean enableLoadMore(){

        if(!mLoadMoreEnable || !mHasMore || mState == STATE_LOADING_MORE || mState == STATE_WILL_LOADING_MORE)
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

    ///手动刷新
    public void startRefresh(){
        if(mState == STATE_REFRESHING || !mRefreshEnable || mContentView == null || mHeaderHandler == null)
            return;

        cancelLoadMore();
        mContentView.scrollTo(0, 0);

        mHeaderHandler.onRefresh(this);
        mRefreshingManually = true;
        mScrollHelper.scrollTo(-mRefreshCriticalOffset, mAnimateDuration);
    }

    ///完成刷新
    public void refreshComplete(){
        this.refreshComplete(true);
    }
    ///完成刷新
    public void refreshComplete(boolean success){

        if(!mRefreshing)
            return;

        mRefreshing = false;

        if(!mTouching){
            boolean delay = backToOrigin(mRefreshStayDelay, mAnimateDuration);
            setState(delay ? STATE_REFRESH_WILL_FINISH : STATE_REFRESH_FINISH);
        }else if(mRefreshEnableBeforeScrollComplete && mTouching){
            ///刷新完成后 没回到顶部也可以继续刷新
            setState(isReachRefreshCritical() ? STATE_REACH_REFRESH_CRITICAL : STATE_NORMAL);
        }else {
            setState(STATE_REFRESH_FINISH);
        }
    }

    ///加载更多完成
    public void loadMoreComplete(boolean hasMore){
        if(!mLoadingMore)
            return;

        setHasMore(hasMore);
        mLoadingMore = false;
        setState(STATE_LOAD_MORE_FINISH);
        backToOrigin(0, 0);
    }

    ///自动滚动完成
    protected void scrollComplete(){
        ///回到原来的位置，把状态改成正常的
        if(mState == STATE_LOAD_MORE_FINISH){
            setState(STATE_NORMAL);
        }else if((mState == STATE_REFRESH_FINISH && mRefreshEnableBeforeScrollComplete) || mScrollHelper
                .getScrollState() == RefreshControlScrollHelper.SCROLL_STATE_NORMAL){
            setState(STATE_NORMAL);
        }

        ///手动刷新
        if(mRefreshingManually){
            mRefreshingManually = false;
            setState(STATE_REFRESHING);
        }
    }

    /**
     * 回到原来位置
     * @param delay 延迟
     * @param duration 动画时间
     * @return 是否延迟
     */
    private boolean backToOrigin(int delay, final int duration){

        final int offset = mScrollHelper.onRelease();
        if(offset != 0){
            if(delay > 0){
                mDelayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScrollHelper.scrollTo(-offset, duration);
                    }
                }, delay);

                return true;
            }else {
                mScrollHelper.scrollTo(-offset, duration);
            }
        }else {
            setState(STATE_NORMAL);
        }

        return false;
    }

    ///移动
    protected void move(int offset){

        int state = mScrollHelper.getScrollState();

        switch (mOrientation){
            case ORIENTATION_VERTICAL : {
                if(mHeader != null && (state == RefreshControlScrollHelper.SCROLL_STATE_REFRESH || mHeader.getBottom
                        () != 0)){
                    mHeader.offsetTopAndBottom(offset);
                }

                mContentView.offsetTopAndBottom(offset);

                if(mFooter != null && (state == RefreshControlScrollHelper.SCROLL_STATE_LOADMORE || mFooter.getTop()
                        != mContentView.getBottom())){
                    mFooter.offsetTopAndBottom(offset);
                }

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

    ///设置状态
    private void setState(int state){
        if(mState != state){
            mState = state;

            switch (mState){
                case STATE_LOADING_MORE :
                    if(mRefreshing && mHeaderHandler != null){
                        mHeaderHandler.onPull(this, mScrollHelper.getOffset());
                    }
                    mRefreshing = false;
                    mLoadingMore = true;
                    mWillLoadingMore = false;
                    break;
                case STATE_REFRESHING :
                    mRefreshing = true;
                    if(mLoadingMore && mFooterHandler != null){
                        mFooterHandler.onPull(this, mScrollHelper.getOffset());
                    }
                    mLoadingMore = false;
                    mWillLoadingMore = false;
                    break;
                case STATE_WILL_LOADING_MORE :
                    if(mRefreshing && mHeaderHandler != null){
                        mHeaderHandler.onPull(this, mScrollHelper.getOffset());
                    }
                    mRefreshing = false;
                    mWillLoadingMore = true;

                    ///延迟加载更多
                    if(mRefreshHandler != null){
                        mDelayHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mState = STATE_LOADING_MORE;
                                mWillLoadingMore = false;
                                mLoadingMore = true;
                                int state = mScrollHelper.getScrollState();
                                mRefreshHandler.onLoadMore(RefreshControl.this);
                                mFooterHandler.onLoadMore(RefreshControl.this);
                            }
                        }, mLoadMoreDelay);
                    }
                    break;
            }

            refreshUI();
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
                            mHeaderHandler.onPull(this, mScrollHelper.getOffset());
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
                        case STATE_REFRESH_WILL_FINISH:
                            mHeaderHandler.onRefreshWillFinish(this, true, mRefreshStayDelay);
                            break;
                    }
                }
                break;
            }
            case RefreshControlScrollHelper.SCROLL_STATE_LOADMORE : {

                if(mFooterHandler != null){

                    switch (mState){
                        case STATE_NORMAL :
                            mFooterHandler.onPull(this, mScrollHelper.getOffset());
                            break;
                        case STATE_LOADING_MORE :
                            mFooterHandler.onLoadMore(this);
                            if(mRefreshHandler != null){
                                mRefreshHandler.onLoadMore(this);
                            }
                            break;
                        case STATE_WILL_LOADING_MORE :
                            mFooterHandler.onWillLoadMore(this, mLoadMoreDelay);
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
                    mHeaderHandler.onPull(this, mScrollHelper.getOffset());
                }

                if(mFooterHandler != null){
                    mFooterHandler.onPull(this, mScrollHelper.getOffset());
                }
                break;
            }
        }
    }

    //刷新完成 或 滑动释放后动画地回到原位
    @Override
    public void computeScroll() {

        mScrollHelper.computeScroll();
        super.computeScroll();
    }
}
