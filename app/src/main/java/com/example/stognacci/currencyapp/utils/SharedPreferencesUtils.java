package com.example.stognacci.currencyapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.stognacci.currencyapp.Constants;

/**
 * Created by stognacci on 04/04/2016.
 */
public class SharedPreferencesUtils {

    private static SharedPreferences mSharedPreferences = LifeCycleApp.getAppContext().getSharedPreferences(
            Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE);

    public static String getCurrency(boolean isBaseCurrency) {
        return mSharedPreferences.getString(
                isBaseCurrency ? Constants.BASE_CURRENCY : Constants.TARGET_CURRENCY,
                isBaseCurrency ? "EUR" : "USD");
    }

    public static void updateCurrency(String currency, boolean isBaseCurrency) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(isBaseCurrency ? Constants.BASE_CURRENCY : Constants.TARGET_CURRENCY, currency);
        editor.apply();
    }

    public static int getServiceRepetition() {
        return mSharedPreferences.getInt(Constants.SERVICE_REPETITION, 0);
    }

    public static void updateServiceRepetition(int serviceRepetition) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.SERVICE_REPETITION, serviceRepetition);
        editor.apply();
    }

    public static int getNumDownloads() {
        return mSharedPreferences.getInt(Constants.NUM_DOWNLOADS, 0);
    }

    public static void updateNumDownloads(int numDownloads) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.NUM_DOWNLOADS, numDownloads);
        editor.apply();
    }
}

