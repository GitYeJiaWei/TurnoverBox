package com.city.trash.di.component;

import com.city.trash.di.ActivityScope;
import com.city.trash.di.module.LoginModule;
import com.city.trash.ui.activity.LoginActivity;

import dagger.Component;

/**
 * 下面的注解，代表使用LoginModule
 * 用来将@Inject和@Module联系起来的桥梁，从@Module中获取依赖并将依赖注入给@Inject
 */
@ActivityScope
@Component(modules = LoginModule.class,dependencies = AppComponent.class)
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
