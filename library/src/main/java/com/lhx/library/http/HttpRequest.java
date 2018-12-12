package com.lhx.library.http;

import android.os.Build;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.lhx.library.util.FileUtil;
import com.lhx.library.util.StringUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * http请求
 */

public class HttpRequest {

    private static final String TAG = "HttpRequest";

    //请求失败错误码
    public static final int ERROR_CODE_NONE = 0; //请求成功 没有错误
    public static final int ERROR_CODE_HTTP = 1; //http错误
    public static final int ERROR_CODE_TIME_OUT = 2; //请求超时
    public static final int ERROR_CODE_BAD_URL = 3; //url 不合法
    public static final int ERROR_CODE_IO = 4; //输入输出流异常
    public static final int ERROR_CODE_FILE_NOT_EXIST = 5; //上传文件文件不存在
    public static final int ERROR_CODE_NETWORK = 6; //网络错误
    public static final int ERROR_CODE_API = 7; //接口报错
    public static final int ERROR_CODE_NOT_KNOW = 8; //未知错误

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

    //请求状态
    public static final int HTTP_REQUEST_STATE_PREPARING = 0; //准备中
    public static final int HTTP_REQUEST_STATE_LOADING = 1; //加载中
    public static final int HTTP_REQUEST_STATE_CANCELED = 2; //取消了
    public static final int HTTP_REQUEST_STATE_FINISHED = 3; //完成了
    public static final int HTTP_REQUEST_STATE_FAILED = 4; //失败了
    public static final int HTTP_REQUEST_STATE_CLOESED = 5; //请求已关闭

