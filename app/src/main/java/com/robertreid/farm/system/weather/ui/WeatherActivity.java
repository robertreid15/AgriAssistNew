package com.robertreid.farm.system.weather.ui;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.robertreid.farm.system.R;
import com.robertreid.farm.system.weather.WeatherDataUtil;
import com.robertreid.farm.system.weather.api.RetrofitClient;
import com.robertreid.farm.system.weather.api.WeatherService;
import com.robertreid.farm.system.weather.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {

    String appId = "ba9035c097ac117bd1443aefc1f699c4";


    private TextView tempText;
    private TextView descrText;
    private TextView textCity;
    private ImageView imageWeather;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        tempText = findViewById(R.id.text_temp);
        descrText = findViewById(R.id.text_description);
        imageWeather = findViewById(R.id.image_weather);
        textCity = findViewById(R.id.text_city);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000000);
        mLocationRequest.setFastestInterval(100000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());


    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.d("weather", "location callback");
                Log.d("weather", String.valueOf(location.getLatitude()));

                String lat = String.valueOf(location.getLatitude());
                String lon = String.valueOf(location.getLongitude());
                getWeatherData(lat, lon);
            }
        }



    };

    private void getWeatherData(String lat, String lon) {
        Log.d("weather", "get weather data");

        //Create network service and cast it ot our Weather Service Interface
        WeatherService service = RetrofitClient.getInstance().retrofit.create(WeatherService.class);

        //Create a network call object
        Call<WeatherResponse> weatherData = service.getCurrentWeatherData(lat, lon, appId);




        weatherData.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {

                WeatherResponse data = response.body();
                Log.d("weather", String.valueOf(data.getId()));
                int temp = (int) (data.getMain().getTemp() - 273.15);
                imageWeather.setImageResource(WeatherDataUtil.updateWeatherIcon(data.getWeather().get(0).getId()));
                tempText.setText(temp + "°C");
                textCity.setText(data.getName());
                descrText.setText(data.getWeather().get(0).getDescription());

            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d("weather", t.getCause().getMessage());

            }
        });
    }

}
