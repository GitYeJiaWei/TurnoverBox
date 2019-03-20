package com.city.trash.di.component;

import com.city.trash.di.ActivityScope;
import com.city.trash.di.module.RuleListModule;
import com.city.trash.ui.activity.MainActivity;

import dagger.Component;

@ActivityScope
@Component(modules = RuleListModule.class,dependencies = AppComponent.class)
public interface RuleListComponent {
    void inject(MainActivity mainActivity);
}