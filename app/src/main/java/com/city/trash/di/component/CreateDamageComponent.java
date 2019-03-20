package com.city.trash.di.component;

import com.city.trash.di.ActivityScope;
import com.city.trash.di.module.CreateDamageModule;
import com.city.trash.ui.activity.PickActivity;

import dagger.Component;

@ActivityScope
@Component(modules = CreateDamageModule.class,dependencies = AppComponent.class)
public interface CreateDamageComponent {
    void inject(PickActivity pickActivity);
}