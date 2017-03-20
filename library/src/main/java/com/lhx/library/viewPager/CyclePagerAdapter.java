package com.lhx.library.viewPager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 循环轮播器
 */
public abstract class CyclePagerAdapter extends PagerAdapter {

    public static final String TAG = "PagerAdapter";

    //关联的viewPager
    private ViewPager mViewPager;

    //真实的数量
    private int mRealCount;

    //子视图数量，用于notify刷新
    private int mChildCount = 0;

    //需要移动到的位置
    private int mTargetPosition = -1;

    //当前显示出来的view
    private SparseArray<HashSet<View>> mVisibleViews = new SparseArray<>();

    //可重用的view
    private SparseArray<HashSet<View>> mReusedViews = new SparseArray<>();

    public CyclePagerAdapter(ViewPager viewPager) {
        this.mViewPager = viewPager;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(mRealCount > 1){
                    if(position == 0){
                        mTargetPosition = mRealCount;
                    }else if(position == mRealCount + 1){
                        mTargetPosition = 1;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                //当不是动画中改变当前显示view的位置
                if(state != ViewPager.SCROLL_STATE_SETTLING && mTargetPosition != -1){
                    mViewPager.setCurrentItem(mTargetPosition, false);
                    mTargetPosition = -1;
                }
            }
        });

        viewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
            @Override
            public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable
                    PagerAdapter newAdapter) {
                scrollToFirstPosition();
            }
        });
    }


    @Override
    public final int getCount() {
        mRealCount = getRealCount();

        //当只有一个view时，不需要循环
        return mRealCount > 1 ? mRealCount + 2 : mRealCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {

        int type = getViewType(position);

        //获取可重用的view
        HashSet<View> views = mReusedViews.get(type);
        View convertView = null;
        if(views != null && views.size() > 0){
            Iterator<View> iterator = views.iterator();
            convertView = iterator.next();
            views.remove(convertView);
        }

        Object object = instantiateItemForRealPosition(convertView, getRealPosition(position), type);
        if(object instanceof View){

            //加入可见队列
            views = mVisibleViews.get(type);
            if(views == null){
                views = new HashSet<>();
                mVisibleViews.put(type, views);
            }

            View view = (View)object;
            container.addView(view);
            views.add(view);
        }

        return object;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof View) {

            View view = (View)object;
            int type = getViewType(position);

            HashSet<View> views = mVisibleViews.get(type);
            views.remove(view);

            views = mReusedViews.get(type);
            if(views == null){
                views = new HashSet<>();
                mReusedViews.put(type, views);
            }
            views.add(view);
            container.removeView(view);
            destroyItemForRealPosition(view, getRealPosition(position), type, object);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
        scrollToFirstPosition();
    }

    @Override
    public int getItemPosition(Object object) {
        if ( mChildCount > 0) {
            mChildCount --;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    //移动到第一个位置
    private void scrollToFirstPosition(){
        if(mRealCount > 1){
            mViewPager.setCurrentItem(1, false);
            mTargetPosition = -1;
        }
    }

    /**
     * 通过viewPager 位置获取真实的数据源位置
     * @param position viewPager 位置
     * @return 真实的数据源位置
     */
    private int getRealPosition(int position){
        if(mRealCount <= 1){
            return position;
        }else {
            if(position == 0){
                return mRealCount - 1;
            }else if(position == mRealCount + 1){
                return 0;
            }else {
                return  position - 1;
            }
        }
    }

    /**
     * 获取真实的数量
     * @return 真实的数量
     */
    public abstract int getRealCount();

    /**
     * 显示真实的页面
     * @param convertView 和listView中的一样 如果不为空，则该视图可重用
     * @param position {@link #instantiateItem(ViewGroup, int)}
     * @param viewType 和listView中的一样 视图类型
     * @return {@link #instantiateItem(ViewGroup, int)}
     */
    public abstract Object instantiateItemForRealPosition(View convertView, int position, int viewType);

    /**
     * 销毁真实的页面
     * @param convertView 需要销毁的view，该view会进入重用队列
     * @param position {@link #destroyItem(ViewGroup, int, Object)}
     * @param viewType 和listView中的一样 视图类型
     * @param object {@link #destroyItem(ViewGroup, int, Object)}
     */
    public void destroyItemForRealPosition(View convertView, int position, int viewType, Object object){

    }

    /**
     * 获取view类型，用于识别重用
     * @param position 视图位置
     * @return view类型
     */
    public int getViewType(int position){
        return 0;
    }
}
