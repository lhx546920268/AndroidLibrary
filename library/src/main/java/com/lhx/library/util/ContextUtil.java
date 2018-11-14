package com.lhx.library.util;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;

//上下文工具类
@SuppressWarnings("unchecked")
public class ContextUtil {

    //获取activity
    public static <T extends Activity> T getActivity(Context context, @NonNull Class<T> tClass){

        if(context != null){
            if(context instanceof ContextWrapper){
                context = ((ContextWrapper) context).getBaseContext();
            }

            if(context.getClass().equals(tClass)){
                try {
                    T t = (T)context;
                    return t;
                }catch (ClassCastException e){
                    return null;
                }
            }
        }

        return null;
    }
}
