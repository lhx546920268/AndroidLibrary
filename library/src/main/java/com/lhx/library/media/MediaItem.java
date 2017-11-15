package com.lhx.library.media;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lhx.library.R;

/**
 * 媒体 item
 */

public class MediaItem extends FrameLayout {

    //图片
    private ImageView mImageView;

    //覆盖物
    private View mOverlay;

    //选择框
    private CheckBox mCheckBox;

    //
    private boolean mChecked;

    public MediaItem(@NonNull Context context) {
        this(context, null);
    }

    public MediaItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaItem(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mImageView, params);

        mCheckBox = new CheckBox(context);

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{};

        int[] colors = new int[]{ContextCompat.getColor(context, R.color.theme_color), Color.GRAY};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        CompoundButtonCompat.setButtonTintList(mCheckBox, colorStateList);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        addView(mCheckBox, params);
    }


    //设置是否选中
    public void setChecked(boolean checked){
        if(mChecked != checked){
            mChecked = checked;

            if(mOverlay == null){
                mOverlay = new View(getContext());
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                addView(mOverlay, 1, params);
            }

            mOverlay.setVisibility(mChecked ? VISIBLE : GONE);
            mCheckBox.setChecked(mChecked);
        }
    }
}

