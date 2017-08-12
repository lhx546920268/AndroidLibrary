package com.lhx.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 标签栏
 */

public class TabBar extends LinearLayout{

    //选中
    private int mSelectedPosition;

    //字体大小 sp
    private int mTextSize;

    //正常字体颜色
    private @ColorInt int mNormalTextColor;

    //选中字体颜色
    private @ColorInt int mSelectedTextColor;

    //按钮信息
    TabBarItemInfo[] mItemInfos;

    public TabBar(Context context) {
        this(context, null, 0);
    }

    public TabBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTextSize = 13;
        mNormalTextColor = Color.LTGRAY;
        mSelectedTextColor = Color.RED;
    }

    //设置按钮信息
    public void setItemInfos(TabBarItemInfo[] itemInfos){
        if(mItemInfos != itemInfos){

        }
    }

    //初始化item
    private void initItems(){


    }

    //按钮
    class TabBarItem extends LinearLayout{

        //图标
        ImageView imageView;

        //文本
        TextView textView;

        //是否选中
        boolean selected;

        public TabBarItem(Context context) {
            super(context);

            setOrientation(VERTICAL);
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                    .WRAP_CONTENT);
            imageView.setLayoutParams(params);
            addView(imageView);
        }
    }

    //按钮信息
    public static class TabBarItemInfo{

        //正常图标
        public @DrawableRes int normalIconRes;

        //选中图标
        public @DrawableRes int selectedIconRes;

        //标题
        public String title;

        //item
        TabBarItem item;

        public TabBarItemInfo(int normalIconRes, int selectedIconRes, String title) {
            this.normalIconRes = normalIconRes;
            this.selectedIconRes = selectedIconRes;
            this.title = title;
        }
    }
}
