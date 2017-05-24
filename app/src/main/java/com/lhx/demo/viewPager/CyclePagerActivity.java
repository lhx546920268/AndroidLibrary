package com.lhx.demo.viewPager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    boolean show = false;

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

//                    LinearLayout linearLayout = new LinearLayout(getBaseContext());
//                    linearLayout.setWeightSum(4);
//                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
//                            .MATCH_PARENT,
//                            100);
//                    linearLayout.setLayoutParams(layoutParams);
//                    convertView = linearLayout;
                    convertView = View.inflate(getBaseContext(), R.layout.page_item, null);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(720, 1280);
                    convertView.setLayoutParams(params);

                    final GridView gridView = (GridView)convertView.findViewById(R.id.grid_view);

                    final BaseAdapter baseAdapter = new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return 20;
                        }

                        @Override
                        public Object getItem(int position) {
                            return null;
                        }

                        @Override
                        public long getItemId(int position) {
                            return 0;
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            if(convertView == null){
                                TextView textView = new TextView(getBaseContext());

                                convertView = textView;
                            }

                            int page = (int)gridView.getTag();
                            TextView textView = (TextView)convertView;
                            textView.setText("第" + position + "个，在第" + page + "页");
                            if(show){
                                textView.setBackgroundColor(Color.LTGRAY);
                            }else {
                                textView.setBackgroundColor(Color.GRAY);
                            }

                            return textView;
                        }
                    };
                    gridView.setAdapter(baseAdapter);

                    gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                            show = !show;
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                    });
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

                Log.d("tag", "position = " + position);

                GridView gridView = (GridView)convertView.findViewById(R.id.grid_view);
                BaseAdapter baseAdapter = (BaseAdapter)gridView.getAdapter();
                gridView.setTag(position);
                baseAdapter.notifyDataSetChanged();

                return convertView;
            }

            @Override
            public int numberOfSubviewInPage(int position) {
                return 4;
            }

            @Override
            public View getSubview(View convertView, int position, int subviewPosition, int subviewType) {
                if(convertView == null){
                    TextView textView = new TextView(getBaseContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                            50);
                    layoutParams.weight = 1;
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    layoutParams.setMargins(10, 0, 10, 0);
                    textView.setLayoutParams(layoutParams);

                    convertView = textView;
                }

                TextView textView = (TextView)convertView;
                textView.setText("第" + subviewPosition + "个");
                if(show){
                    textView.setBackgroundColor(Color.LTGRAY);
                }else {
                    textView.setBackgroundColor(Color.GRAY);
                }

                return textView;
            }
        };

        adapter.setShouldAutoPlay(false);

        viewPager.setAdapter(adapter);


    }


    @Override
    protected void onDestroy() {

        adapter.setShouldAutoPlay(false);
        super.onDestroy();
    }
}
