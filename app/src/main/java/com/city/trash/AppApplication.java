package com.city.trash;

import android.app.Application;
import android.content.Context;
import com.google.gson.Gson;
import com.city.trash.common.AppCaughtException;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerAppComponent;
import com.city.trash.di.module.AppModule;
import org.xutils.x;

import java.util.concurrent.ExecutorService;


public class AppApplication extends Application
{

    private AppComponent mAppComponent;

    private static ExecutorService mThreadPool;

    private static AppApplication mApplication;

    public static AppApplication getApplication()
    {
        return mApplication;
    }

    public AppComponent getAppComponent()
    {
        return mAppComponent;
    }

    public static ExecutorService getExecutorService()
    {
        return mThreadPool;
    }

    public static Gson mGson;

    public static Gson getGson()
    {
        return mGson;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                .build();
        mApplication = (AppApplication) mAppComponent.getApplication();
        mGson = mAppComponent.getGson();
        mThreadPool = mAppComponent.getExecutorService();
        //初始化下载框架xUtils
        x.Ext.init(this);
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        Thread.setDefaultUncaughtExceptionHandler(new AppCaughtException());// 注册全局异常捕获
    }

}
