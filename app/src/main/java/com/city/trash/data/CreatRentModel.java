package com.city.trash.data;

import com.city.trash.bean.BaseBean;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.CreateRentContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class CreatRentModel implements CreateRentContract.ICreateRentModel{
    @Override
    public Observable<BaseBean<Object>> createRent(String ListEpcJson, String CustomerId,double ReplenishmentAmount) {
        Map<String,Object> map = new HashMap<>();
        map.put("ListEpcJson",ListEpcJson);
        map.put("CustomerId",CustomerId);
        map.put("ReplenishmentAmount",ReplenishmentAmount);
        return mApiService.createRent(map);
    }

    private ApiService mApiService;

    public CreatRentModel(ApiService apiService){
        this.mApiService = apiService;
    }

}
