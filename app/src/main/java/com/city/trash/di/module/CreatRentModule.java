package com.city.trash.di.module;

import com.city.trash.data.CreatRentModel;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.CreateRentContract;

import dagger.Module;
import dagger.Provides;

@Module
public class CreatRentModule {
    private CreateRentContract.CreateRentView mView;

    public CreatRentModule(CreateRentContract.CreateRentView createRentView){
        this.mView = createRentView;
    }

    @Provides
    public CreateRentContract.CreateRentView provideView(){return mView;}

    @Provides
    public CreateRentContract.ICreateRentModel privideModel(ApiService apiService){
        return new CreatRentModel(apiService);
    }

}
