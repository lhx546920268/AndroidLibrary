package com.lhx.library.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.AnimRes;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;

import com.lhx.library.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.inter.LoginHandler;
import com.lhx.library.util.AppUtil;
import com.lhx.library.util.SizeUtil;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unchecked")
public class AppBaseActivity extends AppCompatActivity {

    //activity里面fragment的类名
    public static final String FRAGMENT_STRING = "fragmentString";

    //登录请求code
    public static final int LOGIN_REQUEST_CODE = 1118;

    //登录完成回调
    private LoginHandler mLoginHandler;

    //是否有登录回调
    private boolean mHasLoginHandler = false;

    //activity 名称 为fragment的类名 或者 activity类名
    private String mName;

    ///当前显示的Fragment
    private AppBaseFragment fragment;

    //是否可见
    private boolean mVisible;

    public String getName() {
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public boolean isVisible() {
        return mVisible;
    }

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
    @Override
    protected void onResume() {
//        Log.d("AppBaseActivity", "onResume");
        super.onResume();
        mVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVisible = false;
    }

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

        AppUtil.setStatusBarStyle(this, ContextCompat.getColor(this, R.color.status_bar_background_color),
                getResources().getBoolean(R.bool.status_bar_is_light));

        int layoutRes = getContentViewRes();
        if(layoutRes != 0){
            setContentView(layoutRes);
        }

        //java.net.SocketException: sendto failed: ECONNRESET (Connection reset by peer)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ///生成fragment实例
        String className = getIntent().getStringExtra(FRAGMENT_STRING);
        if (className != null && layoutRes != 0) {
            Class clazz;
            try {

                clazz = Class.forName(className);
//                if(!clazz.isAssignableFrom(AppBaseFragment.class)){
//                    throw new RuntimeException(className + "必须是AppBaseFragment或者其子类");
//                }

                mName = className;

                AppBaseFragment currentFragment = (AppBaseFragment) clazz.newInstance();

                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    currentFragment.setArguments(bundle);
                }

                if (currentFragment != null) {

                    setCurrentFragment(currentFragment);
                } else {

                    throw new RuntimeException(className + "不能实例化");
                }
            } catch (Exception e) {

                e.printStackTrace();
                finish();
            }
        }else {
            mName = getClass().getName();
        }

        //添加到堆栈中
        ActivityStack.addActivity(this);
    }

    @Override
    @CallSuper
    protected void onDestroy() {

        //从堆栈移除
        ActivityStack.removeActivity(this);
        super.onDestroy();
    }

    //获取视图内容，如果为0，则忽略 可以包含 fragment_container
    public
    @LayoutRes
    int getContentViewRes() {
        return R.layout.app_base_activity;
    }

    ///设置当前显示的fragment,无动画效果
    public void setCurrentFragment(AppBaseFragment currentFragment) {

        setCurrentFragment(currentFragment, 0, 0);
    }

    /**
     * 设置当前显示的fragment,可设置动画效果
     *
     * @param currentFragment 当前要显示的fragment
     * @param enter           进场动画
     * @param exit            出场动画
     */
    public void setCurrentFragment(AppBaseFragment currentFragment, @AnimRes int enter, @AnimRes int exit) {

        fragment = currentFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (enter != 0 && exit != 0) {

            transaction.setCustomAnimations(enter, exit);
        }

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    public void addFragment(AppBaseFragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    public void removeFragment(AppBaseFragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
    }

    public static Intent getIntentWithFragment(Context context, Class<? extends AppBaseFragment> fragmentClass) {

        Intent intent = new Intent(context, AppBaseActivity.class);
        intent.putExtra(FRAGMENT_STRING, fragmentClass.getName());

        return intent;
    }

    //启动一个activity
    public Intent startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        return intent;
    }

    @Override
    @CallSuper
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()){
            moveTaskToBack(true);
            return true;
        }else {
            if (fragment != null && fragment.onKeyDown(keyCode, event)) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
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
        if (fragment != null) {
            fragment.onWindowFocusChanged(hasFocus);
        }
    }

    //获取授权
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (fragment != null) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOGIN_REQUEST_CODE: {
                    //登录
                    if (mLoginHandler != null) {
                        mLoginHandler.onLogin();
                        mLoginHandler = null;
                    }
                    return;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startActivityForResult(Intent intent, int requestCode, LoginHandler loginHandler) {
        mLoginHandler = loginHandler;
        mHasLoginHandler = true;
        super.startActivityForResult(intent, requestCode);

    }

    //获取px
    public int pxFromDip(float dip){
        return SizeUtil.pxFormDip(dip, this);
    }
    //获取颜色
    public @ColorInt int getColorCompat(@ColorRes int colorRes){
        return ContextCompat.getColor(this, colorRes);
    }

    //获取drawable
    public Drawable getDrawableCompat(@DrawableRes int drawableRes){
        return ContextCompat.getDrawable(this, drawableRes);
    }

    ///获取bundle内容
    public String getExtraStringFromBundle(String key){

        Bundle nBundle = getBundle();
        if(nBundle == null) return "";
        return nBundle.getString(key);
    }

    public double getExtraDoubleFromBundle(String key){
        Bundle nBundle = getBundle();
        if(nBundle != null){
            return nBundle.getDouble(key);
        }
        return 0;
    }

    public int getExtraIntFromBundle(String key){
        return getExtraIntFromBundle(key, 0);
    }

    public int getExtraIntFromBundle(String key, int defValue){
        Bundle nBundle = getBundle();
        if(nBundle != null){
            return nBundle.getInt(key, defValue);
        }
        return defValue;
    }

    public long getExtraLongFromBundle(String key){
        Bundle nBundle = getBundle();
        if(nBundle != null){
            return nBundle.getLong(key);
        }
        return 0;
    }

    public boolean getExtraBooleanFromBundle(String key, boolean def){
        Bundle nBundle = getBundle();
        if(nBundle != null){
            return nBundle.getBoolean(key, def);
        }
        return def;
    }

    public boolean getExtraBooleanFromBundle(String key){
        return getExtraBooleanFromBundle(key, false);
    }

    public List<String> getExtraStringListFromBundle(String key){
        Bundle nBundle = getBundle();
        return nBundle.getStringArrayList(key);
    }

    public Serializable getExtraSerializableFromBundle(String key){
        Bundle nBundle = getBundle();
        return nBundle.getSerializable(key);
    }

    //获取对应bundle
    public Bundle getBundle(){

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            return bundle;
        }

        return null;
    }
}
