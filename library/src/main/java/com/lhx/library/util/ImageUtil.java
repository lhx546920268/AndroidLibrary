package com.lhx.library.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 图片工具类
 */

public class ImageUtil {

    /**
     * 根据给定宽高调整图片大小，如果图片小于给定宽高，将返回原图
     * @param width 想要的图片宽度
     * @param height 想要的图片高度
     * @return 调整后的图片
     */
    public static Bitmap adjustImage(int width, int height){

    }

    public static BitmapFactory.Options getOptions(String imagePath, int width, int height){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置值获取图片大小，不用把图片加载到内存
        BitmapFactory.decodeFile(imagePath, options);

        int imgWidth = options.outWidth;
        int imgHeight = options.outHeight;


    }
}
