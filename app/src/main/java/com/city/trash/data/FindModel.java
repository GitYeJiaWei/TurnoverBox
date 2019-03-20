package com.city.trash.data;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FindBean;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.FindContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class FindModel implements FindContract.IFindModel {
    @Override
    public Observable<BaseBean<FindBean>> find(String epc) {
        Map<String,String> map = new HashMap<>();
        map.put("epc",epc);
        return mApiService.find(map);
    }

    private ApiService mApiService;

    public FindModel(ApiService apiService){
        this.mApiService = apiService;
    }
}
