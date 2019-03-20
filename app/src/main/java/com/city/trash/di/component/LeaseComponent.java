package com.city.trash.di.component;

import com.city.trash.di.ActivityScope;
import com.city.trash.di.module.AppModule;
import com.city.trash.di.module.LeaseidModule;
import com.city.trash.ui.activity.PickActivity;
import com.city.trash.ui.fragment.LeaseFragment;
import com.city.trash.ui.fragment.ReturnFragment;

import dagger.Component;

@ActivityScope
@Component(modules = LeaseidModule.class,dependencies = AppComponent.class)
public interface LeaseComponent {
    void inject(LeaseFragment leaseFragment);
    void inject(ReturnFragment returnFragment);
}
