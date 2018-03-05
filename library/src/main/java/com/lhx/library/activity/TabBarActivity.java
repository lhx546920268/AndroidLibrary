package com.lhx.library.activity;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lhx.library.R;
import com.lhx.library.drawable.DrawableUtil;

import java.util.List;

/**
 * 标签栏 activity
 */

public abstract class TabBarActivity extends AppBaseActivity implements RadioGroup.OnCheckedChangeListener{

    //标签栏
    protected RadioGroup mTabBar;

    //分割线
    protected View mDivider;

    //当前选中的
    private int mCheckedPosition;

    //按钮信息
    private List<TabBarItem> mTabBarItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabBar = (RadioGroup)findViewById(R.id.tabBar);
        mDivider = findViewById(R.id.divider);
        mTabBar.setOnCheckedChangeListener(this);

        //初始化标签
        mTabBarItems = getTabBarItems();
        for(int i = 0;i < mTabBarItems.size();i ++){
            TabBarItem item = mTabBarItems.get(i);
            RadioButton button = new RadioButton(this);
            button.setText(item.getTitle());
            button.setId(i);
            button.setTextColor(getTextColor(item));
            button.setCompoundDrawables(null, getIcon(item), null, null);
            button.setCompoundDrawablePadding(getCompoundDrawablePadding(i));

            mTabBar.addView(button);
        }

        mTabBar.check(mCheckedPosition);
    }

    @Override
    public int getContentViewRes() {
        return R.layout.tab_bar_activity;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if(shouldCheck(checkedId)){

        }else {
            group.check(mCheckedPosition);
        }
    }

    //设置选中
    public void setCheckedPosition(int checkedPosition) {
        if(mCheckedPosition != checkedPosition){
            mCheckedPosition = checkedPosition;
            mTabBar.check(mCheckedPosition);
        }
    }

    //获取图标
    private Drawable getIcon(TabBarItem item){
        StateListDrawable stateListDrawable = new StateListDrawable();

        Drawable drawable = ContextCompat.getDrawable(this, item.getIcon());

        Drawable checkDrawable = drawable;
        if(item.getCheckedIcon() != 0){
            checkDrawable = ContextCompat.getDrawable(this, item.getCheckedIcon());
        }else {
            int color = getCheckedTintColor();
            if(color != 0){
                checkDrawable = DrawableUtil.getTintDrawable(checkDrawable, color);
            }
        }

        checkDrawable.setBounds(0, 0, checkDrawable.getIntrinsicWidth(), checkDrawable.getIntrinsicHeight());
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checkDrawable);

        int color = getNormalTintColor();
        if(color != 0){
            drawable = DrawableUtil.getTintDrawable(drawable, color);
        }

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        stateListDrawable.addState(new int[]{}, drawable);

        return stateListDrawable;
    }

    //获取文字颜色
    private ColorStateList getTextColor(TabBarItem item){

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{};

        int[] colors = new int[]{getCheckedTitleColor(), getNormalTitleColor()};

        return new ColorStateList(states, colors);
    }

    //按钮和图标的间距
    public int getCompoundDrawablePadding(int position){
        return pxFromDip(2);
    }

    //按钮正常着色 0时 不着色
    public abstract @ColorInt int getNormalTintColor();

    //按钮正常颜色 0时 不着色
    public abstract @ColorInt int getCheckedTintColor();

    //按钮标题颜色
    public abstract @ColorInt int getNormalTitleColor();

    //按钮选中标题颜色
    public abstract @ColorInt int getCheckedTitleColor();

    //按钮信息
    public abstract @NonNull List<TabBarItem> getTabBarItems();

    //某个标签是否可以点击
    public boolean shouldCheck(int position){
        return true;
    }

    //标签栏按钮信息
    public static class TabBarItem{

        //标题
        private @NonNull String mTitle;

        //图标
        private @DrawableRes int mIcon;

        //选中图标 当为空时，可使用tintColor 着色 mIcon
        private @DrawableRes int mCheckedIcon;

        //关联的fragment 当为空不会有选中效果
        private Fragment mFragment;

        public TabBarItem(@NonNull String title, @DrawableRes int icon) {
            this(title, icon, null);
        }

        public TabBarItem(@NonNull String title, @DrawableRes int icon, Fragment fragment) {
            this(title, icon, 0, fragment);
        }

        public TabBarItem(@NonNull String title, @DrawableRes int icon, @DrawableRes int checkedIcon, Fragment fragment) {
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

        public Fragment getFragment() {
            return mFragment;
        }
    }
}
