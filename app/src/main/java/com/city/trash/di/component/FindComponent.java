package com.city.trash.di.component;

import com.city.trash.di.ActivityScope;
import com.city.trash.di.module.FindModule;
import com.city.trash.ui.activity.FindActivity;

import dagger.Component;

@ActivityScope
@Component(modules = FindModule.class,dependencies = AppComponent.class)
public interface FindComponent {
    void inject(FindActivity findActivity);
}
