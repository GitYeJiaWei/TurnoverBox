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
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerCreateDamageComponent;
import com.city.trash.di.module.CreateDamageModule;
import com.city.trash.presenter.CreateDamagePresenter;
import com.city.trash.presenter.contract.CreateDamageContract;
import com.city.trash.ui.adapter.InitListAdapter;
import com.city.trash.ui.adapter.LeaseScanadapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class PickActivity extends BaseActivity<CreateDamagePresenter> implements CreateDamageContract.CreateDamageView {

    @BindView(R.id.btn_lease)
    Button btnLease;
    String cardCode;
    int a = 1;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.tv_pick)
    ListView tvPick;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.lin_lease)
    LinearLayout linLease;
    LeaseScanadapter leaseScanadapter;
    private ArrayList<EPC> epclist = new ArrayList<>();
    private HashMap<String,String> map = new HashMap<>();
    private ConcurrentHashMap<String,List<EPC>> hashMap = new ConcurrentHashMap<>();
    @BindView(R.id.list_lease)
    ListView listLease;
    private InitListAdapter initListAdapter;
    private BaseBean<FeeRule> baseBean = null;

    @Override
    public int setLayout() {
        return R.layout.activity_pick;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerCreateDamageComponent.builder().appComponent(appComponent).createDamageModule(new CreateDamageModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("报废登记");
        linLease.setVisibility(View.GONE);
        //AppApplication.mReader.setPower(10);
        hashMap.clear();
        map.clear();
        leaseScanadapter = new LeaseScanadapter(this, "pick");
        tvPick.setAdapter(leaseScanadapter);

        initListAdapter = new InitListAdapter(this,"pickResult");
        listLease.setAdapter(initListAdapter);

        ACache aCache = ACache.get(AppApplication.getApplication());
        ArrayList<EPC> leaseResultlist = (ArrayList<EPC>) aCache.getAsObject("pickResult");
        if (leaseResultlist!=null){
            initListAdapter.updateDatas(leaseResultlist);
        }

        baseBean = (BaseBean<FeeRule>)ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean==null){
            return;
        }
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
        for (int i = 0; i < baseBean.getData().getFeeRules().size(); i++) {
            type = baseBean.getData().getFeeRules().get(i).getProductTypeId();
            name = baseBean.getData().getFeeRules().get(i).getProductTypeName();
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
                tvNum.setText(map.size()+"个");
                leaseScanadapter.updateDatas(epclist);
            }
        }
    }


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

    @Override
    public void createDamageResult(BaseBean<String> baseBean) {
        if (baseBean == null) {
            ToastUtil.toast("报废登记失败");
            return;
        }
        if (baseBean.getCode() == 0) {
            ToastUtil.toast("报废登记成功");

            ACache aCache = ACache.get(AppApplication.getApplication());
            String leaseResult = baseBean.getData();
            long time = System.currentTimeMillis();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1=new Date(time);
            String t1=format.format(d1);
            String num = map.size()+"";

            EPC epc = new EPC();
            epc.setData1(leaseResult);
            epc.setData2(num);
            epc.setData3(aCache.getAsString("username"));
            epc.setData4(t1);

            //使用getAsObject()，直接进行强转
            ArrayList<EPC> leaseResultlist = (ArrayList<EPC>) aCache.getAsObject("pickResult");
            if (leaseResultlist==null){
                leaseResultlist = new ArrayList<>();
            }
            leaseResultlist.add(epc);
            aCache.put("pickResult",leaseResultlist,ACache.TIME_DAY);
            startActivity(new Intent(this,MainActivity.class));
        } else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        ToastUtil.toast("报废登记失败");
    }

    @OnClick({R.id.btn_scan, R.id.btn_lease})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                readTag(a);
                break;
            case R.id.btn_lease:
                if (a==2){
                    readTag(a);
                }
                ArrayList<String> arrayList = new ArrayList<>();
                Iterator it = hashMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    for (int i = 0; i < hashMap.get(key).size(); i++) {
                        arrayList.add(hashMap.get(key).get(i).getData1());
                    }
                }
                mPresenter.createDamage(AppApplication.getGson().toJson(arrayList));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        a=2;
        readTag(a);
        super.onDestroy();
    }
}
