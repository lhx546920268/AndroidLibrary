package com.lhx.library.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;


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

    public static String getFileExtensionFromMimeType(@NonNull String mimeType){

    }

    public static boolean moveFile(@NonNull String filePath, @NonNull String destFilePath){
        return moveFile(new File(filePath), new File(destFilePath));
    }

    public static boolean moveFile(@NonNull File file, @NonNull String destFilePath){
        return moveFile(file, destFilePath);
    }

    public static boolean moveFile(@NonNull String filePath, @NonNull File destFile){
        return moveFile(new File(filePath), destFile);
    }

    /**
     * 移动文件
     * @param file 要移动的文件
     * @param destFile 目的地
     * @return 是否成功
     */
    public static boolean moveFile(@NonNull File file, @NonNull File destFile){
        if(!file.exists() || !file.isFile() || destFile.isDirectory())
            return false;

        try {
            if(!destFile.exists())
                destFile.mkdirs();

            return file.renameTo(destFile);
        }catch (SecurityException e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFile(@NonNull String filePath){
        return deleteFile(new File(filePath));
    }

    /**
     * 删除文件
     * @param file 要删除的文件
     * @return 是否成功
     */
    public static boolean deleteFile(@NonNull File file){
        if(!file.exists())
            return false;

        try {
            return file.delete();
        }catch (SecurityException e){
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] readFile(@NonNull String filePath){
        return readFile(new File(filePath));
    }

    /**
     * 读取文件
     * @param file 要读取的文件
     * @return 文件数据
     */
    public static byte[] readFile(@NonNull File file){

        if(file.exists()){
            try {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                byte[] bytes = new byte[1024 * 256];
                int len = 0;
                while ((len = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }

                return outputStream.toByteArray();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 获取一个独一无二的文件名称
     * @return 文件名称
     */
    public static String getUniqueFileName(){
        return System.currentTimeMillis() + StringUtil.getRandomNumber(10);
    }

    /**
     * 创建一个临时文件
     * @param context 上下文
     * @param fileType 文件类型
     * @return 临时文件
     */
    public static String getTemporaryFilePath(@NonNull Context context, String fileType){
        File file = context.getExternalCacheDir();
        if(file == null){
            file = context.getCacheDir();
        }
        return file.getAbsolutePath() + "/temp/" + getUniqueFileName() + "." + fileType;
    }
}
