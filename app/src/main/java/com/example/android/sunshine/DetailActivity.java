package com.example.android.sunshine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private static final String WEATHER_EXTRA = "weather_extra";
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
    }
}
