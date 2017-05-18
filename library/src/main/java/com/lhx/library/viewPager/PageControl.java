package com.lhx.library.viewPager;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.lhx.library.drawable.CornerBorderDrawable;

import java.util.ArrayList;


/**
 * 轮播广告点
 */

public class PageControl extends LinearLayout {

//    显示的点
    private ArrayList<PageControlPoint> points = new ArrayList<>();

    private int curPage = 0;

//    只有一个点时是否隐藏
    private boolean hideForSingle = false;

    //关联的viewPager
    private ViewPager viewPager;

    //点大小
    private int pointSizeDip = 5;
    
    //点间隔
    private int pointIntervalDip = 5;

    //点颜色
    private int normalColor = Color.GRAY;

    //点高亮颜色
    private int selectedColor = Color.RED;

    public PageControl(Context context) {
        this(context, null);
    }

    public PageControl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(HORIZONTAL);
    }

    //    设置当前选中的
    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {

        if(curPage < points.size()){
            int previousPage = this.curPage;
            if(previousPage < points.size() && previousPage >= 0){
                PageControlPoint point = points.get(previousPage);
                point.drawable.setBackgroundColor(normalColor);
            }
            this.curPage = curPage;
            PageControlPoint point = points.get(curPage);
            point.drawable.setBackgroundColor(selectedColor);
        }
    }

//    设置page总数
    public void setPageCount(int pageCount){
        if(pageCount != points.size()){
            curPage = -1;
            points.clear();
            removeAllViews();

//            创建点
            Context context = getContext();
            float scale = context.getResources().getDisplayMetrics().density;
            int size = (int) (scale * pointSizeDip);// XP与DP转换，适应不同分辨率
            int margin = (int) (scale * pointIntervalDip);


            for(int i = 0;i < pageCount;i ++){
                PageControlPoint point = new PageControlPoint(context);
                LayoutParams layoutParams = new LayoutParams(size, size);
                layoutParams.setMargins(0, 0, margin, 0);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                point.setLayoutParams(layoutParams);
                point.drawable.setCornerRadius(size / 2);
                point.drawable.setBackgroundColor(normalColor);

                addView(point);
                points.add(point);
            }
            setCurPage(0);

            if(hideForSingle && pageCount <= 1){
                setVisibility(INVISIBLE);
            }else {
                setVisibility(VISIBLE);
            }
        }
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        if(this.selectedColor != selectedColor){
            this.selectedColor = selectedColor;
            if(curPage < points.size()){
                PageControlPoint point = points.get(curPage);
                point.drawable.setBackgroundColor(selectedColor);
            }
        }
    }

    public int getNormalColor() {
        return normalColor;
    }

    public void setNormalColor(int normalColor) {
        if(normalColor != this.normalColor){
            this.normalColor = normalColor;
            for(int i = 0;i < points.size();i ++){
                if(i == curPage)
                    continue;
                PageControlPoint point = points.get(i);
                point.drawable.setBackgroundColor(normalColor);
            }
        }
    }

    public int getPointSizeDip() {
        return pointSizeDip;
    }

    public void setPointSizeDip(int pointSizeDip) {
       if(pointSizeDip != this.pointSizeDip){
           this.pointSizeDip = pointSizeDip;

           //重新设置点大小
           Context context = getContext();
           float scale = context.getResources().getDisplayMetrics().density;
           int size = (int) (scale * pointSizeDip);// XP与DP转换，适应不同分辨率
           int margin = (int) (scale * pointIntervalDip);


           for(int i = 0;i < points.size();i ++){
               PageControlPoint point = points.get(i);
               LayoutParams layoutParams = (LayoutParams)point.getLayoutParams();
               layoutParams.setMargins(0, 0, margin, 0);
               layoutParams.width = size;
               layoutParams.height = size;
               point.setLayoutParams(layoutParams);
               point.drawable.setCornerRadius(size / 2);
           }
       }
    }

    public int getPointIntervalDip() {
        return pointIntervalDip;
    }

    public void setPointIntervalDip(int pointIntervalDip) {
        if(pointIntervalDip != this.pointIntervalDip){
            this.pointIntervalDip = pointIntervalDip;

            //重新设置点间隔
            Context context = getContext();
            float scale = context.getResources().getDisplayMetrics().density;
            int margin = (int) (scale * pointIntervalDip);


            for(int i = 0;i < points.size();i ++){
                PageControlPoint point = points.get(i);
                LayoutParams layoutParams = (LayoutParams)point.getLayoutParams();
                layoutParams.setMargins(0, 0, margin, 0);
                point.setLayoutParams(layoutParams);
            }
        }
    }

    public boolean isHideForSingle() {
        return hideForSingle;
    }

    public void setHideForSingle(boolean hideForSingle) {
        if(hideForSingle != this.hideForSingle){
            this.hideForSingle = hideForSingle;
            if(hideForSingle && points.size() <= 1){
                setVisibility(INVISIBLE);
            }else {
                setVisibility(VISIBLE);
            }
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    //如果已关联了viewPager，则不需要额外设置pageCount
    public void setViewPager(final ViewPager viewPager) {
        this.viewPager = viewPager;
        if(viewPager != null){

            //监听viewPager数据变化
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if(viewPager.getAdapter() instanceof CyclePagerAdapter){
                        CyclePagerAdapter adapter = (CyclePagerAdapter)viewPager.getAdapter();
                        setCurPage(adapter.getRealPosition(position));
                    }else {
                        setCurPage(position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            viewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
                @Override
                public void onAdapterChanged(@NonNull final ViewPager viewPager, @Nullable PagerAdapter oldAdapter,
                                             @Nullable final PagerAdapter newAdapter) {
                    if(newAdapter != null){

                        //第一次设置
                        if(oldAdapter == null){
                            setPageCount(newAdapter);
                        }
                        newAdapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                setPageCount(newAdapter);
                            }

                            @Override
                            public void onInvalidated() {
                                super.onInvalidated();
                                setPageCount(0);
                            }
                        });
                    }
                }
            });
        }
    }

    //从adapter中获取数量
    private void setPageCount(PagerAdapter pagerAdapter){
        if(pagerAdapter instanceof CyclePagerAdapter){
            CyclePagerAdapter adapter = (CyclePagerAdapter)pagerAdapter;
            setPageCount(adapter.getRealCount());
        }else {
            setPageCount(pagerAdapter.getCount());
        }
    }

    class PageControlPoint extends View{

        //圆角
        CornerBorderDrawable drawable;

        public PageControlPoint(Context context) {
            super(context);

            drawable = new CornerBorderDrawable();
            drawable.attatchView(this, false);
        }
    }
}
