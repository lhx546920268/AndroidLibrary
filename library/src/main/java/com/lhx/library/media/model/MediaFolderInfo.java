package com.lhx.library.media.model;

import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * 媒体文件夹信息
 */

public class MediaFolderInfo {

    //文件夹唯一标识
    public String bucketId;

    //名称
    public String name;

    //图片数量
    public int totalCount;

    //第一张缩率图
    public String thumbnailPath;


    //解析图片数据
    public void parseImageData(Cursor data){

        bucketId = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
        name = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
    }
}
