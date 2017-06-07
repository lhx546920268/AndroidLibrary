package com.lhx.demo.toast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.demo.R;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.util.ToastUtil;

/**
 *  toast
 */
public class ToastFragment extends AppBaseFragment implements View.OnClickListener {

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setContentView(inflater.inflate(R.layout.toast_fragment, null));

        findViewById(R.id.icon_text).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.icon_text :
                ToastUtil.alert(mContext, "a toast", R.mipmap.checkboxmark);
                break;
        }
    }
}
