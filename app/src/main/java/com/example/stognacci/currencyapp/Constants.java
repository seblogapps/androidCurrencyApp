package com.example.stognacci.currencyapp;

/**
 * Created by stognacci on 24/03/2016.
 */
public class Constants {

    // URL for retrieving currency data
    public static final String CURRENCY_URL = "http://api.fixer.io/latest?base=";

    // Constants for json response
    public static final String BASE = "base";
    public static final String DATE = "date";
    public static final String RATES = "rates";

    // Constants for CurrencyService and receiver
    public static final String URL = "url";
    public static final String RECEIVER = "receiver";
    public static final String RESULT = "reseult";
    public static final String CURRENCY_BASE = "base";
    public static final String CURRENCY_NAME = "name";
    public static final String REQUEST_ID = "requestId";
    public static final String BUNDLE = "bundle";
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    // Constants for Database and tables
    public static final String DATABASE_NAME = "Currency-DB";

    public static final String CURRENCY_TABLE = "currencies";
    public static final String KEY_ID = "_id";
    public static final String KEY_BASE = "base";
    public static final String KEY_DATE = "date";
    public static final String KEY_RATE = "rate";
    public static final String KEY_NAME = "name";

    // Max number of retrieval in background
    public static final int MAX_DOWNLOADS = 5;

    // Constants for currency codes and names
    public static final int CURRENCY_CODE_SIZE = 32;

    public static final String[] CURRENCY_CODES = {
            "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP", "HKD", "HRK",
            "HUF", "IDR", "ILS", "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN",
            "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
    };

    public static final String[] CURRENCY_NAMES = {
            "Australian Dollar", "Bulgarian Lev", "Brazilian Real", "Canadian Dollar", "Swiss Franc",
            "Yuan Renminbi", "Czech Koruna", "Danish Krone", "Euro", "Pound Sterling",
            "Hong Kong Dollar", "Croatian Kuna", "Hungarian Forint", "Indonesian Rupiah",
            "Israeli New Shekel", "Indian Rupee", "Japanese Yen", "Korean Won", "Mexican Nuevo Peso",
            "Malaysian Ringgit", "Norwegian Krone", "New Zealand Dollar", "Philippine Peso",
            "Polish Zloty", "Romanian New Leu", "Belarussian Ruble", "Swedish Krona",
            "Singapore Dollar", "Thai Baht", "Turkish Lira", "US Dollar", "South African Rand"
    };

    // Constants for notification
    public static final int NOTIFICATION_ID = 100;
    public static final int REQUEST_ID_NUM = 101;

    // Constants for SharedPreferences
    public static final String CURRENCY_PREFERENCES = "CURRENCY_PREFERENCES";
    public static final String BASE_CURRENCY = "BASE_CURRENCY";
    public static final String TARGET_CURRENCY = "TARGET_CURRENCY";
    public static final String SERVICE_REPETITION = "SERVICE_REPETITION";
    public static final String NUM_DOWNLOADS = "NUM_DOWNLOADS";

    // Constants for Http connection
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;

}