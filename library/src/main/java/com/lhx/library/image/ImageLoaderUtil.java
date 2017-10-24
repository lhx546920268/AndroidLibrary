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
import android.view.View;
import android.widget.ImageView;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.util.FileUtil;
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

    //单例
    public synchronized static ImageLoaderUtil getInstance(){
        if(mImageLoaderUtil == null){
            mImageLoaderUtil = new ImageLoaderUtil();
        }

        return mImageLoaderUtil;
    }

    //初始化
    public static void initialize(Context context){

        mPlaceholderColor = App.ImagePlaceHolderColor;

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 1);
        // config.denyCacheImageMultipleSizesInMemory();
        // config.memoryCache(new WeakMemoryCache());
        config.threadPoolSize(3);
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.imageDownloader(new BaseImageDownloader(context));
        config.imageDecoder(new BaseImageDecoder(false));
        config.defaultDisplayImageOptions(DisplayImageOptions.createSimple());
        // config.diskCacheSize(800 * 1024 * 1024);
        // config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//        config.writeDebugLogs(); // Remove for release app

        File file = new File(FileUtil.getImageCacheFolder(context));
        config.diskCache(new UnlimitedDiscCache(file)).diskCacheFileCount(800).diskCacheSize(800 *
                1024 * 1024);

        // Initialize ImageLoader with configuration.
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
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                    imageView.setBackgroundDrawable(null);
                }else {
                    imageView.setBackground(null);
                }
            }else if(tag instanceof Drawable){
                Drawable drawable = (Drawable)tag;
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                    imageView.setBackgroundDrawable(drawable);
                }else {
                    imageView.setBackground(drawable);
                }
            }
        }
    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }

    public static void configure(ImageView imageView){
        Object tag = imageView.getTag(R.id.tag_scale_type);
        if(tag instanceof ImageView.ScaleType){

        }else {
            imageView.setTag(R.id.tag_scale_type, imageView.getScaleType());
            imageView.setTag(R.id.tag_background, imageView.getBackground());
        }

        imageView.setBackgroundColor(mPlaceholderColor);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    protected static ImageLoader mImageLoader = ImageLoader.getInstance();

    public static void displayCircleImage(ImageView imageView, String uriString) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView, ImageLoaderUtil.getCircleDisplayImageOptions(),
                ImageLoaderUtil.getInstance());
    }

    public static void displayCircleImage(ImageView imageView, String uriString, int defualtRes) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView, ImageLoaderUtil.getCircleDisplayImageOptions(defualtRes),
                ImageLoaderUtil.getInstance());
    }

    public static void displayImage(ImageView imageView, String uriString, int defualtRes) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView,
                ImageLoaderUtil.getDisplayImageOptions(defualtRes, ImageScaleType.EXACTLY), ImageLoaderUtil.getInstance());
    }

    public static void displayImage(ImageView imageView, String uriString, int defualtRes, ImageScaleType scaleType) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView, ImageLoaderUtil.getDisplayImageOptions(defualtRes,
                scaleType), ImageLoaderUtil.getInstance());
    }

    public static void displayRectangleImage(ImageView imageView, String uriString) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView, ImageLoaderUtil.getRectangleDisplayImageOptions(),
                ImageLoaderUtil.getInstance());
    }

    public static void displayRectangleImage(ImageView imageView, String uriString, ImageScaleType scaleType) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView, ImageLoaderUtil.getRectangleDisplayImageOptions(scaleType),
                ImageLoaderUtil.getInstance());
    }

    public static void displayRoundImage(ImageView imageView, String uriString, int radiusPixels) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView,
                ImageLoaderUtil.getRoundDisplayImageOptions(App.ImagePlaceHolder, radiusPixels),
                ImageLoaderUtil.getInstance());
    }

    public static void displaySquareImage(ImageView imageView, String uriString) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView, ImageLoaderUtil.getSquareDisplayImageOptions(),
                ImageLoaderUtil.getInstance());
    }

    public static void displaySquareImage(ImageView imageView, String uriString, ImageScaleType scaleType) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView, ImageLoaderUtil.getSquareDisplayImageOptions(scaleType),
                ImageLoaderUtil.getInstance());
    }

    public static void displaySquarePayImage(ImageView imageView, String uriString, ImageScaleType scaleType) {
        String mOldUrl = (String) imageView.getTag(R.id.tag_first);
        if (mOldUrl != null && mOldUrl.equals(uriString)) {
            return;
        }
        imageView.setTag(R.id.tag_first, uriString);
        ImageLoaderUtil.configure(imageView);
        mImageLoader.displayImage(uriString, imageView, ImageLoaderUtil.getRectangleDisplayImagePayOptions
                (scaleType), ImageLoaderUtil.getInstance() );
    }

    public static DisplayImageOptions getSquareDisplayImageOptions() {
        return getDisplayImageOptions(App.ImagePlaceHolder, ImageScaleType.EXACTLY);
    }

    public static DisplayImageOptions getSquareDisplayImageOptions(ImageScaleType imageScaleType) {
        return getDisplayImageOptions(App.ImagePlaceHolder, imageScaleType);
    }

    public static DisplayImageOptions getRectangleDisplayImageOptions() {
        return getDisplayImageOptions(App.ImagePlaceHolder, ImageScaleType.EXACTLY);
    }

    public static DisplayImageOptions getRectangleDisplayImageOptions(ImageScaleType imageScaleType) {
        return getDisplayImageOptions(App.ImagePlaceHolder, imageScaleType);
    }

    public static DisplayImageOptions getRectangleDisplayImagePayOptions(ImageScaleType imageScaleType) {
        return getDisplayImageOptions(App.ImagePlaceHolder, imageScaleType);
    }

    public static DisplayImageOptions getDisplayImageOptions(int defaultImagRes, ImageScaleType imageScaleType) {
        @SuppressWarnings("deprecation")
        DisplayImageOptions nOptions = new DisplayImageOptions.Builder()// .showStubImage(R.drawable.default_img_rect)
                // //
                // image在加载过程中，显示的图片
                .showImageForEmptyUri(defaultImagRes) // empty URI时显示的图片
                .showImageOnFail(defaultImagRes) // 不是图片文件 显示图片
                .showImageOnLoading(defaultImagRes)
                .resetViewBeforeLoading(false) // default
                .delayBeforeLoading(100).cacheInMemory(true) // default 不缓存至内存
                .cacheOnDisc(true) // default 不缓存至手机SDCard
                // .preProcessor(...)
                // .postProcessor(...)
                // .extraForDownloader(...)
                .imageScaleType(imageScaleType)// default
                .displayer(new FadeInBitmapDisplayer(300, true, true, true)).bitmapConfig(Bitmap.Config.RGB_565) // default
                // .decodingOptions(...)
                // 可以设置动画，比如圆角或者渐变
                .handler(new Handler()) // default
                .build();
        return nOptions;
    }

    public static Bitmap compressThumImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static DisplayImageOptions getCircleDisplayImageOptions(int defaultImagRes) {
        DisplayImageOptions nOptions = new DisplayImageOptions.Builder()// .showStubImage(R.drawable.shop_avatar)
                // //
                .showImageOnLoading(defaultImagRes)														// image在加载过程中，显示的图片
                .showImageForEmptyUri(defaultImagRes) // empty URI时显示的图片
                .showImageOnFail(defaultImagRes) // 不是图片文件 显示图片
                .resetViewBeforeLoading(false) // default
                .delayBeforeLoading(100).cacheInMemory(true) // default 不缓存至内存
                .cacheOnDisc(false) // default 不缓存至手机SDCard
                // .preProcessor(...)
                // .postProcessor(...)
                // .extraForDownloader(...)
                .imageScaleType(ImageScaleType.EXACTLY)// default
                .displayer(new CircleBitmapDisplayer()).bitmapConfig(Bitmap.Config.RGB_565) // default
                // .decodingOptions(...)
                // 可以设置动画，比如圆角或者渐变
                .handler(new Handler()) // default
                .build();
        return nOptions;
    }

    public static DisplayImageOptions getRoundDisplayImageOptions(int defaultImagRes, int radiusPixels) {
        DisplayImageOptions nOptions = new DisplayImageOptions.Builder()// .showStubImage(R.drawable.shop_avatar)
                // //
                .showImageOnLoading(defaultImagRes)														// image在加载过程中，显示的图片
                .showImageForEmptyUri(defaultImagRes) // empty URI时显示的图片
                .showImageOnFail(defaultImagRes) // 不是图片文件 显示图片
                .resetViewBeforeLoading(false) // default
                .delayBeforeLoading(100).cacheInMemory(true) // default 不缓存至内存
                .cacheOnDisc(false) // default 不缓存至手机SDCard
                // .preProcessor(...)
                // .postProcessor(...)
                // .extraForDownloader(...)
                .imageScaleType(ImageScaleType.EXACTLY)// default
                .displayer(new RoundedBitmapDisplayer(radiusPixels)) // default
                // .decodingOptions(...)
                // 可以设置动画，比如圆角或者渐变
                .handler(new Handler()) // default
                .build();
        return nOptions;
    }

    public static DisplayImageOptions getCircleDisplayImageOptions() {
        return getCircleDisplayImageOptions(App.ImagePlaceHolderCircle);
    }

    public static DisplayImageOptions getCommonCircleDisplayImageOptions() {
        DisplayImageOptions nOptions = new DisplayImageOptions.Builder()// .showStubImage(R.drawable.shop_avatar)
                // //
                .showImageOnLoading(App.ImagePlaceHolderCircle)													//
                // image在加载过程中，显示的图片
                .showImageForEmptyUri(App.ImagePlaceHolderCircle) // empty
                // URI时显示的图片
                .showImageOnFail(App.ImagePlaceHolderCircle) // 不是图片文件 显示图片
                .resetViewBeforeLoading(false) // default
                .delayBeforeLoading(100).cacheInMemory(false) // default 不缓存至内存
                .cacheOnDisc(false) // default 不缓存至手机SDCard
                // .preProcessor(...)
                // .postProcessor(...)
                // .extraForDownloader(...)
                .imageScaleType(ImageScaleType.EXACTLY)// default
                .displayer(new CircleBitmapDisplayer()).bitmapConfig(Bitmap.Config.RGB_565) // default
                // .decodingOptions(...)
                // 可以设置动画，比如圆角或者渐变
                .handler(new Handler()) // default
                .build();
        return nOptions;
    }
    
    

    public static class CircleBitmapDisplayer implements BitmapDisplayer {

        protected  final int margin ;

        public CircleBitmapDisplayer() {
            this(0);
        }

        public CircleBitmapDisplayer(int margin) {
            this.margin = margin;
        }

        @Override
        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
            if (!(imageAware instanceof ImageViewAware)) {
                throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
            }

            imageAware.setImageDrawable(new CircleDrawable(bitmap, margin));
        }


    }

    public static class CircleDrawable extends Drawable {
        public static final String TAG = "CircleDrawable";

        protected final Paint paint;

        protected final int margin;
        protected final BitmapShader bitmapShader;
        protected float radius;
        protected Bitmap oBitmap;//原图
        public CircleDrawable(Bitmap bitmap){
            this(bitmap,0);
        }

        public CircleDrawable(Bitmap bitmap, int margin) {
            this.margin = margin;
            this.oBitmap = bitmap;
            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            computeBitmapShaderSize();
            computeRadius();

        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();//画一个圆圈
            canvas.drawCircle(bounds.width() / 2F,bounds.height() / 2F,radius,paint);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
        }


        /**
         * 计算Bitmap shader 大小
         */
        public void computeBitmapShaderSize(){
            Rect bounds = getBounds();
            if(bounds == null) return;
            //选择缩放比较多的缩放，这样图片就不会有图片拉伸失衡
            Matrix matrix = new Matrix();
            float scaleX = bounds.width() / (float)oBitmap.getWidth();
            float scaleY = bounds.height() / (float)oBitmap.getHeight();
            float scale = scaleX > scaleY ? scaleX : scaleY;
            matrix.postScale(scale,scale);
            bitmapShader.setLocalMatrix(matrix);
        }

        /**
         * 计算半径的大小
         */
        public void computeRadius(){
            Rect bounds = getBounds();
            radius = bounds.width() < bounds.height() ?
                    bounds.width() /2F - margin:
                    bounds.height() / 2F - margin;
        }
    }
}
