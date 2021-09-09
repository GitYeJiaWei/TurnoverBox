package com.city.trash.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.common.http.BaseUrlInterceptor;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.DateUtil;
import com.city.trash.common.util.SoundManage;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.data.http.ApiService;
import com.city.trash.di.component.AppComponent;
import com.hylg.scancode.activity_scan.CommonScanActivity;
import com.hylg.scancode.utils.Constant;
import com.rscja.deviceapi.interfaces.IUHF;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.city.trash.ui.activity.MainActivity.mReader;

public class BindActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edt_rfid)
    EditText edt_rfid;
    @BindView(R.id.edt_scan)
    EditText edt_scan;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    @BindView(R.id.btn_rfidScan)
    Button btn_rfidScan;
    @BindView(R.id.btn_codeScan)
    Button btn_codeScan;

    @Override
    public int setLayout() {
        return R.layout.activity_bind;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("绑定二维码");
        btn_submit.setOnClickListener(this);
        btn_rfidScan.setOnClickListener(this);
        btn_codeScan.setOnClickListener(this);
    }

    private void readCard() {
        if (DateUtil.isFastClick()) {
            return;
        }
        String site = mReader.readData("00000000",
                IUHF.Bank_EPC,
                Integer.parseInt("1"),
                Integer.parseInt("1"));
        if (!TextUtils.isEmpty(site)) {
            String epc = null;
            if ("2".equals(site.substring(0, 1))) {//16位
                epc = mReader.readData("00000000",
                        IUHF.Bank_EPC,
                        Integer.parseInt("2"),
                        Integer.parseInt("4"));
            } else if ("3".equals(site.substring(0, 1))) {//24位
                epc = mReader.readData("00000000",
                        IUHF.Bank_EPC,
                        Integer.parseInt("2"),
                        Integer.parseInt("6"));
            } else if ("4".equals(site.substring(0, 1))) {//32位
                epc = mReader.readData("00000000",
                        IUHF.Bank_EPC,
                        Integer.parseInt("2"),
                        Integer.parseInt("8"));
            }

            if (epc != null) {
                if (!TextUtils.isEmpty(epc)) {
                    SoundManage.PlaySound(AppApplication.getApplication(), SoundManage.SoundType.SUCCESS);
                    edt_rfid.setText(epc);
                } else {
                    SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                    ToastUtil.toast("卡片扫描失败,请将PDA感应模块贴近卡片重新扫描");
                }
            } else {
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                ToastUtil.toast("卡片扫描失败,请将PDA感应模块贴近卡片重新扫描");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String code =data.getExtras().getString(Constant.REQUEST_SCAN_MODE);//得到新Activity关闭后返回的数据
        Log.i("二维码", "onActivityResult: "  + code);
        if (!TextUtils.isEmpty(code)){
            String substring = code.substring(code.indexOf("=")+1);
            edt_scan.setText(substring);
        }
    }

    public static long DEFAULT_TIMEOUT = 60;//设置访问超时时间
    public static Retrofit toretrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //retrofit底层用的okHttp,所以设置超时还需要okHttp
        //然后设置5秒超时
        //TimeUnit为java.util.concurrent包下的时间单位
        //TimeUnit.SECONDS这里为秒的单位
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        //添加拦截器，自动追加参数
        builder.addInterceptor(new BaseUrlInterceptor());
        String BASE_URL = ACache.get(AppApplication.getApplication()).getAsString("BASE_URL");
        if (BASE_URL == null) {
            BASE_URL = ApiService.BASE_URL;
        }

        //构建Retrofit对象
        //然后将刚才设置好的okHttp对象,通过retrofit.client()方法 设置到retrofit中去
        Retrofit retrofit = new Retrofit.Builder()
                //设置基础的URL
                .baseUrl(BASE_URL)
                //设置内容格式,这种对应的数据返回值是Gson类型，需要导包
                .addConverterFactory(GsonConverterFactory.create())
                //设置支持RxJava，应用observable观察者，需要导包
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build();
        return retrofit;
    }

    private void submitMessage() {
        String rfid = edt_rfid.getText().toString().trim();
        String scan = edt_scan.getText().toString().trim();
        if (TextUtils.isEmpty(rfid) || TextUtils.isEmpty(scan)) {
            ToastUtil.toast("RFID编号和二维码编号不能为空");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("epc", rfid);
            map.put("qrCode", scan);

            ApiService apIservice = toretrofit().create(ApiService.class);
            Observable<BaseBean> qqDataCall = apIservice.bind(map);
            qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                    .subscribe(new Observer<BaseBean>() {
                                   @Override
                                   public void onSubscribe(Disposable d) {
                                   }

                                   @Override
                                   public void onNext(BaseBean baseBean) {
                                       if (baseBean == null) {
                                           ToastUtil.toast("提交失败");
                                           return;
                                       }
                                       if (baseBean.getCode() == 0) {
                                           ToastUtil.toast("提交成功");
                                           finish();
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                submitMessage();
                break;
            case R.id.btn_rfidScan:
                readCard();
                break;
            case R.id.btn_codeScan:
                startActivityForResult(new Intent(BindActivity.this, CommonScanActivity.class),  1);
                break;
        }
    }
}