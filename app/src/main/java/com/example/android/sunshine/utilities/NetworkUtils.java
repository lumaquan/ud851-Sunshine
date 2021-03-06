
package com.example.android.sunshine.utilities;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String REAL_WEATHER_URL = "https://api.openweathermap.org/data/2.5/forecast/daily";

    private static final String FORECAST_BASE_URL = REAL_WEATHER_URL;

    private static final String format = "json";
    private static final String units = "metric";
    private static final int numDays = 14;
    private static final String apiKey = "56529b2d2e5475be0800bfcd97f3e1f0";

    private final static String QUERY_PARAM = "q";
    private final static String ZIP_PARAM = "zip";
    private final static String LAT_PARAM = "lat";
    private final static String LON_PARAM = "lon";
    private final static String FORMAT_PARAM = "mode";
    private final static String UNITS_PARAM = "units";
    private final static String DAYS_PARAM = "cnt";
    private final static String API_KEY_PARAM = "appid";

    private static void appendDefaultQueryParams(Uri.Builder builder) {
        builder.appendQueryParameter(FORMAT_PARAM, format);
        builder.appendQueryParameter(UNITS_PARAM, units);
        builder.appendQueryParameter(DAYS_PARAM, String.valueOf(numDays));
        builder.appendQueryParameter(API_KEY_PARAM, apiKey);
    }

    public static URL buildUrlForZipCodeCodeCountry(String zipCodeCountry) {
        Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon();
        builder.appendQueryParameter(ZIP_PARAM, zipCodeCountry);
        appendDefaultQueryParams(builder);
        try {
            return new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static URL buildUrlForCityCountry(String locationQuery) {
        Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon();
        builder.appendQueryParameter(QUERY_PARAM, locationQuery);
        appendDefaultQueryParams(builder);
        try {
            return new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static URL buildUrl(Double lat, Double lon) {
        Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon();
        builder.appendQueryParameter(LAT_PARAM, String.valueOf(lat));
        builder.appendQueryParameter(LON_PARAM, String.valueOf(lon));
        appendDefaultQueryParams(builder);
        try {
            return new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static String getResponseFromHttpUrl(@NonNull URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}