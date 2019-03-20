package com.city.trash.ui.activity;

import com.city.trash.R;
import com.city.trash.di.component.AppComponent;

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
        setTitle("使用帮助");
    }
}
