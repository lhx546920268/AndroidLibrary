package com.lhx.library.popupWindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.listView.AbsListViewAdapter;
import com.lhx.library.section.SectionHandler;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.widget.OnSingleClickListener;

import java.util.ArrayList;

/**
 * 列表弹窗
 */

public class ListPopupWindow extends BasePopupWindow {

    //列表
    private ListView mListView;
    private ListPopupWindowAdapter mAdapter;

    //行高 px
    private int mRowHeight;

    //列表信息
    private ArrayList<ListInfo> mInfos;

    //选中颜色
    private @ColorInt int mSelectedColor;

    //正常颜色
    private @ColorInt int mNormalColor;

    //字体大小 sp
    private int mTextSize = 15;

    //选中的图标
    private Drawable mTickDrawable;

    //是否可以选择已选中的
    private boolean mSelectTheSelectedOneEnable = false;

    //选择后是否关闭弹窗
    private boolean mShouldDismissAfterSelect = true;

    //选中的下标
    private int mSelectedPosition = 0;

    //点击回调
    private OnItemClickListener mOnItemClickListener;

    //列表最大高度
    private int mListMaxHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    public void setRowHeight(int rowHeight) {
        if(mRowHeight != rowHeight){
            mRowHeight = rowHeight;
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public int getRowHeight() {
        return mRowHeight;
    }

    public void setSelectTheSelectedOneEnable(boolean selectTheSelectedOneEnable) {
        mSelectTheSelectedOneEnable = selectTheSelectedOneEnable;
    }

    public void setSelectedColor(int selectedColor) {
        if(mSelectedColor != selectedColor){
            mSelectedColor = selectedColor;
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setNormalColor(int normalColor) {
        if(mNormalColor != normalColor){
            mNormalColor = normalColor;
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setTextSize(int textSize) {
        if(mTextSize != textSize){
            mTextSize = textSize;
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setSelectedPosition(int selectedPosition) {
        if(mSelectedPosition != selectedPosition){
            mSelectedPosition = selectedPosition;
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setTickDrawable(Drawable tickDrawable) {
        if(mTickDrawable != tickDrawable){
            mTickDrawable = tickDrawable;
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setListMaxHeight(int listMaxHeight) {
        if(mListMaxHeight != listMaxHeight){
            mListMaxHeight = listMaxHeight;
            setListHeight();
        }
    }

    public void setInfos(ArrayList<ListInfo> infos) {
        if(mInfos != infos){
            mInfos = infos;
            setListHeight();
            mAdapter.notifyDataSetChanged();
        }
    }

    public ListView getListView() {
        return mListView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){

        if(mOnItemClickListener != onItemClickListener){
            mOnItemClickListener = onItemClickListener;
        }
    }

    public void setShouldDismissAfterSelect(boolean flag) {
        this.mShouldDismissAfterSelect = flag;
    }

    public ListPopupWindow(Context context) {
        super(context);

        mSelectedColor = Color.BLACK;
        mNormalColor = Color.BLACK;
        mRowHeight = SizeUtil.pxFormDip(45, mContext);

        mAdapter = new ListPopupWindowAdapter(mContext);
        mListView.setAdapter(mAdapter);

        mContentView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(isShowing()){
                    dismiss();
                }
            }
        });
    }

    @NonNull
    @Override
    public View getContentView() {

        View view = View.inflate(mContext, R.layout.list_popup_window, null);
        mListView = (ListView)view.findViewById(R.id.list_view);

        return view;
    }



    //设置内容高度
    private void setListHeight(){
        if(mInfos != null && mInfos.size() > 0){
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mListView.getLayoutParams();
            if(mListMaxHeight != ViewGroup.LayoutParams.WRAP_CONTENT){
                params.height = Math.min(mInfos.size() * mRowHeight, mListMaxHeight);
            }else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            mListView.setLayoutParams(params);
        }
    }

    class ListPopupWindowAdapter extends AbsListViewAdapter {

        public ListPopupWindowAdapter(@NonNull Context context) {
            super(context);
        }

        @Override
        public int numberOfItemInSection(int section) {
            return mInfos != null ? mInfos.size() : 0;
        }

        @Override
        public View getViewForIndexPath(int indexInSection, int section, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_popup_window_item, parent, false);
            }

            AbsListView.LayoutParams params = (AbsListView.LayoutParams)convertView.getLayoutParams();
            params.height = mRowHeight;
            convertView.setLayoutParams(params);

            TextView textView = (TextView) convertView.findViewById(R.id.title);
            ListInfo info = mInfos.get(indexInSection);
            textView.setText(info.title);
            textView.setTextColor(indexInSection == mSelectedPosition ? mSelectedColor : mNormalColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.tick);
            imageView.setImageDrawable(mTickDrawable);
            imageView.setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_IN);

            imageView.setVisibility(indexInSection == mSelectedPosition ? View.VISIBLE : View.GONE);

            return convertView;
        }

        @Override
        public int getItemViewTypeForIndexPath(int indexInSection, int section, @SectionHandler.ItemType int type) {
            return 0;
        }

        @Override
        public int numberOfItemViewTypes() {
            return 1;
        }

        @Override
        public void onItemClick(int indexInSection, int section, View view) {

            if (indexInSection == mSelectedPosition && !mSelectTheSelectedOneEnable)
                return;

            mSelectedPosition = indexInSection;

            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(indexInSection, mInfos.get(indexInSection));
            }

            if (mShouldDismissAfterSelect) {
                dismiss();
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    //点击
    public interface OnItemClickListener{

        //点击
        void onItemClick(int position, ListInfo info);
    }
}
