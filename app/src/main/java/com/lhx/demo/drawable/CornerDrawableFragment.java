package com.lhx.demo.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.drawable.CornerBorderDrawable;
import com.lhx.library.drawable.TriangleDrawable;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.image.ImageLoaderUtil;

import org.xml.sax.XMLReader;

/**
 * 圆角
 */

public class CornerDrawableFragment extends AppBaseFragment {

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setContentView(inflater.inflate(R.layout.corner_drawable_fragment, null));

        getContentContainer().setBackgroundColor(getColor(R.color.purple));
        setTitle("CornerBorderDrawable");
        setShowBackButton(true);

        TextView textView = findViewById(R.id.corner_text);

        final float height = textView.getPaint().getFontMetrics().descent - textView.getPaint().getFontMetrics().ascent;
        textView.setText(Html.fromHtml("<img src=\"http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg\"/>  " +
                "圆角圆角圆角圆角圆角圆角圆角圆角", new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {

                CornerBorderDrawable drawable = new CornerBorderDrawable();
                drawable.setBorderColor(Color.RED);
                drawable.setBorderWidth(10);
                drawable.setShouldAbsoluteCircle(true);
                drawable.setBackgroundColor(Color.BLUE);
                drawable.setIntrinsicWidth(Float.valueOf(height).intValue());
                drawable.setIntrinsicHeight(Float.valueOf(height).intValue());
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                return drawable;
            }
        }, null));

        CornerBorderDrawable drawable = new CornerBorderDrawable();
        drawable.setBorderColor(Color.RED);
        drawable.setBorderWidth(10);
        drawable.setShouldAbsoluteCircle(true);
        drawable.setBackgroundColor(Color.BLUE);
        drawable.setIntrinsicWidth(30);
        drawable.setIntrinsicHeight(20);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        textView.setCompoundDrawables(drawable, null, null, null);

//        textView = findViewById(R.id.corner_text1);
//
//        drawable = new CornerBorderDrawable();
//        drawable.setBorderColor(Color.RED);
//        drawable.setBorderWidth(10);
//        drawable.setCornerRadius(30);
//        drawable.setBackgroundColor(Color.YELLOW);
//        drawable.attatchView(textView);
//
//        textView = findViewById(R.id.corner_text2);
//
//        drawable = new CornerBorderDrawable();
//        drawable.setBorderColor(Color.RED);
//        drawable.setBorderWidth(10);
//        drawable.setRightBottomCornerRadius(130);
//        drawable.setLeftTopCornerRadius(130);
//        drawable.setBackgroundColor(Color.CYAN);
//        drawable.attatchView(textView);
//
//        ImageView imageView = findViewById(R.id.img_square);
//        CornerBorderDrawable borderDrawable = new CornerBorderDrawable(BitmapFactory.decodeResource(getResources(), R.drawable
//                .square));
//        borderDrawable.setShouldAbsoluteCircle(true);
//        borderDrawable.attatchView(imageView);
//
//        imageView = findViewById(R.id.img_rect);
//        ImageLoaderUtil.displayRoundImage(imageView,
//                "http://www.ibwang.cn/public/images/2f/e9/5e/0b79fa28a10b2cc42a0bd131ea56967fb3d46cf4.jpg?1510969763" +
//                        "#w", 10);
//
//        TriangleDrawable.setDrawable(findViewById(R.id.triangle_left), TriangleDrawable.DIRECTION_LEFT, Color.RED);
//        TriangleDrawable.setDrawable(findViewById(R.id.triangle_up), TriangleDrawable.DIRECTION_UP, Color.RED);
//        TriangleDrawable.setDrawable(findViewById(R.id.triangle_right), TriangleDrawable.DIRECTION_RIGHT, Color.RED);
//        TriangleDrawable.setDrawable(findViewById(R.id.triangle_down), TriangleDrawable.DIRECTION_DOWN, Color.RED);
//
//        TriangleDrawable triangleDrawable = new TriangleDrawable(TriangleDrawable.DIRECTION_LEFT, Color.CYAN);
//        triangleDrawable.setIntrinsicWidth(30);
//        triangleDrawable.setIntrinsicHeight(30);
//        imageView = findViewById(R.id.triangle);
//        imageView.setImageDrawable(triangleDrawable);
    }

    @Override
    public boolean showNavigationBar() {
        return true;
    }
}
