/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        mWeatherTextView = findViewById(R.id.tv_weather_data);
    }

    private void loadWeatherData() {
        String preferredLocation = SunshinePreferences.getPreferredWeatherLocation(this);
        URL urlPreferredLocation = NetworkUtils.buildUrlForZipCodeCodeCountry(preferredLocation);
        if (urlPreferredLocation != null)
            new FetchWeatherDataTask().execute(urlPreferredLocation);
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchWeatherDataTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            try {
                return NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                mWeatherTextView.setText(s);
            } else {
                mWeatherTextView.setText(getString(R.string.error_loading_weather));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh_action:
                loadWeatherData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}