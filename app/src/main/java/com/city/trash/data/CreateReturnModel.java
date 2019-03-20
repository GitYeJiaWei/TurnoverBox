package com.city.trash.data;

import com.city.trash.bean.BaseBean;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.CreateReturnContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class CreateReturnModel implements CreateReturnContract.ICreateReturnModel {
    @Override
    public Observable<BaseBean<String>> createReturn(String customerId,String damageFee,String listEpcJson) {
        Map<String,String> map = new HashMap<>();
        map.put("customerId",customerId);
        map.put("damageFee",damageFee);
        map.put("listEpcJson",listEpcJson);
        return mApiService.createReturn(map);
    }

    private ApiService mApiService;

    public CreateReturnModel(ApiService apiService){
        this.mApiService = apiService;
    }
}
