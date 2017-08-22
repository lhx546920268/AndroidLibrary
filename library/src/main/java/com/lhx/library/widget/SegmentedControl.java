package com.lhx.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lhx.library.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.util.ViewUtil;

import java.util.ArrayList;

/**
 * 分段选择器 类似IOS的 SegmentedControl
 */

public class SegmentedControl extends LinearLayout{

    //按钮信息
    private String[] mButtonTitles;

    //主题颜色
    private @ColorInt int mTintColor;

    //字体大小 sp
    private int mTextSize;

    //文字颜色
    private  @ColorInt int mTextColor;
    private  @ColorInt int mSelectedTextColor;

    //圆角 px
    private int mCornerRadius;

    //items
    private ArrayList<SegmentedItem> mItems;

    //背景
    private CornerBorderDrawable mBackgroundDrawable;

    //点击回调
    private OnItemClickListener mOnItemClickListener;

    //当前选中的
    private int mSelectedPosition;

    public int getSelectedPosition(){

        return mSelectedPosition;
    }

    public SegmentedControl(Context context) {
        this(context, null);
    }

    public SegmentedControl(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentedControl(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);
        mTintColor = ContextCompat.getColor(context, R.color.white);
        mTextColor = ContextCompat.getColor(context, R.color.white);
        mSelectedTextColor = ContextCompat.getColor(context, R.color.blue);

        mTextSize = 14;
        mCornerRadius = SizeUtil.pxFormDip(5, context);

        mBackgroundDrawable = new CornerBorderDrawable();
        mBackgroundDrawable.setBorderWidth(SizeUtil.pxFormDip(1, context));
        mBackgroundDrawable.setCornerRadius(mCornerRadius);
        mBackgroundDrawable.setBorderColor(mTintColor);
        ViewUtil.setBackground(mBackgroundDrawable, this);
    }

    public void setButtonTitles(String[] buttonTitles){
        if(mButtonTitles != buttonTitles){
            mButtonTitles = buttonTitles;

            initItems();
        }
    }

    public void setTextSize(int textSize){

        if(mTextSize != textSize){
            mTextSize = textSize;

            if(mItems != null && mItems.size() > 0){
                for(SegmentedItem item : mItems){
                    item.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
                }
            }
        }
    }

    public void setTintColor(@ColorInt int tintColor){
        if(mTintColor != tintColor){
            mTintColor = tintColor;

            if(mItems != null && mItems.size() > 0){
                for(SegmentedItem item : mItems){
                    item.selectedBackgroundDrawable.setBackgroundColor(mTintColor);
                }
            }
        }
    }

    public void setTextColor(@ColorInt int textColor){

        if(mTextColor != textColor){
            mTextColor = textColor;
            if(mItems != null && mItems.size() > 0){
                for(SegmentedItem item : mItems){
                    if(item.position != mSelectedPosition){
                        item.setTextColor(mTextColor);
                    }
                }
            }
        }
    }

    public void setSelectedTextColor(@ColorInt int selectedTextColor){

        if(mSelectedTextColor != selectedTextColor){
            mSelectedTextColor = selectedTextColor;
            if(mItems != null && mItems.size() > 0 && mSelectedPosition < mItems.size()){
                SegmentedItem item = mItems.get(mSelectedPosition);
                item.setTextColor(mSelectedTextColor);
            }
        }
    }

    public void setBorderWidth(int borderWidth){

        mBackgroundDrawable.setBorderWidth(borderWidth);
    }

    public void setCornerRadius(int cornerRadius){

        if(mCornerRadius != cornerRadius){
            mCornerRadius = cornerRadius;
            mBackgroundDrawable.setCornerRadius(mCornerRadius);

            if(mItems != null && mItems.size() > 0){
                for(int i = 0; i < mItems.size();i ++){

                    SegmentedItem item = mItems.get(i);
                    setItemCornerRadius(item, i);
                }
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 设置选中
     * @param position 选中的位置
     * @param click 是否是点击，需要回调
     */
    public void setSelectedPosition(int position, boolean click){

        if(mSelectedPosition != position){
            if(mSelectedPosition >= 0 && mSelectedPosition < mItems.size()){
                SegmentedItem item = mItems.get(mSelectedPosition);
                item.setTextColor(mTextColor);
                item.setSelected(false);
            }

            mSelectedPosition = position;

            SegmentedItem item = mItems.get(mSelectedPosition);
            item.setSelected(true);
            item.setTextColor(mSelectedTextColor);

            if(click && mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(mSelectedPosition);
            }
        }
    }

    //设置选中
    public void setSelectedPosition(int position){
        setSelectedPosition(position, false);
    }


    //设置item圆角
    private void setItemCornerRadius(SegmentedItem item, int position){

        if(mButtonTitles.length > 1){

            if(position == 0){
                item.selectedBackgroundDrawable.setLeftTopCornerRadius(mCornerRadius);
                item.selectedBackgroundDrawable.setLeftBottomCornerRadius(mCornerRadius);
            }else if(position == mButtonTitles.length - 1){
                item.selectedBackgroundDrawable.setRightTopCornerRadius(mCornerRadius);
                item.selectedBackgroundDrawable.setRightBottomCornerRadius(mCornerRadius);
            }
        }else {
            item.selectedBackgroundDrawable.setCornerRadius(mCornerRadius);
        }
    }

    //创建UI
    private void initItems(){
        removeAllViews();
        if(mButtonTitles == null || mButtonTitles.length == 0)
            return;

        mSelectedPosition = 0;
        Context context = getContext();
        mItems = new ArrayList<>(mButtonTitles.length);

        for(int i = 0;i < mButtonTitles.length;i ++){
            SegmentedItem item = new SegmentedItem(context);
            item.position = i;
            item.setText(mButtonTitles[i]);
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
            item.setGravity(Gravity.CENTER);
            item.selectedBackgroundDrawable.setBackgroundColor(mTintColor);
            setItemCornerRadius(item, i);
            item.setSelected(i == mSelectedPosition);

            item.setTextColor(item.isSelected() ? mSelectedTextColor : mTextColor);

            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            item.setLayoutParams(params);
            addView(item);
            mItems.add(item);

            item.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    SegmentedItem segmentedItem = (SegmentedItem)v;
                    setSelectedPosition(segmentedItem.position, true);
                }
            });
        }
    }

    //分段选择 item
    class SegmentedItem extends AppCompatTextView{


        //正常drawable
        CornerBorderDrawable normalBackgroundDrawable;

        //选中
        CornerBorderDrawable selectedBackgroundDrawable;

        //下标
        int position;


        public SegmentedItem(Context context) {
            super(context);

            initialization();
        }

        //初始化
        private void initialization(){

            StateListDrawable stateListDrawable = new StateListDrawable();

            selectedBackgroundDrawable = new CornerBorderDrawable();
            selectedBackgroundDrawable.setBackgroundColor(Color.WHITE);

            //state_selected 和 state_pressed一起会冲突
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, selectedBackgroundDrawable);
            normalBackgroundDrawable = new CornerBorderDrawable();
            stateListDrawable.addState(new int[]{}, normalBackgroundDrawable);

            ViewUtil.setBackground(stateListDrawable, this);
        }
    }

    //回调
    public interface OnItemClickListener{

        //点击item
        void onItemClick(int position);
    }
}
