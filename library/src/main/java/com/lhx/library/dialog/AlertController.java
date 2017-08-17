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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.lhx.library.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.util.ColorUtil;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.util.StringUtil;
import com.lhx.library.util.ViewUtil;
import com.lhx.library.widget.OnSingleClickListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 弹窗控制器
 */

public class AlertController implements DialogInterface.OnDismissListener, View.OnClickListener {

    //警告框 类似IOS中的  UIAlertView
    public static final int STYLE_ALERT = 0;

    //类似IOS中的 UIActionSheet，从底部向上弹出
    public static final int STYLE_ACTION_SHEET = 1;

    //关闭dialog
    private static final int DISMISS_DIALOG = 1;
    private static final String POSITION = "position";

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

    //按钮高度padding
    private int mButtonTopBottomPadding;

    //按钮左右padding
    private int mButtonLeftRightPadding;

    //内容垂直间隔
    private int mContentVerticalSpace;

    //内容上下边距
    private int mContentPadding;

    //弹窗边距
    private int mDialogPadding;

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
    private View mContentView;

    //logo
    private ImageView mLogoImageView;

    //标题
    private TextView mTitleTextView;

    //副标题
    private TextView mSubtitleTextView;

    //actionSheet的取消按钮
    private TextView mCancelTextView;

    //actionSheet的 顶部透明视图，用来点击取消
    private View mTopTranparentView;

    //actionSheet 顶部容器
    private LinearLayout mTopContainer;

    //按钮列表
    private RecyclerView mRecyclerView;

    //内容分割线
    private View mContentDivider;

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

    //防止内容过多无法看完
    private ScrollView mScrollView;
    private LinearLayout mScrollContainer;

    //是否需要计算内容高度 当内容或者按钮数量过多时可设置，防止内容显示不完
    private boolean mShouldMesureContentHeight = false;

    //用于延迟操作
    Handler mHandler;


    public static AlertController buildAlert(@NonNull Context context, String title, String subtitle, Drawable icon,
                                            String ... otherButtonTitles){
        return new AlertController(context, STYLE_ALERT, title, subtitle, icon, otherButtonTitles);
    }

    public static AlertController buildAlert(@NonNull Context context, String title,
                                            String ... otherButtonTitles){
        return new AlertController(context, STYLE_ALERT, title, null, null, otherButtonTitles);
    }

    public static AlertController buildActionSheet(@NonNull Context context, String title, String subtitle, Drawable
            icon,
                                                  String ... otherButtonTitles){
        return new AlertController(context, STYLE_ACTION_SHEET, title, subtitle, icon, otherButtonTitles);
    }

    public static AlertController buildActionSheet(@NonNull Context context, String title, String ... otherButtonTitles){
        return new AlertController(context, STYLE_ACTION_SHEET, title, null, null, otherButtonTitles);
    }

