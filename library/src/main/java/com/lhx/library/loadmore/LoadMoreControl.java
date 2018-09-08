package com.lhx.library.loadmore;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.widget.OnSingleClickListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 加载更多控制器
 */

public class LoadMoreControl {

    //加载状态
    public static final int LOAD_MORE_STATUS_NORMAL = 0; //什么都没
    public static final int LOAD_MORE_STATUS_HAS_MORE = 1; //可以加载更多数据
    public static final int LOAD_MORE_STATUS_LOADING = 2; //加载中
    public static final int LOAD_MORE_STATUS_FAIL = 3; //加载失败 点击可加载
    public static final int LOAD_MORE_STATUS_NO_MORE_DATA = 4; //没有数据了

    @IntDef({LOAD_MORE_STATUS_NORMAL,
            LOAD_MORE_STATUS_HAS_MORE,
            LOAD_MORE_STATUS_LOADING,
            LOAD_MORE_STATUS_FAIL,
            LOAD_MORE_STATUS_NO_MORE_DATA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadMoreStatus{}

    ///状态
    private @LoadMoreStatus int mLoadingStatus = LOAD_MORE_STATUS_NORMAL;

    ///所有数据加载完成后是否还显示 加载更多的视图
    private boolean mShouldDisplayWhileNoMoreData = false;

    ///倒数第几个时触发加载更多 不能小于0
    private int mReciprocalToLoadMore = 0;

    ///加载更多的View
    private View mContentView;

    //所有加载完后没有数据后的视图
    private View mNoMoreDataView;

    ///上下文
    private Context mContext;

    ///菊花
    ProgressBar mProgressBar;

    //文本
    TextView mTextView;

    LoadMoreControlHandler mLoadMoreControlHandler;

    public LoadMoreControl(@NonNull Context context) {
        mContext = context;
    }

    //设置标题
    public void setTitle(String title){
        mTextView.setText(title);
    }

    public View getContentView(View reusedView){

        if(mLoadingStatus == LOAD_MORE_STATUS_NO_MORE_DATA){
            if(reusedView == null){
                mNoMoreDataView = View.inflate(mContext, R.layout.common_load_more_no_data, null);
            }else {
                mNoMoreDataView = reusedView;
            }
            return mNoMoreDataView;
        }else {
            if(reusedView == null){
                mContentView = View.inflate(mContext, R.layout.common_load_more, null);
                mProgressBar = (ProgressBar)mContentView.findViewById(R.id.progress_bar);
                mTextView = (TextView)mContentView.findViewById(R.id.text_view);
                mContentView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        if(mLoadMoreControlHandler != null && mLoadingStatus == LOAD_MORE_STATUS_HAS_MORE){
                            mLoadMoreControlHandler.onClickLoadMore();
                        }
                    }
                });
            }else {
                mContentView = reusedView;
            }
            refreshUI();
        }
        return mContentView;
    }

    public void setLoadMoreControlHandler(LoadMoreControlHandler loadMoreControlHandler) {
        mLoadMoreControlHandler = loadMoreControlHandler;
    }

    public void setShouldDisplayWhileNoMoreData(boolean shouldDisplayWhileNoMoreData) {
        mShouldDisplayWhileNoMoreData = shouldDisplayWhileNoMoreData;
    }

    public int getReciprocalToLoadMore() {
        return mReciprocalToLoadMore;
    }

    public void setReciprocalRToLoadMore(int reciprocalToLoadMore) {
        if(mReciprocalToLoadMore != reciprocalToLoadMore){
            if(reciprocalToLoadMore < 0)
                throw new IllegalArgumentException("reciprocalToLoadMore must greater than or equal to zero");
            mReciprocalToLoadMore = reciprocalToLoadMore;
        }
    }

    public void setLoadingStatus(@LoadMoreStatus int status){
        if(mLoadingStatus != status){

            //没有数据了，但是不需要再显示该视图
            if(status == LOAD_MORE_STATUS_NO_MORE_DATA && !mShouldDisplayWhileNoMoreData){
                status = LOAD_MORE_STATUS_NORMAL;
            }
            mLoadingStatus = status;
            refreshUI();
        }
    }

    //刷新UI
    private void refreshUI(){
        switch (mLoadingStatus){
            case LOAD_MORE_STATUS_NORMAL : {

                break;
            }
            case LOAD_MORE_STATUS_HAS_MORE :
            case LOAD_MORE_STATUS_LOADING : {
                if(mContentView != null){
                    mContentView.setEnabled(false);
                }
                if(mProgressBar != null){
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                if(mTextView != null){
                    mTextView.setText("加载中...");
                }
                break;
            }
            case LOAD_MORE_STATUS_FAIL : {
                if(mContentView != null){
                    mContentView.setEnabled(true);
                }
                if(mProgressBar != null){
                    mProgressBar.setVisibility(View.GONE);
                }
                if(mTextView != null){
                    mTextView.setText("加载失败，点击重新加载");
                }
                break;
            }
            case LOAD_MORE_STATUS_NO_MORE_DATA : {

                break;
            }
        }
    }

    ///是否可以加载更多
    public boolean loadMoreEnable(){
        return mLoadingStatus == LOAD_MORE_STATUS_HAS_MORE;
    }

    ///是否显示加载更多视图
    public boolean shouldDisplay(){
        return mLoadingStatus != LOAD_MORE_STATUS_NORMAL;
    }

    ///是否是没有数据的视图
    public boolean isNoData(){
        return mLoadingStatus == LOAD_MORE_STATUS_NO_MORE_DATA;
    }

    ///是否可以显示空视图
    public boolean displayEmptyViewEnable(){
        return mLoadingStatus == LOAD_MORE_STATUS_NORMAL || mLoadingStatus == LOAD_MORE_STATUS_NO_MORE_DATA;
    }

    ///是否正在加载更多
    public boolean isLoadingMore(){
        return mLoadingStatus == LOAD_MORE_STATUS_LOADING;
    }

    //加载更多控制器回调
    public interface LoadMoreControlHandler{

        //点击加载更多
        void onClickLoadMore();
    }
}
