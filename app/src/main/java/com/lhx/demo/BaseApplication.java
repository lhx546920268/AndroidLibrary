package com.lhx.demo;

import android.app.Application;

import com.lhx.library.App;
import com.lhx.library.image.ImageLoaderUtil;

/**
 *
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        App.ImagePlaceHolder = R.drawable.square;
        App.init(this);
    }
}
