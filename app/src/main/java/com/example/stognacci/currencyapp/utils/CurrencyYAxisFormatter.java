package com.example.stognacci.currencyapp.utils;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by stognacci on 07/04/2016.
 */
public class CurrencyYAxisFormatter implements YAxisValueFormatter {

    private DecimalFormat mDecimalFormat;

    public CurrencyYAxisFormatter() {
        //Format the given float number with min 2 significant digits and max 4 significant digits
        mDecimalFormat = new DecimalFormat("@@##");
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mDecimalFormat.format(value);
    }
}
