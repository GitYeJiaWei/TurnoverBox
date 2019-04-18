package com.city.trash.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.city.trash.R;
import com.city.trash.di.component.AppComponent;
import com.city.trash.ui.activity.BleActivity;
import com.city.trash.ui.activity.HelpActivity;
import com.city.trash.ui.activity.InformActivity;
import com.city.trash.ui.activity.PowerActivity;
import com.city.trash.ui.activity.UserActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 设置
 */
public class SettingFragment extends BaseFragment {
    @BindView(R.id.lin_inform)
    LinearLayout linInform;
    @BindView(R.id.lin_user)
    LinearLayout linUser;
    @BindView(R.id.lin_message)
    LinearLayout linMessage;
    @BindView(R.id.lin_ble)
    LinearLayout linBle;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public int setLayout() {
        return R.layout.setting_layout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init(View view) {

    }

    @Override
    public void setBarCode(String barCode) {

    }


    @OnClick({R.id.lin_inform, R.id.lin_user, R.id.lin_message,R.id.lin_ble})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_inform:
                startActivity(new Intent(getActivity(), InformActivity.class));
                break;
            case R.id.lin_user:
                startActivity(new Intent(getActivity(), UserActivity.class));
                break;
            case R.id.lin_message:
                startActivity(new Intent(getActivity(), PowerActivity.class));
                break;
            case R.id.lin_ble:
                startActivity(new Intent(getActivity(), BleActivity.class));
                break;
        }
    }

}
