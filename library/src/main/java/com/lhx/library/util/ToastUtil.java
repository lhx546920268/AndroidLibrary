package com.lhx.library.util;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhx.library.R;

/**
 * Toast 工具类
 */

public class ToastUtil {

    //当前显示的toast
    private static Toast currentToast;


    //获取toast
    public static Toast getToast(Context context){
        close();
        currentToast = new Toast(context);

        return currentToast;
    }


    //关闭上一个toast
    public static void close(){
        if(currentToast != null){
            currentToast.cancel();
            currentToast = null;
        }
    }

    public static void alert(Context context, CharSequence text){
        close();
        currentToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    public static void alert(Context context, CharSequence text, @DrawableRes int icon){
        alert(context, text, icon, Gravity.CENTER);
    }

    public static void alert(Context context, CharSequence text, @DrawableRes int icon, int gravity){
        close();
        currentToast = new Toast(context);
        View view = View.inflate(context, R.layout.icon_text_toast, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.icon);
        imageView.setImageResource(icon);

        TextView textView = (TextView)view.findViewById(R.id.text);
        textView.setText(text);

        currentToast.setGravity(gravity, 0, 0);

        currentToast.setView(view);
        currentToast.setDuration(Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
