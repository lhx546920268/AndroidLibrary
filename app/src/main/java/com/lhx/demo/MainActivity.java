package com.lhx.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import com.lhx.demo.dialog.BaseDialogFragment;
import com.lhx.demo.dialog.DialogFragment;
import com.lhx.demo.drawable.CornerDrawableFragment;
import com.lhx.demo.http.HttpFragment;
import com.lhx.demo.listview.StickListViewFragment;
import com.lhx.demo.loadmore.ListViewLoadMoreFragment;
import com.lhx.demo.loadmore.RecyclerViewLoadMoreFragment;
import com.lhx.demo.nest.NestScrollViewFragment;
import com.lhx.demo.recyclerview.CycleRecyclerViewFragment;
import com.lhx.demo.recyclerview.TetrisFragment;
import com.lhx.demo.refresh.RefreshActivity;
import com.lhx.demo.state.StateFragment;
import com.lhx.demo.tab.TabLayoutFragment;
import com.lhx.demo.text.TextBoundsFragment;
import com.lhx.demo.toast.ToastFragment;
import com.lhx.demo.viewPager.CyclePagerActivity;
import com.lhx.demo.widget.popover.PopoverFragment;
import com.lhx.library.activity.AppBaseActivity;
import com.lhx.library.fragment.WebFragment;
import com.lhx.library.media.fragment.MediaFragment;
import com.lhx.library.scan.fragment.ScanFragment;
import com.lhx.library.viewHoler.ViewHolder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> infos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        infos.add("cycleViewPager(无限轮播viewPager)");
        infos.add("refreshControl(下拉刷新、上拉加载)");
        infos.add("alertController(弹窗 dialog)");
        infos.add("cornerDrawable(圆角 corner)");
        infos.add("TabLayout(横向条形菜单)");
        infos.add("HttpAsyncTask");
        infos.add("Toast");
        infos.add("text bounds(字体范围)");
        infos.add("loadMore (加载更多 listView)");
        infos.add("loadMore (加载更多 recyclerView)");
        infos.add("TetrisLayout (recyclerView)");
        infos.add("ListView stick");
        infos.add("RecyclerView stick");
        infos.add("Base Dialog");
        infos.add("Media");
        infos.add("State");
        infos.add("扫一扫");
        infos.add("气泡");
        infos.add("web");
        infos.add("嵌套滚动");

        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return infos.size();
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
                    convertView = View.inflate(getBaseContext(), R.layout.main_list_item, null);
                }

                TextView textView = ViewHolder.get(convertView, R.id.text);
                textView.setText(infos.get(position));
//                textView.setBackgroundColor(ColorUtil.whitePercentColor(0.9f, 1.0f));

                return convertView;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class clazz = null;
                switch (position){
                    case 0 :
                        clazz = CyclePagerActivity.class;
                        break;
                    case 1 :
                        clazz = RefreshActivity.class;
                        break;
                    case 2 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, DialogFragment.class));
                        break;
                    case 3 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this,
                                CornerDrawableFragment.class));
                        break;
                    case 4 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, TabLayoutFragment.class));
                        break;
                    case 5 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, HttpFragment.class));
                        break;
                    case 6 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, ToastFragment.class));
                        break;
                    case 7 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, TextBoundsFragment
                                .class));
                        break;
                    case 8 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, ListViewLoadMoreFragment
                                .class));
                        break;
                    case 9 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this,
                                RecyclerViewLoadMoreFragment.class));
                        break;
                    case 10 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, TetrisFragment.class));
                        break;
                    case 11 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, StickListViewFragment
                                .class));
                        break;
                    case 12 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this,
                                CycleRecyclerViewFragment.class));
                        break;
                    case 13 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this,
                                BaseDialogFragment.class));
                        break;
                    case 14 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, MediaFragment.class));
                        break;
                    case 15 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, StateFragment.class));
                        break;
                    case 16 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, ScanFragment.class));
                        break;
                    case 17 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, PopoverFragment.class));
                        break;
                    case 18 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, WebFragment.class)
                                .putExtra(WebFragment.WEB_URL, "http://dev.yijiago.com:8102/h5/#/channel-shenghuoquan"));
                        break;
                    case 19 :
                        startActivity(AppBaseActivity.getIntentWithFragment(MainActivity.this, NestScrollViewFragment.class));
                        break;
                }

                if(clazz != null){
                    startActivity(new Intent(MainActivity.this, clazz));
                }
            }
        });
    }
}
