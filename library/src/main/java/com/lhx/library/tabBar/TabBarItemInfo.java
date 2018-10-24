package com.lhx.library.tabBar;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.lhx.library.fragment.AppBaseFragment;

//选项卡按钮信息
public class TabBarItemInfo {

    //标题
    private @NonNull String mTitle;

    //图标
    private @DrawableRes int mIcon;

    //选中图标 当为空时，可使用tintColor 着色 mIcon
    private @DrawableRes int mCheckedIcon;

    //关联的fragment 当为空不会有选中效果
    private AppBaseFragment mFragment;

    public TabBarItemInfo(@NonNull String title, @DrawableRes int icon) {
        this(title, icon, null);
    }

    public TabBarItemInfo(@NonNull String title, @DrawableRes int icon, AppBaseFragment fragment) {
        this(title, icon, 0, fragment);
    }

    public TabBarItemInfo(@NonNull String title, @DrawableRes int icon, @DrawableRes int checkedIcon, AppBaseFragment fragment) {
        mTitle = title;
        mIcon = icon;
        mCheckedIcon = checkedIcon;
        mFragment = fragment;
        if(icon == 0){
            throw new IllegalArgumentException("TabBarActivity TabBarItem 图标不能为空");
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public int getIcon() {
        return mIcon;
    }

    public int getCheckedIcon() {
        return mCheckedIcon;
    }

    public AppBaseFragment getFragment() {
        return mFragment;
    }
}
