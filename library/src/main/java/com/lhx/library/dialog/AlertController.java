package com.lhx.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
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
 * 弹窗控制器
 */

public class AlertController implements DialogInterface.OnDismissListener, View.OnClickListener {

    //警告框 类似IOS中的  UIAlertView
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
    private @ColorInt int mTitleColor = Color.BLACK;

    //标题字体大小
    private @ColorInt int mTitleSize = 17;

    //副标题字体颜色
    private @ColorInt int mSubtitleColor = Color.GRAY;

    //副标题字体大小
    private int mSubtitleSize = 12;

    //按钮字体大小
    private int mButtonTextSize = 15;

    //按钮字体颜色
    private @ColorInt int mButtonTextColor;

    //警示按钮字体颜色 如删除
    private @ColorInt int mDestructiveButtonTextColor = Color.RED;

    //无法点击的按钮字体颜色
    private @ColorInt int mDisableButtonTextColor = Color.GRAY;

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

    //标题
    String mTitle;

    //副标题
    private String mSubtitle;

    //图标
    private Drawable mIcon;

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

    public static AlertController showAlert(@NonNull Context context, String title, String subtitle, Drawable icon,
                                            @NonNull String ... otherButtonTitles){
        return new AlertController(context, STYLE_ALERT, title, subtitle, icon, otherButtonTitles);
    }

    public static AlertController showAlert(@NonNull Context context, String title,
                                            @NonNull String ... otherButtonTitles){
        return new AlertController(context, STYLE_ALERT, title, null, null, otherButtonTitles);
    }

    public static AlertController showActionSheet(@NonNull Context context, String title, String subtitle, Drawable icon,
                                                  @NonNull String ... otherButtonTitles){
        return new AlertController(context, STYLE_ACTION_SHEET, title, subtitle, icon, otherButtonTitles);
    }

    public static AlertController showActionSheet(@NonNull Context context, String title,
                                                  @NonNull String ... otherButtonTitles){
        return new AlertController(context, STYLE_ACTION_SHEET, title, null, null, otherButtonTitles);
    }

    public AlertController(@NonNull Context context, @Style int style, String title, String subtitle, Drawable icon,
                           @NonNull String ... otherButtonTitles) {

        mContext = context;
        mStyle = style;
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
        mButtonTitles = otherButtonTitles;

        mDividerHeight = SizeUtil.pxFormDip(0.5f, mContext);
        mButtonTextColor = ContextCompat.getColor(mContext, R.color.light_blue);
        mHighlightBackgroundColor = ColorUtil.whitePercentColor(0.80f, 1.0f);
    }

    public void setDialogBackgroundColor(int dialogBackgroundColor) {
        mDialogBackgroundColor = dialogBackgroundColor;
    }

    public void setHighlightBackgroundColor(int highlightBackgroundColor) {
        mHighlightBackgroundColor = highlightBackgroundColor;
    }

