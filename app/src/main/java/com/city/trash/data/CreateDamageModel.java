package com.city.trash.data;

import com.city.trash.bean.BaseBean;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.CreateDamageContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class CreateDamageModel implements CreateDamageContract.ICreateDamageModel {
    @Override
    public Observable<BaseBean<String>> createDamage(String ListEpcJson) {
        Map<String,String> map = new HashMap<>();
        map.put("ListEpcJson",ListEpcJson);
        return mApiService.createDamage(map);
    }

    private ApiService mApiService;

    public CreateDamageModel(ApiService apiService){
        this.mApiService = apiService;
    }

}
