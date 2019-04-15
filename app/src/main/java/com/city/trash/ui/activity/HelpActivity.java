package com.city.trash.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.city.trash.R;
import com.city.trash.di.component.AppComponent;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpActivity extends BaseActivity {

    @Override
    public int setLayout() {
        return R.layout.activity_help;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("蓝牙设置");
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);

        if (PrintService.pl != null && (position == 0 || position == 1) && PrintService.pl.getState() != PrinterClass.STATE_CONNECTED) {
            intent = new Intent();
            intent.setClass(HelpActivity.this, PrintSettingActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 0:
                if (PrintService.pl.getState() != PrinterClass.STATE_CONNECTED) {
                    HelpActivity.this.finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //断开打印连接
        //PrintService.pl.disconnect();
        super.onDestroy();
    }
}
