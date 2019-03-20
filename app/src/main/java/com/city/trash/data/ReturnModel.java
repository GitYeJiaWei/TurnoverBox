package com.city.trash.data;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.ReturnBean;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.ReturnContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class ReturnModel implements ReturnContract.IReturnModel {
    private ApiService mApiService;

    public ReturnModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<List<ReturnBean>>> Return(String listEpcJson) {
        Map<String,String> map = new HashMap<>();
        map.put("listEpcJson",listEpcJson);
        return mApiService.getReturn(map);
    }
}
