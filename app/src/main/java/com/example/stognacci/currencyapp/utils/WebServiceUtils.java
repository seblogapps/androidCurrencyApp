package com.example.stognacci.currencyapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.example.stognacci.currencyapp.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stognacci on 24/03/2016.
 */
public class WebServiceUtils {

    public static final String LOG_TAG = WebServiceUtils.class.getSimpleName();

    public static JSONObject requestJSONObject(String serviceURL) {
        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(serviceURL);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(Constants.READ_TIMEOUT);

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                LogUtils.log(LOG_TAG, "Unauthorized access!");
            } else if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                LogUtils.log(LOG_TAG, "404 Page not found");
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                LogUtils.log(LOG_TAG, "URL Response error");
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return new JSONObject(convertInputStreamToString(in));
        } catch (IOException | JSONException e) {
            LogUtils.log(LOG_TAG, e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    private static String convertInputStreamToString(InputStream inStream) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
        String responseText;
        try {
            responseText = bufferedReader.readLine();
            while (responseText != null) {
                stringBuilder.append(responseText);
                responseText = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        return connectivityManager != null &&
                connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
