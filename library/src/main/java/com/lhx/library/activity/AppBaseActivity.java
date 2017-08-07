package com.lhx.library.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.AnimRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.lhx.library.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.inter.LoginHandler;

@SuppressWarnings("unchecked")
public class AppBaseActivity extends AppCompatActivity {

    //登录请求code
    public static final int LOGIN_REQUEST_CODE = 1018;

    //登录完成回调
    private LoginHandler mLoginHandler;

    //是否有登录回调
    private boolean mHasLoginHandler = false;

    ///当前显示的Fragment
    private AppBaseFragment fragment;

    public static final String FRAGMENT_STRING = "fragmentString";

//    @Override
//    protected void onRestart() {
//        Log.d("AppBaseActivity", "onRestart");
//        super.onRestart();
//    }
//
//    @Override
//    protected void onStart() {
//        Log.d("AppBaseActivity", "onStart");
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        Log.d("AppBaseActivity", "onResume");
//        super.onResume();
//    }
//
//    @Override
//    protected void onStop() {
//        Log.d("AppBaseActivity", "onStop");
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        Log.d("AppBaseActivity", "onDestroy");
//        super.onDestroy();
//    }
//
//    @Override
//    public void onAttachedToWindow() {
//        Log.d("AppBaseActivity", "onAttachedToWindow");
//        super.onAttachedToWindow();
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//        Log.d("AppBaseActivity", "onDetachedFromWindow");
//        super.onDetachedFromWindow();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        Log.d("AppBaseActivity", "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(getContentViewRes());

        //java.net.SocketException: sendto failed: ECONNRESET (Connection reset by peer)
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ///生成fragment实例
        String className = getIntent().getStringExtra(FRAGMENT_STRING);
        if(className != null){
            Class clazz;
            try {

                clazz  = Class.forName(className);
//                if(!clazz.isAssignableFrom(AppBaseFragment.class)){
//                    throw new RuntimeException(className + "必须是AppBaseFragment或者其子类");
//                }

                AppBaseFragment currentFragment = (AppBaseFragment) clazz.newInstance();

                if(currentFragment != null){

                    setCurrentFragment(currentFragment);
                }else {

                    throw new RuntimeException(className + "不能实例化");
                }
            }catch (Exception e){

                e.printStackTrace();
                finish();
            }
        }
    }

    //获取视图内容，必须包含 fragment_container
    public @LayoutRes int getContentViewRes(){
        return R.layout.app_base_activity;
    }

    ///设置当前显示的fragment,无动画效果
    public void setCurrentFragment(AppBaseFragment currentFragment){

        setCurrentFragment(currentFragment, 0, 0);
    }

    /**
     * 设置当前显示的fragment,可设置动画效果
     * @param currentFragment 当前要显示的fragment
     * @param enter 进场动画
     * @param exit 出场动画
     */
    public void setCurrentFragment(AppBaseFragment currentFragment, @AnimRes int enter, @AnimRes int exit){

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

    //启动一个activity
    public Intent startActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        return intent;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment != null && fragment.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (fragment != null && fragment.dispatchKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(fragment != null){
            fragment.onWindowFocusChanged(hasFocus);
        }
    }

    //获取授权
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if(fragment != null){
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case LOGIN_REQUEST_CODE : {
                    //登录
                    if(mLoginHandler != null){
                        mLoginHandler.onLogin();
                        mLoginHandler = null;
                    }
                    return;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startActivityForResult(Intent intent, int requestCode, LoginHandler loginHandler){
        mLoginHandler = loginHandler;
        mHasLoginHandler = true;
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(mHasLoginHandler){
            mHasLoginHandler = false;
        }else {
            mLoginHandler = null;
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if(mHasLoginHandler){
            mHasLoginHandler = false;
        }else {
            mLoginHandler = null;
        }
        super.startActivityForResult(intent, requestCode, options);
    }
}
