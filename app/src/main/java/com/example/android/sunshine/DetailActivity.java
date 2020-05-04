package com.example.android.sunshine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private static final String WEATHER_EXTRA = "weather_extra";

    @BindView(R.id.tv_weather)
    TextView mWeatherTextView;

    private String mutableState;

    public static void launch(Context context, String weather) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(WEATHER_EXTRA, weather);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent.hasExtra(WEATHER_EXTRA)) {
            mWeatherTextView.setText(intent.getStringExtra(WEATHER_EXTRA));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.share_action) {
            Intent intent = ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle("Please select how to share...")
                    .setSubject("My chosen weather")
                    .setText(mWeatherTextView.getText())
                    .setType("text/plain")
                    .createChooserIntent();
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
