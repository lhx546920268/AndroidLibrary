package com.lhx.library.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


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

        int index = filePath.lastIndexOf("/");
        String fileName = filePath;
        String extension = "";
        //通过文件名称获取 拓展名，防止文件路径里面有 .
        if(index != -1){
            fileName = filePath.substring(index);
        }

        int extensionIndex = fileName.lastIndexOf(".");
        if(extensionIndex != -1 && extensionIndex + 1 < fileName.length()){
            extension = fileName.substring(extensionIndex + 1);
        }

        if(!StringUtil.isEmpty(extension)){
            MimeTypeMap map = MimeTypeMap.getSingleton();
            return map.getMimeTypeFromExtension(extension);
        }

        return "";
    }

    /**
     * 通过 mimeType 获取文件扩展名称
     * @param mimeType 文件的mimeType
     * @return 文件拓展名
     */
    public static String getFileExtensionFromMimeType(@NonNull String mimeType){

        MimeTypeMap map = MimeTypeMap.getSingleton();
        String extension = map.getExtensionFromMimeType(mimeType);

        if(extension == null){
            extension = "";
        }else{
            int pointIndex = extension.indexOf(".");
            if(pointIndex < 0){
                extension = "." + extension;
            }
        }

        return extension;
    }

    /**
     * 为文件路径添加拓展名
     * @param filePath 文件路径
     * @param extension 拓展名
     * @param replace 如果已存在拓展名，是否替换
     * @return 新的文件路径
     */
    public static String appendFileExtension(@NonNull String filePath, String extension, boolean replace){
        if(!TextUtils.isEmpty(extension)){
            int index = filePath.lastIndexOf("/");

            //通过文件名称获取 拓展名，防止文件路径里面有 .
            if(index != -1){
                String fileName = filePath.substring(index);
                int extensionIndex = fileName.lastIndexOf(".");
                if(extensionIndex != -1){
                    if(replace){
                        return filePath.substring(0, index + extensionIndex) + extension;
                    }
                }else {
                    return filePath + extension;
                }
            }else {
                int extensionIndex = filePath.lastIndexOf(".");
                if(extensionIndex != -1){
                    if(replace){
                        return filePath.substring(0, extensionIndex) + extension;
                    }
                }else {
                    return filePath + extension;
                }
            }
        }

        return filePath;
    }

    public static boolean moveFile(@NonNull String filePath, @NonNull String destFilePath){
        return moveFile(new File(filePath), new File(destFilePath));
    }

    public static boolean moveFile(@NonNull File file, @NonNull String destFilePath){
        return moveFile(file, new File(destFilePath));
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

            createDirectoryIfNotEixist(destFile);
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
     * 获取临时文件夹
     * @param context 上下文
     * @return 临时文件夹
     */
    public static String getTemporaryDirectory(@NonNull Context context){
        File file = context.getExternalCacheDir();
        if(file == null){
            file = context.getCacheDir();
        }
        return file.getAbsolutePath() + "/temp";
    }


    /**
     * 获取一个临时文件名称
     * @param context 上下文
     * @param extension 文件类型 包含 .
     * @return 临时文件
     */
    public static String getTemporaryFilePath(@NonNull Context context, String extension){

        String path = getTemporaryDirectory(context) + "/" + getUniqueFileName();
        if(!TextUtils.isEmpty(extension)){
            path += extension;
        }

        return path;
    }

    public static boolean createNewFileIfNotExist(@NonNull String filePath){
        return createNewFileIfNotExist(new File(filePath));
    }

    /**
     * 创建一个文件，如果文件的上级目录不存在，会创建，防止创建文件失败
     * @param file 文件路径
     * @return 是否成功， 如果文件存在，也返回成功
     */
    public static boolean createNewFileIfNotExist(@NonNull File file){
        if(file.exists() && file.isFile())
            return true;
        try {
            String parent = file.getParent();

            //创建文件夹
            File directory = new File(parent);
            if(!directory.exists() || !directory.isDirectory()){
                if(!directory.mkdirs()){
                    return false;
                }
            }

            return file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean createDirectoryIfNotEixist(@NonNull String filePath){
        return createDirectoryIfNotEixist(new File(filePath));
    }

    /**
     * 创建文件路径 中的文件夹
     * @param file 必须是一个文件路径 而不是文件夹路径
     * @return 是否成功 如果文件夹存在，也返回成功
     */
    public static boolean createDirectoryIfNotEixist(@NonNull File file){
        String parent = file.getParent();

        //创建文件夹
        File directory = new File(parent);
        if(!directory.exists() || !directory.isDirectory()){
            return directory.mkdirs();
        }

        return true;
    }

    /**
     * 通过uri 获取文件路径
     * @param uri uri
     * @return 成功则返回文件路径，否则返回null
     */
    public static String filePathFromUri(Context context, Uri uri){
        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Video.VideoColumns.DATA},
                null, null, null);
        String scheme = uri.getScheme();
        if(scheme == null){

            return uri.getPath();
        }else if(ContentResolver.SCHEME_FILE.equals(scheme)){

            return uri.getPath();
        }else if(ContentResolver.SCHEME_CONTENT.equals(scheme)){

            if(cursor != null){
                if(cursor.moveToFirst()){
                    int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                    if(index > -1){
                        return cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }

        return null;
    }
}
