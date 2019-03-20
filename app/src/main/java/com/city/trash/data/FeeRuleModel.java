package com.city.trash.data;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FeeRule;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.RuleListContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public class FeeRuleModel implements RuleListContract.IFeeRuleModel {
    private ApiService mApiService;

    public FeeRuleModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<List<FeeRule>>> feeRule() {
        Map<String,String> map = new HashMap<>();
        return mApiService.rulelist(map);
    }
}
