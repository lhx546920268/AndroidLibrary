package com.lhx.library.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    /**把图片保存在本地 异步保存，主线程回调
     * @param context  xx
     * @param bitmap 要保存的图片 可通过 getDrawingCache 获取
     * @param toSystemAlbum 是否要保存到系统相册
     * @param onSaveBitmapHandler 保存完成回调
     */
    public static void saveBitmap(final Context context, final Bitmap bitmap, final boolean toSystemAlbum,
                                 final OnSaveBitmapHandler onSaveBitmapHandler) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // 首先保存图片
                FileOutputStream fos = null;
                boolean success = false;
                try {

                    File file = FileUtil.createTemporaryFile(context, ".jpg");
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();

                    if(toSystemAlbum){
                        // 其次把文件插入到系统图库
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(),
                                null);
                        // 最后通知图库更新
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    }

                    success = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {

                    final boolean succ = success;
                    if(onSaveBitmapHandler != null){
                        ThreadUtil.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                onSaveBitmapHandler.onComplete(succ);
                            }
                        });
                    }
                    if(fos != null){
                        try {
                            fos.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    ///保存图片回调
    public interface OnSaveBitmapHandler{

        //保存完成
        void onComplete(boolean success);
    }
}
