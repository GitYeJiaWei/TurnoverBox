package com.city.trash.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.EPC;
import com.city.trash.bean.LeaseBean;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.DateUtil;
import com.city.trash.common.util.SoundManage;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerLeaseComponent;
import com.city.trash.di.module.LeaseidModule;
import com.city.trash.presenter.LeaseidPresenter;
import com.city.trash.presenter.contract.LeaseidContract;
import com.city.trash.ui.activity.LeaseActivity;
import com.city.trash.ui.adapter.InitListAdapter;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 租赁
 */
public class LeaseFragment extends BaseFragment<LeaseidPresenter> implements LeaseidContract.LeaseidView {

    @BindView(R.id.btn_lease)
    Button btnLease;
    String cardCode;
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
        DaggerLeaseComponent.builder().appComponent(appComponent).leaseidModule(new LeaseidModule(this))
                .build().inject(this);
    }

    @Override
    public void init(View view) {
        initListAdapter = new InitListAdapter(getActivity(),"leaseResult");
        listLease.setAdapter(initListAdapter);

        ACache aCache = ACache.get(AppApplication.getApplication());
        ArrayList<EPC> leaseResultlist = (ArrayList<EPC>) aCache.getAsObject("leaseResult");
        if (leaseResultlist!=null){
            initListAdapter.updateDatas(leaseResultlist);
        }
    }

    @Override
    public void setBarCode(String barCode) {
    }

    @Override
    public void myOnKeyDwon() {
        readTag();
    }


    private void readTag() {
        if (DateUtil.isFastClick()){
            return;
        }
        AppApplication.mReader.setPower(10);
        SimpleRFIDEntity entity;
        entity = AppApplication.mReader.readData("00000000",
                RFIDWithUHF.BankEnum.valueOf("TID"),
                Integer.parseInt("0"),
                Integer.parseInt("4"));

        if (entity != null) {
            SoundManage.PlaySound(AppApplication.getApplication(), SoundManage.SoundType.SUCCESS);
            cardCode = entity.getData();
            mPresenter.leaseid("card1");
        } else {
            SoundManage.PlaySound(mActivity, SoundManage.SoundType.FAILURE);
            ToastUtil.toast("租赁卡扫描失败,请将PDA感应模块贴近卡片重新扫描");
        }
    }

    @OnClick(R.id.btn_lease)
    public void onViewClicked() {
        readTag();
    }

    @Override
    public void leaseidResult(BaseBean<LeaseBean> baseBean) {
        if (baseBean == null) {
            ToastUtil.toast("扫描租赁卡失败");
            return;
        }
        if (baseBean.getCode() == 0) {
            ToastUtil.toast("扫描租赁卡成功");
            Intent intent = new Intent(AppApplication.getApplication(), LeaseActivity.class);
            intent.putExtra("TID", cardCode);
            intent.putExtra("cardCode", baseBean.getData());
            startActivity(intent);
        }
    }

    @Override
    public void showError(String msg) {
        ToastUtil.toast("扫描租赁卡失败");
    }

}
