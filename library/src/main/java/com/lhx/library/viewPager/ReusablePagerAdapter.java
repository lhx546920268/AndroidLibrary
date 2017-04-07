package com.lhx.library.viewPager;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 可复用item的PagerAdapter
 */

public abstract class ReusablePagerAdapter extends PagerAdapter implements ViewPager.OnAdapterChangeListener {

    public static final String TAG = "ReusablePagerAdapter";

    //关联的viewPager
    protected ViewPager mViewPager;

    //子视图数量，用于notify刷新
    protected int mChildCount = 0;

    //当前显示出来的view
    private SparseArray<HashSet<View>> mVisibleViews = new SparseArray<>();

    //可重用的view
    private SparseArray<HashSet<View>> mReusedViews = new SparseArray<>();

    //当前显示出来的view中的 subview
    private SparseArray<HashSet<View>> mVisibleSubviews = new SparseArray<>();

    //可重用的view中的 subview
    private SparseArray<HashSet<View>> mReusedSubviews = new SparseArray<>();

    public ReusablePagerAdapter(ViewPager viewPager) {
        this.mViewPager = viewPager;

        viewPager.addOnAdapterChangeListener(this);
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable
            PagerAdapter newAdapter) {
        //数据源改变时回到第一个页
        scrollToFirstPosition();
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

        int realPosition = getRealPosition(position);
        Object object = instantiateItemForRealPosition(convertView, realPosition, type);
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

            //获取可重用的子视图
            if(view instanceof ViewGroup) {
                int count = numberOfSubviewInPage(realPosition);
                ViewGroup viewGroup = (ViewGroup)view;
                for(int i = 0;i < count;i ++){
                    type = getSubviewType(realPosition, i);

                    //获取重用的subview
                    View subConvertView = null;
                    HashSet<View> subviews = mReusedSubviews.get(type);
                    if(subviews != null && subviews.size() > 0){
                        Iterator<View> iterator = subviews.iterator();
                        subConvertView = iterator.next();
                        subviews.remove(subConvertView);
                    }

                    View subview = getSubview(subConvertView, realPosition, i, type);
                    if(subview == null){
                        throw new NullPointerException(TAG + " getSubview 不能返回 null");
                    }

                    //加入可见队列
                    subviews = mVisibleSubviews.get(type);
                    if(subviews == null){
                        subviews = new HashSet<>();
                        mVisibleSubviews.put(type, subviews);
                    }
                    viewGroup.addView(subview);
                    subviews.add(subview);
                }
            }
        }

        return object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof View) {

            View view = (View)object;
            int type = getViewType(position);

            //移出可见队列
            HashSet<View> views = mVisibleViews.get(type);
            views.remove(view);

            //加入重用队列
            views = mReusedViews.get(type);
            if(views == null){
                views = new HashSet<>();
                mReusedViews.put(type, views);
            }
            views.add(view);
            container.removeView(view);

            int realPosition = getRealPosition(position);
            destroyItemForRealPosition(view, realPosition, type, object);

            //子视图
            if(view instanceof ViewGroup){
                ViewGroup viewGroup = (ViewGroup)view;
                int count = numberOfSubviewInPage(realPosition);
                for(int i = 0;i < count;i ++){
                    type = getSubviewType(realPosition, i);
                    View subview = viewGroup.getChildAt(i);

                    //移出可见队列
                    HashSet<View> subviews = mVisibleSubviews.get(type);
                    subviews.remove(view);

                    //加入重用队列
                    subviews = mReusedSubviews.get(type);
                    if(subviews == null){
                        subviews = new HashSet<>();
                        mReusedSubviews.put(type, subviews);
                    }
                    subviews.add(subview);
                }
                viewGroup.removeAllViews();
            }
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
    protected void scrollToFirstPosition(){
        if(getCount() > 0){
            mViewPager.setCurrentItem(0, false);
        }
    }

    /**
     * 通过viewPager 位置获取真实的数据源位置
     * @param position viewPager 位置
     * @return 真实的数据源位置
     */
    public int getRealPosition(int position){
        return position;
    }


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

    /**
     * 获取subview类型，用于识别重用
     * @param position 当前页位置
     * @param subviewPosition 子视图在当前页的位置
     * @return subview类型
     */
    public int getSubviewType(int position, int subviewPosition){
        return 0;
    }

    /**
     * 当前页有多少个subview
     * @param position 当前页
     * @return 子视图数量
     */
    public int numberOfSubviewInPage(int position){
        return 0;
    }

    /**
     * 显示子视图
     * @param convertView 和listView中的一样 如果不为空，则该视图可重用
     * @param position 当前页位置
     * @param subviewPosition 子视图在当前页的位置
     * @param subviewType 子视图类型
     * @return 子视图
     */
    public View getSubview(View convertView, int position, int subviewPosition, int subviewType){
        return null;
    }

    /**
     * 销毁子视图
     * @param convertView 需要销毁的view，该view会进入重用队列
     * @param position 当前页位置
     * @param subviewPosition 子视图在当前页的位置
     * @param subviewType 子视图类型
     */
    public void destorySubview(View convertView, int position, int subviewPosition, int subviewType){

    }

}