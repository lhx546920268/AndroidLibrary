package com.lhx.library.http;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cookie 工具类
 */

public class CookieUtil {


    /**
     * 获取某个域名的cookie列表
     * @param domain 域名
     * @return cookie
     */
    public static List<String> getCookie(String domain){
        List<String> nCookie = null;
        CookieHandler nCookieHandler = CookieManager.getDefault();
        try {
            Map<String, List<String>> nMap = new HashMap<String, List<String>>();
            nMap = nCookieHandler.get(URI.create(domain), nMap);
            nCookie = nMap.get("Cookie");
            if(nCookie != null){
                for(String str : nCookie){
                    Log.d("cookie", str);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nCookie;
    }

    public static int getIntCookie(String key, String domain){
        int nValue = 0;
        List<String> nList = getCookie(domain);
        if (!TextUtils.isEmpty(key) && nList != null && nList.size() > 0) {
            String nKey = key + "=";
            for (String string : nList) {
                if (string.startsWith(nKey)) {
                    String nValueString = string.substring(nKey.length());
                    if (!TextUtils.isEmpty(nValueString) && TextUtils.isDigitsOnly(nValueString)) {
                        nValue = Integer.parseInt(nValueString);
                    }
                    break;
                }
            }
        }
        return nValue;
    }
}
