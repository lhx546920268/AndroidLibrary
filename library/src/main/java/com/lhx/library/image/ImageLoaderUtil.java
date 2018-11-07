package com.lhx.library.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.util.FileUtil;
import com.lhx.library.util.StringUtil;
import com.lhx.library.util.ThreadUtil;
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
        if(Color.alpha(mPlaceholderColor) == 0){
            mPlaceholderColor = 0;
        }

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

            if(mPlaceholderColor != 0){
                tag = imageView.getTag(R.id.tag_background);
                if(tag == null){
                    ViewUtil.setBackground(null, imageView);
                }else if(tag instanceof Drawable){
                    ViewUtil.setBackground((Drawable)tag, imageView);
                }
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

        if(!round && mPlaceholderColor != 0){
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

    //显示部分圆角图片 px
    public static void displayPartialRoundImage(ImageView imageView, String url, PartialRound partialRound){

        displayPartialRoundImage(imageView, url, partialRound, App.ImagePlaceHolder);
    }

    public static void displayPartialRoundImage(ImageView imageView, String url, PartialRound partialRound, int
            placeholderImageRes){
        displayPartialRoundImage(imageView, url, partialRound, placeholderImageRes, ImageScaleType.EXACTLY);
    }

    public static void displayPartialRoundImage(ImageView imageView, String url, PartialRound partialRound, ImageScaleType imageScaleType){
        displayPartialRoundImage(imageView, url, partialRound, App.ImagePlaceHolder, imageScaleType);
    }

    public static void displayPartialRoundImage(ImageView imageView, String url, PartialRound partialRound, int
            placeholderImageRes, ImageScaleType imageScaleType){

        displayImage(imageView, url, getPartialRoundDisplayImageOptions(placeholderImageRes, imageScaleType,
                partialRound, url), false);
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

    //部分圆角图片选项

    public static DisplayImageOptions getPartialRoundDisplayImageOptions(PartialRound partialRound, String url) {
        return getPartialRoundDisplayImageOptions(App.ImagePlaceHolder, partialRound, url);
    }

    public static DisplayImageOptions getPartialRoundDisplayImageOptions(ImageScaleType
            type, PartialRound partialRound, String url) {
        return getPartialRoundDisplayImageOptions(App.ImagePlaceHolder, type, partialRound, url);
    }

    public static DisplayImageOptions getPartialRoundDisplayImageOptions(int placeholderImageRes, PartialRound partialRound, String url) {
        return getPartialRoundDisplayImageOptions(placeholderImageRes, ImageScaleType.EXACTLY, partialRound, url);
    }

    public static DisplayImageOptions getPartialRoundDisplayImageOptions(int placeholderImageRes, ImageScaleType
            type, PartialRound partialRound, String url) {

        return getDisplayImageOptions(placeholderImageRes, type, new PartialRoundBitmapDisplayer(partialRound), url);
    }

    //获取默认的图片显示选项
    public static DisplayImageOptions getDisplayImageOptions(int placeholderImageRes, ImageScaleType type,
                                                             BitmapDisplayer displayer, String url){

        return getDisplayImageOptionsBuilder(placeholderImageRes, type, displayer, url).build();
    }

    public static DisplayImageOptions.Builder getDisplayImageOptionsBuilder(String url){
        return getDisplayImageOptionsBuilder(App.ImagePlaceHolder, ImageScaleType.EXACTLY, new FadeInBitmapDisplayer(200, true, false, false)
                , url);
    }

    public static DisplayImageOptions.Builder getDisplayImageOptionsBuilder(int placeholderImageRes, ImageScaleType type,
                                                                     BitmapDisplayer displayer, String url){
        //判断是否是本地图片
        boolean localUrl = StringUtil.hasPrefix(url, "file://");

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()

                .showImageOnLoading(placeholderImageRes)
                .showImageForEmptyUri(placeholderImageRes)
                .showImageOnFail(placeholderImageRes)
                .resetViewBeforeLoading(true)
                .delayBeforeLoading(100)
                .cacheInMemory(true)
                .cacheOnDisk(!localUrl)
                .imageScaleType(type)
                .displayer(displayer)
                .bitmapConfig(Bitmap.Config.RGB_565);

        return builder;
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

    //圆角
    public static class PartialRound{

        //圆角
        private int mLeftTopRadius;
        private int mRightTopRadius;
        private int mLeftBottomRadius;
        private int mRightBottomRadius;

        public PartialRound(int leftTopRadius, int rightTopRadius, int leftBottomRadius, int rightBottomRadius) {
            mLeftTopRadius = leftTopRadius;
            mRightTopRadius = rightTopRadius;
            mLeftBottomRadius = leftBottomRadius;
            mRightBottomRadius = rightBottomRadius;
        }

        public int getLeftTopRadius() {
            return mLeftTopRadius;
        }

        public int getRightTopRadius() {
            return mRightTopRadius;
        }

        public int getLeftBottomRadius() {
            return mLeftBottomRadius;
        }

        public int getRightBottomRadius() {
            return mRightBottomRadius;
        }
    }

    //部分圆角显示器
    public static class PartialRoundBitmapDisplayer implements BitmapDisplayer {

        //部分圆角
        private PartialRound mPartialRound;

        public PartialRoundBitmapDisplayer(PartialRound partialRound) {
            mPartialRound = partialRound;
        }

        @Override
        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
            if (!(imageAware instanceof ImageViewAware)) {
                throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
            }

            CornerBorderDrawable drawable = new CornerBorderDrawable(bitmap);
            drawable.setLeftTopCornerRadius(mPartialRound.getLeftTopRadius());
            drawable.setRightTopCornerRadius(mPartialRound.getRightTopRadius());
            drawable.setLeftBottomCornerRadius(mPartialRound.getLeftBottomRadius());
            drawable.setRightBottomCornerRadius(mPartialRound.getRightBottomRadius());

            imageAware.setImageDrawable(drawable);
        }
    }

    //获取缓存大小
    public static void getImageCacheSize(final Context context, final CalculateImageCacheHandler handler){

        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(FileUtil.getImageCacheFolder(context));
                final long size = FileUtil.getFileSize(file);

                if(handler != null){
                    ThreadUtil.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.onCalculateImageCache(size);
                        }
                    });
                }
            }
        }).start();
    }

    //清除图片缓存
    public static void clearImageCache(final Context context, final ClearImageCacheHandler handler){

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(FileUtil.getImageCacheFolder(context));
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {

                        for (File f : files){
                            f.delete();
                        }
                    }
                }
                if(handler != null){
                    ThreadUtil.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.onClearImageCache();
                        }
                    });
                }
            }
        }).start();
    }

    //计算缓存完成回调
    public interface CalculateImageCacheHandler{

        //计算完成 字节
        void onCalculateImageCache(long size);
    }

    //清除缓存大小回调
    public interface ClearImageCacheHandler{

        //清除完成回调
        void onClearImageCache();
    }
}
