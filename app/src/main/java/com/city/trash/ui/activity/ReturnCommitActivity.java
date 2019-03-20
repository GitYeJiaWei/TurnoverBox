package com.city.trash.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.EPC;
import com.city.trash.bean.ReturnBean;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerCreateReturnComponent;
import com.city.trash.di.module.CreateReturnModule;
import com.city.trash.presenter.CreateReturnPresenter;
import com.city.trash.presenter.contract.CreateReturnContract;
import com.city.trash.ui.adapter.ReturnCommitAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ReturnCommitActivity extends BaseActivity<CreateReturnPresenter> implements CreateReturnContract.CreateReturnView {
    @BindView(R.id.tv_tid)
    TextView tvTid;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_money)
    EditText tvMoney;
    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.lin_lease)
    LinearLayout linLease;
    private BaseBean<List<ReturnBean>> returnBean;
    private ArrayList<EPC> epclist = new ArrayList<>();
    private ReturnCommitAdapter returnCommitAdapter;
    private double sum =0;
    private double s1 = 0;
    private int num =0;
    String id = null;
    String listEpcJson = null;

    @Override
    public int setLayout() {
        return R.layout.activity_return_commit;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerCreateReturnComponent.builder().appComponent(appComponent).createReturnModule(new CreateReturnModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("扫描入库");
        returnCommitAdapter = new ReturnCommitAdapter(this,"returnCommit");
        listLease.setAdapter(returnCommitAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            returnBean = (BaseBean<List<ReturnBean>>) intent.getSerializableExtra("Return");
            String Tid = intent.getStringExtra("TID");
            String Name = intent.getStringExtra("Name");
            tvName.setText(Name);
            tvTid.setText(Tid);
            id = intent.getStringExtra("ID");
            listEpcJson = intent.getStringExtra("listEpcJson");
        }
        if (returnBean == null) {
            ToastUtil.toast("获取数据失败");
            finish();
        }else {
            for (int i = 0; i < returnBean.getData().size(); i++) {
                ReturnBean rb = returnBean.getData().get(i);
                EPC epc = new EPC();
                epc.setEpc(rb.getProductTypeId());
                epc.setNum(rb.getQty());
                epc.setOverNum(rb.getOvertimeQty());
                epc.setOverTime(rb.getOvertimeDays());
                epc.setMoney(rb.getReturnAmount());
                epclist.add(epc);
                sum +=rb.getReturnAmount();
                num +=rb.getQty();
            }
            s1 = sum;
            tvBack.setText("累计退还："+num+"个  应退金额："+s1+"元");
            returnCommitAdapter.updateDatas(epclist);
        }

        tvMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())){
                    s1 = sum;
                }else {
                    s1=sum -Double.valueOf(s.toString());
                }
                tvBack.setText("累计退还："+num+"个  应退金额："+s1+"元");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void createReturnResult(BaseBean<String> baseBean) {
        if (baseBean == null){
            ToastUtil.toast("退还失败");
            return;
        }
        if (baseBean.getCode()==0){
            ToastUtil.toast("退还成功");

            ACache aCache = ACache.get(AppApplication.getApplication());
            String leaseResult = baseBean.getData();
            String name = tvName.getText().toString();

            EPC epc = new EPC();
            epc.setData1(leaseResult);
            epc.setData2(name);
            epc.setData3(num+"");
            epc.setData4(aCache.getAsString("username"));

            //使用getAsObject()，直接进行强转
            ArrayList<EPC> leaseResultlist = (ArrayList<EPC>) aCache.getAsObject("returnResult");
            if (leaseResultlist==null){
                leaseResultlist = new ArrayList<>();
            }
            leaseResultlist.add(epc);
            aCache.put("returnResult",leaseResultlist,ACache.TIME_DAY);
            startActivity(new Intent(this,MainActivity.class));
        }else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        ToastUtil.toast("退还失败");
    }


    @OnClick({R.id.btn_commit, R.id.btn_print})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (s1<0){
                    ToastUtil.toast("应退金额不能小于0元");
                    return;
                }
                mPresenter.CreateReturn(id,s1+"",listEpcJson);
                break;
            case R.id.btn_print:
                break;
        }
    }
}
