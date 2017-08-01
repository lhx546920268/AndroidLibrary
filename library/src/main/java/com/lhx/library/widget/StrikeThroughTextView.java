package com.lhx.library.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 中间横线
 */

public class StrikeThroughTextView extends AppCompatTextView {

    public StrikeThroughTextView(Context context) {
        this(context, null, 0);
    }

    public StrikeThroughTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrikeThroughTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }
}
