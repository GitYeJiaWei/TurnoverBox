package com.city.trash.di.component;

import com.city.trash.di.ActivityScope;
import com.city.trash.di.module.CreateReturnModule;
import com.city.trash.ui.activity.ReturnCommitActivity;

import dagger.Component;

@ActivityScope
@Component(modules = CreateReturnModule.class,dependencies = AppComponent.class)
public interface CreateReturnComponent {
    void inject(ReturnCommitActivity returnCommitActivity);
}