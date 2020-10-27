package com.romadmit.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Data.WeathersAppDB;
import Model.Weather;

public class ActivityAboutCity extends AppCompatActivity {

    private static WeathersAppDB weathersAppDB;
    private static TextView textViewCityDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_city);
        textViewCityDescription = findViewById(R.id.textViewCityDescription);
        try {
            getCityName();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getCityName() throws ExecutionException, InterruptedException {
        Intent intent = getIntent();
        String city  = intent.getStringExtra("cityName");
        if (city != "") {
            //Работа с базой данных
            weathersAppDB = Room.databaseBuilder(getApplicationContext(), WeathersAppDB.class, "weatherdb")
                    .allowMainThreadQueries()
                    .build();

            List<Weather> listCityName = weathersAppDB.getWeatherDao().getWeather(city);
            if (listCityName.size() == 0) {
                String url = String.format("https://www.google.com/search?q=%s&ie=UTF-8",city);
                DownloadTask task = new DownloadTask();
                String description = task.execute(url).get();
                if (description.isEmpty()) {
                    description = "Нет данных";
                }
                Weather weather = new Weather(city,description);
                weathersAppDB.getWeatherDao().addWeather(weather);
                textViewCityDescription.setText(description);
            }
            else {
                Weather weatherDesc = listCityName.get(0);
                String cityDescription = weatherDesc.description;
                textViewCityDescription.setText(cityDescription);
            }
            }
        else {
            Intent intentBack = new Intent(this, MainActivity.class);
            startActivity(intentBack);
        }
        }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection httpURLConnection = null;
            StringBuilder result = new StringBuilder();
            String desc = "";
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }

                String str = String.valueOf(result);
                Document doc = Jsoup.parse(str,"UTF-8");

                Pattern pattern = Pattern.compile("<h2 class=\"Uo8X3b\">Описание</h2><span>(.*?)<");
                Matcher matcher = pattern.matcher(doc.toString());

                while (matcher.find()){
                    desc = matcher.group(1);
                }

                return desc;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}