package com.lhx.library.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

/**
 * 图片工具类
 */

public class ImageUtil {

    /**
     * 根据给定宽高调整图片大小，如果图片小于给定宽高，将返回原图，此方法返回的图片大小不一定 和 width height 一致
     * @param imagePath 图片路径
     * @param width 想要的图片宽度
     * @param height 想要的图片高度
     * @return 调整后的图片
     */
    public static Bitmap adjustImage(String imagePath, int width, int height){
        return BitmapFactory.decodeFile(imagePath, getOptions(imagePath, width, height));
    }

    public static BitmapFactory.Options getOptions(String imagePath, int width, int height){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置只获取图片大小，不用把图片加载到内存
        BitmapFactory.decodeFile(imagePath, options);

        return getOptions(options, width, height);
    }

    public static BitmapFactory.Options getOptions(byte[] bytes, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置只获取图片大小，不用把图片加载到内存
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        return getOptions(options, width, height);
    }

    public static BitmapFactory.Options getOptions(BitmapFactory.Options options, int width, int height){
        int imgWidth = options.outWidth;
        int imgHeight = options.outHeight;

        options.inJustDecodeBounds = false;

        if(imgWidth < width && imgHeight < height)
            return options;

        options.inSampleSize = Math.max(imgWidth / width, imgHeight / height);

        return options;
    }
}
