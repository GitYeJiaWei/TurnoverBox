package com.city.trash.di.module;

import com.city.trash.data.SettingModel;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.SettingContract;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingModule {
    SettingContract.SettingView mView;

    //Module的构造函数，传入一个view，提供给Component
    public SettingModule(SettingContract.SettingView settingView){
        this.mView = settingView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  SettingContract.SettingView provideView(){
        return mView;
    }

    @Provides
    public SettingContract.ISettingModel provideModel(ApiService apiService){
        return new SettingModel(apiService);
    }
}
