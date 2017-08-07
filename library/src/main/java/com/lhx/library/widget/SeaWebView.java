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

    public SeaWebView(Context context) {
        this(context, null, 0);
    }

    public SeaWebView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public SeaWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        WebSettings webSettings = getSettings();
        webSettings.setSupportZoom(false);
    }

    public void setHtml(String html){

        if(!TextUtils.isEmpty(html)){
            html += "<style>img {width:100%;}</style><meta name=\"viewport\" content=\"width=device-width," +
                    "initial-scale=1\"/>";
            loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        }

    }
}
