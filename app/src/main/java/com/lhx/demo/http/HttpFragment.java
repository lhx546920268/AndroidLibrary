package com.lhx.demo.http;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.lhx.library.security.MD5;
import com.lhx.library.util.FileUtil;
import com.lhx.library.util.ImageUtil;
import com.lhx.library.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * http
 */

public class HttpFragment extends AppBaseFragment implements View.OnClickListener {

    HttpAsyncTask mDownloadImgTask;
    HttpAsyncTask mUploadImgTask;

    ProgressBar mProgressBar;
    ProgressBar mUploadProgressBar;

    static final int SELECT_PHOTO_ID = 101;

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setContentView(inflater.inflate(R.layout.http_fragment, null));

        findViewById(R.id.download_img).setOnClickListener(this);
        mProgressBar = findViewById(R.id.progress_bar);
        mUploadProgressBar = findViewById(R.id.upload_progress_bar);

        findViewById(R.id.upload_btn).setOnClickListener(this);


        onReloadPage();
    }

    @Override
    protected void onReloadPage() {
        setPageLoading(true);
        //登录

        ContentValues values = new ContentValues();
        values.put("method", "b2c.passport.post_login");
        values.put("uname", "qq11");
        values.put("password", "123456");
        values.put("sign", signatureParams(values));

        HttpAsyncTask task = new HttpAsyncTask(DOMAIN, values) {
            @Override
            public void onConfigure(HttpRequest request) {
                request.setPostBodyFormat(HttpRequest.POST_BODY_FORMAT_MULTI_PART_FORM_DATA);
            }
        };

        task.setHttpRequestHandler(new HttpRequestHandler() {
            @Override
            public void onSuccess(HttpAsyncTask task, byte[] result) {
                String string = StringUtil.stringFromBytes(result, task.getStringEncoding());
                Log.d("tag", "登录结果：" + string );
            }

            @Override
            public void onFail(HttpAsyncTask task, int errorCode, int httpCode) {
                setPageLoadFail(true);
            }

            @Override
            public void onComplete(HttpAsyncTask task) {
                setPageLoading(false);
            }
        });

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){
            case R.id.download_img :

                mDownloadImgTask = new HttpAsyncTask("http://chinauenvi.wxy01.com/public/images/ea/0b/12/18ad2dc9140c710738f2cd092164f2b798a0a026.jpg?1496365993#w"){
                    @Override
                    public void onConfigure(HttpRequest request) {
                        request.setDownloadTemporayPath(FileUtil.getTemporaryFilePath(mContext, ""));
                        request.setDownloadDestinationPath(FileUtil.getTemporaryFilePath(mContext, ""));
                        request.setShowDownloadProgress(true);
                    }
                };
                mDownloadImgTask.setHttpRequestHandler(new HttpRequestHandler() {
                    @Override
                    public void onSuccess(HttpAsyncTask task, byte[] result) {

                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(result,0, result.length, ImageUtil.getOptions
                                (result, metrics.widthPixels, metrics.heightPixels));
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
            case R.id.upload_btn : {

                if(mUploadImgTask != null && mUploadImgTask.isExecuting()){
                    mUploadImgTask.cancel();
                    return;
                }
                 Intent intent = new Intent(Intent.ACTION_PICK,
                 android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 intent.setType("image/*");
                 startActivityForResult(intent, SELECT_PHOTO_ID);

                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == SELECT_PHOTO_ID && data != null){
                Uri uri = data.getData();
                ImageView imageView = findViewById(R.id.upload_img);


                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                String filePath = FileUtil.filePathFromUri(mContext, uri);
                imageView.setImageBitmap(ImageUtil.adjustImage(filePath, metrics.widthPixels, metrics.heightPixels));

                File file = new File(filePath);

                ContentValues values = new ContentValues(1);
                values.put("method", "engineer.sort.upload");
                values.put("different", "0");
                values.put("sign", signatureParams(values));

                HashMap<String, File> map = new HashMap<>();
                map.put("file", file);

                mUploadImgTask = new HttpAsyncTask(DOMAIN, values, map) {
                    @Override
                    public void onConfigure(HttpRequest request) {

                        request.setShowUploadProgress(true);
                    }
                };

                mUploadImgTask.setHttpRequestHandler(new HttpRequestHandler() {
                    @Override
                    public void onSuccess(HttpAsyncTask task, byte[] result) {

                        String string = StringUtil.stringFromBytes(result, task.getStringEncoding());
                        Log.d("tag", string + "");
                    }

                    @Override
                    public void onFail(HttpAsyncTask task, int errorCode, int httpCode) {

                    }

                    @Override
                    public void onComplete(HttpAsyncTask task) {

                    }
                });

                mUploadImgTask.setHttpProgressHandler(new HttpProgressHandler<HttpAsyncTask>() {
                    @Override
                    public void onUpdateUploadProgress(HttpAsyncTask target, float progress) {
                        mUploadProgressBar.setProgress((int)(progress * mProgressBar.getMax()));
                    }

                    @Override
                    public void onUpdateDownloadProgress(HttpAsyncTask target, float progress) {

                    }
                });

                mUploadImgTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 签名表单数据
     *
     * @return
     */
    public String signatureParams(ContentValues params) {
        if (params == null) return "";

        List<String> stringList = new ArrayList<String>();
        stringList.addAll(params.keySet());
        if (stringList.size() <= 0) return "";

        Collections.sort(stringList, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });

        String result = "";
        String lastKey = "";

        for (String string : stringList) {
            String nString = string.substring(0);
            int start = nString.indexOf("[");
            if (start != -1) {
                String theKey = nString.substring(0, start);
                if (TextUtils.equals(lastKey, theKey))
                    nString = nString.substring(start);
                lastKey = theKey;
            }

            result = buildString(result, nString.replaceAll("\\[", "")
                    .replace("]", ""), params.getAsString(string));
        }

        return MD5.getMD5(
                buildString(MD5.getMD5(result).toUpperCase(), TOKEN))
                .toUpperCase();
    }

    public static String buildString(Object... element) {
        StringBuffer sb = new StringBuffer();
        for (Object str : element) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static final String DOMAIN = "http://chinauenvi.wxy01.com/index.php/mobile/";
    public static final String TOKEN = "6452cd3ee0c735e1d32aa29f1cb87ff520f60c73febc275f380bf06acf386996";
}
