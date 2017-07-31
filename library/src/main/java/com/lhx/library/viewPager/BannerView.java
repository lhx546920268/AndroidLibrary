package com.lhx.library.viewPager;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lhx.library.util.SizeUtil;

/**
 * 轮播广告视图
 */

public class BannerView extends FrameLayout {

    ///
    ViewPager mViewPager;
    CyclePagerAdapter mAdapter;

    //点
    PageControl mPageControl;

    //回调
    BannerHandler mBannerHandler;

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(@NonNull final Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(!isInEditMode()){

            mViewPager = new ViewPager(context);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mViewPager.setLayoutParams(params);
            addView(mViewPager);

            mPageControl = new PageControl(context);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            params.bottomMargin = SizeUtil.pxFormDip(10, context);
            mPageControl.setLayoutParams(params);
            addView(mPageControl);

            mAdapter = new CyclePagerAdapter(mViewPager) {
                @Override
                public Object instantiateItemForRealPosition(View convertView, int position, int viewType) {
                    if(convertView == null){
                        ImageView imageView = new ImageView(context);
                        convertView = imageView;
                        imageView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBannerHandler.onItemClick((int)v.getTag());
                            }
                        });
                    }

                    convertView.setTag(position);
                    mBannerHandler.configure((ImageView)convertView, position);

                    return convertView;
                }

                @Override
                public int getRealCount() {
                    return mBannerHandler != null ? mBannerHandler.getCount() : 0;
                }
            };
        }
    }

    public PageControl getPageControl() {
        return mPageControl;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public void setBannerHandler(BannerHandler bannerHandler){
        if(mBannerHandler != bannerHandler){
            mBannerHandler = bannerHandler;
            if(mViewPager.getAdapter() == null){
                mViewPager.setAdapter(mAdapter);
            }else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    //回调
    public interface BannerHandler{

        //广告图数量
        int getCount();

        //配置广告图
        void configure(ImageView imageView, int position);

        //点击
        void onItemClick(int position);
    }
}
