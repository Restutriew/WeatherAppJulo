package com.julo.weatherappjulo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.julo.weatherappjulo.R;
import com.julo.weatherappjulo.model.WeatherModel;
import com.julo.weatherappjulo.templaterestu.TextFuntion;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    String temp, humidity, wind_speed, weather_main, weather_desc, icon, time;


    private Context mContext;
    private ArrayList<WeatherModel> mWeatherList;
    private WeatherAdapter.OnItemClickCallback onItemClickCallback;

    public WeatherAdapter(Context mContext, ArrayList<WeatherModel> mWeatherList) {
        this.mContext = mContext;
        this.mWeatherList = mWeatherList;
    }

    public void setOnItemClickCallback(WeatherAdapter.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_weather_forecast, parent, false);
        return new WeatherAdapter.WeatherViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherModel currentItem = mWeatherList.get(position);
        temp = currentItem.getTemp();
        humidity = currentItem.getHumidity();
        wind_speed = currentItem.getWind_speed();
        weather_main = currentItem.getWeather_main();
        weather_desc = currentItem.getWeather_desc();
        icon = currentItem.getIcon();
        time = currentItem.getTime();

//        2022-05-28 03:00:00
        SimpleDateFormat timeBefore = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat timeAfter = new SimpleDateFormat("dd/MM HH:mm");
        try {
            Date date = timeBefore.parse(time);
            holder.tv_dateTime.setText(timeAfter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextFuntion textFuntion = new TextFuntion();

        holder.tv_weatherMainList.setText(weather_main);
        holder.tv_weatherDescList.setText(textFuntion.convertUpperCase(weather_desc));
        String img_weatherIcon = "http://openweathermap.org/img/wn/" + icon + "@4x.png";
        Picasso.with(mContext.getApplicationContext()).load(img_weatherIcon).into(holder.img_iconWeatherList);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(mWeatherList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public interface OnItemClickCallback {
        void onItemClicked(WeatherModel data);
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_dateTime;
        public TextView tv_weatherMainList;
        public TextView tv_weatherDescList;
        public ImageView img_iconWeatherList;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_dateTime = itemView.findViewById(R.id.tv_dateTime);
            tv_weatherMainList = itemView.findViewById(R.id.tv_weatherMainList);
            tv_weatherDescList = itemView.findViewById(R.id.tv_weatherDescList);
            img_iconWeatherList = itemView.findViewById(R.id.img_iconWeatherList);


        }
    }
}
