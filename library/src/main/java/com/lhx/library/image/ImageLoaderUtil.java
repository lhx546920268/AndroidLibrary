package com.lhx.library.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.util.FileUtil;
import com.lhx.library.util.StringUtil;
import com.lhx.library.util.ViewUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * 图片加载工具类
 */
public class ImageLoaderUtil implements ImageLoadingListener{

    //图片加载时的背景颜色
    private static int mPlaceholderColor;

    private static ImageLoaderUtil mImageLoaderUtil = null;

    //显示类型


    //单例
    public synchronized static ImageLoaderUtil getInstance(){
        if(mImageLoaderUtil == null){
            mImageLoaderUtil = new ImageLoaderUtil();
        }

        return mImageLoaderUtil;
    }

    protected static ImageLoader mImageLoader = ImageLoader.getInstance();

    //初始化
    public static void initialize(Context context){

        mPlaceholderColor = ContextCompat.getColor(context, R.color.image_placeholder_color);

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 1);
        config.threadPoolSize(3);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.imageDownloader(new BaseImageDownloader(context));
        config.imageDecoder(new BaseImageDecoder(false));
        config.defaultDisplayImageOptions(DisplayImageOptions.createSimple());
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        File file = new File(FileUtil.getImageCacheFolder(context));
        config.diskCache(new UnlimitedDiscCache(file)).diskCacheFileCount(800).diskCacheSize(800 *
                1024 * 1024);

        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        if(view instanceof ImageView){
            ImageView imageView = (ImageView)view;
            Object tag = imageView.getTag(R.id.tag_scale_type);
            if(tag instanceof ImageView.ScaleType){
                ImageView.ScaleType type = (ImageView.ScaleType)tag;
                imageView.setScaleType(type);
            }

            tag = imageView.getTag(R.id.tag_background);
            if(tag == null){
                ViewUtil.setBackground(null, imageView);
            }else if(tag instanceof Drawable){
                ViewUtil.setBackground((Drawable)tag, imageView);
            }
        }
    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }

    /**
     * 配置图片信息
     * @param imageView 图片
     * @param round 是否是圆角
     */
    public static void configure(ImageView imageView, boolean round){
        Object tag = imageView.getTag(R.id.tag_scale_type);
        if(tag instanceof ImageView.ScaleType){

        }else {
            imageView.setTag(R.id.tag_scale_type, imageView.getScaleType());
            imageView.setTag(R.id.tag_background, imageView.getBackground());
        }

        if(!round){
            imageView.setBackgroundColor(mPlaceholderColor);
        }
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    public static void displayImage(ImageView imageView, String url, DisplayImageOptions options){
        displayImage(imageView, url, options, false);
    }

    public static void displayImage(ImageView imageView, String url, DisplayImageOptions options, boolean round){
        String oldUrl = (String)imageView.getTag(R.id.tag_loading_url);

        if(TextUtils.equals(oldUrl, url)){
            return;
        }

        if(!TextUtils.isEmpty(oldUrl)){
            mImageLoader.cancelDisplayTask(imageView);
        }
        imageView.setTag(R.id.tag_loading_url, url);
        ImageLoaderUtil.configure(imageView, round);
        mImageLoader.displayImage(url, imageView, options, ImageLoaderUtil.getInstance());
    }



    //显示圆形图片

    public static void displayCircleImage(ImageView imageView, String url) {
        displayImage(imageView, url, getCircleDisplayImageOptions(url), true);
    }

    public static void displayCircleImage(ImageView imageView, String url, ImageScaleType imageScaleType){
        displayImage(imageView, url, getCircleDisplayImageOptions(imageScaleType, url), true);
    }

    public static void displayCircleImage(ImageView imageView, String url, int placeholderImageRes){
        displayImage(imageView, url, getCircleDisplayImageOptions(placeholderImageRes, url), true);
    }

    public static void displayCircleImage(ImageView imageView, String url, int placeholderImageRes,
                                          ImageScaleType imageScaleType){
        displayImage(imageView, url, getCircleDisplayImageOptions(placeholderImageRes, imageScaleType, url), true);
    }

    //显示普通图片

    public static void displayImage(ImageView imageView, String url) {
        displayImage(imageView, url, getDisplayImageOptions(url));
    }

    public static void displayImage(ImageView imageView, String url, ImageScaleType imageScaleType){
        displayImage(imageView, url, getDisplayImageOptions(imageScaleType, url));
    }

    public static void displayImage(ImageView imageView, String url, int placeholderImageRes){
        displayImage(imageView, url, getDisplayImageOptions(placeholderImageRes, url));
    }

    public static void displayImage(ImageView imageView, String url, int placeholderImageRes,
                                          ImageScaleType imageScaleType){
        displayImage(imageView, url, getDisplayImageOptions(placeholderImageRes, imageScaleType, url));
    }

    //显示圆角图片

    public static void displayRoundImage(ImageView imageView, String url, int radiusPixels){
        displayImage(imageView, url, getRoundDisplayImageOptions(radiusPixels, url),
                true);
    }

    public static void displayRoundImage(ImageView imageView, String url, int radiusPixels, int placeholderImageRes) {
        displayImage(imageView, url, getRoundDisplayImageOptions(placeholderImageRes, radiusPixels, url),
                true);
    }

    public static void displayRoundImage(ImageView imageView, String url, int radiusPixels,
                                         ImageScaleType imageScaleType) {
        displayImage(imageView, url, getRoundDisplayImageOptions(imageScaleType, radiusPixels, url),
                true);
    }

    public static void displayRoundImage(ImageView imageView, String url, int radiusPixels, int placeholderImageRes,
                                         ImageScaleType imageScaleType) {
        displayImage(imageView, url, getRoundDisplayImageOptions(placeholderImageRes, imageScaleType, radiusPixels, url),
                true);
    }

    //普通图片选项

    public static DisplayImageOptions getDisplayImageOptions(String url){
        return getDisplayImageOptions(App.ImagePlaceHolder, url);
    }

    public static DisplayImageOptions getDisplayImageOptions(ImageScaleType type, String url){
        return getDisplayImageOptions(App.ImagePlaceHolder, type, url);
    }

    public static DisplayImageOptions getDisplayImageOptions(int placeholderImageRes, String url){
        return getDisplayImageOptions(placeholderImageRes, ImageScaleType.EXACTLY, url);
    }

    public static DisplayImageOptions getDisplayImageOptions(int placeholderImageRes, ImageScaleType type, String url) {
        return getDisplayImageOptions(placeholderImageRes, type, new FadeInBitmapDisplayer(200, true, false, false), url);
    }

    //圆角图片选项

    public static DisplayImageOptions getRoundDisplayImageOptions(int
            radiusPixels, String url) {
        return getRoundDisplayImageOptions(App.ImagePlaceHolder, radiusPixels, url);
    }

    public static DisplayImageOptions getRoundDisplayImageOptions(ImageScaleType type, int
            radiusPixels, String url) {
        return getRoundDisplayImageOptions(App.ImagePlaceHolder, type, radiusPixels, url);
    }

    public static DisplayImageOptions getRoundDisplayImageOptions(int placeholderImageRes, int
            radiusPixels, String url) {
        return getRoundDisplayImageOptions(placeholderImageRes, ImageScaleType.EXACTLY, radiusPixels, url);
    }

    public static DisplayImageOptions getRoundDisplayImageOptions(int placeholderImageRes, ImageScaleType type, int
            radiusPixels, String url) {
        return getDisplayImageOptions(placeholderImageRes, type, new RoundedBitmapDisplayer(radiusPixels), url);
    }

    //圆形图片选项

    public static DisplayImageOptions getCircleDisplayImageOptions(String url) {
        return getCircleDisplayImageOptions(App.ImagePlaceHolderCircle > 0 ? App.ImagePlaceHolderCircle : App
                .ImagePlaceHolder, url);
    }

    public static DisplayImageOptions getCircleDisplayImageOptions(ImageScaleType type, String url) {
        return getCircleDisplayImageOptions(App.ImagePlaceHolderCircle > 0 ? App.ImagePlaceHolderCircle : App.ImagePlaceHolder,
                type, url);
    }

    public static DisplayImageOptions getCircleDisplayImageOptions(int placeholderImageRes, String url) {
        return getCircleDisplayImageOptions(placeholderImageRes, ImageScaleType.EXACTLY, url);
    }

    public static DisplayImageOptions getCircleDisplayImageOptions(int placeholderImageRes, ImageScaleType type, String url) {
        return getDisplayImageOptions(placeholderImageRes, type, new CircleBitmapDisplayer(), url);
    }

    //获取默认的图片显示选项
    public static DisplayImageOptions getDisplayImageOptions(int placeholderImageRes, ImageScaleType type,
                                                             BitmapDisplayer displayer, String url){

        //判断是否是本地图片
        boolean localUrl = StringUtil.hasPrefix(url, "file://");

        DisplayImageOptions options = new DisplayImageOptions.Builder()

                .showImageOnLoading(placeholderImageRes)
                .showImageForEmptyUri(placeholderImageRes)
                .showImageOnFail(placeholderImageRes)
                .resetViewBeforeLoading(true)
                .delayBeforeLoading(100)
                .cacheInMemory(true)
                .cacheOnDisk(!localUrl)
                .imageScaleType(type)
                .displayer(displayer)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        return options;
    }

    //圆形图片显示器
    public static class CircleBitmapDisplayer implements BitmapDisplayer {

        @Override
        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
            if (!(imageAware instanceof ImageViewAware)) {
                throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
            }

            CornerBorderDrawable drawable = new CornerBorderDrawable(bitmap);
            drawable.setShouldAbsoluteCircle(true);
            imageAware.setImageDrawable(drawable);
        }
    }
}
