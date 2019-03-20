package com.city.trash.di.component;

import com.city.trash.di.ActivityScope;
import com.city.trash.di.module.ReturnModule;
import com.city.trash.ui.activity.ReturnActivity;
import com.city.trash.ui.fragment.ReturnFragment;

import dagger.Component;

@ActivityScope
@Component(modules = ReturnModule.class,dependencies = AppComponent.class)
public interface ReturnComponent {
    void inject(ReturnActivity returnActivity);
}

