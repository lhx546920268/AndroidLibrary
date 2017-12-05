package com.lhx.library.media.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lhx.library.R;
import com.lhx.library.image.ImageLoaderUtil;
import com.lhx.library.media.model.MediaInfo;
import com.lhx.library.util.ColorUtil;

/**
 * 媒体 item
 */

public class MediaItem extends ConstraintLayout {

    //图片
    private ImageView mImageView;

    //覆盖物
    private View mOverlay;

    //选择框
    private MediaCheckBox mCheckBox;

    //
    private boolean mChecked;

    //容器
    FrameLayout mContainer;

    public MediaItem(@NonNull Context context) {
        this(context, null);
    }

    public MediaItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaItem(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.media_item, this);

        mContainer = (FrameLayout)findViewById(R.id.container);
        mImageView = (ImageView)findViewById(R.id.img);

        LayoutParams params = (LayoutParams) mContainer.getLayoutParams();
        params.leftToLeft = LayoutParams.PARENT_ID;
        params.topToTop = LayoutParams.PARENT_ID;
        params.rightToRight = LayoutParams.PARENT_ID;
        params.width = 0;
        params.height = 0;
        params.dimensionRatio = "1:1";

        mCheckBox = (MediaCheckBox) findViewById(R.id.checkbox);
    }

    //设置媒体信息
    public void setMediaInfo(MediaInfo info){

        ImageLoaderUtil.displayImage(mImageView, "file://" + info.path);
    }

    //设置是否选中
    public void setChecked(boolean checked){
        if(mChecked != checked){
            mChecked = checked;

            if(mOverlay == null){
                mOverlay = new View(getContext());
                mOverlay.setBackgroundColor(ColorUtil.whitePercentColor(0, 0.2f));
                mContainer.addView(mOverlay, 1, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }

            mOverlay.setVisibility(mChecked ? VISIBLE : GONE);
        }
        mCheckBox.setChecked(mChecked);
    }

    public MediaCheckBox getCheckBox() {
        return mCheckBox;
    }
}

