package com.lhx.library.media;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;

/**
 * 媒体数据加载
 */

public class MediaLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    //加载图片 loader id
    private static final int LOAD_IMAGE_ID = 1;

    private Context mContext;

    //图片加载器
    private CursorLoader mImageLoader;

    //回调
    private MediaLoaderHandler mMediaLoaderHandler;

    public MediaLoader(Context context) {
        mContext = context;
    }

    public void setMediaLoaderHandler(MediaLoaderHandler mediaLoaderHandler) {
        mMediaLoaderHandler = mediaLoaderHandler;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return getImageLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        ArrayList<MediaInfo> infos = null;
        ArrayList<MediaFolderInfo> folderInfos = null;

        switch (id){
            case LOAD_IMAGE_ID : {

                CursorLoader cursorLoader = (CursorLoader)loader;
                boolean isAll = cursorLoader.getSelection() != null && cursorLoader.getSelectionArgs() != null;
                if(data != null){

                    int count = data.getCount();
                    infos = new ArrayList<>(count);

                    if(count > 0){
                        if(isAll){
                            folderInfos = new ArrayList<>();
                        }
                        data.moveToFirst();
                        do{

                            MediaInfo info = new MediaInfo();
                            info.parseImageData(data);
                            infos.add(info);

                            if(isAll){
                                //获取文件夹信息
                                String bucketId = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media
                                        .BUCKET_ID));
                                MediaFolderInfo folderInfo = getMediaFolderInfo(bucketId, folderInfos);
                                if(folderInfo != null){
                                    folderInfo.totalCount ++;
                                }else {
                                    folderInfo = new MediaFolderInfo();
                                    folderInfo.parseImageData(data);
                                    folderInfo.totalCount = 1;
                                    folderInfo.thumbnailPath = info.path;
                                    folderInfos.add(folderInfo);
                                }
                            }

                        }while (data.moveToNext());
                    }

                }else {
                    infos = new ArrayList<>();
                }
                if(mMediaLoaderHandler != null){
                    mMediaLoaderHandler.onLoadImages(infos);
                    if(folderInfos != null){
                        mMediaLoaderHandler.onLoadFolders(folderInfos);
                    }
                }
            }
            break;
        }
    }

    //获取存在的文件夹信息
    private MediaFolderInfo getMediaFolderInfo(String bucketId, ArrayList<MediaFolderInfo> infos){

        if(infos.size() > 0){
            for(MediaFolderInfo info : infos){
                if(bucketId.equals(info.bucketId)){
                    return info;
                }
            }
        }
        return null;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //获取图片 loader
    private CursorLoader getImageLoader(){
        if(mImageLoader == null){
            CursorLoader loader = new CursorLoader(mContext);

            ArrayList<String> projections = new ArrayList<>();
            projections.add(MediaStore.Images.Media.DATA);
            projections.add(MediaStore.Images.Media.SIZE);
            projections.add(MediaStore.Images.Media.DISPLAY_NAME);
            projections.add(MediaStore.Images.Media.BUCKET_ID);
            projections.add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                projections.add(MediaStore.Images.Media.WIDTH);
                projections.add(MediaStore.Images.Media.HEIGHT);
            }

            loader.setProjection(projections.toArray(new String[]{}));
            loader.setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            //时间倒序
            mImageLoader.setSortOrder(MediaStore.Images.Media.DATE_ADDED + "desc");
            mImageLoader = loader;
        }
        return mImageLoader;
    }

    //加载图片
    private void loadImages(LoaderManager loaderManager, String bucketId){

        getImageLoader();

        if(bucketId != null){
            mImageLoader.setSelection(bucketId + "=?");
            mImageLoader.setSelectionArgs(new String[]{bucketId});
        }else {
            mImageLoader.setSelection(null);
            mImageLoader.setSelectionArgs(null);
        }
        loaderManager.initLoader(LOAD_IMAGE_ID, null, this);
    }

    //销毁loader
    public void destroy(LoaderManager loaderManager){
        loaderManager.destroyLoader(LOAD_IMAGE_ID);
        mImageLoader = null;
    }

    //加载回调
    public interface MediaLoaderHandler{

        //图片加载完
        void onLoadImages(ArrayList<MediaInfo> infos);

        //文件夹信息加载完成
        void onLoadFolders(ArrayList<MediaFolderInfo> folderInfos);
    }
}
