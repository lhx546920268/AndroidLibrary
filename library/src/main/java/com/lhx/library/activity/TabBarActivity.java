package com.lhx.library.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.drawable.DrawableUtil;
import com.lhx.library.fragment.AppBaseFragment;

import java.util.List;

/**
 * 标签栏 activity
 */

public abstract class TabBarActivity extends AppBaseContainerActivity implements RadioGroup.OnCheckedChangeListener{

    //没有位置
    public static final int NO_POSITION = -1;

    //标签栏
    protected RadioGroup mTabBar;

    //分割线
    protected View mDivider;

    //当前选中的
    private int mCheckedPosition;

    //按钮信息
    private List<TabBarItem> mTabBarItems;

    //当前fragment
    AppBaseFragment mCurrentFragment;

    @Override
    protected final void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setContentView(R.layout.tab_bar_activity);
        mTabBar = (RadioGroup)findViewById(R.id.tabBar);
        mDivider = findViewById(R.id.divider);
        mTabBar.setOnCheckedChangeListener(this);

        initTabBar();
    }

    @Override
    protected boolean showNavigationBar() {
        return false;
    }

    //加载tab
    protected void initTabBar(){
        //初始化标签
        mTabBarItems = getTabBarItems();
        if(mTabBarItems != null && mTabBarItems.size() > 0){
            for(int i = 0;i < mTabBarItems.size();i ++){
                TabBarItem item = mTabBarItems.get(i);
                RadioButton button = new RadioButton(this);
                button.setText(item.getTitle());
                button.setId(i);
                button.setTextColor(getTextColor(item));
                button.setGravity(Gravity.CENTER);
                button.setCompoundDrawables(null, getIcon(item), null, null);
                button.setCompoundDrawablePadding(getCompoundDrawablePadding(i));
                button.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                button.setPadding(0, getItemPaddingTop(i), 0, 0);

                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                params.weight = 1;

                mTabBar.addView(button, params);
            }

            mTabBar.check(mCheckedPosition);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if(shouldCheck(checkedId)){
            TabBarItem item = mTabBarItems.get(checkedId);
            if(item.getFragment() != null){
                switchFragment(item.getFragment());
                mCheckedPosition = checkedId;
            }else {
                checkWithoutListener(mCheckedPosition);
            }
            onCheck(checkedId);
        }else {
            checkWithoutListener(mCheckedPosition);
        }
    }

    //选择某个
    private void checkWithoutListener(int position){
        mTabBar.setOnCheckedChangeListener(null);
        mTabBar.check(position);
        mTabBar.setOnCheckedChangeListener(this);
    }

    //设置选中
    public void setCheckedPosition(int checkedPosition) {
        if(mCheckedPosition != checkedPosition){
            mCheckedPosition = checkedPosition;
            mTabBar.check(mCheckedPosition);
        }
    }

    //获取对应fragment位置 没有则返回
    public int getPosition(Class<? extends AppBaseFragment> fragmentClass){
        if(mTabBarItems != null && mTabBarItems.size() > 0){
            for(int i = 0;i < mTabBarItems.size();i ++){
                TabBarItem item = mTabBarItems.get(i);
                if(item.getFragment() != null && item.getFragment().getClass() == fragmentClass){
                    return i;
                }
            }
        }

        return NO_POSITION;
    }

    //显示某个fragment
    private void switchFragment(AppBaseFragment fragment){

        if(fragment != null && !fragment.isAdded() && fragment != mCurrentFragment){

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if(mCurrentFragment != null){
                transaction.detach(mCurrentFragment);
            }

            if(fragment.isDetached()){
                transaction.attach(fragment);
            }else {
                transaction.add(R.id.fragment_container, fragment);
            }
            transaction.commitAllowingStateLoss();
            mCurrentFragment = fragment;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(mCurrentFragment != null && mCurrentFragment.onKeyDown(keyCode, event)){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //获取图标
    private Drawable getIcon(TabBarItem item){
        StateListDrawable stateListDrawable = new StateListDrawable();

        Drawable drawable = ContextCompat.getDrawable(this, item.getIcon());
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Drawable checkDrawable = drawable;
        if(item.getCheckedIcon() != 0){
            checkDrawable = ContextCompat.getDrawable(this, item.getCheckedIcon());
        }else {
            int color = getCheckedTintColor();
            if(color != 0){
                checkDrawable = DrawableUtil.getTintDrawable(checkDrawable, color);
            }
        }

        checkDrawable.setBounds(0, 0, width, height);
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checkDrawable);

        int color = getNormalTintColor();
        if(color != 0){
            drawable = DrawableUtil.getTintDrawable(drawable, color);
        }


        drawable.setBounds(0, 0, width, height);
        stateListDrawable.addState(new int[]{}, drawable);
        stateListDrawable.setBounds(0, 0, width, height);

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

    //按钮顶部间隔
    public int getItemPaddingTop(int position){
        return pxFromDip(2);
    }

    //按钮正常着色 0时 不着色
    public @ColorInt int getNormalTintColor(){
        return 0;
    }

    //按钮选中颜色 0时 不着色
    public @ColorInt int getCheckedTintColor(){
        return 0;
    }

    //按钮标题颜色
    public abstract @ColorInt int getNormalTitleColor();

    //按钮选中标题颜色
    public abstract @ColorInt int getCheckedTitleColor();

    //按钮信息
    public abstract List<TabBarItem> getTabBarItems();

    //选择某个tab
    public void onCheck(int position){

    }

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
        private AppBaseFragment mFragment;

        public TabBarItem(@NonNull String title, @DrawableRes int icon) {
            this(title, icon, null);
        }

        public TabBarItem(@NonNull String title, @DrawableRes int icon, AppBaseFragment fragment) {
            this(title, icon, 0, fragment);
        }

        public TabBarItem(@NonNull String title, @DrawableRes int icon, @DrawableRes int checkedIcon, AppBaseFragment fragment) {
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
}
