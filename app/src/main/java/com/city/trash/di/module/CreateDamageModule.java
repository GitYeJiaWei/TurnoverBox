package com.city.trash.di.module;

import com.city.trash.data.CreateDamageModel;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.CreateDamageContract;

import dagger.Module;
import dagger.Provides;

@Module
public class CreateDamageModule {
    private CreateDamageContract.CreateDamageView mView;

    public CreateDamageModule(CreateDamageContract.CreateDamageView createRentView){
        this.mView = createRentView;
    }

    @Provides
    public CreateDamageContract.CreateDamageView provideView(){return mView;}

    @Provides
    public CreateDamageContract.ICreateDamageModel privideModel(ApiService apiService){
        return new CreateDamageModel(apiService);
    }
}
