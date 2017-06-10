package com.lhx.library.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.lhx.library.R;

public class AppBaseActivity extends AppCompatActivity {

    ///当前显示的Fragment
    private Fragment fragment;

    public static final String FRAGMENT_STRING = "fragmentString";

    @Override
    protected void onRestart() {
        Log.d("AppBaseActivity", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.d("AppBaseActivity", "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("AppBaseActivity", "onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("AppBaseActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("AppBaseActivity", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onAttachedToWindow() {
        Log.d("AppBaseActivity", "onAttachedToWindow");
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        Log.d("AppBaseActivity", "onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d("AppBaseActivity", "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_base_activity);
        ///生成fragment实例
        String className = getIntent().getStringExtra(FRAGMENT_STRING);
        Class clazz;
        try {

            clazz  = Class.forName(className);
            Fragment currentFragment = (Fragment) clazz.newInstance();

            if(currentFragment != null){

                setCurrentFragment(currentFragment);
            }else {

                Log.w("AppBaseActivity", className + "不能实例化");
            }
        }catch (Exception e){

            e.printStackTrace();
            finish();
        }
    }

    ///设置当前显示的fragment,无动画效果
    public void setCurrentFragment(Fragment currentFragment){

        setCurrentFragment(currentFragment, 0, 0);
    }

    /**
     * 设置当前显示的fragment,可设置动画效果
     * @param currentFragment 当前要显示的fragment
     * @param enter 进场动画
     * @param exit 出场动画
     */
    public void setCurrentFragment(Fragment currentFragment, @AnimRes int enter, @AnimRes int exit){

        fragment = currentFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(enter != 0 && exit != 0){

            transaction.setCustomAnimations(enter, exit);
        }

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    public static Intent getIntentWithFragment(Context context, Class fragmentClass){

        Intent intent = new Intent(context, AppBaseActivity.class);
        intent.putExtra(FRAGMENT_STRING, fragmentClass.getName());

        return intent;
    }
}
