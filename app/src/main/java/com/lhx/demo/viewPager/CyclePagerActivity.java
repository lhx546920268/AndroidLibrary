package com.lhx.demo.viewPager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.demo.R;
import com.lhx.library.viewPager.CyclePagerAdapter;
import com.lhx.library.viewPager.PageControl;

/**
 * 循环播放 demo
 */

public class CyclePagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    CyclePagerAdapter adapter;
    int count = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cycle_pager_activity);
        viewPager = (ViewPager)findViewById(R.id.view_pager);


        PageControl pageControl = (PageControl)findViewById(R.id.page_control);

        pageControl.setNormalColor(Color.CYAN);
        pageControl.setSelectedColor(Color.YELLOW);
        pageControl.setPointSizeDip(10);
        pageControl.setPointIntervalDip(5);
        pageControl.setViewPager(viewPager);

                findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count == 3 ? 20 : 3;
                viewPager.getAdapter().notifyDataSetChanged();
            }
        });

        adapter = new CyclePagerAdapter(viewPager) {
            @Override
            public int getRealCount() {
                return count;
            }


            @Override
            public Object instantiateItemForRealPosition(View convertView, int position, int viewType) {

                if(convertView == null){
                    convertView = new View(getBaseContext());
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    convertView.setLayoutParams(layoutParams);
                }

                switch (position){
                    case 0 :
                        convertView.setBackgroundColor(Color.RED);
                        break;
                    case 1 :
                        convertView.setBackgroundColor(Color.BLUE);
                        break;
                    case 2 :
                        convertView.setBackgroundColor(Color.YELLOW);
                        break;
                    case 3 :
                        convertView.setBackgroundColor(Color.CYAN);
                        break;
                    case 4 :
                        convertView.setBackgroundColor(Color.GRAY);
                        break;
                }

                return convertView;
            }
        };
        adapter.setShouldAutoPlay(true);

        viewPager.setAdapter(adapter);


    }


    @Override
    protected void onDestroy() {

        adapter.setShouldAutoPlay(false);
        super.onDestroy();
    }
}
