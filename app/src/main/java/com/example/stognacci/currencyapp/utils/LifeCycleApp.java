package com.example.stognacci.currencyapp.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by stognacci on 01/04/2016.
 * Simply create a class to keep track of App lifecycle
 */
public class LifeCycleApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new LifeCycleUtils());
        LifeCycleApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return LifeCycleApp.context;
    }

}
