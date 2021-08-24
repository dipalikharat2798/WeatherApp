package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.Adapter.WeatherAdapter;
import com.example.weatherapp.Model.WeatherModel;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout home_RL;
    private ProgressBar loading_pb;
    private TextView cityName_tv, temperature_tv, condition_tv;
    private RecyclerView weather_Rv;
    private TextInputEditText city_edt;
    private ImageView black_imgv, icon_imgv, search_iv;
    private ArrayList<WeatherModel> weatherModelArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int per_code=1;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        home_RL = findViewById(R.id.home_layout);
        loading_pb = findViewById(R.id.progress_bar);
        cityName_tv = findViewById(R.id.cityName_tv);
        temperature_tv = findViewById(R.id.temprature_tv);
        condition_tv = findViewById(R.id.condition_tv);
        weather_Rv = findViewById(R.id.weather_rv);
        city_edt = findViewById(R.id.city_edv);
        black_imgv = findViewById(R.id.blackshade);
        icon_imgv = findViewById(R.id.icon_img);
        search_iv = findViewById(R.id.search_img);
        weatherModelArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModelArrayList);
        weather_Rv.setAdapter(weatherAdapter);

        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},per_code);
        }

        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        city=getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(city);

        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city=city_edt.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter the city name", Toast.LENGTH_SHORT).show();
                }else {
                    cityName_tv.setText(city);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==per_code){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName="Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for (Address adr : addresses){
                if (adr!=null){
                    String city=adr.getLocality();
                    if (city!=null && !city.equals("")){
                        cityName=city;
                    }else {
                        Log.d("TAG", "getCityName:CITY NOT FOUND ");
                       // Toast.makeText(this, "User City Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=154c324421984f0da62112556212308&q=" + cityName + "&days=1&aqi=no&alerts=no";
        cityName_tv.setText(cityName);
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading_pb.setVisibility(View.GONE);
                home_RL.setVisibility(View.VISIBLE);
                weatherModelArrayList.clear();
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperature_tv.setText(temperature+"°C");
                    int isDay=response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    condition_tv.setText(condition);
                    Picasso.get().load("http:".concat(conditionIcon)).into(icon_imgv);
                    if (isDay==1){
                        Picasso.get().load("https://www.bing.com/images/search?view=detailV2&ccid=6t8MG8Pa&id=51F03873588F06C13306334CC7E22B404FCF3B53&thid=OIP.6t8MG8Pa8wqnBHkqdnxOiAHaEo&mediaurl=https%3a%2f%2fnewsexpressngr.com%2fimages%2fnews%2fSunny_weather.jpg&exph=375&expw=600&q=day+image+weather&simid=608008936486627333&FORM=IRPRST&ck=844E012013A3D42A6117320F62554DC8&selectedIndex=22&ajaxhist=0&ajaxserp=0").into(black_imgv);
                    }else {
                        Picasso.get().load("https://www.bing.com/images/search?view=detailV2&ccid=SwXMxUSg&id=6AE024988022E35EA0C6811B2E83080BB360BF03&thid=OIP.SwXMxUSgJhpA7IFJzKoP-gHaEK&mediaurl=https%3a%2f%2fcdn131.picsart.com%2f236447840046202.jpg%3fr1024x1024&exph=576&expw=1024&q=night+image+weather&simid=608014588671576785&FORM=IRPRST&ck=C8A0ED0A67C8E73462B5668A9D0531F3&selectedIndex=59&ajaxhist=0&ajaxserp=0").into(black_imgv);
                    }
                    JSONObject forecastObj=response.getJSONObject("forecast");
                    JSONObject forecastO=forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for (int i=0; i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String tempr = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherModelArrayList.add(new WeatherModel(time,tempr,img,wind));
                    }
                    weatherAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter the valide city name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}