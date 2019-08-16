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
import com.city.trash.ui.activity.ReturnActivity;
import com.city.trash.ui.adapter.InitListAdapter;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 退还
 */
public class ReturnFragment extends BaseFragment{
    @BindView(R.id.btn_lease)
    Button btnLease;
    @BindView(R.id.list_lease)
    ListView listLease;
    private InitListAdapter initListAdapter;

    public static ReturnFragment newInstance() {
        return new ReturnFragment();
    }

    @Override
    public int setLayout() {
        return R.layout.return_layout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init(View view) {
        initListAdapter = new InitListAdapter(getActivity(),"returnResult");
        listLease.setAdapter(initListAdapter);

        ACache aCache = ACache.get(AppApplication.getApplication());
        ArrayList<EPC> leaseResultlist = (ArrayList<EPC>) aCache.getAsObject("returnResult");
        if (leaseResultlist!=null){
            initListAdapter.updateDatas(leaseResultlist);
        }
    }

    @Override
    public void setBarCode(String barCode) {

    }

    @OnClick(R.id.btn_lease)
    public void onViewClicked() {
        Intent intent = new Intent(AppApplication.getApplication(), ReturnActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK){
                    ACache aCache = ACache.get(AppApplication.getApplication());
                    ArrayList<EPC> leaseResultlist = (ArrayList<EPC>) aCache.getAsObject("returnResult");
                    if (leaseResultlist!=null){
                        Collections.reverse(leaseResultlist);
                        initListAdapter.updateDatas(leaseResultlist);
                    }
                }
        }
    }
}
