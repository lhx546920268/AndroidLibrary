package com.lhx.library.bar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhx.library.App;
import com.lhx.library.R;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.widget.OnSingleClickListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义导航栏
 */
public class NavigationBar extends RelativeLayout {

    ///按钮位置 左边
    public static final int NAVIGATIONBAR_ITEM_POSITION_LEFT = 0;

    ///按钮位置 右边
    public static final int NAVIGATIONBAR_ITEM_POSITION_RIGHT = 1;

    @IntDef({NAVIGATIONBAR_ITEM_POSITION_LEFT, NAVIGATIONBAR_ITEM_POSITION_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Position{}

    ///左右边距
    public static int LEFT_RIGHT_MARGIN = 20;

    ///按钮、标题之间的间距
    public static int VIEW_INTERVAL = 20;

    ///标题
    private TextView title_textView;

    ///阴影
    private View shadow_line;

    ///标题视图
    private View title_view;

    ///左边视图
    private View left_navigationBarItem;

    ///右边视图
    private View right_navigationBarItem;

    ///是否需要重新布局
    private boolean needLayout = false;

    ///系统是否已布局完成
    private boolean systemDidLayout = false;

    public NavigationBar(Context context) {
        this(context,null);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (!isInEditMode()) {

            setBackgroundColor(App.NavigationBarBackgroundColor);
            ///标题
            title_textView = new TextView(context);
            title_textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, App.NavigationBarTitleTextSize);
            title_textView.setTextColor(App.NavigationBarTintColor);
            title_textView.setLines(1);
            title_textView.setEllipsize(TextUtils.TruncateAt.END);
            title_textView.setBackgroundColor(Color.TRANSPARENT);
            title_textView.setGravity(Gravity.CENTER);

            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            title_textView.setLayoutParams(layoutParams);

            title_view = title_textView;
            title_view.setPadding(LEFT_RIGHT_MARGIN, 0, LEFT_RIGHT_MARGIN, 0);
            this.addView(title_view);

            ///阴影
            shadow_line = new View(context);
            shadow_line.setBackgroundColor(App.NavigationBarShadowColor);
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, SizeUtil.pxFormDip(0.5f, context));
            layoutParams.addRule(ALIGN_PARENT_BOTTOM);
            this.addView(shadow_line, layoutParams);

            ///添加布局完成监听,只要这样获取视图宽高才有效
            this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
                @Override
                public void onGlobalLayout() {

                    systemDidLayout = true;
                    layoutSubviews();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                        NavigationBar.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    else {

                        NavigationBar.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
    }

    ///设置标题
    public void setTitle(String title){
        title_textView.setText(title);
    }

    ///设置阴影颜色
    public void  setShadowColor(@ColorInt int shadowColor){

        shadow_line.setBackgroundColor(shadowColor);
    }

    ///设置标题视图
    public void  setTitleView(View titleView){

        needLayout = true;
        if(title_view != null) {

            title_view.setVisibility(View.GONE);
        }

        title_view = titleView;
        if(title_view != null) {

            RelativeLayout.LayoutParams layoutParams;
            if(titleView.getLayoutParams() != null && title_view.getLayoutParams() instanceof RelativeLayout.LayoutParams){
                layoutParams = (RelativeLayout.LayoutParams)title_view.getLayoutParams();
            }else {
                layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
            }

            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            title_view.setLayoutParams(layoutParams);

            this.addView(title_view);
        }else {

            title_textView.setVisibility(View.VISIBLE);
            title_view = title_textView;
        }
    }


    ///设置左边视图
    public void setLeftNavigationBarItem(View leftNavigationBarItem){

        needLayout = true;
        if(left_navigationBarItem != null) {

            removeView(leftNavigationBarItem);
        }

        left_navigationBarItem = leftNavigationBarItem;

        if(left_navigationBarItem != null) {

            left_navigationBarItem.setId(R.id.left_navigation_item);

            ///布局item
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.leftMargin = LEFT_RIGHT_MARGIN;
            this.addView(left_navigationBarItem, layoutParams);
            left_navigationBarItem.setPadding(left_navigationBarItem.getPaddingLeft(), left_navigationBarItem.getPaddingTop(),
                    leftNavigationBarItem.getPaddingRight() + VIEW_INTERVAL, left_navigationBarItem.getPaddingBottom());
        }
    }

    ///设置右边视图
    public void setRightNavigationBarItem(View rightNavigationBarItem){

        needLayout = true;
        if(right_navigationBarItem != null) {

            removeView(rightNavigationBarItem);
        }

        right_navigationBarItem = rightNavigationBarItem;
        if(right_navigationBarItem != null) {

            right_navigationBarItem.setId(R.id.right_navigation_item);

            ///布局item
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.rightMargin = LEFT_RIGHT_MARGIN;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            this.addView(right_navigationBarItem, layoutParams);

            right_navigationBarItem.setPadding(right_navigationBarItem.getPaddingLeft() + VIEW_INTERVAL, right_navigationBarItem
                    .getPaddingTop(),
                    right_navigationBarItem.getPaddingRight(), right_navigationBarItem.getPaddingBottom());
        }
    }

    //显示返回按钮
    public TextView setShowBackButton(boolean show) {
        if (show) {

            Drawable drawable = null;
            int icon = App.NavigationBarBackButtonIcon;
            String title = null;
            if (icon != 0) {
                drawable = ContextCompat.getDrawable(getContext(), icon);
            }
            if (drawable == null) {
                title = App.NavigationBarBackButtonTitle;
            }

            TextView textView = setNavigationItem(title, null, NavigationBar
                    .NAVIGATIONBAR_ITEM_POSITION_LEFT);

            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView.setCompoundDrawables(drawable, null, null, null);
            }

            return textView;
        } else {
            setLeftNavigationBarItem(null);
        }
        return null;
    }


    /** 设置导航栏按钮
     *
     * @param title 标题
     * @param drawable 图标,显示在标题上面
     * @param position 位置,使用 NAVIGATIONBAR_ITEM_POSITION_LEFT,NAVIGATIONBAR_ITEM_POSITION_RIGHT
     * @return 新创建的按钮
     */
    public TextView setNavigationItem(String title, Drawable drawable, @Position int position){

        TextView textView = new TextView(this.getContext());

        if(!TextUtils.isEmpty(title)){
            textView.setText(title);
        }

        textView.setGravity(Gravity.CENTER);
        textView.setPadding(SizeUtil.pxFormDip(10, getContext()), 0, SizeUtil.pxFormDip(10, getContext()), 0);
        textView.setTextColor(App.NavigationBarTintColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, App.NavigatonBarButtonTextSize);
        textView.setBackgroundColor(Color.TRANSPARENT);

        if(drawable != null){
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }

        switch (position){
            case NAVIGATIONBAR_ITEM_POSITION_LEFT :
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                if(drawable != null){
                    textView.setCompoundDrawables(drawable, null, null, null);
                }
                setLeftNavigationBarItem(textView);
                break;
            case NAVIGATIONBAR_ITEM_POSITION_RIGHT :
                textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                if(drawable != null){
                    textView.setCompoundDrawables(null, null, drawable, null);
                }
                setRightNavigationBarItem(textView);
                break;
            default :
                break;
        }

        return textView;
    }

    ///重新布局
    private void layoutSubviews(){

        if(!systemDidLayout || !needLayout)
            return;
        if(left_navigationBarItem != null || right_navigationBarItem != null){

            int leftItemWidth = 0;
            int rightItemWidth = 0;
            int left = LEFT_RIGHT_MARGIN;
            int right = LEFT_RIGHT_MARGIN;

            if(left_navigationBarItem != null){

                leftItemWidth = left_navigationBarItem.getWidth();
                left = 0;
            }

            if(right_navigationBarItem != null){

                rightItemWidth = right_navigationBarItem.getWidth();
                right = 0;
            }

            int maxItemWidth = Math.max(leftItemWidth, rightItemWidth);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) title_view.getLayoutParams();

            if(left_navigationBarItem == null && left_navigationBarItem == null)
            {
                layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            }
            else
            {
                if(right_navigationBarItem != null){

                    layoutParams.addRule(RelativeLayout.LEFT_OF, right_navigationBarItem.getId());
                }

                if(left_navigationBarItem != null){

                    layoutParams.addRule(RelativeLayout.RIGHT_OF, left_navigationBarItem.getId());
                }

                if(left_navigationBarItem == null || right_navigationBarItem == null){

                    if(title_view.getWidth() <= this.getWidth() - maxItemWidth * 2 - LEFT_RIGHT_MARGIN){

                        layoutParams.width = this.getWidth() - maxItemWidth * 2 - LEFT_RIGHT_MARGIN;

                    }else {

                        layoutParams.width = this.getWidth() - leftItemWidth - rightItemWidth;
                    }
                }
            }

            title_view.setLayoutParams(layoutParams);
            title_view.setPadding(left, 0, right, 0);
        }
        else {

            title_view.setPadding(LEFT_RIGHT_MARGIN, 0, LEFT_RIGHT_MARGIN, 0);
        }
    }
}
