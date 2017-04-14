package com.lhx.demo.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhx.demo.R;
import com.lhx.library.dialog.AlertController;
import com.lhx.library.fragment.AppBaseFragment;

/**
 * 弹窗demo
 */

public class DialogFragment extends AppBaseFragment implements View.OnClickListener {

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mContentView = inflater.inflate(R.layout.dialog_fragment, null);

        findViewById(R.id.alert_btn).setOnClickListener(this);
        findViewById(R.id.sheet_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.alert_btn : {
                AlertController controller = new AlertController(mActivity, AlertController.STYLE_ALERT, "弹窗显示了",
                        "弹窗真的显示了", 0, "确定", "取消");
                controller.show();
            }
                break;
            case R.id.sheet_btn : {
                AlertController controller = new AlertController(mActivity, AlertController.STYLE_ACTION_SHEET, "弹窗显示了",
                        "弹窗真的显示了", 0, "确定", "取消");
                controller.show();
            }
                break;
        }
    }
}
