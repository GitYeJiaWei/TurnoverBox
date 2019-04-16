package com.city.trash.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FeeRule;
import com.city.trash.common.util.ACache;
import com.city.trash.di.component.AppComponent;

import butterknife.BindView;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {


    @BindView(R.id.lin_lease)
    RelativeLayout linLease;
    @BindView(R.id.lin_back)
    RelativeLayout linBack;
    @BindView(R.id.lin_pick)
    RelativeLayout linPick;
    @BindView(R.id.lin_scan)
    RelativeLayout linScan;
    private BaseBean<FeeRule> baseBean;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int setLayout() {
        return R.layout.home_layout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init(View view) {
        baseBean = (BaseBean<FeeRule>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean == null) {
            return;
        }
        for (int i = 0; i < baseBean.getData().getPadMenus().size(); i++) {
            String id = baseBean.getData().getPadMenus().get(i).getId();
            if (id.equals("1")) {
                linLease.setVisibility(View.VISIBLE);
            } else if (id.equals("2")) {
                linBack.setVisibility(View.VISIBLE);
            } else if (id.equals("3")) {
                linPick.setVisibility(View.VISIBLE);
            } else if (id.equals("4")) {
                linScan.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setBarCode(String barCode) {

    }
}
