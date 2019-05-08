package com.city.trash.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.DateUtil;
import com.city.trash.common.util.SoundManage;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.data.http.ApiService;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerCreatRentComponent;
import com.city.trash.di.module.CreatRentModule;
import com.city.trash.presenter.CreatRentPresenter;
import com.city.trash.presenter.contract.CreateRentContract;
import com.city.trash.ui.adapter.LeaseScanadapter;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class LeaseActivity extends BaseActivity<CreatRentPresenter> implements CreateRentContract.CreateRentView {

    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_print)
    Button btnPrint;
    int a = 1;
    LeaseScanadapter leaseScanadapter = null;
    @BindView(R.id.lin_lease)
    LinearLayout linLease;
    @BindView(R.id.tv_tid)
    TextView tvTid;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_sum)
    TextView tvSum;
    @BindView(R.id.btn_card)
    Button btnCard;
    private ArrayList<EPC> epclist = new ArrayList<>();
    private ConcurrentHashMap<String, List<EPC>> hashMap = new ConcurrentHashMap<>();
    private HashMap<String, String> map = new HashMap<>();
    private BaseBean<FeeRule> baseBean = null;
    private double sum = 0;
    String leaseResult = null;
    String cardCode;
    String Tid;

    @Override
    public int setLayout() {
        return R.layout.activity_lease;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerCreatRentComponent.builder().appComponent(appComponent).creatRentModule(new CreatRentModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("扫描出库");
        linLease.setVisibility(View.GONE);
        hashMap.clear();
        map.clear();
        leaseScanadapter = new LeaseScanadapter(this, "lease");
        listLease.setAdapter(leaseScanadapter);

        baseBean = (BaseBean<FeeRule>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean == null) {
            finish();
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
        double money1 = 0;
        for (int i = 0; i < baseBean.getData().getFeeRules().size(); i++) {
            type = baseBean.getData().getFeeRules().get(i).getProductTypeId();
            name = baseBean.getData().getFeeRules().get(i).getProductTypeName();
            money1 = baseBean.getData().getFeeRules().get(i).getDeposit();
            if (baseEpc._EPC.length() <= type.length() + 1) {
                return;
            }
            if (type.equals(baseEpc._EPC.substring(0, type.length()))) {

                EPC epc = new EPC();
                epc.setData1(baseEpc._EPC);
                epc.setMoney(money1);
                if (!hashMap.containsKey(name)) {
                    ArrayList<EPC> epcs = new ArrayList<>();
                    epcs.add(epc);
                    hashMap.put(name, epcs);
                } else {
                    hashMap.get(name).add(epc);
                }

                epclist.clear();
                Iterator iterator = hashMap.keySet().iterator();
                sum = 0;
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    double money = hashMap.get(key).get(0).getMoney();
                    EPC epc1 = new EPC();
                    epc1.setEpc(key);
                    epc1.setData2(hashMap.get(key).size() + "");
                    epc1.setMoney(money);
                    epclist.add(epc1);
                    sum += money * hashMap.get(key).size();
                }
                map.put(baseEpc._EPC, null);
                tvSum.setText("累计租赁：" + map.size() + "个  应付金额：" + sum + "元");
                leaseScanadapter.updateDatas(epclist);
            }
        }
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


    @OnClick({R.id.btn_scan, R.id.btn_commit, R.id.btn_print, R.id.btn_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                readTag(a);
                break;
            case R.id.btn_commit:
                if (a == 2) {
                    readTag(a);
                }
                String name = tvName.getText().toString();
                String id = tvTid.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id)){
                    ToastUtil.toast("请扫描租赁卡再提交");
                    return;
                }
                ArrayList<String> arrayList = new ArrayList<>();
                Iterator it = hashMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    for (int i = 0; i < hashMap.get(key).size(); i++) {
                        arrayList.add(hashMap.get(key).get(i).getData1());
                    }
                }
                mPresenter.creatrent(AppApplication.getGson().toJson(arrayList), Tid);
                break;
            case R.id.btn_print:
                if (a == 2) {
                    readTag(a);
                }
                createDialog();
                break;
            case R.id.btn_card:
                if (a == 2) {
                    readTag(a);
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
        entity = AppApplication.mReader.readData("00000000",
                RFIDWithUHF.BankEnum.valueOf("TID"),
                Integer.parseInt("0"),
                Integer.parseInt("6"));
        if (entity != null) {
            cardCode = entity.getData();
            if (!TextUtils.isEmpty(cardCode)) {
                SoundManage.PlaySound(AppApplication.getApplication(), SoundManage.SoundType.SUCCESS);

                Map<String, String> map = new HashMap<>();
                map.put("cardCode", cardCode);
                map.put("cardType", "1");
                Retrofit retrofit = new Retrofit.Builder()
                        //设置基础的URL
                        .baseUrl(ApiService.BASE_URL)
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
                                               ToastUtil.toast("扫描租赁卡失败");
                                               return;
                                           }
                                           if (baseBean.getCode() == 0) {
                                               ToastUtil.toast("扫描租赁卡成功");
                                               tvName.setText(baseBean.getData().getContactName());
                                               tvTid.setText(cardCode);
                                               Tid = baseBean.getData().getId();
                                           } else {
                                               ToastUtil.toast(baseBean.getMessage());
                                           }
                                       }

                                       @Override
                                       public void onError(Throwable e) {
                                           ToastUtil.toast("操作失败,请退出重新登录");
                                       }

                                       @Override
                                       public void onComplete() {
                                       }//订阅
                                   }

                        );
            } else {
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                ToastUtil.toast("租赁卡扫描失败,请将PDA感应模块贴近卡片重新扫描");
            }
        } else {
            SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
            ToastUtil.toast("租赁卡扫描失败,请将PDA感应模块贴近卡片重新扫描");
        }
    }

    private void print() {
        if (PrintService.pl == null || PrintService.pl.getState() != PrinterClass.STATE_CONNECTED) {
            ToastUtil.toast("请先连接蓝牙打印机");
            startActivity(new Intent(LeaseActivity.this, BleActivity.class));
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str_time = simpleDateFormat.format(date);

        String mess = "";
        for (int i = 0; i < epclist.size(); i++) {
            String epc = epclist.get(i).getEpc();
            String data = epclist.get(i).getData2();
            String money = epclist.get(i).getMoney() + "";
            mess += epc + "    |    " + data + "     |   " + money + "\n";
        }
        String message =
                "*********租赁信息*********\n" +
                        "租赁单号：" + leaseResult + "\n" +
                        "租赁人：" + tvName.getText().toString() + "\n" +
                        "租赁卡：" + tvTid.getText().toString() + "\n" +
                        "--------------------------\n" +
                        "规格   |   数量   |   押金\n" +
                        mess +
                        "--------------------------\n" +
                        "累计租赁（个）：" + map.size() + "\n" +
                        "应付金额（元）：" + sum + "\n" +
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
        startActivity(new Intent(LeaseActivity.this, MainActivity.class));
        finish();
    }

    private void createDialog() {
        if (TextUtils.isEmpty(leaseResult)) {
            ToastUtil.toast("请先提交再打印！");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提交成功：");
        builder.setMessage("是否打印小票?");
        builder.setIcon(R.mipmap.ic_launcher_round);
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(true);
        //设置正面按钮
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                print();
                dialog.dismiss();
            }
        });
        //设置反面按钮
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(LeaseActivity.this, MainActivity.class));
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        //显示对话框
        dialog.show();
    }

    @Override
    public void createRentResult(BaseBean<String> baseBean) {
        if (baseBean == null) {
            ToastUtil.toast("出库提交失败");
            return;
        }
        if (baseBean.getCode() == 0) {
            ToastUtil.toast("出库提交成功");

            ACache aCache = ACache.get(AppApplication.getApplication());
            leaseResult = baseBean.getData();
            String name = tvName.getText().toString();
            String num = map.size() + "";

            EPC epc = new EPC();
            epc.setData1(leaseResult);
            epc.setData2(name);
            epc.setData3(num);
            epc.setData4(aCache.getAsString("username"));
            //使用getAsObject()，直接进行强转
            ArrayList<EPC> leaseResultlist = (ArrayList<EPC>) aCache.getAsObject("leaseResult");
            if (leaseResultlist == null) {
                leaseResultlist = new ArrayList<>();
            }
            leaseResultlist.add(epc);
            aCache.put("leaseResult", leaseResultlist, ACache.TIME_DAY);

            createDialog();
        } else {
            ToastUtil.toast(baseBean.getMessage());
        }

    }

    @Override
    public void showError(String msg) {
        ToastUtil.toast("操作失败,请退出重新登录");
    }

    @Override
    protected void onDestroy() {
        a = 2;
        readTag(a);
        super.onDestroy();
    }
}
