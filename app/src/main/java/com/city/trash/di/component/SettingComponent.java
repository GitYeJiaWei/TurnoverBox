package com.city.trash.di.component;

import com.city.trash.di.ActivityScope;
import com.city.trash.di.module.SettingModule;
import com.city.trash.ui.activity.UserActivity;

import dagger.Component;

@ActivityScope
@Component(modules = SettingModule.class,dependencies = AppComponent.class)
public interface SettingComponent {
    void inject(UserActivity userActivity);
}
