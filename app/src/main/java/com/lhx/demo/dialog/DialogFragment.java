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
        findViewById(R.id.sheet_btn1).setOnClickListener(this);
        findViewById(R.id.alert_btn1).setOnClickListener(this);
        findViewById(R.id.sheet_btn2).setOnClickListener(this);
        findViewById(R.id.alert_btn2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.alert_btn : {
                AlertController controller = new AlertController(mActivity, AlertController.STYLE_ALERT, "弹窗显示了",
                        "弹窗真的显示了", null, "确定", "取消");
                controller.show();
            }
                break;
            case R.id.sheet_btn : {
                AlertController controller = new AlertController(mActivity, AlertController.STYLE_ACTION_SHEET, "弹窗显示了",
                        "弹窗真的显示了", null, "确定", "删除");
                controller.show();
            }
                break;
            case R.id.sheet_btn1 :{
                AlertController controller = AlertController.showActionSheet(mActivity, "弹窗显示了", "确定", "删除");
                controller.show();
            }
            break;

            case R.id.alert_btn1 :{
                AlertController controller = AlertController.showAlert(mActivity, "弹窗显示了", "确定");
                controller.show();
            }
            break;

            case R.id.sheet_btn2 :{
                AlertController controller = AlertController.showActionSheet(mActivity, null, "确定", "删除");
                controller.setAlertUIHander(new AlertController.AlertUIHandler() {
                    @Override
                    public void onDismiss(AlertController controller) {

                    }

                    @Override
                    public boolean shouldDestructive(AlertController controller, int index) {
                        return index == 1;
                    }

                    @Override
                    public boolean shouldEnable(AlertController controller, int index) {
                        return index == 0;
                    }
                });
                controller.show();
            }
            break;

            case R.id.alert_btn2 :{
                AlertController controller = AlertController.showActionSheet(mActivity, null, "弹窗真的显示了", "确定", "删除");
                controller.setAlertUIHander(new AlertController.AlertUIHandler() {
                    @Override
                    public void onDismiss(AlertController controller) {

                    }

                    @Override
                    public boolean shouldDestructive(AlertController controller, int index) {
                        return index == 1;
                    }

                    @Override
                    public boolean shouldEnable(AlertController controller, int index) {
                        return index == 0;
                    }
                });
                controller.show();
            }
            break;
        }
    }

    @Override
    public boolean showNavigationBar() {
        return false;
    }
}
