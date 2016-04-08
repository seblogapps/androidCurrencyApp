package com.example.stognacci.currencyapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import com.example.stognacci.currencyapp.Constants;
import com.example.stognacci.currencyapp.utils.LogUtils;
import com.example.stognacci.currencyapp.value_objects.Currency;

import java.util.ArrayList;

/**
 * Created by stognacci on 31/03/2016.
 */
public class CurrencyTableHelper {

    private static final String LOG_TAG = CurrencyTableHelper.class.getSimpleName();
    private CurrencyDatabaseAdapter mAdapter;

    private final String[] dbColumns = new String[]{Constants.KEY_ID, Constants.KEY_BASE, Constants.KEY_DATE,
            Constants.KEY_RATE, Constants.KEY_NAME};

    public CurrencyTableHelper(CurrencyDatabaseAdapter adapter) {
        this.mAdapter = adapter;
    }

    public long insertCurrency(Currency currency) {
        ArrayList<Currency> currencies = getCurrencyHistory(currency.getBase(), currency.getName(), currency.getDate());
        if (currencies.size() == 0) {

            ContentValues initialValues = new ContentValues();
            initialValues.put(Constants.KEY_BASE, currency.getBase());
            initialValues.put(Constants.KEY_DATE, currency.getDate());
            initialValues.put(Constants.KEY_RATE, currency.getRate());
            initialValues.put(Constants.KEY_NAME, currency.getName());

            long id = mAdapter.getWritableDatabase().insert(
                    Constants.CURRENCY_TABLE, null, initialValues);
            mAdapter.getWritableDatabase().close();
            LogUtils.log(LOG_TAG, "Inserted record to db n: " + id);
            return id;
        } else {
            LogUtils.log(LOG_TAG, "No record added");
        }
        return currencies.get(0).get_id();
    }


    public ArrayList<Currency> getCurrencyHistory(String base, String name, String date) {
        ArrayList<Currency> currencies = new ArrayList<>();
        String dbSelection = Constants.KEY_BASE + " = '" + base + "' AND " + Constants.KEY_NAME + " = '" +
                name + "' AND " + Constants.KEY_DATE + " = '" + date + "'";
        Cursor cursor = mAdapter.getWritableDatabase().query(
                Constants.CURRENCY_TABLE, dbColumns, dbSelection, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                currencies.add(parseCurrency(cursor));
            }
            cursor.close();
        }
        return currencies;
    }

    public ArrayList<Currency> getCurrencyHistory(String base, String name) {
        ArrayList<Currency> currencies = new ArrayList<>();
        String dbSelection = Constants.KEY_BASE + " = '" + base + "' AND " + Constants.KEY_NAME + " = '" +
                name + "'";
        Cursor cursor = mAdapter.getWritableDatabase().query(
                Constants.CURRENCY_TABLE, dbColumns, dbSelection, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                currencies.add(parseCurrency(cursor));
            }
            cursor.close();
        }
        return currencies;
    }

    public Currency getCurrency(long _id) throws SQLException {
        String dbSelection = Constants.KEY_ID + " = " + _id;
        Cursor cursor = mAdapter.getWritableDatabase().query(
                Constants.CURRENCY_TABLE,
                dbColumns,
                dbSelection, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return parseCurrency(cursor);
            }
        }
        return null;
    }
//            if (cursor.moveToFirst()) {
//                do {
//                    currencies.add(parseCurrency(cursor));
//                }
//                while (cursor.moveToNext());

    private Currency parseCurrency(Cursor cursor) {
        Currency currency = new Currency();
        currency.set_id(cursor.getLong(cursor.getColumnIndex(Constants.KEY_ID)));
        currency.setBase(cursor.getString(cursor.getColumnIndex(Constants.KEY_BASE)));
        currency.setDate(cursor.getString(cursor.getColumnIndex(Constants.KEY_DATE)));
        currency.setRate(cursor.getDouble(cursor.getColumnIndex(Constants.KEY_RATE)));
        currency.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));

        return currency;
    }

    public void clearCurrencyTable() {
        mAdapter.getWritableDatabase().delete(Constants.CURRENCY_TABLE, null, null);
    }
}
