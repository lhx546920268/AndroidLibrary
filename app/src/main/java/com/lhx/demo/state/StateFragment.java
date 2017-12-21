package com.lhx.demo.state;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;

/**
 *  状态
 */

public class StateFragment extends AppBaseFragment {


    @Override
    protected void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setContentView(R.layout.state_fragment);

        final TextView textView = findViewById(R.id.state);

        int[][] states = new int[3][];

        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{android.R.attr.state_pressed};
        states[2] = new int[]{};

        int[] colors = new int[]{getColor(R.color.red), getColor(R.color.red), getColor(R.color.black)};

        textView.setTextColor(new ColorStateList(states, colors));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setSelected(!textView.isSelected());
            }
        });
    }
}
