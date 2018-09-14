package com.lhx.library.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.just.library.WebDefaultSettingsManager;
import com.just.library.WebLifeCycle;
import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.util.StringUtil;

import java.util.Locale;

/**
 * 浏览器
 */

public class WebFragment extends AppBaseFragment implements ChromeClientCallbackManager.ReceivedTitleCallback {


    public static final String WEB_URL = "com.lhx.WEB_URL";//要打开的链接
    public static final String WEB_HTML_STRING = "com.lhx.WEB_HTML_STRING";//要加载的html
    public static final String WEB_TITLE = "com.lhx.WEB_TITLE";//默认显示的标题
    public static final String WEB_USE_WEB_TITLE = "com.lhx.WEB_USE_WEB_TITLE";//是否使用web标题
    public static final String WEB_DISPLAY_PROGRESS = "com.lhx.WEB_DISPLAY_PROGRESS";//是否显示进度条 默认显示
    public static final String WEB_DISPLAY_INDICATOR = "com.lhx.WEB_DISPLAY_INDICATOR";//是否显示菊花，默认不显示

    //浏览器
    AgentWeb mAgentWeb;

    //是否需要使用网页标题
    protected boolean mShouldUseWebTitle = true;

    //html
    protected String mHtmlString;

    //链接
    protected String mURL;

    //原始链接
    protected String mOriginURL;

    //原始标题
    protected String mOriginTitle;

    //是否显示加载进度条
    protected boolean mShouldDisplayProgress = true;

    //是否显示菊花 与进度条互斥
    protected boolean mShouldDisplayIndicator = false;

    public AgentWeb getAgentWeb() {
        return mAgentWeb;
    }

    @Override
    @CallSuper
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setContentView(R.layout.web_fragment);

        mShouldUseWebTitle = getExtraBooleanFromBundle(WEB_USE_WEB_TITLE, true);
        mShouldDisplayProgress = getExtraBooleanFromBundle(WEB_DISPLAY_PROGRESS, true);
        mShouldDisplayIndicator = getExtraBooleanFromBundle(WEB_DISPLAY_INDICATOR, false);
        if (mShouldDisplayIndicator) {
            mShouldDisplayProgress = false;
        }

        if (StringUtil.isEmpty(mURL)) {
            String url = getExtraStringFromBundle(WEB_URL);
            if (StringUtil.isEmpty(url)) {
                url = getURL();
            }

            //没有http头的加上
            if (!TextUtils.isEmpty(url)) {
                int index = url.indexOf("http://");
                if (index != 0) {
                    index = url.indexOf("https://");
                    if (index != 0) {
                        url = "http://" + url;
                    }
                }
            }
            mURL = url;
        }

        if (StringUtil.isEmpty(mHtmlString)) {
            mHtmlString = getExtraStringFromBundle(WEB_HTML_STRING);
            if (StringUtil.isEmpty(mHtmlString)) {
                mHtmlString = getHtmlString();
            }
        }

        String title = getExtraStringFromBundle(WEB_TITLE);
        if (!TextUtils.isEmpty(title)) {
            getNavigationBar().setTitle(title);
        }
        mOriginURL = mURL;
        mOriginTitle = title;

        willCreateAgentWeb();

