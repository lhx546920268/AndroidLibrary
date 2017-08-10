package com.lhx.library.util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * json 工具类
 */

public class JsonUtil {

    /**
     * 获取字符串 防止 空 null
     */
    public static String optString(JSONObject object, String key){

        if(object != null && !StringUtil.isEmpty(key)){
            String str = object.optString(key);
            if(!StringUtil.isEmpty(str)){
                return str;
            }
        }
        return "";
    }

    public static String optString(JSONArray array, int index){

        if(array != null && index < array.length()){
            String str = array.optString(index);
            if(!StringUtil.isEmpty(str)){
                return str;
            }
        }
        return "";
    }
}
