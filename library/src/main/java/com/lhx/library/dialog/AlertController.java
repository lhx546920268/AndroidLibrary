package com.lhx.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.util.ColorUtil;
import com.lhx.library.util.SizeUtil;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * 弹出控制器
 */

public class AlertController implements DialogInterface.OnDismissListener, View.OnClickListener {

    //警告框
    public static final int STYLE_ALERT = 0;

    //类似IOS中的 UIActionSheet，从底部向上弹出
    public static final int STYLE_ACTION_SHEET = 1;

    //弹窗样式
    @IntDef({STYLE_ALERT, STYLE_ACTION_SHEET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Style{}

    private @AlertController.Style int mStyle;

    //弹窗
    private Dialog mDialog;

    //弹窗背景颜色
    private int mDialogBackgroundColor = Color.WHITE;

    //按钮点击背景颜色
    private int mHighlightBackgroundColor;

    //圆角半径
    private int mCornerRadius = 10;

    //标题字体颜色
    private int mTitleColor = Color.BLACK;

    //标题字体大小
    private int mTitleSize = 17;

    //副标题字体颜色
    private int mSubtitleColor = Color.GRAY;

    //副标题字体大小
    private int mSubtitleSize = 12;

    //按钮字体大小
    private int mButtonTextSize = 15;

    //按钮字体颜色
    private int mButtonTextColor;

    //警示按钮字体颜色 如删除
    private int mDestructiveButtonTextColor = Color.RED;

    //无法点击的按钮字体颜色
    private int mDisableButtonTextColor = Color.GRAY;

    //内容视图
    private LinearLayout mContentView;

    //logo
    private ImageView mLogoImageView;

    //标题
    private TextView mTitleTextView;

    //副标题
    private TextView mSubtitleTextView;

    //actionSheet的取消按钮
    private TextView mCancelTextView;

    //actionSheet 顶部容器
    private LinearLayout mTopContainer;

    //按钮列表
    private RecyclerView mRecyclertView;

    //分割线大小px
    private int mDividerHeight;

    //按钮信息
    private String[] mButtonTitles;

    //上下文
    private Context mContext;

    //点击按钮后弹窗是否消失
    private boolean mShouldDismissAfterClickItem = true;

    //点击事件回调
    private OnItemClickListener mOnItemClickListener;

    //UI回调
    private AlertUIHandler mAlertUIHander;

    public AlertController(@NonNull Context context, @Style int style, String title, String subtitle, int iconResId,
                           @NonNull String ... otherButtonTitles) {


        mContext = context;
        mStyle = style;
        mButtonTitles = otherButtonTitles;

        mDividerHeight = SizeUtil.pxFormDip(0.5f, mContext);
        mButtonTextColor = ContextCompat.getColor(mContext, R.color.light_blue);
        mHighlightBackgroundColor = Color.RED;

        mContentView = (LinearLayout)View.inflate(mContext, mStyle == STYLE_ALERT ? R.layout.alert_dialog : R.layout
                .action_sheet_dialog, null);

        mLogoImageView = (ImageView)mContentView.findViewById(R.id.logo);
        if(iconResId != 0){
            mLogoImageView.setImageResource(iconResId);
        }else {
            mLogoImageView.setVisibility(View.GONE);
        }

        mTitleTextView = (TextView)mContentView.findViewById(R.id.title);
        mTitleTextView.setTextColor(mTitleColor);
        mTitleTextView.setTextSize(mTitleSize);
        if(title == null){
            mTitleTextView.setVisibility(View.GONE);
        }else {
            mTitleTextView.setText(title);
        }

        mSubtitleTextView = (TextView)mContentView.findViewById(R.id.subtitle);
        mSubtitleTextView.setTextColor(mSubtitleColor);
        mSubtitleTextView.setTextSize(mSubtitleSize);
        if(subtitle == null){
            mSubtitleTextView.setVisibility(View.GONE);
        }else {
            mSubtitleTextView.setText(subtitle);
        }

        //actionSheet 样式不一样
        if(mStyle == STYLE_ACTION_SHEET){
            mTopContainer = (LinearLayout)mContentView.findViewById(R.id.top_container);
            mCancelTextView = (TextView)mContentView.findViewById(R.id.cancel);
            mCancelTextView.setOnClickListener(this);
            mCancelTextView.setTextSize(mButtonTextSize);
            mCancelTextView.setTextColor(mButtonTextColor);

            setBackground(mTopContainer);
            setHighlightBackground(mCancelTextView);
        }else {
            mContentView.setBackgroundColor(mDialogBackgroundColor);
            setBackground(mContentView);
        }

        mRecyclertView = (RecyclerView)mContentView.findViewById(R.id.recycler_view);
        int spanCount = (mButtonTitles.length > 2 || mStyle == STYLE_ACTION_SHEET) ? 1 : 2;
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, spanCount);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclertView.setLayoutManager(layoutManager);

        if(mButtonTitles.length > 1){
            //添加分割线
            mRecyclertView.addItemDecoration(new ItemDecoration());
        }
        mRecyclertView.setAdapter(new Adapter());

        mDialog = new Dialog(mContext, R.style.Theme_dialog_noTitle_noBackground);
        mDialog.setOnDismissListener(this);
        mDialog.setCancelable(mStyle == STYLE_ACTION_SHEET);

        mDialog.setContentView(mContentView);

        //设置弹窗大小
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = mStyle == STYLE_ALERT ? Gravity.CENTER : Gravity.BOTTOM;
        layoutParams.width = getContentViewWidth();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if(mStyle == STYLE_ACTION_SHEET){
            window.setWindowAnimations(R.style.action_sheet_animate);
        }
        mDialog.getWindow().setAttributes(layoutParams);
    }

    //显示弹窗
    public void show(){
        if(!mDialog.isShowing()){
            mDialog.show();
        }
    }

    //隐藏弹窗
    public void dismiss(){
        if(mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(mAlertUIHander != null){
            mAlertUIHander.onDismiss(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == mCancelTextView){
            dismiss();
        }
    }

    //获取内容视图宽度
    private int getContentViewWidth(){
        switch (mStyle){
            case STYLE_ALERT :
                return SizeUtil.pxFormDip(280, mContext);
            case STYLE_ACTION_SHEET :
                return ViewGroup.LayoutParams.MATCH_PARENT;
        }

        return 0;
    }

    //设置背景
    private void setBackground(View view){
        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.setCornerRadius(mCornerRadius);
        drawable.setBackgroundColor(mDialogBackgroundColor);
        drawable.attatchView(view, false);
    }

    //设置点击效果
    private void setHighlightBackground(View view){
        StateListDrawable stateListDrawable = new StateListDrawable();

        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(mHighlightBackgroundColor));
        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.setCornerRadius(mCornerRadius);
        drawable.setBackgroundColor(mDialogBackgroundColor);
        drawable.attatchView(view, false);
        stateListDrawable.addState(new int[]{}, drawable);

        view.setClickable(true);
        view.setBackground(stateListDrawable);
    }

    //按钮列表适配器
    private class Adapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final ViewHolder holder = new ViewHolder(View.inflate(mContext, R.layout.alert_button_item, null));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(AlertController.this, holder.getAdapterPosition());
                    }
                    if(mShouldDismissAfterClickItem){
                        dismiss();
                    }
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(mButtonTitles[position]);
            holder.textView.setTextSize(mButtonTextSize);

            int color = mButtonTextColor;
            //刷新UI
            if(mAlertUIHander != null){
                if(!mAlertUIHander.shouldEnable(AlertController.this, position)){
                    color = mDisableButtonTextColor;
                }else if(mAlertUIHander.shouldDestructive(AlertController.this, position)){
                    color = mDestructiveButtonTextColor;
                }
            }

            holder.textView.setTextColor(color);
        }

