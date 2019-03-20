package com.city.trash.di.component;

import com.city.trash.di.ActivityScope;
import com.city.trash.di.module.CreatRentModule;
import com.city.trash.ui.activity.LeaseActivity;

import dagger.Component;

@ActivityScope
@Component(modules = CreatRentModule.class,dependencies = AppComponent.class)
public interface CreatRentComponent {
    void inject(LeaseActivity leaseActivity);
}
