package com.example.stognacci.currencyapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.example.stognacci.currencyapp.Constants;
import com.example.stognacci.currencyapp.helpers.CurrencyParserHelper;
import com.example.stognacci.currencyapp.utils.LogUtils;
import com.example.stognacci.currencyapp.utils.WebServiceUtils;
import com.example.stognacci.currencyapp.value_objects.Currency;

import org.json.JSONObject;

/**
 * Created by stognacci on 24/03/2016.
 */
public class CurrencyService extends IntentService {

    private static final String LOG_TAG = CurrencyService.class.getSimpleName();

    public CurrencyService(String name) {
        super(LOG_TAG);
    }

    public CurrencyService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.log(LOG_TAG, "Currency Service has started");
        Bundle intentBundle = intent.getBundleExtra(Constants.BUNDLE);
        final ResultReceiver receiver = intentBundle.getParcelable(Constants.RECEIVER);
        Parcel parcel = Parcel.obtain();
        receiver.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ResultReceiver receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel);
        parcel.recycle();

        String url = intentBundle.getString(Constants.URL);
        String currencyName = intentBundle.getString(Constants.CURRENCY_NAME);

        Bundle bundle = new Bundle();
        if (url != null && !TextUtils.isEmpty(url)) {
            receiverForSending.send(Constants.STATUS_RUNNING, Bundle.EMPTY);
            if (WebServiceUtils.hasInternetConnection(getApplicationContext())) {
                try {
                    JSONObject jsonObject = WebServiceUtils.requestJSONObject(url);
                    if (jsonObject != null) {
                        Currency currency = CurrencyParserHelper.parseCurrency(jsonObject, currencyName);
                        bundle.putParcelable(Constants.RESULT, currency);
                        receiverForSending.send(Constants.STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiverForSending.send(Constants.STATUS_ERROR, bundle);
                }
            } else {
                LogUtils.log(LOG_TAG, "No internet connection");
            }
        }
        LogUtils.log(LOG_TAG, "Currency Service has stopped");
        stopSelf();
    }
}