package com.romadmit.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Horoskope extends AppCompatActivity {
    private ListView listviewhoroskope;
    private ArrayList<String> list_horoskope;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horoskope);
        listviewhoroskope = findViewById(R.id.listviewhoroskope);
        list_horoskope = new ArrayList<>();

        list_horoskope.add("Овен");
        list_horoskope.add("Телец");
        list_horoskope.add("Близнецы");
        list_horoskope.add("Рак");
        list_horoskope.add("Лев");
        list_horoskope.add("Дева");
        list_horoskope.add("Весы");
        list_horoskope.add("Скорпион");
        list_horoskope.add("Стрелец");
        list_horoskope.add("Козерог");
        list_horoskope.add("Водолей");
        list_horoskope.add("Рыбы");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list_horoskope);
        listviewhoroskope.setAdapter(adapter);

        listviewhoroskope.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSignHoroskope(position);
            }
        });
    }

    public void getSignHoroskope(int position) {
        String sign = list_horoskope.get(position);
        Intent intent = new Intent(this, AboutSignHoroskope.class);
        intent.putExtra("sign", sign);
        startActivity(intent);
    };
}