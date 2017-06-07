package com.lhx.demo.drawable;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.fragment.AppBaseFragment;

/**
 * 圆角
 */

public class CornerDrawableFragment extends AppBaseFragment {

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setContentView(inflater.inflate(R.layout.corner_drawable_fragment, null));

        TextView textView = findViewById(R.id.corner_text);

        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.setBorderColor(Color.RED);
        drawable.setBorderWidth(10);
        drawable.setShouldAbsoluteCircle(true);
        drawable.setBackgroundColor(Color.BLUE);
        drawable.attatchView(textView);

        textView = findViewById(R.id.corner_text1);

        drawable = new CornerBorderDrawable();
        drawable.setBorderColor(Color.RED);
        drawable.setBorderWidth(10);
        drawable.setCornerRadius(30);
        drawable.setBackgroundColor(Color.YELLOW);
        drawable.attatchView(textView);

        textView = findViewById(R.id.corner_text2);

        drawable = new CornerBorderDrawable();
        drawable.setBorderColor(Color.RED);
        drawable.setBorderWidth(10);
        drawable.setRightBottomCornerRadius(130);
        drawable.setLeftTopCornerRadius(130);
        drawable.setBackgroundColor(Color.CYAN);
        drawable.attatchView(textView);
    }

    @Override
    public boolean showNavigationBar() {
        return false;
    }
}