    @IntDef({
            HTTP_REQUEST_STATE_PREPARING,
            HTTP_REQUEST_STATE_LOADING,
            HTTP_REQUEST_STATE_CANCELED,
            HTTP_REQUEST_STATE_FINISHED,
            HTTP_REQUEST_STATE_FAILED,
            HTTP_REQUEST_STATE_CLOESED
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface HttpRequestState{}

    protected @HttpRequestState int mState = HTTP_REQUEST_STATE_PREPARING;

    //字符集
    protected static final String CHAR_SET = "charset";

    //内容类型
    protected static final String CONTENT_TYPE = "Content-Type";

    //http链接
    protected HttpURLConnection mConn;

    //请求URL
    protected String mURL;

    //参数信息
    protected ArrayList<Param> mParams = new ArrayList<>();

    //编码类型
    protected String mStringEncoding = "utf-8";

    //请求方法 默认是空，当有参数时将自动设为POST
    private String mHttpMethod;

    //超时 毫秒
    protected int mTimeoutInterval = 15000;

    //上传总大小
    protected long mTotalSizeToUpload = 0;

    //已经上传的大小
    protected long mTotalSizeDidUpload = 0;

    //下载总大小
    protected long mTotalSizeToDownload = 0;

    //是否显示上传进度
    private boolean mShowUploadProgress = false;

    //是否显示下载进度
    private boolean mShowDownloadProgress = false;

    //http响应码
    protected int mHttpResponseCode;

    //错误码
    protected int mErrorCode = ERROR_CODE_NONE;

    //请求返回的数据
    protected byte[] mResponseData;

    //下载路径 如果已设置，下载完成后将会把文件移到这里, 文件类型会根据 contentType 中的mimeType来设置
    private String mDownloadDestinationPath;

    //下载临时文件，下载的内容将保存在这里，如果没有设置，下载后的数据将保存在内存中 ,文件类型会根据 contentType 中的mimeType来设置
    private String mDownloadTemporayPath;

    //是否根据下载内容的大小自动判断使用临时文件保存下载数据
    private boolean mUseDownTemporayFileAutomatically = true;

    //当下载的内容达到这个值时将使用临时文件
    private long mUseDownTemporayFileSize = 1024 * 256;

    //当请求完成时是否删除临时文件
    private boolean mShouldDeleteDownloadTemporayFileAfterFinish = true;

    //下载的文件拓展名称
    private String mFileExtension;

    //请求进度回调
    private HttpProgressHandler<HttpRequest> mHttpProgressHandler;

    //是否已设置cookie管理
    private static boolean mSetupCookieManager = false;

    //post 请求参数格式
    private
    @PostBodyFormat
    int mPostBodyFormat;

    //通过网络错误获取错误信息
    public static String getErrorStringFromCode(int errCode, int httpCode){

        switch (errCode){
            case ERROR_CODE_NONE :
                return "请求成功";
//            case ERROR_CODE_HTTP :
//                return String.valueOf(httpCode);
//            case ERROR_CODE_TIME_OUT :
//                return "请求超时";
//            case ERROR_CODE_BAD_URL :
//                return "URL不合法";
//            case ERROR_CODE_IO :
//                return "IO异常";
//            case ERROR_CODE_NETWORK :
//                return "网络状态不佳";
//            case ERROR_CODE_FILE_NOT_EXIST :
//                return "上传的文件不存在";
//            case ERROR_CODE_NOT_KNOW :
                default :
                    return "网络状态不佳";
        }
    }

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

    public void setHttpMethod(String httpMethod) {
        mHttpMethod = httpMethod;
    }

    public byte[] getResponseData(){
        if(mResponseData == null){
            if(mDownloadTemporayPath != null){
                mResponseData = FileUtil.readFile(mDownloadTemporayPath);
            }else if(mDownloadDestinationPath != null) {
                mResponseData = FileUtil.readFile(mDownloadDestinationPath);
            }
        }
        return mResponseData;
    }

    public int getErrorCode(){
        return mErrorCode;
    }

    public int getHttpResponseCode(){
        return mHttpResponseCode;
    }

    public String getDownloadDestinationPath() {
        return mDownloadDestinationPath;
    }

    public void setDownloadDestinationPath(String path) {
        mDownloadDestinationPath = path;
    }

    public String getDownloadTemporayPath() {
        return mDownloadTemporayPath;
    }

    public void setDownloadTemporayPath(String filePath) {
        mDownloadTemporayPath = filePath;
    }

    public boolean isUseDownTemporayFileAutomatically() {
        return mUseDownTemporayFileAutomatically;
    }

    public void setUseDownTemporayFileAutomatically(boolean flag) {
        mUseDownTemporayFileAutomatically = flag;
    }

    public long getUseDownTemporayFileSize() {
        return mUseDownTemporayFileSize;
    }

    public void setUseDownTemporayFileSize(long size) {
        mUseDownTemporayFileSize = size;
    }

    public boolean shouldDeleteDownloadTemporayFileAfterFinish() {
        return mShouldDeleteDownloadTemporayFileAfterFinish;
    }

    public void setShouldDeleteDownloadTemporayFileAfterFinish(boolean flag) {
        mShouldDeleteDownloadTemporayFileAfterFinish = flag;
    }

    public boolean isShowUploadProgress() {
        return mShowUploadProgress;
    }

    public void setShowUploadProgress(boolean flag) {
        mShowUploadProgress = flag;
    }

    public boolean isShowDownloadProgress() {
        return mShowDownloadProgress;
    }

    public void setShowDownloadProgress(boolean flag) {
        mShowDownloadProgress = flag;
    }

    public HttpProgressHandler<HttpRequest> getHttpProgressHandler() {
        return mHttpProgressHandler;
    }

    public void setHttpProgressHandler(HttpProgressHandler<HttpRequest> httpProgressHandler) {
        mHttpProgressHandler = httpProgressHandler;
    }

    public HttpRequest(String URL) {
        mURL = URL;
    }

    protected void setState(@HttpRequestState int state){
        if(mState != state){
            mState = state;
        }
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

    /**开启请求任务
     * @return 是否成功
     */
    public boolean startRequest() {

        switch (mState){
            case HTTP_REQUEST_STATE_LOADING :
                throw new IllegalStateException("HttpRequest is loading");
            case HTTP_REQUEST_STATE_FINISHED :
                throw new IllegalStateException("HttpRequest have been finished");
            case HTTP_REQUEST_STATE_CANCELED :
                throw new IllegalStateException("HttpRequest have been canceled");
            case HTTP_REQUEST_STATE_CLOESED :
                throw new IllegalStateException("HttpRequest have been close");
        }

        InputStream inputStream = null;
        try {

            setState(HTTP_REQUEST_STATE_LOADING);

            if(!mSetupCookieManager) {
                mSetupCookieManager = true;
                CookieManager cookieManager = new CookieManager();
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                CookieHandler.setDefault(cookieManager);
            }

            if("GET".equals(mHttpMethod)){
                buildGetParams();
            }

            URL url = new URL(mURL);
            mConn = (HttpURLConnection) url.openConnection();
            mConn.setReadTimeout(mTimeoutInterval);
            mConn.setDoInput(true);
            mConn.setUseCaches(false);
            mConn.setConnectTimeout(mTimeoutInterval);
            mConn.setRequestProperty("Connection", "Keep-Alive");

            if(StringUtil.isEmpty(mHttpMethod)){
                if (mParams.size() > 0) {
                    mConn.setRequestMethod("POST");
                    buildPostBody();
                } else {
                    mConn.setRequestMethod("GET");
                }
            }else{
                mConn.setRequestMethod(mHttpMethod);
            }

            mHttpResponseCode = mConn.getResponseCode();
            //http 304 是读本地缓存时返回的
            if ((mHttpResponseCode < 200 || mHttpResponseCode > 299) && mHttpResponseCode != 304) {
                fail(ERROR_CODE_HTTP, mHttpResponseCode);
                return false;
            }


            /// 系统提供的方法要 java 7以上才能用
            mTotalSizeToDownload = StringUtil.parseLong(mConn.getHeaderField("Content-Length"));
            inputStream = mConn.getInputStream();

//            String header = mConn.getHeaderField("set-Cookie");

            if(mDownloadTemporayPath != null){
                writeToFile(inputStream, mConn.getContentType());
            }else {
                if(mUseDownTemporayFileAutomatically && mTotalSizeToDownload >= mUseDownTemporayFileSize){
                    writeToFile(inputStream, mConn.getContentType());
                }else {
                    mResponseData = readInputStream(inputStream);
                }
            }
            setState(HTTP_REQUEST_STATE_FINISHED);
            mConn = null;
            return true;
        } catch (MalformedURLException e) {

            fail(ERROR_CODE_BAD_URL, 0);
        }catch (SocketException e){

            if(mState == HTTP_REQUEST_STATE_LOADING){
                fail(ERROR_CODE_HTTP, 0);
            }
        }catch (IOException ioe) {

            //包括 UnknownHostException
            if(mState == HTTP_REQUEST_STATE_LOADING){
                if(ioe instanceof UnknownHostException){
                    fail(ERROR_CODE_NETWORK, 0);
                }else {
                    fail(ERROR_CODE_TIME_OUT, 0);
                }
            }
        } finally {

            //关闭io流
            closeStream(inputStream);

            if(mConn != null && mState == HTTP_REQUEST_STATE_LOADING){
                mConn.disconnect();
            }
        }

        if(mState != HTTP_REQUEST_STATE_CANCELED && mState != HTTP_REQUEST_STATE_FAILED){
            fail(ERROR_CODE_NOT_KNOW, 0);
        }

        return false;
    }

    //取消
    public synchronized void cancel(){

        if(mConn != null && mState == HTTP_REQUEST_STATE_LOADING){
            setState(HTTP_REQUEST_STATE_CANCELED);
            mConn.disconnect();
        }
    }

    //请求失败
    private void fail(int code, int httpCode) {
        mHttpResponseCode = httpCode;
        mErrorCode = code;
        setState(HTTP_REQUEST_STATE_FAILED);
        mConn = null;
    }

    //关闭 http 释放资源
    public synchronized void close(){
        if(mState == HTTP_REQUEST_STATE_CLOESED)
            return;

        cancel();
        setState(HTTP_REQUEST_STATE_CLOESED);
        mConn = null;
        mResponseData = null;
        mParams.clear();
        if(mShouldDeleteDownloadTemporayFileAfterFinish && mDownloadTemporayPath != null){
            FileUtil.deleteFile(mDownloadTemporayPath);
        }
    }

    //关闭io流
    private void closeStream(Closeable stream){
        if(stream != null){
            try {
                stream.close();
            }catch (IOException e){

            }
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

        //multiPart 数据
        byte[] multiPartBytes;

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

        byte[] getMultiPartBytes(){
            try {
                String string = null;
                if(multiPartBytes == null){
                    switch (type){
                        case PARAM_TYPE_NORMAL :
                            string = "Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n" + value;
                            break;
                        case PARAM_TYPE_FILE :
                            StringBuilder builder = new StringBuilder();
                            builder.append("Content-Disposition: form-data; name=\"");
                            builder.append(key);
                            builder.append("\"; filename=\"");
                            builder.append(file.getName());
                            builder.append("\"\r\n");
                            builder.append("Content-Type: ");

                            String mime = FileUtil.getMimeType(file.getAbsolutePath());
                            if(StringUtil.isEmpty(mime)){
                                mime = "application/octet-stream; charset=" + mStringEncoding;
                            }
                            builder.append(mime);
                            builder.append("\r\n\r\n");
                            string = builder.toString();
                            break;
                        default :
                            string = "";
                            break;
                    }
                    multiPartBytes = string.getBytes(mStringEncoding);
                }

            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
                multiPartBytes = new byte[0];
            }

            return multiPartBytes;
        }
    }

    //构建post body
    private void buildPostBody() throws IOException {
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

    //构建get参数
    private void buildGetParams(){
        if(mURL != null && mParams.size() > 0){
            StringBuilder builder = new StringBuilder(mURL);
            builder.append("?");
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

            mURL = builder.toString();
        }
    }

    //构建 URL encode 类型的
    private void buildURLEncodePostBody(){

        mTotalSizeDidUpload = 0;
        mConn.setRequestProperty(CONTENT_TYPE, String.format(Locale.getDefault(), "%s; %s=%s", URL_ENCODE, CHAR_SET,
                mStringEncoding));
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

        byte[] bytes =  getBytes(builder.toString());
        mTotalSizeToUpload = bytes.length;
        mConn.setRequestProperty("Content-Length", mTotalSizeToUpload + "");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){

            mConn.setFixedLengthStreamingMode((int)mTotalSizeToUpload);
        }else {
            //sdk 1.9才有
            try {
                //jdk 1.7才有
                mConn.setFixedLengthStreamingMode(mTotalSizeToUpload);
            }catch (NoSuchMethodError e){
                mConn.setFixedLengthStreamingMode((int)mTotalSizeToUpload);
            }
        }


        OutputStream outputStream = null;
        try {
            mConn.setDoOutput(true);
            outputStream = new DataOutputStream(mConn.getOutputStream());

            //这时已建立连接 不能在设置 setRequestProperty
            outputStream.write(bytes);

            mTotalSizeDidUpload += bytes.length;
            updateUploadProgress();

            outputStream.flush();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            closeStream(outputStream);
        }
    }

    //获取字节
    private byte[] getBytes(String str){
        try {
            return str.getBytes(mStringEncoding);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            if(mConn != null && mState == HTTP_REQUEST_STATE_LOADING){
                mConn.disconnect();
            }
        }

        return null;
    }

    //构建multi part form data 类型的，上传文件必须用此类型
    private void buildMultiPartFormDataPostBody(){

        mTotalSizeDidUpload = 0;
        mTotalSizeToUpload = 0;

        ///form data 边界
        String boundary = "0xKhTmLbOuNdArY-" + Build.SERIAL;

        //格式类型
        String contentType = String.format(Locale.getDefault(), "%s; %s=%s; boundary=%s",
                MULTI_PART_FORM_DATA, CHAR_SET, mStringEncoding, boundary);

        mConn.setRequestProperty(CONTENT_TYPE, contentType);

        //每个参数的分隔符
        byte[] paramSeparatorBytes = getBytes(String.format(Locale.getDefault(), "\r\n--%s\r\n", boundary));

        //添加开始边界
        byte[] bytes = getBytes(String.format(Locale.getDefault(), "--%s\r\n", boundary));
        //结束边界
        byte[] endBytes = getBytes(String.format(Locale.getDefault(), "\r\n--%s--\r\n", boundary));


        mTotalSizeToUpload += bytes.length;
        mTotalSizeToUpload += endBytes.length;

        int i = 0;
        //计算body大小
        for(Param param : mParams){
            mTotalSizeToUpload += param.getMultiPartBytes().length;
            if(param.type == Param.PARAM_TYPE_FILE){
                mTotalSizeToUpload += param.file.length();
            }
            if (i != mParams.size() - 1) {
                mTotalSizeToUpload += paramSeparatorBytes.length;
            }
            i ++;
        }

        //设置上传长度
        mConn.setRequestProperty("Content-Length", mTotalSizeToUpload + "");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            mConn.setFixedLengthStreamingMode((int)mTotalSizeToUpload);
        }else {
            //api 19才有
            try {
                //jdk1.7 才有
                mConn.setFixedLengthStreamingMode(mTotalSizeToUpload);
            }catch (NoSuchMethodError e){
                mConn.setFixedLengthStreamingMode((int)mTotalSizeToUpload);
            }
        }


        OutputStream outputStream = null;
        try {
            mConn.setDoOutput(true);
            outputStream = new BufferedOutputStream(mConn.getOutputStream());

            //这时已建立连接 不能在设置 setRequestProperty
            outputStream.write(bytes);
            mTotalSizeDidUpload += bytes.length;
            updateUploadProgress();

            i = 0;
            for (Param param : mParams) {
                StringBuilder builder = new StringBuilder();
                switch (param.type) {
                    case Param.PARAM_TYPE_NORMAL: {
                        outputStream.write(param.getMultiPartBytes());
                        mTotalSizeDidUpload += param.getMultiPartBytes().length;
                        break;
                    }
                    case Param.PARAM_TYPE_FILE: {
                        outputStream.write(param.getMultiPartBytes());
                        mTotalSizeDidUpload += paramSeparatorBytes.length;
                        readFile(param.file, outputStream);

                        break;
                    }
                }
                if (i != mParams.size() - 1) {
                    outputStream.write(paramSeparatorBytes);
                    mTotalSizeDidUpload += paramSeparatorBytes.length;
                }

                updateUploadProgress();

                i ++;
            }
            //添加结束边界
            outputStream.write(endBytes);
            outputStream.flush();

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            closeStream(outputStream);
        }
    }

    //更新上传进度
    private void updateUploadProgress(){
        if(mShowUploadProgress && mHttpProgressHandler != null){
            mHttpProgressHandler.onUpdateUploadProgress(this, (float)mTotalSizeDidUpload / (float)mTotalSizeToUpload);
        }
    }

    //读取文件
    private void readFile(File file, OutputStream outputStream){
        BufferedInputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            int result = 0;

            byte[] bytes = new byte[1024 * 256];
            while ((result = inputStream.read(bytes)) != -1){
                //写入实际大小
                outputStream.write(bytes, 0, result);
                mTotalSizeDidUpload += result;
                updateUploadProgress();
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            closeStream(inputStream);
        }
    }

    //把输入流转成字节流
    private byte[] readInputStream(InputStream inputStream){

        BufferedInputStream bufferedInputStream = null;
        ByteArrayOutputStream outputStream = null;
        byte[] bytes = new byte[1024 * 256];

        try {

            bufferedInputStream = new BufferedInputStream(inputStream);
            outputStream = new ByteArrayOutputStream();

            int len = 0;
            long totalSize = 0;
            while ((len = bufferedInputStream.read(bytes)) != -1) {

                //不一定每次读取都有 1024 * 256
                outputStream.write(bytes, 0, len);
                if(mShowDownloadProgress && mHttpProgressHandler != null){
                    totalSize += len;
                    mHttpProgressHandler.onUpdateDownloadProgress(this, (float)totalSize / (float)mTotalSizeToDownload);
                }
            }

            bytes = outputStream.toByteArray();

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            closeStream(bufferedInputStream);
            closeStream(outputStream);
        }

        return bytes;
    }

    //把数据写入文件
    private void writeToFile(InputStream inputStream, String contentType) {

        ///获取文件拓展名称
        if (!TextUtils.isEmpty(contentType)) {
            mFileExtension = FileUtil.getFileExtensionFromMimeType(contentType);
            if (!TextUtils.isEmpty(mFileExtension)) {
                mDownloadTemporayPath = FileUtil.appendFileExtension(mDownloadTemporayPath, mFileExtension, true);
            }
        }

        File file = new File(mDownloadTemporayPath);

        FileOutputStream outputStream = null;
        BufferedInputStream bufferedInputStream = null;

        try {

            FileUtil.createNewFileIfNotExist(file);
            outputStream = new FileOutputStream(file);
            bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] bytes = new byte[1024 * 256];

            int len = 0;
            long totalSize = 0;
            while ((len = bufferedInputStream.read(bytes)) != -1) {

                //不一定每次读取都有 1024 * 256
                outputStream.write(bytes, 0, len);
                if (mShowDownloadProgress && mHttpProgressHandler != null) {
                    totalSize += len;
                    mHttpProgressHandler.onUpdateDownloadProgress(this, (float) totalSize / (float) mTotalSizeToDownload);
                }
            }

            outputStream.flush();

            //把文件移到其他位置
            if (mDownloadDestinationPath != null && file.exists()) {
                if (!TextUtils.isEmpty(mDownloadDestinationPath)) {
                    mDownloadDestinationPath = FileUtil.appendFileExtension(mDownloadDestinationPath, mFileExtension, true);
                }
                if (FileUtil.moveFile(file, mDownloadDestinationPath)) {
                    mDownloadTemporayPath = null;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(bufferedInputStream);
            closeStream(outputStream);
        }
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
