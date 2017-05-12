package com.lhx.library.util;

import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

/**
 * 文件操作
 */

public class FileUtil {

    /**
     * 获取文件的 mimetype
     * @param filePath 文件路径
     * @return mimetype
     */
    public static String getMimeType(@NonNull String filePath){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        String mine = "";
        try {
            retriever.setDataSource(filePath);
            mine = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        return mine;
    }
}
