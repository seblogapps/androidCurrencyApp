package com.example.stognacci.currencyapp.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by stognacci on 01/04/2016.
 * Simply create a class to keep track of App lifecycle
 */
public class LifeCycleApp extends Application {

    private static Context context;
    private static Resources appResources;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new LifeCycleUtils());
        LifeCycleApp.context = getApplicationContext();
        LifeCycleApp.appResources = getResources();
    }

    public static Context getAppContext() {
        return LifeCycleApp.context;
    }

    public static Resources getAppResources() {
        return appResources;
    }
}
