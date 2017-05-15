package com.lhx.library.http;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.lhx.library.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;

/**
 * http请求
 */

public class HttpRequest {

    private static final String TAG = "HttpRequest";

    //请求失败错误码
    public static final int ERROR_CODE_HTTP = -1; //http错误
    public static final int ERROR_CODE_TIME_OUT = 0; //请求超时
    public static final int ERROR_CODE_BAD_URL = 1; //url 不合法
    public static final int ERROR_CODE_IO = 2; //输入输出流异常
    public static final int ERROR_CODE_FILE_NOT_EXIST = 3; //上传文件文件不存在

    //postBody 默认格式
    protected static final String URL_ENCODE = "application/x-www-form-urlencoded";
    public static final int POST_BODY_FORMAT_URL_ENCODE = 0;

    //postBody 表单格式
    protected static final String MULTI_PART_FORM_DATA = "multipart/form-data";
    public static final int POST_BODY_FORMAT_MULTI_PART_FORM_DATA = 1;

    @IntDef({POST_BODY_FORMAT_URL_ENCODE, POST_BODY_FORMAT_MULTI_PART_FORM_DATA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PostBodyFormat {
    }

    //字符集
    protected static final String CHAR_SET = "charset";

    //内容类型
    protected static final String CONTENT_TYPE = "Content-Type";

    //http链接
    protected HttpURLConnection mConn;

    //请求URL
    String mURL;

    //请求回调
    HttpRequestHandler mHttpRequestHandler;

    //参数信息
    ArrayList<Param> mParams = new ArrayList<>();

    //参数编码类型
    String mStringEncoding = "utf-8";

    //post body
    String mPostBody;

    //超时 毫秒
    int mTimeoutInterval = 30000;

    //上传总大小
    int mUploadTotalSize = 0;

    //下载总大小
    int mDownloadTotalSize = 0;

    //post 请求参数格式
    private
    @PostBodyFormat
    int mPostBodyFormat;

    public String getStringEncoding() {
        return mStringEncoding;
    }

    public void setStringEncoding(String stringEncoding) {
        mStringEncoding = stringEncoding;
    }

    public String getURL() {
        return mURL;
    }

    public ArrayList<Param> getParams() {
        return mParams;
    }

    public
    @PostBodyFormat
    int getPostBodyFormat() {
        return mPostBodyFormat;
    }

    public void setPostBodyFormat(@PostBodyFormat int postBodyFormat) {
        mPostBodyFormat = postBodyFormat;
    }

    public void setTimeoutInterval(int timeoutInterval) {
        mTimeoutInterval = timeoutInterval;
    }

    public void setHttpRequestHandler(HttpRequestHandler httpRequestHandler) {
        mHttpRequestHandler = httpRequestHandler;
    }

    public HttpRequest(String URL) {
        mURL = URL;
    }

    //添加参数 参数名可以相同
    public void addPostValue(String key, String value) {
        if (key != null && value != null) {
            mParams.add(new Param(key, value));
        }
    }

    //添加参数 会替换参数key相同的值
    public void setPostValue(String key, String value) {
        if (key != null && value != null) {
            for (Param param : mParams) {
                if (param.key.equals(key)) {
                    param.value = value;
                    return;
                }
            }

            mParams.add(new Param(key, value));
        }
    }

    //添加文件
    public void addFile(String key, String filePath){
        if(key != null && filePath != null){
            File file = new File(filePath);
            addFile(key, file);
        }
    }

    //添加文件
    public void addFile(String key, File file){
        if(key != null && file != null){
            if(file.exists()){
                mParams.add(new Param(key, file));
            }
        }
    }

    //开启请求任务
    public void startRequest() {
        try {
            URL url = new URL(mURL);
            mConn = (HttpURLConnection) url.openConnection();
            mConn.setReadTimeout(mTimeoutInterval);
            mConn.setConnectTimeout(mTimeoutInterval);
            mConn.setRequestProperty("Connection", "Keep-Alive");

            if (mParams.size() > 0) {
                mConn.setRequestMethod("POST");
                buildPostBody();
            } else {
                mConn.setRequestMethod("GET");
            }

            int code = mConn.getResponseCode();
            //http 304 是读本地缓存时返回的
            if ((code < 200 || code > 299) && code != 304) {
                fail(ERROR_CODE_HTTP, code);
            }

            InputStream is = mConn.getInputStream();
            byte[] bytes = readInputStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            fail(ERROR_CODE_BAD_URL, 0);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail(ERROR_CODE_IO, 0);
        }
    }

    //请求失败
    private void fail(int code, int httpCode) {
        if (mHttpRequestHandler != null) {
            mHttpRequestHandler.onFail(code, httpCode);
            mHttpRequestHandler.onComplete();
        }
    }

    //参数信息
    private class Param {

        //普通参数
        static final int PARAM_TYPE_NORMAL = 0;

        //文件
        static final int PARAM_TYPE_FILE = 1;

        //参数key
        String key;

        //参数值
        String value;

        //文件
        File file;

        //参数类型
        int type;

        Param(String key, String value) {
            this.key = key;
            this.value = value;
            type = PARAM_TYPE_NORMAL;
        }

        public Param(String key, File file) {
            this.key = key;
            this.file = file;
            type = PARAM_TYPE_FILE;
        }

        String getEncodedValue(String stringEncoding) {

            try {
                return URLEncoder.encode(value, stringEncoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return value;
            }
        }
    }

    //构建post body
    private void buildPostBody() {
        if (mParams.size() == 0 || mConn == null)
            return;
        switch (mPostBodyFormat) {
            case POST_BODY_FORMAT_URL_ENCODE: {
                if (isExistFile()) {
                    buildMultiPartFormDataPostBody();
                } else {
                    buildURLEncodePostBody();
                }
                break;
            }

            case POST_BODY_FORMAT_MULTI_PART_FORM_DATA: {
                buildMultiPartFormDataPostBody();
                break;
            }
        }
    }

    //构建 URL encode 类型的
    private void buildURLEncodePostBody() {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (Param param : mParams) {
            builder.append(param.key);
            builder.append("=");
            builder.append(param.getEncodedValue(mStringEncoding));
            if (i != mParams.size() - 1) {
                builder.append("&");
            }
            i++;
        }

        mPostBody = builder.toString();
        mConn.setRequestProperty(CONTENT_TYPE, String.format(Locale.getDefault(), "%s; %s=%s", URL_ENCODE, CHAR_SET,
                mStringEncoding));
        Log.d(TAG, mPostBody);
    }

    //构建multi part form data 类型的，上传文件必须用此类型
    private void buildMultiPartFormDataPostBody() {

        try {
            mConn.setDoOutput(true);
            OutputStream outputStream = mConn.getOutputStream();

            ///form data 边界
            String boundary = "0xKhTmLbOuNdArY-" + Build.SERIAL;

            //格式类型
            mConn.setRequestProperty(CONTENT_TYPE, String.format(Locale.getDefault(), "%s; %s=%s; boundary=%s", MULTI_PART_FORM_DATA,
                    CHAR_SET, mStringEncoding, boundary));
            if (isExistFile()) {
                mConn.setChunkedStreamingMode(0); //分块上传，防止内存过大
            }

            //每个参数的分隔符
            String paramSeparator = String.format(Locale.getDefault(), "\r\n--%s\r\n", boundary);

            //数据总长度
            int totalSize = 0;

            //添加开始边界
            byte[] bytes = String.format(Locale.getDefault(), "--%s\r\n", boundary).getBytes(mStringEncoding);
            outputStream.write(bytes);
            totalSize += bytes.length;

            int i = 0;

            for(Param param : mParams){
                StringBuilder builder = new StringBuilder();
                switch (param.type){
                    case Param.PARAM_TYPE_NORMAL : {
                        builder.append("Content-Disposition: form-data; name=\"");
                        builder.append(param.key);
                        builder.append("\"\r\n");
                        builder.append(param.value);
                        bytes = builder.toString().getBytes(mStringEncoding);
                        outputStream.write(bytes);
                        totalSize += bytes.length;

                        break;
                    }
                    case Param.PARAM_TYPE_FILE : {
                        builder.append("Content-Disposition: form-data; name=\"");
                        builder.append(param.key);
                        builder.append("\"; filename=\"");
                        builder.append(param.file.getName());
                        builder.append("\"\r\n");
                        builder.append("Content-Type: ");
                        builder.append(FileUtil.getMimeType(param.file.getAbsolutePath()));
                        builder.append("\r\n");

                        bytes = builder.toString().getBytes(mStringEncoding);
                        outputStream.write(bytes);
                        totalSize += bytes.length;
                        totalSize += readFile(param.file, outputStream);

                        break;
                    }
                }
                if(i != mParams.size() - 1){
                    outputStream.write(paramSeparator.getBytes());
                }
            }

            //添加结束边界
            outputStream.write(String.format(Locale.getDefault(), "\r\n--%s--\r\n", boundary).getBytes
                    (mStringEncoding));

            mConn.setRequestProperty("Content-Length", totalSize + "");
            mUploadTotalSize = totalSize;

            outputStream.flush();
            outputStream.close();

        }catch (IOException ioe){
            ioe.printStackTrace();
            fail(ERROR_CODE_IO, 0);
        }
    }

    //读取文件
    private int readFile(File file, OutputStream outputStream) throws IOException{
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        int result = 0;
        int len = 0;
        do {
            byte[] bytes = new byte[1024 * 256];
            result = inputStream.read(bytes);
            outputStream.write(bytes);
            if(result != -1){
                len += result;
            }
        }while (result != -1);

        inputStream.close();

        return len;
    }

    //把输入流转成字节流
    private byte[] readInputStream(InputStream inputStream){
        try {
            byte[] bytes = new byte[1024 * 256];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while (inputStream.read(bytes) != -1){
                outputStream.write(bytes);
            }

            bytes = outputStream.toByteArray();
            outputStream.close();

            return bytes;
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    //判断是否存在文件
    private boolean isExistFile() {
        for (Param param : mParams) {
            if (param.type == Param.PARAM_TYPE_FILE) {
                return true;
            }
        }

        return false;
    }
}
