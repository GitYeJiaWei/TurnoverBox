package com.city.trash;

import android.app.Application;
import android.content.Context;
import com.google.gson.Gson;
import com.city.trash.common.AppCaughtException;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerAppComponent;
import com.city.trash.di.module.AppModule;
import com.rscja.deviceapi.RFIDWithUHF;
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

    public static RFIDWithUHF mReader; //RFID扫描


    @Override
    public void onCreate()
    {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                .build();
        mApplication = (AppApplication) mAppComponent.getApplication();
        mGson = mAppComponent.getGson();
        mThreadPool = mAppComponent.getExecutorService();

        initUHF();
    }

    private int cycleCount = 3;//循环3次初始化
    //初始化RFID扫描
    public void initUHF()
    {
        try
        {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex)
        {
            //ToastUtil.toast(ex.getMessage());
            return;
        }

        if (mReader != null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!mReader.init())
                    {
                        /*Toast.makeText(mApplication, "init uhf fail,reset ...",
                                Toast.LENGTH_SHORT).show();*/
                        if(cycleCount > 0)
                        {
                            cycleCount--;
                            if (mReader != null)
                            {
                                mReader.free();
                            }
                            initUHF();

                        }
                    }else
                    {
                        /*Toast.makeText(mApplication, "init uhf success",
                                Toast.LENGTH_SHORT).show();*/
                    }
                }
            }).start();
        }
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        Thread.setDefaultUncaughtExceptionHandler(new AppCaughtException());// 注册全局异常捕获
    }

}
