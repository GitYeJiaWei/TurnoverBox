package com.city.trash.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
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
import com.city.trash.common.util.DateUtil;
import com.city.trash.common.util.SoundManage;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.data.http.ApiService;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerReturnComponent;
import com.city.trash.di.module.ReturnModule;
import com.city.trash.presenter.ReturnPresenter;
import com.city.trash.presenter.contract.ReturnContract;
import com.city.trash.ui.adapter.LeaseScanadapter;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.city.trash.ui.activity.MainActivity.mReader;

public class ReturnActivity extends BaseActivity<ReturnPresenter> implements ReturnContract.ReturnView {

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
    @BindView(R.id.btn_card)
    Button btnCard;

    private LinearLayout linLease;
    private ArrayList<EPC> epclist = new ArrayList<>();
    private ConcurrentHashMap<String, List<EPC>> hashMap = new ConcurrentHashMap<>();
    private HashMap<String, String> map = new HashMap<>();
    LeaseScanadapter leaseScanadapter = null;
    private ArrayList<String> arrayList = new ArrayList<>();
    private BaseBean<FeeRule> baseBean = null;
    String cardCode;
    String Tid;

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
        for (int i = 0; i < baseBean.getData().getFeeRules().size(); i++) {
            type = baseBean.getData().getFeeRules().get(i).getProductTypeId();
            name = baseBean.getData().getFeeRules().get(i).getProductTypeName();
            if (baseEpc._EPC.length() <= type.length() + 1) {
                return;
            }
            if (type.equals(baseEpc._EPC.substring(0, type.length()))) {
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
        linLease = findViewById(R.id.lin_lease);
        baseBean = (BaseBean<FeeRule>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean == null) {
            finish();
        }

        //AppApplication.mReader.setPower(10);
        setTitle("扫描入库");
        linLease.setVisibility(View.GONE);
        hashMap.clear();
        map.clear();
        leaseScanadapter = new LeaseScanadapter(this, "return");
        listLease.setAdapter(leaseScanadapter);
    }

    @Override
    public void returnResult(BaseBean<List<ReturnBean>> baseBean) {
        btnCommit.setEnabled(true);
        if (baseBean == null) {
            ToastUtil.toast("获取费用小计失败");
            return;
        }
        if (baseBean.getCode() == 0 && baseBean.getData() != null) {
            if (btnScan.getText().toString().equals("停止扫描")){
                readTag("停止扫描");
            }
            String TID = tvTid.getText().toString();
            String Name = tvName.getText().toString();
            Intent intent = new Intent(ReturnActivity.this, ReturnCommitActivity.class);
            intent.putExtra("Return", baseBean);
            intent.putExtra("TID", TID);
            intent.putExtra("Name", Name);
            intent.putExtra("ID", Tid);
            intent.putExtra("listEpcJson", AppApplication.getGson().toJson(arrayList));
            startActivityForResult(intent,1);
        } else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        btnCommit.setEnabled(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                readTag("扫描货物");
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                readTag("停止扫描");
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void readTag(String state) {
        linLease.setVisibility(View.VISIBLE);
        if (state.equals("扫描货物")) {
            if (mReader.startInventoryTag((byte) 0, (byte) 0)) {
                btnScan.setText("停止扫描");
                loopFlag = true;
                new TagThread(10).start();
            } else {
                btnScan.setText("扫描货物");
                mReader.stopInventory();
                loopFlag = false;
                ToastUtil.toast("开始扫描失败");
            }
        } else {
            btnScan.setText("扫描货物");
            mReader.stopInventory();
            loopFlag = false;
        }
    }

    @OnClick({R.id.btn_scan, R.id.btn_commit, R.id.btn_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                if (btnScan.getText().toString().equals("扫描货物")){
                    readTag("扫描货物");
                }else {
                    readTag("停止扫描");
                }
                break;
            case R.id.btn_commit:
                if (btnScan.getText().toString().equals("停止扫描")){
                    readTag("停止扫描");
                }
                String name = tvName.getText().toString();
                String id = tvTid.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id)) {
                    ToastUtil.toast("请扫描退还卡再提交");
                    return;
                }

                btnCommit.setEnabled(false);
                Iterator it = hashMap.keySet().iterator();
                arrayList.clear();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    for (int i = 0; i < hashMap.get(key).size(); i++) {
                        arrayList.add(hashMap.get(key).get(i).getData1());
                    }
                }
                mPresenter.Return(AppApplication.getGson().toJson(arrayList)); //获取超时数量
                break;
            case R.id.btn_card:
                if (btnScan.getText().toString().equals("停止扫描")){
                    readTag("停止扫描");
                }
                readCard();
                break;
        }
    }

    private void readCard() {
        if (DateUtil.isFastClick()) {
            return;
        }
        //AppApplication.mReader.setPower(10);
        SimpleRFIDEntity entity;
        entity = mReader.readData("00000000",
                RFIDWithUHF.BankEnum.valueOf("TID"),
                Integer.parseInt("0"),
                Integer.parseInt("6"));
        if (entity != null) {
            cardCode = entity.getData();
            if (!TextUtils.isEmpty(cardCode)) {
                SoundManage.PlaySound(AppApplication.getApplication(), SoundManage.SoundType.SUCCESS);
                String BASE_URL = ACache.get(AppApplication.getApplication()).getAsString("BASE_URL");
                if (BASE_URL == null){
                    BASE_URL = ApiService.BASE_URL;
                }
                Map<String, String> map = new HashMap<>();
                map.put("cardCode", cardCode);
                map.put("cardType", "2");
                Retrofit retrofit = new Retrofit.Builder()
                        //设置基础的URL
                        .baseUrl(BASE_URL)
                        //设置内容格式,这种对应的数据返回值是Gson类型，需要导包
                        .addConverterFactory(GsonConverterFactory.create())
                        //设置支持RxJava，应用observable观察者，需要导包
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(new OkHttpClient())
                        .build();

                ApiService apIservice = retrofit.create(ApiService.class);
                Observable<BaseBean<LeaseBean>> qqDataCall = apIservice.leaseid(map);
                qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                        .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                        .subscribe(new Observer<BaseBean<LeaseBean>>() {
                                       @Override
                                       public void onSubscribe(Disposable d) {

                                       }

                                       @Override
                                       public void onNext(BaseBean<LeaseBean> baseBean) {
                                           if (baseBean == null) {
                                               ToastUtil.toast("扫描退还卡失败");
                                               return;
                                           }
                                           if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                                               ToastUtil.toast("扫描退还卡成功");
                                               tvName.setText(baseBean.getData().getContactName());
                                               tvTid.setText(cardCode);
                                               Tid = baseBean.getData().getId();
                                           } else {
                                               ToastUtil.toast(baseBean.getMessage());
                                           }
                                       }

                                       @Override
                                       public void onError(Throwable e) {
                                           ToastUtil.toast(e.getMessage());
                                       }

                                       @Override
                                       public void onComplete() {
                                       }//订阅
                                   }

                        );
            } else {
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                ToastUtil.toast("退还卡扫描失败,请将PDA感应模块贴近卡片重新扫描");
            }
        } else {
            SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
            ToastUtil.toast("退还卡扫描失败,请将PDA感应模块贴近卡片重新扫描");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                }
        }
    }

    @Override
    protected void onDestroy() {
        if (btnScan.getText().toString().equals("停止扫描")){
            readTag("停止扫描");
        }
        super.onDestroy();
    }
}
