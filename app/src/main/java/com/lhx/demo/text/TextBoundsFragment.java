package com.lhx.demo.text;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.util.SizeUtil;
import com.lhx.library.util.StringUtil;

/**
 * 文本范围
 */

public class TextBoundsFragment extends AppBaseFragment implements View.OnClickListener {

    TextView textView;

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mContentView = inflater.inflate(R.layout.text_bounds_fragment, null);
        textView = findViewById(R.id.text);

        textView.setText("其实我不懂fewgeoijgeogijreg你写个xxxgeoi个哦我" +
                "价格哦该机构改革个欧冠我加热欧冠我今儿个个欧冠我就饿哦嘎嘎嘎人 干扰个我加热结果 ");
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.text:
                Log.d("TextBoundsFragment", "height = " + textView.getHeight());

                Log.d("TextBoundsFragment", "mesure height = " + StringUtil.mesureTextHeight(textView.getText(),
                        textView.getPaint(), textView.getWidth()));
                break;
        }
    }
}

