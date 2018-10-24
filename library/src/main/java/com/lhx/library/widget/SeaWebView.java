package com.lhx.library.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * web
 */

public class SeaWebView extends WebView {

    //滑动回调
    private OnScrollChangeListener mOnScrollChangeListener;

    //水平滑动
    private boolean mClampedX = false;

    //关联的viewPager 防止webView和viewPager 滑动冲突
    private ViewPager mViewPager;

    //是否需要获取viewPager
    private boolean mShouldGetViewPager = true;

    public SeaWebView(Context context) {
        this(context, null, 0);
    }

    public SeaWebView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public SeaWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHtml(String html){

        if(!TextUtils.isEmpty(html)){
            html += "<style>img {width:100%;}</style><meta name=\"viewport\" content=\"width=device-width," +
                    "initial-scale=1\"/>";
            loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        mOnScrollChangeListener = onScrollChangeListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if(mOnScrollChangeListener != null){
            mOnScrollChangeListener.onScrollChange(this, getScrollY(), getContentHeight() * getScaleY());
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mViewPager == null && mShouldGetViewPager){
            mShouldGetViewPager = false;
            mViewPager = getViewPager(getParent());
        }

        if(mViewPager != null){
            if (event.getPointerCount() == 1) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mClampedX = false;
                        //事件由webView处理
                        mViewPager.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //嵌套Viewpager时
                        mViewPager.requestDisallowInterceptTouchEvent(!mClampedX);
                        break;
                    default:
                        mViewPager.requestDisallowInterceptTouchEvent(false);
                }
            } else {
                //使webView可以双指缩放(前提是webView必须开启缩放功能,并且加载的网页也支持缩放)
                mViewPager.requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.onTouchEvent(event);
    }

    //获取对应viewPager
    private ViewPager getViewPager(ViewParent viewParent){
        if(viewParent != null){
            if(viewParent instanceof ViewPager){
                return (ViewPager)viewParent;
            }else {
                return getViewPager(viewParent.getParent());
            }
        }
        return null;
    }

    //当webView滚动到边界时执行
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        mClampedX = clampedX;
    }



    //回调
    public interface OnScrollChangeListener{

        //webView滑动改变
        void onScrollChange(SeaWebView webView, int y, float contentHeight);
    }
}
