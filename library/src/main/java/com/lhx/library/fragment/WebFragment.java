package com.lhx.library.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.progress.ProgressBar;
import com.lhx.library.util.StringUtil;
import com.lhx.library.widget.SeaWebView;

import java.util.Locale;

/**
 * 浏览器
 */

@SuppressLint("SetJavaScriptEnabled")
public class WebFragment extends AppBaseFragment {


    public static final String WEB_URL = "com.lhx.WEB_URL";//要打开的链接
    public static final String WEB_HTML_STRING = "com.lhx.WEB_HTML_STRING";//要加载的html
    public static final String WEB_TITLE = "com.lhx.WEB_TITLE";//默认显示的标题
    public static final String WEB_USE_WEB_TITLE = "com.lhx.WEB_USE_WEB_TITLE";//是否使用web标题
    public static final String WEB_DISPLAY_PROGRESS = "com.lhx.WEB_DISPLAY_PROGRESS";//是否显示进度条 默认显示
    public static final String WEB_DISPLAY_INDICATOR = "com.lhx.WEB_DISPLAY_INDICATOR";//是否显示菊花，默认不显示

    //文件选择
    private static final int FILE_CHOOSER_REQUEST_CODE = 1101;

    //浏览器
    protected SeaWebView mWebView;

    //进度条
    protected ProgressBar mProgressBar;

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

    //是否需要清除历史
    private boolean mShouldClearHistory;

    //上传文件回调
    private ValueCallback<Uri[]> mUploadFileCallback;
    private ValueCallback<Uri> mUploadMsg;

    //返回自定义的 layout res
    public @LayoutRes
    int getContentRes(){
        return 0;
    }

    @Override
    @CallSuper
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        int res = getContentRes();
        if(res != 0){
            setContentView(res);
        }else {
            setContentView(R.layout.web_fragment);
        }

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

        mWebView = findViewById(R.id.web_view);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);

        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setProgressColor(getColor(R.color.web_progress_color));

        WebSettings settings = mWebView.getSettings();
        String userAgent = getCustomUserAgent();
        if (!StringUtil.isEmpty(userAgent)) {
            settings.setUserAgentString(String.format(Locale.getDefault(), "%s %s", settings.getUserAgentString(),
                    userAgent));
        }

        settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放,将图片调整到适合webview的大小

        // 便页面支持缩放：
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setDefaultFontSize(12);
        settings.setDefaultFixedFontSize(12);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        settings.setSupportMultipleWindows(false);
        settings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            settings.setAllowFileAccessFromFileURLs(false);
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL); // 支持内容重新布局

        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        settings.setBlockNetworkImage(false);

        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        settings.setDomStorageEnabled(true); //设置适应Html5
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //设置缓存
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);

        settings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        String dir = mContext.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setGeolocationDatabasePath(dir);


        loadWebContent();
    }

    @Override
    public void onDestroy() {
        if(mWebView != null){
            mWebView.destroy();
        }
        super.onDestroy();
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
        if(mWebView == null)
            return;
        if (!StringUtil.isEmpty(mURL) || !StringUtil.isEmpty(mHtmlString)) {

            mShouldClearHistory = true;
            if (!StringUtil.isEmpty(mURL)) {
                mWebView.loadUrl(mURL);
            } else {
                String html = mHtmlString;
                if (shouldAddMobileMeta()) {
                    html = "<style>img {width:100%;}</style><meta name='viewport' " +
                            "content='width=device-width, initial-scale=1'/>" + mHtmlString;
                }
                mWebView.loadDataWithBaseURL(null, html, "text/html", "utf8", null);
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

    //调用js
    public void evaluateJavascript(String js){
        if(StringUtil.isEmpty(js))
            return;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            mWebView.evaluateJavascript(js, null);
        }else {
            mWebView.loadUrl(js);
        }
    }

    //
    protected WebChromeClient mWebChromeClient = new WebChromeClient(){

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mShouldUseWebTitle) {
                if (showNavigationBar()) {
                    setTitle(title);
                }
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(mShouldDisplayProgress){
                mProgressBar.setProgress(newProgress / 100.0f);
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

        //4.0 - 5.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMsg = uploadMsg;
            WebFragment.this.openFileChooser(acceptType);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams
                fileChooserParams) {

            //上传文件
            mUploadFileCallback = filePathCallback;

            String type = null;
            String[] types = fileChooserParams.getAcceptTypes();
            if(types != null && types.length > 0){
                StringBuilder builder = new StringBuilder();
                int i = 0;
                for(String str : types){
                    builder.append(str);
                    if(i < types.length - 1){
                        builder.append(",");
                    }
                    i++;
                }
                type = builder.toString();
            }
            WebFragment.this.openFileChooser(type);

            return true;
        }
    };

    //打开文件选择器
    private void openFileChooser(String mineType){

        if(StringUtil.isEmpty(mineType)){
            mineType = "*/*";
        }
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mineType);
            startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

    boolean mLoadURL = false;

    //
    protected WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(shouldOpenURL(view, url)){
                return false;
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mShouldDisplayIndicator) {
                setPageLoading(false);
                mShouldDisplayIndicator = false;
            }

            onPageFinish(url);
            if(mShouldClearHistory){
                mShouldClearHistory = false;
                mWebView.clearHistory();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            if (mShouldDisplayIndicator) {
                setPageLoading(true);
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mWebView.canGoBack()){
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case FILE_CHOOSER_REQUEST_CODE : {
                    //文件选择完成
                    if(data != null){
                        Uri uri = data.getData();
                        if(uri != null){
                            if(mUploadFileCallback != null){
                                mUploadFileCallback.onReceiveValue(new  Uri[]{uri});
                            }else if(mUploadMsg != null){
                                mUploadMsg.onReceiveValue(uri);
                            }
                        }
                        mUploadFileCallback = null;
                        mUploadMsg = null;
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //加载完成
    protected void onPageFinish(String url){

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
        if(!StringUtil.isEmpty(url)){
            return url.startsWith("http://") || url.startsWith("https://");
        }
        return true;
    }

    //返回需要设置的自定义 userAgent 会拼在系统的userAgent后面
    public String getCustomUserAgent() {
        return null;
    }
}
