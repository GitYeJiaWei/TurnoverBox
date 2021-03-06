package com.city.trash.di.module;

import com.city.trash.data.FeeRuleModel;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.RuleListContract;

import dagger.Module;
import dagger.Provides;

@Module
public class RuleListModule {
    RuleListContract.FeeRuleView mView;

    //Module的构造函数，传入一个view，提供给Component
    public RuleListModule(RuleListContract.FeeRuleView feeRuleView){
        this.mView = feeRuleView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  RuleListContract.FeeRuleView provideView(){
        return mView;
    }

    @Provides
    public RuleListContract.IFeeRuleModel provideModel(ApiService apiService){
        return new FeeRuleModel(apiService);
    }
}