        @Override
        public int getItemCount() {
            return mButtonTitles != null ? mButtonTitles.length : 0;
        }
    }

    //弹窗按钮
    private class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView)itemView.findViewById(R.id.text);
        }
    }

    //按钮分割线
    private class ItemDecoration extends RecyclerView.ItemDecoration{

        ///分割线
        Drawable mDivider;

        ///构造方法
        ItemDecoration(){

            ColorDrawable drawable = new ColorDrawable();
            drawable.setColor(ContextCompat.getColor(mContext, R.color.divider_color));
            mDivider = drawable;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);

            int count = parent.getChildCount();
            for(int i = 0;i < count;i ++){
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                if(position < mButtonTitles.length - 1){
                    GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams)child.getLayoutParams();

                    //垂直排列
                    if(mStyle == STYLE_ACTION_SHEET || mButtonTitles.length > 2){
                        mDivider.setBounds(0, child.getBottom(), child.getRight(), child.getBottom() + mDividerHeight);
                    }else {
                        //水平排列
                        mDivider.setBounds(child.getRight(), 0, child.getRight() + mDividerHeight, child.getBottom());
                    }
                    mDivider.draw(c);
                }
            }
        }
    }

    //弹窗按钮点击回调
    public interface OnItemClickListener{

        //点击某个按钮 从左到右，从上到下
        void onItemClick(AlertController controller, int index);
    }

    //弹窗UI回调
    public interface AlertUIHandler{

        //弹窗消失
        void onDismiss(AlertController controller);

        //该按钮是否具有警示意义 从左到右，从上到下
        boolean shouldDestructive(AlertController controller, int index);

        //该按钮是否可以点击 从左到右，从上到下
        boolean shouldEnable(AlertController controller, int index);
    }
}
