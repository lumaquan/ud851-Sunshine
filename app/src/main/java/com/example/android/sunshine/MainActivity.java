package com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<String[]>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int WEATHER_FORECAST_LOADER_ID = 783;
    private static final String WEATHER_FORECAST_EXTRA = "forecast_extra";
    private static boolean sharedPreferencesUpdated = false;

    @BindView(R.id.rv_forecast)
    RecyclerView mForecastRecyclerView;
    @BindView(R.id.tv_error_loading)
    TextView mErrorTextView;
    @BindView(R.id.pb_weather_loading)
    ProgressBar mLoadingProgressBar;
    @BindView(R.id.srl_forecast)
    SwipeRefreshLayout swipeRefreshLayout;

    private ForecastAdapter mForecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        ButterKnife.bind(this);
        initialSetup();
    }

    private void initialSetup() {
        mForecastRecyclerView.setHasFixedSize(true);
        mForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mForecastAdapter = new ForecastAdapter(null, this);
        mForecastRecyclerView.setAdapter(mForecastAdapter);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_falldown);
        mForecastRecyclerView.setLayoutAnimation(controller);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    private void loadWeatherData() {
        String preferredLocation = SunshinePreferences.getPreferredWeatherLocation(this);
        URL urlPreferredLocation = NetworkUtils.buildUrlForZipCodeCodeCountry(preferredLocation);
        if (urlPreferredLocation != null) {
            Bundle forecastURL = new Bundle();
            forecastURL.putSerializable(WEATHER_FORECAST_EXTRA, urlPreferredLocation);
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader loader = loaderManager.getLoader(WEATHER_FORECAST_LOADER_ID);
            if (loader == null) {
                loaderManager.initLoader(WEATHER_FORECAST_LOADER_ID, forecastURL, this);
            } else {
                loaderManager.restartLoader(WEATHER_FORECAST_LOADER_ID, forecastURL, this);
            }
        }
    }

    @Override
    public void onItemClicked(String weather) {
        DetailActivity.launch(this, weather);
    }

    @Override
    public void onRefresh() {
        loadWeatherData();
    }

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable final Bundle args) {

        return new AsyncTaskLoader<String[]>(this) {

            private String[] resultsCached;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) return;

                if (resultsCached != null) {
                    deliverResult(resultsCached);
                    return;
                }
                swipeRefreshLayout.setRefreshing(true);
                forceLoad();
            }

            @Nullable
            @Override
            public String[] loadInBackground() {
                try {
                    assert args != null;
                    URL url = (URL) args.getSerializable(WEATHER_FORECAST_EXTRA);
                    Thread.sleep(2000);
                    String weatherJsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                    return OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(getBaseContext(), weatherJsonResponse);
                } catch (JSONException | IOException | InterruptedException e) {
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable String[] data) {
                resultsCached = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] forecast) {
        swipeRefreshLayout.setRefreshing(false);
        if (forecast != null) {
            showWeatherDataView(true);
            mForecastAdapter.setForecast(forecast);
            mForecastRecyclerView.scheduleLayoutAnimation();
        } else {
            showWeatherDataView(false);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {
        loader.reset();
    }

    private void showWeatherDataView(boolean showWeatherData) {
        mErrorTextView.setVisibility(showWeatherData ? View.INVISIBLE : View.VISIBLE);
        mForecastRecyclerView.setVisibility(showWeatherData ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_action) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferencesUpdated) {
            loadWeatherData();
            sharedPreferencesUpdated = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sharedPreferencesUpdated = true;
    }
}