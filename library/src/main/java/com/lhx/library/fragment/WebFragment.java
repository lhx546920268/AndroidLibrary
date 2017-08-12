package com.lhx.library.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.just.library.WebDefaultSettingsManager;
import com.lhx.library.App;
import com.lhx.library.R;

/**
 * 浏览器
 */

public class WebFragment extends AppBaseFragment implements ChromeClientCallbackManager.ReceivedTitleCallback{


    public static final String WEB_URL = "com.lhx.WEB_URL";//要打开的链接
    public static final String WEB_TITLE = "com.lhx.WEB_TITLE";//默认显示的标题
    public static final String WEB_USE_WEB_TITLE = "com.lhx.WEB_USE_WEB_TITLE";//是否使用web标题

    //浏览器
    AgentWeb mAgentWeb;

    //是否需要使用网页标题
    boolean mShouldUseWebTitle = true;


    @Override
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

        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent((ViewGroup) getContentView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//
                .setIndicatorColorWithHeight(App.WebProgressColor, 2)//
                .setAgentWebWebSettings(WebDefaultSettingsManager.getInstance())//
                .setReceivedTitleCallback(this)
                .setSecurityType(AgentWeb.SecurityType.strict)
                .createAgentWeb()//
                .ready()//
                .go(url);

        String title = getExtraStringFromBundle(WEB_TITLE);
        if(!TextUtils.isEmpty(title)){
            getNavigationBar().setTitle(title);
        }

        setShowBackButton(true);
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
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event) && mAgentWeb.handleKeyEvent(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }
}
