package com.city.trash.ui.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.BaseEpc;
import com.city.trash.bean.EPC;
import com.city.trash.bean.FeeRule;
import com.city.trash.bean.LeaseBean;
import com.city.trash.bean.ReturnBean;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerReturnComponent;
import com.city.trash.di.module.ReturnModule;
import com.city.trash.presenter.ReturnPresenter;
import com.city.trash.presenter.contract.ReturnContract;
import com.city.trash.ui.adapter.LeaseScanadapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class ReturnActivity extends BaseActivity<ReturnPresenter> implements ReturnContract.ReturnView {

    LeaseBean leaseBean;
    @BindView(R.id.tv_tid)
    TextView tvTid;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.lin_lease)
    LinearLayout linLease;
    int a = 1;
    private ArrayList<EPC> epclist = new ArrayList<>();
    private ConcurrentHashMap<String,List<EPC>> hashMap = new ConcurrentHashMap<>();
    private HashMap<String,String> map = new HashMap<>();
    LeaseScanadapter leaseScanadapter = null;
    private ArrayList<String> arrayList = new ArrayList<>();
    private BaseBean<List<FeeRule>> baseBean = null;

    @Override
    public int setLayout() {
        return R.layout.activity_return;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerReturnComponent.builder().appComponent(appComponent).returnModule(new ReturnModule(this))
                .build().inject(this);
    }

    //获取EPC群读数据
    @Override
    public void handleUi(BaseEpc baseEpc) {
        super.handleUi(baseEpc);
        if (map.containsKey(baseEpc._EPC)) {
            return;
        }

        String type = null;
        String name = null;
        for (int i = 0; i < baseBean.getData().size(); i++) {
            type = baseBean.getData().get(i).getProductTypeId();
            name = baseBean.getData().get(i).getProductTypeName();
            if (baseEpc._EPC.length()<=type.length()+1){
                return;
            }
            if (type.equals(baseEpc._EPC.substring(0,type.length()))){
                EPC epc = new EPC();
                epc.setData1(baseEpc._EPC);
                if (!hashMap.containsKey(name)) {
                    ArrayList<EPC> epcs = new ArrayList<>();
                    epcs.add(epc);
                    hashMap.put(name, epcs);
                } else {
                    hashMap.get(name).add(epc);
                }

                epclist.clear();
                Iterator iterator = hashMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    EPC epc1 = new EPC();
                    epc1.setEpc(key);
                    epc1.setData2(hashMap.get(key).size() + "");
                    epclist.add(epc1);
                }
                map.put(baseEpc._EPC, null);
                leaseScanadapter.updateDatas(epclist);
            }
        }
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        if (intent != null) {
            leaseBean = (LeaseBean) intent.getSerializableExtra("cardCode");
            String Tid = intent.getStringExtra("TID");
            tvName.setText(leaseBean.getName());
            tvTid.setText(Tid);
        }
        if (leaseBean == null) {
            ToastUtil.toast("获取数据失败");
            finish();
        }

        baseBean = (BaseBean<List<FeeRule>>)ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean==null){
            return;
        }

        AppApplication.mReader.setPower(10);
        setTitle("扫描入库");
        linLease.setVisibility(View.GONE);
        hashMap.clear();
        map.clear();
        leaseScanadapter = new LeaseScanadapter(this,"return");
        listLease.setAdapter(leaseScanadapter);
    }

    @Override
    public void returnResult(BaseBean<List<ReturnBean>> baseBean) {
        if (baseBean==null){
            ToastUtil.toast("获取费用小计失败");
            return;
        }
        if (baseBean.getCode()==0 && baseBean.getData().size()>0){
            String TID = tvTid.getText().toString();
            String Name = tvName.getText().toString();
            Intent intent = new Intent(ReturnActivity.this,ReturnCommitActivity.class);
            intent.putExtra("Return",baseBean);
            intent.putExtra("TID",TID);
            intent.putExtra("Name",Name);
            intent.putExtra("ID",leaseBean.getId());
            intent.putExtra("listEpcJson",AppApplication.getGson().toJson(arrayList));
            startActivity(intent);
        }else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                a = 1;
                readTag(a);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                a = 2;
                readTag(a);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void readTag(int scan) {
        linLease.setVisibility(View.VISIBLE);
        if (scan == 1) {
            if (AppApplication.mReader.startInventoryTag((byte) 0, (byte) 0)) {
                btnScan.setText("停止扫描");
                loopFlag = true;
                a = 2;
                new TagThread(10).start();
            } else {
                btnScan.setText("扫描货物");
                AppApplication.mReader.stopInventory();
                loopFlag = false;
                a = 1;
                ToastUtil.toast("开始扫描失败");
            }
        } else {
            btnScan.setText("扫描货物");
            AppApplication.mReader.stopInventory();
            loopFlag = false;
            a = 1;
        }
    }

    @OnClick({R.id.btn_scan, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                readTag(a);
                break;
            case R.id.btn_commit:
                if (a==2){
                    readTag(a);
                }
                Iterator it = hashMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    for (int i = 0; i < hashMap.get(key).size(); i++) {
                        arrayList.add(hashMap.get(key).get(i).getData1());
                    }
                }
                mPresenter.Return(AppApplication.getGson().toJson(arrayList)); //获取超时数量
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (a==2){
            readTag(a);
        }
        super.onDestroy();
    }
}
