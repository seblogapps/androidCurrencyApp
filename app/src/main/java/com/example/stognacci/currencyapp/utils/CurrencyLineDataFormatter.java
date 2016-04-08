package com.example.stognacci.currencyapp.utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by stognacci on 07/04/2016.
 */
public class CurrencyLineDataFormatter implements ValueFormatter {

    private DecimalFormat mDecimalFormat;

    public CurrencyLineDataFormatter() {
        //Format the given float number with min 2 significant digits and max 4 significant digits
        mDecimalFormat = new DecimalFormat("@@##");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mDecimalFormat.format(value);
    }
}
