package com.city.trash.di.module;

import com.city.trash.data.LoginModel;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.LoginContract;

import dagger.Module;
import dagger.Provides;

/**
 * 提供依赖（presenter构造器需要的参数）
 */
@Module
public class LoginModule {
    private LoginContract.LoginView mView;

    //Module的构造函数，传入一个view，提供给Component
    public LoginModule(LoginContract.LoginView loginView){
        this.mView = loginView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  LoginContract.LoginView provideView(){
        return mView;
    }

    @Provides
    public LoginContract.ILoginModel provideModel(ApiService apiService){
        return new LoginModel(apiService);
    }



}
