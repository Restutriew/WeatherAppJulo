package com.julo.weatherappjulo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.julo.weatherappjulo.adapter.WeatherAdapter;
import com.julo.weatherappjulo.database.DBHelper;
import com.julo.weatherappjulo.model.WeatherModel;
import com.julo.weatherappjulo.templaterestu.TextFuntion;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    int PERMISSION_CODE = 1;
    String cityName;
    TextView tv_city, tv_humidity, tv_wind, tv_temp, tv_weatherMain, tv_weatherDesc, tv_dateWeather;
    ImageView img_iconWeather, img_iconSearch, img_favouriteCity, img_favouriteList;
    EditText et_searchCity;
    RecyclerView rv_listForecast;
    SwipeRefreshLayout srl_refreshMain;
    DBHelper DB;
    LinearLayout ln_topBar;
    private ArrayList<WeatherModel> weatherList;
    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initiate view
        tv_city = findViewById(R.id.tv_city);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_wind = findViewById(R.id.tv_wind);
        tv_temp = findViewById(R.id.tv_temp);
        tv_weatherMain = findViewById(R.id.tv_weatherMain);
        tv_weatherDesc = findViewById(R.id.tv_weatherDesc);
        img_iconWeather = findViewById(R.id.img_iconWeather);
        img_iconSearch = findViewById(R.id.img_iconSearch);
        et_searchCity = findViewById(R.id.et_searchCity);
        tv_dateWeather = findViewById(R.id.tv_dateWeather);
        srl_refreshMain = findViewById(R.id.srl_refreshMain);
        img_favouriteCity = findViewById(R.id.img_favouriteCity);
        img_favouriteList = findViewById(R.id.img_favouriteList);
        ln_topBar = findViewById(R.id.ln_topBar);


//        initiate dbhelper sqlite
        DB = new DBHelper(this);

//        initiate list and recyclerview for forecast
        weatherList = new ArrayList<>();
        rv_listForecast = findViewById(R.id.rv_listForecast);
//        make recyclerview cant scroll
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rv_listForecast.setLayoutManager(linearLayoutManager);
        rv_listForecast.setHasFixedSize(true);

//        initiate location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

//        initiate location to get location from user
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(), location.getLatitude());

//        checking did MainActivity is launch at the first time or called from FavouriteActivity
        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        if (intent.hasExtra("sendCity")) {
            String cityGet = data.getString("sendCity");
            getWeatherNow(cityGet);
            loadForecast(cityGet);
            ln_topBar.setVisibility(View.GONE);
        } else {
            tv_city.setText(cityName);
            getWeatherNow(cityName);
            loadForecast(cityName);
        }

        Log.d("cityname", cityName);

//        add function when icon search clicked then search weather data by city inserted
        img_iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchCity = et_searchCity.getText().toString();
                if (searchCity.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Insert City Name!", Toast.LENGTH_SHORT).show();
                } else {
                    getWeatherNow(searchCity);
                    loadForecast(searchCity);
                }
            }
        });

//        function for swipe to refresh and get data if user want to refresh
        srl_refreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherNow(cityName);
                loadForecast(cityName);
                srl_refreshMain.setRefreshing(false);
            }
        });

