package com.lhx.demo.dialog;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.lhx.demo.R;
import com.lhx.library.dialog.AlertController;
import com.lhx.library.fragment.AppBaseFragment;

import java.util.ArrayList;

/**
 * 弹窗demo
 */

public class DialogFragment extends AppBaseFragment implements View.OnClickListener {

    CheckedTextView subtitleCheckedTextView;
    CheckedTextView titleCheckedTextView;
    CheckedTextView logoCheckedTextView;
    TabLayout tabLayout;
    EditText countEditText;

    @Override
    public void initialize(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        mContentView = inflater.inflate(R.layout.dialog_fragment, null);

        findViewById(R.id.show_btn).setOnClickListener(this);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.addTab(tabLayout.newTab().setText("alert"));
        tabLayout.addTab(tabLayout.newTab().setText("actionSheet"));
        countEditText = findViewById(R.id.btn_count);
        subtitleCheckedTextView = findViewById(R.id.subtitle_checkbox);
        titleCheckedTextView = findViewById(R.id.title_checkbox);
        logoCheckedTextView = findViewById(R.id.logo_checkbox);
        subtitleCheckedTextView.setOnClickListener(this);
        titleCheckedTextView.setOnClickListener(this);
        logoCheckedTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.show_btn : {
                String title = null;
                if(titleCheckedTextView.isChecked()){
                    title = "标题";
                }

                String subtitle = null;
                if(subtitleCheckedTextView.isChecked()){
                    subtitle = "副标题";
                }

                int count = 0;
                try {
                    count = Integer.parseInt(countEditText.getText().toString());
                }catch (NumberFormatException e){

                }
                String[] strings = null;
                if(count > 0){
                    strings = new String[count];
                    for(int i = 0;i < count;i ++){
                        strings[i] = "按钮" + (i + 1);
                    }
                }

                switch (tabLayout.getSelectedTabPosition()){
                    case 0 : {
                        AlertController controller = AlertController.buildAlert(mContext, title, subtitle,
                                logoCheckedTextView.isChecked() ? ContextCompat.getDrawable(mContext, R.mipmap
                                        .checkboxmark) : null, strings);

                        controller.setShouldMesureContentHeight(true);
                        controller.setOnItemClickListener(new AlertController.OnItemClickListener() {
                            @Override
                            public void onItemClick(AlertController controller, int index) {

                            }
                        });

                        controller.show();
                    }
                    break;
                    case 1 : {
                        AlertController controller = AlertController.buildActionSheet(mContext, title, subtitle,
                                logoCheckedTextView.isChecked() ? ContextCompat.getDrawable(mContext, R.mipmap
                                        .checkboxmark) : null, strings);

                        controller.setShouldMesureContentHeight(true);
                        controller.setOnItemClickListener(new AlertController.OnItemClickListener() {
                            @Override
                            public void onItemClick(AlertController controller, int index) {

                            }
                        });

                        controller.show();
                    }
                    break;
                }
            }
            break;
            case R.id.title_checkbox :
                titleCheckedTextView.setChecked(!titleCheckedTextView.isChecked());
                break;
            case R.id.subtitle_checkbox :
                subtitleCheckedTextView.setChecked(!subtitleCheckedTextView.isChecked());
                break;
            case R.id.logo_checkbox :
                logoCheckedTextView.setChecked(!logoCheckedTextView.isChecked());
                break;
        }
    }

    @Override
    public boolean showNavigationBar() {
        return false;
    }
}
