package com.city.trash.di.module;

import com.city.trash.data.LeaseidModel;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.LeaseidContract;

import dagger.Module;
import dagger.Provides;

@Module
public class LeaseidModule {
    private LeaseidContract.LeaseidView mView;

    public LeaseidModule(LeaseidContract.LeaseidView leaseidView){
        this.mView = leaseidView;
    }

    @Provides
    public LeaseidContract.LeaseidView provideView(){return mView;}

    @Provides
    public LeaseidContract.ILeaseidModel privideModel(ApiService apiService){
        return new LeaseidModel(apiService);
    }
}
