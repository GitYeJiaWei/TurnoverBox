package com.city.trash.data;

import com.city.trash.bean.BaseBean;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.SettingContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class SettingModel implements SettingContract.ISettingModel{
    private ApiService mApiService;

    public SettingModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<String>> setting(String password, String newpassword, String twoPassword) {
        Map<String,String> map = new HashMap<>();
        map.put("oldPwd",password);
        map.put("newPwd",newpassword);
        map.put("confirmPwd",twoPassword);
        return mApiService.setting(map);
    }
}
