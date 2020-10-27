package com.romadmit.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AboutSignHoroskope extends AppCompatActivity {
    private TextView signHoroskope;
    private String horoskopeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_sign_horoskope);
        signHoroskope = findViewById(R.id.textViewSignHoroskope);

        showHoroskopeSign();
    }

    private void showHoroskopeSign() {
        Intent intent = getIntent();

        String sign = intent.getStringExtra("sign");
        switch (sign){
            case "Овен":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","aries");
                break;
            case "Телец":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","taurus");
                break;
            case "Близнецы":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","gemini");
                break;
            case "Рак":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","cancer");
                break;
            case "Лев":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","leo");
                break;
            case "Дева":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","virgo");
                break;
            case "Весы":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","libra");
                break;
            case "Скорпион":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","scorpio");
                break;
            case "Стрелец":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","sagittarius");
                break;
            case "Козерог":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","capricorn");
                break;
            case "Водолей":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","aquarius");
                break;
            case "Рыбы":
                horoskopeUrl = String.format("https://horo.mail.ru/prediction/%s/today/","pisces");
                break;
        }

                DownloadTask downloadTask = new DownloadTask();
                try {
                    String urlHttpsHoroskope = downloadTask.execute(horoskopeUrl).get();

                    Pattern pattern = Pattern.compile("period=today --></div><div class=\"text text_color_white text_large padding_10\"><p>(.*?)</p></div>");
                    Matcher matcher = pattern.matcher(urlHttpsHoroskope.toString());
                    String textSign = "";
                    while (matcher.find()){
                        textSign = matcher.group(1);
                    }
                    String finishSign = textSign.replace("</p><p>", "\n");
                    signHoroskope.setText(finishSign);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

