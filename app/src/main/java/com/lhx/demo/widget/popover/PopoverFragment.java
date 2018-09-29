package com.lhx.demo.widget.popover;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.popover.PopoverContainer;
import com.lhx.library.popover.PopoverLayout;
import com.lhx.library.widget.OnSingleClickListener;

//气泡弹窗
public class PopoverFragment extends AppBaseFragment {

    @Override
    protected void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setTitle("气泡弹窗");
        setContentView(R.layout.popover_fragment);
        findViewById(R.id.btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                Popover popover = new Popover(mContext);
                LinearLayout linearLayout = (LinearLayout)getContentView();
                linearLayout.addView(popover, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
    }

    private static class Popover extends PopoverContainer{

        public Popover(@NonNull Context context) {
            super(context);
        }

        public Popover(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public Popover(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @NonNull
        @Override
        public View getContentView(PopoverLayout parent) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(20);
            textView.setText("这是一个很大很大的气泡弹窗编译版本的问题，\n" +
                    "            module的编译版本（即你的主app）必须和library的一致才行，");
            textView.setTextColor(Color.BLACK);

            return textView;
        }
    }
}
