package com.lhx.library.util;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;

//上下文工具类
@SuppressWarnings("unchecked")
public class ContextUtil {

    //获取activity
    public static Activity getActivity(Context context){

        if(context != null){

            if(context instanceof Activity){
                return (Activity)context;
            }

            if(context instanceof ContextThemeWrapper){
                context = ((ContextThemeWrapper) context).getBaseContext();
            }

            if(context instanceof Activity){
                return (Activity)context;
            }
        }

        return null;
    }
}
