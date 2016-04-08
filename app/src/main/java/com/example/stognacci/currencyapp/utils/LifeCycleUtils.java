package com.example.stognacci.currencyapp.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by stognacci on 01/04/2016.
 * Used to check if current app is in foreground or background
 */
public class LifeCycleUtils implements Application.ActivityLifecycleCallbacks {

    private static final String LOG_TAG = LifeCycleUtils.class.getSimpleName();

    private static int resumed;
    private static int stopped;
    private static int paused;
    private static int started;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        LogUtils.log(LOG_TAG, "Application went to foreground n: " + resumed + " times");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        LogUtils.log(LOG_TAG, "Application paused n: " + paused + " times");

    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        LogUtils.log(LOG_TAG, "Application went to background n: " + stopped + " times");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public static boolean isAppInBackground() {
        return paused >= resumed;
    }
}
