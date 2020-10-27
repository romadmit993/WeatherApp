package com.romadmit.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static EditText editText;
    private TextView textView;
    private static TextView realLocation;
    private String url = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=8ecbc96930cfa7461559503ef81e4d3b&lang=ru&units=metric";
    private static ConstraintLayout fon;
    private int REQUEST_CHECK_SETTINGS = 111;
    public LocationCallback locationCallback;
    public static FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editTextTextPersonName);
        textView = findViewById(R.id.textView);
        realLocation = findViewById(R.id.realLocation);
        fon = findViewById(R.id.fon);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//Запрашиваем пользователя о получении местоположения
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        final int REQUEST_CODE = 100;
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // ask permissions here using below code
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Update UI with location data
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
//Получаем название населенного пункта
                        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses.size() > 0) {
                            String cityName = addresses.get(0).getLocality();

                            DownloadTask weather = new DownloadTask();
                            String info = getInfoWeather(weather, cityName);

                            realLocation.setText(info);
                        }
                    }
                    // Update UI with location data
                    // ...
                }
            };
        } ;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    public void knownWeather(View view) {
        //Скрыть клавиатуру
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        //
        DownloadTask weather = new DownloadTask();
        Editable text = editText.getText();
        String cityName = text.toString().trim();
        String info = getInfoWeather(weather, cityName);
        textView.setText(info);
    }

    private String getInfoWeather(DownloadTask weather, String cityName) {
        String url2 = String.format(url, cityName);
        String wetherInfo = "";
        try {
            String finish2 = weather.execute(url2).get();
            if (finish2 == null) {
                return "Нет данных)";
            }
            JSONObject jsonObject = new JSONObject(finish2);
            String city = jsonObject.getString("name");
            String tempStr = jsonObject.getJSONObject("main").getString("temp");
            int resultRoundTemp = (int) Math.round(Double.parseDouble(tempStr));
            String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            wetherInfo = String.format("%s\nТемпература: %s \u2103\nНа улице: %s\n", city, resultRoundTemp, description);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wetherInfo;
    }

    public void onClickAboutCity(View view) throws IOException {

        Editable text = editText.getText();
        String cityName = text.toString().trim();

        if (cityName.isEmpty()) {
            Toast.makeText(this, "Введите название города", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, ActivityAboutCity.class);
            intent.putExtra("cityName", cityName);
            startActivity(intent);
        }
    }

    public void onClickHoroskope(View view) {
        Intent intenthoroskope = new Intent(this,Horoskope.class);
        startActivity(intenthoroskope);
    }

}