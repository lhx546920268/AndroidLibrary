package com.lhx.library.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lhx.library.dialog.AlertController;

/**
 * app有关的工具类
 */

public class AppUtil {

    /**
     * 获取app版本号
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int ver = 0;
        try {
            String packageName = context.getPackageName();
            PackageManager packageManager = context.getPackageManager();
            ver = packageManager.getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return ver;
    }

    /**
     * 获取app包名
     * @param context
     * @return 包名
     */
    public static String getAppPackageName(Context context){
        return context.getPackageName();
    }


    static final String SHORTCUT_INSTALLED = "SHORTCUT_INSTALLED";

    /**
     * 创建桌面快捷方式
     * @param appName app名称
     * @param appIconRes app图标
     */
    public static void createShortcut(Context context, String appName, @DrawableRes int appIconRes){

        if (!CacheUtil.loadPrefsBoolean(context, SHORTCUT_INSTALLED, false)) {
            CacheUtil.savePrefs(context, SHORTCUT_INSTALLED, true);

            Intent main = new Intent();
            main.setComponent(new ComponentName(context, context.getClass()));
            main.setAction(Intent.ACTION_MAIN);
            main.addCategory(Intent.CATEGORY_LAUNCHER);

            //要添加这句话
            main.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            shortcutIntent.putExtra("duplicate", true);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, main);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext
                    (context, appIconRes));
            context.sendBroadcast(shortcutIntent);
        }
    }

    ///拨打电话
    public static void makePhoneCall(final Context context, final String phone){

        if(StringUtil.isEmpty(phone))
            return;
        AlertController controller = AlertController.buildAlert(context, "是否拨打 " + phone, "取消", "拨打");
        controller.setOnItemClickListener(new AlertController.OnItemClickListener() {
            @Override
            public void onItemClick(AlertController controller, int index) {
                if(index == 1){
                    String nPhone = phone;
                    if (nPhone.contains("-")) {
                        nPhone = nPhone.replaceAll("-", "");
                    }
                    try{
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + nPhone));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (SecurityException e){

                    }
                }
            }
        });
        controller.show();
    }

    /**
     * 关闭软键盘
     * @param context  上下文
     * @param view 当前焦点
     */
    public static void hideSoftInputMethod(Context context, View view) {
        try {
            // 隐藏软键盘
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开软键盘
     * @param context 上下文
     * @param view 当前焦点
     */
    public static void showSoftInputMethod(Context context, View view) {
        try {
            // 打开软键盘
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