    public AlertController(@NonNull Context context, @Style int style, String title, String subtitle, Drawable icon,
                           String ... otherButtonTitles) {

        mContext = context;
        mStyle = style;
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;

        mButtonTitles = otherButtonTitles;
        if(mButtonTitles == null){
            mButtonTitles = new String[]{};
        }

        mDividerHeight = SizeUtil.pxFormDip(0.5f, mContext);
        mButtonTextColor = ContextCompat.getColor(mContext, R.color.light_blue);
        mHighlightBackgroundColor = ColorUtil.whitePercentColor(0.80f, 1.0f);
        mButtonTopBottomPadding = SizeUtil.pxFormDip(13, mContext);
        mButtonLeftRightPadding = SizeUtil.pxFormDip(10, mContext);
        mContentVerticalSpace = SizeUtil.pxFormDip(5, mContext);
        mContentPadding = SizeUtil.pxFormDip(15, mContext);
        mDialogPadding = SizeUtil.pxFormDip(10, context);

        mContentView = View.inflate(mContext, mStyle == STYLE_ALERT ? R.layout.alert_dialog : R.layout
                .action_sheet_dialog, null);

        mScrollView = (ScrollView)mContentView.findViewById(R.id.scrollView);
        mScrollContainer = (LinearLayout)mContentView.findViewById(R.id.scroll_container);
        mLogoImageView = (ImageView)mContentView.findViewById(R.id.logo);
        mTitleTextView = (TextView)mContentView.findViewById(R.id.title);
        mSubtitleTextView = (TextView)mContentView.findViewById(R.id.subtitle);
        mContentDivider = mContentView.findViewById(R.id.divider);

        if(mStyle == STYLE_ACTION_SHEET){
            mTopContainer = (LinearLayout)mContentView.findViewById(R.id.top_container);
            mCancelTextView = (TextView)mContentView.findViewById(R.id.cancel);
        }
        mRecyclerView = (RecyclerView)mContentView.findViewById(R.id.recycler_view);
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

    public void setButtonTopBottomPadding(int buttonTopBottomPadding) {
        mButtonTopBottomPadding = buttonTopBottomPadding;
    }

    public void setButtonLeftRightPadding(int buttonLeftRightPadding) {
        mButtonLeftRightPadding = buttonLeftRightPadding;
    }

    public void setContentVerticalSpace(int contentVerticalSpace) {
        mContentVerticalSpace = contentVerticalSpace;
    }

    public void setContentPadding(int contentPadding) {
        mContentPadding = contentPadding;
    }

    public void setDialogPadding(int dialogPadding) {
        mDialogPadding = dialogPadding;
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

    public void setButtonTitles(@NonNull String[] buttonTitles) {
        mButtonTitles = buttonTitles;
    }

    public void setShouldDismissAfterClickItem(boolean shouldDismissAfterClickItem) {
        mShouldDismissAfterClickItem = shouldDismissAfterClickItem;
    }

    public void setShouldMesureContentHeight(boolean shouldMesureContentHeight){
        mShouldMesureContentHeight = shouldMesureContentHeight;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setAlertUIHander(AlertUIHandler alertUIHander) {
        mAlertUIHander = alertUIHander;
    }

    public View getContentView() {
        return mContentView;
    }

    public ImageView getLogoImageView() {
        return mLogoImageView;
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public TextView getSubtitleTextView() {
        return mSubtitleTextView;
    }

    public TextView getCancelTextView() {
        return mCancelTextView;
    }

    public LinearLayout getTopContainer() {
        return mTopContainer;
    }

    public RecyclerView getRecyclertView() {
        return mRecyclerView;
    }

    //初始化视图
    private void initView(){

        if((mButtonTitles == null || mButtonTitles.length == 0) && mStyle == STYLE_ALERT){
            mButtonTitles = new String[]{"确定"};
        }

        if(mIcon != null){
            mLogoImageView.setImageDrawable(mIcon);
            mLogoImageView.setPadding(0, mContentVerticalSpace, 0, 0);
        }else {
            mLogoImageView.setVisibility(View.GONE);
        }

        if(mTitle == null){
            mTitleTextView.setVisibility(View.GONE);
        }else {
            mTitleTextView.setTextColor(mTitleColor);
            mTitleTextView.setTextSize(mTitleSize);
            mTitleTextView.setText(mTitle);
            mTitleTextView.setPadding(0, mContentVerticalSpace, 0, 0);
        }

        if(mSubtitle == null){
            mSubtitleTextView.setVisibility(View.GONE);
        }else {
            mSubtitleTextView.setTextColor(mSubtitleColor);
            mSubtitleTextView.setTextSize(mSubtitleSize);
            mSubtitleTextView.setText(mSubtitle);
            mSubtitleTextView.setPadding(0, mContentVerticalSpace, 0, 0);
        }

        //actionSheet 样式不一样
        if(mStyle == STYLE_ACTION_SHEET){
            mCancelTextView.setOnClickListener(this);
            mCancelTextView.setTextSize(mButtonTextSize);
            mCancelTextView.setTextColor(mButtonTextColor);
            mCancelTextView.setPadding(mButtonLeftRightPadding, mButtonTopBottomPadding,
                    mButtonLeftRightPadding, mButtonTopBottomPadding);

            setBackground(mTopContainer);
            setBackgroundSelector(mCancelTextView);

            mTopTranparentView = mContentView.findViewById(R.id.top_tranparent_view);
            mTopTranparentView.setOnClickListener(this);

            boolean has = hasTopContent();
            //隐藏顶部分割线 没有按钮也隐藏
            if(!has || mButtonTitles.length == 0){
                mContentDivider.setVisibility(View.GONE);
            }

            mScrollContainer.setPadding(0, has ? mContentPadding - mContentVerticalSpace : 0, 0,
                    has ? mContentPadding : 0);

        }else {
            mScrollContainer.setPadding(0, mContentPadding - mContentVerticalSpace, 0, mContentPadding);
            mContentView.setBackgroundColor(mDialogBackgroundColor);
            setBackground(mContentView);
        }

        if(mShouldMesureContentHeight){
            mesureContentHeight();
        }

        int spanCount = (mButtonTitles.length != 2 || mStyle == STYLE_ACTION_SHEET) ? 1 : 2;
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, spanCount);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        if(mButtonTitles.length > 1){
            //添加分割线
            mRecyclerView.addItemDecoration(new ItemDecoration());
        }

        mRecyclerView.setAdapter(new Adapter());

        mDialog = new Dialog(mContext, R.style.Theme_dialog_noTitle_noBackground);
        mDialog.setOnDismissListener(this);
        mDialog.setContentView(mContentView);

        //设置弹窗大小
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = mStyle == STYLE_ALERT ? Gravity.CENTER : Gravity.BOTTOM;
        layoutParams.width = getContentViewWidth();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        switch (mStyle){
            case STYLE_ACTION_SHEET :
                window.getDecorView().setPadding(mDialogPadding, mDialogPadding, mDialogPadding, mDialogPadding);
                window.setWindowAnimations(R.style.action_sheet_animate);
                mDialog.setCancelable(true);
                mDialog.setCanceledOnTouchOutside(true);
                break;
            case STYLE_ALERT :
                window.getDecorView().setPadding(0, mDialogPadding, 0 , mDialogPadding);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                break;
        }

        mDialog.getWindow().setAttributes(layoutParams);
    }

    //计算内容高度
    private void mesureContentHeight(){

        //按钮内容高度
        int buttonContentHeight = 0;

        //顶部内容高度
        int topContentHeight = 0;

        //取消按钮高度 STYLE_ALERT 为 0
        int cancelButtonHeight = 0;

        //图标高度
        if(mIcon != null){
            topContentHeight += mIcon.getIntrinsicHeight();
            topContentHeight += mContentVerticalSpace;
        }

        int contentWidth = getContentViewWidth();
        if(mTitle != null || mSubtitle != null){

            //标题高度
            if(mTitle != null){
                topContentHeight += mContentVerticalSpace;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mTitleTextView.getLayoutParams();
                int maxWidth = contentWidth - params.leftMargin - params.rightMargin;
                topContentHeight += StringUtil.mesureTextHeight(mTitle, mTitleTextView.getPaint(), maxWidth);
            }

            //副标题高度
            if(mSubtitle != null){
                topContentHeight += mContentVerticalSpace;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mSubtitleTextView.getLayoutParams();
                int maxWidth = contentWidth - params.leftMargin - params.rightMargin;
                topContentHeight += StringUtil.mesureTextHeight(mSubtitle, mSubtitleTextView.getPaint(), maxWidth);
            }
        }

        int maxWidth = contentWidth - mButtonLeftRightPadding * 2;
        //加上边距和分割线
        switch (mStyle){
            case STYLE_ACTION_SHEET :
                if(hasTopContent()){
                    topContentHeight += mContentPadding * 2 - mContentVerticalSpace;
                }
                if(mButtonTitles.length > 0){

                    TextView textView = (TextView)View.inflate(mContext, R.layout.alert_button_item, null);
                    textView.setTextSize(mButtonTextSize);

                    for(int i = 0;i < mButtonTitles.length; i ++){
                        String title = mButtonTitles[i];
                        buttonContentHeight += StringUtil.mesureTextHeight(title, textView.getPaint(),
                                maxWidth) + mButtonTopBottomPadding * 2 + mDividerHeight;
                    }
                    buttonContentHeight -= mDividerHeight;
                }

                //取消按钮高度
                cancelButtonHeight += StringUtil.mesureTextHeight(mCancelTextView.getText(), mCancelTextView
                        .getPaint(), maxWidth) + mButtonTopBottomPadding * 2 + mDialogPadding;

                break;
            case STYLE_ALERT :
                topContentHeight += mContentPadding * 2 - mContentVerticalSpace;
                TextView textView = (TextView)View.inflate(mContext, R.layout.alert_button_item, null);
                textView.setTextSize(mButtonTextSize);

                for(int i = 0;i < mButtonTitles.length; i ++){
                    String title = mButtonTitles[i];
                    buttonContentHeight += StringUtil.mesureTextHeight(title, textView.getPaint(),
                            maxWidth) + mButtonTopBottomPadding * 2 + mDividerHeight;
                    if(mButtonTitles.length <= 2)
                        break;
                }
                if(buttonContentHeight > 0){
                    buttonContentHeight -= mDividerHeight;
                }
                break;
        }

        int maxHeight = mContext.getResources().getDisplayMetrics().heightPixels - mDialogPadding * 2 -
                cancelButtonHeight - mScrollContainer.getPaddingBottom() - mScrollContainer.getPaddingTop();
        if(mContentDivider.getVisibility() == View.VISIBLE){
            maxHeight -= mDividerHeight;
        }

        if(topContentHeight + buttonContentHeight > maxHeight){
            //内容太多了
            int half = maxHeight / 2;
            if(topContentHeight > half && buttonContentHeight < half){
                setScrollViewHeight(maxHeight - buttonContentHeight);
            }else if(topContentHeight < half && buttonContentHeight > half){
                setRecyclerViewHeight(maxHeight - topContentHeight);
            }else {
                setRecyclerViewHeight(half);
                setScrollViewHeight(half);
            }
        }
    }

    //设置scrollview
    private void setScrollViewHeight(int height){
        ViewGroup.LayoutParams params = mScrollView.getLayoutParams();
        params.height = height;
        mScrollView.setLayoutParams(params);
    }

    //设置recyclerView
    private void setRecyclerViewHeight(int height){
        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
        params.height = height;
        mRecyclerView.setLayoutParams(params);
    }

    //显示弹窗
    public void show(){
        if(mDialog == null){
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
        if(v == mCancelTextView || v == mTopTranparentView){
            dismiss();
        }
    }

    //获取内容视图宽度
    private int getContentViewWidth(){
        switch (mStyle){
            case STYLE_ALERT :
                return SizeUtil.pxFormDip(280, mContext);
            case STYLE_ACTION_SHEET :
                return mContext.getResources().getDisplayMetrics().widthPixels;
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
        ViewUtil.setBackground(stateListDrawable, view);

        return new CornerBorderDrawable[]{drawablePressed, drawable};
    }

    public Handler getHandler() {
        if(mHandler == null){
            mHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what){
                        case DISMISS_DIALOG : {

                            Bundle bundle = msg.getData();
                            if(mOnItemClickListener != null){
                                mOnItemClickListener.onItemClick(AlertController.this, bundle.getInt(POSITION));
                            }
                        }
                        break;
                    }
                    return true;
                }
            });
        }
        return mHandler;
    }

    //按钮列表适配器
    private class Adapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final ViewHolder holder = new ViewHolder(View.inflate(mContext, R.layout.alert_button_item, null));
            holder.itemView.setOnClickListener(new OnSingleClickListener() {
                                                   @Override
                                                   public void onSingleClick(View v) {
                                                       if(mShouldDismissAfterClickItem){
                                                           dismiss();

                                                           Bundle bundle = new Bundle();
                                                           bundle.putInt(POSITION, holder.getAdapterPosition());

                                                           Message message = getHandler().obtainMessage();
                                                           message.what = DISMISS_DIALOG;
                                                           message.setData(bundle);
                                                           getHandler().sendMessageDelayed(message, 200);
                                                       }else {
                                                           if(mOnItemClickListener != null){
                                                               mOnItemClickListener.onItemClick(AlertController.this, holder.getAdapterPosition());
                                                           }
                                                       }
                                                   }
                                               });

                    holder.textView.setTextSize(mButtonTextSize);
            holder.textView.setPadding(mButtonLeftRightPadding, mButtonTopBottomPadding,
                    mButtonLeftRightPadding, mButtonTopBottomPadding);

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.textView.setText(mButtonTitles[position]);

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
            if(mStyle == STYLE_ACTION_SHEET || mButtonTitles.length != 2){
                //垂直
                if(mButtonTitles.length == 1 && mStyle == STYLE_ACTION_SHEET && !hasTopContent()){
                    holder.drawablePressed.setCornerRadius(mCornerRadius);
                    holder.drawable.setCornerRadius(mCornerRadius);
                }else {
                    if(position == 0 && !hasTopContent() && mStyle == STYLE_ACTION_SHEET){
                        holder.drawablePressed.setCornerRadius(mCornerRadius, 0, mCornerRadius, 0);
                        holder.drawable.setCornerRadius(mCornerRadius, 0, mCornerRadius, 0);
                    }else if(position == mButtonTitles.length - 1){
                        holder.drawablePressed.setCornerRadius(0, mCornerRadius, 0, mCornerRadius);
                        holder.drawable.setCornerRadius(0, mCornerRadius, 0, mCornerRadius);
                    }else {
                        holder.drawablePressed.setCornerRadius(0);
                        holder.drawable.setCornerRadius(0);
                    }
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

            textView = (TextView)itemView;
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
                    if(mStyle == STYLE_ACTION_SHEET || mButtonTitles.length != 2){
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
                if(mStyle == STYLE_ACTION_SHEET || mButtonTitles.length != 2){
                    //垂直排列
                    outRect.bottom = mDividerHeight;
                }else {
                    //水平
                    outRect.right = mDividerHeight;
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
