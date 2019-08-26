package com.city.trash.ui.activity;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.common.ScreenUtils;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerSettingComponent;
import com.city.trash.di.module.SettingModule;
import com.city.trash.presenter.SettingPresenter;
import com.city.trash.presenter.contract.SettingContract;

import butterknife.BindView;
import butterknife.OnClick;

import static com.city.trash.ui.activity.LoginActivity.PASS_WORD;

public class UserActivity extends BaseActivity<SettingPresenter> implements SettingContract.SettingView {

    @BindView(R.id.edt_user)
    EditText edtUser;
    @BindView(R.id.edt_pass)
    EditText edtPass;
    @BindView(R.id.edt_pass1)
    EditText edtPass1;
    @BindView(R.id.edt_pass2)
    EditText edtPass2;
    @BindView(R.id.btn_save)
    Button btnSave;

    @Override
    public int setLayout() {
        return R.layout.activity_user;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerSettingComponent.builder().appComponent(appComponent).settingModule(new SettingModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("账号设置");
        edtUser.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.USER_NAME));
        edtPass.setText("");
        edtPass1.setText("");
        edtPass2.setText("");
    }


    @OnClick(R.id.btn_save)
    public void onViewClicked() {
        if (!ScreenUtils.Utils.isFastClick()) return;

        String password = edtPass.getText().toString();
        String newpassword = edtPass1.getText().toString();
        String twopassword = edtPass2.getText().toString();
        if (TextUtils.isEmpty(password)){
            ToastUtil.toast("请输入初始密码");
            return;
        }
        if (TextUtils.isEmpty(newpassword)){
            ToastUtil.toast("请输入新密码");
            return;
        }
        if (TextUtils.isEmpty(twopassword)){
            ToastUtil.toast("请输入确认密码");
            return;
        }

        btnSave.setEnabled(false);
        mPresenter.setting(password,newpassword,twopassword);
    }

    @Override
    public void settingResult(BaseBean<String> baseBean) {
        if (baseBean == null){
            btnSave.setEnabled(true);
            ToastUtil.toast("修改密码失败");
            return;
        }
        btnSave.setEnabled(true);
        if (baseBean.getCode()==0){
            ToastUtil.toast("密码修改成功");
            ACache.get(AppApplication.getApplication()).put(PASS_WORD, edtPass1.getText().toString());
            finish();
        }else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        btnSave.setEnabled(true);
    }
}
