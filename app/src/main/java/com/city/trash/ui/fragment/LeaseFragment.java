package com.city.trash.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.EPC;
import com.city.trash.common.util.ACache;
import com.city.trash.di.component.AppComponent;
import com.city.trash.ui.activity.LeaseActivity;
import com.city.trash.ui.adapter.InitListAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 租赁
 */
public class LeaseFragment extends BaseFragment {

    @BindView(R.id.btn_lease)
    Button btnLease;
    @BindView(R.id.list_lease)
    ListView listLease;
    private InitListAdapter initListAdapter;


    public static LeaseFragment newInstance() {
        return new LeaseFragment();
    }

    @Override
    public int setLayout() {
        return R.layout.lease_layout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init(View view) {
        initListAdapter = new InitListAdapter(getActivity(), "leaseResult");
        listLease.setAdapter(initListAdapter);

        ACache aCache = ACache.get(AppApplication.getApplication());
        ArrayList<EPC> leaseResultlist = (ArrayList<EPC>) aCache.getAsObject("leaseResult");
        if (leaseResultlist != null) {
            initListAdapter.updateDatas(leaseResultlist);
        }
    }

    @Override
    public void setBarCode(String barCode) {
    }

    @OnClick(R.id.btn_lease)
    public void onViewClicked() {
        Intent intent = new Intent(AppApplication.getApplication(), LeaseActivity.class);
        startActivity(intent);
    }
}
