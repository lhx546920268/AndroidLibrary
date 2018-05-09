package com.lhx.demo.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lhx.demo.R;
import com.lhx.library.dialog.BaseDialog;
import com.lhx.library.fragment.AppBaseFragment;
import com.lhx.library.widget.AppBaseContainer;
import com.lhx.library.widget.OnSingleClickListener;

/**
 * 基础弹窗 demo
 */

public class BaseDialogFragment extends AppBaseFragment {

    @Override
    protected void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setContentView(R.layout.base_dialog_fragment);

        findViewById(R.id.btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(final View v) {
                BaseDialog dialog = new BaseDialog(mContext) {

                    @Override
                    public void onConfigure(Window window, RelativeLayout.LayoutParams contentViewLayoutParams) {

                    }

                    @NonNull
                    @Override
                    public View getContentView(AppBaseContainer parent) {
                        View view = View.inflate(mContext, R.layout.base_dialog, null);
                        view.findViewById(R.id.btn).setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                dismiss();
                            }
                        });
                        return view;
                    }


                    @Override
                    public boolean showNavigationBar() {
                        return false;
                    }
                };
                dialog.show();
            }
        });
    }
}
