package com.example.stognacci.currencyapp;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stognacci.currencyapp.adapters.CurrencyAdapter;
import com.example.stognacci.currencyapp.database.CurrencyDatabaseAdapter;
import com.example.stognacci.currencyapp.database.CurrencyTableHelper;
import com.example.stognacci.currencyapp.receivers.CurrencyReceiver;
import com.example.stognacci.currencyapp.services.CurrencyService;
import com.example.stognacci.currencyapp.utils.AlarmUtils;
import com.example.stognacci.currencyapp.utils.CurrencyLineDataFormatter;
import com.example.stognacci.currencyapp.utils.CurrencyYAxisFormatter;
import com.example.stognacci.currencyapp.utils.LifeCycleUtils;
import com.example.stognacci.currencyapp.utils.LogUtils;
import com.example.stognacci.currencyapp.utils.NotificationUtils;
import com.example.stognacci.currencyapp.utils.SharedPreferencesUtils;
import com.example.stognacci.currencyapp.value_objects.Currency;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements CurrencyReceiver.Receiver {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String mBaseCurrency = Constants.CURRENCY_CODES[8];
    private String mTargetCurrency = Constants.CURRENCY_CODES[9];

    private CurrencyTableHelper mCurrencyTableHelper;

    private int mServiceRepetition = AlarmUtils.REPEAT.REPEAT_EVERY_MINUTE.ordinal();

    private CoordinatorLayout mLogLayout;
    private FloatingActionButton mFloatingActionButton;
    private boolean mIsLogVisible = true;
    private boolean mIsFabVisibile = true;

    private ListView mBaseCurrencyListView;
    private ListView mTargetCurrencyListView;
    private LineChart mLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetDownloads();
        initCurrencies();
        initDB();
        initToolbar();
        initSpinner();
        initCurrencyList();
        initLineChart();
        addActionButtonListener();
        showLogs();

        mLogLayout = (CoordinatorLayout) findViewById(R.id.log_layout);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mServiceRepetition = SharedPreferencesUtils.getServiceRepetition();
        retrieveCurrencyExchangeRate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.setLogListener(null);
    }

    @Override
    public void onReceiveResult(int resultCode, final Bundle resultData) {
        switch (resultCode) {
            case Constants.STATUS_RUNNING:
                LogUtils.log(LOG_TAG, "Currency Service running");
                break;

            case Constants.STATUS_FINISHED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Currency currencyParcel = resultData.getParcelable(Constants.RESULT);
                        if (currencyParcel != null) {
                            String messageForLog = "Currency: " + currencyParcel.getBase() + " - "
                                    + currencyParcel.getName() + " = " + currencyParcel.getRate();
                            LogUtils.log(LOG_TAG, messageForLog);
                            long _id = mCurrencyTableHelper.insertCurrency(currencyParcel);
                            Currency currency = null;
                            try {
                                currency = mCurrencyTableHelper.getCurrency(_id);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                LogUtils.log(LOG_TAG, "Error reading from db");
                            }
                            if (currency != null) {
                                String dbLogMessage = "Currency (DB): " + currency.getBase() + " - "
                                        + currency.getName() + " = " + currency.getRate();
                                LogUtils.log(LOG_TAG, dbLogMessage);
                                NotificationUtils.showNotificationMessage(getApplicationContext(),
                                        getString(R.string.notification_title),
                                        dbLogMessage);
                            }
                            if (LifeCycleUtils.isAppInBackground()) {
                                int numDownloads = SharedPreferencesUtils.getNumDownloads();
                                SharedPreferencesUtils.updateNumDownloads(++numDownloads);
                                if (numDownloads == Constants.MAX_DOWNLOADS) {
                                    LogUtils.log(LOG_TAG, "Maximum download for the background process hit");
                                    mServiceRepetition = AlarmUtils.REPEAT.REPEAT_EVERY_DAY.ordinal();
                                    retrieveCurrencyExchangeRate();
                                }
                            } else {
                                updateLineChart();
                            }
                        }
                    }
                });
                break;

            case Constants.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                LogUtils.log(LOG_TAG, error);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void initDB() {
        CurrencyDatabaseAdapter currencyDatabaseAdapter = new CurrencyDatabaseAdapter(this);
        mCurrencyTableHelper = new CurrencyTableHelper(currencyDatabaseAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.time_frequency);
        spinner.setSaveEnabled(true);
        spinner.setSelection(SharedPreferencesUtils.getServiceRepetition(), false);
        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SharedPreferencesUtils.updateServiceRepetition(position);
                        mServiceRepetition = position;
                        if (position >= AlarmUtils.REPEAT.values().length) {
                            AlarmUtils.stopService();
                        } else {
                            retrieveCurrencyExchangeRate();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    private void initCurrencyList() {
        mBaseCurrencyListView = (ListView) findViewById(R.id.base_currency_list);
        mTargetCurrencyListView = (ListView) findViewById(R.id.target_currency_list);

        CurrencyAdapter baseCurrencyAdapter = new CurrencyAdapter(this);
        CurrencyAdapter targetCurrencyAdapter = new CurrencyAdapter(this);

        mBaseCurrencyListView.setAdapter(baseCurrencyAdapter);
        mTargetCurrencyListView.setAdapter(targetCurrencyAdapter);

        int baseCurrencyIndex = retrieveIndexOf(mBaseCurrency);
        int targetCurrencyIndex = retrieveIndexOf(mTargetCurrency);

        mBaseCurrencyListView.setItemChecked(baseCurrencyIndex, true);
        mTargetCurrencyListView.setItemChecked(targetCurrencyIndex, true);

        mBaseCurrencyListView.setSelection(baseCurrencyIndex);
        mTargetCurrencyListView.setSelection(targetCurrencyIndex);

        addCurrencySelectionListener();
    }

    private int retrieveIndexOf(String currency) {
        return Arrays.asList(Constants.CURRENCY_CODES).indexOf(currency);
    }


    private void addCurrencySelectionListener() {
        mBaseCurrencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBaseCurrency = Constants.CURRENCY_CODES[position];
                LogUtils.log(LOG_TAG, "Base currency changed to : " + mBaseCurrency);
                SharedPreferencesUtils.updateCurrency(mBaseCurrency, true);
                retrieveCurrencyExchangeRate();
            }
        });

        mTargetCurrencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTargetCurrency = Constants.CURRENCY_CODES[position];
                LogUtils.log(LOG_TAG, "Target currency changed to : " + mTargetCurrency);
                SharedPreferencesUtils.updateCurrency(mTargetCurrency, false);
                retrieveCurrencyExchangeRate();
            }
        });
    }

    private void initLineChart() {
        mLineChart = (LineChart) findViewById(R.id.line_chart);
        mLineChart.setNoDataText("No data");
        mLineChart.setHighlightPerDragEnabled(true);
        mLineChart.setHighlightPerTapEnabled(true);
        mLineChart.setTouchEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setPinchZoom(true);
        mLineChart.setDescriptionTextSize(16f);
        mLineChart.setDescriptionColor(ColorTemplate.getHoloBlue());


        LineData lineData = new LineData();
        lineData.setValueTextColor(Color.BLUE);
        mLineChart.setData(lineData);

        Legend legend = mLineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(ColorTemplate.getHoloBlue());

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);

        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setDrawGridLines(true);
        //yAxis.setAxisMaxValue(120f);
        yAxis.setSpaceTop(50f);
        yAxis.setSpaceBottom(20f);
        yAxis.setLabelCount(10, true);
        yAxis.setValueFormatter(new CurrencyYAxisFormatter());

        YAxis yAxisRight = mLineChart.getAxisRight();
        yAxisRight.setEnabled(false);
    }


    private void updateLineChart() {
        mLineChart.setDescription(getString(R.string.graph_tabledescription, mBaseCurrency, mTargetCurrency));
        ArrayList<Currency> currencies = mCurrencyTableHelper.getCurrencyHistory(mBaseCurrency, mTargetCurrency);
        LineData lineData = mLineChart.getData();
        lineData.clearValues();
        for (Currency currency : currencies) {
            addChartEntry(currency.getDate(), currency.getRate());
        }
    }

    private void addChartEntry(String date, double value) {
        LineData lineData = mLineChart.getData();
        if (lineData != null) {
            lineData.setValueFormatter(new CurrencyLineDataFormatter());
            ILineDataSet lineDataSet = lineData.getDataSetByIndex(0);
            if (lineDataSet == null) {
                lineDataSet = createSet();
                lineData.addDataSet(lineDataSet);

            }
            if (!mLineChart.getData().getXVals().contains(date)) {
                lineData.addXValue(date);
            }
            lineData.addEntry(new Entry((float) value, lineDataSet.getEntryCount()), 0);
            mLineChart.notifyDataSetChanged();
        }
    }

    private ILineDataSet createSet() {
        LineDataSet lineDataSet = new LineDataSet(null, "Rate");
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(ColorTemplate.getHoloBlue());
        lineDataSet.setCircleColor(ColorTemplate.getHoloBlue());
        lineDataSet.setLineWidth(2f);
        //lineDataSet.setCircleSize(4f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setFillAlpha(65);
        lineDataSet.setFillColor(ColorTemplate.getHoloBlue());
        lineDataSet.setHighLightColor(Color.CYAN);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(12f);
        return lineDataSet;
    }

    private void initCurrencies() {
        mBaseCurrency = SharedPreferencesUtils.getCurrency(true);
        mTargetCurrency = SharedPreferencesUtils.getCurrency(false);
    }


    private void showLogs() {
        final TextView logText = (TextView) findViewById(R.id.log_text);
        LogUtils.setLogListener(new LogUtils.LogListener() {
            @Override
            public void onLogged(final StringBuffer log) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logText.setText(log.toString());
                        logText.invalidate();
                    }
                });
            }
        });
    }


    private void retrieveCurrencyExchangeRate() {
        if (mServiceRepetition < AlarmUtils.REPEAT.values().length) {
            CurrencyReceiver receiver = new CurrencyReceiver(new Handler());
            receiver.setReceiver(this);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), CurrencyService.class);
            intent.setExtrasClassLoader(CurrencyService.class.getClassLoader());

            Bundle bundle = new Bundle();
            String url = Constants.CURRENCY_URL + mBaseCurrency;
            bundle.putString(Constants.URL, url);
            bundle.putParcelable(Constants.RECEIVER, receiver);
            bundle.putInt(Constants.REQUEST_ID, Constants.REQUEST_ID_NUM);
            bundle.putString(Constants.CURRENCY_NAME, mTargetCurrency);
            bundle.putString(Constants.CURRENCY_BASE, mBaseCurrency);
            intent.putExtra(Constants.BUNDLE, bundle);
            //startService(intent); //Don't test starting the intent, but start now the alarm service
            AlarmUtils.startService(this, intent, AlarmUtils.REPEAT.values()[mServiceRepetition]);
        }
    }

    private void resetDownloads() {
        SharedPreferencesUtils.updateNumDownloads(0);
    }

    private void addActionButtonListener() {
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupMenu popupMenu = new PopupMenu(MainActivity.this, mFloatingActionButton, Gravity.TOP);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                Menu menuItem = popupMenu.getMenu();
                if (findViewById(R.id.currency_list_layout).getVisibility() != View.VISIBLE) {
                    menuItem.removeItem(R.id.graph);
                } else {
                    menuItem.removeItem(R.id.selection);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.clear_database:
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.log_layout), "Are you sure?", Snackbar.LENGTH_LONG);
                                snackbar.setAction("Yes", new SnackBarListener());
                                snackbar.show();
                                break;
                            case R.id.graph:
                                findViewById(R.id.currency_list_layout).setVisibility(View.GONE);
                                mLineChart.setVisibility(View.VISIBLE);
                                updateLineChart();
                                break;
                            case R.id.selection:
                                findViewById(R.id.currency_list_layout).setVisibility(View.VISIBLE);
                                mLineChart.setVisibility(View.GONE);
                                break;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private class SnackBarListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mCurrencyTableHelper.clearCurrencyTable();
            LogUtils.log(LOG_TAG, "Currency Database cleared");
            mLineChart.clearValues();
            updateLineChart();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.activity_clear_logs:
                LogUtils.clearLogs();
                return true;
            case R.id.activity_show_log:
                mIsLogVisible = !mIsLogVisible;
                item.setIcon(mIsLogVisible ? R.drawable.ic_keyboard_hide : R.drawable.ic_keyboard);
                mLogLayout.setVisibility(mIsLogVisible ? View.VISIBLE : View.GONE);
                break;
            case R.id.action_show_fab:
                mIsFabVisibile = !mIsFabVisibile;

                if (mIsFabVisibile) {
                    item.setIcon(R.drawable.ic_remove);
                    mFloatingActionButton.show();
                } else {
                    item.setIcon(R.drawable.ic_add);
                    mFloatingActionButton.hide();
                }

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
