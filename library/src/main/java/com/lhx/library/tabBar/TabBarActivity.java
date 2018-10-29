package com.lhx.library.tabBar;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.activity.AppBaseContainerActivity;
import com.lhx.library.drawable.DrawableUtil;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.util.ViewUtil;
import com.lhx.library.widget.OnSingleClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签栏 activity
 */

public abstract class TabBarActivity extends AppBaseContainerActivity{

    //没有位置
    public static final int NO_POSITION = -1;

    //标签栏
    protected LinearLayout mTabBar;

    //分割线
    protected View mDivider;

    //当前选中的
    private int mCheckedPosition = NO_POSITION;

    //背景视图
    private View mBackgroundView;

    //按钮信息
    private List<TabBarItemInfo> mTabBarItemInfos;

    //按钮
    protected List<TabBarItem> mTabBarItems = new ArrayList<>();

    //当前fragment
    AppBaseFragment mCurrentFragment;

    @Override
    protected final void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setContentView(R.layout.tab_bar_activity);
        mTabBar = findViewById(R.id.tabBar);
        mDivider = findViewById(R.id.divider);

        initTabBar();
    }

    //设置tab背景视图
    protected void setBackgroundView(View backgroundView){

        if(backgroundView != mBackgroundView){
            if(mBackgroundView != null){
                ViewUtil.removeFromParent(mBackgroundView);
            }
            mBackgroundView = backgroundView;
            if(mBackgroundView != null){
                RelativeLayout relativeLayout = (RelativeLayout)getContainer().getContentView();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                relativeLayout.addView(mBackgroundView, relativeLayout.indexOfChild(mTabBar), params);
            }
        }
    }

    @Override
    protected boolean showNavigationBar() {
        return false;
    }

    //加载tab
    protected void initTabBar(){
        //初始化标签
        mTabBarItemInfos = getTabBarItemInfos();
        if(mTabBarItemInfos != null && mTabBarItemInfos.size() > 0){
            for(int i = 0;i < mTabBarItemInfos.size();i ++){
                TabBarItemInfo info = mTabBarItemInfos.get(i);
                TabBarItem item = (TabBarItem)LayoutInflater.from(this).inflate(R.layout.tab_bar_item, null);
                item.getTextView().setText(info.getTitle());
                item.getTextView().setTextSize(getItemTextSize(i));
                item.getTextView().setTextColor(getTextColor(item));
                item.getImageView().setImageDrawable(getIcon(info));
                item.setImageTextPadding(pxFromDip(2));
                item.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        TabBarItem tabBarItem = (TabBarItem)v;
                        if(!tabBarItem.isChecked()){
                            int position = mTabBarItems.indexOf(tabBarItem);
                            if(shouldCheck(position)){
                                setCheckedPosition(position);
                            }
                        }
                    }
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight = 1;
                params.gravity = Gravity.CENTER_VERTICAL;

                onConfigureItem(item, params, i);

                mTabBar.addView(item, params);
                mTabBarItems.add(item);
            }

            setCheckedPosition(0);
        }
    }

    //设置选中
    public void setCheckedPosition(int checkedPosition) {
        if(mCheckedPosition != checkedPosition){

            if(checkedPosition < 0){
                checkedPosition = 0;
            }else if(checkedPosition >= mTabBarItems.size()){
                checkedPosition = mTabBarItems.size() - 1;
            }

            TabBarItemInfo info = mTabBarItemInfos.get(checkedPosition);
            if(info.getFragment() != null){

                if(mCheckedPosition >= 0 && mCheckedPosition < mTabBarItems.size()){
                    TabBarItem item = mTabBarItems.get(mCheckedPosition);
                    item.setChecked(false);
                }

                mCheckedPosition = checkedPosition;

                TabBarItem item = mTabBarItems.get(mCheckedPosition);
                item.setChecked(true);
                switchFragment(info.getFragment());
            }

            onCheck(checkedPosition);
        }
    }

    //获取对应fragment位置 没有则返回
    public int getPosition(Class<? extends AppBaseFragment> fragmentClass){
        if(mTabBarItemInfos != null && mTabBarItemInfos.size() > 0){
            for(int i = 0;i < mTabBarItemInfos.size();i ++){
                TabBarItemInfo info = mTabBarItemInfos.get(i);
                if(info.getFragment() != null && info.getFragment().getClass() == fragmentClass){
                    return i;
                }
            }
        }

        return NO_POSITION;
    }

    public int getCheckedPosition() {
        return mCheckedPosition;
    }

    //设置角标
    public void setBadgeValue(String badgeValue, int position){
        if(mCheckedPosition >= 0 && mCheckedPosition < mTabBarItems.size()){
            TabBarItem item = mTabBarItems.get(position);
            item.setBadgeValue(badgeValue);
        }
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
    private Drawable getIcon(TabBarItemInfo item){
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
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, checkDrawable);

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
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};

        int[] colors = new int[]{getCheckedTitleColor(), getNormalTitleColor()};

        return new ColorStateList(states, colors);
    }

    //配置button
    public void onConfigureItem(TabBarItem item, LinearLayout.LayoutParams params, int position){

    }

    //字体大小
    public int getItemTextSize(int position){
        return 11;
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
    public abstract List<TabBarItemInfo> getTabBarItemInfos();

    //选择某个tab
    public void onCheck(int position){

    }

    //某个标签是否可以点击
    public boolean shouldCheck(int position){
        return true;
    }
}
