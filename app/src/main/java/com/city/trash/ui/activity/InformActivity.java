package com.city.trash.ui.activity;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.data.http.ApiService;
import com.city.trash.di.component.AppComponent;

import butterknife.BindView;
import butterknife.OnClick;

public class InformActivity extends BaseActivity {

    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_host)
    EditText etHost;
    @BindView(R.id.btn_save)
    Button btnSave;

    @Override
    public int setLayout() {
        return R.layout.activity_inform;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("通讯设置");
        String ip = ACache.get(AppApplication.getApplication()).getAsString("ip");
        String host = ACache.get(AppApplication.getApplication()).getAsString("host");
        if (ip == null) {
            ip = ApiService.ip;
        }
        if (host == null){
            host = ApiService.host;
        }
        etIp.setText(ip);
        etHost.setText(host);
    }


    @OnClick(R.id.btn_save)
    public void onViewClicked() {
        String ip = etIp.getText().toString();
        String host = etHost.getText().toString();
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(host)){
            ToastUtil.toast("IP地址或端口号不能为空");
        }else {
            ACache.get(AppApplication.getApplication()).put("ip",ip);
            ACache.get(AppApplication.getApplication()).put("host",host);
            ACache.get(AppApplication.getApplication()).put("BASE_URL", "http://" + ip + ":"+host+"/");
            ToastUtil.toast("保存成功");
            finish();
        }

    }
}
