package com.example.android.sunshine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    interface OnItemClickListener {
        void onItemClicked(String weather);
    }

    private List<String> mForecast;
    private final OnItemClickListener mOnItemClicklistener;

    public ForecastAdapter(List<String> forecast, OnItemClickListener listener) {
        this.mForecast = forecast;
        this.mOnItemClicklistener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        if (mForecast != null) {
            return mForecast.size();
        }
        return 0;
    }

    public void setForecast(String[] forecast) {
        if (forecast == null) {
            mForecast = null;
        } else {
            mForecast = Arrays.asList(forecast);
        }
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView weatherData;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            weatherData = itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }

        void bind() {
            weatherData.setText(mForecast.get(getAdapterPosition()));
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClicklistener != null) {
                mOnItemClicklistener.onItemClicked(mForecast.get(getAdapterPosition()));
            }
        }
    }
}


