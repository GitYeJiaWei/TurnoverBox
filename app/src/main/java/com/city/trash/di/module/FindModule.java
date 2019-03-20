package com.city.trash.di.module;

import com.city.trash.data.FindModel;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.FindContract;

import dagger.Module;
import dagger.Provides;

@Module
public class FindModule {
    private FindContract.FindView mView;

    public FindModule(FindContract.FindView leaseidView){
        this.mView = leaseidView;
    }

    @Provides
    public FindContract.FindView provideView(){return mView;}

    @Provides
    public FindContract.IFindModel privideModel(ApiService apiService){
        return new FindModel(apiService);
    }

}
