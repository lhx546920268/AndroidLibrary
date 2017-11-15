package com.lhx.library.media;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lhx.library.R;
import com.lhx.library.fragment.RecyclerViewFragment;
import com.lhx.library.recyclerView.RecyclerViewGridAdapter;
import com.lhx.library.viewHoler.RecyclerViewHolder;

import java.util.ArrayList;

/**
 * 媒体 相册 图片
 */

public class MediaFragment extends RecyclerViewFragment implements MediaLoader.MediaLoaderHandler {

    //多选数量
    public static final String MULTI_COUNT = "com.lhx.MULTI_COUNT";

    /**
     * 类型 {@link MediaInfo#MEDIA_TYPE_IMAGE}
     */
    public static final String MEDIA_TYPE = "com.lhx.MEDIA_TYPE";


    //多选数量
    private int mMultiCount;

    //类型
    private int mMediaType;

    //每行数量
    private int mCountPerRow;

    //item间隔
    private int mItemSpacing;

    //图片信息
    private ArrayList<MediaInfo> mMediaInfos = new ArrayList<>();

    //加载器
    MediaLoader mMediaLoader;

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        super.initialize(inflater, container, saveInstanceState);

        mMultiCount = getExtraIntFromBundle(MULTI_COUNT, 1);
        mMediaType = getExtraIntFromBundle(MEDIA_TYPE, MediaInfo.MEDIA_TYPE_IMAGE);
        mCountPerRow = getResources().getInteger(R.integer.media_count_per_row);
        mItemSpacing = getDimensionPixelSize(R.dimen.media_item_spacing);

        mMediaLoader = new MediaLoader(mContext);
        mMediaLoader.setMediaLoaderHandler(this);
    }

    @Override
    public void onLoadImages(ArrayList<MediaInfo> infos) {

    }

    @Override
    public void onLoadFolders(ArrayList<MediaFolderInfo> folderInfos) {

    }

    private class MediaAdapter extends RecyclerViewGridAdapter{

        public MediaAdapter(@Orientation int orientation, RecyclerView recyclerView) {
            super(orientation, recyclerView);
        }

        @Override
        public RecyclerViewHolder onCreateViewHolderForViewType(int viewType, ViewGroup parent) {
            return null;
        }

        @Override
        public int numberOfItemInSection(int section) {
            return 0;
        }

        @Override
        public void onBindItemViewHolderForIndexPath(RecyclerViewHolder viewHolder, int indexInSection, int section) {

        }

        @Override
        public int numberOfColumnInSection(int section) {
            return 0;
        }

        @Override
        public int getDifferentColumnProduct() {
            return 0;
        }
    }
}
