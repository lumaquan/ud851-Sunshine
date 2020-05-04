package com.example.android.sunshine;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements ForecastAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    }

    private void loadWeatherData() {
        String preferredLocation = SunshinePreferences.getPreferredWeatherLocation(this);
        URL urlPreferredLocation = NetworkUtils.buildUrlForZipCodeCodeCountry(preferredLocation);
        if (urlPreferredLocation != null)
            new FetchWeatherDataTask().execute(urlPreferredLocation);
    }

    @Override
    public void onItemClicked(String weather) {
        DetailActivity.launch(this, weather);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        loadWeatherData();
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchWeatherDataTask extends AsyncTask<URL, Void, String[]> {

        @Override
        protected void onPreExecute() {
            // mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(URL... urls) {
            URL url = urls[0];
            try {
                String weatherJsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                return OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(getBaseContext(), weatherJsonResponse);
            } catch (JSONException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] forecast) {
            //mLoadingProgressBar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            if (forecast != null) {
                showWeatherDataView(true);
                mForecastAdapter.setForecast(forecast);
                mForecastRecyclerView.scheduleLayoutAnimation();
            } else {
                showWeatherDataView(false);
            }
        }

    }

    private void showWeatherDataView(boolean showWeatherData) {
        mErrorTextView.setVisibility(showWeatherData ? View.INVISIBLE : View.VISIBLE);
        mForecastRecyclerView.setVisibility(showWeatherData ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh_action) {
            mForecastAdapter.setForecast(null);
            onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}