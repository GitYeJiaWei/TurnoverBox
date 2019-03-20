package com.city.trash.data;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.LeaseBean;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.LeaseidContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class LeaseidModel implements LeaseidContract.ILeaseidModel{
    private ApiService mApiService;

    public LeaseidModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<LeaseBean>> leaseid(String cardCode) {
        Map<String,String> map = new HashMap<>();
        map.put("cardCode",cardCode);
        return mApiService.leaseid(map);
    }
}
