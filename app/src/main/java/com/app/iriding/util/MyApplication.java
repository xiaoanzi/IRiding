package com.app.iriding.util;

import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by 王海 on 2015/5/21.
 */
public class MyApplication extends com.activeandroid.app.Application {
    private static Context context;
    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //在使用百度地图SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(context);
    }
    public static Context getContext() { return context; }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public MyApplication() {
        super();
    }
}
