package com.city.trash.ui.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FindBean;
import com.city.trash.common.util.SoundManage;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerFindComponent;
import com.city.trash.di.module.FindModule;
import com.city.trash.presenter.FindPresenter;
import com.city.trash.presenter.contract.FindContract;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;

import butterknife.BindView;
import butterknife.OnClick;

public class FindActivity extends BaseActivity<FindPresenter> implements FindContract.FindView {

    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.tv_Epc)
    TextView tvEpc;
    @BindView(R.id.tv_LastCustomerName)
    TextView tvLastCustomerName;
    @BindView(R.id.tv_LastCustomerMobile)
    TextView tvLastCustomerMobile;
    @BindView(R.id.tv_LastRentTime)
    TextView tvLastRentTime;
    @BindView(R.id.tv_CustomerName)
    TextView tvCustomerName;
    @BindView(R.id.tv_CustomerMobile)
    TextView tvCustomerMobile;
    @BindView(R.id.tv_RentTime)
    TextView tvRentTime;
    @BindView(R.id.tv_RentDays)
    TextView tvRentDays;
    @BindView(R.id.tv_Deposit)
    TextView tvDeposit;
    @BindView(R.id.tv_Fee)
    TextView tvFee;
    @BindView(R.id.tv_OvertimeDays)
    TextView tvOvertimeDays;
    @BindView(R.id.tv_OvertimeFee)
    TextView tvOvertimeFee;
    @BindView(R.id.lin_lease)
    LinearLayout linLease;

    @Override
    public int setLayout() {
        return R.layout.activity_find;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerFindComponent.builder().appComponent(appComponent).findModule(new FindModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("扫码查询");
        linLease.setVisibility(View.GONE);
    }

    @Override
    public void findResult(BaseBean<FindBean> baseBean) {
        if (baseBean == null) {
            ToastUtil.toast("");
            return;
        }
        if (baseBean.getCode() == 0) {
            linLease.setVisibility(View.VISIBLE);
            FindBean findBean = baseBean.getData();
            tvEpc.setText(findBean.getEpc());
            tvLastCustomerName.setText(findBean.getLastCustomerName());
            tvLastCustomerMobile.setText(findBean.getCustomerMobile());
            tvLastRentTime.setText(findBean.getLastRentTime());
            tvCustomerName.setText(findBean.getCustomerName());
            tvCustomerMobile.setText(findBean.getCustomerMobile());
            tvRentTime.setText(findBean.getRentTime());
            tvRentDays.setText(findBean.getRentDays()+"");
            tvDeposit.setText(findBean.getDeposit()+"");
            tvFee.setText(findBean.getFee()+"");
            tvOvertimeDays.setText(findBean.getOvertimeDays()+"");
            tvOvertimeFee.setText(findBean.getOvertimeFee()+"");
        } else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        ToastUtil.toast("");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                readTag();
            }
        }

        return super.onKeyDown(keyCode, event);

    }

    private void readTag() {
        //AppApplication.mReader.setPower(10);
        SimpleRFIDEntity entity;
        entity = AppApplication.mReader.readData("00000000",
                RFIDWithUHF.BankEnum.valueOf("UII"),
                Integer.parseInt("0"),
                Integer.parseInt("4"));

        if (entity!=null){
            SoundManage.PlaySound(AppApplication.getApplication(), SoundManage.SoundType.SUCCESS);
            String epc = entity.getId().substring(4);
            mPresenter.find(epc);
        }else {
            SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
            ToastUtil.toast("周转箱编号扫描失败,请将PDA感应模块贴近卡片重新扫描");
        }
    }


    @OnClick(R.id.btn_scan)
    public void onViewClicked() {
        readTag();
    }
}
