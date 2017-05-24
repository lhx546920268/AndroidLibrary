package com.lhx.demo.http;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.http.HttpAsyncTask;
import com.lhx.library.http.HttpProgressHandler;
import com.lhx.library.http.HttpRequest;
import com.lhx.library.http.HttpRequestHandler;
import com.lhx.library.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * http
 */

public class HttpFragment extends AppBaseFragment implements View.OnClickListener {

    HttpAsyncTask mDownloadImgTask;

    ProgressBar mProgressBar;

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        mContentView = inflater.inflate(R.layout.http_fragment, null);

        findViewById(R.id.download_img).setOnClickListener(this);
        mProgressBar = findViewById(R.id.progress_bar);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){
            case R.id.download_img :

                mDownloadImgTask = new HttpAsyncTask("http://chinauenvi.wxy01.com/public/images/e9/67/5b/b7f80f93bc7950" +
                        "c914cddf1e36cca2255c2d902e.png?1494425270#h"){
                    @Override
                    public void onConfigure(HttpRequest request) {
                        request.setDownloadTemporayPath(FileUtil.getTemporaryFilePath(mActivity, ""));
                        request.setDownloadDestinationPath(FileUtil.getTemporaryFilePath(mActivity, ""));
                        request.setShowDownloadProgress(true);
                    }
                };
                mDownloadImgTask.setHttpRequestHandler(new HttpRequestHandler() {
                    @Override
                    public void onSuccess(HttpAsyncTask task, byte[] result) {

                        Bitmap bitmap = BitmapFactory.decodeByteArray(result,0, result.length);
                        ImageView imageView = findViewById(R.id.img);
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFail(HttpAsyncTask task, int errorCode, int httpCode) {

                    }

                    @Override
                    public void onComplete(HttpAsyncTask task) {

                    }
                });

                mDownloadImgTask.setHttpProgressHandler(new HttpProgressHandler<HttpAsyncTask>() {
                    @Override
                    public void onUpdateUploadProgress(HttpAsyncTask target, float progress) {

                    }

                    @Override
                    public void onUpdateDownloadProgress(HttpAsyncTask target, float progress) {
                        mProgressBar.setProgress((int)(progress * mProgressBar.getMax()));
                    }
                });

                mDownloadImgTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }
}
