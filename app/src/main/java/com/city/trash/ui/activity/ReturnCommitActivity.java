package com.city.trash.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
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
import com.city.trash.common.EditInputFilter;
import com.city.trash.common.ScreenUtils;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerCreateReturnComponent;
import com.city.trash.di.module.CreateReturnModule;
import com.city.trash.presenter.CreateReturnPresenter;
import com.city.trash.presenter.contract.CreateReturnContract;
import com.city.trash.ui.adapter.ReturnCommitAdapter;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private BaseBean<List<ReturnBean>> returnBean;
    private ArrayList<EPC> epclist = new ArrayList<>();
    private ReturnCommitAdapter returnCommitAdapter;
    private double sum = 0;
    private double s1 = 0;
    private int num = 0;
    String id = null;
    String listEpcJson = null;
    private String leaseResult = null;
    String Balance;

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
        returnCommitAdapter = new ReturnCommitAdapter(this, "returnCommit");
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
        } else {
            for (int i = 0; i < returnBean.getData().size(); i++) {
                ReturnBean rb = returnBean.getData().get(i);
                EPC epc = new EPC();
                epc.setEpc(rb.getProductTypeName());
                epc.setNum(rb.getQty());
                epc.setOverNum(rb.getOvertimeQty());
                epc.setOverTime(rb.getOvertimeDays());
                epc.setMoney(rb.getReturnAmount());
                epclist.add(epc);
                BigDecimal bd1 = new BigDecimal(Double.toString(rb.getReturnAmount()));
                BigDecimal bd2 = new BigDecimal(Double.toString(sum));

                //累加应退总额和个数
                sum = bd1.add(bd2).doubleValue();
                num += rb.getQty();
            }
            s1 = sum;
            tvBack.setText("累计退还：" + num + "个  应退金额：" + s1 + "元");
            returnCommitAdapter.updateDatas(epclist);
        }

        tvMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    s1 = sum;
                } else {
                    //new BigDecimal("string")转完一定是string，不会有精度问题
                    BigDecimal st = new BigDecimal(s.toString());
                    BigDecimal su = new BigDecimal(Double.toString(sum));
                    s1 = su.subtract(st).doubleValue();
                }
                tvBack.setText("累计退还：" + num + "个  应退金额：" + s1 + "元");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //EditInputFilter.MAX_VALUE = 4;
        InputFilter[] filters = {new EditInputFilter()};

        tvMoney.setFilters(filters);
    }

    @Override
    public void createReturnResult(BaseBean<Object> baseBean) {
        if (baseBean == null) {
            btnCommit.setEnabled(true);
            ToastUtil.toast("退还失败");
            return;
        }
        if (baseBean.getCode() == 0 && baseBean.getData() != null) {
            ToastUtil.toast("退还成功");

            ACache aCache = ACache.get(AppApplication.getApplication());
            Map<String, Object> map = (Map<String, Object>) baseBean.getData();
            leaseResult = map.get("Id").toString();
            Balance = map.get("Balance").toString();
            String name = tvName.getText().toString();

            EPC epc = new EPC();
            epc.setData1(leaseResult);
            epc.setData2(name);
            epc.setData3(num + "");
            epc.setData4(aCache.getAsString("username"));

            //使用getAsObject()，直接进行强转
            ArrayList<EPC> leaseResultlist = (ArrayList<EPC>) aCache.getAsObject("returnResult");
            if (leaseResultlist == null) {
                leaseResultlist = new ArrayList<>();
            }
            leaseResultlist.add(epc);
            aCache.put("returnResult", leaseResultlist, ACache.TIME_HOUR);

            createDialog();

        } else {
            btnCommit.setEnabled(true);
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        btnCommit.setEnabled(false);
    }

    private void print() {
        if (PrintService.pl == null || PrintService.pl.getState() != PrinterClass.STATE_CONNECTED) {
            ToastUtil.toast("请先连接蓝牙打印机");
            startActivity(new Intent(this, BleActivity.class));
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str_time = simpleDateFormat.format(date);

        String mess = "";
        for (int i = 0; i < epclist.size(); i++) {
            String epc = epclist.get(i).getEpc();
            String num1 = epclist.get(i).getNum() + "";
            String overNum = epclist.get(i).getOverNum() + "";
            String overTime = epclist.get(i).getOverTime() + "";
            mess += epc + " |  " + num1 + " |    " + overNum + "   |    " + overTime + "\n";
        }
        String message =
                "*********退还信息*********\n" +
                        "退还单号：" + leaseResult + "\n" +
                        "退还人：" + tvName.getText().toString() + "\n" +
                        "退还卡：" + tvTid.getText().toString() + "\n" +
                        "--------------------------\n" +
                        "规格|数量|超时数量|超时天数\n" +
                        mess +
                        "--------------------------\n" +
                        "累计退还（个）：" + num + "\n" +
                        "破损总扣费（元）：" + tvMoney.getText().toString() + "\n" +
                        "应退金额（元）：" + s1 + "\n" +
                        "余额（元）：" + Balance + "\n" +
                        "操作员：" + ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.REAL_NAME) + "\n" +
                        "打印时间：" + str_time + "\n\n\n";
        try {
            byte[] send = message.getBytes("GBK");
            PrintService.pl.write(send);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PrintService.pl.printText("\n");
        PrintService.pl.write(new byte[]{0x1d, 0x0c});
        setResult(RESULT_OK);
        finish();
    }

    private void createDialog() {
        if (TextUtils.isEmpty(leaseResult)) {
            ToastUtil.toast("请先提交再打印！");
            btnCommit.setEnabled(true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提交成功：");
        builder.setMessage("是否打印小票?");
        builder.setIcon(R.mipmap.ic_launcher_round);
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(false);
        //设置正面按钮
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnCommit.setEnabled(true);
                print();
                dialog.dismiss();
            }
        });
        //设置反面按钮
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnCommit.setEnabled(true);
                dialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        //显示对话框
        dialog.show();
    }

    @OnClick({R.id.btn_commit, R.id.btn_print})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (!ScreenUtils.Utils.isFastClick()) return;

                btnCommit.setEnabled(false);

                if (s1 < 0) {
                    ToastUtil.toast("应退金额不能小于0元，请先充值再退还");
                    btnCommit.setEnabled(true);
                    return;
                }
                double damageFee = 0;
                if (!TextUtils.isEmpty(tvMoney.getText().toString())) {
                    damageFee = Double.valueOf(tvMoney.getText().toString());
                }

                mPresenter.CreateReturn(id, damageFee + "", listEpcJson);
                break;
            case R.id.btn_print:
                if (!ScreenUtils.Utils.isFastClick()) return;

                createDialog();
                break;
        }
    }
}
