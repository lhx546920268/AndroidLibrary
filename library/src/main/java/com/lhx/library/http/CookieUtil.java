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
 * 使用前 要先
    CookieHandler.setDefault(new CookieManager(null, null));
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
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nCookie;
    }

    //获取cookie中的某个值
    public static String getCookieValue(String key, String domain){
        String nValue = "";
        List<String> nList = getCookie(domain);
        if (!TextUtils.isEmpty(key) && nList != null && nList.size() > 0) {
            String nKey = key + "=";
            for (String string : nList) {
                if (string.startsWith(nKey)) {
                    nValue = string.substring(nKey.length());
                    break;
                }
            }
        }
        return nValue;
    }
}
