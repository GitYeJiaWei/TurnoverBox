package com.city.trash.di.component;

import android.app.Application;

import com.google.gson.Gson;
import com.city.trash.data.http.ApiService;
import com.city.trash.di.module.AppModule;
import com.city.trash.di.module.HttpModule;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;


@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent
{

    public Application getApplication();

    public ExecutorService getExecutorService();

    public Gson getGson();

    public ApiService getApiService();

    public OkHttpClient getOkHttpClient();

}
