package com.lhx.library.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * web
 */

public class SeaWebView extends WebView {

    //滑动回调
    private OnScrollChangeListener mOnScrollChangeListener;

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

    //回调
    public interface OnScrollChangeListener{

        //
        void onScrollChange(SeaWebView webView, int y, float contentHeight);
    }
}
