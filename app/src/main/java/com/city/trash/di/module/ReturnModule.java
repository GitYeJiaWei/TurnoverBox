package com.city.trash.di.module;

import com.city.trash.data.ReturnModel;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.ReturnContract;

import dagger.Module;
import dagger.Provides;

@Module
public class ReturnModule {
    private ReturnContract.ReturnView mView;

    //Module的构造函数，传入一个view，提供给Component
    public ReturnModule(ReturnContract.ReturnView returnView){
        this.mView = returnView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  ReturnContract.ReturnView provideView(){
        return mView;
    }

    @Provides
    public ReturnContract.IReturnModel provideModel(ApiService apiService){
        return new ReturnModel(apiService);
    }
}
