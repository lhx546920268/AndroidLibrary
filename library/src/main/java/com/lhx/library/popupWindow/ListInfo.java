package com.lhx.library.popupWindow;

import android.support.annotation.DrawableRes;

/**
 * 列表信息
 */
public class ListInfo {

    //标题
    public String title;

    //左边图标
    public @DrawableRes int leftIconRes;

    //关联的对象
    public Object object;

    public ListInfo(String title) {
        this.title = title;
    }

    public ListInfo(String title, Object object) {
        this.title = title;
        this.object = object;
    }

    public ListInfo(String title, int leftIconRes, Object object) {
        this.title = title;
        this.leftIconRes = leftIconRes;
        this.object = object;
    }
}
