package com.example.stognacci.currencyapp.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.stognacci.currencyapp.Constants;
import com.example.stognacci.currencyapp.utils.LogUtils;

/**
 * Created by stognacci on 31/03/2016.
 */
public class CurrencyDatabaseAdapter extends SQLiteOpenHelper {

    private static final String LOG_TAG = CurrencyDatabaseAdapter.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String CURRENCY_TABLE_CREATE = "create table " +
            Constants.CURRENCY_TABLE + " (" +
            Constants.KEY_ID + " integer primary key autoincrement, " +
            Constants.KEY_BASE + " text not null, " +
            Constants.KEY_NAME + " text not null, " +
            Constants.KEY_RATE + " real, " +
            Constants.KEY_DATE + " date);";

    public CurrencyDatabaseAdapter(Context context) {
        super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CURRENCY_TABLE_CREATE);
            LogUtils.log(LOG_TAG, "Currency table created");
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.log(LOG_TAG, "Currency table creation error");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearCurrentTable(db);
        onCreate(db);
    }

    private void clearCurrentTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.CURRENCY_TABLE);
    }
}
