package com.lhx.library.media.model;

import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

/**
 * 媒体数据信息
 */

public class MediaInfo {

    //信息类型
    public static final int MEDIA_TYPE_IMAGE = 0; //图片
    public static final int MEDIA_TYPE_VIDEO = 1; //视频

    //路径
    public String path;

    //名称
    public String name;

    //类型
    public int type;

    //大小
    public long size;

    //宽度和高度要 api 16以上才有

    //宽度
    public int width;

    //高度
    public int height;


    //解析 图片数据
    public void parseImageData(Cursor data){
        path = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        name = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
        type = MEDIA_TYPE_IMAGE;
        size = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            width = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
            height = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
        }
    }
}