    public void setCornerRadius(int cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public void setTitleColor(@ColorInt int titleColor) {
        mTitleColor = titleColor;
    }

    public void setTitleSize(int titleSize) {
        mTitleSize = titleSize;
    }

    public void setSubtitleColor(int subtitleColor) {
        mSubtitleColor = subtitleColor;
    }

    public void setSubtitleSize(int subtitleSize) {
        mSubtitleSize = subtitleSize;
    }

    public void setButtonTextSize(int buttonTextSize) {
        mButtonTextSize = buttonTextSize;
    }

    public void setButtonTextColor(int buttonTextColor) {
        mButtonTextColor = buttonTextColor;
    }

    public void setDestructiveButtonTextColor(int destructiveButtonTextColor) {
        mDestructiveButtonTextColor = destructiveButtonTextColor;
    }

    public void setDisableButtonTextColor(int disableButtonTextColor) {
        mDisableButtonTextColor = disableButtonTextColor;
    }

    public void setDividerHeight(int dividerHeight) {
        mDividerHeight = dividerHeight;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public void setButtonTitles(String[] buttonTitles) {
        mButtonTitles = buttonTitles;
    }

    public void setShouldDismissAfterClickItem(boolean shouldDismissAfterClickItem) {
        mShouldDismissAfterClickItem = shouldDismissAfterClickItem;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setAlertUIHander(AlertUIHandler alertUIHander) {
        mAlertUIHander = alertUIHander;
    }

    public LinearLayout getmContentView() {
        return mContentView;
    }

    public ImageView getmLogoImageView() {
        return mLogoImageView;
    }

    public TextView getmTitleTextView() {
        return mTitleTextView;
    }

    public TextView getmSubtitleTextView() {
        return mSubtitleTextView;
    }

    public TextView getmCancelTextView() {
        return mCancelTextView;
    }

    public LinearLayout getmTopContainer() {
        return mTopContainer;
    }

    public RecyclerView getmRecyclertView() {
        return mRecyclertView;
    }

    //初始化视图
    private void initView(){

        mContentView = (LinearLayout)View.inflate(mContext, mStyle == STYLE_ALERT ? R.layout.alert_dialog : R.layout
                .action_sheet_dialog, null);
        mLogoImageView = (ImageView)mContentView.findViewById(R.id.logo);
        if(mIcon != null){
            mLogoImageView.setImageDrawable(mIcon);
        }else {
            mLogoImageView.setVisibility(View.GONE);
        }

        mTitleTextView = (TextView)mContentView.findViewById(R.id.title);
        mTitleTextView.setTextColor(mTitleColor);
        mTitleTextView.setTextSize(mTitleSize);
        if(mTitle == null){
            mTitleTextView.setVisibility(View.GONE);
        }else {
            mTitleTextView.setText(mTitle);
        }

        mSubtitleTextView = (TextView)mContentView.findViewById(R.id.subtitle);
        mSubtitleTextView.setTextColor(mSubtitleColor);
        mSubtitleTextView.setTextSize(mSubtitleSize);
        if(mSubtitle == null){
            mSubtitleTextView.setVisibility(View.GONE);
        }else {
            mSubtitleTextView.setText(mSubtitle);
        }

        //actionSheet 样式不一样
        if(mStyle == STYLE_ACTION_SHEET){
            mTopContainer = (LinearLayout)mContentView.findViewById(R.id.top_container);
            mCancelTextView = (TextView)mContentView.findViewById(R.id.cancel);
            mCancelTextView.setOnClickListener(this);
            mCancelTextView.setTextSize(mButtonTextSize);
            mCancelTextView.setTextColor(mButtonTextColor);

            setBackground(mTopContainer);
            setBackgroundSelector(mCancelTextView);

            //隐藏顶部分割线
            if(!hasTopContent()){
                mContentView.findViewById(R.id.divider).setVisibility(View.GONE);
            }

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
        if(mContentView == null){
            initView();
        }
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

    //是否有头部内容
    private boolean hasTopContent(){
        return mTitle != null || mSubtitle != null || mIcon != null;
    }

    //设置背景
    private void setBackground(View view){
        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.setCornerRadius(mCornerRadius);
        drawable.setBackgroundColor(mDialogBackgroundColor);
        drawable.attatchView(view, false);
    }

    //设置点击效果
    private CornerBorderDrawable[] setBackgroundSelector(View view){
        StateListDrawable stateListDrawable = new StateListDrawable();

        CornerBorderDrawable drawablePressed = new CornerBorderDrawable();
        drawablePressed.setCornerRadius(mCornerRadius);
        drawablePressed.setBackgroundColor(mHighlightBackgroundColor);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, drawablePressed);

        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.setCornerRadius(mCornerRadius);
        drawable.setBackgroundColor(mDialogBackgroundColor);
        stateListDrawable.addState(new int[]{}, drawable);

        view.setClickable(true);
        view.setBackground(stateListDrawable);

        return new CornerBorderDrawable[]{drawablePressed, drawable};
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
            boolean enable = true;
            //刷新UI
            if(mAlertUIHander != null){
                if(!mAlertUIHander.shouldEnable(AlertController.this, position)){
                    color = mDisableButtonTextColor;
                    enable = false;
                }else if(mAlertUIHander.shouldDestructive(AlertController.this, position)){
                    color = mDestructiveButtonTextColor;
                }
            }
            holder.itemView.setEnabled(enable);
            holder.textView.setTextColor(color);

            //设置点击效果
            if(mStyle == STYLE_ACTION_SHEET || mButtonTitles.length > 2){
                //垂直
                if(position == 0 && !hasTopContent()){
                    holder.drawablePressed.setCornerRadius(mCornerRadius, 0, mCornerRadius, 0);
                    holder.drawable.setCornerRadius(mCornerRadius, 0, mCornerRadius, 0);
                }else if(position == mButtonTitles.length - 1){
                    holder.drawablePressed.setCornerRadius(0, mCornerRadius, 0, mCornerRadius);
                    holder.drawable.setCornerRadius(0, mCornerRadius, 0, mCornerRadius);
                }else {
                    holder.drawablePressed.setCornerRadius(0);
                    holder.drawable.setCornerRadius(0);
                }
            }else {

                //水平
                if(position == 0){
                    holder.drawablePressed.setCornerRadius(0, mCornerRadius, 0, 0);
                    holder.drawable.setCornerRadius(0, mCornerRadius, 0, 0);
                }else {
                    holder.drawablePressed.setCornerRadius(0, 0, 0, mCornerRadius);
                    holder.drawable.setCornerRadius(0, 0, 0, mCornerRadius);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mButtonTitles != null ? mButtonTitles.length : 0;
        }
    }

    //弹窗按钮
    private class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        CornerBorderDrawable drawable;
        CornerBorderDrawable drawablePressed;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView)itemView.findViewById(R.id.text);
            CornerBorderDrawable[] drawables = setBackgroundSelector(itemView);
            drawable = drawables[0];
            drawable.setCornerRadius(0);
            drawablePressed = drawables[1];
            drawablePressed.setCornerRadius(0);
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

            //绘制按钮分割线
            int count = parent.getChildCount();
            for(int i = 0;i < count;i ++){
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                if(position < mButtonTitles.length - 1){

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

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //设置item的偏移量 大小为item+分割线
            int position = parent.getChildAdapterPosition(view);
            if(position < mButtonTitles.length - 1){
                if(mStyle == STYLE_ACTION_SHEET || mButtonTitles.length > 2){
                    //垂直排列
                    outRect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom() + mDividerHeight);
                }else {
                    //水平
                    outRect.set(view.getLeft(), view.getTop(), view.getRight() + mDividerHeight, view.getBottom());
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
