package com.lhx.library.gridView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 根据内容自动调整大小的gridView
 */

public class SeaGridView extends GridView {

    public SeaGridView(Context context) {
        super(context);
    }

    public SeaGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeaGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
