package com.lhx.library.media.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.library.R;
import com.lhx.library.fragment.RecyclerViewFragment;
import com.lhx.library.media.model.MediaFolderInfo;
import com.lhx.library.media.model.MediaInfo;
import com.lhx.library.media.widget.MediaCheckBox;
import com.lhx.library.media.widget.MediaItem;
import com.lhx.library.media.MediaLoader;
import com.lhx.library.recyclerView.RecyclerViewGridAdapter;
import com.lhx.library.section.EdgeInsets;
import com.lhx.library.viewHoler.RecyclerViewHolder;

import java.util.ArrayList;

/**
 * 媒体 相册 图片
 */

public class MediaFragment extends RecyclerViewFragment implements MediaLoader.MediaLoaderHandler {

    //多选数量
    public static final String MULTI_COUNT = "com.lhx.MULTI_COUNT";

    //是否是单选
    public static final String SINGLE_SELECTION = "com.lhx.SINGLE_SELECTION";

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

    //单选
    private boolean mSingleSelection = false;

    //图片信息
    private ArrayList<MediaInfo> mMediaInfos = new ArrayList<>();

    //加载器
    MediaLoader mMediaLoader;

    //
    MediaAdapter mMediaAdapter;

    //选中的图片
    private ArrayList<MediaInfo> mSelectedMediaInfos = new ArrayList<>();

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        super.initialize(inflater, container, saveInstanceState);

        mRecyclerView.setBackgroundColor(getColor(R.color.fragment_gray_background));
        mMultiCount = getExtraIntFromBundle(MULTI_COUNT, 1);
        mSingleSelection = getExtraBooleanFromBundle(SINGLE_SELECTION, false);
        mMediaType = getExtraIntFromBundle(MEDIA_TYPE, MediaInfo.MEDIA_TYPE_IMAGE);
        mCountPerRow = getResources().getInteger(R.integer.media_count_per_row);
        mItemSpacing = getDimensionPixelSize(R.dimen.media_item_spacing);
        mRecyclerView.setPadding(mItemSpacing, 0, mItemSpacing, 0);

        mMediaLoader = new MediaLoader(mContext);
        mMediaLoader.setMediaLoaderHandler(this);

        setShowBackButton(true);

        loadMedia();
    }

    //加载媒体信息
    private void loadMedia(){
        setPageLoading(true);
        mMediaLoader.loadImages(getLoaderManager(), null);
    }

    @Override
    public void onLoadImages(ArrayList<MediaInfo> infos) {

        //图片加载完成
        setPageLoading(false);
        mMediaInfos.clear();
        mMediaInfos.addAll(infos);
        if(mMediaAdapter == null){
            mMediaAdapter = new MediaAdapter(mRecyclerView);
            mRecyclerView.setAdapter(mMediaAdapter);
        }else {
            mMediaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadFolders(ArrayList<MediaFolderInfo> folderInfos) {

    }

    private class MediaAdapter extends RecyclerViewGridAdapter{

        public MediaAdapter(@NonNull RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public RecyclerViewHolder onCreateViewHolderForViewType(int viewType, ViewGroup parent) {

            MediaItem mediaItem = new MediaItem(mContext);
            mediaItem.getCheckBox().setVisibility(mSingleSelection ? View.GONE : View.VISIBLE);
            final RecyclerViewHolder holder = new RecyclerViewHolder(mediaItem);

            mediaItem.getCheckBox().setOnCheckedChangeListener(new MediaCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(MediaCheckBox checkBox, boolean isChecked) {
                    //选中图片
                    int position = holder.getAdapterPosition();
                    MediaInfo mediaInfo = mMediaInfos.get(position);
                    if(!mSelectedMediaInfos.contains(mediaInfo)){
                        mSelectedMediaInfos.add(mediaInfo);
                    }else {
                        mSelectedMediaInfos.remove(mediaInfo);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public String getCheckedText(MediaCheckBox checkBox) {
                    int position = holder.getLayoutPosition();
                    MediaInfo mediaInfo = mMediaInfos.get(position);
                    return String.valueOf(mSelectedMediaInfos.indexOf(mediaInfo) + 1);
                }
            });

            return holder;
        }

        @Override
        public int numberOfItemInSection(int section) {
            return mMediaInfos.size();
        }

        @Override
        public void onBindItemViewHolderForIndexPath(RecyclerViewHolder viewHolder, int indexInSection, int section) {

            MediaItem mediaItem = (MediaItem)viewHolder.itemView;

            MediaInfo mediaInfo = mMediaInfos.get(indexInSection);
            mediaItem.setMediaInfo(mediaInfo);
            if(!mSingleSelection){
                mediaItem.setChecked(mSelectedMediaInfos.contains(mediaInfo));
            }
        }

        @Override
        public int numberOfColumnInSection(int section) {
            return mCountPerRow;
        }

        @Override
        public int getItemSpaceAtSection(int section) {
            return MediaFragment.this.mItemSpacing;
        }

        @Override
        public EdgeInsets getSectionInsetsAtSection(int section) {
            int space = MediaFragment.this.mItemSpacing;
            return new EdgeInsets(0, space, 0, space);
        }

        @Override
        public int getDifferentColumnProduct() {
            return mCountPerRow;
        }

        @Override
        public void onItemClick(int indexInSection, int section, View view) {

            if(mSingleSelection){

            }else {

            }
        }
    }
}
