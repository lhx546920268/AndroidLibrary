package com.lhx.library.listView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自适应高度
 */

public class SeaListView extends ListView {

    public SeaListView(Context context) {
        super(context);
    }

    public SeaListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeaListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
