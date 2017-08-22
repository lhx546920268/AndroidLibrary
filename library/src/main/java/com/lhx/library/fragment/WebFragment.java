package com.lhx.library.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.just.library.WebDefaultSettingsManager;
import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.util.StringUtil;

/**
 * 浏览器
 */

public class WebFragment extends AppBaseFragment implements ChromeClientCallbackManager.ReceivedTitleCallback{


    public static final String WEB_URL = "com.lhx.WEB_URL";//要打开的链接
    public static final String WEB_HTML_STRING = "com.lhx.WEB_HTML_STRING";//要加载的html
    public static final String WEB_TITLE = "com.lhx.WEB_TITLE";//默认显示的标题
    public static final String WEB_USE_WEB_TITLE = "com.lhx.WEB_USE_WEB_TITLE";//是否使用web标题

    //浏览器
    AgentWeb mAgentWeb;

    //是否需要使用网页标题
    boolean mShouldUseWebTitle = true;

    //html
    protected String mHtmlString;

    //链接
    protected String mURL;

    @Override
    @CallSuper
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setContentView(R.layout.web_fragment);

        mShouldUseWebTitle = getExtraBooleanFromBundle(WEB_USE_WEB_TITLE, true);

        String url = getExtraStringFromBundle(WEB_URL);
        //没有http头的加上
        if(!TextUtils.isEmpty(url)){
            int index = url.indexOf("http://");
            if(index != 0){
                index = url.indexOf("https://");
                if(index != 0){
                    url = "http://" + url;
                }
            }
        }
        mURL = url;
        mHtmlString = getExtraStringFromBundle(WEB_HTML_STRING);

        String title = getExtraStringFromBundle(WEB_TITLE);
        if(!TextUtils.isEmpty(title)){
            getNavigationBar().setTitle(title);
        }

        setShowBackButton(true);

        loadWebContent();
    }

    //加载
    public void loadWebContent(){
        if(!StringUtil.isEmpty(mURL) || !StringUtil.isEmpty(mHtmlString)){

            if(mAgentWeb == null){
                mAgentWeb = AgentWeb.with(this)//
                        .setAgentWebParent((ViewGroup) getContentView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//
                        .setIndicatorColorWithHeight(App.WebProgressColor, 2)//
                        .setAgentWebWebSettings(WebDefaultSettingsManager.getInstance())//
                        .setReceivedTitleCallback(this)
                        .setSecurityType(AgentWeb.SecurityType.strict)
                        .createAgentWeb()//
                        .ready()//
                        .go("");
            }

            if(!StringUtil.isEmpty(mURL)){
                mAgentWeb.getLoader().loadUrl(mURL);
            }else {
                String html = mHtmlString;
                if(shouldAddMobileMeta()){
                    html =  "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" /><style>" +
                            "{max-width:100%;}</style>" + mHtmlString;
                }
                mAgentWeb.getLoader().loadDataWithBaseURL(null, html, "text/html", "utf8", null);
            }
        }
    }

    //是否添加移动设备头部
    public boolean shouldAddMobileMeta(){
        return true;
    }

    @Override
    public boolean showNavigationBar() {
        return true;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if(mShouldUseWebTitle){
            getNavigationBar().setTitle(title);
        }
    }

    @Override
    public void onResume() {
        if(mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if(mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onPause();
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mAgentWeb != null){
            return super.onKeyDown(keyCode, event) && mAgentWeb.handleKeyEvent(keyCode, event);
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onDestroyView() {
        if(mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroyView();
    }
}