//        setonclick favourite adding data to database
        img_favouriteCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityTODB = tv_city.getText().toString();

                Boolean checkinsertData = DB.insertCityData(cityTODB);
                if (checkinsertData) {
                    Toast.makeText(getApplicationContext(), "City added to Favourite list!", Toast.LENGTH_SHORT).show();
                    img_favouriteCity.setImageResource(R.drawable.icon_favorit_blue);
                } else {
                    DB.deleteCityData(cityTODB);
                    img_favouriteCity.setImageResource(R.drawable.icon_favorit_line);
                    Toast.makeText(getApplicationContext(), "City removed from Favourite list!", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        function to go to favourite list
        img_favouriteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToFavouriteList = new Intent(getApplicationContext(), FavouriteActivity.class);
                startActivity(goToFavouriteList);
            }
        });

    }

    //    function to load forecast data then insert all of data into recyclerview
    private void loadForecast(String cityName) {
        String URL_READ = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&cnt=24&appid=9a0f84e13a718a9bd63afef90a9d6b8e";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (weatherList.size() > 0) {
                            weatherList.clear();
                        }
                        try {

                            JSONObject jsonObjectMain = new JSONObject(response);
                            JSONArray jsonarrayList = jsonObjectMain.getJSONArray("list");

                            if (jsonarrayList.length() > 0) {
                                for (int i = 0; i < jsonarrayList.length(); i++) {
                                    JSONObject jsonObject = jsonarrayList.getJSONObject(i);
//                                  getting weather data
                                    String res_tempNow = jsonObject.getJSONObject("main").getString("temp").trim();
                                    String res_humidityNow = jsonObject.getJSONObject("main").getString("humidity").trim();
                                    String res_windNow = jsonObject.getJSONObject("wind").getString("speed").trim();

                                    JSONArray arrayWeather = jsonObject.getJSONArray("weather");
                                    JSONObject jsonObjectWeather = arrayWeather.getJSONObject(0);

                                    String res_weatherMainNow = jsonObjectWeather.getString("main");
                                    String res_weatherDescNow = jsonObjectWeather.getString("description");
                                    String res_weatherIconNow = jsonObjectWeather.getString("icon");

                                    String res_date = jsonObject.getString("dt_txt");

                                    Log.d("weatherjulo", res_tempNow);
                                    Log.d("weatherjulo", res_humidityNow);
                                    Log.d("weatherjulo", res_windNow);
                                    Log.d("weatherjulo", res_weatherMainNow);
                                    Log.d("weatherjulo", res_weatherDescNow);
                                    Log.d("weatherjulo", res_weatherIconNow);
                                    Log.d("weatherjulo", res_date);

//                                    adding all data into array list so all data can inserted into recyclerview
                                    weatherList.add(new WeatherModel(res_tempNow, res_humidityNow, res_windNow, res_weatherMainNow, res_weatherDescNow, res_weatherIconNow, res_date));
                                    weatherAdapter = new WeatherAdapter(getApplicationContext(), weatherList);
                                    rv_listForecast.setAdapter(weatherAdapter);

//                                    setting onclick at every data exist at recyclerciew then show it into main box
                                    weatherAdapter.setOnItemClickCallback(new WeatherAdapter.OnItemClickCallback() {
                                        @Override
                                        public void onItemClicked(WeatherModel data) {
                                            tv_humidity.setText(data.getHumidity());
                                            tv_wind.setText(data.getWind_speed() + " km/h");

                                            //converting temp from kelvin to celcius
                                            double tempcelcius = Double.parseDouble(data.getTemp()) - 273.15;
                                            String formatted = String.format("%.2f", (tempcelcius)) + " °C";
                                            tv_temp.setText(formatted);

                                            tv_weatherMain.setText(data.getWeather_main());
                                            tv_weatherDesc.setText(data.getWeather_desc());

                                            String img_weatherIcon = "http://openweathermap.org/img/wn/" + data.getIcon() + "@4x.png";
                                            Picasso.with(MainActivity.this).load(img_weatherIcon).into(img_iconWeather);

                                            SimpleDateFormat timeBefore = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            SimpleDateFormat timeAfter = new SimpleDateFormat("dd/MM HH:mm");
                                            try {
                                                Date date = timeBefore.parse(data.getTime());
                                                tv_dateWeather.setText(timeAfter.format(date));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Data Doesnt Exist!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "No Internet Connection or data doesnt exixt!", Toast.LENGTH_LONG).show();
                        }

                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            }
        }) {
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    //    checking permission is granted or not. if not app closed
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Please provide the permission!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    //get recent Weather Data
    private void getWeatherNow(String cityNameNow) {
        String URL_READ = "https://api.openweathermap.org/data/2.5/weather?q=" + cityNameNow + "&appid=9a0f84e13a718a9bd63afef90a9d6b8e";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

//                            getting weather data
                            String res_tempNow = jsonObject.getJSONObject("main").getString("temp").trim();
                            String res_humidityNow = jsonObject.getJSONObject("main").getString("humidity").trim();
                            String res_windNow = jsonObject.getJSONObject("wind").getString("speed").trim();

                            JSONArray arrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = arrayWeather.getJSONObject(0);

                            String res_weatherMainNow = jsonObjectWeather.getString("main");
                            String res_weatherDescNow = jsonObjectWeather.getString("description");
                            String res_weatherIconNow = jsonObjectWeather.getString("icon");

                            String res_cityName = jsonObject.getString("name");

                            tv_city.setText(res_cityName);

//                            checking is there city data loaded is exist at database or not, if exist then change icon to favourite full
                            Boolean checkdataFavourite = DB.checkDataExist(res_cityName);
                            if (checkdataFavourite) {
                                Log.d("cekDB", "data exist at sqlite");
                                img_favouriteCity.setImageResource(R.drawable.icon_favorit_blue);
                            } else {
                                Log.d("cekDB", "data not exist at sqlite");
                                img_favouriteCity.setImageResource(R.drawable.icon_favorit_line);
                            }

                            Log.d("icon", res_weatherIconNow);

                            Log.d("weatherjulo", res_tempNow);
                            Log.d("weatherjulo", res_humidityNow);
                            Log.d("weatherjulo", res_windNow);

                            tv_humidity.setText(res_humidityNow);
                            tv_wind.setText(res_windNow + " km/h");

//                            converting temp from kelvin to celcius
                            double tempcelcius = Double.parseDouble(res_tempNow) - 273.15;
                            String formatted = String.format("%.2f", (tempcelcius)) + " °C";
                            tv_temp.setText(formatted);

                            tv_weatherMain.setText(res_weatherMainNow);

                            TextFuntion textFuntion = new TextFuntion();
                            tv_weatherDesc.setText(textFuntion.convertUpperCase(res_weatherDescNow));

                            String img_weatherIcon = "http://openweathermap.org/img/wn/" + res_weatherIconNow + "@4x.png";
                            Picasso.with(MainActivity.this).load(img_weatherIcon).into(img_iconWeather);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Data Doesnt Exist!", Toast.LENGTH_LONG).show();
                        }

                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "No Internet Connections!", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    //    get user city name
    public String getCityName(double longitude, double latitude) {
        String cityName = "A";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("cityName", "City Not Found");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }
}