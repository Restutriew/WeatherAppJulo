package com.julo.weatherappjulo.model;

public class WeatherModel {
    String temp;
    String humidity;
    String wind_speed;
    String weather_main;
    String weather_desc;
    String icon;
    String time;

    public WeatherModel(String temp, String humidity, String wind_speed, String weather_main, String weather_desc, String icon, String time) {
        this.temp = temp;
        this.humidity = humidity;
        this.wind_speed = wind_speed;
        this.weather_main = weather_main;
        this.weather_desc = weather_desc;
        this.icon = icon;
        this.time = time;
    }



    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(String wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String getWeather_main() {
        return weather_main;
    }

    public void setWeather_main(String weather_main) {
        this.weather_main = weather_main;
    }

    public String getWeather_desc() {
        return weather_desc;
    }

    public void setWeather_desc(String weather_desc) {
        this.weather_desc = weather_desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