        AgentWeb.IndicatorBuilderForFragment indicatorBuilder = AgentWeb.with(this)//
                .setAgentWebParent((ViewGroup) getContentView(), new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        AgentWeb.CommonBuilderForFragment builder;
        if (mShouldDisplayProgress) {
            builder = indicatorBuilder.setIndicatorColorWithHeight(getColor(R.color.web_progress_color), getInteger(R
                    .integer
                    .web_progress_height_dp));// 进度条
        } else {
            builder = indicatorBuilder.closeDefaultIndicator();
        }


        builder.setAgentWebWebSettings(WebDefaultSettingsManager.getInstance())//
                .setReceivedTitleCallback(this)
                .setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {

                        return false;
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                        return false;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        if (mShouldDisplayIndicator) {
                            setPageLoading(false);
                            mShouldDisplayIndicator = false;
                        }
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        if (mShouldDisplayIndicator) {
                            setPageLoading(true);
                        }
                    }
                })
                .setSecurityType(AgentWeb.SecurityType.strict);

        WebView webView = getWebView();
        if (webView != null) {
            builder.setWebView(webView);
        }
        mAgentWeb = builder.createAgentWeb()//
                .ready()//
                .go(null);

        WebSettings settings = mAgentWeb.getAgentWebSettings().getWebSettings();
        String userAgent = getCustomUserAgent();
        if (!StringUtil.isEmpty(userAgent)) {
            settings.setUserAgentString(String.format(Locale.getDefault(), "%s %s", settings.getUserAgentString(),
                    userAgent));
        }
        loadWebContent();
    }

    public void setShouldDisplayProgress(boolean shouldDisplayProgress) {
        mShouldDisplayProgress = shouldDisplayProgress;
    }

    public void setShouldUseWebTitle(boolean shouldUseWebTitle) {
        mShouldUseWebTitle = shouldUseWebTitle;
    }

    public void setShouldDisplayIndicator(boolean shouldDisplayIndicator) {
        mShouldDisplayIndicator = shouldDisplayIndicator;
    }

    public void setHtmlString(String htmlString) {
        mHtmlString = htmlString;
    }

    public void setURL(String URL) {
        mURL = URL;
    }

    //加载
    public void loadWebContent() {
        if (!StringUtil.isEmpty(mURL) || !StringUtil.isEmpty(mHtmlString)) {

            if (!StringUtil.isEmpty(mURL)) {
                mAgentWeb.getLoader().loadUrl(mURL);
            } else {
                String html = mHtmlString;
                if (shouldAddMobileMeta()) {
                    html = "<style>img {width:100%;}</style><meta name='viewport' " +
                            "content='width=device-width, initial-scale=1'/>" + mHtmlString;
                }
                mAgentWeb.getLoader().loadDataWithBaseURL(null, html, "text/html", "utf8", null);
            }
        }
    }

    //是否添加移动设备头部
    public boolean shouldAddMobileMeta() {
        return true;
    }

    @Override
    public boolean showNavigationBar() {
        return true;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (mShouldUseWebTitle) {
            if (showNavigationBar()) {
                setTitle(title);
            }
        }
    }

    @Override
    public void onResume() {
        if (mAgentWeb != null) {
            WebLifeCycle lifeCycle = mAgentWeb.getWebLifeCycle();
            if (lifeCycle != null) {
                lifeCycle.onResume();
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mAgentWeb != null) {
            WebLifeCycle lifeCycle = mAgentWeb.getWebLifeCycle();
            if (lifeCycle != null) {
                lifeCycle.onPause();
            }
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb != null) {
            return super.onKeyDown(keyCode, event) && mAgentWeb.handleKeyEvent(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onDestroy() {
        if (shouldOnDestroy()) {
            if (mAgentWeb != null) {
                WebLifeCycle lifeCycle = mAgentWeb.getWebLifeCycle();
                if (lifeCycle != null) {
                    lifeCycle.onDestroy();
                }
            }
        }

        super.onDestroy();
    }

    //将要设置agent可以在这里设置对应参数
    public void willCreateAgentWeb() {

    }

    //获取要打开的链接
    public String getURL() {
        return "";
    }

    public String getHtmlString() {
        return "";
    }

    //当前url是否可以打开
    public boolean shouldOpenURL(WebView view, String url) {
        return true;
    }

    //是否需要销毁 tabBar 需要返回 false
    public boolean shouldOnDestroy() {
        return true;
    }

    //返回需要设置的自定义 userAgent 会拼在系统的userAgent后面
    public String getCustomUserAgent() {
        return null;
    }

    //获取自定义的webView
    public WebView getWebView() {
        return null;
    }
}
