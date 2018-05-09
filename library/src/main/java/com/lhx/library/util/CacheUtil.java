package com.lhx.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 缓存工具类
 */

@SuppressWarnings("unchecked")
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

    //保存对象
    public static void saveObject(Context context, String key, Serializable object) {

        if(TextUtils.isEmpty(key))
            return;
        key = AppUtil.getAppPackageName(context) + "." + key;
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            prefs.putString(key, temp);
            prefs.apply();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static <T extends Object> T getObject(Context context, String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        key = AppUtil.getAppPackageName(context) + "." + key;
        String temp = prefs.getString(key, "");

        ByteArrayInputStream bais =  new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        T object = null;

        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            object = (T) ois.readObject();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return object;
    }


    // 删除缓存目录
    public static void deleteCacheFolder(@NonNull Context context, final Runnable runnable) {

        final String folder = FileUtil.getImageCacheFolder(context);
        new Thread() {
            @Override
            public void run() {
                FileUtil.deleteAllFiles(new File(folder));
                ThreadUtil.runOnMainThread(runnable);
            }
        }.start();
    }

    // 获取缓存大小
    public static void getCacheSize(final Context context, final OnCacheHandler onCacheHander) {

        final String folder = FileUtil.getImageCacheFolder(context);
        new Thread(){

            @Override
            public void run() {

                final String size = Formatter.formatFileSize(context, FileUtil.getFileSize(new File(folder)));
                if(onCacheHander != null){
                    ThreadUtil.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            onCacheHander.onGetCacheSize(size);
                        }
                    });
                }
            }
        }.start();
    }

    public interface OnCacheHandler{

        //获取缓存大小
        void onGetCacheSize(String size);
    }
}
