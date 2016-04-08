package com.example.stognacci.currencyapp.helpers;

import com.example.stognacci.currencyapp.Constants;
import com.example.stognacci.currencyapp.utils.LogUtils;
import com.example.stognacci.currencyapp.value_objects.Currency;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by stognacci on 24/03/2016.
 */
public class CurrencyParserHelper {

    private static final String LOG_TAG = CurrencyParserHelper.class.getSimpleName();

    public static Currency parseCurrency(JSONObject object, String currencyName) {
        Currency currency = new Currency();
        currency.setBase(object.optString(Constants.BASE));
        currency.setDate(object.optString(Constants.DATE));
        JSONObject rateObject = null;
        try {
            rateObject = object.getJSONObject(Constants.RATES);
            currency.setRate(rateObject.optDouble(currencyName));
        } catch (JSONException e) {
            LogUtils.log(LOG_TAG, "Failed to find valid rates" + e.getMessage());
        }
        currency.setName(currencyName);
        return currency;
    }
}
