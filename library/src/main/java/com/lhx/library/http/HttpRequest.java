package com.lhx.library.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * http请求
 */

public class HttpRequest {

    //http链接
    HttpURLConnection mConn;

    //请求URL
    String mURL;

    //参数信息
    ArrayList<Param> mParams = new ArrayList<>();

    public HttpRequest(String URL) {
        mURL = URL;
    }

    //添加参数 参数名可以相同
    public void addPostValue(String key, String value){
        if(key != null && value != null){
            mParams.add(new Param(key, value));
        }
    }

    //添加参数 会替换参数key相同的值
    public void setPostValue(String key, String value){
        
    }

    //参数信息
    private class Param{

        //参数key
        String key;

        //参数值
        String value;

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
