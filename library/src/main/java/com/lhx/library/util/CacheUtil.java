package com.lhx.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * 缓存工具类
 */

public class CacheUtil {

    /**
     * 保存配置文件
     * @param context 上下文
     * @param key 要保存的key
     * @param value 保存的值
     */
    public static void savePrefs(Context context, String key, Object value) {

        if(TextUtils.isEmpty(key))
            return;
        key = AppUtil.getAppPackageName(context) + "." + key;
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();

        if (value instanceof Boolean) {
            
            prefs.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            
            prefs.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            
            prefs.putString(key, (String) value);
        } else if (value instanceof Long) {
            
            prefs.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            
            prefs.putFloat(key, (Float) value);
        } else {
            
            throw new UnsupportedOperationException();
        }
        
        prefs.apply();
    }

    //

    // //////////////////////////////加载配置文件中的信息////////////////////////////////
    public static String loadPrefsString(Context context, String key, String defValue) {
        key = AppUtil.getAppPackageName(context) + "." + key;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defValue);
    }

    public static int loadPrefsInt(Context context, String key, int defValue) {
        key = AppUtil.getAppPackageName(context) + "." + key;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defValue);
    }

    public static boolean loadPrefsBoolean(Context context, String key, boolean defValue) {
        key = AppUtil.getAppPackageName(context) + "." + key;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defValue);
    }

    public static long loadPrefsLong(Context context, String key, long defValue) {
        key = AppUtil.getAppPackageName(context) + "." + key;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getLong(key, defValue);
    }

    public static float loadPrefsFloat(Context context, String key, float defValue) {
        key = AppUtil.getAppPackageName(context) + "." + key;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat(key, defValue);
    }

    public static void removePrefs(Context context, String key) {
        key = AppUtil.getAppPackageName(context) + "." + key;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove(key).apply();
    }

    // 是否包含key
    public static boolean containsPrefs(Context context, String key) {
        key = AppUtil.getAppPackageName(context) + "." + key;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.contains(key);
    }
}
